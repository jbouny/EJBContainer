package ejb.container;

import java.util.HashMap;
import java.util.Map;

import ejb.exceptions.EjbAllocationException;
import ejb.exceptions.InvokeAnnotatedMethodException;

public class Client 
{
	private Object mId;
	private Map<Class<?>, Object> mBeans;
	
	public Client(Object inInteger)
	{
		mId = inInteger;
		mBeans = new HashMap<Class<?>, Object>();
	}
	
	public Object getId()
	{
		return mId;
	}
	
	public Object getBean(Class<?> inBeanClass) throws EjbAllocationException, InvokeAnnotatedMethodException
	{
		if(mBeans.containsKey(inBeanClass))
			return mBeans.get(inBeanClass);
		
		Object bean = BeanPool.getInstance().create(inBeanClass);
		mBeans.put(inBeanClass, bean);
		return bean;
	}
	
	public void release() throws InvokeAnnotatedMethodException
	{
		for(Object bean : mBeans.values())
		{
			BeanPool.getInstance().release(bean);
		}
	}
}
