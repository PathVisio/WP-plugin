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
 * Description: A simple class to get user's loginname and password.
 * @author Sravanthi Sinha
 * @version 1.0
 **/

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import org.pathvisio.core.debug.Logger;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.wikipathways.webservice.WSPathway;
import org.wikipathways.client.WikiPathwaysClient;

public class LoginPanel extends JPanel implements ActionListener 
{
	String itsUsername = "";
	String itsPassword = "";
	boolean itsFirst = true;
	boolean itsKeep = false;
	JTextField itsUserField = new JTextField(15);
	JPasswordField itsPassField = new JPasswordField(15);
	JCheckBox itsKeepBox = new JCheckBox("Save details:", false);
	boolean itsInit = false;
	private WikiPathwaysClientPlugin plugin;
	private PvDesktop desktop;String clientName ;
	private JComboBox clientDropdown; WikiPathwaysClient client ;
	String actiontype;

	public LoginPanel(PvDesktop desktop, WikiPathwaysClientPlugin plugin, String actiontype)
	{
		super();
		this.plugin=plugin;
		this.desktop=desktop;
		this.actiontype=actiontype;
		setLayout(new GridLayout(3, 2));
		add(new JLabel("Username:"));
		add(itsUserField);
		add(new JLabel("Password"));
		add(itsPassField);
		add(itsKeepBox);
		JButton submit = new JButton("done");
		add(submit);
		Vector<String> clients = new Vector<String>(plugin.getClients().keySet());
		Collections.sort(clients);
		
		clientDropdown = new JComboBox(clients);
		clientDropdown.setSelectedIndex(0);
		clientDropdown.setRenderer(new DefaultListCellRenderer() 
		{
			public Component getListCellRendererComponent(final JList list,final Object value, final int index,final boolean isSelected, final boolean cellHasFocus) 
			{
				String strValue = WikiPathwaysClientPlugin.shortClientName(value.toString());
				return super.getListCellRendererComponent(list, strValue,index, isSelected, cellHasFocus);
			}
		});
		submit.addActionListener(this);

	}

	public String[] getLogin()
	{
		if (!itsKeep && !itsFirst) 
		{
			return null;
		}
		if (!itsInit) 
		{
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

	public void actionPerformed(ActionEvent e)
	{
	
	

		if(actiontype.equalsIgnoreCase("create"))
			createPathway();
		else if(actiontype.equalsIgnoreCase("update"))
			updatePathway();
		
	}
	
	String user = "sravanthi";
	String pass = "kmitkmit";
	private void login() throws RemoteException {
		 clientName = clientDropdown.getSelectedItem().toString();
		 client = plugin.getClients().get(clientName);
		client.login(user, pass);
	
	

	}
	public void createPathway(){
		try {
			login();
		
			
			client.createPathway(desktop.getSwingEngine().getEngine().getActivePathway());
			
			JOptionPane.showMessageDialog(null,"The pathway created");
		

			
		} catch(Exception e) {
			e.printStackTrace();
			
		}
	}
	
	public void updatePathway() {
		try {
			login();
			WSPathway wsp = client.getPathway("WP1");
			Pathway p = WikiPathwaysClient.toPathway(wsp);
			p.getMappInfo().addComment("Soap test - " + System.currentTimeMillis(), "Soap test");

			client.updatePathway(
					"WP1", p,
					"Soap test - " + System.currentTimeMillis(),
					Integer.parseInt(wsp.getRevision()));
			
			
			JOptionPane.showMessageDialog(null,"The pathway updated");

			
		} catch(Exception e) {
			e.printStackTrace();
			
		}
	}

}
