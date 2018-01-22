package net.java.sip.communicator.gui.extension;
import java.awt.BorderLayout;
import java.awt.Dimension;
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

import gov.nist.sip.proxy.extension.DB_server;
import gov.nist.sip.proxy.extension.DB_server.Query;
import gov.nist.sip.proxy.extension.Billing_server;
import net.java.sip.communicator.sip.security.UserCredentials;

public class Billing_gui extends JFrame{
	
	   private static final long serialVersionUID = 1L;
	   private JFrame mainFrame;
	   private JLabel headerLabel;
	   private JLabel statusLabel;
	   private JPanel controlPanel;

	   public Billing_gui(UserCredentials cred){
	      prepareGUI();
	      
	  	  showEvent(cred);       
	      
	   }
	      
	public static final long serialversionUID=1;
	
	protected String Username;
	
	protected int UserId;
	
	
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
	private int getPolicy(int user){
		 int policy = 0;
		try{
			DB_server dbs = DB_server.getInstance();
			List<Object> ps = Arrays.asList((Object) user);
			dbs.execute_query(Query.GET_POLICY_ID, ps);
			return (int) dbs.getFirst("billing_policy");

		}catch(SQLException e){
			JOptionPane.showMessageDialog(null, "Failed GET_POLICY query","Error", JOptionPane.ERROR_MESSAGE);
		}
		return policy;
	}
	private float getDebt(int user){
		 float debt = 0;
		try{
			DB_server dbs = DB_server.getInstance();
			List<Object> ps = Arrays.asList((Object) user);
			dbs.execute_query(Query.GET_DEBT, ps);
			return (float) dbs.getFirst("debt");

		}catch(SQLException e){
			JOptionPane.showMessageDialog(null, "Failed GET_DEBT query","Error", JOptionPane.ERROR_MESSAGE);
		}
		return debt;
	}
	
	private void showEvent(UserCredentials cred){
		  String username = cred.getUserName();
	      int user = getUserId(username);
	      
	      if(user==0){
		    	JOptionPane.showMessageDialog(null, "Username derived from credentials does not exist!..","Error", JOptionPane.ERROR_MESSAGE);
		    	return;
	      }
	      int policy =getPolicy(user);
	      float debt = getDebt(user);
	      String policy_name = "";
	      switch(policy){
	      case 1: policy_name = "normal"; break;
	      case 2: policy_name = "premium"; break;
	      case 3: policy_name = "weekend"; break;
	      }
	      headerLabel.setText("Hi User: " +username + " Your bill is "+ debt+" and you are under "+ policy_name+" policy"); 

	      JButton okButton = new JButton("OK");
	      JButton submitButton = new JButton("Complain");

	      okButton.setActionCommand("OK");
	      submitButton.setActionCommand("Complain");

	      okButton.addActionListener(new ButtonClickListener()); 
	      submitButton.addActionListener(new ButtonClickListener()); 

	      controlPanel.add(okButton);
	      controlPanel.add(submitButton);      

	      mainFrame.setVisible(true);  
	   }

	private void prepareGUI(){
	      mainFrame = new JFrame("Billing Info");
	      mainFrame.setSize(320,135);
	      mainFrame.setLayout(new GridLayout(3, 1));

	      headerLabel = new JLabel("",JLabel.CENTER );
	      statusLabel = new JLabel("",JLabel.CENTER);        

	      statusLabel.setSize(300,100);
	      
	      controlPanel = new JPanel();
	      controlPanel.setLayout(new FlowLayout());

	      mainFrame.add(headerLabel);
	      mainFrame.add(controlPanel);
	      mainFrame.add(statusLabel);
	      mainFrame.setVisible(true);
	      this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	   }
	
	public static void main(String[] args)
	{  
	/*	UserCredentials cred = new UserCredentials();
		cred.setUserName("Mario Moretti");
		new Billing_gui(cred);*/
	
 }
		
class ButtonClickListener implements ActionListener{
    public void actionPerformed(ActionEvent e) {
       String command = e.getActionCommand();  
       if( command.equals( "OK" ))  {
          statusLabel.setText("Thanks mate");
       }
       else if( command.equals( "Complain" ) )  {
          statusLabel.setText("Well actually we really dont care if you want to complain"); 
       }	
    }		
 }
}