package gov.nist.sip.proxy.extension;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;

public class Forwarding_Server extends DB_server{

private static Forwarding_Server instance = null;
	
	protected Forwarding_Server()
	{
		// Exists only to defeat instantiation.
	}
	
	public static Forwarding_Server getInstance()
	{
		if (instance == null) {
			instance = new Forwarding_Server();
		}
		return instance;
	}
	
	
	public boolean is_forwarding(int callee_id)
	{
		try{
			DB_server dbServer = DB_server.getInstance();
			List<Object> params = Arrays.asList( (Object) callee_id);
			dbServer.execute_query(Query.GET_FORWARDING,params);
			return !dbServer.is_empty();
		}catch(SQLException e){
			JOptionPane.showMessageDialog(null, "Failed check if callee_id is forwarding", 
					"Error", JOptionPane.ERROR_MESSAGE);
		}
		// takes the id of the user being called and returns true if he is forwarding his calls
		return false;
	}
	
	public int get_forwarding(int callee_id)
	{

		try{
			DB_server dbServer = DB_server.getInstance();
			List<Object> params = Arrays.asList( (Object) callee_id);
			dbServer.execute_query(Query.GET_FORWARDING,params);
			ResultSet rst = dbServer.getRS();
			if(rst.first()){
				return rst.getInt("receiver_id");
			}
		}catch(SQLException e){
			JOptionPane.showMessageDialog(null, "Failed get callee_id forwarding\'s id","Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		// takes the id of the user being called and returns and returns the id of the user he is forwarding his calls to
		return 0;
	}
	
	public int get_forwarding_final(int caller_id,int callee_id){
		int fc = 0; //final callee
		int cc = callee_id; //current callee
		Blocking_Server blServer = Blocking_Server.getInstance();
		HashSet<Integer> dst = new HashSet<Integer>(); //destination set
		dst.add(caller_id);
		dst.add(callee_id);
		while(true){
			boolean flag=false;
			Iterator<Integer> iter = dst.iterator();

			while(iter.hasNext()){
				if(blServer.isBlocked((int)iter.next(),cc)){
					flag=true;
					break;
				}
			}
			//everytime cc changes check if anyone from the previous (on the chain)
			//users have him blocked.

			if(!flag){
				if(!is_forwarding(cc)){
					fc = cc;
					break;
				}
				else{
					cc = get_forwarding(cc);
					if(!dst.contains(cc)){
						dst.add(cc);
					}
					else{
						fc = -1;
						break;
					}
				}
			}
			else{
				fc=0;
				break;
			}
		}
		return fc;
	}

	public boolean forward(int callee_id, int forward_id)
	{
		try{
			DB_server dbServer = DB_server.getInstance();
			List<Object> params = Arrays.asList( (Object) callee_id, (Object) forward_id);
			dbServer.execute_query(Query.INSERT_FORWARDING,params);
			return true;
		}catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Failed to insert forwarding query", "Error", JOptionPane.ERROR_MESSAGE);
		}
		//updates database. who forwards to who.
		return false;
	}
	public boolean unforward(int callee_id)
	{
		try{
			DB_server dbServer = DB_server.getInstance();
			List<Object> params = Arrays.asList( (Object) callee_id);
			dbServer.execute_query(Query.REMOVE_FORWARDING,params);
			return true;
		}catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Failed to insert forwarding query", "Error", JOptionPane.ERROR_MESSAGE);
		}
		//updates database. deletes entry from database. the user no longer forwards his calls to another.
		return false;
	}
	public static void main(String[] args)
	{ 
		
		Forwarding_Server fwdServer = Forwarding_Server.getInstance();
		int caller = 1;
		int callee = 2;
		int final_target = callee;
		fwdServer.forward(2,3);
		//fwdServer.forward(3,2); //for cycle
		//fwdServer.forward(3,4); //for block block for fourth
		//fwdServer.unforward(2); //to check unforward
		if (fwdServer.is_forwarding(callee)) {
			System.out.println("Call is being forwarded.");
			final_target = fwdServer.get_forwarding_final(caller, callee);
					
		}
		if (final_target == -1) {
			System.out.println("Forwarding cycle was detected.");
		}
		else if (final_target == 0) {
			System.out.println("Call was blocked");
		}
		else {
			System.out.println("Final callee is " + final_target);
		}
	}
}
