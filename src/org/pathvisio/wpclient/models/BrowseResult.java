package org.pathvisio.wpclient.models;

import java.util.ArrayList;
import java.util.List;

import org.pathvisio.wikipathways.webservice.WSCurationTag;
import org.pathvisio.wikipathways.webservice.WSPathwayInfo;

public class BrowseResult {

	private WSPathwayInfo pathway;
	private List<WSCurationTag> tags;
	
	
	public BrowseResult(WSPathwayInfo pathway) {
		this.pathway = pathway;  
		tags = new ArrayList<WSCurationTag>();
	}

	public WSPathwayInfo getPathway() {
		return pathway;
	}
	
	public void setPathway(WSPathwayInfo pathway) {
		this.pathway = pathway;
	}
	
	public List<WSCurationTag> getTags() {
		return tags;
	}
}
