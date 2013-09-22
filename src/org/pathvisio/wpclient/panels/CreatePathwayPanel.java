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
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.wikipathways.webservice.WSPathwayInfo;
import org.pathvisio.wpclient.WikiPathwaysClientPlugin;
import org.wikipathways.client.WikiPathwaysClient;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class CreatePathwayPanel extends JPanel implements ActionListener {
	LoginPanel p;
	JDialog d,d2;
	
	static WikiPathwaysClient client;
	private JTextArea description = new JTextArea(2, 2);
	private PvDesktop desktop;
	private String Description="";

	public CreatePathwayPanel(PvDesktop desktop) {
		this.desktop = desktop;
		if (LoginPanel.Username.equals("") || LoginPanel.Password.equals("")) {
			showLoginPanel();
		}
		if (!(LoginPanel.Username.equals("") && LoginPanel.Password.equals("")) ){
			showDescriptionPanel();
		}

	}

	private void showDescriptionPanel() {
		FormLayout layout = new FormLayout(
				"7dlu,150px,fill:pref,150px",
				"pref, 2dlu, pref");
		CellConstraints cc = new CellConstraints();
		descriptionPanel dp = new descriptionPanel();
		d2 = new JDialog(desktop.getFrame(), "WikiPathways", false);
		JButton submit = new JButton("create");

		submit.setActionCommand("Create");
		submit.addActionListener(this);
		d2.setLayout(layout);
	
		d2.add(dp, cc.xyw(2, 1,3));

		JPanel p = new JPanel();

		p.add(submit);
		d2.add(p, cc.xy(3,3));

		d2.pack();
		d2.setVisible(true);
		d2.setResizable(false);
		d2.setLocationRelativeTo(desktop.getSwingEngine().getFrame());
		d2.setVisible(true);

	}

	private class descriptionPanel extends JPanel {
		public descriptionPanel() {
			super();
			setLayout(new GridLayout(2, 2));
			add(new JLabel("Description for Pathway:"));
			add(new JScrollPane(description));

		}
	}

	private void showLoginPanel() {

		p = new LoginPanel(desktop);
		d = new JDialog(desktop.getFrame(), "WikiPathways Login", false);
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
		d.setLocationRelativeTo(desktop.getSwingEngine().getFrame());
		d.setVisible(true);

	}

	public void createPathway() {

	
			
			if (client != null) {
				try {
					Pathway pathway = desktop
					.getSwingEngine().getEngine().getActivePathway();
						
					pathway.getMappInfo().addComment(Description, "WP-Client");				
				
				
								WSPathwayInfo l = client.createPathway(pathway);
								client.saveCurationTag(l.getId(), "Curation:UnderConstruction", "curation tag UnderConstruction added by WikiPathways Client Plugin",Integer.parseInt(l.getRevision()));
									JOptionPane.showMessageDialog(null,
							"The Pathway " + l.getId() + " has been Uploaded. \n With Curation Tag : Under Construction. \n Please Update the Curation Tag.");
								WikiPathwaysClientPlugin.revisionno =l.getRevision();
								WikiPathwaysClientPlugin.pathwayid = l.getId();

				} catch (Exception e) {
					JOptionPane.showMessageDialog(null,
							"Error While creating a pathway", "ERROR",
							JOptionPane.ERROR_MESSAGE);

				}
			}
		

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if ("Login".equals(e.getActionCommand())) {
			d.dispose();
			try {
				client = p.login();
			} catch (RemoteException e1) {
				
				e1.printStackTrace();
			} catch (MalformedURLException e1) {
				
				e1.printStackTrace();
			} catch (ServiceException e1) {
				
				e1.printStackTrace();
			}
			if(LoginPanel.loggedin)
			showDescriptionPanel();

		}
		if ("Create".equals(e.getActionCommand())) {
			Description= description.getText();
		
			createPathway();
			d2.dispose();
			

		}
	}

}
