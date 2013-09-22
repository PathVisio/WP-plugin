package org.pathvisio.wpclient.panels;

import org.pathvisio.wikipathways.webservice.WSSearchResult;

public class WSResult {
 WSSearchResult wsSearchResult;
 int count;

public WSSearchResult getWsSearchResult() {
	return wsSearchResult;
}
public void setWsSearchResult(WSSearchResult wsSearchResult) {
	this.wsSearchResult = wsSearchResult;
}
public int getCount() {
	return count;
}
public void setCount(int count) {
	this.count = count;
}
}
