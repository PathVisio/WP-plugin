package org.tno.wpclient;

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingWorker;
import javax.xml.rpc.ServiceException;

import org.bridgedb.DataSource;
import org.pathvisio.core.Engine;
import org.pathvisio.core.debug.Logger;
import org.pathvisio.core.model.ConverterException;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.core.model.PathwayElement;
import org.pathvisio.core.preferences.GlobalPreference;
import org.pathvisio.core.util.ProgressKeeper;
import org.pathvisio.core.view.Graphics;
import org.pathvisio.core.view.VPathwayElement;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.desktop.plugin.Plugin;
import org.pathvisio.gui.PathwayElementMenuListener.PathwayElementMenuHook;
import org.pathvisio.gui.ProgressDialog;
import org.pathvisio.wikipathways.webservice.WSPathway;
import org.wikipathways.client.WikiPathwaysClient;

/**
 * Plugin that provides a WikiPathways client for PathVisio. Enables users to
 * open pathways directly from DataNodes annotated to a pathway, using the
 * right-click menu.
 * 
 * Available WikiPathways instances can be specified by system properties, e.g.
 * 
 * java ...
 * -Dorg.tno.wpclient.0=http://www.wikipathways.org/wpi/webservice/webservice
 * .php
 * -Dorg.tno.wpclient.1=http://mylocalinstance.com/wpi/webservice/webservice.php
 * 
 * This plugin also includes a dialog to search and load pathways from
 * WikiPathways (like in the Cytoscape GPML plugin).
 * 
 * @author thomas , sravanthi
 */
public class WikiPathwaysClientPlugin implements Plugin 
{
	Map<String, WikiPathwaysClient> clients = new HashMap<String, WikiPathwaysClient>();
	PvDesktop desktop;
	File tmpDir = new File(GlobalPreference.getApplicationDir(),"wpclient-cache");
	private JMenu UploadMenu,WikipathwaysMenu;

