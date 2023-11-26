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

import java.awt.Component;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.glasspath.common.icons.Icons;
import org.glasspath.common.swing.color.ColorUtils;

public class FileTreeCellRenderer extends DefaultTreeCellRenderer {

	public FileTreeCellRenderer() {
		setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		setBackgroundSelectionColor(ColorUtils.SELECTION_COLOR_FOCUSSED);
		setTextSelectionColor(getTextNonSelectionColor());
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

		if (value instanceof File) {

			File file = (File) value;

			if (file.isDirectory()) {

				if (tree.getModel() instanceof FileTreeModel && ((FileTreeModel) tree.getModel()).isRootFile(file)) {
					setText(file.getAbsolutePath());
					setIcon(Icons.folderOutlineBlue);
				} else {
					setText(file.getName());
					setIcon(Icons.folderBlue);
				}

			} else {
				setText(file.getName());
				setIcon(Icons.fileOutline);
			}

		} else {
			setIcon(Icons.folderOutlineBlue);
		}

		return this;

	}

}