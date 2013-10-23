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
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingWorker;
import javax.xml.rpc.ServiceException;

import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.pathvisio.core.ApplicationEvent;
import org.pathvisio.core.Engine;
import org.pathvisio.core.Engine.ApplicationEventListener;
import org.pathvisio.core.debug.Logger;
import org.pathvisio.core.model.ConverterException;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.core.model.PathwayElement;
import org.pathvisio.core.preferences.GlobalPreference;
import org.pathvisio.core.preferences.Preference;
import org.pathvisio.core.preferences.PreferenceManager;
import org.pathvisio.core.util.CommonsFileUtils;
import org.pathvisio.core.util.ProgressKeeper;
import org.pathvisio.core.view.GeneProduct;
import org.pathvisio.core.view.Graphics;
import org.pathvisio.core.view.VPathway;
import org.pathvisio.core.view.VPathwayElement;
import org.pathvisio.core.view.VPathwayEvent;
import org.pathvisio.core.view.VPathwayListener;
import org.pathvisio.desktop.PreferencesDlg;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.desktop.plugin.Plugin;
import org.pathvisio.gui.PathwayElementMenuListener.PathwayElementMenuHook;
import org.pathvisio.gui.ProgressDialog;
import org.pathvisio.wikipathways.webservice.WSPathway;
import org.pathvisio.wikipathways.webservice.WSSearchResult;
import org.pathvisio.wpclient.actions.BrowseAction;
import org.pathvisio.wpclient.actions.OpenPathwayFromXrefAction;
import org.pathvisio.wpclient.actions.SearchAction;
import org.pathvisio.wpclient.actions.UpdateAction;
import org.pathvisio.wpclient.actions.UploadAction;
import org.pathvisio.wpclient.panels.PathwayPanel;
import org.pathvisio.wpclient.utils.FileUtils;
import org.wikipathways.client.WikiPathwaysClient;

/**
 * Plugin that provides a WikiPathways client for PathVisio. Enables users to
 * open pathways directly from DataNodes annotated to a pathway, using the
 * right-click menu.
 * 
 * This plugin also includes a dialog to search, advanced search browse and load
 * pathways from WikiPathways (like in the Cytoscape GPML plugin).
 * 
 * @author Thomas Kelder, Sravanthi Sinha, Martina Kutmon
 * @version 1.0
 */
public class WikiPathwaysClientPlugin implements Plugin, ApplicationEventListener, VPathwayListener {
	
	private PvDesktop desktop;
	private File tmpDir = new File(GlobalPreference.getPluginDir(), "wpclient-cache");
	private JMenu uploadMenu, wikipathwaysMenu;
	
	public static String revisionno = "";
	public static String pathwayid = "";
	
	// menu items will only be enabled when pathway is opened
	private JMenuItem createMenu;
	private JMenuItem updateMenu;

