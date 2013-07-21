package org.tno.wpclient;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.util.Map.Entry;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import org.bridgedb.bio.Organism;
import org.pathvisio.core.debug.Logger;
import org.pathvisio.core.util.ProgressKeeper;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.gui.CommonActions;
import org.pathvisio.gui.ObjectsPane;
import org.pathvisio.gui.ProgressDialog;
import org.pathvisio.wikipathways.webservice.WSCurationTag;
import org.pathvisio.wikipathways.webservice.WSPathwayInfo;
import org.wikipathways.client.WikiPathwaysClient;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class BrowsePanel extends JPanel {
	WikiPathwaysClientPlugin plugin;
	JComboBox clientDropdown;
	private JComboBox organismOpt;
	JTable resultTable;
	private JScrollPane resultspane;
	Border etch = BorderFactory.createEtchedBorder();
	private JLabel speciesLabel, CollecLabel, CuraLabel;
	private JComboBox collOpt;
	// private JComboBox categoryOpt;
	private JComboBox curationOpt;
	WSPathwayInfo[] results2;
	String collkey = null, curkey = null;
	java.util.HashMap<String, String> curationtags = new HashMap<String, String>();
	java.util.HashMap<String, String> coll = new HashMap<String, String>();

	public BrowsePanel(final WikiPathwaysClientPlugin plugin) {
		this.plugin = plugin;

		setLayout(new BorderLayout());

		Action browseAction = new AbstractAction("Browse") {
			public void actionPerformed(ActionEvent e) {
				try {
					resultspane.setBorder(BorderFactory.createTitledBorder(
							etch, "Pathways"));
					browse();
				} catch (Exception ex) {
					JOptionPane
							.showMessageDialog(BrowsePanel.this,
									ex.getMessage(), "Error",
									JOptionPane.ERROR_MESSAGE);
					Logger.log.error("Error searching WikiPathways", ex);
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
		java.util.List<String> org = new ArrayList<String>();
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

		organismOpt = new JComboBox(org.toArray());
		organismOpt.setSelectedItem(Organism.HomoSapiens.latinName());
		/*
		 * organismOpt.addActionListener(new ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) { try {
		 * resultspane.setBorder(BorderFactory.createTitledBorder( etch,
		 * "Pathways")); if (collOpt.getSelectedItem().toString()
		 * .equals("All pathways") && curationOpt.getSelectedItem().toString()
		 * .equals("Not specified")) { browseByCollection(); } else browse(); }
		 * catch (Exception ex) {
		 * JOptionPane.showMessageDialog(BrowsePanel.this, ex.getMessage(),
		 * "Error", JOptionPane.ERROR_MESSAGE);
		 * Logger.log.error("Error Browsing WikiPathways", ex); } } });
		 */

		DefaultFormBuilder idOptBuilder = new DefaultFormBuilder(
				new FormLayout("right:pref, 3dlu,right:pref"));
		idOptBuilder.append(organismOpt);
		final JPanel opts = new JPanel();
		final CardLayout optCards = new CardLayout();
		opts.setLayout(optCards);
		JPanel idOpt = idOptBuilder.getPanel();
		opts.add(idOpt, "Species");

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

		collOpt = new JComboBox();
		Iterator it = coll.keySet().iterator();
		while (it.hasNext()) {
			collOpt.addItem(coll.get(it.next()));
		}
		collOpt.setSelectedItem("All pathways");
		DefaultFormBuilder colOptBuilder = new DefaultFormBuilder(
				new FormLayout("right:pref, 3dlu,right:pref"));
		colOptBuilder.append(collOpt);
		/*
		 * collOpt.addActionListener(new ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) { try {
		 * resultspane.setBorder(BorderFactory.createTitledBorder( etch,
		 * "Pathways")); browseByCollection(); } catch (Exception ex) {
		 * JOptionPane.showMessageDialog(BrowsePanel.this, ex.getMessage(),
		 * "Error", JOptionPane.ERROR_MESSAGE);
		 * Logger.log.error("Error Browsing WikiPathways", ex); } } });
		 */
		JPanel opts2 = new JPanel();
		final CardLayout opt2Cards = new CardLayout();
		opts2.setLayout(opt2Cards);
		JPanel collOptBuilder = colOptBuilder.getPanel();
		opts2.add(collOptBuilder, "Collections");

		// Category COmboBox
		/*
		 * java.util.List<String> cat = new ArrayList<String>();
		 * cat.add("Cellular Process"); cat.add("Metabolic Process");
		 * cat.add("Molecular Function"); cat.add("Physiological Process");
		 * cat.add("All Categories"); cat.add("Un Categoriesed");
		 * 
		 * categoryOpt = new JComboBox(cat.toArray());
		 * categoryOpt.setSelectedItem("All Categories"); DefaultFormBuilder
		 * catOptBuilder = new DefaultFormBuilder( new
		 * FormLayout("right:pref, 3dlu,right:pref"));
		 * catOptBuilder.append(categoryOpt);
		 * 
		 * final JPanel opts3 = new JPanel(); final CardLayout opt3Cards = new
		 * CardLayout(); opts3.setLayout(opt3Cards); JPanel catOpt =
		 * catOptBuilder.getPanel(); opts3.add(catOpt, "Category");
		 */

		// Curation Combobox
		curationtags.put("Not specified", "Not specified");
		curationtags.put("Curation:MissingXRef", "missing annotations");
		curationtags.put("Curation:NoInteractions", "unconnected lines");
		curationtags.put("Curation:UnderConstruction", "under construction");
		curationtags.put("Curation:Stub", "stub");
		curationtags.put("Curation:NeedsWork", "needs work");
		curationtags.put("Curation:All", "All pathways");
		curationOpt = new JComboBox();
		it = curationtags.keySet().iterator();
		while (it.hasNext()) {
			curationOpt.addItem(curationtags.get(it.next()));
		}
		curationOpt.setSelectedItem("All pathways");
		DefaultFormBuilder curationOptBuilder = new DefaultFormBuilder(
				new FormLayout("right:pref, 3dlu,right:pref"));
		curationOptBuilder.append(curationOpt);
		/*
		 * curationOpt.addActionListener(new ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) { try {
		 * resultspane.setBorder(BorderFactory.createTitledBorder( etch,
		 * "Pathways")); if (collOpt.getSelectedItem().toString()
		 * .equals("All pathways") && curationOpt.getSelectedItem().toString()
		 * .equals("Not specified")) { browseBySpecies(); } else {
		 * browseByCurationTag(); } } catch (Exception ex) {
		 * JOptionPane.showMessageDialog(BrowsePanel.this, ex.getMessage(),
		 * "Error", JOptionPane.ERROR_MESSAGE);
		 * Logger.log.error("Error Browsing WikiPathways", ex); } } });
		 */

		final JPanel opts4 = new JPanel();
		final CardLayout opt4Cards = new CardLayout();
		opts4.setLayout(opt4Cards);
		JPanel curOpt = curationOptBuilder.getPanel();

		opts4.add(curOpt, "Curation");

		// preparing the container for the labels and comboboxes

		JPanel browseOptBox = new JPanel();
		FormLayout layout = new FormLayout(
				"left:pref,6dlu,left:pref,6dlu,left:pref,6dlu,left:pref,6dlu,left:pref,6dlu",
				"p,14dlu");
		CellConstraints cc = new CellConstraints();

		browseOptBox.setLayout(layout);
		browseOptBox.setBorder(BorderFactory.createTitledBorder(etch,
				"Browse options"));
		browseOptBox.add(speciesLabel, cc.xy(1, 1));
		browseOptBox.add(opts, cc.xy(1, 2));
		browseOptBox.add(CollecLabel, cc.xy(3, 1));
		browseOptBox.add(opts2, cc.xy(3, 2));

		browseOptBox.add(CuraLabel, cc.xy(5, 1));
		browseOptBox.add(opts4, cc.xy(5, 2));
		JButton browseButton = new JButton(browseAction);

		browseOptBox.add(browseButton, cc.xy(7, 2));
		add(browseOptBox, BorderLayout.CENTER);

		Vector<String> clients = new Vector<String>(plugin.getClients()
				.keySet());
		Collections.sort(clients);

		clientDropdown = new JComboBox(clients);
		clientDropdown.setSelectedIndex(0);
		clientDropdown.setRenderer(new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(final JList list,
					final Object value, final int index,
					final boolean isSelected, final boolean cellHasFocus) {
				String strValue = SearchPanel.shortClientName(value.toString());

				return super.getListCellRendererComponent(list, strValue,
						index, isSelected, cellHasFocus);

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

		resultTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					JTable target = (JTable) e.getSource();
					int row = target.getSelectedRow();

					BrowseTableModel2 model = (BrowseTableModel2) target
							.getModel();

					File tmpDir = new File(plugin.getTmpDir(), BrowsePanel
							.shortClientName(model.clientName));
					tmpDir.mkdirs();

					try {
						plugin.openPathwayWithProgress(
								plugin.getClients().get(model.clientName),
								model.getValueAt(row, 0).toString(), 0, tmpDir);
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(BrowsePanel.this,
								ex.getMessage(), "Error",
								JOptionPane.ERROR_MESSAGE);
						Logger.log.error("Error", ex);
					}
				}

			}
		});
	}

	/*
	 * protected void browseBySpecies() throws RemoteException,
	 * InterruptedException, ExecutionException {
	 * 
	 * String clientName = clientDropdown.getSelectedItem().toString(); final
	 * WikiPathwaysClient client = plugin.getClients().get(clientName); final
	 * ProgressKeeper pk = new ProgressKeeper(); final ProgressDialog d = new
	 * ProgressDialog( JOptionPane.getFrameForComponent(this), "", pk, true,
	 * true);
	 * 
	 * SwingWorker<WSPathwayInfo[], Void> sw = new SwingWorker<WSPathwayInfo[],
	 * Void>() { protected WSPathwayInfo[] doInBackground() throws Exception {
	 * pk.setTaskName("Browsing"); WSPathwayInfo[] results = null; try {
	 * 
	 * results = client.listPathways(Organism
	 * .fromLatinName(organismOpt.getSelectedItem() .toString())); } catch
	 * (Exception e) { throw e; } finally { pk.finished(); } return results; }
	 * };
	 * 
	 * sw.execute(); d.setVisible(true);
	 * 
	 * resultTable.setModel(new BrowseTableModel2(sw.get(), clientName));
	 * resultTable.setRowSorter(new TableRowSorter(resultTable.getModel()));
	 * 
	 * }
	 */

	public static String shortClientName(String clientName) {
		Pattern pattern = Pattern.compile("http://(.*?)/");
		Matcher matcher = pattern.matcher(clientName);

		if (matcher.find()) {
			clientName = matcher.group(1);
		}

		return clientName;
	}

	protected void browse() throws RemoteException, InterruptedException,
			ExecutionException {

		String clientName = clientDropdown.getSelectedItem().toString();
		final WikiPathwaysClient client = plugin.getClients().get(clientName);
		final ProgressKeeper pk = new ProgressKeeper();
		final ProgressDialog d = new ProgressDialog(
				JOptionPane.getFrameForComponent(this), "", pk, true, true);

		SwingWorker<WSCurationTag[], Void> sw = new SwingWorker<WSCurationTag[], Void>() {

			protected WSCurationTag[] doInBackground() throws Exception {
				pk.setTaskName("Browsing");

				WSCurationTag[] results3 = null;
				WSCurationTag[] results4 = null;
				ArrayList<String> results = new ArrayList<String>();
				ArrayList<WSCurationTag> results2 = new ArrayList<WSCurationTag>();
				int i = 0;

				try {

					for (Entry<String, String> entry : curationtags.entrySet()) {
						if ((curationOpt.getSelectedItem().toString())
								.equals(entry.getValue())) {
							curkey = entry.getKey();
							break; // breaking because its one to one map
						}
					}

					for (Entry<String, String> entry : coll.entrySet()) {
						if ((collOpt.getSelectedItem().toString()).equals(entry
								.getValue())) {
							collkey = entry.getKey();
							break; // breaking because its one to one map
						}
					}

					String p = organismOpt.getSelectedItem().toString();

					String l = "";
					results3 = client.getCurationTagsByName(collkey);

					for (WSCurationTag tag : results3) {
						if (!p.equals("ALL SPECIES")) {
							l = tag.getPathway().getSpecies();

							if (l.equals(p)) {
								results.add(tag.getPathway().getId());

							}
						}

					}

					Iterator itr = results.iterator();
					while (itr.hasNext()) {
						String sid = itr.next().toString();
						results4 = client.getCurationTags(sid);
						for (WSCurationTag tag : results4) {
							String op = tag.getName();
							if (curkey.equals(op)) {
								results2.add(tag);
								i++;
							}
						}

					}
				} catch (Exception e) {
					throw e;
				} finally {
					pk.finished();
				}
				if (i > 0) {
					results3 = new WSCurationTag[i];
					results2.toArray(results3);
					return results3;
				} else {
					WSCurationTag[] result = Arrays.copyOf(results3,
							results3.length + results4.length);
					System.arraycopy(results4, 0, result, results3.length,
							results4.length);
					return result;
				}
			}
		};

		sw.execute();
		d.setVisible(true);

		resultTable.setModel(new BrowseTableModel2(sw.get(), clientName));
		resultTable.setDefaultRenderer(JPanel.class, new TableCellRenderer() {

			@Override
			public Component getTableCellRendererComponent(JTable arg0,
					Object value, boolean arg2, boolean arg3, int arg4, int arg5) {
				// TODO Auto-generated method stub
				JPanel p = (JPanel) value;

				return p;
			}
		});
		resultTable.setRowSorter(new TableRowSorter(resultTable.getModel()));

	}

	/*
	 * protected void browseByCurationTag() throws RemoteException,
	 * InterruptedException, ExecutionException {
	 * 
	 * String clientName = clientDropdown.getSelectedItem().toString(); final
	 * WikiPathwaysClient client = plugin.getClients().get(clientName); final
	 * ProgressKeeper pk = new ProgressKeeper(); final ProgressDialog d = new
	 * ProgressDialog( JOptionPane.getFrameForComponent(this), "", pk, true,
	 * true);
	 * 
	 * SwingWorker<WSCurationTag[], Void> sw = new SwingWorker<WSCurationTag[],
	 * Void>() { protected WSCurationTag[] doInBackground() throws Exception {
	 * 
	 * pk.setTaskName("Browsing"); WSCurationTag[] results3 = null;
	 * ArrayList<WSCurationTag> results2 = new ArrayList<WSCurationTag>(); int i
	 * = 0; String key = null; try { for (Entry<String, String> entry :
	 * curationtags.entrySet()) { if ((curationOpt.getSelectedItem().toString())
	 * .equals(entry.getValue())) { key = entry.getKey(); break; // breaking
	 * because its one to one map } }
	 * 
	 * results3 = client.getCurationTagsByName(key);
	 * 
	 * for (WSCurationTag tag : results3) { String p =
	 * organismOpt.getSelectedItem().toString(); if (!p.equals("ALL SPECIES")) {
	 * String l = tag.getPathway().getSpecies(); if (l.equals(p)) {
	 * results2.add(tag); i++; } } }
	 * 
	 * } catch (Exception e) { throw e; } finally { pk.finished(); } if (i > 0)
	 * { results3 = new WSCurationTag[i]; results2.toArray(results3); return
	 * results3; } else return results3; } };
	 * 
	 * sw.execute(); d.setVisible(true);
	 * 
	 * resultTable.setModel(new BrowseTableModel2(sw.get(), clientName));
	 * resultTable.setRowSorter(new TableRowSorter(resultTable.getModel())); }
	 * 
	 * protected void browseByCollection() throws RemoteException,
	 * InterruptedException, ExecutionException { String clientName =
	 * clientDropdown.getSelectedItem().toString(); final WikiPathwaysClient
	 * client = plugin.getClients().get(clientName); final ProgressKeeper pk =
	 * new ProgressKeeper(); final ProgressDialog d = new ProgressDialog(
	 * JOptionPane.getFrameForComponent(this), "", pk, true, true);
	 * 
	 * SwingWorker<WSCurationTag[], Void> sw = new SwingWorker<WSCurationTag[],
	 * Void>() { protected WSCurationTag[] doInBackground() throws Exception {
	 * 
	 * pk.setTaskName("Browsing"); WSPathwayInfo[] results = null;
	 * WSCurationTag[] results3 = null; ArrayList<WSCurationTag> results2 = new
	 * ArrayList<WSCurationTag>(); int i = 0; String key = null; try {
	 * 
	 * for (Entry<String, String> entry : coll.entrySet()) { if
	 * ((collOpt.getSelectedItem().toString()).equals(entry .getValue())) { key
	 * = entry.getKey(); break; // breaking because its one to one map } }
	 * String p = organismOpt.getSelectedItem().toString(); if
	 * (!key.equals("Curation:All")) { results3 =
	 * client.getCurationTagsByName(key); for (WSCurationTag tag : results3) {
	 * if (!p.equals("ALL SPECIES")) { String l = tag.getPathway().getSpecies();
	 * if (l.equals(p)) { results2.add(tag); i++; } } }
	 * 
	 * } else { if (!p.equals("ALL SPECIES")) { results =
	 * client.listPathways(Organism .fromLatinName(organismOpt
	 * .getSelectedItem().toString()));
	 * 
	 * for (WSPathwayInfo pa : results) { WSCurationTag[] lo =
	 * client.getCurationTags(pa .getId()); for (WSCurationTag tag2 : lo) { if
	 * (coll.containsKey(tag2.getName())) { if (!tag2.getName().equals(
	 * "Curation:All")) { results2.add(tag2); i++; } } } } } else { for
	 * (Entry<String, String> entry : coll.entrySet()) {
	 * 
	 * key = entry.getKey(); if (!key.equals("Curation:All")) { WSCurationTag[]
	 * lo = client .getCurationTagsByName(key); for (WSCurationTag tag2 : lo) {
	 * results2.add(tag2); i++; } } } } } }
	 * 
	 * catch (Exception e) { throw e; } finally { pk.finished(); } if (i > 0) {
	 * results3 = new WSCurationTag[i]; results2.toArray(results3); return
	 * results3; } else return results3; } };
	 * 
	 * sw.execute(); d.setVisible(true);
	 * 
	 * resultTable.setModel(new BrowseTableModel2(sw.get(), clientName));
	 * resultTable.setRowSorter(new TableRowSorter(resultTable.getModel())); }
	 */
	private class BrowseTableModel2 extends AbstractTableModel {
		WSCurationTag[] results;
		String[] columnNames = new String[] { "ID", "Name", "Species",
				"Curation Tag" };

		String clientName = clientDropdown.getSelectedItem().toString();
		final WikiPathwaysClient client = plugin.getClients().get(clientName);

		public BrowseTableModel2(WSCurationTag[] wsCurationTags,
				String clientName2) {
			this.clientName = clientName2;
			this.results = wsCurationTags;
		}

		public int getColumnCount() {
			return 4;
		}

		public Class getColumnClass(int column) {
			return getValueAt(3, column).getClass();
		}

		public int getRowCount() {
			return results.length;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
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

				HashMap<String, String> tags = new HashMap<String, String>();
				tags.put("Curation:MissingXRef", "MissingXRef");
				tags.put("Curation:Stub", "Stub");
				tags.put("Curation:NeedsWork", "NeedsWork");
				tags.put("Curation:AnalysisCollection", "Curated");
				tags.put("Curation:MissingDescription", "MissingDescription");
				tags.put("Curation:NoInteractions", "UnConnected");
				tags.put("Curation:NeedsReference", "NeedsRef");

				JPanel p = new JPanel();
				p.setLayout(new FlowLayout(FlowLayout.CENTER, 2, 1));

				ImageIcon icon = null;

				try {
					for (WSCurationTag tag : client.getCurationTags(r
							.getPathway().getId())) {

						String[] re = tag.getName().split(":");
						if (tags.containsKey(tag.getName())) {
							IMG_SEARCH = "resources/" + tags.get(tag.getName())
									+ ".png";
							URL url = this.getClass().getClassLoader()
									.getResource(IMG_SEARCH);
							icon = new ImageIcon(url);

							JLabel l = new JLabel(icon);
							l.setToolTipText(tags.get(tag.getName()));
							p.add(l);

						}
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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

	/*
	 * private class BrowseTableModel extends AbstractTableModel {
	 * WSPathwayInfo[] results; String[] columnNames = new String[] { "ID",
	 * "Name", "Species" }; String clientName;
	 * 
	 * public BrowseTableModel(WSPathwayInfo[] wsPathwayInfos, String
	 * clientName) { this.clientName = clientName; this.results =
	 * wsPathwayInfos; }
	 * 
	 * public int getColumnCount() { return 3; }
	 * 
	 * public int getRowCount() { return results.length; }
	 * 
	 * public Object getValueAt(int rowIndex, int columnIndex) { WSPathwayInfo r
	 * = results[rowIndex]; switch (columnIndex) { case 0: return r.getId();
	 * case 1: return r.getName(); case 2: return r.getSpecies(); } return "";
	 * 
	 * }
	 * 
	 * public String getColumnName(int column) { return columnNames[column]; } }
	 */
}
