package net.java.sip.communicator.gui.extension;

import net.java.sip.communicator.SipCommunicator;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainMenu extends JFrame{
	
	public static final long serialversionUID=1L;
	private static boolean open = false; //a flag to check if already open

	public MainMenu(){
		//init and settings
		final JFrame mf = new JFrame();
		mf.setLayout(new BorderLayout());
		//new BorderLayout(10,10)
		

		//Add Title
		JLabel t = new JLabel("RENEGADE",JLabel.CENTER);
		t.setSize(150,30);
		mf.add(t,BorderLayout.NORTH);
		
		//Add Buttons
		//login button
		JButton login = new JButton("Login");
		login.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e){
				if(open){
					return;
				}
				else{
					open = true;
				}
				String[] in = {};
				SipCommunicator.main(in);
				mf.dispose();
			}
		});
		
		//register button
		JButton register = new JButton("Register");
		register.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e){
				new Sign_Up_gui();
				mf.dispose();
			}
		});
		
		//a panel that has login and register button
		JPanel lrPanel = new JPanel();
		
		//add buttons to panel
		lrPanel.add(login);
		lrPanel.add(register);

		
		//finally add panel to main frame
		mf.add(t, BorderLayout.NORTH);
		mf.add(lrPanel,BorderLayout.CENTER);
		mf.setSize(260,100);
		mf.setTitle("Main Menu");
		mf.setLocationByPlatform(true);
		mf.setVisible(true);
		mf.setLocationRelativeTo(null);
		mf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}

	public static void main(String[] args)
	{  
		new MainMenu();
	}

}
