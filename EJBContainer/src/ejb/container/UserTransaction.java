package ejb.container;

import java.util.Stack;

public class UserTransaction {
	private Stack<Transaction> mTransactions;
	
	public UserTransaction()
	{
		mTransactions = new Stack<>();
	}
	
	public void push(Transaction inTransaction)
	{
		mTransactions.push(inTransaction);
	}
	
	public Transaction pop()
	{
		return mTransactions.pop();
	}
	
	public Transaction first()
	{
		if(mTransactions.empty())
			return null;
		return mTransactions.firstElement();
	}
	
	public boolean hasOne()
	{
		return !mTransactions.empty();
	}
	
	public int size()
	{
		return mTransactions.size();
	}
	
	public void sleep()
	{
		if(!mTransactions.empty())
		{
			mTransactions.firstElement().sleep();
		}
	}
	
	public void awake()
	{
		if(!mTransactions.empty())
		{
			mTransactions.firstElement().awake();
		}
	}
}
