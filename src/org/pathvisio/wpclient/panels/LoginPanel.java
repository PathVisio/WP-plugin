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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.xml.rpc.ServiceException;

import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.wpclient.WikiPathwaysClientPlugin;
import org.pathvisio.wpclient.validators.Validator;
import org.wikipathways.client.WikiPathwaysClient;

public class LoginPanel extends JPanel implements ActionListener {
	static String Username = "";
	static String Password = "";
	boolean itsFirst = true;
	boolean itsKeep = false;
	JTextField UserField = new JTextField(15);
	JPasswordField PassField = new JPasswordField(15);
	JCheckBox itsKeepBox = new JCheckBox("Save details:", false);
	boolean itsInit = false;
	private PvDesktop desktop;
	String clientName;
	private JComboBox clientDropdown;
	WikiPathwaysClient client;
	String actiontype;
	static boolean loggedin;

	public LoginPanel(PvDesktop desktop) {
		super();
		this.desktop = desktop;
		setLayout(new GridLayout(3, 2));
		add(new JLabel("Username:"));
		add(UserField);
		add(new JLabel("Password:"));
		add(PassField);
		add(itsKeepBox);
		loggedin = false;

	}

	public WikiPathwaysClient login() throws RemoteException,
			MalformedURLException, ServiceException {

		try {

			client = WikiPathwaysClientPlugin.loadClient();

			Username = UserField.getText();
			Password = PassField.getText();
			if (Validator.CheckNonAlpha(Username)) {

				client.login(Username, Password);
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
			JOptionPane
					.showMessageDialog(
							null,
							"You do not have permissions. \n Please Send an email to:\n wikipathways-devel@googlegroups.com",
							"WikiPathways Login ERROR",
							JOptionPane.ERROR_MESSAGE);
			Username = "";
			Password = "";
		}
		return client;

	}

	@Override
	public void actionPerformed(ActionEvent e) {

	}

}
