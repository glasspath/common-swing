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
package org.glasspath.common.swing.graphics;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JComponent;

import org.glasspath.common.swing.SwingUtils;

public class GraphicsUtils {

	public static final Color HINT_BG_COLOR = new Color(215, 215, 215, 150);
	public static final Color HINT_TEXT_COLOR = Color.black;

	private GraphicsUtils() {

	}

	public static void drawTableHint(JComponent component, Graphics2D g2d, Point preferredLocation, Rectangle2D withinRect, Map<String, String> table) {

		FontMetrics fontMetrics = g2d.getFontMetrics();
		Rectangle2D fontRect;
		int col1Width = 0;
		int col2Width = 0;

		int x = preferredLocation.x;
		int y = preferredLocation.y;
		int width = 16; // 4px margin left and 4px right, 8px spacing between col's
		int height = 4; // 2px margin top and 2px bottom

		for (Entry<String, String> entry : table.entrySet()) {

			fontRect = fontMetrics.getStringBounds(entry.getKey(), g2d);
			if (fontRect.getWidth() > col1Width) {
				col1Width = (int) fontRect.getWidth();
			}
			height += fontRect.getHeight() + 4;

			fontRect = fontMetrics.getStringBounds(entry.getValue(), g2d);
			if (fontRect.getWidth() > col2Width) {
				col2Width = (int) fontRect.getWidth();
			}

		}

		width += col1Width + col2Width;

		g2d.setColor(HINT_BG_COLOR);
		if (x + width > withinRect.getWidth()) {
			x = (int) preferredLocation.x - width + 1;
			// g2d.drawLine((int)atRect.getX(), (int)atRect.getY(), (int)atRect.getX(), y + (height - 2));
		} else {
			// g2d.drawLine((int)atRect.getMaxX(), (int)atRect.getY(), x, y + (height - 2));
		}

		if (y + height > withinRect.getHeight()) {
			y = (int) withinRect.getHeight() - height;
		}

		RoundRectangle2D background = new RoundRectangle2D.Double(x, y, width, height, 4, 4);
		g2d.fill(background);

		g2d.setColor(HINT_TEXT_COLOR);

		x += 4;
		y += 2;
		for (Entry<String, String> entry : table.entrySet()) {

			fontRect = fontMetrics.getStringBounds(entry.getKey(), g2d);
			y += fontRect.getHeight();

			// g2d.drawString(entry.getKey(), x, y);
			SwingUtils.drawString(component, g2d, entry.getKey(), x, y);
			// g2d.drawString(entry.getValue(), x + col1Width + 8, y);
			SwingUtils.drawString(component, g2d, entry.getValue(), x + col1Width + 8, y);

			y += 4;

		}

	}

	public static void drawSingleLineHint(Graphics2D g2d, Point preferredLocation, String text) {

		int x = preferredLocation.x;
		int y = preferredLocation.y;
		int width = 8;
		int height = 8;

		FontMetrics fontMetrics = g2d.getFontMetrics();
		Rectangle2D fontRect = fontMetrics.getStringBounds(text, g2d);

		width += fontRect.getWidth();
		height += fontRect.getHeight();

		RoundRectangle2D background = new RoundRectangle2D.Double(x, y, width, height, 4, 4);
		g2d.setColor(HINT_BG_COLOR);
		g2d.fill(background);

		x += 4;
		y += fontRect.getHeight() + 2;
		g2d.setColor(HINT_TEXT_COLOR);
		g2d.drawString(text, x, y);

	}

	public static int getColorDifference(Color color1, Color color2) {
		if (color1 == null || color2 == null) {
			return 0;
		} else {
			return (color1.getRed() + color1.getGreen() + color1.getBlue()) - (color2.getRed() + color2.getGreen() + color2.getBlue());
		}
	}

}
