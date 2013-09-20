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
package org.pathvisio.wpclient.actions;

import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.xml.rpc.ServiceException;

import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.wpclient.WikiPathwaysClientPlugin;
import org.pathvisio.wpclient.dialogs.SearchDialog;



/**
 * Search menu action in the WikiPathways menu
 */
public class SearchAction extends AbstractAction
{
	private String IMG_SEARCH = "resources/search.gif";
	URL url = WikiPathwaysClientPlugin.class.getClassLoader().getResource(IMG_SEARCH);
	PvDesktop desktop;
	private WikiPathwaysClientPlugin plugin;

	public SearchAction(PvDesktop desktop, WikiPathwaysClientPlugin plugin) 
	{
		this.desktop=desktop;
		this.plugin=plugin;
		putValue(NAME, "Search");
		putValue(SMALL_ICON, new ImageIcon(url));
		putValue(SHORT_DESCRIPTION, "Search pathways in Wikipathways");
	}

	public void actionPerformed(ActionEvent e) 
	{

		try {
			new SearchDialog(desktop, plugin);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ServiceException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
	}
}