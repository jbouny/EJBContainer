package ejb.exceptions;

import java.lang.reflect.Field;

public class EjbInjectionException extends Exception {
	
	private static final long serialVersionUID = 7066075608635102983L;

    public EjbInjectionException( Field inField ) {
        super( "Can't inject the EJB instance on the field " + inField );
    }
}
