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
import org.pathvisio.wpclient.panels.BrowsePanel;

public class BrowseDialog extends JDialog {

	public BrowseDialog(PvDesktop desktop, WikiPathwaysClientPlugin plugin) {

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

	public Browse(final PvDesktop desktop, final WikiPathwaysClientPlugin plugin) {
		BrowsePanel p = new BrowsePanel(desktop, plugin);

		add(p);
	}

}
