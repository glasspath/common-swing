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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import javax.swing.border.AbstractBorder;

import org.glasspath.common.swing.theme.Theme;

public class RoundedLineBorder extends AbstractBorder {

	public static final Color LINE_COLOR = Theme.isDark() ? new Color(50, 50, 50) : new Color(220, 220, 220);

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {

		if (g instanceof Graphics2D) {

			final Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			g2d.setColor(LINE_COLOR);

			if (g2d.getTransform().getScaleX() >= 1.5) {
				g2d.scale(0.5, 0.5);
				g2d.draw(new RoundRectangle2D.Float(x * 2, y * 2, (width * 2) - 1, (height * 2) - 1, 8, 8));
				g2d.scale(2.0, 2.0);
			} else {
				g2d.draw(new RoundRectangle2D.Float(x, y, width - 1, height - 1, 4, 4));
			}

		}

	}

	@Override
	public Insets getBorderInsets(Component c) {
		return new Insets(0, 0, 0, 0);
	}

	@Override
	public Insets getBorderInsets(Component c, Insets insets) {

		insets.left = 0;
		insets.top = 0;
		insets.right = 0;
		insets.bottom = 0;

		return insets;

	}

	@Override
	public boolean isBorderOpaque() {
		return true;
	}

}