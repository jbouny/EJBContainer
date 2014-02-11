package ejb.container;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ejb.annotations.PostConstruct;
import ejb.annotations.Singleton;
import ejb.annotations.Stateful;
import ejb.exceptions.InvokeAnnotatedMethodException;
import ejb.exceptions.ManyInterfaceImplementationException;
import ejb.test.EJBTest;
import ejb.test.MockMethodAnnotations;

public class DynamicHelperTest 
{
	@Test
	public void invokeAnnotatedMethodsTest() throws InvokeAnnotatedMethodException, ManyInterfaceImplementationException
	{
		MockMethodAnnotations mockObject = new MockMethodAnnotations();
		DynamicHelper helper = new DynamicHelper();
		assertTrue( mockObject.getValue() == null ) ;
		
		helper.invokeAnnotatedMethods( mockObject, PostConstruct.class );
		assertTrue( "PostConstruct".equals( mockObject.getValue() ) ) ;
	}
	
	@Test
	public void classAnnotatedWithTest()
	{
		DynamicHelper helper = new DynamicHelper();
		assertFalse( helper.classAnnotatedWith(EJBTest.class, Stateful.class) );
		assertTrue( helper.classAnnotatedWith(EJBTest.class, Singleton.class) );
	}
}
