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
package org.glasspath.common.swing.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.glasspath.common.swing.SwingUtils;
import org.glasspath.common.swing.color.ColorUtils;
import org.glasspath.common.swing.theme.Theme;

public class DefaultDialogHeader extends JPanel {

	public static final Color TITLE_COLOR;
	static {
		if (Theme.isDark()) {
			TITLE_COLOR = ColorUtils.TEXT_COLOR;
		} else {
			TITLE_COLOR = ColorUtils.GRAY_50;
		}
	}

	private ImageIcon icon = null;
	private String title = null;
	private int titleOffset = 0;

	public DefaultDialogHeader() {

	}

	public ImageIcon getIcon() {
		return icon;
	}

	public void setIcon(ImageIcon icon) {
		this.icon = icon;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getTitleOffset() {
		return titleOffset;
	}

	public void setTitleOffset(int titleOffset) {
		this.titleOffset = titleOffset;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		Graphics2D g2d = (Graphics2D) g;

		int height = getHeight();
		int width = getWidth();

		g2d.setColor(ColorUtils.TITLE_BAR_COLOR);
		g2d.fillRect(0, 0, width, height);

		if (icon != null) {
			icon.paintIcon(this, g2d, width - (icon.getIconWidth() + 15), ((height - icon.getIconHeight()) / 2) + 2);
		}

		if (title != null) {

			g2d.setFont(getFont().deriveFont(16.0F));
			g2d.setColor(TITLE_COLOR);
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			SwingUtils.drawString(this, g2d, title, 20 + titleOffset, 35);

		}

	}

	@Override
	public Dimension getPreferredSize() {
		Dimension preferredSize = super.getPreferredSize();
		preferredSize.setSize(450, 60);
		return preferredSize;
	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(450, 60);
	}

}
