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
import java.util.List;
import java.util.Set;

import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.bridgedb.bio.Organism;
import org.pathvisio.core.model.ConverterException;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.core.util.ProgressKeeper;
import org.pathvisio.wikipathways.webservice.WSCurationTag;
import org.pathvisio.wikipathways.webservice.WSPathway;
import org.pathvisio.wikipathways.webservice.WSPathwayInfo;
import org.pathvisio.wikipathways.webservice.WSSearchResult;

/**
 * 
 * Interface for WP queries that can be used by other plugins
 * that depend on the WP Client plugin
 * 
 * @author mkutmon
 *
 */
public interface IWPQueries {
	
	public List<String> listOrganisms (ProgressKeeper pk) throws RemoteException, FailedConnectionException;
	
	public Set<WSPathwayInfo> browseAll(ProgressKeeper pk) throws RemoteException, FailedConnectionException;

	public Set<WSPathwayInfo> browseByOrganism(Organism organism , ProgressKeeper pk) throws RemoteException, FailedConnectionException;
	
	public Set<WSPathwayInfo> browseByCurationTag(String curationTag , ProgressKeeper pk) throws RemoteException, FailedConnectionException;
	
	public Set<WSPathwayInfo> browseByOrganismAndCurationTag(Organism organism, String curationTag , ProgressKeeper pk) throws RemoteException, FailedConnectionException;
	
	public WSSearchResult[] findByText(String text, ProgressKeeper pk) throws RemoteException, FailedConnectionException;

	public Set<WSCurationTag> getCurationTags(String pwId, ProgressKeeper pk) throws RemoteException, FailedConnectionException;

	public WSPathway getPathway(String id, Integer revision, ProgressKeeper pk) throws RemoteException, FailedConnectionException, ConverterException;

	public WSSearchResult[] findByTextInOrganism(String text, Organism organism, ProgressKeeper pk) throws RemoteException, FailedConnectionException;

	public WSSearchResult[] findByLiteratureReference(String reference, ProgressKeeper pk) throws RemoteException, FailedConnectionException;

	public void login(String username, String password) throws RemoteException, FailedConnectionException;

	public WSPathwayInfo uploadPathway(Pathway pathway) throws RemoteException, FailedConnectionException, ConverterException;

	public void updatePathway(Pathway pathway, String id, Integer revision, String description) throws RemoteException, FailedConnectionException, ConverterException;

	public void updateCurationTag(String tag, String id, String description, int revision) throws RemoteException, FailedConnectionException, ConverterException;
	public WSPathwayInfo getPathwayInfo(String id, ProgressKeeper pk) throws RemoteException, FailedConnectionException, ConverterException;

	public WSSearchResult[] findByXref(Xref[] xrefs, ProgressKeeper pk) throws RemoteException, FailedConnectionException, ConverterException;

	public String[] getXrefList(String pwId, DataSource ds, ProgressKeeper pk) throws RemoteException, FailedConnectionException;
}
