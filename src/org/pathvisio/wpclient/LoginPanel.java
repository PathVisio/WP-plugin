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
package org.pathvisio.wpclient;

/**
 * Class: LoginPanel
 * Description: A simple class to get username and password.
 * @author Sravanthi Sinha
 * @version 1.0
 **/

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.xml.rpc.ServiceException;

import org.pathvisio.core.debug.Logger;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.wikipathways.webservice.WSPathway;
import org.pathvisio.wikipathways.webservice.WSPathwayInfo;
import org.wikipathways.client.WikiPathwaysClient;

import com.sun.net.httpserver.Authenticator.Success;

public class LoginPanel extends JPanel implements ActionListener {
	static String Username="";
	static String Password ="";
	boolean itsFirst = true;
	boolean itsKeep = false;
	JTextField UserField = new JTextField(15);
	JPasswordField PassField = new JPasswordField(15);
	JCheckBox itsKeepBox = new JCheckBox("Save details:", false);
	boolean itsInit = false;
	private WikiPathwaysClientPlugin plugin;
	private PvDesktop desktop;
	String clientName;
	private JComboBox clientDropdown;
	WikiPathwaysClient client;
	String actiontype;
static boolean loggedin ;


	public LoginPanel(PvDesktop desktop, WikiPathwaysClientPlugin plugin) {
		super();
		this.plugin = plugin;
		this.desktop = desktop;		
		setLayout(new GridLayout(3, 2));
		add(new JLabel("Username:"));
		add(UserField);
		add(new JLabel("Password:"));
		add(PassField);
		add(itsKeepBox);
		loggedin=false;
		
	}




	public  WikiPathwaysClient login() throws RemoteException, MalformedURLException,
			ServiceException {
	
		
		try{
			
		client = WikiPathwaysClientPlugin.loadClient();
	
		Username=UserField.getText();
		Password=PassField.getText();
	
		
		client.login(Username,Password);
		loggedin=true;
		if(!itsKeepBox.isSelected())
		{
			Username="";
			Password="";
		}
		
		}catch(Exception ex)
		{
			JOptionPane.showMessageDialog(null, "You do not have permissions",
					"ERROR", JOptionPane.ERROR_MESSAGE);
		}
		return client;
	
	}

	

	public void updatePathway() {
		try {
			login();
			WSPathway wsp = client.getPathway("WP1");
			Pathway p = WikiPathwaysClient.toPathway(wsp);
			p.getMappInfo().addComment(
					"Soap test - " + System.currentTimeMillis(), "Soap test");

			client.updatePathway("WP1", p,
					"Soap test - " + System.currentTimeMillis(),
					Integer.parseInt(wsp.getRevision()));

			JOptionPane.showMessageDialog(null, "The pathway updated");

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		
	}

}
