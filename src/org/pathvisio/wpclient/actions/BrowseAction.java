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

import org.pathvisio.wpclient.WikiPathwaysClientPlugin;
import org.pathvisio.wpclient.dialogs.BrowseDialog;

/**
 * This class has the different methods for the Browse Action Browse include-
 * Browse ALL Browse by Organism, Browse by Curation Tags, Browse by Collections,
 * Browse by Organism and Collections, Browse by Organism and Curtaion Tags
 * Browse by Collections and CUrtaion Tags, Browse by Organism and Collections
 * and CUrtaion Tags
 * 
 * @author Sravanthi Sinha
 * @author mkutmon
 */
public class BrowseAction extends AbstractAction {

	private WikiPathwaysClientPlugin plugin;

	public BrowseAction(WikiPathwaysClientPlugin plugin) {
		this.plugin = plugin;
		putValue(NAME, "Browse");
		putValue(SHORT_DESCRIPTION, "Browse Pathways in WikiPathways");
	}

	public void actionPerformed(ActionEvent e) {
		new BrowseDialog(plugin);
	}
}
