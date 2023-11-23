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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.glasspath.common.icons.Icons;
import org.glasspath.common.swing.filter.FilterTools;
import org.glasspath.common.swing.table.Filterable;

public class FileTreePanel extends JPanel {

	private final JTree tree;
	private final JToolBar toolBar;
	private final FilterTools filterTools;
	private final List<ActionListener> actionListeners = new ArrayList<>();

	public FileTreePanel() {
		this(null);
	}

	public FileTreePanel(List<File> rootDirs) {

		setLayout(new BorderLayout());

		tree = new JTree();
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.setCellRenderer(new FileTreeCellRenderer());
		tree.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		tree.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {

				if (e.getClickCount() >= 2) {

					Object selectedObject = tree.getLastSelectedPathComponent();
					if (selectedObject instanceof File) {
						fireActionPerformed((File) selectedObject);
					}

				}

			}
		});

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

	public void addActionListener(ActionListener listener) {
		actionListeners.add(listener);
	}

	public void removeActionListener(ActionListener listener) {
		actionListeners.remove(listener);
	}

	private void fireActionPerformed(File file) {

		FileTreeEvent event = new FileTreeEvent(tree, file);

		for (ActionListener listener : actionListeners) {
			listener.actionPerformed(event);
		}

	}

	public void selectFile(File file) {

		if (file != null) {

			if (tree.getModel() instanceof FileTreeModel) {

				List<File> rootDirs = ((FileTreeModel) tree.getModel()).getRootDirs();

				List<Object> selectionPath = new ArrayList<>();

				File parent = file;
				while (parent != null) {

					selectionPath.add(parent);

					if (rootDirs.contains(parent)) {

						selectionPath.add(((FileTreeModel) tree.getModel()).getRoot());

						Collections.reverse(selectionPath);
						tree.setSelectionPath(new TreePath(selectionPath.toArray()));

						break;

					}

					parent = parent.getParentFile();

				}

			}

		} else {
			tree.getSelectionModel().clearSelection();
		}

	}

	public static class FileTreeEvent extends ActionEvent {

		public static final String FILE_SELECTED = "FileSelected"; //$NON-NLS-1$

		private final File file;

		public FileTreeEvent(JTree tree, File file) {
			super(tree, ACTION_PERFORMED, FILE_SELECTED);
			this.file = file;
		}

		public File getFile() {
			return file;
		}

	}

}
