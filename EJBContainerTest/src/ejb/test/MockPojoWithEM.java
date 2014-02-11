package ejb.test;

import ejb.annotations.PersistenceContext;
import ejb.container.EntityManager;

public class MockPojoWithEM {
	
	@PersistenceContext(unitName="MockEntityManager")
	public EntityManager mEntityManager = null;
	
}
