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
package org.glasspath.common.swing.border;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;

import javax.swing.Icon;

import org.glasspath.common.swing.theme.Theme;

public class BorderIcon implements Icon {

	public static final Stroke DASHED_LINE_STROKE = new BasicStroke(1.0F, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 0, new float[] { 1.5F, 1.5F }, 0);
	public static final Color DASHED_LINE_COLOR = Theme.isDark() ? new Color(220, 220, 220) : new Color(75, 75, 75);
	public static final Stroke LINE_STROKE = new BasicStroke(1.0F);
	public static final Color LINE_COLOR = Theme.isDark() ? new Color(220, 220, 220) : new Color(50, 50, 50);

	private final boolean top;
	private final boolean left;
	private final boolean bottom;
	private final boolean right;
	private final boolean vertical;
	private final boolean horizontal;

	public BorderIcon(boolean top, boolean left, boolean bottom, boolean right, boolean vertical, boolean horizontal) {
		this.top = top;
		this.left = left;
		this.bottom = bottom;
		this.right = right;
		this.vertical = vertical;
		this.horizontal = horizontal;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		Rectangle rect = new Rectangle(x + 2, y + 2, 12, 12);

		g2d.setStroke(DASHED_LINE_STROKE);
		g2d.setColor(DASHED_LINE_COLOR);
		g2d.draw(rect);

		g2d.setStroke(LINE_STROKE);
		g2d.setColor(LINE_COLOR);

		if (top) {
			g2d.drawLine(x + 2, y + 2, x + 2 + 12, y + 2);
		}

		if (left) {
			g2d.drawLine(x + 2, y + 2, x + 2, y + 2 + 12);
		}

		if (bottom) {
			g2d.drawLine(x + 2, y + 2 + 12, x + 2 + 12, y + 2 + 12);
		}

		if (right) {
			g2d.drawLine(x + 2 + 12, y + 2, x + 2 + 12, y + 2 + 12);
		}

		if (vertical) {
			g2d.drawLine(x + 2 + 6, y + 2, x + 2 + 6, y + 2 + 12);
		}

		if (horizontal) {
			g2d.drawLine(x + 2, y + 2 + 6, x + 2 + 12, y + 2 + 6);
		}

	}

	@Override
	public int getIconWidth() {
		return 16;
	}

	@Override
	public int getIconHeight() {
		return 16;
	}

}
