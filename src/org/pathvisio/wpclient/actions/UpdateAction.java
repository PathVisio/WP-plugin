package org.pathvisio.wpclient.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.wpclient.panels.UpdatePathwayPanel;

public class UpdateAction extends AbstractAction {

	private PvDesktop desktop;
	
	public UpdateAction(PvDesktop desktop) {
		this.desktop = desktop;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		new UpdatePathwayPanel(desktop);
	}

}
