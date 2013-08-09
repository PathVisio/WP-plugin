package org.pathvisio.wpclient;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
import javax.swing.JTextArea;
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
import org.pathvisio.wikipathways.webservice.WSSearchResult;
import org.wikipathways.client.WikiPathwaysClient;


import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 *	This class creates the content in the Dialog of the SearchByIdentifier TabbedPane of Search 
 */
public class SearchByIdentifierPanel extends JPanel 
{
	WikiPathwaysClientPlugin plugin;
	public static Xref[] xrefs;
	JComboBox clientDropdown;
	java.util.HashMap<String, String> curationtags = new HashMap<String, String>();
	JTable resultTable;
	int i=0;
	private JComboBox curationOpt;
	private JTextArea txtId;
	private JComboBox cbSyscode;
	private JComboBox cbSearchBy;
	private Component symbolOpt;
	private JScrollPane resultspane;
	
	public int flag = 0;
	private JLabel tipLabel;


	public SearchByIdentifierPanel(final WikiPathwaysClientPlugin plugin) 
	{

		this.plugin = plugin;

		setLayout(new BorderLayout());
		

		
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
					JOptionPane.showMessageDialog(SearchByIdentifierPanel.this,	ex.getMessage(), "Error",JOptionPane.ERROR_MESSAGE);
					Logger.log.error("Error searching WikiPathways", ex);
				}
			}

		};
		
	
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
	

	
		txtId = new JTextArea(2,2);
	
		cbSyscode = new JComboBox(new DataSourceModel());

		JPanel searchReferenceBox = new JPanel();
		FormLayout layout2 = new FormLayout(
				"p,3dlu,140px,1dlu,70px,fill:pref,3dlu,fill:pref,30dlu",
				"pref,pref");
		CellConstraints cc2 = new CellConstraints();

		searchReferenceBox.setLayout(layout2);
	
		searchReferenceBox.setBorder(BorderFactory.createTitledBorder(WikiPathwaysClientPlugin.etch,
				"Search By Reference"));

		searchReferenceBox.add(new JLabel("ID"), cc2.xy(1, 1));
		searchReferenceBox.add(new JScrollPane(txtId), cc2.xy(3, 1));
		searchReferenceBox.add(new JLabel("System Code"), cc2.xy(5, 1));
		searchReferenceBox.add(cbSyscode, cc2.xy(6, 1));
		JButton searchButton = new JButton(searchXrefAction);
		searchReferenceBox.add(searchButton, cc2.xy(8, 1));
		tipLabel = new JLabel("Enter DataNode identifiers (semicolon seperated) and choose the codes from dropdown (e.g.: '1234;3949' , 'EntrezGene')");
		tipLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
	
		
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
		searchReferenceBox.add(tipLabel,cc2.xyw(1, 2,9));
		searchOptBox.add(clientDropdown, cc.xy(8, 1));

		if (plugin.getClients().size() < 2)
			clientDropdown.setVisible(false);
		
		searchBox.add(searchReferenceBox, ccf.xyw(1, 4, 8));
		
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
					
						ResultTableModel	model = (ResultTableModel) target.getModel();
						File tmpDir = new File(plugin.getTmpDir(), WikiPathwaysClientPlugin.shortClientName(model.clientName));
						tmpDir.mkdirs();

						plugin.openPathwayWithProgress(plugin.getClients().get(model.clientName),model.getValueAt(row, 0).toString(), 0, tmpDir,xrefs);
					
					
							
					
					}
					catch (Exception ex) 
					{
						JOptionPane.showMessageDialog(SearchByIdentifierPanel.this,ex.getMessage(), "Error",JOptionPane.ERROR_MESSAGE);
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
					String[] xrefids= txtId.getText().split(";");
					 DataSource ds = DataSource.getByFullName(""	+ cbSyscode.getSelectedItem());
					List< Xref> pxXref = new ArrayList<Xref>();int i = 0;
					for (; i < xrefids.length; i++) {
					 pxXref.add( new Xref(xrefids[i],ds	));	
					 System.out.println(pxXref.get(i));
					}
					
					 xrefs = new Xref[i];
					pxXref.toArray(xrefs);
					

					results = client.findPathwaysByXref(xrefs);
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

	
	
	
}
