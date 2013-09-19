package org.pathvisio.wpclient.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.wpclient.panels.CreatePathwayPanel;

public class UploadAction extends AbstractAction {

	private PvDesktop desktop;
	
	public UploadAction(PvDesktop desktop) {
		this.desktop = desktop;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		new CreatePathwayPanel(desktop);
	}

}
