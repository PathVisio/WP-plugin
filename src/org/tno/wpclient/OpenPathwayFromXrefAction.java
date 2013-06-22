package org.tno.wpclient;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.bridgedb.Xref;
import org.pathvisio.core.debug.Logger;
import org.pathvisio.core.model.PathwayElement;
import org.wikipathways.client.WikiPathwaysClient;

class OpenPathwayFromXrefAction extends AbstractAction {
	private PathwayElement elm;
	private WikiPathwaysClient client;
	private WikiPathwaysClientPlugin plugin;
	
	public OpenPathwayFromXrefAction(WikiPathwaysClientPlugin plugin, PathwayElement elm) {
		putValue(NAME, "Open pathway from " + elm.getXref().getDataSource().getFullName());
		this.plugin = plugin;
		this.elm = elm;
	}

	public void setClient(WikiPathwaysClient client) {
		this.client = client;
	}
	
	public void actionPerformed(ActionEvent evt) {
		try {
			int id=0;
			Xref x = elm.getXref();
			try{
				if(!(x.getDataSource().toString().isEmpty())){
				id=Integer.parseInt(x.getId().toString()); // to check wheter the id is a NUMBER
				}
			
			}catch (Exception e)
			{
			Logger.log.error("The Pathway was annotated with an invalid identifier", e);
			 JOptionPane.showMessageDialog(null,"The Pathway is annotated with an invalid identifier", "ERROR", JOptionPane.ERROR_MESSAGE);
			}
			if(id>0)
			{
			 File tmpDir= new File(plugin.getTmpDir(), x.getDataSource().getFullName());
			 tmpDir.mkdirs();
			 plugin.openPathwayWithProgress(client, x.getId(), 0, tmpDir);					
			} 
		} catch (Exception e) 
		{
			Logger.log.error("Error while loading pathway from remote database", e);
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}