package ejb.test;

public class MockPojo implements MockInterface {
	String mData;
	
	public String sayHelloWorld() {
		return "Hello World";
	}
	
	public void setData(String inData) {
		mData = inData;
	}
	
	public String getData() {
		return mData;
	}
}
