package gov.nist.sip.proxy.extension;
	import java.sql.Timestamp;

	public class Premium_Cost_Calculator implements calculator_interface{
		
		public static final double PREMIUM_COST=1.1;
		
		public double calculate_cost(int caller, int callee, Timestamp start, Timestamp end)
		{
				double cost;
				long start_time = start.getTime();
				long end_time = end.getTime();//correct it!
			    cost = PREMIUM_COST*(end_time - start_time)/1000;
				
			    //takes caller calle and timestamps and calculates cost
				return cost;
			}
		    //takes caller calle and timestamps and calculates cost
	}

