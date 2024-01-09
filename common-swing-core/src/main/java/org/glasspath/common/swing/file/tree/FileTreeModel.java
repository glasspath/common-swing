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
	private final Map<String, File[]> cache;

	public FileTreeModel(List<File> rootFiles) {
		this(rootFiles, new HashMap<>());
	}

	public FileTreeModel(List<File> rootFiles, Map<String, File[]> cache) {
		this.rootFiles.addAll(rootFiles);
		this.cache = cache;
	}

	public List<File> getRootFiles() {
		return rootFiles;
	}

	public boolean isRootFile(File file) {
		return rootFiles.contains(file);
	}

	public Map<String, File[]> getCache() {
		return cache;
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

	public FileTreeModel createFilteredModel(List<File> files) {

		List<File> rootFiles = new ArrayList<>();

		Map<String, File[]> cache = new HashMap<>();
		cache.putAll(this.cache);

		List<File> queue = new ArrayList<>();
		queue.addAll(files);

		addPathsToRoot(queue, rootFiles);

		List<File> filesWithSameParent = getFilesWithSameParent(queue);
		while (filesWithSameParent != null && filesWithSameParent.size() > 0) {

			cache.put(filesWithSameParent.get(0).getParentFile().getAbsolutePath(), filesWithSameParent.toArray(new File[0]));

			queue.removeAll(filesWithSameParent);
			filesWithSameParent = getFilesWithSameParent(queue);

		}

		return new FileTreeModel(rootFiles, cache);

	}

	private void addPathsToRoot(List<File> files, List<File> rootFiles) {

		List<File> filesToAdd = new ArrayList<>();

		for (File file : files) {

			File parent = file.getParentFile();
			while (parent != null) {

				if (this.rootFiles.contains(parent)) {
					if (!rootFiles.contains(parent)) {
						rootFiles.add(parent);
					}
					break;
				} else {
					filesToAdd.add(parent);
					parent = parent.getParentFile();
				}

			}

		}

		for (File file : filesToAdd) {
			if (!files.contains(file)) {
				files.add(file);
			}
		}

	}

	private List<File> getFilesWithSameParent(List<File> files) {

		List<File> filesWithSameParent = new ArrayList<>();

		if (files.size() > 0) {

			filesWithSameParent.add(files.get(0));

			File parent = files.get(0).getParentFile();
			if (parent != null) {
				for (int i = 1; i < files.size(); i++) {
					if (parent.equals(files.get(i).getParentFile())) {
						filesWithSameParent.add(files.get(i));
					}
				}
			}

		}

		return filesWithSameParent;

	}

}