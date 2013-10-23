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

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.xml.rpc.ServiceException;

import org.pathvisio.wpclient.FailedConnectionException;
import org.pathvisio.wpclient.WikiPathwaysClientPlugin;
import org.pathvisio.wpclient.panels.BrowsePanel;

public class BrowseDialog {

	private JDialog dialog;
	private Browse browsePanel;
	
	public BrowseDialog(WikiPathwaysClientPlugin plugin) {
		dialog = new JDialog(plugin.getDesktop().getFrame(), "Browse WikiPathways", false);
		
		try {
			browsePanel = new Browse(plugin);
			dialog.setLayout(new BorderLayout());
			Border padBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
			browsePanel.setLayout(new CardLayout());
			browsePanel.setBorder(padBorder);	
			
			JScrollPane pnlScroll = new JScrollPane(browsePanel);	
			dialog.add(pnlScroll);
			dialog.pack();	
			//loading dialog at the centre of the frame
			dialog.setLocationRelativeTo(plugin.getDesktop().getSwingEngine().getFrame());
			dialog.setVisible(true);
			
		} catch (RemoteException e) {
			JOptionPane.showMessageDialog(dialog,
				    "Can not connect to WikiPathways webservice.",
				    "Connection error",
				    JOptionPane.ERROR_MESSAGE);
			dialog.setVisible(false);
		} catch (MalformedURLException e) {
			JOptionPane.showMessageDialog(dialog,
				    "Can not connect to WikiPathways webservice.\nInvalid URL.",
				    "Connection error",
				    JOptionPane.ERROR_MESSAGE);
			dialog.setVisible(false);
		} catch (ServiceException e) {
			JOptionPane.showMessageDialog(dialog,
				    "Can not connect to WikiPathways webservice.",
				    "Connection error",
				    JOptionPane.ERROR_MESSAGE);
			dialog.setVisible(false);
		} catch (FailedConnectionException e) {
			JOptionPane.showMessageDialog(dialog,
				    "Can not connect to WikiPathways webservice.",
				    "Connection error",
				    JOptionPane.ERROR_MESSAGE);
			dialog.setVisible(false);
		}
	}
	
	/**
	 * Load Search Dialog with Search and AdvancedSearch in the TabbedPane
	 */
	private class Browse extends JPanel {
		
		public Browse(WikiPathwaysClientPlugin plugin) throws RemoteException, MalformedURLException, ServiceException, FailedConnectionException {
			BrowsePanel p = new BrowsePanel(plugin);
			add(p);
		}
	}
}


