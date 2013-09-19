package org.pathvisio.wpclient;

import java.rmi.RemoteException;
import java.util.Set;

import org.bridgedb.bio.Organism;
import org.pathvisio.core.util.ProgressKeeper;
import org.pathvisio.wikipathways.webservice.WSPathwayInfo;
import org.pathvisio.wikipathways.webservice.WSSearchResult;
import org.wikipathways.client.WikiPathwaysClient;

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
