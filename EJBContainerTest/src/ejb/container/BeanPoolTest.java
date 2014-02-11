package ejb.container;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

import ejb.exceptions.EjbAllocationException;
import ejb.exceptions.InvokeAnnotatedMethodException;

public class BeanPoolTest 
{
	@Test
	public void GetReleaseTest() throws EjbAllocationException, InvokeAnnotatedMethodException
	{
		BeanPool pool = BeanPool.getInstance();

		Object object1 = new Object();
		pool.release(object1);
		
		Object object2 = pool.create(object1.getClass());
		assertTrue(object1 == object2);
	}
}
