package org.pathvisio.wpclient;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
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

import org.pathvisio.core.model.ObjectType;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.core.model.PathwayElement;
import org.pathvisio.core.model.PathwayElement.Comment;
import org.pathvisio.core.model.PropertyType;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.wikipathways.webservice.WSPathwayInfo;
import org.wikipathways.client.WikiPathwaysClient;

import com.sun.org.apache.bcel.internal.generic.NEW;

public class CreatePathwayPanel extends JPanel implements ActionListener {
	LoginPanel p;
	JDialog d,d2;
	static WikiPathwaysClient client;
	private JTextField description = new JTextField(30);
	private PvDesktop desktop;
	private WikiPathwaysClientPlugin plugin;
	private String Description="";

	public CreatePathwayPanel(PvDesktop desktop, WikiPathwaysClientPlugin plugin) {
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
		d2 = new JDialog(desktop.getFrame(), "WikiPathways", false);
		JButton submit = new JButton("create");

		submit.setActionCommand("Create");
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

	public void createPathway() {

	
			
			if (client != null) {
				try {
					Pathway pathway = desktop
					.getSwingEngine().getEngine().getActivePathway();
						
					pathway.getMappInfo().addComment(Description, "WP-Client");
					
				
				
								WSPathwayInfo l = client.createPathway(pathway);
								client.saveCurationTag(l.getId(), "Curation:UnderConstruction", "curation tag UnderConstruction added by WikiPathways Client Plugin",Integer.parseInt(l.getRevision()));
									JOptionPane.showMessageDialog(null,
							"The Pathway " + l.getId() + "has been Uploaded with Curation Tag : under Construction, Please Update the Curation Tag");

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
		if ("Create".equals(e.getActionCommand())) {
			Description= description.getText();
			createPathway();
			d2.dispose();
			

		}
	}

}
