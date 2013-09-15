package org.pathvisio.wpclient.models;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Image;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.table.AbstractTableModel;
import javax.xml.rpc.ServiceException;

import org.pathvisio.wikipathways.webservice.WSCurationTag;
import org.pathvisio.wikipathways.webservice.WSPathwayInfo;
import org.pathvisio.wpclient.WikiPathwaysClientPlugin;
import org.pathvisio.wpclient.panels.BrowsePanel;
import org.wikipathways.client.WikiPathwaysClient;

/**
	 * This class creates the BrowseTableModel Based on the Browse Criteria
	 */
	public class BrowseTableModel extends AbstractTableModel {
		Set<WSPathwayInfo> results;
		String[] columnNames = new String[] { "ID", "Name", "Species",
				"Curation Tag" };
		HashMap<String, WSCurationTag[]> imagetags = new HashMap<String, WSCurationTag[]>();
		final WikiPathwaysClient client;
		public String clientName;

		public BrowseTableModel(Set<WSPathwayInfo> wsCurationTags,
				String clientName2) throws MalformedURLException,
				ServiceException {
			client = WikiPathwaysClientPlugin.loadClient();
			clientName = client.toString();
			this.clientName = clientName2;
			this.results = wsCurationTags;
			getCurationTags(wsCurationTags);
			
		}

		public int getColumnCount() {
			return 4;
		}

		public Class getColumnClass(int column) {
			return getValueAt(3, column).getClass();
		}

		public int getRowCount() {
			return results.size();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			WSPathwayInfo r = (WSPathwayInfo) results.toArray()[rowIndex];

			switch (columnIndex) {
			case 0:
				return r.getId();
			case 1:
				return r.getName();
			case 2:
				return r.getSpecies();
			case 3: {
				String IMG_SEARCH = "";

				JPanel p = new JPanel();
				p.setLayout(new FlowLayout(FlowLayout.CENTER, 2, 1));

				ImageIcon icon;

				// storing images of the curated tags belonging to certain pathways in Panel
				for (WSCurationTag tag : imagetags.get(r.getId())) 
				{

					if (BrowsePanel.tags.containsKey(tag.getName())) 
					{
						IMG_SEARCH = "resources/" + BrowsePanel.tags.get(tag.getName())+ ".png";
						URL url = this.getClass().getClassLoader().getResource(IMG_SEARCH);
						icon = new ImageIcon(url);

						Image img = icon.getImage();
						Image newimg = img.getScaledInstance(15, 10,java.awt.Image.SCALE_SMOOTH);//SCALING THE IMAGE
						ImageIcon newIcon = new ImageIcon(newimg);

						JLabel l = new JLabel(newIcon);

						p.setBackground(Color.white);
						p.add(l);

					}
				}
				return p;

			}
			}
			
			return "";
		}

		public String getColumnName(int column) {
			return columnNames[column];
		}

	
	public void getCurationTags(Set<WSPathwayInfo> tags)
	{
		Iterator<WSPathwayInfo> itr = tags.iterator();
		while (itr.hasNext())
	{
		String sid = itr.next().getId();
		try 
		{
			WSCurationTag[] ptags = client.getCurationTags(sid);
			imagetags.put(sid, ptags);
		}
		catch (RemoteException e)
		{
			
		}
		
	}
	
	}}

