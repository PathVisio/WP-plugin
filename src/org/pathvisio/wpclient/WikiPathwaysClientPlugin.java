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

package org.pathvisio.wpclient;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.xml.rpc.ServiceException;

import org.bridgedb.DataSource;
import org.bridgedb.Xref;

import org.pathvisio.core.preferences.GlobalPreference;
import org.pathvisio.core.Engine;
import org.pathvisio.core.debug.Logger;
import org.pathvisio.core.model.ConverterException;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.core.model.PathwayElement;
import org.pathvisio.core.util.ProgressKeeper;
import org.pathvisio.core.view.GeneProduct;
import org.pathvisio.core.view.Graphics;
import org.pathvisio.core.view.VPathway;
import org.pathvisio.core.view.VPathwayElement;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.desktop.plugin.Plugin;
import org.pathvisio.gui.PathwayElementMenuListener.PathwayElementMenuHook;
import org.pathvisio.gui.ProgressDialog;
import org.pathvisio.wikipathways.webservice.WSPathway;
import org.pathvisio.wikipathways.webservice.WSSearchResult;
import org.wikipathways.client.WikiPathwaysClient;

/**
 * Plugin that provides a WikiPathways client for PathVisio. Enables users to
 * open pathways directly from DataNodes annotated to a pathway, using the
 * right-click menu.
 * 
 * This plugin also includes a dialog to search, advanced search browse and load pathways from
 * WikiPathways (like in the Cytoscape GPML plugin).
 * 
 * @author Thomas Kelder, Sravanthi Sinha
 * @version 1.0
 */
public class WikiPathwaysClientPlugin implements Plugin 
{
	public static Border etch = BorderFactory.createEtchedBorder();
	Map<String, WikiPathwaysClient> clients = new HashMap<String, WikiPathwaysClient>();
	PvDesktop desktop;
	File tmpDir = new File(GlobalPreference.getApplicationDir(),"wpclient-cache");
	private JMenu uploadMenu, wikipathwaysMenu;

	@Override
	public void init(PvDesktop desktop)
	{
		try
		{
			this.desktop = desktop;
			tmpDir.mkdirs();
			Logger.log.info ("Initializing WikiPathways Client plugin");
			loadClients();
			registerActions();
		
			new WikipathwaysPluginManagerAction(desktop);
			this.desktop = desktop;
		} 
		catch (Exception e) 
		{
			Logger.log.error("Error while initializing WikiPathways client", e);
			JOptionPane.showMessageDialog(desktop.getSwingEngine().getApplicationPanel(), e.getMessage(), "Error",JOptionPane.ERROR_MESSAGE);
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
			//preparing menus and submenus 
			wikipathwaysMenu = new JMenu("Wikipathways");
			JMenuItem searchMenu = new JMenuItem("Search");
			JMenuItem browseMenu = new JMenuItem("Browse");

			//preparing actions for menus and submenus 
			SearchAction searchAction = new SearchAction();
			BrowseAction browseAction = new BrowseAction();

			searchMenu.addActionListener(searchAction);
			browseMenu.addActionListener(browseAction);

			uploadMenu = new JMenu("Upload");

			JMenuItem createMenu = new JMenuItem("Create Pathway");
			JMenuItem updateMenu = new JMenuItem("Update Pathway");

			CreateAction createAction = new CreateAction();
			UpdateAction updateAction = new UpdateAction();

			createMenu.addActionListener(createAction);
			updateMenu.addActionListener(updateAction);

			uploadMenu.add(createMenu);
			uploadMenu.add(updateMenu);

			wikipathwaysMenu.add(searchMenu);
			wikipathwaysMenu.add(browseMenu);
			wikipathwaysMenu.add(uploadMenu);

			desktop.registerSubMenu("Plugins", wikipathwaysMenu);

		}

	}

