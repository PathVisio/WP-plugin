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

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.pathvisio.core.model.Pathway;
import org.pathvisio.wpclient.WikiPathwaysClientPlugin;
import org.pathvisio.wpclient.panels.CreatePathwayPanel;

/**
 * action that enabled the upload of a new pathway
 * @author mkutmon, SravanthiSinah
 */
public class UploadAction extends AbstractAction {

	private WikiPathwaysClientPlugin plugin;
	
	public UploadAction(WikiPathwaysClientPlugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(plugin.getDesktop().getSwingEngine().getCurrentOrganism() == null) {
			JOptionPane.showMessageDialog(plugin.getDesktop().getFrame(), "Please specify organism by double clicking on info box\n in the top left corner of the pathway before uploading.",
					"Error", JOptionPane.ERROR_MESSAGE);
		} else {
			if(!isEmpty(plugin.getDesktop().getSwingEngine().getEngine().getActivePathway())){
				if(plugin.getPathwayID().equals("")) {
					new CreatePathwayPanel(plugin);
				} else {
					int dialogResult = JOptionPane.showConfirmDialog (plugin.getDesktop().getFrame(), "Your pathway was loaded through the plugin.\nAre you sure that you want to create a new pathway on WikiPathways?\n\nIf not, please click 'No' and select the 'Plugins -> WikiPathways plugin -> Update' option.","Warning",JOptionPane.YES_NO_CANCEL_OPTION);
					if(dialogResult == JOptionPane.YES_OPTION){
						new CreatePathwayPanel(plugin);
					}
				}
			} else {
				JOptionPane.showMessageDialog(plugin.getDesktop().getFrame(), "Please add some content to your pathway before uploading it.",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private boolean isEmpty(Pathway p) {
		if(p.getDataObjects().size() > 2) {
			return false;
		}
		return true;
	}

}
