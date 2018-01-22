package net.java.sip.communicator.gui.extension;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import gov.nist.sip.proxy.extension.Forwarding_Server;
import net.java.sip.communicator.sip.security.UserCredentials;

public class Forwarding_gui extends JFrame{
	
	public static final long serialversionUID=1L;
	protected String username;
	protected int forwarder;
	protected JLabel cs; // current state 
	
	public boolean is_signed_up(String user){
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
	
	public Forwarding_gui(UserCredentials cred){
		String username = cred.getUserName();
	    forwarder = getUserId(username);
	    if(forwarder==0){
	    	JOptionPane.showMessageDialog(null, "Username derived from credentials does not exist!..","Error", JOptionPane.ERROR_MESSAGE);
	    	return;
	    }
		System.out.println("Username is "+ username);
		//design window
		this.setLayout(new BorderLayout());
		
		
		JPanel enp = init_enp(); //init enable forwarding panel
		JPanel dsp = init_dsp(); //init disable forwarding panel
		
		
		getContentPane().add(enp,BorderLayout.PAGE_START);
		getContentPane().add(dsp, BorderLayout.CENTER);
		
		this.setTitle("Forwarding");
		//this.setSize(300,140);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}
	
	protected void updateLabel()
	{
		Forwarding_Server fwdServer = Forwarding_Server.getInstance();
		if (fwdServer.is_forwarding(forwarder)) {
			cs.setText("<html> You are currently forwarding<br />    your calls to user: \""+ getUsername(fwdServer.get_forwarding(forwarder))+"\"</html>");
			this.setSize(300,160);
		} else {
			cs.setText("<html>Forwarding is disabled</html>");
			this.setSize(300,140);
		}
	}
	
	protected JPanel init_enp(){
		JPanel pnl = new JPanel();
		pnl.setLayout(new BorderLayout());
		//set panel layout
		cs = new JLabel();
		updateLabel();
		//set Label layout
		JButton fwdb = new JButton("Forward");
		final JTextField tf = new JTextField(10);
		
		ActionListener alb = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent a){
				String forwarding_username = tf.getText();
				if(is_signed_up(forwarding_username)){
					int receiver = getUserId(forwarding_username);
					Forwarding_Server fs = Forwarding_Server.getInstance();
					if(fs.is_forwarding(forwarder)){
						fs.unforward(forwarder);
					}
					if(fs.forward(forwarder,receiver)){
						JOptionPane.showMessageDialog(null, "Your calls were successfully forwarded to selected user.","Success", JOptionPane.INFORMATION_MESSAGE);
						//After success update Label
						updateLabel();
					}
				}else{
					JOptionPane.showMessageDialog(null, "User is not signed up.","Success", JOptionPane.INFORMATION_MESSAGE);
				}
				tf.setText("");
			}
			
		};
		fwdb.addActionListener(alb);
		
		//centering cs
		JPanel tmp_pnl2 = new JPanel();
		tmp_pnl2.setLayout(new FlowLayout(FlowLayout.CENTER));
		tmp_pnl2.add(cs);

		pnl.add(tmp_pnl2,BorderLayout.PAGE_START);
		//master a correct format
		JPanel tmp_pnl = new JPanel();
		tmp_pnl.setLayout(new FlowLayout());
		tmp_pnl.add(tf);
		tmp_pnl.add(fwdb);

		pnl.add(tmp_pnl,BorderLayout.CENTER);
		
		//BorderLaout
		return pnl;
	}
	
	protected JPanel init_dsp(){
		JPanel pnl = new JPanel();
		pnl.setLayout(new FlowLayout(FlowLayout.CENTER));
		JButton dbt = new JButton("Disable Forwarding");
		dbt.setPreferredSize(new Dimension(200,20));
		
		ActionListener alb = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent a){
				Forwarding_Server fs = Forwarding_Server.getInstance();
				if(fs.unforward(forwarder)){
					JOptionPane.showMessageDialog(null, "Forwarding is now disabled.","Success",JOptionPane.INFORMATION_MESSAGE);
					updateLabel();
				}
			}
		};
		
		dbt.addActionListener(alb);
		pnl.add(dbt);
		return pnl;
		
	}
	
	public static void main(String[] args)
	{  
		/*
		UserCredentials cred = new UserCredentials();
		cred.setUserName("Mario Moretti");
		new Forwarding_gui(cred);*/
	}

}
