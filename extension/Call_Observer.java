package gov.nist.sip.proxy.extension;

public class Call_Observer {
	
private static Call_Observer instance = null;
	
	protected Call_Observer()
	{
		// Exists only to defeat instantiation.
	}
	
	
	public static Call_Observer getInstance()
	{
		if (instance == null) {
			instance = new Call_Observer();
		}
		return instance;
	}
	
	public void callStarts()
	{
		//get the timestamp start of call
	}
	
	
	public void callEnds()
	{
		//get the timestamp end of call
	}
	
	public void attach()
	{
		//attach observer to call
	}

	public void notify_all_observers(){
		
	}
}
