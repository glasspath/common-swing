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
package org.glasspath.common.swing.search;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTextField;

import org.glasspath.common.icons.Icons;
import org.glasspath.common.swing.SwingUtils;
import org.glasspath.common.swing.color.ColorUtils;
import org.glasspath.common.swing.resources.Resources;

public class SearchField extends JTextField {

	public static final int BUTTONS_VISIBLE_WHEN_NEEDED = 0;
	public static final int BUTTONS_VISIBLE_ALWAYS = 1;

	private int buttonPolicy = BUTTONS_VISIBLE_WHEN_NEEDED;
	private int leftMargin = 0;
	private int rightMargin = 0;

	private Color defaultBackground = getBackground();

	private boolean highlightClearIcon = false;

	public SearchField() {
		this(0, 0);
	}

	public SearchField(int leftMargin, int rightMargin) {

		this.leftMargin = leftMargin;
		this.rightMargin = rightMargin;

		setMargin(new Insets(0, leftMargin + 22, 0, rightMargin + 22));

		addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				if (isPointOverClearIcon(e.getPoint())) {
					clear();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				updateClearIcon(e.getPoint());
			}

			@Override
			public void mouseExited(MouseEvent e) {
				highlightClearIcon = false;
				repaint();
			}
		});

		addMouseMotionListener(new MouseAdapter() {

			@Override
			public void mouseMoved(MouseEvent e) {
				updateClearIcon(e.getPoint());
			}
		});

		// putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT, new JButton("X"));

		// See https://bugs.openjdk.org/browse/JDK-8298017
		setAutoscrolls(false);

	}

	public int getButtonPolicy() {
		return buttonPolicy;
	}

	public void setButtonPolicy(int buttonPolicy) {
		this.buttonPolicy = buttonPolicy;
	}

	public void clear() {

	}

	public Color getDefaultBackground() {
		return defaultBackground;
	}

	public Color getNoResultBackground() {
		return ColorUtils.INVALID_INPUT_BACKGROUND;
	}

	private boolean isPointOverClearIcon(Point point) {
		return point.x > getWidth() - (rightMargin + 25);
	}

	private void updateClearIcon(Point point) {
		if (isPointOverClearIcon(point)) {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			highlightClearIcon = true;
		} else {
			setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			highlightClearIcon = false;
		}
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		int yIcon = (int) Math.round(getHeight() / 2.0) - 8;
		int yText = getBaseline(getWidth(), getHeight());

		Icons.magnify.paintIcon(this, g, leftMargin + 6, yIcon);

		boolean paintButtons = buttonPolicy == BUTTONS_VISIBLE_ALWAYS;

		if (getText() != null && getText().length() > 0) {

			if (buttonPolicy == BUTTONS_VISIBLE_WHEN_NEEDED) {
				paintButtons = true;
			}

		} else {

			g.setColor(Color.lightGray);
			SwingUtils.drawString(this, (Graphics2D) g, Resources.getString("Search"), leftMargin + 27, yText); //$NON-NLS-1$

		}

		if (paintButtons) {

			if (highlightClearIcon) {
				Icons.closeRed.paintIcon(this, g, getWidth() - (rightMargin + 21), yIcon);
			} else {
				Icons.close.paintIcon(this, g, getWidth() - (rightMargin + 21), yIcon);
			}

		}

	}

}
