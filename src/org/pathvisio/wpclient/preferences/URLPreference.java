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
package org.pathvisio.wpclient.preferences;

import org.pathvisio.core.preferences.Preference;

/**
 * preference that stores the connection URL for
 * the wikipathways webservice
 * @author mkutmon
 */
public enum URLPreference implements Preference {

	CONNECTION_URL(new String("http://www.wikipathways.org/wpi/webservice/webservice.php"));
	
	URLPreference(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	private String defaultValue;

	public String getDefault() {
		return defaultValue;
	}
}
