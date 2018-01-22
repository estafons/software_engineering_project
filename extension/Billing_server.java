package gov.nist.sip.proxy.extension;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JOptionPane;
import gov.nist.sip.proxy.extension.DB_server.Query;

public class Billing_server extends DB_server{

	private static Billing_server instance = null;
	
	protected Billing_server()
	{
		// Exists only to defeat instantiation.
	}
	
	
	public static Billing_server getInstance()
	{
		if (instance == null) {
			instance = new Billing_server();
		}
		return instance;
	}
	
	
	public void updateBill(int call_id)
	{
		int caller =0;
		int policy = 0;
		int callee = 0;
		float debt =  0;
		Timestamp start = null;
		Timestamp end = null;
		try {
			DB_server dbServer = DB_server.getInstance();
			List<Object> params = null;

			params = Arrays.asList((Object) call_id);
			dbServer.execute_query(Query.GET_CALLER ,params);
			ResultSet rst = dbServer.getRS();
			if(rst.first()){
				caller = rst.getInt("caller_id");
			}
			dbServer.execute_query(Query.GET_CALLEE ,params);
			 rst = dbServer.getRS();
			if(rst.first()){
				callee = rst.getInt("callee_id");
			}
			dbServer.execute_query(Query.GET_CALL_START ,params);
			rst = dbServer.getRS();
			if(rst.first()){
				start = rst.getTimestamp("call_start");
			}
			dbServer.execute_query(Query.GET_CALL_END ,params);
			rst = dbServer.getRS();
			if(rst.first()){
				end = rst.getTimestamp("call_end");
			}
			params = Arrays.asList((Object) caller);
			dbServer.execute_query(Query.GET_POLICY_ID ,params);
			rst = dbServer.getRS();
			if(rst.first()){
				policy = rst.getInt("billing_policy");
			}
			params = Arrays.asList((Object) caller);
			dbServer.execute_query(Query.GET_DEBT ,params);
			rst = dbServer.getRS();
			if(rst.first()){
				debt = rst.getFloat("debt");
			}
			
			Cost_Calculator_Factory costCalculatorFactory  = new Cost_Calculator_Factory();
			calculator_interface costCalculator = costCalculatorFactory.invoke_calculator(policy);//check check
			float cost = debt + (float) costCalculator.calculate_cost(caller, callee, start, end);
			params = Arrays.asList((Object) cost ,(Object) caller);
			dbServer.execute_query(Query.UPDATE_DEBT ,params);
			
		}
		catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Failed to update bill query", "Error", JOptionPane.ERROR_MESSAGE);
		}
		//updates the bill of the caller
	}


	public boolean changeBilling(int user_id,int policy_id)
	{
		try{
			DB_server dbServer = DB_server.getInstance();
			List<Object> params = Arrays.asList( (Object) policy_id,(Object) user_id);
			dbServer.execute_query(Query.INSERT_POLICY,params);
			return true;
		}catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Failed to insert policy query", "Error", JOptionPane.ERROR_MESSAGE);
		}
		return false;
		//changes the policy of billing for the user
	}


	public double getSubtotal(int start_time,int end_time)
	{
	   return 0.0;
		//returns the cost of the call
	}
	
	public static void main(String[] args)
	{ 
		/*
		 Billing_server Bill = Billing_server.getInstance();
		//int call_id=1;
		int caller_id=3;
		int callee_id=4;
		//Bill.updateBill(call_id);
		
		 if (Bill.changeBilling(caller_id,5)){
		}
		Timestamp start_time = new Timestamp(System.currentTimeMillis());
		int sipcallid=0;
		int x = Bill.registercallstart( caller_id,  callee_id, start_time);
		
		System.out.println("Call id = "+x);
		Timestamp end_time = new Timestamp(System.currentTimeMillis());
		Bill.registercallend(1, end_time); */
		//debbuging
	//	something wrong with registercallend overwrites call start most likely mysql problem*/
		
	}  
	public int getCallId(String sipcallid){
		int call_id = 0;
		DB_server dbServer = DB_server.getInstance();
		List<Object> params = null;
		params = Arrays.asList((Object) sipcallid);
		try {
			dbServer.execute_query(Query.RETURN_CALL_ID ,params);
			if(!dbServer.is_empty()){
				call_id = (int)dbServer.getFirst("call_id");
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Failed to get CallId from SipCallId", "Error", JOptionPane.ERROR_MESSAGE);
		}
		return call_id;
	}
	
	
	public int registercallstart(int caller_id, int callee_id, Timestamp start_time,String sipcallid){
		int call_id=1001;
		try {
			DB_server dbServer = DB_server.getInstance();
			List<Object> params = null;
			 ResultSet rst = dbServer.getRS();
			//long start = start_time.getTime();
			params = Arrays.asList((Object) callee_id, (Object) caller_id, (Object) start_time,(Object) sipcallid);
			dbServer.execute_query(Query.INSERT_CALL ,params);
			params = Arrays.asList((Object) sipcallid);
			dbServer.execute_query(Query.RETURN_CALL_ID ,params);
			rst = dbServer.getRS();
			if(rst.first()){
				call_id = rst.getInt("call_id");
			}
			return call_id;
		}
		catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Failed to insert call query", "Error", JOptionPane.ERROR_MESSAGE);
		}
		return 1010;
	}
	public void registercallend(int call_id, Timestamp end_time){
		try {
			DB_server dbServer = DB_server.getInstance();
			List<Object> params = null;
			//long end = end_time.getTime();
			params = Arrays.asList((Object) end_time,
					(Object) call_id);
			dbServer.execute_query(Query.UPDATE_CALL_TIME ,params);
		}
		catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Failed to insert call query", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	

}