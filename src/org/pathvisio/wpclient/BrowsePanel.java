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

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import org.bridgedb.bio.Organism;
import org.pathvisio.core.debug.Logger;
import org.pathvisio.core.util.ProgressKeeper;
import org.pathvisio.gui.ProgressDialog;
import org.pathvisio.wikipathways.webservice.WSCurationTag;
import org.wikipathways.client.WikiPathwaysClient;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 *	This class creates the content in the Browse Dialog
 *	Basic Browse-
 *	Browse by organism
 *	Browse by curation tags
 *	Browse by Collections 
 * 	@author Sravanthi Sinha
 * 	@version 1.0
 */

public class BrowsePanel extends JPanel
{
	WikiPathwaysClientPlugin plugin;
	JComboBox clientDropdown;
	private JComboBox organismOpt;
	JTable resultTable;
	private JScrollPane resultspane;	
	private JLabel speciesLabel, CollecLabel, CuraLabel;
	private JComboBox collOpt;
	private JComboBox curationOpt;

	String collkey = null, curkey = null;
	HashMap<String, String> curationtags = new HashMap<String, String>();
	HashMap<String, String> coll = new HashMap<String, String>();
	HashMap<String, String> tags = new HashMap<String, String>();
	HashMap<String, WSCurationTag[]> imagetags = new HashMap<String, WSCurationTag[]>();
	final ArrayList<String> results = new ArrayList<String>();
	WikiPathwaysClient client;
	WSCurationTag[] pcolltags = null;
	WSCurationTag[] ptags = null;
	int i = 0;;
	ArrayList<WSCurationTag> results2 = new ArrayList<WSCurationTag>();
	JLabel l;

