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
package org.pathvisio.wpclient.models;

import org.pathvisio.wikipathways.webservice.WSPathwayInfo;

/**
 * class stores the browse result and the curation tags
 * for each pathways
 * @author mkutmon
 *
 */
public class BrowseResult {

	private WSPathwayInfo pathway;
//	private List<WSCurationTag> tags;
	
	
	public BrowseResult(WSPathwayInfo pathway) {
		this.pathway = pathway;  
//		tags = new ArrayList<WSCurationTag>();
	}

	public WSPathwayInfo getPathway() {
		return pathway;
	}
	
	public void setPathway(WSPathwayInfo pathway) {
		this.pathway = pathway;
	}
	
//	public List<WSCurationTag> getTags() {
//		return tags;
//	}
}
