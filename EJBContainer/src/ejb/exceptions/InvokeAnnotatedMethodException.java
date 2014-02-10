package ejb.exceptions;

import java.lang.reflect.Method;

public class InvokeAnnotatedMethodException extends Exception {
	
	private static final long serialVersionUID = 7066075608635102983L;

    public InvokeAnnotatedMethodException( Method inMethod ) {
        super( "The method " + inMethod + " can't be invoked" );
    }
}
