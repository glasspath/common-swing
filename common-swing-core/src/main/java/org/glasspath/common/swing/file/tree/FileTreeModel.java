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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

public class FileTreeModel implements TreeModel {

	private final DefaultMutableTreeNode root = new DefaultMutableTreeNode();
	private final List<File> rootFiles = new ArrayList<>();
	private final Map<String, File[]> cache = new HashMap<>();

	public FileTreeModel(List<File> rootFiles) {
		this.rootFiles.addAll(rootFiles);
	}

	public List<File> getRootFiles() {
		return rootFiles;
	}

	public boolean isRootFile(File file) {
		return rootFiles.contains(file);
	}

	@Override
	public Object getRoot() {
		return root;
	}

	@Override
	public int getChildCount(Object parent) {
		if (parent == root) {
			return rootFiles.size();
		} else if (parent instanceof File) {
			return listFiles((File) parent).length;
		} else {
			return 0;
		}
	}

	@Override
	public Object getChild(Object parent, int index) {
		if (parent == root) {
			return rootFiles.get(index);
		} else if (parent instanceof File) {
			return listFiles((File) parent)[index];
		} else {
			return null;
		}
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {

		if (parent == root) {
			return rootFiles.indexOf(child);
		} else if (parent instanceof File) {
			File[] files = listFiles((File) parent);
			for (int i = 0; i < files.length; i++) {
				if (files[i] == child) {
					return i;
				}
			}
		}

		return -1;

	}

	@Override
	public boolean isLeaf(Object node) {
		if (node == root) {
			return false;
		} else if (node instanceof File) {
			return !((File) node).isDirectory();
		} else {
			return false;
		}
	}

	@Override
	public void addTreeModelListener(javax.swing.event.TreeModelListener l) {

	}

	@Override
	public void removeTreeModelListener(javax.swing.event.TreeModelListener l) {

	}

	@Override
	public void valueForPathChanged(javax.swing.tree.TreePath path, Object newValue) {

	}

	public synchronized File[] listFiles(File file) {

		File[] files = cache.get(file.getAbsolutePath());

		if (files == null) {

			files = file.listFiles();

			Arrays.sort(files, new Comparator<File>() {

				@Override
				public int compare(File f1, File f2) {
					if (f1.isDirectory() && !f2.isDirectory()) {
						return -1;
					} else if (!f1.isDirectory() && f2.isDirectory()) {
						return 1;
					} else {
						return f1.compareTo(f2);
					}
				}
			});

			cache.put(file.getAbsolutePath(), files);

		}

		return files;

	}

}