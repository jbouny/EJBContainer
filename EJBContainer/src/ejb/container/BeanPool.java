package ejb.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import ejb.annotations.PostConstruct;
import ejb.annotations.PreDestroy;
import ejb.exceptions.EjbAllocationException;
import ejb.exceptions.InvokeAnnotatedMethodException;

public class BeanPool
{
	private static BeanPool mInstance = null;
	public static BeanPool getInstance() 
	{
		if(mInstance == null)
			mInstance = new BeanPool();
		return mInstance;
	}
	
	private DynamicHelper mDynamicHelper;
	private ProxyFactory mProxyFactory;
	private Map<Class<?>, Stack<Object>> mBeans;
	
	private BeanPool()
	{
		mDynamicHelper = new DynamicHelper();
		mProxyFactory = new ProxyFactory();
		mBeans = new HashMap<Class<?>, Stack<Object>>();
	}
	
	protected void checkMapKey(Class<?> inBeanClass)
	{
		// Check if the list exists
		if(!mBeans.containsKey(inBeanClass))
			mBeans.put(inBeanClass, new Stack<Object>());
	}
	
	public void release(Object inBean) throws InvokeAnnotatedMethodException
	{
		Class<?> classBean = inBean.getClass();
		checkMapKey(classBean);
		mBeans.get(classBean).push(inBean);

		mDynamicHelper.invokeAnnotatedMethods( inBean, PreDestroy.class );
	}
	
	public Object create(Class<?> inBeanClass) throws EjbAllocationException, InvokeAnnotatedMethodException
	{
		checkMapKey(inBeanClass);
		Object bean = null;
		
		// If there is at least one bean, return it
		if(mBeans.get(inBeanClass).size() > 0)
		{
			bean = mBeans.get(inBeanClass).pop();
		}
		else
		{
			// Else, create a new instance
			try {
				bean = inBeanClass.newInstance();
				bean = mProxyFactory.createWithBean(bean);
			} catch (InstantiationException | IllegalAccessException e) {
				throw new EjbAllocationException(inBeanClass);
			}
		}

		mDynamicHelper.invokeAnnotatedMethods( bean, PostConstruct.class );
		
		return bean;
	}		
}
