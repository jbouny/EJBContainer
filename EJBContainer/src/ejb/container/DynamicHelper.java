package ejb.container;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;

import ejb.exceptions.InvokeAnnotatedMethodException;

public class DynamicHelper 
{
	public void invokeAnnotatedMethods( Object inObject, Class<? extends Annotation> inAnnotation ) throws InvokeAnnotatedMethodException
	{
		// Retrieve all methods annotated with the given annotation
		Reflections reflections = new Reflections( inObject.getClass().getName(), new MethodAnnotationsScanner() );
		Set<Method> methods = reflections.getMethodsAnnotatedWith( inAnnotation );
		
		// Invoke them
		for( Method method : methods )
		{
			try {
				method.invoke( inObject, new Object[]{} );
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new InvokeAnnotatedMethodException( method );
			}
		}
	}
	
	public boolean classAnnotatedWith(Class<?> inClass, Class<?> inAnnotation)
	{
		Annotation[] typeAnnotations = inClass.getDeclaredAnnotations();
		for( Annotation typeAnnotation : typeAnnotations )
		{
			if( inAnnotation.isInstance(typeAnnotation) )
				return true;
		}
		return false;
	}
}
