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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

public class ComponentTitledBorder implements Border, MouseListener, SwingConstants {

	private final Component component;
	private final JComponent container;
	private final Border border;
	private int offset = 5;
	private Rectangle rect = null;

	public ComponentTitledBorder(Component component, JComponent container, Border border) {

		this.component = component;
		this.container = container;
		this.border = border;

		container.addMouseListener(this);

	}

	@Override
	public boolean isBorderOpaque() {
		return true;
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {

		int yOffset = (getBorderInsets(c).top - border.getBorderInsets(c).top) / 2;
		border.paintBorder(c, g, x, y + yOffset, width, height - yOffset);

		Dimension size = component.getPreferredSize();
		rect = new Rectangle(offset, 0, size.width, size.height);

		SwingUtilities.paintComponent(g, component, (Container) c, rect);

	}

	@Override
	public Insets getBorderInsets(Component c) {
		Dimension size = component.getPreferredSize();
		Insets insets = border.getBorderInsets(c);
		insets.top = Math.max(insets.top, size.height);
		return insets;
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		dispatchEvent(event);
	}

	@Override
	public void mouseEntered(MouseEvent event) {
		dispatchEvent(event);
	}

	@Override
	public void mouseExited(MouseEvent event) {
		dispatchEvent(event);
	}

	@Override
	public void mousePressed(MouseEvent event) {
		dispatchEvent(event);
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		dispatchEvent(event);
	}

	@SuppressWarnings("deprecation")
	private void dispatchEvent(MouseEvent event) {

		if (rect != null && rect.contains(event.getX(), event.getY())) {

			Point p = event.getPoint();
			p.translate(-offset, 0);

			component.setBounds(rect);
			component.dispatchEvent(new MouseEvent(component, event.getID(), event.getWhen(), event.getModifiers(), p.x, p.y, event.getClickCount(), event.isPopupTrigger(), event.getButton()));

			if (!component.isValid()) {
				container.repaint();
			}

		}

	}

}
