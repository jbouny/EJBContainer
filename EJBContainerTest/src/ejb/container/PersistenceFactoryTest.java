package ejb.container;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import ejb.exceptions.UnknowUnitNameException;
import ejb.test.MockEntityManager;

public class PersistenceFactoryTest 
{
	@Test
	public void addPersistenceClassTest() throws InstantiationException, IllegalAccessException, UnknowUnitNameException
	{
		PersistenceFactory factory = new PersistenceFactory();
		factory.addPersistenceClass("MockEntityManager", MockEntityManager.class);
		
		Object object1 = factory.get("MockEntityManager");
		assertTrue(object1 instanceof MockEntityManager);
		
		Object object2 = factory.get("MockEntityManager");
		assertTrue(object1 == object2);
	}
}
