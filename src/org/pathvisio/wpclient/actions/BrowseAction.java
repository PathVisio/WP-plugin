package org.pathvisio.wpclient.actions;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bridgedb.bio.Organism;
import org.pathvisio.wikipathways.webservice.WSCurationTag;
import org.pathvisio.wikipathways.webservice.WSPathwayInfo;
import org.wikipathways.client.WikiPathwaysClient;

public class BrowseAction {
	
	public Set<WSPathwayInfo> browseAll(WikiPathwaysClient client) {
		Set<WSPathwayInfo> set = new HashSet<WSPathwayInfo>();
		
		try {
			WSPathwayInfo [] result = client.listPathways();
			set.addAll(Arrays.asList(result));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return set;
	}
	
	public Set<WSPathwayInfo> browseByOrganism(WikiPathwaysClient client, Organism organism) {
		Set<WSPathwayInfo> set = new HashSet<WSPathwayInfo>();
		try {
			WSPathwayInfo [] result = client.listPathways(organism);
			set.addAll(Arrays.asList(result));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return set;		
	}
	
	public Set<WSPathwayInfo> browseByCurationTag(WikiPathwaysClient client, String curationTag) {
		Set<WSPathwayInfo> set = new HashSet<WSPathwayInfo>();

		try {
			WSCurationTag[] result = client.getCurationTagsByName(curationTag);
			for(WSCurationTag tag : result) {
				set.add(tag.getPathway());
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return set;
	}
	
	public Set<WSPathwayInfo> browseByCollection(WikiPathwaysClient client, String collection) {
		return browseByCurationTag(client, collection);
	}
	
	public Set<WSPathwayInfo> browseByOrganismAndCurationTag(WikiPathwaysClient client, Organism organism, String curationTag) {
		Set<WSPathwayInfo> set = new HashSet<WSPathwayInfo>();
		
		Set<WSPathwayInfo> pwyCurTag = browseByCurationTag(client, curationTag);
		for(WSPathwayInfo info : pwyCurTag) {
			if(info.getSpecies().equals(organism.latinName())) {
				set.add(info);
			}
		}
		return set;
	}
	
	public Set<WSPathwayInfo> browseByCollectionAndCurationTag(WikiPathwaysClient client, String collection, String curationTag) {
		Set<WSPathwayInfo> set = new HashSet<WSPathwayInfo>();
		
		Set<WSPathwayInfo> pwyCurTag = browseByCurationTag(client, curationTag);
		Set<WSPathwayInfo> pwyCollection = browseByCollection(client, collection);
		for(WSPathwayInfo info : pwyCurTag) {
			if(pwyCollection.contains(info)) {
				set.add(info);
			}
		}
		return set;
	}
	
	public Set<WSPathwayInfo> browseByOrganismAndCollection(WikiPathwaysClient client, Organism organism, String collection) {
		Set<WSPathwayInfo> set = new HashSet<WSPathwayInfo>();
		
		Set<WSPathwayInfo> pwyCurTag = browseByCollection(client, collection);
		for(WSPathwayInfo info : pwyCurTag) {
			if(info.getSpecies().equals(organism.latinName())) {
				set.add(info);
			}
		}
		return set;
	}
	
	public Set<WSPathwayInfo> browseByOrganismAndCollectionAndCurationTag(WikiPathwaysClient client, Organism organism, String collection, String curationTag) {
		Set<WSPathwayInfo> set = new HashSet<WSPathwayInfo>();
		
		Set<WSPathwayInfo> pwyCurTag = browseByCurationTag(client, curationTag);
		Set<WSPathwayInfo> pwyCollection = browseByCollection(client, collection);
		for(WSPathwayInfo info : pwyCurTag) {
			if(pwyCollection.contains(info)) {
				if(info.getSpecies().equals(organism.latinName())) {	
					set.add(info);
				}
			}
		}
		return set;
	}
}
