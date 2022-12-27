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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.glasspath.common.swing.theme.Theme;

public class ColorChooserPanel extends JPanel {

	public static final int MARGIN = 3;
	public static final int COLOR_RECT_SIZE = 22;
	public static final int GRID_SPACING = 1;
	public static final int ROW_COUNT = 5;
	public static final int COLUMN_COUNT = 7;
	public static final int PALETTE_WIDTH;
	public static final int PALETTE_HEIGHT;
	public static final Color PALETTE_GRID_COLOR = new Color(235, 235, 235);
	public static final Color[][] COLORS = new Color[COLUMN_COUNT][ROW_COUNT];
	static {

		PALETTE_WIDTH = MARGIN + (COLUMN_COUNT * COLOR_RECT_SIZE) + ((COLUMN_COUNT - 1) * GRID_SPACING) + MARGIN;
		PALETTE_HEIGHT = MARGIN + (ROW_COUNT * COLOR_RECT_SIZE) + ((ROW_COUNT - 1) * GRID_SPACING) + MARGIN;

		// Gray
		COLORS[0][0] = new Color(255, 255, 255);
		COLORS[0][1] = new Color(190, 190, 190);
		COLORS[0][2] = new Color(125, 125, 125);
		COLORS[0][3] = new Color(65, 65, 65);
		COLORS[0][4] = new Color(0, 0, 0);

		// Green
		COLORS[1][0] = new Color(200, 242, 194);
		COLORS[1][1] = new Color(150, 217, 141);
		COLORS[1][2] = new Color(115, 191, 105);
		COLORS[1][3] = new Color(86, 166, 75);
		COLORS[1][4] = new Color(55, 135, 45);

		// Yellow
		COLORS[2][0] = new Color(255, 248, 153);
		COLORS[2][1] = new Color(255, 238, 82);
		COLORS[2][2] = new Color(250, 222, 42);
		COLORS[2][3] = new Color(242, 204, 12);
		COLORS[2][4] = new Color(224, 180, 0);

		// Orange
		COLORS[3][0] = new Color(255, 203, 125);
		COLORS[3][1] = new Color(255, 179, 87);
		COLORS[3][2] = new Color(255, 152, 48);
		COLORS[3][3] = new Color(255, 120, 10);
		COLORS[3][4] = new Color(250, 100, 0);

		// Red
		COLORS[4][0] = new Color(255, 166, 176);
		COLORS[4][1] = new Color(255, 115, 131);
		COLORS[4][2] = new Color(242, 73, 92);
		COLORS[4][3] = new Color(224, 47, 68);
		COLORS[4][4] = new Color(196, 22, 42);

		// Purple
		COLORS[5][0] = new Color(222, 182, 242);
		COLORS[5][1] = new Color(202, 149, 229);
		COLORS[5][2] = new Color(184, 119, 217);
		COLORS[5][3] = new Color(163, 82, 204);
		COLORS[5][4] = new Color(143, 59, 184);

		// Blue
		COLORS[6][0] = new Color(192, 216, 255);
		COLORS[6][1] = new Color(138, 184, 255);
		COLORS[6][2] = new Color(87, 148, 242);
		COLORS[6][3] = new Color(50, 116, 217);
		COLORS[6][4] = new Color(31, 96, 196);

	}

	private final ColorChooser colorChooser;

	private final List<ActionListener> actionListeners = new ArrayList<>();

	private Color selectedColor = null;

	public ColorChooserPanel() {
		this(null);
	}

