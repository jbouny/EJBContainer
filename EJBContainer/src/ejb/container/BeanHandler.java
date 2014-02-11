package ejb.container;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ejb.annotations.TransactionAttribute;
import ejb.logger.Log;

public class BeanHandler implements InvocationHandler 
{
    // EJB bean instance
    private Object mBean;
	
    // EJB bean class
    private Class<?> mBeanClass;
    
    private static UserTransaction mUserTransaction = new UserTransaction();
    
    Transaction mTransaction = null;
    
    TransactionAttribute.Type mClassTransaction;
    
	public BeanHandler(Object inBean)
	{
		setBean(inBean);
	}
	
	private void beginInvoke(TransactionAttribute.Type inTransactionType)
	{
		//Log.info("BeanHandler", "Pre invoke with " + inTransactionType.toString() + " Transaction");
		switch(inTransactionType)
		{
		case NEVER:
			break;
			
		case REQUIRED:
			if(!mUserTransaction.hasOne())
			{
				mTransaction = new Transaction(mBean);
				mTransaction.begin();
				mUserTransaction.push(mTransaction);
			}
			break;
			
		case REQUIRES_NEW:
			mUserTransaction.sleep();
			mTransaction = new Transaction(mBean);
			mTransaction.begin();
			mUserTransaction.push(mTransaction);
			break;
		}
	}
	
	private void endInvoke(TransactionAttribute.Type inTransactionType)
	{
		//Log.info("BeanHandler", "Post invoke with " + inTransactionType.toString() + " Transaction");
		switch(inTransactionType)
		{
		case NEVER:
			break;
			
		case REQUIRED:
			if(mTransaction != null && mUserTransaction.first() == mTransaction)
				mUserTransaction.pop().end();
			break;
			
		case REQUIRES_NEW:
			mUserTransaction.pop().end();
			if(mUserTransaction.hasOne())
				mUserTransaction.awake();
			break;
		}
	}
	
	private TransactionAttribute.Type getTransactionAttribute(Method inMethod) throws NoSuchMethodException, SecurityException
	{
		// Retrieve the original method
		Class<?>[] params = inMethod.getParameterTypes();
		Method beanMethod = mBeanClass.getMethod(inMethod.getName(), (params.length > 0 ? params : null));
		TransactionAttribute methodAttribute = beanMethod.getAnnotation(TransactionAttribute.class);
    	
		// Extract the current transaction attribute
		if( methodAttribute == null )
    		return mClassTransaction;
    	else
    		return methodAttribute.value();
	}
	
    private Object invokeBeanMethod(Method inMethod, Object[] inArguments) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException 
    {
    	Log.info("BeanHandler", "Invoke method [begin] " + inMethod.getName());
		Object value = inMethod.invoke(mBean, inArguments);
    	Log.info("BeanHandler", "Invoke method [end] " + inMethod.getName());
	
		return value;
    }

	@Override
	public Object invoke(Object inPoxy, Method inMethod, Object[] inArguments) throws Throwable 
	{
		Object value;

		Method realMethod = null;
		
	    // Gets real method of bean class
	    String methodName = inMethod.getName();
	    Class<?>[] parameterTypes = inMethod.getParameterTypes();
	    realMethod = mBeanClass.getDeclaredMethod(methodName, parameterTypes);

	    // Manage begin of transaction
	    TransactionAttribute.Type transactionType = getTransactionAttribute(inMethod);
    	beginInvoke(transactionType);
    	
	    try
	    {
		    // Invoke it
		    value = invokeBeanMethod(realMethod, inArguments);
	    } 
	    catch (Exception e) 
		{
		    throw e;
		}
	    finally 
		{
		    // Manage end of transaction
	    	endInvoke(transactionType);
		}
	    
		return value;
	}
	
	public void setBean(Object inBean) {
		mBean = inBean;
		mBeanClass = inBean.getClass();
		
		TransactionAttribute classTransaction = mBeanClass.getAnnotation(TransactionAttribute.class);
		if(classTransaction == null)
			mClassTransaction = TransactionAttribute.Type.NEVER;
		else
			mClassTransaction = classTransaction.value();
	}
	
	public Object getBean() {
		return mBean;
	}
}
