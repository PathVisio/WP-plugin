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
package org.pathvisio.wpclient.panels;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import javax.xml.rpc.ServiceException;

import org.bridgedb.bio.Organism;
import org.pathvisio.core.debug.Logger;
import org.pathvisio.core.util.ProgressKeeper;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.gui.ProgressDialog;
import org.pathvisio.wikipathways.webservice.WSCurationTag;
import org.pathvisio.wikipathways.webservice.WSPathwayInfo;
import org.pathvisio.wpclient.WikiPathwaysClientPlugin;
import org.pathvisio.wpclient.actions.BrowseAction;
import org.pathvisio.wpclient.models.BrowseTableModel;
import org.wikipathways.client.WikiPathwaysClient;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 *	This class creates the content in the Browse Panel *
 * 	@author Sravanthi Sinha
 * 	@author Martina Kutmon
 * 	@version 1.0
 */

public class BrowsePanel extends JPanel
{
	WikiPathwaysClientPlugin plugin;

	private JComboBox organismOpt;
	JTable resultTable;
	private JScrollPane resultspane;	
	private JLabel speciesLabel, CollecLabel, CuraLabel;
	private JComboBox collOpt;
	private JComboBox curationOpt;

	String collkey, curkey;
	HashMap<String, String> curationtags = new HashMap<String, String>();
	HashMap<String, String> coll = new HashMap<String, String>();
	public static HashMap<String, String> tags = new HashMap<String, String>();
	
	final ArrayList<String> results = new ArrayList<String>();

	WSCurationTag[] pcolltags;
	WSCurationTag[] ptags ;

	JLabel l;

	private PvDesktop desktop;

