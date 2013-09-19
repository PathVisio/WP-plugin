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

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.pathvisio.desktop.plugin.Plugin;
import org.pathvisio.wpclient.impl.WPQueries;

/**
 * OSGi activator class for the WikiPathways Plugin
 * @author Sravanthi Sinha
 * @author Martina Kutmon
 */
public class Activator implements BundleActivator{
	
	private WikiPathwaysClientPlugin plugin;

	@Override
	public void start(BundleContext context) throws Exception {
		plugin = new WikiPathwaysClientPlugin();
		context.registerService(Plugin.class.getName(), plugin, null);
		
		// provide API for wikipathways queries
		// other plugins that depend on this plugin can use this API to
		// query data on wikipathways
		IWPQueries wpQueries = new WPQueries();
		context.registerService(IWPQueries.class.getName(), wpQueries, null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin.done();
	}
}
