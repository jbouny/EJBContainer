package ejb.container;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.junit.Test;

import ejb.logger.Log;
import ejb.test.MockInterface;
import ejb.test.MockPojo;

public class ProxyFactoryTest 
{
	private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	
	private Object createProxy( Object inObject ) 
	{
		ProxyFactory factory = new ProxyFactory();
		return factory.createWithBean(inObject);
	}
	
	@Test
	public void createWithBeanTest() 
	{
		MockInterface object = (MockInterface) createProxy(new MockPojo());
		
		assertTrue(object != null);
		assertTrue(object instanceof Proxy);
		assertTrue(object instanceof MockInterface);
		
		// Check the instance of the bean inside the handler
		InvocationHandler handler = Proxy.getInvocationHandler(object);
		assertTrue(handler instanceof BeanHandler);
		BeanHandler beanHandler = (BeanHandler)handler;
		assertTrue(beanHandler.getBean() instanceof MockPojo);
		
	}

	@Test
	public void invokeMethodTest() 
	{
		PrintStream ps = new PrintStream(outputStream);
		Log.setDefaultWriter(ps);
		MockInterface object = (MockInterface) createProxy(new MockPojo());
		
		// Invoke method
		object.sayHelloWorld();
		
		String output = outputStream.toString();
		assertTrue(output.contains("Invoke method [begin] sayHelloWorld"));
		assertTrue(output.contains("Invoke method [end] sayHelloWorld"));
		Log.setDefaultWriter(System.out);
	}
	
	@Test
	public void invokeMethodReturnTest() 
	{
		PrintStream ps = new PrintStream(outputStream);
		Log.setDefaultWriter(ps);
		MockInterface object = (MockInterface) createProxy(new MockPojo());
		// Invoke method
		String output = object.sayHelloWorld();
		assertTrue("Hello World".equals(output));
		Log.setDefaultWriter(System.out);
	}
	
	@Test
	public void invokeMethodParamsTest() 
	{
		MockInterface object = (MockInterface) createProxy(new MockPojo());
		
		String data = "Data";
		assertFalse(data.equals(object.getData()));
		// Invoke method
		object.setData(data);
		assertTrue(data.equals(object.getData()));
	}
	
	@Test
	public void transactionsTest()
	{
		
	}
}
