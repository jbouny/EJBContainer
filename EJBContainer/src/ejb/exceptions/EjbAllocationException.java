package ejb.exceptions;

public class EjbAllocationException extends Exception {
	
	private static final long serialVersionUID = 7066075608635102983L;

    public EjbAllocationException( Class<?> inClass ) {
        super( "Can't create a new instance of " + inClass.getName() );
    }
}
