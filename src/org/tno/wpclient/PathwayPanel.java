package org.tno.wpclient;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
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
import org.pathvisio.gui.ProgressDialog;
import org.pathvisio.wikipathways.webservice.WSSearchResult;

import org.wikipathways.client.WikiPathwaysClient;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * This class creates the content in the Browse Dialog
 * Basic Search
 * Search by query as on http://www.wikipathways.org itself.
 * 1.Search for pathways by name, pathway element labels
 * 2.Search by text but for specific species
 * 	@author Sravanthi Sinha
 * 	@version 1.0
 */
public class PathwayPanel extends JPanel 
{
	WikiPathwaysClientPlugin plugin;

	JComboBox clientDropdown;

	JTable resultTable;
	

	private JScrollPane resultspane;
	public static	Border etch = BorderFactory.createEtchedBorder();
	private JLabel tipLabel;
	

	public PathwayPanel(final WikiPathwaysClientPlugin plugin, WSSearchResult[] wsp, final File tmpDir) 
	{
		this.plugin = plugin;

		setLayout(new BorderLayout());
		
	

		

		JPanel searchOptBox = new JPanel();
		FormLayout layout = new FormLayout("p,3dlu,150px,3dlu,fill:pref:grow,3dlu,fill:pref:grow,3dlu","p, pref, p, 2dlu");
		CellConstraints cc = new CellConstraints();

	
		
	
		Vector<String> clients = new Vector<String>(plugin.getClients().keySet());
		Collections.sort(clients);
		
		
		clientDropdown = new JComboBox(clients);
		clientDropdown.setSelectedIndex(0);
		clientDropdown.setRenderer(new DefaultListCellRenderer() 
		{
			public Component getListCellRendererComponent(final JList list,final Object value, final int index,final boolean isSelected, final boolean cellHasFocus) 
			{
				String strValue = SearchPanel.shortClientName(value.toString());
				return super.getListCellRendererComponent(list, strValue,index, isSelected, cellHasFocus);
			}
		});
		
		searchOptBox.add(clientDropdown, cc.xy(8, 1));
		
		
		if (plugin.getClients().size() < 2)
			clientDropdown.setVisible(false);

		add(searchOptBox, BorderLayout.NORTH);

		// Center contains table model for results
		resultTable = new JTable();
		String clientName = clientDropdown.getSelectedItem().toString();
		resultTable.setModel(new SearchTableModel(wsp,clientName));
		resultTable.setRowSorter(new TableRowSorter(resultTable.getModel()));
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
					SearchTableModel model = (SearchTableModel) target.getModel();

					
					tmpDir.mkdirs();
					
					try
					{
						plugin.openPathwayWithProgress(plugin.getClients().get(model.clientName),model.getValueAt(row, 0).toString(), 0, tmpDir);
					}
					catch (Exception ex) 
					{
						JOptionPane.showMessageDialog(PathwayPanel.this,ex.getMessage(), "Error",JOptionPane.ERROR_MESSAGE);
						Logger.log.error("Error", ex);
					}
				}
			}
		});
	}


	
	

	/**
	 * This class creates the SearchTableModel 
	 * Based on the Search Criteria
	 */
	private class SearchTableModel extends AbstractTableModel
	{
		WSSearchResult[] results;
		String[] columnNames = new String[] { "ID", "Name", "Species" };
		private String clientName;
		

		public SearchTableModel(WSSearchResult[] results, String clientName ) 
		{
			
			this.results = results;
			this.clientName= clientName;
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

		public String getColumnName(int column) 
		{
			return columnNames[column];
		}
	}
}
