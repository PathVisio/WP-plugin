package org.pathvisio.wpclient.actions;

import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JDialog;

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

		new SearchDialog(desktop, plugin);
		
		
	}
}