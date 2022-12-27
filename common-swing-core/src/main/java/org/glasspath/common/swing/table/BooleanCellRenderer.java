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
package org.glasspath.common.swing.table;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class BooleanCellRenderer extends DefaultTableCellRenderer {

	private final JCheckBox checkBox;

	private boolean modifyClip = false;

	public BooleanCellRenderer() {

		checkBox = new JCheckBox() {

			@Override
			public void paint(Graphics g) {

				// TODO: This is a hack..
				// Prevent clearRect from removing the vertical line
				if (modifyClip) {

					if (g.getClip() instanceof Rectangle) {
						Rectangle clip = (Rectangle) g.getClip();
						g.setClip(new Rectangle(clip.x, clip.y, clip.width - 1, clip.height));
					} else if (g.getClip() instanceof Rectangle2D) {
						Rectangle2D clip = (Rectangle2D) g.getClip();
						g.setClip(new Rectangle2D.Double(clip.getX(), clip.getY(), clip.getWidth() - 1, clip.getHeight()));
					}

				}

				super.paint(g);

			}

		};
		checkBox.setHorizontalAlignment(JCheckBox.CENTER);

	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		modifyClip = true;
		for (int r : table.getSelectedRows()) {
			if (r == row) {
				modifyClip = false;
				break;
			}
		}

		if (value instanceof Boolean) {
			checkBox.setSelected((Boolean) value);
		} else {
			checkBox.setSelected(false);
		}

		return checkBox;

	}

}