	public ColorChooserPanel(Color selectedColor) {

		this.selectedColor = selectedColor;

		setFocusable(false);
		setOpaque(false);
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));

		final ColorPalette colorPalette = new ColorPalette();
		add(colorPalette, BorderLayout.CENTER);

		colorChooser = new ColorChooser() {

			@Override
			protected Frame getFrame() {
				return ColorChooserPanel.this.getFrame();
			}
		};
		colorChooser.setFocusable(false);
		colorChooser.setSelectedColor(selectedColor);
		colorChooser.setOpaque(false);
		colorChooser.getColorTextField().setEditable(false);
		colorChooser.getColorTextField().setFocusable(false);
		colorChooser.getActionButton().setFocusable(false);
		add(colorChooser, BorderLayout.SOUTH);

		colorChooser.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Color color = colorChooser.getSelectedColor();

				ColorChooserPanel.this.selectedColor = color;
				fireActionPerformed(new ColorEvent(e, color));

			}
		});

	}

	protected Frame getFrame() {
		return null;
	}

	public ColorChooser getColorChooser() {
		return colorChooser;
	}

	public void addActionListener(ActionListener actionListener) {
		actionListeners.add(actionListener);
	}

	public void removeActionListener(ActionListener actionListener) {
		actionListeners.remove(actionListener);
	}

	private void fireActionPerformed(ActionEvent actionEvent) {
		for (ActionListener actionListener : actionListeners) {
			actionListener.actionPerformed(actionEvent);
		}
	}

	public Color getSelectedColor() {
		return selectedColor;
	}

	public class ColorPalette extends JPanel {

		private Point mouse = null;
		private Color colorAtMouse = null;

		public ColorPalette() {

			setFocusable(false);
			setOpaque(false);
			setPreferredSize(new Dimension(PALETTE_WIDTH, PALETTE_HEIGHT));

			addMouseMotionListener(new MouseAdapter() {

				@Override
				public void mouseMoved(MouseEvent e) {
					mouse = e.getPoint();
					colorAtMouse = null;
					repaint();
				}

				@Override
				public void mouseExited(MouseEvent e) {
					mouse = null;
					colorAtMouse = null;
					repaint();
				}
			});

			addMouseListener(new MouseAdapter() {

				@Override
				public void mouseReleased(MouseEvent e) {
					if (colorAtMouse != null) {
						selectedColor = colorAtMouse;
						fireActionPerformed(new ColorEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ColorChooser.ACTION_EVENT_COMMAND), colorAtMouse));
					}
				}
			});

		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);

			final Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			int x = MARGIN + ((getWidth() - PALETTE_WIDTH) / 2);
			int y = MARGIN + ((getHeight() - PALETTE_HEIGHT) / 2);

			if (!Theme.isDark()) {
				g2d.setColor(PALETTE_GRID_COLOR);
				g2d.fillRect(x - 1, y - 1, (COLUMN_COUNT * COLOR_RECT_SIZE) + ((COLUMN_COUNT - 1) * GRID_SPACING) + 2, (ROW_COUNT * COLOR_RECT_SIZE) + ((ROW_COUNT - 1) * GRID_SPACING) + 2);
			}

			for (int col = 0; col < COLUMN_COUNT; col++) {

				y = MARGIN + ((getHeight() - PALETTE_HEIGHT) / 2);

				for (int row = 0; row < ROW_COUNT; row++) {

					if (COLORS[col][row] != null) {

						g2d.setColor(COLORS[col][row]);
						g2d.fillRect(x, y, COLOR_RECT_SIZE, COLOR_RECT_SIZE);

						if (selectedColor != null && selectedColor.equals(COLORS[col][row])) {
							g2d.setColor(Color.gray);
							g2d.drawRect(x - 1, y - 1, COLOR_RECT_SIZE + 1, COLOR_RECT_SIZE + 1);
						}

						if (mouse != null && mouse.x >= x && mouse.x < x + COLOR_RECT_SIZE && mouse.y >= y && mouse.y < y + COLOR_RECT_SIZE) {
							colorAtMouse = COLORS[col][row];
							g2d.setColor(Theme.isDark() ? Color.lightGray : Color.darkGray);
							g2d.drawRect(x - 1, y - 1, COLOR_RECT_SIZE + 1, COLOR_RECT_SIZE + 1);
						}

					}

					y += COLOR_RECT_SIZE + GRID_SPACING;

				}

				x += COLOR_RECT_SIZE + GRID_SPACING;

			}

		}

	}

	public static class ColorEvent extends ActionEvent {

		public final Color color;

		public ColorEvent(ActionEvent e, Color color) {
			super(e.getSource(), e.getID(), e.getActionCommand());
			this.color = color;
		}

	}

}
