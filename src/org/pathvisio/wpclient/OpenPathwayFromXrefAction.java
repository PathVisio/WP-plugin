package org.pathvisio.wpclient;

import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.bridgedb.Xref;
import org.pathvisio.core.debug.Logger;
import org.pathvisio.core.model.PathwayElement;
import org.wikipathways.client.WikiPathwaysClient;

/**
* This class handles the GUI for the pathway to load 
* Load Pathway into PathVisio on choosing option of "Open Pathway from Wikipathways"
* from right click menu of pathway element (consedering the Xref of the selected element)
* 	@author Sravanthi Sinha
* 	@version 1.0
*/
class OpenPathwayFromXrefAction extends AbstractAction
{
	private PathwayElement elm;
	private WikiPathwaysClient client;
	private WikiPathwaysClientPlugin plugin;
	

	public OpenPathwayFromXrefAction(WikiPathwaysClientPlugin plugin, PathwayElement elm)
	{
		putValue(NAME, "Open pathway from Wikipathways");
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
			int flag=0,flag2=0;
			Xref x = elm.getXref();
			try
			{
				if((x.getDataSource().toString().isEmpty()) ||x.getId().isEmpty() ||(!x.getId().matches(".*\\d.*")) || (!x.getId().startsWith("WP")))
				{
				flag++;
				throw new Exception();
				}
				else
				{
				flag2++;
				
				}
			}
			catch (Exception e)
			{
			Logger.log.error("The Pathway was annotated with an invalid identifier", e);
			JOptionPane.showMessageDialog(null,"The Pathway is annotated with an invalid identifier", "ERROR", JOptionPane.ERROR_MESSAGE);
			}
			if(flag==0)
			{
			 File tmpDir= new File(plugin.getTmpDir(), x.getDataSource().getFullName());
			 tmpDir.mkdirs();
			 plugin.openPathwayWithProgress(client, x.getId(), 0, tmpDir);					
			}
			else
			{File tmpDir= new File(plugin.getTmpDir(), x.getDataSource().getFullName());
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
