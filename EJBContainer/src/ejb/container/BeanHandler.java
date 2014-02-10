package ejb.container;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

public class BeanHandler implements InvocationHandler 
{
	public BeanHandler(Object inObject)
	{
		m_Bean = inObject;
		m_BeanClass = inObject.getClass();
	}
	
    // EJB bean instance
    private Object m_Bean;
	
    // EJB bean class
    private final Class<?> m_BeanClass;
	
    private Object invokeBeanMethod(final Collection<EntityManager> ems, final Method method, Object[] arguments) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException 
    {
		/*if (transactionField == null) 
		{
		    //BeanTransactions.addTransaction(this, method, ems);
		} else 
		{
		    //setTransactionField(ems);
		}*/
	
		// Calls interceptors for this method or bean instance
		//Object[] intercepteds = callInterceptors(method, arguments);
	
		// Calls for bean method with "intercepted" parameters
		Object value = method.invoke(m_Bean, arguments);
	
		return value;
    }

	@Override
	public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable 
	{
		Object value;

		//Collection<EntityManager> ems = createEntityManagers();
		Method realMethod = null;
		try 
		{
		    String methodName = method.getName();
		    Class<?>[] parameterTypes = method.getParameterTypes();

		    // Gets real method of bean class
		    realMethod = m_BeanClass.getDeclaredMethod(methodName, parameterTypes);
		    value = invokeBeanMethod(null, realMethod, arguments);
		} catch (Throwable th) 
		{
		    //rollback(realMethod);
		    throw new Throwable(th);
		} finally 
		{
		    //close(realMethod);
		}

		return value;
	}

}