	public BrowsePanel(final PvDesktop desktop,final WikiPathwaysClientPlugin plugin) throws RemoteException, MalformedURLException, ServiceException {
		this.plugin = plugin;
		
		this.desktop=desktop;
		WikiPathwaysClient client = WikiPathwaysClientPlugin.loadClient();
		List<String> org = new ArrayList<String>();

		// preparing organism list for combobox
		org.add("ALL SPECIES");
		String[] organisms = client.listOrganisms();
		List<String> organismslist= new ArrayList<String>(Arrays.asList(organisms));
		org.addAll(1, organismslist);
		/*org.add("Anopheles gambiae");
		org.add("Arabidopsis thaliana");
		org.add("Bos taurus");
		org.add("Bacillus subtilis");
		org.add("Caenorhabditis elegans");
		org.add("Canis familiaris");
		org.add("Clostridium thermocellum");
		org.add("Danio rerio");
		org.add("Drosophila melanogaster");
		org.add("Escherichia coli");
		org.add("Equus caballus");
		org.add("Escherichia coli");
		org.add("Gallus gallus");
		org.add("Glycine max");
		org.add("Gibberella zeae");
		org.add("Homo sapiens");
		org.add("Mus musculus");
		org.add("Mycobacterium tuberculosis");
		org.add("Oryza sativa");
		org.add("Pan troglodytes");
		org.add("Populus trichocarpa");
		org.add("Rattus norvegicus");
		org.add("Saccharomyces cerevisiae");
		org.add("Solanum lycopersicum");
		org.add("Sus scrofa");
		org.add("Vitis vinifera");
		org.add("Xenopus tropicalis");
		org.add("Zea mays");
*/
		// collections combo box
		coll.put("Curation:AnalysisCollection", "Curated pathways");
		coll.put("Curation:FeaturedPathway", "Featured pathways");
		coll.put("Curation:Reactome_Approved", "Reactome pathways");
		coll.put("Curation:WormBase_Approved", "WormBase pathways");
		coll.put("Curation:CIRM_Related ", "CIRM pathways");
		coll.put("Curation:Wikipedia", "Wikipedia pathways");
		coll.put("Curation:OpenAccess", "Open Access pathways");
		coll.put("Curation:GenMAPP_Approved", "GenMAPP pathways");
		coll.put("Curation:All", "All pathways");

		// Curation Combobox
		curationtags.put("No Curation", "All tags");
		curationtags.put("Curation:MissingXRef", "missing annotations");
		curationtags.put("Curation:NoInteractions", "unconnected lines");
		curationtags.put("Curation:UnderConstruction", "under construction");
		curationtags.put("Curation:Stub", "stub");
		curationtags.put("Curation:NeedsWork", "needs work");
		

		// curationtags mapping for image resouces
		tags.put("Curation:MissingXRef", "MissingXRef");
		tags.put("Curation:Stub", "Stub");
		tags.put("Curation:NeedsWork", "NeedsWork");
		tags.put("Curation:AnalysisCollection", "Curated");
		tags.put("Curation:MissingDescription", "MissingDescription");
		tags.put("Curation:NoInteractions", "UnConnected");
		tags.put("Curation:NeedsReference", "NeedsRef");
		setLayout(new BorderLayout());

		Action browseAction = new AbstractAction("Browse")
		{
			public void actionPerformed(ActionEvent e) 
			{
				try 
				{
					resultspane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Pathways"));
					browse();
				}
				catch (Exception ex) 
				{
					JOptionPane.showMessageDialog(BrowsePanel.this,ex.getMessage(), "Error",JOptionPane.ERROR_MESSAGE);
					Logger.log.error("Error browsing WikiPathways", ex);
				}
			}
		};

		// Browse Option Labels
		speciesLabel = new JLabel("Species:");
		// catLabel = new JLabel("Categories:");
		CollecLabel = new JLabel("Collection:");
		CuraLabel = new JLabel("Curation Tag:");

		// species combo box,Right now combo box is hardcoded , we should get
		// the list of organisms from the webservices

		organismOpt = new JComboBox(org.toArray());
		organismOpt.setSelectedItem(Organism.HomoSapiens.latinName());

		DefaultFormBuilder idOptBuilder = new DefaultFormBuilder(new FormLayout("right:pref, 3dlu,right:pref"));
		idOptBuilder.append(organismOpt);
		
		
		final JPanel opts = new JPanel();
		final CardLayout optCards = new CardLayout();
		opts.setLayout(optCards);
		JPanel idOpt = idOptBuilder.getPanel();
		opts.add(idOpt, "Species");

		
		collOpt = new JComboBox();
		Iterator it = coll.keySet().iterator();
		while (it.hasNext())
		{
			collOpt.addItem(coll.get(it.next()));
		}
		collOpt.setSelectedItem("All pathways");
		
		DefaultFormBuilder colOptBuilder = new DefaultFormBuilder(new FormLayout("right:pref, 3dlu,right:pref"));
		colOptBuilder.append(collOpt);

		JPanel opts2 = new JPanel();
		final CardLayout opt2Cards = new CardLayout();
		opts2.setLayout(opt2Cards);
		JPanel collOptBuilder = colOptBuilder.getPanel();
		opts2.add(collOptBuilder, "Collections");

		
		
		curationOpt = new JComboBox();
		it = curationtags.keySet().iterator();
		while (it.hasNext())
		{
			curationOpt.addItem(curationtags.get(it.next()));
		}
		curationOpt.setSelectedItem("All tags");
		DefaultFormBuilder curationOptBuilder = new DefaultFormBuilder(	new FormLayout("right:pref, 3dlu,right:pref"));
		curationOptBuilder.append(curationOpt);

		final JPanel opts4 = new JPanel();
		final CardLayout opt4Cards = new CardLayout();
		opts4.setLayout(opt4Cards);
		JPanel curOpt = curationOptBuilder.getPanel();

		opts4.add(curOpt, "Curation");

		// preparing the container for the labels and comboboxes

		JPanel browseOptBox = new JPanel();
		FormLayout layout = new FormLayout("left:pref,6dlu,left:pref,6dlu,left:pref,6dlu,left:pref,6dlu,left:pref,6dlu","p,20dlu");
		CellConstraints cc = new CellConstraints();

		browseOptBox.setLayout(layout);
		browseOptBox.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"Browse options"));
		browseOptBox.add(speciesLabel, cc.xy(1, 1));
		browseOptBox.add(opts, cc.xy(1, 2));
		browseOptBox.add(CollecLabel, cc.xy(3, 1));
		browseOptBox.add(opts2, cc.xy(3, 2));

		browseOptBox.add(CuraLabel, cc.xy(5, 1));
		browseOptBox.add(opts4, cc.xy(5, 2));
		JButton browseButton = new JButton(browseAction);

		browseOptBox.add(browseButton, cc.xy(7, 2));
		add(browseOptBox, BorderLayout.CENTER);

	
		add(browseOptBox, BorderLayout.NORTH);

		// Center contains table model for results
		resultTable = new JTable();
		resultspane = new JScrollPane(resultTable);
		add(resultspane, BorderLayout.CENTER);

		resultTable.addMouseListener(new MouseAdapter() 
		{
			public void mouseClicked(MouseEvent e) 
			{
				if (e.getClickCount() == 2)
				{
					JTable target = (JTable) e.getSource();
					int row = target.getSelectedRow();

					BrowseTableModel model = (BrowseTableModel) target.getModel();

					File tmpDir = new File(plugin.getTmpDir(), WikiPathwaysClientPlugin.shortClientName(model.clientName));
					tmpDir.mkdirs();

					try 
					{
						plugin.openPathwayWithProgress(WikiPathwaysClientPlugin.loadClient(),model.getValueAt(row, 0).toString(), 0, tmpDir);
					} 
					catch (Exception ex)
					{
						JOptionPane.showMessageDialog(BrowsePanel.this,ex.getMessage(), "Error",JOptionPane.ERROR_MESSAGE);
						Logger.log.error("Error", ex);
					}
				}

			}
		});
	}
	
	/**
	 * 
	 * @throws RemoteException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws ServiceException 
	 * @throws MalformedURLException 
	 */
	protected void browse() throws RemoteException, InterruptedException,ExecutionException, MalformedURLException, ServiceException 
	{
		
		
		final WikiPathwaysClient client = WikiPathwaysClientPlugin.loadClient();
		final ProgressKeeper pk = new ProgressKeeper();
		final ProgressDialog d = new ProgressDialog(JOptionPane.getFrameForComponent(this), "Browse", pk, true, true);

		SwingWorker<WSPathwayInfo[], Void> sw = new SwingWorker<WSPathwayInfo[], Void>() 
		{
			WSPathwayInfo[] wsPathwayInfos;

			protected WSPathwayInfo[] doInBackground() throws Exception 
			{
				pk.setTaskName("Browsing WikiPathways");
				Set<WSPathwayInfo> pathways= new HashSet<WSPathwayInfo>();
				try {

					// obtaining the selected cutaion tag
					for (Entry<String, String> entry : curationtags.entrySet())
					{
						if ((curationOpt.getSelectedItem().toString()).equals(entry.getValue())) 
						{
							curkey = entry.getKey();
							break; // breaking because its one to one map
						}
					}

					// obtaining the selected collection
					for (Entry<String, String> entry : coll.entrySet())
					{
						if ((collOpt.getSelectedItem().toString()).equals(entry.getValue())) 
						{
							collkey = entry.getKey();
							break; // breaking because its one to one map
						}
					}
					// Retrieving the selected species
					String organism = organismOpt.getSelectedItem().toString();
				
					
					BrowseAction b = new BrowseAction(desktop, plugin);
					
					if ((organism.equalsIgnoreCase("ALL SPECIES"))&& ((collkey.equalsIgnoreCase("Curation:All")) && ((curkey.equalsIgnoreCase("No Curation")))))
					
						pathways= b.browseAll(client,pk);
						
					
					else if (!organism.equalsIgnoreCase("ALL SPECIES")&& ((collkey.equalsIgnoreCase("Curation:All")) && ((curkey.equalsIgnoreCase("No Curation")))))
					
						pathways=  b.browseByOrganism(client, Organism.fromLatinName(organism),pk);
					
					else if ((organism.equalsIgnoreCase("ALL SPECIES"))&& (!(collkey.equalsIgnoreCase("Curation:All")) && ((curkey.equalsIgnoreCase("No Curation")))))
					{
						pk.setTaskName("Browsing through Collections");
						pathways=  b.browseByCollection(client, collkey,pk);
					}					
					else if ((organism.equalsIgnoreCase("ALL SPECIES"))	&& ((collkey.equalsIgnoreCase("Curation:All")) && (!(curkey.equalsIgnoreCase("No Curation")))))
					{
						pk.setTaskName("Browsing through CurationTags");
						pathways=  b.browseByCurationTag(client, curkey,pk);
					}
					else if (!(organism.equalsIgnoreCase("ALL SPECIES"))&& (!(collkey.equalsIgnoreCase("Curation:All")) && ((curkey.equalsIgnoreCase("No Curation")))))
						pathways=  b.browseByOrganismAndCollection(client, Organism	.fromLatinName(organism), collkey,pk);
					
					else if (!(organism.equalsIgnoreCase("ALL SPECIES"))&& ((collkey.equalsIgnoreCase("Curation:All")) && (!(curkey.equalsIgnoreCase("No Curation")))))
						pathways=  b.browseByOrganismAndCurationTag(client, Organism.fromLatinName(organism), curkey,pk);
					
					else if ((organism.equalsIgnoreCase("ALL SPECIES"))	&& (!(collkey.equalsIgnoreCase("Curation:All")) && (!(curkey.equalsIgnoreCase("No Curation")))))
						pathways=  b.browseByCollectionAndCurationTag(client, collkey,curkey,pk);
					
					else if ((!organism.equalsIgnoreCase("ALL SPECIES"))&& (!(collkey.equalsIgnoreCase("Curation:All")) && (!(curkey.equalsIgnoreCase("No Curation")))))
						pathways= b.browseByOrganismAndCollectionAndCurationTag(client, Organism.fromLatinName(organism), collkey,curkey,pk);
					
					wsPathwayInfos= new WSPathwayInfo[pathways.size()];
					return pathways.toArray(wsPathwayInfos);
				
				}
				catch (Exception ex)
				{
					JOptionPane.showMessageDialog(BrowsePanel.this,ex.getMessage(), "Error",JOptionPane.ERROR_MESSAGE);
					Logger.log.error("Error", ex);
				}
				finally 
				{
					pk.finished();
				}
			return wsPathwayInfos;
			}
			protected void done() {
				if (!pk.isCancelled()) {
					try {
						if (get().length == 0) {
							JOptionPane.showMessageDialog(null,
									"0 results found");
						}
					} catch (HeadlessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (pk.isCancelled()) {
					pk.finished();
				}
			}
		};

		sw.execute();
		d.setVisible(true);

		resultTable.setModel(new BrowseTableModel(sw.get(), client.toString()));
		resultTable.setDefaultRenderer(JPanel.class, new TableCellRenderer() 
		{

			@Override
			public Component getTableCellRendererComponent(JTable table,Object value, boolean isSelected, boolean arg3, int arg4,
					int arg5) {
				
				JPanel p = (JPanel) value;
				return p;
			}
		});
		resultTable.setRowSorter(new TableRowSorter(resultTable.getModel()));
	}
				

	}
