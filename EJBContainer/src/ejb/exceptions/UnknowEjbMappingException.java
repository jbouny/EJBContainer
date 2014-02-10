package ejb.exceptions;

public class UnknowEjbMappingException extends Exception {
	
	private static final long serialVersionUID = 7066075608635102983L;

    public UnknowEjbMappingException( Class<?> inInterface ) {
        super( "No ejb implements the interface " + inInterface.getName() );
    }
}
