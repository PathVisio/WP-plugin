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
package org.pathvisio.wpclient.dialogs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.xml.rpc.ServiceException;

import org.pathvisio.core.model.Pathway;
import org.pathvisio.wikipathways.webservice.WSPathwayInfo;
import org.pathvisio.wpclient.FailedConnectionException;
import org.pathvisio.wpclient.WikiPathwaysClientPlugin;
import org.pathvisio.wpclient.panels.LoginPanel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * JPanel 
 * @author martinakutmon
 *
 */
public class UpdatePathwayDialog implements ActionListener {
	LoginPanel p;
	JDialog d,d2;
	private JTextArea description = new JTextArea(2, 2);
	private WikiPathwaysClientPlugin plugin;

	public UpdatePathwayDialog(WikiPathwaysClientPlugin plugin) {
		this.plugin = plugin;
		if (LoginPanel.username.equals("") || LoginPanel.password.equals("")) {
			showLoginPanel();
		}
		if (!(LoginPanel.username.equals("") && LoginPanel.password.equals(""))){
			showDescriptionPanel();
		}
	}

	private void showDescriptionPanel() {
		FormLayout layout = new FormLayout(
				"7dlu,150px,fill:pref,150px",
				"pref, 2dlu, pref");
		CellConstraints cc = new CellConstraints();
		descriptionPanel dp = new descriptionPanel();
		d2 = new JDialog(plugin.getDesktop().getFrame(), "WikiPathways", false);
		JButton submit = new JButton("Update");

		submit.setActionCommand("Update");
		submit.addActionListener(this);
		d2.setLayout(layout);

		d2.add(dp, cc.xyw(2, 1,3));

		JPanel p = new JPanel();

		p.add(submit);
		d2.add(p, cc.xy(3,3));

		d2.pack();
		d2.setVisible(true);
		d2.setResizable(false);
		d2.setLocationRelativeTo(plugin.getDesktop().getSwingEngine().getFrame());
		d2.setVisible(true);
	}

	private class descriptionPanel extends JPanel {
		public descriptionPanel() {
			super();
			setLayout(new GridLayout(2, 2));
			add(new JLabel("Give a description of your changes"));
			add(new JScrollPane(description));
		}
	}

	private void showLoginPanel() {
		p = new LoginPanel(plugin);
		d = new JDialog(plugin.getDesktop().getFrame(), "WikiPathways Login", false);
		JButton submit = new JButton("Login");

		submit.setActionCommand("Login");
		submit.addActionListener(this);
		d.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		d.add(p, c);

		c.weighty = 0.0;
		c.weightx = 0.5;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = GridBagConstraints.HORIZONTAL;
		JPanel p = new JPanel();

		p.add(submit);
		d.add(p, c);

		d.pack();
		d.setVisible(true);
		d.setResizable(false);
		d.setLocationRelativeTo(plugin.getDesktop().getSwingEngine().getFrame());
		d.setVisible(true);
	}

	public void UpdatePathway() throws RemoteException, MalformedURLException, ServiceException, FailedConnectionException {
		plugin.getWpQueries().login(LoginPanel.username,LoginPanel.password);
		try {
			Pathway pathway = plugin.getDesktop().getSwingEngine().getEngine().getActivePathway();
			WSPathwayInfo wsPathwayInfo=plugin.getWpQueries().getPathwayInfo(WikiPathwaysClientPlugin.pathwayid, null);
			String newrevision=wsPathwayInfo.getRevision();
			
			if(WikiPathwaysClientPlugin.revisionno.equals(newrevision)) {
				plugin.getWpQueries().updatePathway(pathway, WikiPathwaysClientPlugin.pathwayid, Integer.parseInt(WikiPathwaysClientPlugin.revisionno), description.getText());
				JOptionPane.showMessageDialog(null,
					"The pathway is updated");
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
				"Error While creating a pathway", "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}		
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if ("Login".equals(e.getActionCommand())) {
			d.dispose();
			try {
				p.login();
			} catch (Exception e1) {
				e1.printStackTrace();
			} 
			
			if(LoginPanel.loggedin)
			showDescriptionPanel();
		}
		
		if ("Update".equals(e.getActionCommand())) {
			try {
				UpdatePathway();
			} catch (Exception e1) {
				e1.printStackTrace();
			} 
			d2.dispose();
		}
	}
}
