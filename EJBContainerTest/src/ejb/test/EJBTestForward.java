package ejb.test;

import ejb.annotations.EJB;
import ejb.annotations.Singleton;
import ejb.annotations.TransactionAttribute;
import ejb.annotations.TransactionAttribute.Type;

@Singleton
public class EJBTestForward implements EJBTestForwardInterface 
{
	@EJB
	EJBTestInterface ejb;
	
	@TransactionAttribute(value=Type.REQUIRES_NEW)
	public int getData()
	{
		return ejb.getData();
	}

	@TransactionAttribute(value=Type.REQUIRED)
	public void setData(int inData)
	{
		ejb.setData(inData);
	}
}
