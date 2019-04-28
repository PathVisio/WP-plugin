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
package org.pathvisio.wpclient.dialogs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.xml.rpc.ServiceException;

import org.pathvisio.core.debug.Logger;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.core.util.ProgressKeeper;
import org.pathvisio.gui.ProgressDialog;
import org.pathvisio.wikipathways.webservice.WSCurationTag;
import org.pathvisio.wikipathways.webservice.WSPathwayInfo;
import org.pathvisio.wpclient.FailedConnectionException;
import org.pathvisio.wpclient.WikiPathwaysClientPlugin;
import org.pathvisio.wpclient.panels.LoginPanel;
import org.pathvisio.wpclient.utils.FileUtils;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * JPanel 
 * @author martinakutmon
 *
 */
public class UpdatePathwayDialog implements ActionListener {
	LoginPanel p;
	JDialog d,d2;
	private JTextArea description = new JTextArea(2, 2);
	private WikiPathwaysClientPlugin plugin;

	public UpdatePathwayDialog(WikiPathwaysClientPlugin plugin) {
		this.plugin = plugin;
		if (LoginPanel.username.equals("") || LoginPanel.password.equals("")) {
			showLoginPanel();
		}
		if (!(LoginPanel.username.equals("") && LoginPanel.password.equals(""))){
			showDescriptionPanel();
		}
	}

	private void showDescriptionPanel() {
		FormLayout layout = new FormLayout(
				"7dlu,150px,fill:pref,150px",
				"pref, 2dlu, pref");
		CellConstraints cc = new CellConstraints();
		descriptionPanel dp = new descriptionPanel();
		d2 = new JDialog(plugin.getDesktop().getFrame(), "WikiPathways", false);
		JButton submit = new JButton("Update");

		submit.setActionCommand("Update");
		submit.addActionListener(this);
		d2.setLayout(layout);

		d2.add(dp, cc.xyw(2, 1,3));

		JPanel p = new JPanel();

		p.add(submit);
		d2.add(p, cc.xy(3,3));

		d2.pack();
		d2.setVisible(true);
		d2.setResizable(false);
		d2.setLocationRelativeTo(plugin.getDesktop().getSwingEngine().getFrame());
		d2.setVisible(true);
	}

	private class descriptionPanel extends JPanel {
		public descriptionPanel() {
			super();
			setLayout(new GridLayout(2, 2));
			add(new JLabel("Give a description of your changes"));
			add(new JScrollPane(description));
		}
	}

	private void showLoginPanel() {
		p = new LoginPanel(plugin);
		d = new JDialog(plugin.getDesktop().getFrame(), "WikiPathways Login", false);
		JButton submit = new JButton("Login");

		submit.setActionCommand("Login");
		submit.addActionListener(this);
		d.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		d.add(p, c);

		c.weighty = 0.0;
		c.weightx = 0.5;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = GridBagConstraints.HORIZONTAL;
		JPanel p = new JPanel();

		p.add(submit);
		d.add(p, c);

		d.pack();
		d.setVisible(true);
		d.setResizable(false);
		d.setLocationRelativeTo(plugin.getDesktop().getSwingEngine().getFrame());
		d.setVisible(true);
	}

