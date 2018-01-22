package gov.nist.sip.proxy.extension;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JOptionPane;

public class Blocking_Server extends DB_server{
	
	private static Blocking_Server instance = null;
	
	protected Blocking_Server()
	{
	    // Exists only to defeat instantiation.
	}
	
	public static Blocking_Server getInstance()
	{
		if (instance == null) {
			instance = new Blocking_Server();
		}
		return instance;
		//creates and returns instance of blocking server
	}
	
	
	public boolean isBlocked (int caller, int callee)
	{
		boolean res = false;
		try{
			DB_server dbServer = DB_server.getInstance();
			List<Object> params = Arrays.asList((Object) callee, (Object) caller);
			dbServer.execute_query(Query.IS_BLOCKED,params);
			res = !dbServer.is_empty();
		}catch(SQLException e){
			JOptionPane.showMessageDialog(null, "Failed IS_BLOCKED query","Error", JOptionPane.ERROR_MESSAGE);
		}
		return res;
		//checks in db if the callee has blocked the caller
	}
	
	public boolean block (int blocker, int blocked)
	{
		boolean res = false;
		try {
			DB_server dbServer = DB_server.getInstance();
			List<Object> params = Arrays.asList( (Object) blocker, (Object) blocked);
			dbServer.execute_query(Query.INSERT_BLOCKING,params);
			res = true;		
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Failed INSERT_BLOCKING query. User is probably already blocked.","Error", JOptionPane.ERROR_MESSAGE);
		}
		return res;
		//blocks a user,  updates the blocked list in db
	}
	
	public boolean unblock (int blocker, int blocked)
	{
		boolean res = false;
		try {
			DB_server dbServer  = DB_server.getInstance();
			List<Object> params = Arrays.asList( (Object) blocker, (Object) blocked);
			dbServer.execute_query(Query.DELETE_BLOCKING,params);
			res = true;			
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Failed DELETE_BLOCKING query. User probably wasn't blocked.\n" + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		return res;
		//unblocks a user
	}
	
	public List<Integer> getBlockedUsers (int userId)
	{
		List<Integer> l = new ArrayList<Integer>();
		try{
			DB_server dbServer = DB_server.getInstance();
			List<Object> params = Arrays.asList((Object) userId);
			dbServer.execute_query(Query.GET_BLOCKED_USERS,params);
			l = (List<Integer>) ((Object) dbServer.get_col("blocked_id"));
		}catch(SQLException e){
			JOptionPane.showMessageDialog(null, "Failed GET_BLOCKED_USERS query","Error", JOptionPane.ERROR_MESSAGE);
		}
		return l;
		//returns a list of blocked users ids
	}
	
	public List<String> getBlockedUserNames (int userId){
		List<String> s = new ArrayList<String>();
		try{
			DB_server dbServer = DB_server.getInstance();
			List<Object> params = Arrays.asList((Object) userId);
			dbServer.execute_query(Query.GET_BLOCKED_USERNAMES,params);
			if(is_empty()){
				s = (List<String>)Arrays.asList((String) "");
			}else{
				s = (List<String>) ((Object) dbServer.get_col("username"));
			}
		}catch(SQLException e){
			JOptionPane.showMessageDialog(null, "Failed GET_BLOCKED_USERNAMES query","Error", JOptionPane.ERROR_MESSAGE);
		}
		return s;
	}
	
	public static void main(String[] args)
	{  
			/*
	        Blocking_Server blServer = Blocking_Server.getInstance();
	        int caller = 4;
	        int callee = 1;
	        boolean result = blServer.block(caller, 1);
	        */
		
	        /*
	        System.out.println("User " + caller + " is blocked by user " + callee + ": " + result);
	        result = blServer.isBlocked(callee, caller);
	        System.out.println("User " + caller + " is blocked by user " + callee + ": " + result);
	        result = blServer.unblock(caller, callee);
	        System.out.println("User " + caller + " is blocked by user " + callee + ": " + result);
	        result = blServer.isBlocked(callee, caller);
	        System.out.println("User " + caller + " is blocked by user " + callee + ": " + result);
	        */
	        /*
	        result = blServer.block(caller, 2);
	        result = blServer.block(caller, 3);
	        result = blServer.block(caller, 5);
	        List<Integer> list = blServer.getBlockedUsers(caller);
	        System.out.println(Arrays.toString(list.toArray()));*/
	        
	 }  
}
