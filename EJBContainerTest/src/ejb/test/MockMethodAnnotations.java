package ejb.test;

public class MockMethodAnnotations {
	String mValue = null;
	
	public String getValue()
	{
		return mValue;
	}
	
	@ejb.annotations.PostConstruct
	public void PostConstruct()
	{
		mValue = "PostConstruct";
	}

	@ejb.annotations.PreDestroy
	public void PreDestroy()
	{
		mValue = "PreDestroy";
	}
}
