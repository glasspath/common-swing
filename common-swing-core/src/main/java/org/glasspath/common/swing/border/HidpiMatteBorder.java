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

import javax.swing.border.MatteBorder;

import org.glasspath.common.swing.theme.Theme;

public class HidpiMatteBorder extends MatteBorder {

	public static final Color COLOR = Theme.isDark() ? new Color(60, 60, 60) : new Color(210, 210, 210);

	public HidpiMatteBorder(Insets insets) {
		super(insets, COLOR);
	}

	public HidpiMatteBorder(Insets insets, Color color) {
		super(insets, color);
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {

		Graphics2D g2d = (Graphics2D) g;
		if (g2d.getTransform().getScaleX() >= 2.0) {
			g2d.scale(0.5, 0.5);
			super.paintBorder(c, g2d, x * 2, y * 2, width * 2, height * 2);
			g2d.scale(2.0, 2.0);
		} else if (g2d.getTransform().getScaleX() >= 1.5) {
			g2d.scale(0.5, 0.5);
			super.paintBorder(c, g2d, (x * 2) - 1, y * 2, width * 2, height * 2);
			g2d.scale(2.0, 2.0);
		} else {
			super.paintBorder(c, g2d, x, y, width, height);
		}

	}

}
