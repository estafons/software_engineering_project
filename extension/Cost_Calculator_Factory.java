package gov.nist.sip.proxy.extension;

import java.util.Calendar;

public class Cost_Calculator_Factory extends Billing_server{

	
	
private static Cost_Calculator_Factory instance = null;
	
	protected Cost_Calculator_Factory()
	{
		// Exists only to defeat instantiation.
	}
	
	
	public static Cost_Calculator_Factory getInstance()
	{
		if (instance == null) {
			instance = new Cost_Calculator_Factory();
		}
		return instance;
	}
	
	
	
	public calculator_interface invoke_calculator(int policyid){
Calendar c = Calendar.getInstance();
	        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
	        if (dayOfWeek == (Calendar.SATURDAY)||(dayOfWeek ==(Calendar.SUNDAY))) {
	        	return new Weekends_Cost_Calculator();
	        }
	        else if (policyid==2) return new Premium_Cost_Calculator();
			else return new Normal_Cost_Calculator();
		//creates the object that will calculate the cost based on the policy we are on
	}
}