	public void UpdatePathway() throws RemoteException, MalformedURLException, ServiceException, FailedConnectionException {
		try {
			final ProgressKeeper pk = new ProgressKeeper();
			final ProgressDialog d = new ProgressDialog(
					plugin.getDesktop().getFrame(), "", pk, true, true);

			SwingWorker<WSPathwayInfo, Void> sw = new SwingWorker<WSPathwayInfo, Void>() {
				WSPathwayInfo info;
				protected WSPathwayInfo doInBackground() throws Exception {
					try {
						pk.setTaskName("Checking if pathway has been changed.");
						Pathway pathway = plugin.getDesktop().getSwingEngine().getEngine().getActivePathway();
						WSPathwayInfo wsPathwayInfo = plugin.getWpQueries().getPathwayInfo(plugin.getPathwayID(), pk);
						String newrevision = wsPathwayInfo.getRevision();
						
						if(plugin.getRevision().equals(newrevision)) {
							pk.setTaskName("Check if curation tags need to be updated.");
							// check if curation tags need to be updated
							boolean curTagActive = false;
							boolean feaTagActive = false;
							boolean updateCurTag = false;
							boolean updateFeaTag = false;
							Set<WSCurationTag> tags = plugin.getWpQueries().getCurationTags(plugin.getPathwayID(), null);
							for(WSCurationTag tag : tags) {
								if(tag.getName().equals("Curation:AnalysisCollection")) {
									if(tag.getRevision().equals(plugin.getRevision())) {
										curTagActive = true;
									}
								} else if (tag.getName().equals("Curation:FeaturedPathway")) {
									if(tag.getRevision().equals(plugin.getRevision())) {
										feaTagActive = true;
									}
								}
							}
						
							// ask if user wants to update curation tags
							if(curTagActive || feaTagActive) {
								pk.setTaskName("Ask user if tags should be updated.");
								if(d.isVisible()) {
									int n = JOptionPane.showConfirmDialog(
											d,
										    "Do you want to update the Approved and/or Featured version tags?",
										    "Tag update",
										    JOptionPane.YES_NO_OPTION);
									if(n == JOptionPane.YES_OPTION) {
										if(curTagActive) {
											updateCurTag = true;
										}
										if(feaTagActive) {
											updateFeaTag = true;
										}
									}
								} else {
									int n = JOptionPane.showConfirmDialog(
											plugin.getDesktop().getFrame(),
										    "Do you want to update the Approved and/or Featured version tags?",
										    "Tag update",
										    JOptionPane.YES_NO_OPTION);
									if(n == JOptionPane.YES_OPTION) {
										if(curTagActive) {
											updateCurTag = true;
										}
										if(feaTagActive) {
											updateFeaTag = true;
										}
									}
								}
							}						
							
							pk.setTaskName("Update pathway.");
							// update pathway
							plugin.getWpQueries().updatePathway(pathway, plugin.getPathwayID(), Integer.parseInt(plugin.getRevision()), description.getText());
	
							// get latest info to reload pathway
							info = plugin.getWpQueries().getPathwayInfo(plugin.getPathwayID(), pk);
							String message = "The pathway is updated.";
							if(updateCurTag) {
								plugin.getWpQueries().updateCurationTag("Curation:AnalysisCollection", plugin.getPathwayID(), "", Integer.parseInt(info.getRevision()));
								message = message + "\nApproved version tag has been updated.";
							}
							if(updateFeaTag) {
								plugin.getWpQueries().updateCurationTag("Curation:FeaturedPathway", plugin.getPathwayID(), "", Integer.parseInt(info.getRevision()));
								message = message + "\nFeatured version tag has been updated.";
							}
							pk.setTaskName(message);
							
							JOptionPane.showMessageDialog(d,
									message, "Update",
									JOptionPane.INFORMATION_MESSAGE);
						} else {
							JOptionPane.showMessageDialog(plugin.getDesktop().getFrame(),
									"The pathway is not up-to-date.\nPlease reload the latest revision.", "Error",
									JOptionPane.ERROR_MESSAGE);
						}
					 } catch (Exception e) {
						JOptionPane.showMessageDialog(plugin.getDesktop().getFrame(),
								"Error while updating the pathway, since you do not have write access.\n Please submit a request at the following website: \n http://plugins.pathvisio.org/wp-client/request-webservice-access/", "Error",
								JOptionPane.ERROR_MESSAGE);
					} finally {     
						pk.finished();
					}
					return info;
				}
				
				protected void done() {
					if(info != null) {
						// open latest revision
						File tmpDir = new File(plugin.getTmpDir(), FileUtils.getTimeStamp());
						tmpDir.mkdirs();
	
						try {
							pk.setTaskName("Open latest revision of pathway.");
							plugin.openPathwayWithProgress(info.getId(), 0, tmpDir);
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(plugin.getDesktop().getFrame(),
									"Could not load new revision.", "Error",
									JOptionPane.ERROR_MESSAGE);
							Logger.log.error("Error", ex);
						}
					}
				}
				
			};
			
			sw.execute();
			d.setVisible(true);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(plugin.getDesktop().getFrame(),
				"Error while updating pathway.\n"+e.getMessage(), "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}		
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if ("Login".equals(e.getActionCommand())) {
			final ProgressKeeper pk = new ProgressKeeper();
			final ProgressDialog dialog = new ProgressDialog(plugin.getDesktop().getFrame(), "", pk, true, true);
			
			SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {
				
				Boolean value = false;
				
				protected Void doInBackground() throws Exception {
					pk.setTaskName("Checking login details.");
					d.dispose();
					try {
						if(p.login()) {
							value = true;
						}
					} catch (Exception e1) {}
					return null; 
				}
				
				protected void done() {
					if(!pk.isCancelled()) {
						if(value) {
							showDescriptionPanel();
							pk.finished();
						}
					}
				}
			};
			sw.execute();
			dialog.setVisible(true);
		}
		
		if ("Update".equals(e.getActionCommand())) {
			try {
				d2.dispose();
				UpdatePathway();
			} catch (Exception e1) {
				e1.printStackTrace();
			} 
		}
	}
}
