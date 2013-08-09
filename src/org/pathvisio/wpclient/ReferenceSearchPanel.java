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
 *	This class creates the content in the Dialog of the ReferenceSearch TabbedPane of Search 
 */
public class ReferenceSearchPanel extends JPanel 
{
	WikiPathwaysClientPlugin plugin;
	
	JComboBox clientDropdown;
	
	JTable resultTable;
	int i=0;
	
	private JTextField txtId;
	
	private JScrollPane resultspane;
	
	public int flag = 0;
	private JTextField pubXref;

	public ReferenceSearchPanel(final WikiPathwaysClientPlugin plugin) 
	{

		this.plugin = plugin;

		setLayout(new BorderLayout());
	
		pubXref = new JTextField();

		
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
					JOptionPane.showMessageDialog(ReferenceSearchPanel.this,ex.getMessage(), "Error",JOptionPane.ERROR_MESSAGE);
					Logger.log.error("Error searching WikiPathways", ex);
				}
			}

		};
		
		
		
		pubXref.addActionListener(searchLiteratureAction);
		JPanel searchBox = new JPanel();
		FormLayout layoutf = new FormLayout("p,3dlu,120px,2dlu,30px,fill:pref:grow,3dlu,fill:pref:grow,3dlu",
				"pref, pref, 4dlu, pref, 4dlu, pref");
		CellConstraints ccf = new CellConstraints();

		searchBox.setLayout(layoutf);
		searchBox.setBorder(BorderFactory.createTitledBorder(WikiPathwaysClientPlugin.etch));

		JPanel searchOptBox = new JPanel();
		FormLayout layout = new FormLayout(
				"3dlu,p,3dlu,2dlu,30px,fill:pref:grow,3dlu,fill:pref:grow,3dlu",
				"pref, pref, 4dlu, pref, 4dlu, pref");
		CellConstraints cc = new CellConstraints();

		searchOptBox.setLayout(layout);
		searchOptBox.setBorder(BorderFactory.createTitledBorder(WikiPathwaysClientPlugin.etch,
				"Search options"));
		

		searchOptBox.add(new JLabel("Publication Title/ID"), cc.xy(2, 1));
		searchOptBox.add(pubXref, cc.xyw(4, 1, 5));
	
	
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
		
		add(searchBox, BorderLayout.NORTH);

		// Center contains table model for results
		resultTable = new JTable();
		resultspane = new JScrollPane(resultTable);

		add(resultspane, BorderLayout.CENTER);

	

		resultTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					JTable target = (JTable) e.getSource();
					int row = target.getSelectedRow();
					
					try
					{				
					
						LiteratureResultTableModel	model = (LiteratureResultTableModel) target.getModel();
						File tmpDir = new File(plugin.getTmpDir(), WikiPathwaysClientPlugin.shortClientName(model.clientName));
						tmpDir.mkdirs();
						
					
					}
					catch (Exception ex) 
					{
						JOptionPane.showMessageDialog(ReferenceSearchPanel.this,ex.getMessage(), "Error",JOptionPane.ERROR_MESSAGE);
						Logger.log.error("Error", ex);
					}
				}
			}
		});
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

	
	
}
