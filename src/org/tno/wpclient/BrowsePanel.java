package org.tno.wpclient;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;
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
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;


import org.bridgedb.bio.Organism;
import org.pathvisio.core.debug.Logger;
import org.pathvisio.wikipathways.webservice.WSSearchResult;



import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class BrowsePanel extends JPanel {
	WikiPathwaysClientPlugin plugin;

	JComboBox clientDropdown;
	private JComboBox organismOpt;
	JTable resultTable;
	int i;

	private JScrollPane resultspane;
	Border etch = BorderFactory.createEtchedBorder();
	private JLabel speciesLabel,catLabel,CollecLabel,CuraLabel;

	private JComboBox collOpt;

	private JComboBox categoryOpt;

	private JComboBox curationOpt;



	public BrowsePanel(final WikiPathwaysClientPlugin plugin) 
	{
		this.plugin = plugin;

		setLayout(new BorderLayout());

		Action browseAction = new AbstractAction("Browse")
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					
					browse();
				}
				catch (Exception ex) 
				{
					JOptionPane.showMessageDialog(BrowsePanel.this,ex.getMessage(), "Error",JOptionPane.ERROR_MESSAGE);
					Logger.log.error("Error searching WikiPathways", ex);
				}
			}
		};

		
	speciesLabel= new JLabel("Species:");
	catLabel= new JLabel("Categories:");
	CollecLabel= new JLabel("Collections:");
	CuraLabel= new JLabel("Curation Tags:");
	
	// species combo box
	java.util.List<String> org = new ArrayList<String>();
	
	org.add("ALL SPECIES");
	org.addAll(1, Organism.latinNames());
	
	
	organismOpt = new JComboBox(org.toArray());
	organismOpt.setSelectedItem(Organism.HomoSapiens.latinName());

	DefaultFormBuilder idOptBuilder = new DefaultFormBuilder(new FormLayout("right:pref, 3dlu,right:pref"));
	idOptBuilder.append(organismOpt);
	
	final JPanel opts = new JPanel();
	final CardLayout optCards = new CardLayout();
	opts.setLayout(optCards);

	JPanel idOpt = idOptBuilder.getPanel();
	opts.add(idOpt, "Species");

	//colllections combo box
	java.util.List<String> coll = new ArrayList<String>();

	coll.add("Curated pathways");
	coll.add("Featured pathways");
	coll.add("GenMAPP pathways");
	coll.add("CIRM pathways");
	coll.add("Reactome pathways");
	coll.add("Open Access pathways");
	coll.add("WormBase pathways");
	coll.add("Wikipedia pathways");
	coll.add("All pathways");
	
	collOpt = new JComboBox(coll.toArray());

	DefaultFormBuilder colOptBuilder = new DefaultFormBuilder(new FormLayout("right:pref, 3dlu,right:pref"));
	colOptBuilder.append(collOpt);
	
	 JPanel opts2 = new JPanel();
	 final CardLayout opt2Cards = new CardLayout();
	opts2.setLayout(opt2Cards);

	JPanel collOpt = colOptBuilder.getPanel();
	opts2.add(collOpt, "Collections");
	
	//Category COmboBox

	java.util.List<String> cat = new ArrayList<String>();

	cat.add("Cellular Process");
	cat.add("Metabolic Process");
	cat.add("Molecular Function");
	cat.add("Physiological Process");
	
	cat.add("All Categories");
	cat.add("Un Categoriesed");
	
	
	categoryOpt = new JComboBox(cat.toArray());
	categoryOpt.setSelectedItem("All Categories");
	DefaultFormBuilder catOptBuilder = new DefaultFormBuilder(new FormLayout("right:pref, 3dlu,right:pref"));
	catOptBuilder.append(categoryOpt);
	
	final JPanel opts3 = new JPanel();
	final CardLayout opt3Cards = new CardLayout();
	opts3.setLayout(opt3Cards);

	JPanel catOpt = catOptBuilder.getPanel();
	opts3.add(catOpt, "Category");
	
	//Curation Combobox
	
	java.util.List<String> curationtags = new ArrayList<String>();

	curationtags.add("Not specified");
	curationtags.add("Missing Annotations");
	curationtags.add("unconnected lineS");
	curationtags.add("under construction");	
	curationtags.add("stub");
	curationtags.add("needs work");
	
	
	curationOpt = new JComboBox(curationtags.toArray());
	curationOpt.setSelectedItem("Not specified");
	DefaultFormBuilder curationOptBuilder = new DefaultFormBuilder(new FormLayout("right:pref, 3dlu,right:pref"));
	curationOptBuilder.append(curationOpt);
	
	final JPanel opts4 = new JPanel();
	final CardLayout opt4Cards = new CardLayout();
	opts4.setLayout(opt4Cards);

	JPanel curOpt = curationOptBuilder.getPanel();
	opts4.add(curOpt, "Curation Tags");
	
	JPanel browseOptBox = new JPanel();
	FormLayout layout = new FormLayout("left:pref,6dlu,left:pref,6dlu,left:pref,6dlu,left:pref,6dlu,left:pref,6dlu","p,24dlu, p, 2dlu");
	CellConstraints cc = new CellConstraints();

	browseOptBox.setLayout(layout);
	browseOptBox.setBorder(BorderFactory.createTitledBorder(etch,"Browse options"));
	browseOptBox.add(speciesLabel, cc.xy(1, 1));
	browseOptBox.add(CollecLabel, cc.xy(3, 1));
	browseOptBox.add(catLabel, cc.xy(5, 1));
	browseOptBox.add(CuraLabel, cc.xy(7, 1));
	browseOptBox.add(opts, cc.xy(1, 2));
	browseOptBox.add(opts2, cc.xy(3, 2));
	browseOptBox.add(opts3, cc.xy(5, 2));
	browseOptBox.add(opts4, cc.xy(7, 2));
	JButton BrowseButton = new JButton(browseAction);
	browseOptBox.add(BrowseButton, cc.xy(9, 2));
	add(browseOptBox, BorderLayout.CENTER);
	
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
				SearchTableModel model = (SearchTableModel) target.getModel();

				File tmpDir = new File(plugin.getTmpDir(),SearchPanel.shortClientName(model.clientName));
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

	

	

	protected void browse() {
		// TODO Auto-generated method stub
		
	}


	private class SearchTableModel extends AbstractTableModel
	{
		WSSearchResult[] results;
		String[] columnNames = new String[] { "Name"};
		String clientName;

		public SearchTableModel(WSSearchResult[] results, String clientName) 
		{
			this.clientName = clientName;
			this.results = results;
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
