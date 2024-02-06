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
package org.glasspath.common.swing.color;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.MenuSelectionManager;

import org.glasspath.common.icons.Icons;
import org.glasspath.common.swing.button.SplitButton;
import org.glasspath.common.swing.theme.Theme;

public class ColorButton extends SplitButton {

	public static final int PAINT_MODE_DEFAULT = 0;
	public static final int PAINT_MODE_BOTTOM = 1;
	public static final int PAINT_MODE_BOTTOM_RIGHT_OVAL = 2;

	private int paintMode = PAINT_MODE_DEFAULT;
	private Color color = null;

	public ColorButton() {
		this(null);
	}

	public ColorButton(Action action) {

		configureForToolBar();
		setArrowOffset(-3);

		setIcon(Icons.null_16x16);

		JPopupMenu popup = new JPopupMenu();
		setPopupMenu(popup);

		ColorChooserPanel colorChooserPanel = new ColorChooserPanel(color) {

			@Override
			protected Frame getFrame() {
				return ColorButton.this.getFrame();
			}
		};
		popup.add(colorChooserPanel);
		colorChooserPanel.getColorChooser().getActionButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				MenuSelectionManager.defaultManager().clearSelectedPath();
			}
		});
		colorChooserPanel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				MenuSelectionManager.defaultManager().clearSelectedPath();

				final Color color;
				if (ColorChooserDialog.NULL_COLOR.equals(colorChooserPanel.getSelectedColor())) {
					color = null;
				} else {
					color = colorChooserPanel.getSelectedColor();
				}

				ColorButton.this.color = color;
				ColorButton.this.repaint();

				colorChooserPanel.getColorChooser().setSelectedColor(color);

				if (action != null) {
					action.actionPerformed(e);
				}

			}
		});

	}

	protected Frame getFrame() {
		return null;
	}

	public int getPaintMode() {
		return paintMode;
	}

	public void setPaintMode(int paintMode) {
		this.paintMode = paintMode;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if (color != null) {
			g2d.setColor(color);
		} else {
			g2d.setColor(Theme.isDark() ? ColorUtils.GRAY_75 : ColorUtils.GRAY_220);
		}

		if (paintMode == PAINT_MODE_DEFAULT) {
			g2d.fillRect(6, 5, getHeight() - 10, getHeight() - 10);
		} else if (paintMode == PAINT_MODE_BOTTOM) {
			g2d.fillRect(6, getHeight() - 7, getWidth() - getSplitWidth() - 11, 3);
		} else if (paintMode == PAINT_MODE_BOTTOM_RIGHT_OVAL) {
			g2d.fillOval(getWidth() - getSplitWidth() - 10, getHeight() - 12, 8, 8);
		}

	}

}
