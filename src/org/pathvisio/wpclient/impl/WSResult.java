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

import org.pathvisio.wikipathways.webservice.WSSearchResult;

/**
 * object stores the results of search by xref list
 * for each pathway it stores how many of the query xrefs 
 * is in the pathway
 * @author mkutmon
 *
 */
public class WSResult implements Comparable<WSResult>{
	
	private WSSearchResult wsSearchResult;
	private Integer count;

	//////////////////////////////////////
	// SETTERS & GETTERS
	//////////////////////////////////////
	
	public WSSearchResult getWsSearchResult() {
		return wsSearchResult;
	}

	public void setWsSearchResult(WSSearchResult wsSearchResult) {
		this.wsSearchResult = wsSearchResult;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public int compareTo(WSResult res) {
		return res.getCount().compareTo(getCount());
	}
}
