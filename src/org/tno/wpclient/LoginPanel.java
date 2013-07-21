package org.tno.wpclient;

/**
 * Class: LoginPane
 * Description: A simple class to get user's loginname and password.
 * NOTE: This class is not very secure!
 * @author Le Cuong Nguyen
 **/
//package atnf.atoms.mon.util;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginPanel extends JPanel implements ActionListener {
	String itsUsername = "";
	String itsPassword = "";
	boolean itsFirst = true;
	boolean itsKeep = false;
	JTextField itsUserField = new JTextField(15);
	JPasswordField itsPassField = new JPasswordField(15);
	JCheckBox itsKeepBox = new JCheckBox("Save details:", false);
	boolean itsInit = false;

	public LoginPanel() {
		super();

		setLayout(new GridLayout(3, 2));
		add(new JLabel("Username:"));
		add(itsUserField);
		add(new JLabel("Password"));
		add(itsPassField);
		add(itsKeepBox);
		JButton submit = new JButton("done");
		add(submit);
		submit.addActionListener(this);

	}

	public String[] getLogin() {
		if (!itsKeep && !itsFirst) {
			return null;
		}
		if (!itsInit) {
			return null;
		}
		itsFirst = false;
		String[] res = new String[2];
		res[0] = itsUsername;
		res[1] = itsPassword;
		if (!itsKeep) {
			itsUsername = "";
			itsPassword = "";
		}
		return res;
	}

	public void actionPerformed(ActionEvent e) {
		itsUsername = itsUserField.getText();
		itsPassword = new String(itsPassField.getPassword());
		itsKeep = itsKeepBox.isSelected();
		itsInit = true;
		setVisible(false);
	}
}
