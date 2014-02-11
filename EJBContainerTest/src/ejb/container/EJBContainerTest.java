package ejb.container;

import java.lang.reflect.Proxy;

import org.junit.Test;

import ejb.annotations.EJB;
import ejb.annotations.PersistenceContext;
import ejb.annotations.Singleton;
import ejb.exceptions.EjbAllocationException;
import ejb.exceptions.EjbInjectionException;
import ejb.exceptions.InvokeAnnotatedMethodException;
import ejb.exceptions.ManyInterfaceImplementationException;
import ejb.exceptions.UnknowEjbMappingException;
import ejb.exceptions.UnknowUnitNameException;
import ejb.test.EJBTestForwardInterface;
import ejb.test.EJBTestInterface;
import ejb.test.MockEntityManager;
import ejb.test.MockMethodAnnotations;
import ejb.test.MockPojoWithEM;
import static org.junit.Assert.*;

@Singleton
public class EJBContainerTest 
{
	@EJB
	EJBTestInterface ejbTest;
	
	@EJB
	EJBTestInterface ejbTest2;
	
	@EJB
	EJBTestForwardInterface ejbTestForward;
	
	@PersistenceContext(unitName="MockEntityManager")
	EntityManager manager;
	
	@Test
	public void deleteEJBTest() throws InvokeAnnotatedMethodException, ManyInterfaceImplementationException
	{
		MockMethodAnnotations mockObject = new MockMethodAnnotations();
		EJBContainer.getInstance().deleteEJB( mockObject );
		assertTrue( "PreDestroy".equals( mockObject.getValue() ) );
	}
	
	@Test
	public void injectFieldAnnotationsTest() throws UnknowEjbMappingException, InvokeAnnotatedMethodException, EjbAllocationException, EjbInjectionException, ManyInterfaceImplementationException, InstantiationException, IllegalAccessException, UnknowUnitNameException 
	{
		EJBContainer.getInstance().injectFieldAnnotations( this );

		assertTrue( ejbTest instanceof Proxy );
		assertTrue( ejbTest2 instanceof Proxy );
		assertTrue( ejbTest == ejbTest2 );
		assertTrue( Proxy.getInvocationHandler(ejbTest) instanceof BeanHandler );
	}
	
	@Test
	public void injectFieldAnnotationsEMTest() throws UnknowEjbMappingException, InvokeAnnotatedMethodException, EjbAllocationException, EjbInjectionException, ManyInterfaceImplementationException, InstantiationException, IllegalAccessException, UnknowUnitNameException
	{
		MockPojoWithEM pojo = new MockPojoWithEM();

		assertTrue(pojo.mEntityManager == null);
		EJBContainer.getInstance().injectFieldAnnotations( pojo );
		assertTrue(pojo.mEntityManager != null);
		assertTrue(pojo.mEntityManager instanceof MockEntityManager);
	}
	
	@Test
	public void transactionTest() throws InstantiationException, IllegalAccessException, UnknowEjbMappingException, InvokeAnnotatedMethodException, EjbAllocationException, EjbInjectionException, UnknowUnitNameException, ManyInterfaceImplementationException
	{
		EJBContainer.getInstance().injectFieldAnnotations( this );
		ejbTest.setData(42);
		System.out.println();
		
		assertTrue( ejbTest.getData() == 42 );
		System.out.println();
	}

	@Test
	public void transactionForwardTest() throws InstantiationException, IllegalAccessException, UnknowEjbMappingException, InvokeAnnotatedMethodException, EjbAllocationException, EjbInjectionException, UnknowUnitNameException, ManyInterfaceImplementationException
	{
		EJBContainer.getInstance().injectFieldAnnotations( this );
		EJBContainer.getInstance().injectFieldAnnotations( ejbTestForward );
		
		ejbTestForward.setData(42);
		System.out.println();
		
		assertTrue( ejbTestForward.getData() == 42 );
		System.out.println();
	}
	
	/*@Test
	public void injectClassAnnotationsTest() 
	{
		//EJBContainer.getInstance().injectClassAnnotations( this );
		
		try
		{
			Object instance = this.getClass().getDeclaredMethod("getInstance").invoke(null, new Object[]{});
			assertTrue( instance instanceof EJBTest );
		}
		catch( Exception e )
		{
			assertTrue(false);
		}
	}*/
}
