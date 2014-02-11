package ejb.container;

import java.util.HashMap;
import java.util.Map;

import ejb.exceptions.InvokeAnnotatedMethodException;

public class ClientHolder {
	private Map<Object, Client> mClientBeans;
	
	public ClientHolder()
	{
		mClientBeans = new HashMap<Object, Client>();
	}
	
	public boolean checkClient(Object inClient)
	{
		if(mClientBeans.containsKey(inClient))
			return true;
		mClientBeans.put(inClient, new Client(inClient));
		return false;
	}
	
	public Client getClient(Object inClient)
	{
		checkClient(inClient);
		return mClientBeans.get(inClient);
	}
	
	public void releaseClient(Object inClient)
	{
		if(mClientBeans.containsKey(inClient))
		{
			mClientBeans.remove(inClient);
		}
	}
	
	public void destroyClient(Client inClient) throws InvokeAnnotatedMethodException
	{
		if(mClientBeans.containsKey(inClient.getId()))
		{
			mClientBeans.remove(inClient);
			inClient.release();
		}
	}
}
