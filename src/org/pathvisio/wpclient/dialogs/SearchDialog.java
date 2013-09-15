package org.pathvisio.wpclient.dialogs;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;

import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.wpclient.WikiPathwaysClientPlugin;
import org.pathvisio.wpclient.panels.KeywordSearchPanel;
import org.pathvisio.wpclient.panels.LiteratureSearchPanel;
import org.pathvisio.wpclient.panels.PathwaySearchPanel;
import org.pathvisio.wpclient.panels.XrefSearchPanel;

public class SearchDialog extends JDialog {
	public SearchDialog(PvDesktop desktop,WikiPathwaysClientPlugin plugin)
	{
	Search p = new Search(plugin);
	final CardLayout cards = new CardLayout();
	JDialog d = new JDialog(desktop.getFrame(), "Search WikiPathways",false);
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
 *Load Search Dialog with Search and AdvancedSearch in the TabbedPane
 */
class Search extends JPanel
{
	JTabbedPane searchTabbedPane;
	 
	public Search(final WikiPathwaysClientPlugin plugin)
	{
		KeywordSearchPanel p = new KeywordSearchPanel(plugin);
		PathwaySearchPanel a = new PathwaySearchPanel(plugin);
		LiteratureSearchPanel r = new LiteratureSearchPanel(plugin);
		XrefSearchPanel sp = new XrefSearchPanel(plugin);
		
		searchTabbedPane = new JTabbedPane();
		searchTabbedPane.addTab("KeyWord Search", p);
		searchTabbedPane.addTab("Pathway Search", a);
		searchTabbedPane.addTab("Search By Identifier", sp);
		searchTabbedPane.addTab("References", r);
		
		add(searchTabbedPane);
	}
	

}