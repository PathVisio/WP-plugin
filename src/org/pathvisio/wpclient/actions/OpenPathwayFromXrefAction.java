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
package org.pathvisio.wpclient.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.bridgedb.Xref;
import org.pathvisio.core.debug.Logger;
import org.pathvisio.core.model.PathwayElement;
import org.pathvisio.wpclient.WikiPathwaysClientPlugin;
import org.wikipathways.client.WikiPathwaysClient;

/**
* This class handles the GUI for the pathway to load 
* Load Pathway into PathVisio on choosing option of "Open Pathway from WikiPathways"
* from right click menu of pathway element (consedering the Xref of the selected element)
* 	@author Sravanthi Sinha
* 	@author Martina Kutmon
* 	@version 1.0
*/
public class OpenPathwayFromXrefAction extends AbstractAction
{
	private PathwayElement elm;
	private WikiPathwaysClient client;
	private WikiPathwaysClientPlugin plugin;
	

	public OpenPathwayFromXrefAction(WikiPathwaysClientPlugin plugin, PathwayElement elm)
	{
		putValue(NAME, "Open pathway from WikiPathways");
		this.plugin = plugin;
		this.elm = elm;
	}

	public void setClient(WikiPathwaysClient client)
	{
		this.client = client;
	}
	
	public void actionPerformed(ActionEvent evt)
	{
		try
		{
			int flag=0;
			Xref x = elm.getXref();
			try
			{
				if((x.getDataSource().toString().isEmpty()) ||x.getId().isEmpty())
				{				
				throw new Exception();
				}
				if((x.getId().matches(".*\\d.*")) && (x.getId().startsWith("WP")))
				{
				flag++;
				}
				
			}
			catch (Exception e)
			{
			Logger.log.error("The Pathway was annotated with an invalid identifier", e);
			JOptionPane.showMessageDialog(null,"The Pathway is annotated with an invalid identifier", "ERROR", JOptionPane.ERROR_MESSAGE);
			}
			if(flag>0)
			{
			 File tmpDir= new File(plugin.getTmpDir(), x.getDataSource().getFullName());
			 tmpDir.mkdirs();
			 plugin.openPathwayWithProgress(client, x.getId(), 0, tmpDir);					
			}
			else
			{
			File tmpDir= new File(plugin.getTmpDir(), x.getDataSource().getFullName());
			 tmpDir.mkdirs();
			 plugin.openPathwayXrefWithProgress(client, x, 0, tmpDir);
				
			}
		} 
		catch (Exception e) 
		{
			Logger.log.error("Error while loading pathway from remote database", e);
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
