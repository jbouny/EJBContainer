package ejb.exceptions;

public class UnknowUnitNameException extends Exception {
	private static final long serialVersionUID = 5398487264467712648L;

	public UnknowUnitNameException( String inUnitName ) {
        super( "No class " + inUnitName + " that implements EntityManager" );
    }
}
