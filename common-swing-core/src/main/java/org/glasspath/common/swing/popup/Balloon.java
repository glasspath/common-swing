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
package org.glasspath.common.swing.popup;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.Border;

import org.glasspath.common.swing.SwingUtils;
import org.glasspath.common.swing.color.ColorUtils;
import org.glasspath.common.swing.theme.Theme;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.ui.FlatDropShadowBorder;
import com.formdev.flatlaf.ui.FlatUIUtils;

public class Balloon {

	public static final int TOP = 0;
	public static final int LEFT = 1;
	public static final int BOTTOM = 2;
	public static final int RIGHT = 3;

	public static final int SHADOW_PADDING = 6;
	public static final int SHADOW_PADDING_TOP = 3;
	public static final int ARROW_SIZE = 5;
	public static final Stroke STROKE = new BasicStroke(1.0F);

	private final Border shadow;
	private Color background = Theme.isDark() ? ColorUtils.DARK_31 : ColorUtils.GRAY_250;
	private Color borderColor = ColorUtils.GRAY_125;
	private int cornerRadius = 8;

	public Balloon() {

		if (UIManager.getLookAndFeel() instanceof FlatLaf) {
			shadow = new FlatDropShadowBorder(UIManager.getColor("Popup.dropShadowColor"), new Insets(8, 8, 8, 8), FlatUIUtils.getUIFloat("Popup.dropShadowOpacity", 0.5f)); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			shadow = null;
		}

	}

	public Color getBackground() {
		return background;
	}

	public void setBackground(Color background) {
		this.background = background;
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

	public int getCornerRadius() {
		return cornerRadius;
	}

	public void setCornerRadius(int cornerRadius) {
		this.cornerRadius = cornerRadius;
	}

	public void paintTextBalloon(JComponent c, Graphics2D g2d, String text, int x, int y, boolean right) {
		paintTextBalloon(c, g2d, text, x, y, right ? RIGHT : LEFT);
	}

	public void paintTextBalloon(JComponent c, Graphics2D g2d, String text, int x, int y, int orientation) {

		FontMetrics fontMetrics = g2d.getFontMetrics();
		Rectangle2D fontRect = fontMetrics.getStringBounds(text, g2d);

		int w = (int) fontRect.getWidth() + 20;
		int h = (int) fontRect.getHeight() + 6;

		if (orientation != BOTTOM) {
			y -= h / 2;
		}

		if (orientation == RIGHT) {
			x -= w;
		}

		paintBalloon(c, g2d, x, y, w, h, orientation);

		if (orientation == RIGHT) {
			x -= 5;
		}

		g2d.setColor(ColorUtils.TEXT_COLOR);
		SwingUtils.drawString(c, g2d, text, x + 12, y + h - (int) fontMetrics.getDescent() - 3);

	}

	public void paintBalloon(Component c, Graphics2D g2d, int x, int y, int w, int h, boolean right) {
		paintBalloon(c, g2d, x, y, w, h, right ? RIGHT : LEFT);
	}

	public void paintBalloon(Component c, Graphics2D g2d, int x, int y, int w, int h, int orientation) {

		if (shadow != null) {
			if (orientation == RIGHT) {
				shadow.paintBorder(c, g2d, x - SHADOW_PADDING, y - SHADOW_PADDING_TOP, w + SHADOW_PADDING + SHADOW_PADDING - ARROW_SIZE, h + SHADOW_PADDING_TOP + SHADOW_PADDING);
			} else if (orientation == TOP) {
				shadow.paintBorder(c, g2d, x - SHADOW_PADDING, y + ARROW_SIZE - SHADOW_PADDING_TOP, w + SHADOW_PADDING + SHADOW_PADDING - ARROW_SIZE, h + SHADOW_PADDING_TOP + SHADOW_PADDING - ARROW_SIZE);
			} else if (orientation == BOTTOM) {
				shadow.paintBorder(c, g2d, x - SHADOW_PADDING, y - SHADOW_PADDING_TOP, w + SHADOW_PADDING + SHADOW_PADDING - ARROW_SIZE, h + SHADOW_PADDING_TOP + SHADOW_PADDING - ARROW_SIZE);
			} else {
				shadow.paintBorder(c, g2d, x - SHADOW_PADDING + ARROW_SIZE, y - SHADOW_PADDING_TOP, w + SHADOW_PADDING + SHADOW_PADDING - ARROW_SIZE, h + SHADOW_PADDING_TOP + SHADOW_PADDING);
			}
		}

		Shape balloonShape = createBalloonShape(x, y, w, h, orientation);

		g2d.setColor(background);
		g2d.fill(balloonShape);

		g2d.setColor(borderColor);
		g2d.setStroke(STROKE);
		g2d.draw(balloonShape);

	}

	private Shape createBalloonShape(int x, int y, int w, int h, int orientation) {

		RoundRectangle2D balloonRect;
		Path2D arrowPath;

		if (orientation == RIGHT) {

			balloonRect = new RoundRectangle2D.Float(x, y, w - ARROW_SIZE, h, cornerRadius, cornerRadius);

			int xOffset = w - ARROW_SIZE;
			int yOffset = (h - (ARROW_SIZE + ARROW_SIZE)) / 2;
			arrowPath = FlatUIUtils.createPath(x + xOffset, y + yOffset, x + xOffset + ARROW_SIZE, y + yOffset + ARROW_SIZE, x + xOffset, y + yOffset + ARROW_SIZE + ARROW_SIZE);

		} else if (orientation == TOP) {

			balloonRect = new RoundRectangle2D.Float(x, y + ARROW_SIZE, w, h - ARROW_SIZE, cornerRadius, cornerRadius);

			int xOffset = (w - (ARROW_SIZE + ARROW_SIZE)) / 2;
			arrowPath = FlatUIUtils.createPath(x + xOffset, y + ARROW_SIZE, x + xOffset + ARROW_SIZE, y, x + xOffset + ARROW_SIZE + ARROW_SIZE, y + ARROW_SIZE);

		} else if (orientation == BOTTOM) {

			balloonRect = new RoundRectangle2D.Float(x, y, w, h - ARROW_SIZE, cornerRadius, cornerRadius);

			int xOffset = (w - (ARROW_SIZE + ARROW_SIZE)) / 2;
			int yOffset = h - ARROW_SIZE;
			arrowPath = FlatUIUtils.createPath(x + xOffset, y + yOffset, x + xOffset + ARROW_SIZE, y + yOffset + ARROW_SIZE, x + xOffset + ARROW_SIZE + ARROW_SIZE, y + yOffset);

		} else {

			balloonRect = new RoundRectangle2D.Float(x + ARROW_SIZE, y, w - ARROW_SIZE, h, cornerRadius, cornerRadius);

			int yOffset = (h - (ARROW_SIZE + ARROW_SIZE)) / 2;
			arrowPath = FlatUIUtils.createPath(x + ARROW_SIZE, y + yOffset, x, y + yOffset + ARROW_SIZE, x + ARROW_SIZE, y + yOffset + ARROW_SIZE + ARROW_SIZE);

		}

		Area area = new Area(balloonRect);
		area.add(new Area(arrowPath));

		return area;

	}

}
