// PathVisio WP Client
// Plugin that provides a WikiPathways client for PathVisio.
// Copyright 2013 developed for Google Summer of Code
//
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// 
// http://www.apache.org/licenses/LICENSE-2.0 
//  
// Unless required by applicable law or agreed to in writing, software 
// distributed under the License is distributed on an "AS IS" BASIS, 
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
// See the License for the specific language governing permissions and 
// limitations under the License.
//
package org.pathvisio.wpclient.panels;

/**
 * Class: LoginPanel
 * Description: A simple class to get username and password.
 * @author Sravanthi Sinha
 * @author Martina Kutmon
 * @version 1.0
 **/

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.xml.rpc.ServiceException;

import org.pathvisio.wpclient.WikiPathwaysClientPlugin;
import org.pathvisio.wpclient.validators.Validator;

public class LoginPanel extends JPanel implements ActionListener {
	static String Username = "";
	static String Password = "";
	boolean itsFirst = true;
	boolean itsKeep = false;
	JTextField UserField = new JTextField(15);
	JPasswordField PassField = new JPasswordField(15);
	JCheckBox itsKeepBox = new JCheckBox("Save details:", false);
	boolean itsInit = false;
	String clientName;
	String actiontype;
	static boolean loggedin;
	private WikiPathwaysClientPlugin plugin;

	public LoginPanel(WikiPathwaysClientPlugin plugin) {
		super();
		this.plugin = plugin;
		setLayout(new GridLayout(3, 2));
		add(new JLabel("Username:"));
		add(UserField);
		add(new JLabel("Password:"));
		add(PassField);
		add(itsKeepBox);
		loggedin = false;

	}

	public void login() throws RemoteException,
			MalformedURLException, ServiceException {

		try {
			Username = UserField.getText();
			Password = PassField.getPassword().toString();
			if (Validator.CheckNonAlpha(Username)) {

				plugin.getWpQueries().login(Username, Password);
				loggedin = true;
				if (!itsKeepBox.isSelected()) {
					Username = "";
					Password = "";
				}

			} else {
				JOptionPane.showMessageDialog(null,
						"Please Enter Valid UserName", "ERROR",
						JOptionPane.ERROR_MESSAGE);
				Username = "";
				Password = "";
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			JOptionPane
					.showMessageDialog(
							null,
							"You do not have permissions. \n Please Send an email to:\n wikipathways-devel@googlegroups.com",
							"WikiPathways Login ERROR",
							JOptionPane.ERROR_MESSAGE);
			Username = "";
			Password = "";
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

	}

}
