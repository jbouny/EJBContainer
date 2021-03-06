package ejb.test;

import ejb.annotations.Singleton;
import ejb.annotations.TransactionAttribute;
import ejb.annotations.TransactionAttribute.Type;

@Singleton
public class EJBTest implements EJBTestInterface 
{
	private int mData;
	
	@TransactionAttribute(value=Type.REQUIRES_NEW)
	public int getData()
	{
		return mData;
	}

	@TransactionAttribute(value=Type.REQUIRED)
	public void setData(int inData)
	{
		mData = inData;
	}
}
