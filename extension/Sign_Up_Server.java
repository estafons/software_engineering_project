package gov.nist.sip.proxy.extension;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

public class Sign_Up_Server extends DB_server{
	
private static Sign_Up_Server instance = null;
	
	protected Sign_Up_Server()
	{
		// Exists only to defeat instantiation.
	}
	
	
	public static Sign_Up_Server getInstance()
	{
		if (instance == null) {
			instance = new Sign_Up_Server();
		}
		return instance;
	}
	public boolean do_sign_up(String user, String pass, String email){
		boolean res = false;
		int n=-1;
		try{
			DB_server dBServer = DB_server.getInstance();
			List<Object> params = null;

			params = Arrays.asList((Object) user);
			dBServer.execute_query(Query.IS_SIGNED_UP ,params);
			n=dBServer.get_nRows();
			if(n==-1){
				JOptionPane.showMessageDialog(null, "Failed query is not executed", "Error", JOptionPane.ERROR_MESSAGE);
					return false;
			}else if(n==0){
				//user does not exist
				System.out.println("Sign Up: User = \""+user+"\"..");
				params = Arrays.asList( (Object) user, (Object) pass, (Object) email, (Object) 0.0f, (Object) 1);
				dBServer.execute_query(Query.INSERT_USER,params);
				res = true;
			}else{
				//System.out.println("User = \""+user+"\" exists!");
				res = false;
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(
					null, "Failed DO_SIGN_UP query", 
					"Error", JOptionPane.ERROR_MESSAGE);
		}
		return res;
	}
	public boolean authenticate_user(String username, String password){
		boolean res = false;
		try{		
			DB_server dbServer = DB_server.getInstance();
			List<Object> params = Arrays.asList( (Object) username, (Object) password);
			dbServer.execute_query(Query.AUTHENTICATE_USER,params);
			Integer n=dbServer.get_nRows();
			if(n==-1){
				JOptionPane.showMessageDialog(null, "Failed query is not executed", "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}else if(n==0){
				res = false;
			}else{
				res = true;
			}
		}catch (SQLException e){
			JOptionPane.showMessageDialog(null, "Failed AUTHENTICATE_USER query", 
					"Error", JOptionPane.ERROR_MESSAGE);
		}
		return res;
	}
	public static void main(String[] args)
	{  
		/*
		Sign_Up_Server suserver = Sign_Up_Server.getInstance();
		boolean su = suserver.do_sign_up("Mario Moretti", "Brigade Rosse", "mmoreti@gmail.com");
		if(su){
			System.out.println("Sign Up was Succesfull");
		}
		else{
			System.out.println("Sign Up unsuccesfull");
		}
		su = suserver.authenticate_user("Mario Moretti", "BrigadeRosse");
		if(su){
			System.out.println("Authentication succesfull");
		}
		else{
			System.out.println("Authentication unsuccesfull");
		}*/
	}

}
