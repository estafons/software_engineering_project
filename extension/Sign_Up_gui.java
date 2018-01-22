package net.java.sip.communicator.gui.extension;

import gov.nist.sip.proxy.extension.Sign_Up_Server;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;


public class Sign_Up_gui extends JFrame{
	
	public static final long serialversionUID=1;
	
	public Sign_Up_gui(){
		JPanel mainpanel;

		this.setLayout(new BorderLayout());

		mainpanel = init();

		getContentPane().add(mainpanel);

		this.setSize(280,200);
		this.setTitle("Registration Form");
		this.setLocationByPlatform(true);
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}


	protected JPanel init(){
		JPanel mainpanel = new JPanel();
		
		mainpanel.setLayout(null);

		//Add Title
		JLabel Title = new JLabel("Sign Up");
		Title.setSize(200,30);
		Title.setLocation(119,2);
		mainpanel.add(Title);
		
		//Username, Password, E-mail
		JLabel userl = new JLabel("Username:");
		userl.setSize(80,30);
		userl.setLocation(5,30);
		mainpanel.add(userl);
		
		final JTextField userf = new JTextField(20);
		userf.setSize(160, 20);
		userf.setLocation(75, 36);
		mainpanel.add(userf);
		
		JLabel passl = new JLabel("Password:");
		passl.setSize(80, 30);
		passl.setLocation(5,54);
		mainpanel.add(passl);
		
		final JPasswordField passf = new JPasswordField(20);
		passf.setSize(160, 20);
		passf.setLocation(75, 61);
		mainpanel.add(passf);
		
		
		JLabel emaill = new JLabel("Email:");
		emaill.setSize(80, 30);
		emaill.setLocation(32,79);
		mainpanel.add(emaill);
		
		final JTextField emailf = new JTextField(20);
		emailf.setSize(160, 20);
		emailf.setLocation(75, 86);
		mainpanel.add(emailf);
		
		//Submit Button
		JButton submit = new JButton("Submit");
		submit.setSize(100, 25);
		submit.setLocation(97,116);
		
		ActionListener sal = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
						String username = userf.getText();
			String password = new String(passf.getPassword());
			String email = emailf.getText();
			
			if (username.equals("")) {
				JOptionPane.showMessageDialog(null, "The username field cannot be empty.","Empty username", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			if (password.equals("")) {
				JOptionPane.showMessageDialog(null, "The password field cannot be empty.","Empty password", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			if (email.equals("")) {
				JOptionPane.showMessageDialog(null, "The e-mail field cannot be empty.",	"Empty email", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			System.out.println("Password is " + password);
			Sign_Up_Server suServer = Sign_Up_Server.getInstance();
			
			//try to sign up user
			if(suServer.do_sign_up(username, password, email)) {
				JOptionPane.showMessageDialog(null, "You were successfully signed up.",	"Success", JOptionPane.INFORMATION_MESSAGE);
			}
			else {
				JOptionPane.showMessageDialog(null, "Username already exists.",	"Invalid username", JOptionPane.INFORMATION_MESSAGE);
			}
			}
		};
		submit.addActionListener(sal);
		mainpanel.add(submit);
		
		//Add a return button
		JButton ret = new JButton("Main Menu");
		ret.setSize(80,25);
		ret.setLocation(0,137);
		ret.setMargin(new Insets(0, 0, 0, 0));
		ret.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				new MainMenu();
				dispose();
			}
		});
		mainpanel.add(ret);
		
		return mainpanel;

	}
	public static void main(String[] args)
	{  
		new Sign_Up_gui();
	}

}
