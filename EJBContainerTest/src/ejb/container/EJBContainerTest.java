package ejb.container;

import org.junit.Test;

import ejb.annotations.EJB;
import ejb.annotations.PostConstruct;
import ejb.annotations.Singleton;
import ejb.exceptions.EjbAllocationException;
import ejb.exceptions.EjbInjectionException;
import ejb.exceptions.InvokeAnnotatedMethodException;
import ejb.exceptions.ManyInterfaceImplementationException;
import ejb.exceptions.UnknowEjbMappingException;
import ejb.test.EJBTest;
import ejb.test.EJBTestInterface;
import ejb.test.MockMethodAnnotations;
import static org.junit.Assert.*;

@Singleton
public class EJBContainerTest {

	@EJB
	EJBTestInterface ejbTest;
	
	@EJB
	EJBTestInterface ejbTest2;

	@Test
	public void invokeAnnotatedMethodsTest() throws InvokeAnnotatedMethodException, ManyInterfaceImplementationException
	{
		MockMethodAnnotations mockObject = new MockMethodAnnotations();
		assertTrue( mockObject.getValue() == null ) ;
		
		EJBContainer.getInstance().invokeAnnotatedMethods( mockObject, PostConstruct.class );
		assertTrue( "PostConstruct".equals( mockObject.getValue() ) ) ;
	}
	
	@Test
	public void deleteEJBTest() throws InvokeAnnotatedMethodException, ManyInterfaceImplementationException
	{
		MockMethodAnnotations mockObject = new MockMethodAnnotations();
		EJBContainer.getInstance().deleteEJB( mockObject );
		assertTrue( "PreDestroy".equals( mockObject.getValue() ) ) ;
	}
	
	@Test
	public void injectFieldAnnotationsTest() throws UnknowEjbMappingException, InvokeAnnotatedMethodException, EjbAllocationException, EjbInjectionException, ManyInterfaceImplementationException {
		EJBContainer.getInstance().injectFieldAnnotations( this );

		assertTrue( ejbTest instanceof EJBTest ) ;
		assertTrue( ejbTest2 instanceof EJBTest ) ;
		assertTrue( ejbTest == ejbTest2 ) ;
	}
	
	/*@Test
	public void injectClassAnnotationsTest() {
		//EJBContainer.getInstance().injectClassAnnotations( this );
		
		try
		{
			Object instance = this.getClass().getDeclaredMethod("getInstance").invoke(null, new Object[]{});
			assertTrue( instance instanceof EJBTest ) ;
		}
		catch( Exception e )
		{
			assertTrue(false);
		}
	}*/
}
