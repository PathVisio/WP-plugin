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
package org.pathvisio.wpclient.models;

import java.util.List;

import javax.swing.table.AbstractTableModel;

		
/**
 * This class creates the BrowseTableModel 
 * Based on the results
 * @author Sravanthi Sinha
 * @author mkutmon
 */
public class BrowseTableModel extends AbstractTableModel {
	
	// TODO: CURATION TAGS are currently disabled to improve performance

	private List<BrowseResult> results;
	private String[] columnNames = new String[] { "ID", "Name", "Species"//,"Curation Tag" 
			};
	
	// private HashMap<String, List<WSCurationTag>> imagetags = new HashMap<String, List<WSCurationTag>>();
		
	public BrowseTableModel(List<BrowseResult> results) {
//		putImagetags(results);
		this.results = results;
	}

//	private void putImagetags(List<BrowseResult> results) {
//		for(BrowseResult res : results) {
//			imagetags.put(res.getPathway().getId(), res.getTags());
//		}
//	}

	public int getColumnCount() {
//		return 4;
		return 3;
	}

	public Class getColumnClass(int column) {
		for (int row = 0; row < getRowCount(); row++) {
			Object o = getValueAt(row, column);
			if (o != null) {
				return o.getClass();
			}
		}
		return Object.class;
	}
		
	public int getRowCount() {
		return results.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		BrowseResult r = results.get(rowIndex);
		
		switch (columnIndex) {
			case 0:
				return r.getPathway().getId();
			case 1:
				return r.getPathway().getName();
			case 2:
				return r.getPathway().getSpecies();
//			case 3: {
//				String IMG_SEARCH = "";
//
//				JPanel p = new JPanel();
//				p.setLayout(new FlowLayout(FlowLayout.CENTER, 2, 1));
//
//				ImageIcon icon;
//
//				// storing images of the curated tags belonging to certain pathways in Panel
//				for (WSCurationTag tag : imagetags.get(r.getPathway().getId())) 
//				{
//
//					if (BrowsePanel.tagImages.containsKey(tag.getName())) 
//					{
//						IMG_SEARCH = "resources/" + BrowsePanel.tagImages.get(tag.getName())+ ".png";
//						URL url = this.getClass().getClassLoader().getResource(IMG_SEARCH);
//						icon = new ImageIcon(url);
//						
//						Image img = icon.getImage();
//						Image newimg = img.getScaledInstance(15, 10,java.awt.Image.SCALE_SMOOTH);//SCALING THE IMAGE
//						ImageIcon newIcon = new ImageIcon(newimg);
//
//						JLabel	l = new JLabel(newIcon);
//
//						p.setBackground(Color.white);
//						p.add(l);
//
//					}
//				}
//				return p;
//			}
		}
		return "";
	}

	public String getColumnName(int column) {
		return columnNames[column];
	}
}
