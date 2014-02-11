package ejb.container;

import ejb.logger.Log;

public class Transaction {
	private String mId;
	private String mTag;

	public Transaction(Object inBean) {
		mId = inBean.getClass().getSimpleName();
		mTag = "Transaction " + mId + "\t";
	}

	public void begin() {
		Log.info(mTag, "Create");
	}

	public void sleep() {  
		Log.info(mTag, "Sleep");
	}

	public void awake() {
		Log.info(mTag, "Awake");
	}

	public void end() {
		Log.info(mTag, "End");
	}
}