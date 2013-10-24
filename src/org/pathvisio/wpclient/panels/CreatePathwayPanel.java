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
import org.pathvisio.wpclient.WikiPathwaysClientPlugin;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class CreatePathwayPanel extends JPanel implements ActionListener {
	private LoginPanel p;
	private JDialog dialog, descriptionDialog;
	
	private JTextArea description = new JTextArea(2, 2);
	private WikiPathwaysClientPlugin plugin;

	public CreatePathwayPanel(WikiPathwaysClientPlugin plugin) {
		this.plugin = plugin;
		if (LoginPanel.username.equals("") || LoginPanel.password.equals("")) {
			showLoginPanel();
		}
		if (!(LoginPanel.username.equals("") && LoginPanel.password.equals("")) ){
			showDescriptionPanel();
		}
	}

	private void showDescriptionPanel() {
		FormLayout layout = new FormLayout(
				"7dlu,150px,fill:pref,150px",
				"pref, 2dlu, pref");
		CellConstraints cc = new CellConstraints();
		DescriptionPanel dp = new DescriptionPanel();
		descriptionDialog = new JDialog(plugin.getDesktop().getFrame(), "WikiPathways", false);
		JButton submit = new JButton("create");

		submit.setActionCommand("Create");
		submit.addActionListener(this);
		descriptionDialog.setLayout(layout);
	
		descriptionDialog.add(dp, cc.xyw(2, 1,3));

		JPanel p = new JPanel();

		p.add(submit);
		descriptionDialog.add(p, cc.xy(3,3));

		descriptionDialog.pack();
		descriptionDialog.setVisible(true);
		descriptionDialog.setResizable(false);
		descriptionDialog.setLocationRelativeTo(plugin.getDesktop().getFrame());
		descriptionDialog.setVisible(true);
	}

	private class DescriptionPanel extends JPanel {
		public DescriptionPanel() {
			super();
			setLayout(new GridLayout(2, 2));
			add(new JLabel("Description for Pathway:"));
			add(new JScrollPane(description));
		}
	}

	private void showLoginPanel() {
		p = new LoginPanel(plugin);
		dialog = new JDialog(plugin.getDesktop().getFrame(), "WikiPathways Login", false);
		JButton submit = new JButton("Login");

		submit.setActionCommand("Login");
		submit.addActionListener(this);
		dialog.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		dialog.add(p, c);

		c.weighty = 0.0;
		c.weightx = 0.5;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = GridBagConstraints.HORIZONTAL;
		JPanel p = new JPanel();

		p.add(submit);
		dialog.add(p, c);

		dialog.pack();
		dialog.setVisible(true);
	
		dialog.setResizable(false);
		dialog.setLocationRelativeTo(plugin.getDesktop().getSwingEngine().getFrame());
		dialog.setVisible(true);
	}

	public void createPathway() {
		try {
			Pathway pathway = plugin.getDesktop().getSwingEngine().getEngine().getActivePathway();
			WSPathwayInfo l = plugin.getWpQueries().uploadPathway(pathway);
			plugin.getWpQueries().updateCurationTag( "Curation:UnderConstruction", l.getId(), "", Integer.parseInt(l.getRevision()));
			JOptionPane.showMessageDialog(plugin.getDesktop().getFrame(),
					"The Pathway " + l.getId() + " has been Uploaded. \n With Curation Tag : Under Construction. \n Please Update the Curation Tag.");
						WikiPathwaysClientPlugin.revisionno =l.getRevision();
						WikiPathwaysClientPlugin.pathwayid = l.getId();

		} catch (Exception e) {
			JOptionPane.showMessageDialog(plugin.getDesktop().getFrame(),
					"Error While creating a pathway", "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if ("Login".equals(e.getActionCommand())) {
			dialog.dispose();
			try {
				p.login();
				
				if(LoginPanel.loggedin) {
					showDescriptionPanel();
				}
			} catch (RemoteException e1) {
				e1.printStackTrace();
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			} catch (ServiceException e1) {
				e1.printStackTrace();
			}
		} else if ("Create".equals(e.getActionCommand())) {		
			createPathway();
			descriptionDialog.dispose();
		}
	}
}