	private void loadClients() throws MalformedURLException, ServiceException 
	{

		int i = 0;

		while (true)
		{
			String clientStr = System.getProperty("org.tno.wpclient." + i);

			if (clientStr == null) // In case we're running from webstart, try
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

	/**
	 * Register actions to provide option to open a pathway From Xref on right click  
	 */
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

	private WikiPathwaysClient findRegisteredClient(String url) 
	{
		for (String clientStr : clients.keySet()) 
		{/*
			if (isSameServer(clientStr, url))
			{
				return clients.get(clientStr);
			}*/
			return clients.get(clientStr);
		}
		return null;
	}

	
	protected void openPathwayWithProgress(final WikiPathwaysClient client,final String id, final int rev, final File tmpDir)	throws InterruptedException, ExecutionException 
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
				}
				catch (Exception e) 
				{
					Logger.log.error("The Pathway is not found", e);
					JOptionPane.showMessageDialog(null,"The Pathway is not found", "ERROR",JOptionPane.ERROR_MESSAGE);
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

	protected void openPathwayWithProgress(final WikiPathwaysClient client,final String id, final int rev, final File tmpDir,final Xref[] xrefs)	throws InterruptedException, ExecutionException 
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
					openPathway(client, id, rev, tmpDir,xrefs);
				}
				catch (Exception e) 
				{
					Logger.log.error("The Pathway is not found", e);
					JOptionPane.showMessageDialog(null,"The Pathway is not found", "ERROR",JOptionPane.ERROR_MESSAGE);
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
	/**
	 *Load Pathway into PathVisio on selection of pathway from list provided by any Search/ Browse Dialog.
	 */
	protected void openPathway(WikiPathwaysClient client, String id, int rev, File tmpDir)throws RemoteException, ConverterException 
	{
		WSPathway wsp = client.getPathway(id, rev);
		Pathway p = WikiPathwaysClient.toPathway(wsp);
		File tmp = new File(tmpDir, wsp.getId() + ".r" + wsp.getRevision()+ ".gpml");
		p.writeToXml(tmp, true);

		Engine engine = desktop.getSwingEngine().getEngine();
		engine.setWrapper(desktop.getSwingEngine().createWrapper());
		engine.openPathway(tmp);
		
	
		
		
	}
	/**
	 *Load Pathway into PathVisio on selection of pathway from list provided by any Search/ Browse Dialog.
	 */
	protected void openPathway(WikiPathwaysClient client, String id, int rev, File tmpDir,Xref[] xrefs)throws RemoteException, ConverterException 
	{
		WSPathway wsp = client.getPathway(id, rev);
		Pathway p = WikiPathwaysClient.toPathway(wsp);
		File tmp = new File(tmpDir, wsp.getId() + ".r" + wsp.getRevision()+ ".gpml");
		p.writeToXml(tmp, true);

		Engine engine = desktop.getSwingEngine().getEngine();
		engine.setWrapper(desktop.getSwingEngine().createWrapper());
		engine.openPathway(tmp);
		
	
		highlightResults(xrefs);
		
	}
	private void highlightResults(Xref[] xrefs) {
		Rectangle2D interestingRect = null;
		Engine engine = desktop.getSwingEngine().getEngine();
		VPathway vpy = engine.getActiveVPathway();
		for (VPathwayElement velt : vpy.getDrawingObjects())
		{
			if (velt instanceof GeneProduct)
			{
				GeneProduct gp = (GeneProduct)velt;
				for (Xref xref: xrefs)
				{
				
					if (xref.equals(gp.getPathwayElement().getXref()))
					{
						gp.highlight(Color.YELLOW);
						if (interestingRect == null)
						{
							interestingRect = gp.getVBounds();
						}
						break;
					}
				}
			}
		}
		if (interestingRect != null)
			vpy.getWrapper().scrollTo (interestingRect.getBounds());
	}
	public static String shortClientName(String clientName) 
	{
		Pattern pattern = Pattern.compile("http://(.*?)/");
		Matcher matcher = pattern.matcher(clientName);
		
		if (matcher.find())
		{
			clientName = matcher.group(1);
		}
		
		return clientName;
	}

	static boolean isSameServer(String clientStr, String url) 
	{
		return url.toLowerCase().startsWith(clientStr.toLowerCase().replace("wpi/webservice/webservice.php?wsdl", ""));
	}

	@Override
	public void done() 
	{
		desktop.unregisterSubMenu("Plugins", wikipathwaysMenu);
	}

	protected void openPathwayXrefWithProgress(final WikiPathwaysClient client,final Xref x, final int rev, final File tmpDir) throws InterruptedException, ExecutionException
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
					openPathwayXref(client, x, rev, tmpDir);
				}
				catch (Exception e) 
				{
					Logger.log.error("The Pathway is not found", e);
					JOptionPane.showMessageDialog(null,"The Pathway is not found", "ERROR",JOptionPane.ERROR_MESSAGE);
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
	
	protected void openPathwayXref(WikiPathwaysClient client, Xref x,int rev, File tmpDir)
	{
		
		WSSearchResult[] wsp;
		try
		{
			wsp = client.findPathwaysByXref(x);
		
		
			PathwayPanel p = new PathwayPanel(WikiPathwaysClientPlugin.this,wsp,tmpDir);
			JDialog d = new JDialog(desktop.getFrame(), "Related Pathways from WikiPathways",false);

			d.getContentPane().add(p);
			d.pack();
			d.setVisible(true);
			d.setResizable(false);
			d.setLocationRelativeTo(desktop.getSwingEngine().getFrame());
			d.setVisible(true);
		
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	/**
	 *Load Search Dialog with Search and AdvancedSearch in the TabbedPane
	 */
	private class Search extends JPanel
	{
		JTabbedPane searchTabbedPane;

		public Search(final WikiPathwaysClientPlugin plugin)
		{
			SearchPanel p = new SearchPanel(plugin);
			PathwaySearchPanel a = new PathwaySearchPanel(plugin);
			ReferenceSearchPanel r = new ReferenceSearchPanel(plugin);
			SearchByIdentifierPanel sp = new SearchByIdentifierPanel(plugin);
			searchTabbedPane = new JTabbedPane();
			searchTabbedPane.addTab("KeyWord Search", p);
			searchTabbedPane.addTab("Pathway Search", a);
			searchTabbedPane.addTab("Search By Identifier", sp);
			searchTabbedPane.addTab("References", r);
			
			add(searchTabbedPane);
		}

	}
	
	/**
	 *Load Search Dialog with Search and AdvancedSearch in the TabbedPane
	 */
	private class Browse extends JPanel
	{
		JTabbedPane searchTabbedPane;

		public Browse(final WikiPathwaysClientPlugin plugin)
		{
			BrowsePanel p = new BrowsePanel(plugin);
			
			OntologyBrowsePanel a = new OntologyBrowsePanel(plugin);
			searchTabbedPane = new JTabbedPane();
			searchTabbedPane.addTab("Browse", p);
			searchTabbedPane.addTab("Ontology Search", a);
			add(searchTabbedPane);
		}

	}
	
	
	/**
	 * Search menu action in the WikiPathways menu
	 */
	private class SearchAction extends AbstractAction
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

			Search p = new Search(WikiPathwaysClientPlugin.this);
			JDialog d = new JDialog(desktop.getFrame(), "Search WikiPathways",false);

			d.getContentPane().add(p);
			d.pack();
			d.setVisible(true);
			d.setResizable(false);
			//loading dialog at the centre of the frame
			d.setLocationRelativeTo(desktop.getSwingEngine().getFrame());
			d.setVisible(true);
		}
	}


	/**
	 * Browse menu action in the WikiPathways menu
	 */
	private class BrowseAction extends AbstractAction
	{

		public BrowseAction() 
		{
			putValue(NAME, "Browse");
			putValue(SHORT_DESCRIPTION, "Browse pathways in Wikipathways");
		}

		public void actionPerformed(ActionEvent e) 
		{
			Browse p = new Browse(WikiPathwaysClientPlugin.this);
			JDialog d = new JDialog(desktop.getFrame(), "Browse WikiPathways",false);

			d.getContentPane().add(p);
			d.pack();
			d.setVisible(true);
			
			d.setLocationRelativeTo(desktop.getSwingEngine().getFrame());
			d.setVisible(true);
		}
	}

	private class UploadAction extends AbstractAction
	{

		public void actionPerformed(ActionEvent e)
		{
			// needs to be implemented
		}
	}

	private class CreateAction extends AbstractAction 
	{

		public void actionPerformed(ActionEvent e) 
		{
			LoginPanel p = new LoginPanel();
			JDialog d = new JDialog(desktop.getFrame(), "WikiPathways Login",false);

			d.getContentPane().add(p);
			d.pack();
			d.setVisible(true);
			d.setResizable(false);
			d.setLocationRelativeTo(desktop.getSwingEngine().getFrame());
			d.setVisible(true);
		}
	}

	private class UpdateAction extends AbstractAction 
	{

		public void actionPerformed(ActionEvent e) 
		{
			// needs to be implemented
		}
	}
}
