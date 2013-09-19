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
package org.pathvisio.wpclient;

import java.rmi.RemoteException;
import java.util.Set;

import org.bridgedb.bio.Organism;
import org.pathvisio.core.util.ProgressKeeper;
import org.pathvisio.wikipathways.webservice.WSPathwayInfo;
import org.pathvisio.wikipathways.webservice.WSSearchResult;
import org.wikipathways.client.WikiPathwaysClient;

/**
 * 
 * Interface for WP queries that can be used by other plugins
 * that depend on the WP Client plugin
 * 
 * @author Martina Kutmon
 *
 */
public interface IWPQueries {

	public String [] listOrganisms (WikiPathwaysClient client, ProgressKeeper pk) throws RemoteException;
	
	public Set<WSPathwayInfo> browseAll(WikiPathwaysClient client, ProgressKeeper pk) throws RemoteException;

	public Set<WSPathwayInfo> browseByOrganism(WikiPathwaysClient client,
				Organism organism , ProgressKeeper pk) throws RemoteException;
	
	public Set<WSPathwayInfo> browseByCurationTag(WikiPathwaysClient client,
			String curationTag , ProgressKeeper pk) throws RemoteException;
	
	public Set<WSPathwayInfo> browseByOrganismAndCurationTag(
			WikiPathwaysClient client, Organism organism, String curationTag , ProgressKeeper pk) throws RemoteException;
	
	public WSSearchResult[] findByText(WikiPathwaysClient client, String text, ProgressKeeper pk) throws RemoteException;
	
	// TODO: add all search queries
	// TODO: add all update/upload queries
}
