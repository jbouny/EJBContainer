package ejb.container;

import java.util.HashMap;
import java.util.Map;

import ejb.exceptions.UnknowUnitNameException;

public class PersistenceFactory 
{
	Map<String,Class<? extends EntityManager>> mPersistenceClasses;
	Map<String,EntityManager> mPersistenceInstances;
	
	public PersistenceFactory()
	{
		mPersistenceClasses = new HashMap<String,Class<? extends EntityManager>>();
		mPersistenceInstances = new HashMap<String,EntityManager>();
	}
	
	public void addPersistenceClass(String inUnitName, Class<? extends EntityManager> inClass)
	{
		mPersistenceClasses.put(inUnitName, inClass);
	}
	
	public EntityManager get(String inUnitName) throws UnknowUnitNameException, InstantiationException, IllegalAccessException
	{
		if(!mPersistenceClasses.containsKey(inUnitName))
			throw new UnknowUnitNameException(inUnitName);

		if(!mPersistenceInstances.containsKey(inUnitName))
		{
			Object entityManager = mPersistenceClasses.get(inUnitName).newInstance();
			mPersistenceInstances.put(inUnitName, (EntityManager) entityManager);
		}
		
		return mPersistenceInstances.get(inUnitName);
	}
}
