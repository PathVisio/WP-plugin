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
package org.pathvisio.wpclient.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.bridgedb.Xref;
import org.pathvisio.core.model.PathwayElement;
import org.pathvisio.wpclient.WikiPathwaysClientPlugin;

/**
* This class handles the GUI for the pathway to load 
* Load Pathway into PathVisio on choosing option of "Open Pathway from WikiPathways"
* from right click menu of pathway element (consedering the Xref of the selected element)
* 	@author Sravanthi Sinha
* 	@author mkutmon
*/
public class OpenPathwayFromXrefAction extends AbstractAction {
	private PathwayElement elm;
	private WikiPathwaysClientPlugin plugin;
	
	public OpenPathwayFromXrefAction(WikiPathwaysClientPlugin plugin, PathwayElement elm) {
		this.plugin = plugin;
		this.elm = elm;
		if(elm.getDataNodeType().equals("Pathway")) {
			putValue(NAME, "Open Pathway from WikiPathways");
		} else  {
			putValue(NAME, "Find pathways containing " + elm.getXref());
		}
	}
	
	public void actionPerformed(ActionEvent evt) {
		Xref xref = elm.getXref();
		if(xref.getDataSource() != null && xref.getId() != null) {
			// pathway nodes - open pathway from wikipathways
			if(elm.getDataNodeType().equals("Pathway")) {
				try{
					if(xref.getId().startsWith("WP")) {
						File tmpDir= new File(plugin.getTmpDir(), xref.getDataSource().getSystemCode() + "_" + xref.getId());
						tmpDir.mkdirs();
						plugin.openPathwayWithProgress(xref.getId(), 0, tmpDir);
					} 
				} catch(Exception ex) {
					JOptionPane.showMessageDialog(plugin.getDesktop().getFrame(), "Can not open pathway from WikiPathways.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			// other datanodes - search for pathways containing the same datanode
			} else {
				try {
					File tmpDir= new File(plugin.getTmpDir(), xref.getDataSource().getSystemCode() + "_" + xref.getId());
					tmpDir.mkdirs();
					plugin.openPathwayXrefWithProgress(xref, 0, tmpDir);
				} catch(Exception e) {
					JOptionPane.showMessageDialog(plugin.getDesktop().getFrame(), "Can not open pathway from WikiPathways.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		} else {
			JOptionPane.showMessageDialog(plugin.getDesktop().getFrame(), "Error occured when searching for pathways.\nPlease check annotation of the selected element.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