	@Override
	public void init(PvDesktop desktop)
	{
		try
		{
			this.desktop = desktop;
			tmpDir.mkdirs();
			loadClients();
			registerActions();
			// registerMenuOptions(); removed
			// register our action in the "Wikipathways" menu.
			new WikipathwaysPluginManagerAction(desktop);
			this.desktop = desktop;

		} 
		catch (Exception e) {
			Logger.log.error("Error while initializing WikiPathways client", e);
			JOptionPane.showMessageDialog(desktop.getSwingEngine()
					.getApplicationPanel(), e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public Map<String, WikiPathwaysClient> getClients()
	{
		return clients;
	}

	public File getTmpDir()
	{
		return tmpDir;
	}

	private class WikipathwaysPluginManagerAction 
	{

	
	
		public WikipathwaysPluginManagerAction(PvDesktop desktop)
		{
			WikipathwaysMenu = new JMenu("Wikipathways");
			JMenuItem search = new JMenuItem("Search");
			SearchAction searchAction = new SearchAction();
			BrowseAction browseAction = new BrowseAction();
			
			search.addActionListener(searchAction);

			JMenuItem browse = new JMenuItem("Browse");
			browse.addActionListener(browseAction);
			
			JMenuItem create = new JMenuItem("Create Pathway");
			JMenuItem update = new JMenuItem("Update Pathway");
			
			CreateAction createAction = new CreateAction();
			UpdateAction updateAction = new UpdateAction();
			
			create.addActionListener(createAction);		
			update.addActionListener(updateAction);
			//UploadMenu.add(create);
			//UploadMenu.add(update);
			
			WikipathwaysMenu.add(search);
			WikipathwaysMenu.add(browse);
		//	WikipathwaysMenu.add(UploadMenu);
			

			//desktop.registerMenuAction("Wikipathways", searchAction);
			//desktop.registerMenuAction("Wikipathways", browseAction);
			desktop.registerSubMenu("Plugins", WikipathwaysMenu);
			
		}

	}

	public class SearchAction extends AbstractAction
	{
		private String IMG_SEARCH = "resources/search.gif";
		URL url = WikiPathwaysClientPlugin.class.getClassLoader().getResource(IMG_SEARCH);

		public SearchAction() 
		{
			putValue(NAME, "Search");
			putValue(SMALL_ICON, new ImageIcon(url));
			putValue(SHORT_DESCRIPTION, "Search pathways in Wikipathways");
		}

		public void actionPerformed(ActionEvent e)
		{
			SearchPanel p = new SearchPanel(WikiPathwaysClientPlugin.this);
			JDialog d = new JDialog(desktop.getFrame(), "Search WikiPathways",false);
			
			d.getContentPane().add(p);
			d.pack();
			d.setVisible(true);
		}
	}

	public class BrowseAction extends AbstractAction
	{

		public BrowseAction()
		{
			putValue(NAME, "Browse");
			putValue(SHORT_DESCRIPTION, "Browse pathways in Wikipathways");
		}

		public void actionPerformed(ActionEvent e)
		{
			BrowsePanel p = new BrowsePanel(WikiPathwaysClientPlugin.this);
			JDialog d = new JDialog(desktop.getFrame(), "Browse WikiPathways",false);
			
			d.getContentPane().add(p);
			d.pack();
			d.setVisible(true);
		}
	}

	public class UploadAction extends AbstractAction
	{

		public void actionPerformed(ActionEvent e) 
		{
			// needs to be implemented
		}
	}

	public class CreateAction extends AbstractAction
	{

		public void actionPerformed(ActionEvent e) 
		{
			// needs to be implemented
		}
	}

	public class UpdateAction extends AbstractAction
	{

		public void actionPerformed(ActionEvent e)
		{
			// needs to be implemented
		}
	}

	/*
	 * private void registerMenuOptions() 
	 * {
	 *  Action menuAction = new AbstractAction("Search")
	 *   {
	 *    public void actionPerformed(ActionEvent e)
	 *     {
	 *		 SearchPanel p = new SearchPanel(WikiPathwaysClientPlugin.this);
	 *		 JDialog d = new JDialog(desktop.getFrame(), "Search WikiPathways", false);
	 * 		 d.getContentPane().add(p); d.pack(); d.setVisible(true);
	 * 		}
	 *   };
	 *  desktop.registerMenuAction("Wikipathways", menuAction); 
	 *  }
	 */
	private void loadClients() throws MalformedURLException, ServiceException 
	{
		
		int i = 0;
		
		while (true)
		{
			String clientStr = System.getProperty("org.tno.wpclient." + i);
			
			if (clientStr == null) // In case we're running from webstart, try safe properties
			{ 										 
				clientStr = System.getProperty("javaws.org.tno.wpclient." + i);
			}
			if (clientStr == null)
			{
				break;
			}
			
			WikiPathwaysClient client = new WikiPathwaysClient(new URL(clientStr));
			clients.put(clientStr, client);
			i++;
		}
		
		if (i == 0) // No clients specified, use default wikipathways.org
		{ 
			clients.put("http://www.wikipathways.org/wpi/webservice/webservice.php?wsdl",new WikiPathwaysClient());
		}
	}

	private void registerActions() 
	{
		desktop.addPathwayElementMenuHook(new PathwayElementMenuHook()
		{			
			public void pathwayElementMenuHook(VPathwayElement e,JPopupMenu menu)
			{
				if (!(e instanceof Graphics))
				{
					return;
				}
				
				PathwayElement pe = ((Graphics) e).getPathwayElement();
				
				if (pe.getXref() == null)
				{
					return;
				}
				
				DataSource ds = pe.getXref().getDataSource();
				
				if (ds == null)
				{
					return;
				}
					
				WikiPathwaysClient client = findRegisteredClient(ds.getMainUrl());
				
				if (client == null)
				{
					return;
				}
				
				OpenPathwayFromXrefAction action = new OpenPathwayFromXrefAction(WikiPathwaysClientPlugin.this, pe);
				action.setClient(client);
				menu.add(action);
			}
		});
	}

	WikiPathwaysClient findRegisteredClient(String url) 
	{
		for (String clientStr : clients.keySet())
		{
			if (isSameServer(clientStr, url))
			{
				return clients.get(clientStr);
			}
		}
		return null;
	}

	void openPathwayWithProgress(final WikiPathwaysClient client,final String id, final int rev, final File tmpDir) throws InterruptedException, ExecutionException 
	
	{
		final ProgressKeeper pk = new ProgressKeeper();
		final ProgressDialog d = new ProgressDialog(JOptionPane.getFrameForComponent(desktop.getSwingEngine().getApplicationPanel()), "", pk, false, true);

		SwingWorker<Boolean, Void> sw = new SwingWorker<Boolean, Void>()
		{
			protected Boolean doInBackground() throws Exception 
			{
				pk.setTaskName("Opening pathway");
				try
				{
					openPathway(client, id, rev, tmpDir);
				}catch (Exception e)
				{
					Logger.log.error("The Pathway is not found", e);
					JOptionPane.showMessageDialog(null,"The Pathway is not found", "ERROR", JOptionPane.ERROR_MESSAGE);
				}
				finally 
				{
					pk.finished();
				}
				return true;
			}
		};

		sw.execute();
		d.setVisible(true);
		sw.get();
	}

	void openPathway(WikiPathwaysClient client, String id, int rev, File tmpDir)throws RemoteException, ConverterException 
	{
		WSPathway wsp = client.getPathway(id, rev);		
		Pathway p = WikiPathwaysClient.toPathway(wsp);
		File tmp = new File(tmpDir, wsp.getId() + ".r" + wsp.getRevision()+ ".gpml");
		p.writeToXml(tmp, true);

		Engine engine = desktop.getSwingEngine().getEngine();
		engine.setWrapper(desktop.getSwingEngine().createWrapper());
		engine.openPathway(tmp);
		
	}

	static boolean isSameServer(String clientStr, String url)
	{
		return url.toLowerCase().startsWith(clientStr.toLowerCase().replace("wpi/webservice/webservice.php?wsdl", ""));
	}

	@Override
	public void done()
	{
		desktop = null;
	}

}
