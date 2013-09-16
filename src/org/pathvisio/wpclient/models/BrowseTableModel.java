package org.pathvisio.wpclient.models;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Image;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;

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
	 * This class creates the BrowseTableModel 
	 * Based on the Browse Criteria
	 */
	public class BrowseTableModel extends AbstractTableModel 
	{
		WSPathwayInfo[] results;
		String[] columnNames = new String[] { "ID", "Name", "Species","Curation Tag" };
		HashMap<String, WSCurationTag[]> imagetags = new HashMap<String, WSCurationTag[]>();
		
		final WikiPathwaysClient client;
		public String clientName;
		
		public BrowseTableModel(WSPathwayInfo[] results,String clientName) throws MalformedURLException, ServiceException, RemoteException 
		{
			client = WikiPathwaysClientPlugin.loadClient();
			clientName = client.toString();
			this.clientName = clientName;
			putImagetags(results);
			this.results = results;
		}

		private void putImagetags(WSPathwayInfo[] results) throws RemoteException {
		for (int i = 0; i < results.length; i++) {
			String sid = results[i].getId();
			WSCurationTag[] ptags = client.getCurationTags(sid);
			imagetags.put(sid, ptags);
		}
		
			
		}

		public int getColumnCount() 
		{
			return 4;
		}

		public Class getColumnClass(int column)
        {
            for (int row = 0; row < getRowCount(); row++)
            {
                Object o = getValueAt(row, column);

                if (o != null)
                {
                    return o.getClass();
                }
            }

            return Object.class;
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

						JLabel	l = new JLabel(newIcon);

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

	}
