package org.tno.wpclient;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

import org.bridgedb.bio.Organism;
import org.pathvisio.core.debug.Logger;
import org.pathvisio.core.util.ProgressKeeper;
import org.pathvisio.gui.DataSourceModel;
import org.pathvisio.gui.ProgressDialog;
import org.pathvisio.wikipathways.webservice.WSSearchResult;
import org.wikipathways.client.WikiPathwaysClient;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class SearchPanel extends JPanel {
	WikiPathwaysClientPlugin plugin;
	JTextField searchField;
	JComboBox clientDropdown;
	private JComboBox organism;
	JTable resultTable;
	int i;
	private JTextField txtSpecies;
	final private JComboBox cbSearchBy;
	private JTextField txtName;
	private JTextField txtPLabel;
	private JScrollPane resultspane;
	Border etch = BorderFactory.createEtchedBorder();

	public SearchPanel(final WikiPathwaysClientPlugin plugin) {
		this.plugin = plugin;

		setLayout(new BorderLayout());

		Action searchAction = new AbstractAction("Search") {
			public void actionPerformed(ActionEvent e) {
				try {
					resultspane.setBorder(BorderFactory.createTitledBorder(
							etch, "Pathways"));
					search();
				} catch (Exception ex) {
					JOptionPane
							.showMessageDialog(SearchPanel.this,
									ex.getMessage(), "Error",
									JOptionPane.ERROR_MESSAGE);
					Logger.log.error("Error searching WikiPathways", ex);
				}
			}
		};

		txtName = new JTextField();
		txtName.setToolTipText("Enter the Name of the pathway");
		txtPLabel = new JTextField();
		txtPLabel.setToolTipText("Enter any label of elements in the pathway");
		DefaultFormBuilder NameOptBuilder = new DefaultFormBuilder(
				new FormLayout("pref, 4dlu, fill:pref:grow"));
		NameOptBuilder.append("Name:", txtName);

		JPanel NameOpt = NameOptBuilder.getPanel();
		DefaultFormBuilder LabelOptBuilder = new DefaultFormBuilder(
				new FormLayout("pref, 4dlu, fill:pref:grow"));
		LabelOptBuilder.append("Element Label", txtPLabel);
		JPanel LabelOpt = LabelOptBuilder.getPanel();

		txtSpecies = new JTextField();

		organism = new JComboBox(Organism.values());
		DefaultFormBuilder idOptBuilder = new DefaultFormBuilder(
				new FormLayout(
						"p, 3dlu,right:pref,3dlu, fill:pref:grow, p, 3dlu"));
		idOptBuilder.append("Species:", organism);
		idOptBuilder.append(txtSpecies);

		JPanel idOpt = idOptBuilder.getPanel();

		final JPanel opts = new JPanel();
		final CardLayout optCards = new CardLayout();
		opts.setLayout(optCards);
		opts.add(NameOpt, "Name");
		opts.add(idOpt, "Species");
		opts.add(LabelOpt, "Element Label");

		JPanel searchOptBox = new JPanel();
		FormLayout layout = new FormLayout(
				"p,3dlu,fill:pref,3dlu,fill:pref:grow, 3dlu, fill:pref, 3dlu,fill:pref", // columns
				"p, 3dlu, p, 3dlu");
		CellConstraints cc = new CellConstraints();

		searchOptBox.setLayout(layout);

		searchOptBox.setBorder(BorderFactory.createTitledBorder(etch,
				"Search options"));

		searchOptBox.add(new JLabel("Search by:"), cc.xy(1, 1));

		cbSearchBy = new JComboBox();
		cbSearchBy.addItem("Name");
		cbSearchBy.addItem("Species");
		cbSearchBy.addItem("Element Label");
		cbSearchBy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				i = cbSearchBy.getSelectedIndex();
				if (i == 0) {
					optCards.show(opts, "Name");
					searchField = txtName;
				} else if (i == 1) {
					optCards.show(opts, "Species");
					searchField = txtSpecies;
				} else {
					optCards.show(opts, "Element Label");
					searchField = txtPLabel;
				}

			}
		});
		searchOptBox.add(cbSearchBy, cc.xy(3, 1));
		searchOptBox.add(opts, cc.xy(5, 1));

		JButton searchButton = new JButton(searchAction);

		searchOptBox.add(searchButton, cc.xy(7, 1));

		Vector<String> clients = new Vector<String>(plugin.getClients()
				.keySet());
		Collections.sort(clients);
		clientDropdown = new JComboBox(clients);
		clientDropdown.setSelectedIndex(0);
		clientDropdown.setRenderer(new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(final JList list,
					final Object value, final int index,
					final boolean isSelected, final boolean cellHasFocus) {
				String strValue = shortClientName(value.toString());

				return super.getListCellRendererComponent(list, strValue,
						index, isSelected, cellHasFocus);

			}
		});
		searchOptBox.add(clientDropdown, cc.xy(9, 1));
		if (plugin.getClients().size() < 2)
			clientDropdown.setVisible(false);

		add(searchOptBox, BorderLayout.NORTH);

		// Center contains table model for results
		resultTable = new JTable();
		resultspane = new JScrollPane(resultTable);

		add(resultspane, BorderLayout.CENTER);
		searchField = txtName;
		searchField.requestDefaultFocus();

		resultTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					JTable target = (JTable) e.getSource();
					int row = target.getSelectedRow();
					SearchTableModel model = (SearchTableModel) target
							.getModel();

					File tmpDir = new File(plugin.getTmpDir(),
							shortClientName(model.clientName));
					tmpDir.mkdirs();
					try {
						plugin.openPathwayWithProgress(
								plugin.getClients().get(model.clientName),
								model.getValueAt(row, 0).toString(), 0, tmpDir);
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(SearchPanel.this,
								ex.getMessage(), "Error",
								JOptionPane.ERROR_MESSAGE);
						Logger.log.error("Error", ex);
					}
				}
			}
		});
	}

	private String shortClientName(String clientName) {
		Pattern pattern = Pattern.compile("http://(.*?)/");
		Matcher matcher = pattern.matcher(clientName);
		if (matcher.find())
			clientName = matcher.group(1);
		return clientName;
	}

	private void search() throws RemoteException, InterruptedException,
			ExecutionException {
		final String query = searchField.getText();
		String clientName = clientDropdown.getSelectedItem().toString();

		final WikiPathwaysClient client = plugin.getClients().get(clientName);

		final ProgressKeeper pk = new ProgressKeeper();
		final ProgressDialog d = new ProgressDialog(
				JOptionPane.getFrameForComponent(this), "", pk, true, true);

		SwingWorker<WSSearchResult[], Void> sw = new SwingWorker<WSSearchResult[], Void>() {
			protected WSSearchResult[] doInBackground() throws Exception {
				pk.setTaskName("Searching");
				WSSearchResult[] results = null;
				try {
					if (i == 1)
						results = client
								.findPathwaysByText(query, Organism
										.valueOf(organism.getSelectedItem()
												.toString()));

					else {

						results = client.findPathwaysByText(query);
					}

				} catch (Exception e) {
					throw e;
				} finally {
					pk.finished();
				}
				return results;
			}
		};

		sw.execute();
		d.setVisible(true);
		resultTable.setModel(new SearchTableModel(sw.get(), clientName));
		resultTable.setRowSorter(new TableRowSorter(resultTable.getModel()));
	}

	private class SearchTableModel extends AbstractTableModel {
		WSSearchResult[] results;
		String[] columnNames = new String[] { "ID", "Name", "Species" };
		String clientName;

		public SearchTableModel(WSSearchResult[] results, String clientName) {
			this.clientName = clientName;
			this.results = results;
		}

		public int getColumnCount() {
			return 3;
		}

		public int getRowCount() {
			return results.length;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			WSSearchResult r = results[rowIndex];
			switch (columnIndex) {
			case 0:
				return r.getId();
			case 1:
				return r.getName();
			case 2:
				return r.getSpecies();
			}
			return "";
		}

		public String getColumnName(int column) {
			return columnNames[column];
		}
	}
}
