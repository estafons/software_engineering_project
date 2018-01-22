package net.java.sip.communicator.gui.extension;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import gov.nist.sip.proxy.extension.Blocking_Server;
import gov.nist.sip.proxy.extension.DB_server;
import gov.nist.sip.proxy.extension.Forwarding_Server;
import gov.nist.sip.proxy.extension.DB_server.Query;
import net.java.sip.communicator.gui.extension.Billing_gui.ButtonClickListener;
import net.java.sip.communicator.sip.security.UserCredentials;

public class Blocking_gui extends JFrame{
	
	private static final long serialVersionUID = 1L;
	protected String Username;
	protected int BlockerId;
	protected JButton BlockButton;
	private JFrame mainFrame;
	private JLabel headerLabel;
	private JLabel statusLabel;
	private JPanel controlPanel;
	private JTextField text;
	private String Blocked_UNs = "";
	private String Busers ="";
	protected int user=0;
	protected String username = "";
	public Blocking_gui(UserCredentials cred){
	   prepareGUI(cred);
	   showEvent(cred);          
	}
	private void prepareGUI(UserCredentials cred){
	      mainFrame = new JFrame("Manage Blocked Users");
	      mainFrame.setSize(330,145);
	      mainFrame.setLayout(new GridLayout(3, 1));

	      headerLabel = new JLabel("",JLabel.CENTER );
	      statusLabel = new JLabel("",JLabel.CENTER);        

	      statusLabel.setSize(300,145);
	      controlPanel = new JPanel();
	      controlPanel.setLayout(new FlowLayout());

	      mainFrame.add(headerLabel);
	      mainFrame.add(controlPanel);
	      mainFrame.add(statusLabel);
	      
	      mainFrame.setVisible(true);
	      username = cred.getUserName();
	      user = getUserId(username); 
	      Blocking_Server Block = Blocking_Server.getInstance();
	      Blocked_UNs = String.join(", ", Block.getBlockedUserNames(user));
	      if(!Blocked_UNs.trim().equals("")){
	    	  Busers = "Blocked Users are: "+ Blocked_UNs;
	      }else{
	    	  Busers = "";
	      }
	      statusLabel.setText(Busers);
	      
	      this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	   }
	private int getUserId(String user){
		int id = 0;
		try{
			DB_server dbs = DB_server.getInstance();
			List<Object> ps = Arrays.asList((Object) user);
			dbs.execute_query(Query.GET_USER_ID, ps);
			return dbs.is_empty()? 0 : (int) dbs.getFirst("user_id");
		}catch(SQLException e){
			JOptionPane.showMessageDialog(null, "Failed GET_USER_ID query",	"Error", JOptionPane.ERROR_MESSAGE);
		}
		return id;
	}
	private String getUsername(int user){
		String username = "";
		try{
			DB_server dbs = DB_server.getInstance();
			List<Object> ps = Arrays.asList((Object) user);
			dbs.execute_query(Query.GET_USERNAME, ps);
			return (String) dbs.getFirst("username");

		}catch(SQLException e){
			JOptionPane.showMessageDialog(null, "Failed GET_USERNAME query","Error", JOptionPane.ERROR_MESSAGE);
		}
		return username;
	}
	private boolean is_signed_up(String user){
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
				res = false;
			}else{
				//user exists
				res = true;
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Failed IS_SIGNED_UP query","Error", JOptionPane.ERROR_MESSAGE);
		}
		return res;
	}
	private void showEvent(UserCredentials cred){
		  if(user==0){
		    	JOptionPane.showMessageDialog(null, "Username derived from credentials does not exist!..","Error", JOptionPane.ERROR_MESSAGE);
		    	return;
	      }
	      headerLabel.setText("Hi User: " +username ); 
	      JButton okButton = new JButton("Block");
	      JButton submitButton = new JButton("Unblock");
	      JTextField text = new JTextField(10);
	      okButton.setActionCommand("Block");
	      submitButton.setActionCommand("Unblock");
	      controlPanel.add(okButton);
	      controlPanel.add(submitButton);      
	      controlPanel.add(text);      
	      mainFrame.setVisible(true);  
	   
	      ActionListener blockal = new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent a){
					String blocked_username = text.getText();
					int blocked = getUserId(blocked_username);
					Blocking_Server Block = Blocking_Server.getInstance();
					if (is_signed_up(blocked_username)){
						if (!Block.isBlocked(blocked,user)){
							if(Block.block(user, blocked)){
								Blocked_UNs = String.join(", ", Block.getBlockedUserNames(user));
								Busers = "Blocked Users are:\n"+ Blocked_UNs;
								statusLabel.setText("<html>Blocked "+blocked_username+"<br />"+Busers+"</html>");
							}
							else{
								statusLabel.setText("<html>Problem blocking user: \""+blocked_username+"\""+"<br />"+Busers+"</html>");
							}
						}
						else {
							statusLabel.setText("<html>User already blocked "+blocked_username+"<br />"+Busers+"</html>");
						}
					
					}
					else {
						statusLabel.setText("<html>There is no such user: \""+blocked_username+"\"<br />"+Busers+"</html>");
					}
					text.setText("");
				}
				
			};
			okButton.addActionListener(blockal);
			ActionListener unbloackal = new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent a){
					String blocked_username = text.getText();
					int blocked = getUserId(blocked_username);
					Blocking_Server Block = Blocking_Server.getInstance();
					if(Block.isBlocked(blocked,user)){
						if(Block.unblock(user, blocked)){
							Blocked_UNs = String.join(", ", Block.getBlockedUserNames(user));
							if(!Blocked_UNs.trim().equals("")){
						    	  Busers = "Blocked Users are: "+ Blocked_UNs;
						    }else{
						    	  Busers = "";
						    }
							statusLabel.setText("<html>"+"Sucessfully unblocked user: \""+blocked_username+"\"<br />"+Busers+"</html>");
						}
						else {
							statusLabel.setText("<html>"+"Problem unblocking user: \""+blocked_username+"\"<br />"+Busers+"</html>");
						}
					}
					else {
							statusLabel.setText("<html>"+"User: \""+blocked_username+"\" is not currently blocked..<br />"+Busers+"</html>");
					}
					text.setText("");
				}
			};
			submitButton.addActionListener(unbloackal);
	}		
	public static void main(String[] args)
	{  
		UserCredentials cred = new UserCredentials();
		cred.setUserName("Mario Moretti");
		new Blocking_gui(cred);
	}
}
