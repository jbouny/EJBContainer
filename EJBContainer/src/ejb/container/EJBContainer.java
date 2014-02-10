package ejb.container;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;

import ejb.annotations.EJB;
import ejb.annotations.Local;
import ejb.annotations.PostConstruct;
import ejb.annotations.PreDestroy;
import ejb.annotations.Singleton;
import ejb.annotations.Statefull;
import ejb.annotations.Stateless;
import ejb.exceptions.EjbAllocationException;
import ejb.exceptions.EjbInjectionException;
import ejb.exceptions.InvokeAnnotatedMethodException;
import ejb.exceptions.ManyInterfaceImplementationException;
import ejb.exceptions.UnknowEjbMappingException;

public class EJBContainer {
	
	private static EJBContainer INSTANCE = null;
	
	public static EJBContainer getInstance() throws ManyInterfaceImplementationException
	{
		if( INSTANCE == null )
			INSTANCE = new EJBContainer();
		return INSTANCE;
	}
	
	private Map<Class<?>, Object> mSingletonInstances; // Map Singleton -> instance
	private Map<Class<?>, Class<?>> mEjbImplementations; // Map Interface -> EJB
	
	private EJBContainer() throws ManyInterfaceImplementationException
	{
		mSingletonInstances = new HashMap<Class<?>, Object>();
		mEjbImplementations = new HashMap<Class<?>, Class<?>>();
		
		bootstrapInit() ;
	}
	
	private void MapInterfacesToImplementations( Class<?> inClasse ) throws ManyInterfaceImplementationException
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
		classes.addAll( reflections.getTypesAnnotatedWith(Statefull.class) );
		classes.addAll( reflections.getTypesAnnotatedWith(Stateless.class) );
		for( Class<?> ejbInterface : classes )
		{
			MapInterfacesToImplementations( ejbInterface );
		}
	}
	
	public void inject( Object o ) throws Exception
	{
		// Manage @Stateless, @Statefull, @Singleton and @Local
		//injectClassAnnotations( o );
		
		// Scan all @EJB and inject EJB implementations
		injectFieldAnnotations( o );
	}
	
	public Object createProxyInstance( Class<?> inEJBClass ) throws InstantiationException, IllegalAccessException
	{
		/* Pour chaque classes EJB
		 * On regarde ses interfaces
		 * On associe interface -> class EJB
		 * On injecte non pas l'instance de l'EJB mais un proxy sur celui-ci
		 */
		
		Object bean = inEJBClass.newInstance();
		Class<?>[] interfaces = inEJBClass.getInterfaces();
		BeanHandler handler = new BeanHandler( bean );
		Object proxy = Proxy.newProxyInstance( inEJBClass.getClassLoader(), interfaces, handler );
		
		return proxy;
	}
	
	public Object createEjbInstance( Class<?> inClass ) throws InvokeAnnotatedMethodException, EjbAllocationException
	{
		Object object;
		try {
			object = createProxyInstance( inClass );
		} catch (InstantiationException | IllegalAccessException e) {
			throw new EjbAllocationException( inClass );
		}
		
		invokeAnnotatedMethods( object, PostConstruct.class );
		return object;
	}
	
	public void injectEJB( Object inObject, Field inField ) throws UnknowEjbMappingException, InvokeAnnotatedMethodException, EjbAllocationException, EjbInjectionException
	{
		Class<? extends Object> fieldClass = inField.getType();
		if( mEjbImplementations.containsKey( fieldClass ) )
			fieldClass = mEjbImplementations.get( fieldClass );
		else
			throw new UnknowEjbMappingException( fieldClass );
		
		Annotation[] typeAnnotations = fieldClass.getDeclaredAnnotations();
		
		Object instance = null;
		for( Annotation typeAnnotation : typeAnnotations )
		{
			if( typeAnnotation instanceof Singleton )
			{
				// If the instance of the singleton already exists, get it
				if( mSingletonInstances.containsKey( fieldClass ) )
				{
					instance = mSingletonInstances.get( fieldClass );
				}
				else // Else, instantiate it in the map
				{
					instance = createEjbInstance( fieldClass );
					mSingletonInstances.put( fieldClass, instance );
				}
				break;
			}
		}
		// If the field has not been initialized (with singleton annotation), initialize it
		if( instance == null )
		{
			instance = createEjbInstance( fieldClass );
		}
		
		try {
			inField.set( inObject, instance );
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new EjbInjectionException( inField );
		}
	}
	
	public void invokeAnnotatedMethods( Object inObject, Class<? extends Annotation> inAnnotation ) throws InvokeAnnotatedMethodException
	{
		// Retrieve all methods annotated with the given annotation
		Reflections reflections = new Reflections( inObject.getClass().getName(), new MethodAnnotationsScanner() );
		Set<Method> methods = reflections.getMethodsAnnotatedWith( inAnnotation );
		
		// Invoke them
		for( Method method : methods )
		{
			try {
				method.invoke( inObject, new Object[]{} );
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new InvokeAnnotatedMethodException( method );
			}
		}
	}
	
	public void injectFieldAnnotations( Object o ) throws UnknowEjbMappingException, InvokeAnnotatedMethodException, EjbAllocationException, EjbInjectionException
	{
		Class<? extends Object> objectClass = o.getClass();
		
        Reflections reflections = new Reflections( objectClass.getName(), new FieldAnnotationsScanner()) ;
		Set<Field> fields = reflections.getFieldsAnnotatedWith( EJB.class );

		for( Field field : fields )
		{
			injectEJB( o, field );
		}
	}
	
	public void deleteEJB( Object inObject ) throws InvokeAnnotatedMethodException
	{
		invokeAnnotatedMethods( inObject, PreDestroy.class );
	}
	
	public void injectClassAnnotations( Class<? extends Object> type )
	{
		Annotation[] annotations = type.getDeclaredAnnotations();
		
		for( Annotation annotation : annotations )
		{
			if( annotation instanceof Stateless )
			{
				
			}
			else if( annotation instanceof Local )
			{
				
			}
		}
	}
	
	public void injectClassSingleton( Class<? extends Object> type )
	{
		
	}
}
