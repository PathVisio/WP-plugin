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

import java.net.URL;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

import javax.xml.rpc.ServiceException;

import org.bridgedb.bio.Organism;
import org.pathvisio.core.util.ProgressKeeper;
import org.pathvisio.wikipathways.webservice.WSCurationTag;
import org.pathvisio.wikipathways.webservice.WSPathwayInfo;
import org.pathvisio.wikipathways.webservice.WSSearchResult;
import org.wikipathways.client.WikiPathwaysClient;

/**
 * 
 * Interface for WP queries that can be used by other plugins
 * that depend on the WP Client plugin
 * 
 * @author mkutmon
 *
 */
public interface IWPQueries {

	// which wikipathways webservice should be used
	// main site: http://www.wikipathways.org/wpi/webservice/webservice.php
	// returns true if successful
	public void initialize(String url) throws FailedConnectionException;
	
	public List<String> listOrganisms (ProgressKeeper pk) throws RemoteException, FailedConnectionException;
	
	public Set<WSPathwayInfo> browseAll(ProgressKeeper pk) throws RemoteException, FailedConnectionException;

	public Set<WSPathwayInfo> browseByOrganism(Organism organism , ProgressKeeper pk) throws RemoteException, FailedConnectionException;
	
	public Set<WSPathwayInfo> browseByCurationTag(String curationTag , ProgressKeeper pk) throws RemoteException, FailedConnectionException;
	
	public Set<WSPathwayInfo> browseByOrganismAndCurationTag(Organism organism, String curationTag , ProgressKeeper pk) throws RemoteException, FailedConnectionException;
	
	public WSSearchResult[] findByText(String text, ProgressKeeper pk) throws RemoteException, FailedConnectionException;

	public Set<WSCurationTag> getCurationTags(String pwId, ProgressKeeper pk) throws RemoteException, FailedConnectionException;
	
	// TODO: add all search queries
	// TODO: add all update/upload queries
}
