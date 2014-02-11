package ejb.container;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;

import ejb.annotations.EJB;
import ejb.annotations.PersistenceContext;
import ejb.annotations.PreDestroy;
import ejb.annotations.Singleton;
import ejb.annotations.Stateful;
import ejb.annotations.Stateless;
import ejb.exceptions.EjbAllocationException;
import ejb.exceptions.EjbInjectionException;
import ejb.exceptions.InvokeAnnotatedMethodException;
import ejb.exceptions.ManyInterfaceImplementationException;
import ejb.exceptions.UnknowEjbMappingException;
import ejb.exceptions.UnknowUnitNameException;

public class EJBContainer 
{
	private static EJBContainer INSTANCE = null;
	
	public static EJBContainer getInstance() throws ManyInterfaceImplementationException
	{
		if( INSTANCE == null )
			INSTANCE = new EJBContainer();
		return INSTANCE;
	}
	
	private Map<Class<?>, Object> mSingletonInstances; // Map Singleton -> instance
	private Map<Class<?>, Class<?>> mEjbImplementations; // Map Interface -> EJB
	private DynamicHelper mDynamicHelper;
	private ClientHolder mClientHolder;
	private PersistenceFactory mPersistenceFactory;
	
	private EJBContainer() throws ManyInterfaceImplementationException
	{
		mSingletonInstances = new HashMap<Class<?>, Object>();
		mEjbImplementations = new HashMap<Class<?>, Class<?>>();
		mDynamicHelper = new DynamicHelper();
		mClientHolder = new ClientHolder();
		mPersistenceFactory = new PersistenceFactory();
		
		bootstrapInit() ;
	}
	
	private void mapInterfacesToImplementations( Class<?> inClasse ) throws ManyInterfaceImplementationException
	{
		Class<?> interfaces[] = inClasse.getInterfaces();
		for( Class<?> ejbInterface : interfaces )
		{
			if( mEjbImplementations.containsKey( ejbInterface ) )
				throw new ManyInterfaceImplementationException( ejbInterface );
			mEjbImplementations.put( ejbInterface, inClasse );
		}
	}
	
	private void bootstrapInit() throws ManyInterfaceImplementationException
	{
		Reflections reflections = new Reflections();
		
		// Scan all classes of the class loader with annotations (Singleton, Statefull, Stateless)
		Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Singleton.class);
		classes.addAll( reflections.getTypesAnnotatedWith(Stateful.class) );
		classes.addAll( reflections.getTypesAnnotatedWith(Stateless.class) );
		for( Class<?> ejbInterface : classes )
		{
			mapInterfacesToImplementations( ejbInterface );
		}
		
		// Scan all classes that implements EntityManager
		Set<Class<? extends EntityManager>> entityManagers = reflections.getSubTypesOf(EntityManager.class);
		for( Class<? extends EntityManager> managerClass : entityManagers )
		{
			mPersistenceFactory.addPersistenceClass(managerClass.getSimpleName(), managerClass);
			//Log.info("EJBContainer", "Find EntityManager: " + managerClass.getSimpleName());
		}
		
	}
	
	public void inject( Object inObject ) throws Exception
	{		
		// Scan all @EJB and inject EJB implementations
		injectFieldAnnotations( inObject );
	}
	
	public Object createProxyInstance( Class<?> inEJBClass, Object inClient ) throws InstantiationException, IllegalAccessException, EjbAllocationException, InvokeAnnotatedMethodException
	{
		Object bean = null;
		// Manage stateful behavior
		if(mDynamicHelper.classAnnotatedWith(inEJBClass, Stateful.class))
		{
			bean = mClientHolder.getClient(inClient).getBean(inEJBClass);
		}
		// Manage stateless behavior
		else if(mDynamicHelper.classAnnotatedWith(inEJBClass, Stateless.class))
		{
			bean = BeanPool.getInstance().create(inEJBClass);
		}
		// Manage singleton behavior
		else if(mDynamicHelper.classAnnotatedWith(inEJBClass, Singleton.class))
		{
			// If the instance of the singleton already exists, get it
			if( mSingletonInstances.containsKey( inEJBClass ) )
			{
				bean = mSingletonInstances.get( inEJBClass );
			}
			else // Else, instantiate it in the map
			{
				bean = BeanPool.getInstance().create(inEJBClass);
				mSingletonInstances.put( inEJBClass, bean );
			}
		}
		return bean;
	}
	
	public Object createEjbInstance( Class<?> inClass, Object inClient ) throws InvokeAnnotatedMethodException, EjbAllocationException
	{
		Object object;
		try {
			object = createProxyInstance( inClass, inClient );
		} catch (InstantiationException | IllegalAccessException e) {
			throw new EjbAllocationException( inClass );
		}
		
		return object;
	}
	
	public void injectEJB( Object inObject, Field inField ) throws UnknowEjbMappingException, InvokeAnnotatedMethodException, EjbAllocationException, EjbInjectionException
	{
		// Search the implementation of the interface
		Class<?> fieldClass = inField.getType();
		if( mEjbImplementations.containsKey( fieldClass ) )
			fieldClass = mEjbImplementations.get( fieldClass );
		else
			throw new UnknowEjbMappingException( fieldClass );
		
		Object instance = createEjbInstance( fieldClass, inObject );
		
		// Inject the instance
		try {
			inField.setAccessible(true);
			inField.set( inObject, instance );
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new EjbInjectionException( inField );
		}
	}
	
	public void injectPersistenceContext( Object inObject, Field inField ) throws InstantiationException, IllegalAccessException, UnknowUnitNameException, EjbInjectionException
	{
		PersistenceContext annotation = inField.getAnnotation(PersistenceContext.class);
		EntityManager manager = mPersistenceFactory.get(annotation.unitName());

		try {
			inField.set( inObject, manager );
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new EjbInjectionException( inField );
		}
		
	}
	
	public void injectFieldAnnotations(Object inObject) throws UnknowEjbMappingException, InvokeAnnotatedMethodException, EjbAllocationException, EjbInjectionException, InstantiationException, IllegalAccessException, UnknowUnitNameException
	{
		if(inObject instanceof Proxy)
		{
			InvocationHandler handler = Proxy.getInvocationHandler(inObject);
			if( handler instanceof BeanHandler )
			{
				BeanHandler beanHandler = (BeanHandler)handler;
				inObject = beanHandler.getBean();
			}
		}
		
		Class<? extends Object> objectClass = inObject.getClass();
		
		// Search all fields annotated with @EJB
        Reflections reflections = new Reflections( objectClass.getName(), new FieldAnnotationsScanner()) ;
		Set<Field> fields = reflections.getFieldsAnnotatedWith( EJB.class );

		// Inject them all
		for( Field field : fields )
		{
			injectEJB( inObject, field );
		}
		
		// Search all fields annotated with @PersistenceContext
		fields = reflections.getFieldsAnnotatedWith( PersistenceContext.class );

		// Inject them all
		for( Field field : fields )
		{
			injectPersistenceContext( inObject, field );
		}
	}
	
	public void deleteEJB( Object inObject ) throws InvokeAnnotatedMethodException
	{
		mDynamicHelper.invokeAnnotatedMethods( inObject, PreDestroy.class );
	}
}
