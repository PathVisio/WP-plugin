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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;
import javax.xml.rpc.ServiceException;

import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.wpclient.FailedConnectionException;
import org.pathvisio.wpclient.WikiPathwaysClientPlugin;
import org.pathvisio.wpclient.panels.BrowsePanel;

public class BrowseDialog extends JDialog {

	public BrowseDialog(PvDesktop desktop, WikiPathwaysClientPlugin plugin) throws RemoteException, MalformedURLException, ServiceException, FailedConnectionException {

		Browse p = new Browse(desktop, plugin);
		final CardLayout cards = new CardLayout();
		JDialog d = new JDialog(desktop.getFrame(), "Browse WikiPathways",
				false);
	
		
		d.setLayout(new BorderLayout());
		Border padBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		p.setLayout(cards);
		p.setBorder(padBorder);	
		
		JScrollPane pnlScroll = new JScrollPane(p);	
		d.add(pnlScroll);
		d.pack();	
		//loading dialog at the centre of the frame
		d.setLocationRelativeTo(desktop.getSwingEngine().getFrame());
		d.setVisible(true);
	}

}

/**
 * Load Search Dialog with Search and AdvancedSearch in the TabbedPane
 */
class Browse extends JPanel {
	JTabbedPane searchTabbedPane;

	public Browse(final PvDesktop desktop, final WikiPathwaysClientPlugin plugin) throws RemoteException, MalformedURLException, ServiceException, FailedConnectionException {
		BrowsePanel p = new BrowsePanel(plugin);

		add(p);
	}

}
