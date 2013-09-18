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
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;

import org.bridgedb.bio.Organism;
import org.pathvisio.core.util.ProgressKeeper;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.wikipathways.webservice.WSCurationTag;
import org.pathvisio.wikipathways.webservice.WSPathwayInfo;
import org.pathvisio.wpclient.WikiPathwaysClientPlugin;
import org.pathvisio.wpclient.dialogs.BrowseDialog;
import org.wikipathways.client.WikiPathwaysClient;

public class BrowseAction extends AbstractAction {

	PvDesktop desktop;
	private WikiPathwaysClientPlugin plugin;

	public BrowseAction(PvDesktop desktop, WikiPathwaysClientPlugin plugin) {
		this.desktop = desktop;
		this.plugin = plugin;
		putValue(NAME, "Browse");
		putValue(SHORT_DESCRIPTION, "Browse pathways in Wikipathways");
	}

	public void actionPerformed(ActionEvent e) {
		new BrowseDialog(desktop, plugin);
	}

	public Set<WSPathwayInfo> browseAll(WikiPathwaysClient client, ProgressKeeper pk) {
		Set<WSPathwayInfo> set = new HashSet<WSPathwayInfo>();

		try {
			pk.setTaskName("Browsing WikiPathways");
			WSPathwayInfo[] result = client.listPathways();
			set.addAll(Arrays.asList(result));
		} catch (RemoteException e) {
			
			e.printStackTrace();
		}
		return set;
	}

	public Set<WSPathwayInfo> browseByOrganism(WikiPathwaysClient client,
			Organism organism , ProgressKeeper pk) {
		Set<WSPathwayInfo> set = new HashSet<WSPathwayInfo>();
		try {
			pk.setTaskName("Browsing through Organisms");
			WSPathwayInfo[] result = client.listPathways(organism);
			set.addAll(Arrays.asList(result));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return set;
	}

	public Set<WSPathwayInfo> browseByCurationTag(WikiPathwaysClient client,
			String curationTag , ProgressKeeper pk) {
		Set<WSPathwayInfo> set = new HashSet<WSPathwayInfo>();

		try {
			
			WSCurationTag[] result = client.getCurationTagsByName(curationTag);
			for (WSCurationTag tag : result) {
				set.add(tag.getPathway());
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return set;
	}

	public Set<WSPathwayInfo> browseByCollection(WikiPathwaysClient client,
			String collection, ProgressKeeper pk) {
		pk.setTaskName("Browsing through Collections");
		return browseByCurationTag(client, collection,pk);
	}

	public Set<WSPathwayInfo> browseByOrganismAndCurationTag(
			WikiPathwaysClient client, Organism organism, String curationTag , ProgressKeeper pk) {
		Set<WSPathwayInfo> set = new HashSet<WSPathwayInfo>();
		pk.setTaskName("Browsing through CurationTags");
		Set<WSPathwayInfo> pwyCurTag = browseByCurationTag(client, curationTag,pk);
		pk.setTaskName("Filtering ");
		for (WSPathwayInfo info : pwyCurTag) {
			if (info.getSpecies().equals(organism.latinName())) {
				set.add(info);
			}
		}
		return set;
	}

	public Set<WSPathwayInfo> browseByCollectionAndCurationTag(
			WikiPathwaysClient client, String collection, String curationTag, ProgressKeeper pk) {
		Set<WSPathwayInfo> set = new HashSet<WSPathwayInfo>();
		pk.setTaskName("Browsing through Curation Tags");
		Set<WSPathwayInfo> pwyCurTag = browseByCurationTag(client, curationTag,pk);
		pk.setTaskName("Browsing through Collections");
		Set<WSPathwayInfo> pwyCollection = browseByCollection(client,
				collection,pk);
		pk.setTaskName("Filtering ");
		for (WSPathwayInfo info : pwyCurTag) {
			if (pwyCollection.contains(info)) {
				set.add(info);
			}
		}
		return set;
	}

	public Set<WSPathwayInfo> browseByOrganismAndCollection(
			WikiPathwaysClient client, Organism organism, String collection, ProgressKeeper pk) {
		Set<WSPathwayInfo> set = new HashSet<WSPathwayInfo>();
		pk.setTaskName("Browsing through Collections");
		Set<WSPathwayInfo> pwyCurTag = browseByCollection(client, collection,pk);
		pk.setTaskName("Filtering ");
		for (WSPathwayInfo info : pwyCurTag) {
			if (info.getSpecies().equals(organism.latinName())) {
				set.add(info);
			}
		}
		return set;
	}

	public Set<WSPathwayInfo> browseByOrganismAndCollectionAndCurationTag(
			WikiPathwaysClient client, Organism organism, String collection,
			String curationTag, ProgressKeeper pk) {
		Set<WSPathwayInfo> set = new HashSet<WSPathwayInfo>();
		pk.setTaskName("Browsing through Curation Tags");
		Set<WSPathwayInfo> pwyCurTag = browseByCurationTag(client, curationTag,  pk);
		pk.setTaskName("Browsing through Collections");
		Set<WSPathwayInfo> pwyCollection = browseByCollection(client,
				collection,pk);
		pk.setTaskName("Filtering ");
		for (WSPathwayInfo info : pwyCurTag) {
			if (pwyCollection.contains(info)) {
				if (info.getSpecies().equals(organism.latinName())) {
					set.add(info);
				}
			}
		}
		return set;
	}
}
