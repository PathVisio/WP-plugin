package org.tno.wpclient;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.pathvisio.core.debug.Logger;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class LoginPanel extends JPanel{

  private WikiPathwaysClientPlugin plugin;

	public LoginPanel(WikiPathwaysClientPlugin plugin) {
		this.plugin = plugin;

		setLayout(new BorderLayout());

		Action loginAction = new AbstractAction("Login")
		{
			private JTextField username;
			private JTextField password;

			public void actionPerformed(ActionEvent e)
			{
				try
				{
					
				}
				catch (Exception ex) 
				{
					JOptionPane.showMessageDialog(LoginPanel.this,ex.getMessage(), "Error",JOptionPane.ERROR_MESSAGE);
					Logger.log.error("Error searching WikiPathways", ex);
				}
				
				username = new JTextField();
				password = new JTextField();
				JPanel loginOptBox = new JPanel();
				FormLayout layout = new FormLayout("p,3dlu,150px,3dlu,fill:pref:grow,3dlu,fill:pref:grow,3dlu","p, pref, p,  pref, p, 2dlu,2dlu");
				CellConstraints cc = new CellConstraints();
				loginOptBox.setLayout(layout);
				loginOptBox.setBorder(BorderFactory.createTitledBorder(SearchPanel.etch,"UserCredentials"));
				loginOptBox.add(new JLabel("Username"), cc.xy(1, 1));
				loginOptBox.add(username, cc.xy(3, 1));
				loginOptBox.add(new JLabel("Password"), cc.xy(1, 2));
				loginOptBox.add(password, cc.xy(3, 2));
				loginOptBox.add(new JButton("Submit"), cc.xy(4, 4));;
				
			}
		};
	}

}
