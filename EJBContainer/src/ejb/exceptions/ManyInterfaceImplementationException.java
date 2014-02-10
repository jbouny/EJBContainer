package ejb.exceptions;

public class ManyInterfaceImplementationException extends Exception {
	
	private static final long serialVersionUID = 7066075608635102983L;

    public ManyInterfaceImplementationException( Class<?> inInterface ) {
        super( "The interface " + inInterface.getName() + " has more than one implementation" );
    }
}