	public BrowsePanel(final WikiPathwaysClientPlugin plugin) {
		this.plugin = plugin;

		List<String> org = new ArrayList<String>();

		// preparing organism list for combobox
		org.add("ALL SPECIES");
		org.add("Anopheles gambiae");
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
		curationtags.put("No Curation", "No Curation");
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
					resultspane.setBorder(BorderFactory.createTitledBorder(WikiPathwaysClientPlugin.etch, "Pathways"));
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
		CollecLabel = new JLabel("Collections:");
		CuraLabel = new JLabel("Curation Tags:");

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
		curationOpt.setSelectedItem("No Curation");
		DefaultFormBuilder curationOptBuilder = new DefaultFormBuilder(	new FormLayout("right:pref, 3dlu,right:pref"));
		curationOptBuilder.append(curationOpt);

		final JPanel opts4 = new JPanel();
		final CardLayout opt4Cards = new CardLayout();
		opts4.setLayout(opt4Cards);
		JPanel curOpt = curationOptBuilder.getPanel();

		opts4.add(curOpt, "Curation");

		// preparing the container for the labels and comboboxes

		JPanel browseOptBox = new JPanel();
		FormLayout layout = new FormLayout("left:pref,6dlu,left:pref,6dlu,left:pref,6dlu,left:pref,6dlu,left:pref,6dlu","p,14dlu");
		CellConstraints cc = new CellConstraints();

		browseOptBox.setLayout(layout);
		browseOptBox.setBorder(BorderFactory.createTitledBorder(WikiPathwaysClientPlugin.etch,"Browse options"));
		browseOptBox.add(speciesLabel, cc.xy(1, 1));
		browseOptBox.add(opts, cc.xy(1, 2));
		browseOptBox.add(CollecLabel, cc.xy(3, 1));
		browseOptBox.add(opts2, cc.xy(3, 2));

		browseOptBox.add(CuraLabel, cc.xy(5, 1));
		browseOptBox.add(opts4, cc.xy(5, 2));
		JButton browseButton = new JButton(browseAction);

		browseOptBox.add(browseButton, cc.xy(7, 2));
		add(browseOptBox, BorderLayout.CENTER);

		Vector<String> clients = new Vector<String>(plugin.getClients().keySet());
		Collections.sort(clients);

		clientDropdown = new JComboBox(clients);
		clientDropdown.setSelectedIndex(0);
		clientDropdown.setRenderer(new DefaultListCellRenderer() 
		{
			public Component getListCellRendererComponent(final JList list,final Object value, final int index,final boolean isSelected, final boolean cellHasFocus)
			{
				String strValue = WikiPathwaysClientPlugin.shortClientName(value.toString());

				return super.getListCellRendererComponent(list, strValue,index, isSelected, cellHasFocus);

			}
		});

		browseOptBox.add(clientDropdown, cc.xy(8, 1));

		if (plugin.getClients().size() < 2)
			clientDropdown.setVisible(false);

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
						plugin.openPathwayWithProgress(plugin.getClients().get(model.clientName),model.getValueAt(row, 0).toString(), 0, tmpDir);
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

	protected void browse() throws RemoteException, InterruptedException,ExecutionException 
	{
		
		String clientName = clientDropdown.getSelectedItem().toString();
		client = plugin.getClients().get(clientName);
		final ProgressKeeper pk = new ProgressKeeper();
		final ProgressDialog d = new ProgressDialog(JOptionPane.getFrameForComponent(this), "", pk, true, true);

		SwingWorker<WSCurationTag[], Void> sw = new SwingWorker<WSCurationTag[], Void>() 
		{
		

			protected WSCurationTag[] doInBackground() throws Exception 
			{
				pk.setTaskName("Browsing");

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
					if (!collkey.equals("Curation:All"))
					{
						pcolltags = client.getCurationTagsByName(collkey); // Retrieving all pathways belonging to certain curation tag

						getPathwaysOfSpecColl(pcolltags, organism);
					}
					else
					{
						getPathwaysOfColl(organism);
					}
					if (!curkey.equals("No Curation"))
					{
					getPathwaysOfSpecCollCur();
					}
					else
					{
					getPathwaysOfSpecColl();
					}

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
				if (i > 0) 
				{
					pcolltags = new WSCurationTag[i];
					results2.toArray(pcolltags);
					return pcolltags;
				} else
				{
					WSCurationTag[] result = Arrays.copyOf(pcolltags,pcolltags.length + ptags.length);
					System.arraycopy(ptags, 0, result, pcolltags.length,ptags.length);
					return result;
				}
			}
		};

		sw.execute();
		d.setVisible(true);

		resultTable.setModel(new BrowseTableModel(sw.get(), clientName));
		resultTable.setDefaultRenderer(JPanel.class, new TableCellRenderer() 
		{

			@Override
			public Component getTableCellRendererComponent(JTable table,Object value, boolean isSelected, boolean arg3, int arg4,
					int arg5) {
				
				JPanel p = (JPanel) value;
				/*
				 * Component[] c = p.getComponents(); for (Component component :
				 * c) { if(component instanceof JLabel) { JLabel l= (JLabel)
				 * component; setToolTipText(l.getToolTipText()); } }
				 */
				return p;
			}
		});
		resultTable.setRowSorter(new TableRowSorter(resultTable.getModel()));
	}
				
	/**
	* storing pathways belonging to certain collection
	*  tag and selected species and curation tag
	*/
	private	void getPathwaysOfSpecColl()
	{
		Iterator itr = results.iterator();
		while (itr.hasNext())
		{
			String sid = itr.next().toString();
			try 
			{
				ptags = client.getCurationTags(sid);
				imagetags.put(sid, ptags);
			}
			catch (RemoteException e)
			{
				e.printStackTrace();
			}
					
			for (Entry<String, String> entry : curationtags.entrySet()) 
			{
				curkey = entry.getKey();
				try 
					{
						for (WSCurationTag tag : ptags)
						{
							if (curkey.equals(tag)) 
							{
								results2.add(tag);
								i++;
							}
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();					
					}
			}

		}
				
	}
			
	/**
	 * storing pathways belonging to all collections and/or selected Species
	 * 
     */
	private void getPathwaysOfColl(String organism)
	 {
	
		HashMap<String, String> tempcoll = coll;
		tempcoll.remove("Curation:All");

		for (Entry<String, String> entry : tempcoll.entrySet()) 
		{
		 String l = "";
		 collkey = entry.getKey();

		 try 
		 {
			for (WSCurationTag tag : client.getCurationTagsByName((collkey)))
			{
			if (!organism.equals("ALL SPECIES"))
				{
				l = tag.getPathway().getSpecies();
				if (l.equals(organism))
					{
					// storing pathways belonging to certain collection and selected species
					results.add(tag.getPathway().getId()); 
					}
				}
				else
				{
				// storing all pathways belonging to certain collection
				results.add(tag.getPathway().getId());
				}
			}
	 	}
		catch (RemoteException e)
		{
		e.printStackTrace();
		}
	
		}
	}

	/**
	 * storing pathways belonging to certain collection and/or selected Species
	 * 
	 */
	private void getPathwaysOfSpecColl(WSCurationTag[] pcolltags,String organism) 
	{
		String l = "";
		for (WSCurationTag tag : pcolltags)
		{
		if (!organism.equals("ALL SPECIES"))
			{
				l = tag.getPathway().getSpecies();
				if (l.equals(organism)) 
				{
				// storing pathways belonging to certain collection and selected species
				results.add(tag.getPathway().getId());
				}
			}
			else
			{
				// storing all pathways belonging to certain collection
				results.add(tag.getPathway().getId());
			}

		}
	}
	
	/**
	 * storing pathways belonging to certain collection
	 * tag and selected species and curation tag
	 */
	private void getPathwaysOfSpecCollCur() 
	{
		Iterator itr = results.iterator();
			while (itr.hasNext())
			{
				String sid = itr.next().toString();
				try 
				{
					ptags = client.getCurationTags(sid);
					imagetags.put(sid, ptags);
				}
				catch (RemoteException e)
				{
					e.printStackTrace();
				}
				for (WSCurationTag tag : ptags)
				{
					String op = tag.getName();
					if (curkey.equals(op)) 
					{
						// storing pathways belonging to certain collection tag and selected species and curation tag
						results2.add(tag);
						i++;
					}
				}
			}
	}

		
	/**
	 * This class creates the BrowseTableModel 
	 * Based on the Browse Criteria
	 */
	private class BrowseTableModel extends AbstractTableModel 
	{
		WSCurationTag[] results;
		String[] columnNames = new String[] { "ID", "Name", "Species","Curation Tag" };

		String clientName = clientDropdown.getSelectedItem().toString();
		final WikiPathwaysClient client = plugin.getClients().get(clientName);

		public BrowseTableModel(WSCurationTag[] wsCurationTags,String clientName2) 
		{
			this.clientName = clientName2;
			this.results = wsCurationTags;
		}

		public int getColumnCount() 
		{
			return 4;
		}

		public Class getColumnClass(int column)
		{
			return getValueAt(3, column).getClass();
		}

		public int getRowCount() 
		{
			return results.length;
		}

		public Object getValueAt(int rowIndex, int columnIndex)
		{
			WSCurationTag r = results[rowIndex];

			switch (columnIndex) {
			case 0:
				return r.getPathway().getId();
			case 1:
				return r.getPathway().getName();
			case 2:
				return r.getPathway().getSpecies();
			case 3: {
				String IMG_SEARCH = "";

				JPanel p = new JPanel();
				p.setLayout(new FlowLayout(FlowLayout.CENTER, 2, 1));

				ImageIcon icon = null;

				// storing images of the curated tags belonging to certain pathways in Panel
				for (WSCurationTag tag : imagetags.get(r.getPathway().getId())) 
				{

					if (tags.containsKey(tag.getName())) 
					{
						IMG_SEARCH = "resources/" + tags.get(tag.getName())+ ".png";
						URL url = this.getClass().getClassLoader().getResource(IMG_SEARCH);
						icon = new ImageIcon(url);
						
						Image img = icon.getImage();
						Image newimg = img.getScaledInstance(15, 10,java.awt.Image.SCALE_SMOOTH);//SCALING THE IMAGE
						ImageIcon newIcon = new ImageIcon(newimg);

						l = new JLabel(newIcon);

						p.setBackground(Color.white);
						p.add(l);

					}
				}
				return p;

			}
			}
			return "";
		}

		public String getColumnName(int column) {
			return columnNames[column];
		}

	}

	}
