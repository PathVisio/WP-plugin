package org.pathvisio.wpclient;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

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
		Object[] hierarchy = {
				"Pathway Ontology",
				new Object[] {
						"classic metabolic pathway",
						new Object[] {
								"altered metabolic pathway",
								new Object[] {
										"altered amino acid metabolic pathway",
										new Object[] {
												"altered hydrophobic amino acid metabolic pathway",
												new Object[] { "altered methionine cycle/metabolic pathway" },

										} },
								new Object[] { "altered carbohydrate metabolic pathway" },
								"altered galactose metabolic pathway",
								new Object[] {
										"altered glycogen metabolic pathway",
										new Object[] {
												"	altered glycogen biosynthetic pathway",
												"	altered glycogen degradation pathway" } } 
						}},

				new Object[] { "disease pathway" },
				new Object[] { "drug pathway" },
				new Object[] { "regulatory pathway" },
				new Object[] { "signaling pathway" } };

		JPanel searchOptBox = new JPanel();
		FormLayout layout = new FormLayout("p,1dlu,p,1dlu,p,1dlu",
				"p, pref, p, 2dlu");
		CellConstraints cc = new CellConstraints();

		searchOptBox.setLayout(layout);
		searchOptBox.setBorder(BorderFactory.createTitledBorder(
				WikiPathwaysClientPlugin.etch, "Search options"));
		searchOptBox.add(new SimpleTree(hierarchy), cc.xy(1, 1));
		searchOptBox.add(new SimpleTree(hierarchy), cc.xy(3, 1));
		searchOptBox.add(new SimpleTree(hierarchy), cc.xy(5, 1));
		add(searchOptBox);
	}
}

class SimpleTree extends JPanel {

	Object[] hierarchy;

	public SimpleTree(Object[] hierarchy) {
		this.hierarchy = hierarchy;

		DefaultMutableTreeNode root = processHierarchy(hierarchy);
		JTree tree = new JTree(root);
		add(tree);

	}

	/**
	 * Small routine that will make node out of the first entry in the array,
	 * then make nodes out of subsequent entries and make them child nodes of
	 * the first one. The process is repeated recursively for entries that are
	 * arrays.
	 */

	private DefaultMutableTreeNode processHierarchy(Object[] hierarchy) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(hierarchy[0]);
		DefaultMutableTreeNode child;
		for (int i = 1; i < hierarchy.length; i++) {
			Object nodeSpecifier = hierarchy[i];
			if (nodeSpecifier instanceof Object[]) // Ie node with children
				child = processHierarchy((Object[]) nodeSpecifier);
			else
				child = new DefaultMutableTreeNode(nodeSpecifier); // Ie Leaf
			node.add(child);
		}
		return (node);
	}
}
