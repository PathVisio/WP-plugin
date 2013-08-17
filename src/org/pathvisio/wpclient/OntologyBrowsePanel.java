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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import org.pathvisio.core.debug.Logger;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class OntologyBrowsePanel extends JPanel {

	private WikiPathwaysClientPlugin plugin;
	private JScrollPane resultspane;

	public OntologyBrowsePanel(WikiPathwaysClientPlugin plugin) {
		this.plugin = plugin;

		setLayout(new BorderLayout());
	

		Action searchAction = new AbstractAction("Browse") {
			public void actionPerformed(ActionEvent e) {
				try {
					resultspane.setBorder(BorderFactory.createTitledBorder(
							WikiPathwaysClientPlugin.etch, "Pathways"));
					browse();
				} catch (Exception ex) {
					JOptionPane
							.showMessageDialog(OntologyBrowsePanel.this,
									ex.getMessage(), "Error",
									JOptionPane.ERROR_MESSAGE);
					Logger.log.error("Error searching WikiPathways", ex);
				}
			}

			private void browse() {
				// TODO Auto-generated method stub

			}

		};
		Object[] ontologytag1 = 
			{"Pathway Ontology",
				new Object[] {"classic metabolic pathway",new Object[] {"altered metabolic pathway",
						new Object[] {"altered amino acid metabolic pathway",
						new Object[] {"altered hydrophobic amino acid metabolic pathway",new Object[] { "altered methionine cycle/metabolic pathway" }},
								new Object[] {"altered carbohydrate metabolic pathway" ,new Object[] {"altered galactose metabolic pathway"},
								new Object[] {
										"altered glycogen metabolic pathway","altered glycogen biosynthetic pathway","altered glycogen degradation pathway"}
											  
											},
											new Object[]{"altered energy metabolic pathway",
								new Object[]{"altered citric acid cycle pathway"}
											},
											new Object[]{"altered glycan metabolic pathway",
								new Object[]{"altered glycosaminoglycan pathway",	new Object[]{"altered heparan sulfate pathway"}}
											},
											new Object[]{"altered lipid metabolic pathway",
								new Object[]{"altered isoprenoid metabolic pathway",	new Object[]{"altered isoprenoid biosynthetic pathway",new Object[]{"altered isoprenoid biosynthetic pathway"}}}
											}}
											}},

				new Object[] { "disease pathway"},
				new Object[] { "drug pathway" },
				new Object[] { "regulatory pathway" },
				new Object[] { "signaling pathway" } };
		
		Object[] ontologytag2 = {"Disease",new Object[]{"disease by infectious agent"},new Object[]{"disease of anatomical entity"},
				new Object[]{"disease of cellular proliferation"},
				new Object[]{"disease of mental health"},
				new Object[]{"disease of metabolism"},
				new Object[]{"genetic disease"},	new Object[]{"medical disorder"},
				new Object[]{"syndrome"}};
		Object[] ontologytag3 = {"Cell Type"};

		JPanel searchOptBox = new JPanel();
		FormLayout layout = new FormLayout("p,1dlu,p,1dlu,p,1dlu",
				"p, pref, p, 2dlu");
		CellConstraints cc = new CellConstraints();
searchOptBox.setBackground(Color.WHITE);
		searchOptBox.setLayout(layout);
		searchOptBox.setBorder(BorderFactory.createTitledBorder(
				WikiPathwaysClientPlugin.etch, "Search options"));
		searchOptBox.add(new SimpleTree(ontologytag1), cc.xy(1, 1));
		searchOptBox.add(new SimpleTree(ontologytag2), cc.xy(3, 1));
		searchOptBox.add(new SimpleTree(ontologytag3), cc.xy(5, 1));
	
		add(searchOptBox);
		
	}
}

class SimpleTree extends JPanel {

	Object[] ontologytag1;

	public SimpleTree(Object[] ontologytag1) {
		this.ontologytag1 = ontologytag1;

		DefaultMutableTreeNode root = processHierarchy(ontologytag1);
		JTree tree = new JTree(root);
		add(tree);

	}

	/**
	 * Small routine that will make node out of the first entry in the array,
	 * then make nodes out of subsequent entries and make them child nodes of
	 * the first one. The process is repeated recursively for entries that are
	 * arrays.
	 */

	private DefaultMutableTreeNode processHierarchy(Object[] ontologytags) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(ontologytags[0]);
		DefaultMutableTreeNode child;
		for (int i = 1; i < ontologytags.length; i++) {
			Object nodeSpecifier = ontologytags[i];
			if (nodeSpecifier instanceof Object[]) // Ie node with children
				child = processHierarchy((Object[]) nodeSpecifier);
			else
				child = new DefaultMutableTreeNode(nodeSpecifier); // Ie Leaf
			node.add(child);
		}
		return (node);
	}
}
