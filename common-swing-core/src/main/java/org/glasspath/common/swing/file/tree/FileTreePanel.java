/*
 * This file is part of Glasspath Common.
 * Copyright (C) 2011 - 2023 Remco Poelstra
 * Authors: Remco Poelstra
 * 
 * This program is offered under a commercial and under the AGPL license.
 * For commercial licensing, contact us at https://glasspath.org. For AGPL licensing, see below.
 * 
 * AGPL licensing:
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.glasspath.common.swing.file.tree;

import java.awt.BorderLayout;
import java.io.File;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;

import org.glasspath.common.icons.Icons;
import org.glasspath.common.swing.filter.FilterTools;
import org.glasspath.common.swing.table.Filterable;

public class FileTreePanel extends JPanel {

	private final JTree tree;
	private final JToolBar toolBar;
	private final FilterTools filterTools;

	public FileTreePanel() {
		this(null);
	}

	public FileTreePanel(List<File> rootDirs) {

		setLayout(new BorderLayout());

		tree = new JTree();
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.setCellRenderer(new FileTreeRenderer());
		tree.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JScrollPane treeScrollPane = new JScrollPane(tree);
		treeScrollPane.setBorder(BorderFactory.createEmptyBorder());
		add(treeScrollPane, BorderLayout.CENTER);

		toolBar = new JToolBar();
		toolBar.setOpaque(false);

		JButton nextButton = new JButton();
		nextButton.setIcon(Icons.arrowDown);
		toolBar.add(nextButton);

		JButton previousButton = new JButton();
		previousButton.setIcon(Icons.arrowUp);
		toolBar.add(previousButton);

		JButton expandAllButton = new JButton();
		expandAllButton.setIcon(Icons.plusBoxMultipleOutline);
		toolBar.add(expandAllButton);

		JButton collapseAllButton = new JButton();
		collapseAllButton.setIcon(Icons.minusBoxMultipleOutline);
		toolBar.add(collapseAllButton);

		filterTools = new FilterTools(new Filterable() {

			@Override
			public void setFilter(String filter, Date from, Date to) {
				// TODO
			}

			@Override
			public int getFilterResultCount() {
				return 0; // TODO
			}
		}, 200);

		if (rootDirs != null) {
			setRootDirectories(rootDirs);
		}

	}

	public JTree getTree() {
		return tree;
	}

	public JToolBar getToolBar() {
		return toolBar;
	}

	public FilterTools getFilterTools() {
		return filterTools;
	}

	public void setRootDirectories(List<File> rootDirs) {
		tree.setModel(new FileTreeModel(rootDirs));
	}

}
