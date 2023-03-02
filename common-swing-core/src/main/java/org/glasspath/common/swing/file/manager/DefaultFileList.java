/*
 * This file is part of Glasspath Common.
 * Copyright (C) 2011 - 2022 Remco Poelstra
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
package org.glasspath.common.swing.file.manager;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.glasspath.common.Common;

public class DefaultFileList extends FileList {

	private final List<File> files = new ArrayList<>();
	private File directory = null;
	private boolean valid = false;

	public DefaultFileList() {

	}

	public DefaultFileList(File directory, FileFilter fileFilter) {
		this.directory = directory;
		this.fileFilter = fileFilter;
	}

	public File getDirectory() {
		return directory;
	}

	public void setDirectory(File directory) {
		this.directory = directory;
		valid = false;
	}

	@Override
	public void fileFilterChanged() {
		valid = false;
	}

	public void validate() {
		if (!valid) {
			reload();
			valid = true;
		}
	}

	public void reload() {

		files.clear();

		try {

			if (directory != null && directory.isDirectory()) {

				File[] filteredFiles = directory.listFiles(fileFilter);
				for (File file : filteredFiles) {
					files.add(file);
				}

				Collections.sort(files);

			}

		} catch (Exception e) {
			Common.LOGGER.error("Exception while getting files: ", e); //$NON-NLS-1$
		}

	}

	@Override
	public int size() {
		validate();
		return files.size();
	}

	@Override
	public File get(int index) {
		validate();
		return files.get(index);
	}

	@Override
	public int indexOf(File file) {
		validate();
		return files.indexOf(file);
	}

}
