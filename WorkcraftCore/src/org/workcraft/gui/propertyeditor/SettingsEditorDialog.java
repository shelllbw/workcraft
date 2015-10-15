/*
*
* Copyright 2008,2009 Newcastle University
*
* This file is part of Workcraft.
*
* Workcraft is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Workcraft is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Workcraft.  If not, see <http://www.gnu.org/licenses/>.
*
*/

package org.workcraft.gui.propertyeditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.workcraft.Framework;
import org.workcraft.gui.MainWindow;
import org.workcraft.plugins.PluginInfo;

public class SettingsEditorDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JScrollPane sectionPane;
	private JScrollPane propertiesPane;
	private JSplitPane splitPane;
	private JPanel buttonsPane;
	private JButton okButton;
	private DefaultMutableTreeNode sectionRoot;
	private JTree sectionTree;
	private final PropertyEditorTable propertiesTable;

	static class SettingsPageNode {
		private Settings page;

		public SettingsPageNode(Settings page) {
			this.page = page;
		}

		@Override
		public String toString() {
			return page.getName();
		}

		public Settings getPage() {
			return page;
		}
	}

	public SettingsEditorDialog(MainWindow owner) {
		super(owner);

		propertiesTable = new PropertyEditorTable();
		propertiesTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setModal(true);
		setTitle("Settings");
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				ok();
			}
		});

		Dimension minSize = new Dimension(500, 200);
		setMinimumSize(minSize);
		Dimension mySize = new Dimension(900, 600);
		setSize(mySize);

		Dimension parentSize = owner.getSize();
		owner.getLocationOnScreen();
		setLocation(((parentSize.width - mySize.width)/2) + 0, ((parentSize.height - mySize.height)/2) + 0);

		initComponents();
		loadSections();
	}

	public DefaultMutableTreeNode getSectionNode (DefaultMutableTreeNode node, String section) {
		int dotPos = section.indexOf('.');

		String thisLevel, nextLevel;

		if (dotPos < 0) {
			thisLevel = section;
			nextLevel = null;
		} else {
			thisLevel = section.substring(0, dotPos);
			nextLevel = section.substring(dotPos+1);
		}

		DefaultMutableTreeNode thisLevelNode = null;

		for (int i=0; i < node.getChildCount(); i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
			if (! (child.getUserObject() instanceof String)) {
				continue;
			}
			if (((String)child.getUserObject()).equals(thisLevel)) {
				thisLevelNode = child;
				break;
			}
		}

		if (thisLevelNode == null) {
			thisLevelNode = new DefaultMutableTreeNode(thisLevel);
		}
		node.add(thisLevelNode);

		if (nextLevel == null) {
			return thisLevelNode;
		} else {
			return getSectionNode(thisLevelNode, nextLevel);
		}
	}

	private void addItem (String section, Settings item) {
		DefaultMutableTreeNode sectionNode = getSectionNode(sectionRoot, section);
		sectionNode.add(new DefaultMutableTreeNode(new SettingsPageNode(item)));
	}

	private void loadSections() {
		final Framework framework = Framework.getInstance();
		for (PluginInfo<? extends Settings> info : framework.getPluginManager().getPlugins(Settings.class)) {
			Settings e = info.getSingleton();
			addItem (e.getSection(), e);
		}

		sectionTree.setModel(new DefaultTreeModel(sectionRoot));

		// expand all tree branches
		for(int i=0;i<sectionTree.getRowCount();i++) {
			final TreePath treePath = sectionTree.getPathForRow(i);
			sectionTree.expandPath(treePath);
		}
	}

	private void setObject(Settings p) {
		if (p == null) {
			propertiesTable.setObject(null);
		} else {
			propertiesTable.setObject(p);
		}
	}

	private void initComponents() {
		contentPane = new JPanel(new BorderLayout());
		setContentPane(contentPane);
		sectionPane = new JScrollPane();
		sectionRoot = new DefaultMutableTreeNode("root");

		sectionTree = new JTree();
		sectionTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		sectionTree.setRootVisible(false);
		sectionTree.setShowsRootHandles(true);

		sectionTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				Object userObject = ((DefaultMutableTreeNode)e.getPath().getLastPathComponent()).getUserObject();
				if (userObject instanceof SettingsPageNode) {
					setObject( ((SettingsPageNode) userObject).getPage() );
				} else {
					setObject(null);
				}
			}
		});

		sectionPane.setViewportView(sectionTree);
		sectionPane.setMinimumSize(new Dimension(50,0));
		sectionPane.setPreferredSize(new Dimension(250,0));
		sectionPane.setBorder(BorderFactory.createTitledBorder("Section"));

		propertiesPane = new JScrollPane();
		propertiesPane.setMinimumSize(new Dimension(250,0));
		propertiesPane.setPreferredSize(new Dimension(450,0));
		propertiesPane.setBorder(BorderFactory.createTitledBorder("Selection properties"));
		propertiesPane.setViewportView(propertiesTable);

		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sectionPane, propertiesPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(250);
		splitPane.setResizeWeight(0.1);

		okButton = new JButton();
		okButton.setPreferredSize(new Dimension(100, 20));
		okButton.setText("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ok();
			}
		});

		buttonsPane = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		buttonsPane.add(okButton);
		contentPane.add(splitPane, BorderLayout.CENTER);
		contentPane.add(buttonsPane, BorderLayout.SOUTH);
		getRootPane().setDefaultButton(okButton);
	}

	private void ok() {
		setObject(null);
		setVisible(false);
	}

}