	@Override
	public void init(PvDesktop desktop) {
		try {
			this.desktop = desktop;
			tmpDir.mkdirs();
			Logger.log.info("Initializing WikiPathways Client plugin");

			initPreferences();

			registerActions();

			new WikipathwaysPluginManagerAction(desktop);

			// register a listener to notify when a pathway is opened
			desktop.getSwingEngine().getEngine()
					.addApplicationEventListener(this);

		} catch (Exception e) {
			Logger.log.error("Error while initializing WikiPathways client", e);
			JOptionPane.showMessageDialog(desktop.getSwingEngine()
					.getApplicationPanel(), e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Initializing Preferences.
	 */
	private void initPreferences() {
		PreferencesDlg dlg = desktop.getPreferencesDlg();
		
		dlg.addPanel(
				"WikiPathways Plugin",
				dlg.builder()
						.booleanField(UrlPreference.USE_TESTSITE,
								"Use WikiPathways Test Site to Search, Browse, Upload Pathways")
						.stringField(UrlPreference.TESTSITE_URL, "TestSite:")
						.build()

		); 

	}

	enum UrlPreference implements Preference {

		USE_TESTSITE(Boolean.toString(false)),
		TESTSITE_URL(new String("test2"));
		UrlPreference(String defaultValue) {
			this.defaultValue = defaultValue;
		}

		private String defaultValue;

		public String getDefault() {
			return defaultValue;
		}
	}

	public File getTmpDir() {
		return tmpDir;
	}
	/**
	 * Preparing the Submenu For WikiPathways Menu
	 */

	private class WikipathwaysPluginManagerAction {

		public WikipathwaysPluginManagerAction(PvDesktop desktop) {
			// preparing menus and submenus
			wikipathwaysMenu = new JMenu("WikiPathways");
			JMenuItem searchMenu = new JMenuItem("Search");
			JMenuItem browseMenu = new JMenuItem("Browse");

			// preparing actions for menus and submenus
			SearchAction searchAction = new SearchAction(desktop,
					WikiPathwaysClientPlugin.this);
			BrowseAction browseAction = new BrowseAction(desktop,
					WikiPathwaysClientPlugin.this);

			searchMenu.addActionListener(searchAction);
			browseMenu.addActionListener(browseAction);

			uploadMenu = new JMenu("Upload");

			createMenu = new JMenuItem("Create Pathway");
			updateMenu = new JMenuItem("Update Pathway");

			UploadAction createAction = new UploadAction(desktop);
			UpdateAction updateAction = new UpdateAction(desktop);

			createMenu.addActionListener(createAction);
			updateMenu.addActionListener(updateAction);

			uploadMenu.add(createMenu);
			uploadMenu.add(updateMenu);

			wikipathwaysMenu.add(searchMenu);
			wikipathwaysMenu.add(browseMenu);
			wikipathwaysMenu.add(uploadMenu);

			desktop.registerSubMenu("Plugins", wikipathwaysMenu);
			updateState();
		}

	}

	/**
	 * Checks if a pathway is open or not. If there is no open pathway, the
	 * create and update menus are disabled/enabled.
	 */
	public void updateState() {
		boolean status = (desktop.getSwingEngine().getEngine().hasVPathway());
		createMenu.setEnabled(status);
		updateMenu.setEnabled(status);
	
	}

	private static WikiPathwaysClient client;

	public static WikiPathwaysClient loadClient() throws MalformedURLException,
			ServiceException {
		String testsite=PreferenceManager.getCurrent().get(UrlPreference.TESTSITE_URL);
			// TODO: if preferences get changed - set client to null!!!!
			if (PreferenceManager.getCurrent().getBoolean(
					UrlPreference.USE_TESTSITE)) {
				client = new WikiPathwaysClient(
						new URL(
								"http://"+testsite+".wikipathways.org/wpi/webservice/webservice.php"));
			} else {
				client = new WikiPathwaysClient(
						new URL(
								"http://www.wikipathways.org/wpi/webservice/webservice.php"));
			}

		
		return client;
	}

	/**
	 * Register actions to provide option to open a pathway From Xref on right
	 * click
	 */
	private void registerActions() {
		desktop.addPathwayElementMenuHook(new PathwayElementMenuHook() {
			public void pathwayElementMenuHook(VPathwayElement e,
					JPopupMenu menu) {
				if (!(e instanceof Graphics)) {
					return;
				}

				PathwayElement pe = ((Graphics) e).getPathwayElement();

				if (pe.getXref() == null) {
					return;
				}

				DataSource ds = pe.getXref().getDataSource();

				if (ds == null) {
					return;
				}

				WikiPathwaysClient client;
				try {
					client = loadClient();

					if (client == null) {
						return;
					}

					OpenPathwayFromXrefAction action = new OpenPathwayFromXrefAction(
							WikiPathwaysClientPlugin.this, pe);
					action.setClient(client);
					menu.add(action);
				} catch (MalformedURLException e1) {

					e1.printStackTrace();
				} catch (ServiceException e1) {

					e1.printStackTrace();
				}

			}
		});
	}

	public void openPathwayWithProgress(final WikiPathwaysClient client,
			final String id, final int rev, final File tmpDir)
			throws InterruptedException, ExecutionException {
		final ProgressKeeper pk = new ProgressKeeper();
		final ProgressDialog d = new ProgressDialog(
				JOptionPane.getFrameForComponent(desktop.getSwingEngine()
						.getApplicationPanel()), "", pk, false, true);

		SwingWorker<Boolean, Void> sw = new SwingWorker<Boolean, Void>() {
			protected Boolean doInBackground() throws Exception {
				pk.setTaskName("Opening pathway");
				try {
					openPathway(client, id, rev, tmpDir);
				} catch (Exception e) {
					Logger.log.error("The Pathway is not found", e);
					JOptionPane.showMessageDialog(null,
							"The Pathway is not found", "ERROR",
							JOptionPane.ERROR_MESSAGE);
				} finally {
					pk.finished();
				}
				return true;
			}

			protected void done() {
				if (pk.isCancelled()) {
					pk.finished();
				}
			}
		};

		sw.execute();
		d.setVisible(true);
		sw.get();
	}

	public void openPathwayWithProgress(final WikiPathwaysClient client,
			final String id, final int rev, final File tmpDir,
			final Xref[] xrefs) throws InterruptedException, ExecutionException {
		final ProgressKeeper pk = new ProgressKeeper();
		final ProgressDialog d = new ProgressDialog(
				JOptionPane.getFrameForComponent(desktop.getSwingEngine()
						.getApplicationPanel()), "", pk, false, true);

		SwingWorker<Boolean, Void> sw = new SwingWorker<Boolean, Void>() {
			protected Boolean doInBackground() throws Exception {
				pk.setTaskName("Opening pathway");
				try {
					openPathway(client, id, rev, tmpDir, xrefs);
				} catch (Exception e) {
					Logger.log.error("The Pathway is not found", e);
					JOptionPane.showMessageDialog(null,
							"The Pathway is not found", "ERROR",
							JOptionPane.ERROR_MESSAGE);
				} finally {
					pk.finished();
				}
				return true;
			}

			protected void done() {
				if (pk.isCancelled()) {
					pk.finished();
				}
			}
		};

		sw.execute();
		d.setVisible(true);
		sw.get();
	}

	/**
	 * Load Pathway into PathVisio on selection of pathway from list provided by
	 * any Search/ Browse Dialog.
	 */
	protected void openPathway(WikiPathwaysClient client, String id, int rev,
			File tmpDir) throws RemoteException, ConverterException {
		WSPathway wsp = client.getPathway(id, rev);
		Pathway p = WikiPathwaysClient.toPathway(wsp);
		File tmp = new File(tmpDir, wsp.getId() + ".r" + wsp.getRevision()
				+ ".gpml");
		p.writeToXml(tmp, true);
		revisionno = wsp.getRevision();
		pathwayid = wsp.getId();
		Engine engine = desktop.getSwingEngine().getEngine();
		engine.setWrapper(desktop.getSwingEngine().createWrapper());
		engine.openPathway(tmp);

	}

	/**
	 * Load Pathway into PathVisio on selection of pathway from list provided by
	 * any Search/ Browse Dialog.
	 */
	protected void openPathway(WikiPathwaysClient client, String id, int rev,
			File tmpDir, Xref[] xrefs) throws RemoteException,
			ConverterException {
		WSPathway wsp = client.getPathway(id, rev);
		Pathway p = WikiPathwaysClient.toPathway(wsp);
		File tmp = new File(tmpDir, wsp.getId() + ".r" + wsp.getRevision()
				+ ".gpml");
		p.writeToXml(tmp, true);
		revisionno = wsp.getRevision();
		pathwayid = wsp.getId();
		Engine engine = desktop.getSwingEngine().getEngine();
		engine.setWrapper(desktop.getSwingEngine().createWrapper());
		engine.openPathway(tmp);

		highlightResults(xrefs);

	}
	/**
	 * HighLight the DataNodes With particular Xref	 
	 */
	private void highlightResults(Xref[] xrefs) {
		Rectangle2D interestingRect = null;
		Engine engine = desktop.getSwingEngine().getEngine();
		VPathway vpy = engine.getActiveVPathway();
		for (VPathwayElement velt : vpy.getDrawingObjects()) {
			if (velt instanceof GeneProduct) {
				GeneProduct gp = (GeneProduct) velt;
				for (Xref xref : xrefs) {

					if (xref.equals(gp.getPathwayElement().getXref())) {
						gp.highlight(Color.YELLOW);
						if (interestingRect == null) {
							interestingRect = gp.getVBounds();
						}
						break;
					}
				}
			}
		}
		if (interestingRect != null)
			vpy.getWrapper().scrollTo(interestingRect.getBounds());
	}

	public static String shortClientName(String clientName) {
		Pattern pattern = Pattern.compile("http://(.*?)/");
		Matcher matcher = pattern.matcher(clientName);

		if (matcher.find()) {
			clientName = matcher.group(1);
		}

		return clientName;
	}

	static boolean isSameServer(String clientStr, String url) {
		return url.toLowerCase().startsWith(
				clientStr.toLowerCase().replace(
						"wpi/webservice/webservice.php?wsdl", ""));
	}

	@Override
	public void done() {
		desktop.unregisterSubMenu("Plugins", wikipathwaysMenu);
		if(tmpDir.exists()) {
			FileUtils.deleteDirectory(tmpDir);
		}
	}
	
	public void openPathwayXrefWithProgress(final WikiPathwaysClient client,
			final Xref x, final int rev, final File tmpDir)
			throws InterruptedException, ExecutionException {

		final ProgressKeeper pk = new ProgressKeeper();
		final ProgressDialog d = new ProgressDialog(
				JOptionPane.getFrameForComponent(desktop.getSwingEngine()
						.getApplicationPanel()), "", pk, false, true);

		SwingWorker<Boolean, Void> sw = new SwingWorker<Boolean, Void>() {
			protected Boolean doInBackground() throws Exception {
				pk.setTaskName("Finding Related Pathways");
				try {
					openPathwayXref(client, x, rev, tmpDir);
				} catch (Exception e) {
					Logger.log.error("The Pathway is not found", e);
					JOptionPane.showMessageDialog(null,
							"The Pathway is not found", "ERROR",
							JOptionPane.ERROR_MESSAGE);
				} finally {
					pk.finished();
				}
				return true;
			}

		};

		sw.execute();
		d.setVisible(true);
		sw.get();
	}

	protected void openPathwayXref(WikiPathwaysClient client, Xref x, int rev,
			File tmpDir) throws MalformedURLException, ServiceException {

		WSSearchResult[] wsp;
		try {
			wsp = client.findPathwaysByXref(x);

			Xref[] xref = { x };
			PathwayPanel p = new PathwayPanel(WikiPathwaysClientPlugin.this,
					wsp, tmpDir, xref);
			JDialog d = new JDialog(desktop.getFrame(),
					"Related Pathways from WikiPathways", false);

			d.getContentPane().add(p);
			d.pack();
			d.setVisible(true);
			d.setResizable(false);
			d.setLocationRelativeTo(desktop.getSwingEngine().getFrame());
			d.setVisible(true);

		} catch (RemoteException e) {
			
			e.printStackTrace();
		}
	}

	@Override
	public void applicationEvent(ApplicationEvent e) {
	
		updateState();
	}

	@Override
	public void vPathwayEvent(VPathwayEvent arg0) {
		updateState();
	}
}
