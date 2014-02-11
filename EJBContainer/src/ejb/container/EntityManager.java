package ejb.container;

public interface EntityManager 
{
	void clear();
	
	boolean contains( Object inEntity ); 
	
	void flush();
	
	<T> void merge( T inEntity );
	
	void persist( Object inObject );
	
	void remove( Object inObject );
	
	void close();
	
	void detach();
	
	boolean isOpen();
	
	void joinTransaction();
}
