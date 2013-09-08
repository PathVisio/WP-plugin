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
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
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
import javax.xml.rpc.ServiceException;

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
 *	This class creates the content in the Dialog 
 *  of the SearchByIdentifier TabbedPane of Search
 *  @author Sravanthi Sinha
 *  @author Martina Kutmon
 * 	@version 1.0  
 */
public class SearchByIdentifierPanel extends JPanel 
{
	WikiPathwaysClientPlugin plugin;
	public static Xref[] xrefs;

	java.util.HashMap<String, String> curationtags = new HashMap<String, String>();
	JTable resultTable;
	int i=0;
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
	
		
	

		searchReferenceBox.add(tipLabel,cc2.xyw(1, 2,9));
	

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

						plugin.openPathwayWithProgress(WikiPathwaysClientPlugin.loadClient(),model.getValueAt(row, 0).toString(), 0, tmpDir,xrefs);
					
					
							
					
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

	private void searchByXref() throws RemoteException, InterruptedException,ExecutionException, MalformedURLException, ServiceException 
	{
		

		final WikiPathwaysClient client = WikiPathwaysClientPlugin.loadClient();

		final ProgressKeeper pk = new ProgressKeeper();
		final ProgressDialog d = new ProgressDialog(JOptionPane.getFrameForComponent(this), "", pk, true, true);
	
		SwingWorker<WSSearchResult[], Void> sw = new SwingWorker<WSSearchResult[], Void>()
		{
			WSSearchResult[] results;
			protected WSSearchResult[] doInBackground() throws Exception
			{
				pk.setTaskName("Starting Search");
				
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
					
					pk.setTaskName("Searching ");
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
			protected void done() {
				if(!pk.isCancelled())
				{
					if(results.length==0)
					{
						 JOptionPane.showMessageDialog(null,"0 results found");
					}
				}
				else if(pk.isCancelled())
				{
					pk.finished();
				}
			}
		};

		sw.execute();
		d.setVisible(true);

		resultTable.setModel(new ResultTableModel(sw.get(), client.toString()));
		resultTable.setRowSorter(new TableRowSorter(resultTable.getModel()));
	}

	
	
	
}
