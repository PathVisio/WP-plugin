package org.pathvisio.wpclient;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

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
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.pathvisio.core.debug.Logger;
import org.pathvisio.core.util.ProgressKeeper;
import org.pathvisio.gui.DataSourceModel;
import org.pathvisio.gui.ProgressDialog;
import org.pathvisio.wikipathways.webservice.WSIndexField;
import org.pathvisio.wikipathways.webservice.WSPathwayInfo;
import org.pathvisio.wikipathways.webservice.WSSearchResult;
import org.wikipathways.client.WikiPathwaysClient;


import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 *	This class creates the content in the Dialog of the AdvancesSearch TabbedPane of Search 
 */
public class AdvancedSearchPanel extends JPanel 
{
	WikiPathwaysClientPlugin plugin;
	JTextField pTitleOrId;
	JComboBox clientDropdown;
	java.util.HashMap<String, String> curationtags = new HashMap<String, String>();
	JTable resultTable;
	int i=0;
	private JComboBox curationOpt;
	private JTextField txtId;
	private JComboBox cbSyscode;
	private JComboBox cbSearchBy;
	private Component symbolOpt;
	private JScrollPane resultspane;
	
	public int flag = 0;
	private JTextField pubXref;

	public AdvancedSearchPanel(final WikiPathwaysClientPlugin plugin) 
	{

		this.plugin = plugin;

		setLayout(new BorderLayout());
		pTitleOrId = new JTextField();
		pubXref = new JTextField();

		Action searchAction = new AbstractAction("Search")
		{
			public void actionPerformed(ActionEvent e) {
				try {
					resultspane.setBorder(BorderFactory.createTitledBorder(WikiPathwaysClientPlugin.etch, "Pathways"));
					if (pTitleOrId.getText().startsWith("WP"))
						searchID();
					else 
					{
						search();
					}
				}
				catch (Exception ex) 
				{
					JOptionPane.showMessageDialog(AdvancedSearchPanel.this,	ex.getMessage(), "Error",JOptionPane.ERROR_MESSAGE);
					Logger.log.error("Error searching WikiPathways", ex);
				}
			}

		};
		Action searchLiteratureAction = new AbstractAction("searchlit") 
		{
			public void actionPerformed(ActionEvent e)
			{
				try 
				{
					resultspane.setBorder(BorderFactory.createTitledBorder(WikiPathwaysClientPlugin.etch, "Pathways"));
					searchByLiterature();
				} 
				catch (Exception ex)
				{
					JOptionPane.showMessageDialog(AdvancedSearchPanel.this,ex.getMessage(), "Error",JOptionPane.ERROR_MESSAGE);
					Logger.log.error("Error searching WikiPathways", ex);
				}
			}

		};
		Action searchXrefAction = new AbstractAction("Search") 
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					resultspane.setBorder(BorderFactory.createTitledBorder(WikiPathwaysClientPlugin.etch, "Pathways"));
					searchByXref();
				}
				catch (Exception ex) 
				{
					JOptionPane.showMessageDialog(AdvancedSearchPanel.this,	ex.getMessage(), "Error",JOptionPane.ERROR_MESSAGE);
					Logger.log.error("Error searching WikiPathways", ex);
				}
			}

		};
		
		pTitleOrId.addActionListener(searchAction);
		pubXref.addActionListener(searchLiteratureAction);
		JPanel searchBox = new JPanel();
		FormLayout layoutf = new FormLayout("p,3dlu,120px,2dlu,30px,fill:pref:grow,3dlu,fill:pref:grow,3dlu",
				"pref, pref, 4dlu, pref, 4dlu, pref");
		CellConstraints ccf = new CellConstraints();

		searchBox.setLayout(layoutf);
		searchBox.setBorder(BorderFactory.createTitledBorder(WikiPathwaysClientPlugin.etch));

		JPanel searchOptBox = new JPanel();
		FormLayout layout = new FormLayout(
				"p,3dlu,120px,2dlu,30px,fill:pref:grow,3dlu,fill:pref:grow,3dlu",
				"pref, pref, 4dlu, pref, 4dlu, pref");
		CellConstraints cc = new CellConstraints();

		searchOptBox.setLayout(layout);
		searchOptBox.setBorder(BorderFactory.createTitledBorder(WikiPathwaysClientPlugin.etch,
				"Search options"));
		searchOptBox.add(new JLabel("Title/ID"), cc.xy(1, 1));
		searchOptBox.add(pTitleOrId, cc.xyw(1, 2, 3));

		searchOptBox.add(new JLabel("Publication Title/ID"), cc.xy(6, 1));
		searchOptBox.add(new JLabel("(OR)"), cc.xyw(5, 2, 1));
		searchOptBox.add(pubXref, cc.xyw(6, 2, 3));
	
		// NEXT PANEL
		txtId = new JTextField();

		cbSyscode = new JComboBox(new DataSourceModel());

		JPanel searchReferenceBox = new JPanel();
		FormLayout layout2 = new FormLayout(
				"p,3dlu,140px,1dlu,70px,fill:pref:grow,3dlu,fill:pref:grow",
				"pref, pref, 4dlu, pref, 4dlu, pref");
		CellConstraints cc2 = new CellConstraints();

		searchReferenceBox.setLayout(layout2);
	
		searchReferenceBox.setBorder(BorderFactory.createTitledBorder(WikiPathwaysClientPlugin.etch,
				"Search By Reference"));

		searchReferenceBox.add(new JLabel("ID"), cc.xy(1, 1));
		searchReferenceBox.add(txtId, cc2.xy(3, 1));
		searchReferenceBox.add(new JLabel("System Code"), cc.xy(5, 1));
		searchReferenceBox.add(cbSyscode, cc2.xy(6, 1));
		JButton searchButton = new JButton(searchXrefAction);
		searchReferenceBox.add(searchButton, cc2.xy(8, 1));
		Vector<String> clients = new Vector<String>(plugin.getClients()
				.keySet());
		Collections.sort(clients);

		clientDropdown = new JComboBox(clients);
		clientDropdown.setSelectedIndex(0);
		clientDropdown.setRenderer(new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(final JList list,
					final Object value, final int index,
					final boolean isSelected, final boolean cellHasFocus)
			{
				String strValue = WikiPathwaysClientPlugin.shortClientName(value.toString());
				return super.getListCellRendererComponent(list, strValue,
						index, isSelected, cellHasFocus);
			}
		});

		searchOptBox.add(clientDropdown, cc.xy(8, 1));

		if (plugin.getClients().size() < 2)
			clientDropdown.setVisible(false);
		searchBox.add(searchOptBox, ccf.xyw(1, 1, 8));
		searchBox.add(searchReferenceBox, ccf.xyw(1, 4, 8));
		add(searchBox, BorderLayout.NORTH);

		// Center contains table model for results
		resultTable = new JTable();
		resultspane = new JScrollPane(resultTable);

		add(resultspane, BorderLayout.CENTER);

		pTitleOrId.requestDefaultFocus();

		resultTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					JTable target = (JTable) e.getSource();
					int row = target.getSelectedRow();
					
					try
					{
					switch (flag) 
					{
					
					case 0:
					{
						ResultTableModel	model = (ResultTableModel) target.getModel();
						File tmpDir = new File(plugin.getTmpDir(), WikiPathwaysClientPlugin.shortClientName(model.clientName));
						tmpDir.mkdirs();

						plugin.openPathwayWithProgress(plugin.getClients().get(model.clientName),model.getValueAt(row, 0).toString(), 0, tmpDir);
						break;
					}
					case 1:
					{
						SearchTableModel	model = (SearchTableModel) target.getModel();
						File tmpDir = new File(plugin.getTmpDir(), WikiPathwaysClientPlugin.shortClientName(model.clientName));
						tmpDir.mkdirs();

						plugin.openPathwayWithProgress(plugin.getClients().get(model.clientName),model.getValueAt(row, 0).toString(), 0, tmpDir);
						break;
					}
					case 2:
					{
						LiteratureResultTableModel	model = (LiteratureResultTableModel) target.getModel();
						File tmpDir = new File(plugin.getTmpDir(), WikiPathwaysClientPlugin.shortClientName(model.clientName));
						tmpDir.mkdirs();
						
						plugin.openPathwayWithProgress(plugin.getClients().get(model.clientName),model.getValueAt(row, 0).toString(), 0, tmpDir);
						break;
					}
					
					}			
					
					}
					catch (Exception ex) 
					{
						JOptionPane.showMessageDialog(AdvancedSearchPanel.this,ex.getMessage(), "Error",JOptionPane.ERROR_MESSAGE);
						Logger.log.error("Error", ex);
					}
				}
			}
		});
	}

	private void searchByXref() throws RemoteException, InterruptedException,ExecutionException 
	{
		String clientName = clientDropdown.getSelectedItem().toString();

		final WikiPathwaysClient client = plugin.getClients().get(clientName);

		final ProgressKeeper pk = new ProgressKeeper();
		final ProgressDialog d = new ProgressDialog(JOptionPane.getFrameForComponent(this), "", pk, true, true);
	
		SwingWorker<WSSearchResult[], Void> sw = new SwingWorker<WSSearchResult[], Void>()
		{
			protected WSSearchResult[] doInBackground() throws Exception
			{
				pk.setTaskName("Searching");
				WSSearchResult[] results = null;
				try
				{
					Xref pxXref = new Xref(txtId.getText(),	DataSource.getByFullName(""	+ cbSyscode.getSelectedItem()));
					results = client.findPathwaysByXref(pxXref);
				}
				catch (Exception e) 
				{
					throw e;
				}
				finally 
				{
					pk.finished();
				}
				return results;
			}
		};

		sw.execute();
		d.setVisible(true);

		resultTable.setModel(new ResultTableModel(sw.get(), clientName));
		resultTable.setRowSorter(new TableRowSorter(resultTable.getModel()));
	}

	private void searchByLiterature() throws RemoteException,InterruptedException, ExecutionException 
	{
		final String query = pubXref.getText();

		if (!query.isEmpty()) 
		{
			String clientName = clientDropdown.getSelectedItem().toString();
			final WikiPathwaysClient client = plugin.getClients().get(clientName);
			
			final ProgressKeeper pk = new ProgressKeeper();
			final ProgressDialog d = new ProgressDialog(JOptionPane.getFrameForComponent(this), "", pk, true, true);

			SwingWorker<WSSearchResult[], Void> sw = new SwingWorker<WSSearchResult[], Void>() 
			{
				protected WSSearchResult[] doInBackground() throws Exception 
				{
					pk.setTaskName("Searching");
					WSSearchResult[] results = null;
					try 
					{
						results = client.findPathwaysByLiterature(query);
					}
					catch (Exception e) 
					{
						throw e;
					} 
					finally 
					{
						pk.finished();
					}
					return results;
				}
			};

			sw.execute();
			d.setVisible(true);

			resultTable.setModel(new LiteratureResultTableModel(sw.get(), clientName));
			resultTable.setRowSorter(new TableRowSorter(resultTable.getModel()));
		} 
		else
		{
			JOptionPane.showMessageDialog(null, "Please Enter a Search Query","ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}

	
	private void search() throws RemoteException, InterruptedException,
			ExecutionException {
		final String query = pTitleOrId.getText();
		String clientName = clientDropdown.getSelectedItem().toString();

		final WikiPathwaysClient client = plugin.getClients().get(clientName);

		final ProgressKeeper pk = new ProgressKeeper();
		final ProgressDialog d = new ProgressDialog(
				JOptionPane.getFrameForComponent(this), "", pk, true, true);
		i = 0;
		SwingWorker<WSSearchResult[], Void> sw = new SwingWorker<WSSearchResult[], Void>() 
			{
			protected WSSearchResult[] doInBackground() throws Exception 
			{
				pk.setTaskName("Searching");
				WSSearchResult[] results = null;
				ArrayList<WSSearchResult> results2 = new ArrayList<WSSearchResult>();
				try 
				{
					results = client.findPathwaysByText(query);
				} 
				catch (Exception e)
				{
					throw e;
				} finally {
					pk.finished();
				}

				for (WSSearchResult wsSearchResult : results)
				{
					if (wsSearchResult.getName().toUpperCase().indexOf(query.toUpperCase()) != -1) 
					{
						results2.add(wsSearchResult);
						i++;

					}
				}
				results = new WSSearchResult[i];
				results2.toArray(results);
				return results;

			}
		};

		sw.execute();
		d.setVisible(true);
		resultTable.setModel(new ResultTableModel(sw.get(), clientName));
		resultTable.setRowSorter(new TableRowSorter(resultTable.getModel()));
	}

	private void searchID() throws RemoteException, InterruptedException,ExecutionException 
	{
		final String query = pTitleOrId.getText();

		if (!query.isEmpty())
		{
			String clientName = clientDropdown.getSelectedItem().toString();
			final WikiPathwaysClient client = plugin.getClients().get(clientName);
			final ProgressKeeper pk = new ProgressKeeper();
			final ProgressDialog d = new ProgressDialog(JOptionPane.getFrameForComponent(this), "", pk, true, true);

			SwingWorker<WSPathwayInfo[], Void> sw = new SwingWorker<WSPathwayInfo[], Void>()
				{
				protected WSPathwayInfo[] doInBackground() throws Exception 
				{
					i = 0;
					pk.setTaskName("Searching");

					WSPathwayInfo[] results3 = null;

					ArrayList<WSPathwayInfo> results2 = new ArrayList<WSPathwayInfo>();
					try
					{
						results2.add(client.getPathwayInfo(query));
						i++;
					} catch (Exception e) {
						throw e;
					} finally {
						pk.finished();
					}

					results3 = new WSPathwayInfo[i];
					results2.toArray(results3);
					return results3;

				}
			};

			sw.execute();
			d.setVisible(true);

			resultTable.setModel(new SearchTableModel(sw.get(), clientName));
			resultTable
					.setRowSorter(new TableRowSorter(resultTable.getModel()));
		} else {
			JOptionPane.showMessageDialog(null, "Please Enter a Search Query",
					"ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}

	private class LiteratureResultTableModel extends AbstractTableModel
	{
		WSSearchResult[] results;
		String[] columnNames = new String[] { "ID", "Name", "Species",
				"Literature Title" };
		String clientName;

		public LiteratureResultTableModel(WSSearchResult[] results, String clientName) 
		{
			this.clientName = clientName;
			this.results = results;
			flag = 2;
		}

		public int getColumnCount()
		{
			return 4;
		}

		public int getRowCount() 
		{
			return results.length;
		}

		public Object getValueAt(int rowIndex, int columnIndex) 
		{
			WSSearchResult r = results[rowIndex];
			switch (columnIndex) {
			case 0:
				return r.getId();
			case 1:
				return r.getName();
			case 2:
				return r.getSpecies();
			case 3:
			{
			WSIndexField[] fields = r.getFields();
				for (int i = 0; i < fields.length; i++)
				{
					if(fields[i].getName().toString().equals("literature.title"))
						return fields[i].getValues(0).toString();					
				}
				
			}
				
			}
			return "";
		}

		public String getColumnName(int column) 
		{
			return columnNames[column];
		}
	}

	
	private class SearchTableModel extends AbstractTableModel
	{
		WSPathwayInfo[] results;
		String[] columnNames = new String[] { "ID", "Name", "Species" };
		String clientName;

		public SearchTableModel(WSPathwayInfo[] wsPathwayInfos,	String clientName) 
		{
			this.clientName = clientName;
			this.results = wsPathwayInfos;
			flag = 1;
		}

		public int getColumnCount() 
		{
			return 3;
		}

		public int getRowCount() 
		{
			return results.length;
		}

		public Object getValueAt(int rowIndex, int columnIndex)
		{
			WSPathwayInfo r = results[rowIndex];
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

		public String getColumnName(int column)
		{
			return columnNames[column];
		}

	}

}