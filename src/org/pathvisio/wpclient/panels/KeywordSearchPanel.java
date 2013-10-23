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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.TableRowSorter;
import javax.xml.rpc.ServiceException;

import org.bridgedb.bio.Organism;
import org.pathvisio.core.debug.Logger;
import org.pathvisio.core.util.ProgressKeeper;
import org.pathvisio.gui.ProgressDialog;
import org.pathvisio.wikipathways.webservice.WSSearchResult;
import org.pathvisio.wpclient.WikiPathwaysClientPlugin;
import org.pathvisio.wpclient.models.ResultTableModel;
import org.pathvisio.wpclient.utils.FileUtils;
import org.wikipathways.client.WikiPathwaysClient;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * This class creates the content in the Search Panel which includes following
 * functionalities Basic Search Search by query as on
 * http://www.wikipathways.org itself. 1.Search for pathways by name, pathway
 * element labels 2.Search by text but for specific species
 * 
 * @author Sravanthi Sinha
 * @author Martina Kutmon
 * @version 1.0
 */
public class KeywordSearchPanel extends JPanel {
	private WikiPathwaysClientPlugin plugin;
	private JTextField searchField;
	private JComboBox organismOpt;
	private JTable resultTable;
	private JScrollPane resultspane;
	private JLabel tipLabel;
	private JLabel lblNumFound;

	public KeywordSearchPanel(final WikiPathwaysClientPlugin plugin) throws MalformedURLException, ServiceException, RemoteException {
		this.plugin = plugin;
		final WikiPathwaysClient client = WikiPathwaysClientPlugin
				.loadClient();
		setLayout(new BorderLayout());

		Action searchAction = new AbstractAction("Search") {
			public void actionPerformed(ActionEvent e) {
				try {
					resultspane.setBorder(BorderFactory.createTitledBorder(
							BorderFactory.createEtchedBorder(), "Pathways"));
					search();
				} catch (Exception ex) {
					JOptionPane
							.showMessageDialog(KeywordSearchPanel.this,
									ex.getMessage(), "Error",
									JOptionPane.ERROR_MESSAGE);
					Logger.log.error("Error searching WikiPathways", ex);
				}
			}
		};

		searchField = new JTextField();
		searchField
				.setToolTipText("Enter any search query (e.g. 'Apoptosis' or 'P53').");
		searchField.addActionListener(searchAction);
		tipLabel = new JLabel(
				"Tip: use AND, OR, *, ?, parentheses or quotes (e.g.: 'Apoptosis or P53' , 'DNA*')");
		tipLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));

		// preparing List Of Organisms to load in species combobox
		List<String> org = new ArrayList<String>();

		// preparing organism list for combobox
		org.add("ALL SPECIES");
		String[] organisms = client.listOrganisms();
		List<String> organismslist= new ArrayList<String>(Arrays.asList(organisms));
		org.addAll(1, organismslist);

		organismOpt = new JComboBox(org.toArray());
		organismOpt.addActionListener(searchAction);
		DefaultFormBuilder idOptBuilder = new DefaultFormBuilder(
				new FormLayout("right:pref, 3dlu,right:pref"));
		idOptBuilder.append("Species:", organismOpt);

		final JPanel opts = new JPanel();
		final CardLayout optCards = new CardLayout();
		opts.setLayout(optCards);

		JPanel idOpt = idOptBuilder.getPanel();
		opts.add(idOpt, "Species");

		JPanel searchOptBox = new JPanel();
		FormLayout layout = new FormLayout(
				"p,3dlu,150px,3dlu,pref:grow,3dlu",
				"p, pref, p, 2dlu");
		CellConstraints cc = new CellConstraints();

		searchOptBox.setLayout(layout);
		searchOptBox.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Search options"));
		searchOptBox.add(new JLabel("Search For:"), cc.xy(1, 1));
		searchOptBox.add(searchField, cc.xy(3, 1));
		searchOptBox.add(opts, cc.xy(5, 1));

	
		searchOptBox.add(tipLabel, cc.xyw(1, 2, 6));

		add(searchOptBox, BorderLayout.NORTH);

		// prepare result Table
		resultTable = new JTable();
		resultspane = new JScrollPane(resultTable);

		add(resultspane, BorderLayout.CENTER);
		lblNumFound = new JLabel();
		add (lblNumFound, BorderLayout.SOUTH);
		searchField.requestDefaultFocus();

		resultTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				// on double click
				if (e.getClickCount() == 2) {
					JTable target = (JTable) e.getSource();
					int row = target.getSelectedRow();
					ResultTableModel model = (ResultTableModel) target
							.getModel();

					File tmpDir = new File(plugin.getTmpDir(), FileUtils.getTimeStamp());
					tmpDir.mkdirs();

					try {
						plugin.openPathwayWithProgress(
								WikiPathwaysClientPlugin.loadClient(),
								model.getValueAt(row, 0).toString(), 0, tmpDir);
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(KeywordSearchPanel.this,
								ex.getMessage(), "Error",
								JOptionPane.ERROR_MESSAGE);
						Logger.log.error("Error", ex);
					}
				}
			}
		});
		
	}

	/**
	 * Search method for- Search for pathways by name, pathway element labels
	 * Search of pathways belonging to specific species
	 * 
	 * @throws ServiceException
	 * @throws MalformedURLException
	 */
	private void search() throws RemoteException, InterruptedException,
			ExecutionException, MalformedURLException, ServiceException {
		lblNumFound.setText("");
		final String query = searchField.getText();

		if (!query.isEmpty()) {

			final WikiPathwaysClient client = WikiPathwaysClientPlugin
					.loadClient();
			final ProgressKeeper pk = new ProgressKeeper();
			final ProgressDialog d = new ProgressDialog(
					JOptionPane.getFrameForComponent(this), "", pk, true, true);

			SwingWorker<WSSearchResult[], Void> sw = new SwingWorker<WSSearchResult[], Void>() {
				WSSearchResult[] results ;

				protected WSSearchResult[] doInBackground() throws Exception {
					pk.setTaskName("Starting Search");

					try {
						if (organismOpt.getSelectedItem().toString()
								.equalsIgnoreCase("ALL SPECIES")) {
							pk.setTaskName("Searching in "
									+ organismOpt.getSelectedItem().toString());
							results = client.findPathwaysByText(query);

						} else
							pk.setTaskName("Searching");
						results = client.findPathwaysByText(query, Organism
								.fromLatinName(organismOpt.getSelectedItem()
										.toString()));

					} catch (Exception e) {
						throw e;
					} finally {
						pk.finished();
					}
					return results;
				}

				protected void done() {
					if (!pk.isCancelled()) {
						if (results.length == 0) {
							JOptionPane.showMessageDialog(null,
									"0 results found");
						}
					} else if (pk.isCancelled()) {
						pk.finished();
					}
				}
			};

			sw.execute();
			d.setVisible(true);

			resultTable.setModel(new ResultTableModel(sw.get()));
			resultTable
					.setRowSorter(new TableRowSorter(resultTable.getModel()));
			lblNumFound.setText(" No.of results found: "+sw.get().length);
		} else {
			JOptionPane.showMessageDialog(null, "Please Enter a Search Query",
					"ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}

}
