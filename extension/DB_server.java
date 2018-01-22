package gov.nist.sip.proxy.extension;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

import gov.nist.sip.proxy.extension.xml_data_db;


public class DB_server{
	/* set designated address relevant to work dir */
	private static final String xml_designated_address = "../sip-communicator";
	private static final String xml_filename = "sip-communicator.xml";
	private static String DB_NAME="test";
	private static String user = "root";
	private static String ip = "127.0.0.0";
	// db ip must be the same as proxy
	private static String pass = "";
	private Connection con = null;
	private static ResultSet rs = null;
	private static Integer eur = 0; //execute update result
	private static DB_server instance = null;

	public enum Query {
		GET_FORWARDING, INSERT_FORWARDING,REMOVE_FORWARDING,IS_SIGNED_UP,
		INSERT_USER, AUTHENTICATE_USER, IS_BLOCKED, INSERT_BLOCKING, DELETE_BLOCKING, GET_BLOCKED_USERS, GET_USER_ID, GET_USERNAME,
		GET_DEBT, INSERT_POLICY, UPDATE_DEBT, INSERT_CALL, UPDATE_CALL_TIME, GET_CALLER, 
		GET_CALLEE, GET_CALL_START, GET_CALL_END, GET_POLICY_ID, RETURN_LAST_AI, RETURN_CALL_ID, GET_BLOCKED_USERNAMES}
	
	private enum SQLstmt{
		UPDATE, QUERY, NONCE
	};
	
	
	
	private static void get_xml_data(){
		String[] data = xml_data_db.getData(xml_filename,xml_designated_address);
		DB_NAME = data[0];
		user = data[1];
		ip = data[2];
		pass = data[3];
	}
	
