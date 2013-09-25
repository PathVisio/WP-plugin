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
package org.pathvisio.wpclient.impl;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bridgedb.bio.Organism;
import org.pathvisio.core.util.ProgressKeeper;
import org.pathvisio.wikipathways.webservice.WSCurationTag;
import org.pathvisio.wikipathways.webservice.WSPathwayInfo;
import org.pathvisio.wikipathways.webservice.WSSearchResult;
import org.pathvisio.wpclient.IWPQueries;
import org.wikipathways.client.WikiPathwaysClient;

/**
 * WP Queries implementation
 * These functions can be used by other plugins by using the
 * OSGi service IWPQueries
 * 
 * @author Martina Kutmon
 *
 */
public class WPQueries implements IWPQueries {

	@Override
	public Set<WSPathwayInfo> browseAll(WikiPathwaysClient client, ProgressKeeper pk) throws RemoteException {
		Set<WSPathwayInfo> set = new HashSet<WSPathwayInfo>();

		pk.setTaskName("Browsing WikiPathways");
		WSPathwayInfo[] result = client.listPathways();
		set.addAll(Arrays.asList(result));
		
		return set;
	}

	@Override
	public Set<WSPathwayInfo> browseByOrganism(WikiPathwaysClient client, Organism organism, ProgressKeeper pk) throws RemoteException {
		Set<WSPathwayInfo> set = new HashSet<WSPathwayInfo>();

		pk.setTaskName("Browse WikiPathways");
		pk.report("Get pathways for species " + organism.latinName());
		
		WSPathwayInfo[] result = client.listPathways(organism);
		set.addAll(Arrays.asList(result));
		
		return set;
	}

	@Override
	public Set<WSPathwayInfo> browseByCurationTag(WikiPathwaysClient client, String curationTag, ProgressKeeper pk) throws RemoteException {
		Set<WSPathwayInfo> set = new HashSet<WSPathwayInfo>();

		pk.setTaskName("Browse WikiPathways");
		pk.report("Get pathways with curation tag: " + curationTag);
		
		WSCurationTag[] result = client.getCurationTagsByName(curationTag);
		for (WSCurationTag tag : result) {
			set.add(tag.getPathway());
		}
		
		return set;
	}

	@Override
	public Set<WSPathwayInfo> browseByOrganismAndCurationTag(WikiPathwaysClient client, Organism organism, String curationTag, ProgressKeeper pk) throws RemoteException {
		Set<WSPathwayInfo> set = new HashSet<WSPathwayInfo>();
		
		pk.setTaskName("Browse WikiPathways");
		pk.report("Get pathways with curation tag " + curationTag);
		Set<WSPathwayInfo> pwyCurTag = browseByCurationTag(client, curationTag,pk);
		
		pk.report("Filter pathways for species " + organism.latinName());
		
		for (WSPathwayInfo info : pwyCurTag) {
			if (info.getSpecies().equals(organism.latinName())) {
				set.add(info);
			}
		}
		
		return set;
	}

	@Override
	public String[] listOrganisms(WikiPathwaysClient client, ProgressKeeper pk) throws RemoteException {
		pk.setTaskName("Test connection to WikiPathways");
		pk.report("Get list of organisms from WikiPathways");
		String [] organisms = client.listOrganisms();
		return organisms;
	}

	@Override
	public WSSearchResult[] findByText(WikiPathwaysClient client, String text, ProgressKeeper pk) {
		// TODO Auto-generated method stub
		return null;
	}

}
