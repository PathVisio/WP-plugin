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
import javax.swing.JTextField;
import javax.swing.SwingWorker;
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
 * This class creates the content in the Search Panel which includes floolwomg functionalities
 * Basic Search
 * Search by query as on http://www.wikipathways.org itself.
 * 1.Search for pathways by name, pathway element labels
 * 2.Search by text but for specific species
 * 	@author Sravanthi Sinha
 * 	@version 1.0
 */
public class SearchPanel extends JPanel 
{
	private WikiPathwaysClientPlugin plugin;
	private JTextField searchField;
	private JComboBox clientDropdown;
	private JComboBox organismOpt;
	private JTable resultTable;
	private JScrollPane resultspane;
	private JLabel tipLabel;
	
	public SearchPanel(final WikiPathwaysClientPlugin plugin) 
	{
		this.plugin = plugin;

		setLayout(new BorderLayout());

		Action searchAction = new AbstractAction("Search")
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					resultspane.setBorder(BorderFactory.createTitledBorder(WikiPathwaysClientPlugin.etch, "Pathways"));
					search();
				}
				catch (Exception ex) 
				{
					JOptionPane.showMessageDialog(SearchPanel.this,ex.getMessage(), "Error",JOptionPane.ERROR_MESSAGE);
					Logger.log.error("Error searching WikiPathways", ex);
				}
			}
		};

		searchField = new JTextField();
		searchField.setToolTipText("Enter any search query (e.g. 'Apoptosis' or 'P53').");

		tipLabel = new JLabel("Tip: use AND, OR, *, ?, parentheses or quotes (e.g.: 'Apoptosis or P53' , 'DNA*')");
		tipLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
		
		//preparing List Of Organisms to load in species combobox
		List<String> org = new ArrayList<String>();
		org.add("ALL SPECIES");
		org.addAll(1, Organism.latinNames());
		organismOpt = new JComboBox(org.toArray());

		DefaultFormBuilder idOptBuilder = new DefaultFormBuilder(new FormLayout("right:pref, 3dlu,right:pref"));
		idOptBuilder.append("Species:", organismOpt);
		
		final JPanel opts = new JPanel();
		final CardLayout optCards = new CardLayout();
		opts.setLayout(optCards);

		JPanel idOpt = idOptBuilder.getPanel();
		opts.add(idOpt, "Species");

		
		JPanel searchOptBox = new JPanel();
		FormLayout layout = new FormLayout("p,3dlu,150px,3dlu,fill:pref:grow,3dlu,fill:pref:grow,3dlu","p, pref, p, 2dlu");
		CellConstraints cc = new CellConstraints();

		searchOptBox.setLayout(layout);
		searchOptBox.setBorder(BorderFactory.createTitledBorder(WikiPathwaysClientPlugin.etch,"Search options"));
		searchOptBox.add(new JLabel("Search For:"), cc.xy(1, 1));
		searchOptBox.add(searchField, cc.xy(3, 1));
		searchOptBox.add(opts, cc.xy(5, 1));
		
		JButton searchButton = new JButton(searchAction);
		searchOptBox.add(searchButton, cc.xy(7, 1));

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
		
		searchOptBox.add(clientDropdown, cc.xy(8, 1));
		searchOptBox.add(tipLabel,cc.xyw(1, 2,8));
		if (plugin.getClients().size() < 2)
			clientDropdown.setVisible(false);

		add(searchOptBox, BorderLayout.NORTH);

		//prepare result Table
		resultTable = new JTable();
		resultspane = new JScrollPane(resultTable);

		add(resultspane, BorderLayout.CENTER);

		searchField.requestDefaultFocus();

		resultTable.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e) 
			{
				//on double click
				if (e.getClickCount() == 2) 
				{
					JTable target = (JTable) e.getSource();
					int row = target.getSelectedRow();
					ResultTableModel model = (ResultTableModel) target.getModel();

					File tmpDir = new File(plugin.getTmpDir(),WikiPathwaysClientPlugin.shortClientName(model.clientName));
					tmpDir.mkdirs();
					
					try
					{
						plugin.openPathwayWithProgress(plugin.getClients().get(model.clientName),model.getValueAt(row, 0).toString(), 0, tmpDir);
					}
					catch (Exception ex) 
					{
						JOptionPane.showMessageDialog(SearchPanel.this,ex.getMessage(), "Error",JOptionPane.ERROR_MESSAGE);
						Logger.log.error("Error", ex);
					}
				}
			}
		});
	}

	
	/**
	 * Search method for-
	 * Search for pathways by name, pathway element labels
	 * Search of pathways belonging to specific species
	 */
	private void search() throws RemoteException, InterruptedException,ExecutionException 
	{
		final String query = searchField.getText();
	
		if(!query.isEmpty())
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
							if (organismOpt.getSelectedItem().toString().equalsIgnoreCase("ALL SPECIES")) 
							{
								results = client.findPathwaysByText(query);
							}
							else
								results = client.findPathwaysByText(query, Organism.fromLatinName(organismOpt.getSelectedItem().toString()));

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
	 else
	 {
	  JOptionPane.showMessageDialog(null,"Please Enter a Search Query", "ERROR", JOptionPane.ERROR_MESSAGE);
	 }
	}
	
}
