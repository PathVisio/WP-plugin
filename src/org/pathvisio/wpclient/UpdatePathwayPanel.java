package org.pathvisio.wpclient;

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
import javax.swing.JTextField;
import javax.xml.rpc.ServiceException;

import org.pathvisio.core.model.Pathway;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.wikipathways.webservice.WSPathwayInfo;
import org.wikipathways.client.WikiPathwaysClient;

public class UpdatePathwayPanel extends JPanel implements ActionListener {
	LoginPanel p;
	JDialog d,d2;
	static WikiPathwaysClient client;
	private JTextField description = new JTextField(30);
	private PvDesktop desktop;
	private WikiPathwaysClientPlugin plugin;
	private String Description="";

	public UpdatePathwayPanel(PvDesktop desktop, WikiPathwaysClientPlugin plugin) {
		this.desktop = desktop;
		this.plugin = plugin;
		if (LoginPanel.Username.equals("") || LoginPanel.Password.equals("")) {
			showLoginPanel();
		}
		if (!(LoginPanel.Username.equals("") && LoginPanel.Password.equals("")) ){
			showDescriptionPanel();
		}

	}

	private void showDescriptionPanel() {
		descriptionPanel dp = new descriptionPanel();
		d2 = new JDialog(desktop.getFrame(), "wikipathways", false);
		JButton submit = new JButton("Update");

		submit.setActionCommand("Update");
		submit.addActionListener(this);
		d2.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		d2.add(dp, c);

		c.weighty = 0.0;
		c.weightx = 0.5;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = GridBagConstraints.HORIZONTAL;
		JPanel p = new JPanel();

		p.add(submit);
		d2.add(p, c);

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
			add(new JLabel("Description"));
			add(description);

		}
	}

	private void showLoginPanel() {

		p = new LoginPanel(desktop, plugin);
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

	public void UpdatePathway() {

	
			
			if (client != null) {
				try {
					Pathway pathway = desktop
					.getSwingEngine().getEngine().getActivePathway();
						
				
					WSPathwayInfo wsPathwayInfo=client.getPathwayInfo(WikiPathwaysClientPlugin.pathwayid);
				
				String newrevision=wsPathwayInfo.getRevision();
				
				if(WikiPathwaysClientPlugin.revisionno.equals(newrevision))
				{
					
				client.updatePathway(WikiPathwaysClientPlugin.pathwayid, pathway, description.getText() +System.currentTimeMillis(),Integer.parseInt(WikiPathwaysClientPlugin.revisionno));
					JOptionPane.showMessageDialog(null,
							"The pathway is updated");
				}

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
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ServiceException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(LoginPanel.loggedin)
			showDescriptionPanel();

		}
		if ("Update".equals(e.getActionCommand())) {
			Description= description.getText();
			UpdatePathway();
			d2.dispose();
			

		}
	}

}