	protected DB_server(){
		get_xml_data();
		String url = "jdbc:mysql://"+ip+":3306/" + DB_NAME;
		try{
			con = DriverManager.getConnection(url,user,pass);
		} catch (SQLException ex){
			JOptionPane.showMessageDialog(
					null, "Failed to open connection with db on url="+url, 
					"Error", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		} finally{
			/*try{
				if (con != null) {
                    //con.close();
                }
	            if(rs!=null){
	            	//rs.close();
	            }
			} catch (SQLException ex) {  
                JOptionPane.showMessageDialog(null, "Failed to close connection with db", "Error", JOptionPane.ERROR_MESSAGE);
            }*/
		}
	}


public static DB_server getInstance()
{
	if (instance == null) {
		instance = new DB_server();
	}
	return instance;
}

//if unitialised return -1;
public int get_nRows() throws SQLException{
	int rows;
	if(rs==null){
		return -1;
	}
	if (rs.last()) {
    	rows = rs.getRow();
	    rs.beforeFirst();
    }else{
    	rows = 0;
    }
    return rows;
}

public static List<Object> get_col(String label) throws SQLException{
	List<Object> res = new ArrayList<Object>();
	if (rs!=null){
		int idx = rs.findColumn(label);
		while (rs.next()) {
            res.add(rs.getObject(idx));
        }
        rs.beforeFirst();
	}
	return res;
}
public ResultSet getRS(){
	return rs;
}

public Object getFirst(String col){
	Object res=null;
	if(rs!=null){
		try{
			if(rs.first()){
				res = rs.getObject(col);
				rs.beforeFirst();
				return res;
			}
			else{
				System.out.println("Nothing to return..");
				System.out.println("Result set is empty!");
				return null;
			}
		}catch(SQLException e){
			JOptionPane.showMessageDialog(null, "Result Set method calls fail inside getFirst..","Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return null;
		}
	}else{
		System.out.println("Nothing to return..");
		System.out.println("Result set is null!");
		return null;
	}
}

public boolean is_empty() throws SQLException{
	return (rs==null)? true : ((boolean)(!rs.isBeforeFirst()));
}

public void execute_query (Query query, List<Object> params) throws SQLException{
	String stmt = "";
	PreparedStatement pst = null;
	SQLstmt type = 	SQLstmt.NONCE; 
	rs = null;
	try{
		switch(query){
			case GET_FORWARDING:
				stmt = "SELECT receiver_id FROM forwarding WHERE forwarder_id = ?";
				pst = con.prepareStatement(stmt);
				pst.setInt(1,(int) params.get(0));
				type = SQLstmt.QUERY;
				break;
			case INSERT_FORWARDING:
				stmt =  "INSERT IGNORE into forwarding (forwarder_id,receiver_id) values (?,?)";
				pst = con.prepareStatement(stmt);
				pst.setInt(1,(int) params.get(0));
				pst.setInt(2,(int) params.get(1));
				type = SQLstmt.UPDATE;
				break;
			case REMOVE_FORWARDING:
				stmt =  "Delete from forwarding WHERE forwarder_id=?";
				pst = con.prepareStatement(stmt);
				pst.setInt(1,(int) params.get(0));
				type = SQLstmt.UPDATE;
				break;
			case IS_SIGNED_UP:
		 		stmt = "SELECT * FROM users WHERE username = ?";
		 		pst = con.prepareStatement(stmt);
		 		pst.setString(1,(String) params.get(0));
		 		type = SQLstmt.QUERY;
		 		break;
		 	case INSERT_USER:
		 		stmt = "INSERT INTO users VALUES(DEFAULT,?,?,?,?,?)";
		 		pst = con.prepareStatement(stmt);
		 		pst.setString(1,(String) params.get(0)); //user
		 		pst.setString(2,(String) params.get(1)); //pass
		 		pst.setString(3,(String) params.get(2)); //email
		 		pst.setFloat(4,(float) params.get(3)); //debt
		 		pst.setInt(5,(int) params.get(4)); //billing policy
				type = SQLstmt.UPDATE;
		 		break;
		 	case AUTHENTICATE_USER:
		 		stmt = "SELECT * FROM users WHERE username = ? AND password = ?";
				pst = con.prepareStatement(stmt);
				pst.setString(1,(String) params.get(0));
				pst.setString(2,(String) params.get(1));
				type = SQLstmt.QUERY;
				break;
			case IS_BLOCKED:
				stmt = "SELECT * FROM blocking WHERE  blocker_id = ? AND blocked_id = ?";
				pst = con.prepareStatement(stmt);
				pst.setInt(1,(int) params.get(0));
				pst.setInt(2,(int) params.get(1));
				type = SQLstmt.QUERY;
				break;
			case INSERT_BLOCKING:
				stmt = 	"INSERT INTO blocking VALUES (?,?)";
				pst = con.prepareStatement(stmt);
				pst.setInt(1,(int) params.get(0));
				pst.setInt(2,(int) params.get(1));
				type = SQLstmt.UPDATE;
				break;
			case DELETE_BLOCKING:
				stmt = "DELETE FROM blocking WHERE blocker_id = ? AND blocked_id = ?";
				pst = con.prepareStatement(stmt);
				pst.setInt(1,(int) params.get(0));
				pst.setInt(2,(int) params.get(1));
				type = SQLstmt.UPDATE;
				break;
			case GET_BLOCKED_USERS:
				stmt = "SELECT blocked_id FROM blocking WHERE blocker_id = ?";
				pst = con.prepareStatement(stmt);
				pst.setInt(1,(int) params.get(0));
				type = SQLstmt.QUERY;
				break;
			case GET_BLOCKED_USERNAMES:
				stmt = "SELECT DISTINCT username FROM users INNER JOIN blocking ON (blocked_id = user_id AND blocker_id = ?)";
				pst = con.prepareStatement(stmt);
				pst.setInt(1, (int) params.get(0));
				type = SQLstmt.QUERY;
				break;
			case GET_USER_ID:
				stmt = "SELECT user_id FROM users WHERE username = ?";
				pst = con.prepareStatement(stmt);
				pst.setString(1,(String) params.get(0));
				type = SQLstmt.QUERY;
				break;
			case GET_USERNAME:
				stmt = "SELECT username FROM users WHERE user_id = ?";
				pst = con.prepareStatement(stmt);
				pst.setInt(1, (int) params.get(0));
				type = SQLstmt.QUERY;
				break;	
			case GET_DEBT:
				stmt = "SELECT debt FROM users WHERE user_id = ?";
				pst = con.prepareStatement(stmt);
				pst.setInt(1, (int) params.get(0)); //maybe problem with type
				type = SQLstmt.QUERY;
				break;
			case INSERT_POLICY:
				stmt =  "UPDATE users SET billing_policy = ? where user_id = ?"; //check check
				pst = con.prepareStatement(stmt);
				pst.setInt(1,(int) params.get(0));
				pst.setInt(2,(int) params.get(1));
				type = SQLstmt.UPDATE;
				break;
			case INSERT_CALL:
			stmt = "INSERT INTO calls (callee_id,caller_id,call_start,sipcallid) VALUES(?,?,?,?)";
	 		pst = con.prepareStatement(stmt);
	 		pst.setInt(1,(int) params.get(0)); //callee_id
	 		pst.setInt(2,(int) params.get(1)); //caller_id
	 		pst.setTimestamp(3,(Timestamp) params.get(2)); //start_time
	 		pst.setString(4,(String) params.get(3)); //start_time
			type = SQLstmt.UPDATE;
	 		break;
			case RETURN_CALL_ID:
				stmt = "SELECT call_id FROM calls WHERE sipcallid = ?";
				pst = con.prepareStatement(stmt);
				pst.setString(1, (String) params.get(0)); //maybe problem with type
				type = SQLstmt.QUERY;
				break;
			case UPDATE_CALL_TIME:
				stmt = "UPDATE calls SET call_end = ? where call_id = ?";
						
		 		pst = con.prepareStatement(stmt);
		 		pst.setTimestamp(1,(Timestamp) params.get(0)); //call_end
		 		pst.setInt(2,(int) params.get(1)); //call_id
				type = SQLstmt.UPDATE;
		 		break;
			case GET_CALLER:
				stmt = "SELECT caller_id FROM calls WHERE call_id = ?";
				pst = con.prepareStatement(stmt);
				pst.setInt(1, (int) params.get(0)); 
				type = SQLstmt.QUERY;
				break;
			case GET_CALLEE:
				stmt = "SELECT callee_id FROM calls WHERE call_id = ?";
				pst = con.prepareStatement(stmt);
				pst.setInt(1, (int) params.get(0)); 
				type = SQLstmt.QUERY;
				break;
			case GET_CALL_START:
				stmt = "SELECT call_start FROM calls WHERE call_id = ?";
				pst = con.prepareStatement(stmt);
				pst.setInt(1, (int) params.get(0));
				type = SQLstmt.QUERY;
				break;	
			case GET_CALL_END:
				stmt = "SELECT call_end FROM calls WHERE call_id = ?";
				pst = con.prepareStatement(stmt);
				pst.setInt(1, (int) params.get(0));
				type = SQLstmt.QUERY;
				break;	
			case GET_POLICY_ID:
				stmt = "SELECT billing_policy FROM users WHERE user_id = ?";
				pst = con.prepareStatement(stmt);
				pst.setInt(1, (int) params.get(0)); //maybe problem with type
				type = SQLstmt.QUERY;
				break;	
				
			case UPDATE_DEBT:
					stmt = "UPDATE users SET debt = ? where user_id = ?  ";
			 		pst = con.prepareStatement(stmt);
			 		pst.setFloat(1,(float) params.get(0)); //debt
			 		pst.setInt(2,(int) params.get(1)); //call_id
					type = SQLstmt.UPDATE;
			 		break;
		}
		switch(type){
			case QUERY:
				rs = pst.executeQuery();
				break;
			case UPDATE:
				eur = pst.executeUpdate();
				break;
			case NONCE:
				break;
		}
	} catch (SQLException e) {
		JOptionPane.showMessageDialog(null, "Failed \""+ stmt + "\" type query", "Error", JOptionPane.ERROR_MESSAGE);
		e.printStackTrace();
		if(pst!=null){
			pst.close();
		}
	}

}
// takes a query for the database and executes it return the result
/*public static void main(String[] args){
//test for xml parsing
	System.out.println("Before");
	System.out.println(DB_NAME+" "+ip+" "+user+" "+pass);
	System.out.println("After");
	get_xml_data();
	System.out.println(DB_NAME+" "+ip+" "+user+" "+pass);
}*/
}
