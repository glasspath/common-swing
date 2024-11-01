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
package org.glasspath.common.swing.statusbar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.glasspath.common.swing.border.HidpiMatteBorder;
import org.glasspath.common.swing.color.ColorUtils;
import org.glasspath.common.swing.theme.Theme;

public class StatusBarPanel extends JPanel {

	public static final int HEIGHT = 20;
	public static final int DEFAULT_LEFT_MARGIN = 5;

	public static Color BG_COLOR;
	public static Color LINE_TOP_COLOR;
	public static Color SEPARATOR_DARK_COLOR;
	public static Color SEPARATOR_LIGHT_COLOR;
	static {
		Theme.register(() -> {
			if (Theme.isDark()) {
				BG_COLOR = ColorUtils.DARK_44;
				LINE_TOP_COLOR = HidpiMatteBorder.COLOR;
				SEPARATOR_DARK_COLOR = ColorUtils.GRAY_40;
				SEPARATOR_LIGHT_COLOR = ColorUtils.GRAY_75;
			} else {
				BG_COLOR = new Color(0, 0, 0, 20);
				LINE_TOP_COLOR = ColorUtils.GRAY_192;
				SEPARATOR_DARK_COLOR = ColorUtils.GRAY_192;
				SEPARATOR_LIGHT_COLOR = Color.white;
			}
		});
	}

	private final JPanel statusBar;
	private final Dimension preferredSize = new Dimension(100, HEIGHT);
	private int leftMargin = DEFAULT_LEFT_MARGIN;

	public StatusBarPanel() {

		setPreferredSize(new Dimension(100, 20));

		GridBagLayout statusBarPanelLayout = new GridBagLayout();
		statusBarPanelLayout.rowWeights = new double[] { 0.0 };
		statusBarPanelLayout.rowHeights = new int[] { HEIGHT };
		statusBarPanelLayout.columnWeights = new double[] { 0.1 };
		statusBarPanelLayout.columnWidths = new int[] { 100 };
		setLayout(statusBarPanelLayout);

		statusBar = new JPanel() {

			private int w, h, x;
			private double scale;
			private Component component;

			@Override
			public void paint(Graphics g) {

				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				w = getWidth();
				h = getHeight();
				x = 0;
				scale = g2d.getTransform().getScaleX();

				g2d.clearRect(0, 0, w, h);
				g2d.setColor(BG_COLOR);
				g2d.fillRect(0, 0, w, h);

				super.paint(g);

				if (scale >= 1.5) {
					g2d.scale(0.5, 0.5);
					h *= 2;
				}

				for (int i = 1; i < getComponentCount(); i++) {

					component = getComponent(i);

					if (component.isVisible()) {

						x = component.getX();
						if (scale >= 1.5) {
							x *= 2;
						}

						x -= 2;
						g.setColor(SEPARATOR_DARK_COLOR);
						g.drawLine(x, 0, x, h);

						x++;
						g.setColor(SEPARATOR_LIGHT_COLOR);
						// g.drawLine(x, 0, x, h); // TODO

					}

				}

				if (scale >= 1.5) {
					g2d.scale(2.0, 2.0);
					h = getHeight();
				}

				g2d.setColor(LINE_TOP_COLOR);
				if (scale >= 1.5) {
					g2d.scale(0.5, 0.5);
					g2d.drawLine(0, 0, w * 2, 0);
					g2d.scale(2.0, 2.0);
				} else {
					g2d.drawLine(0, 0, w, 0);
				}

			}
		};
		statusBar.setOpaque(false);
		statusBar.setPreferredSize(new Dimension(100, HEIGHT));
		statusBar.setLayout(new LayoutManager() {

			private int x;
			private Component component;
			private Dimension dimension;

			@Override
			public void removeLayoutComponent(Component comp) {

			}

			@Override
			public Dimension preferredLayoutSize(Container parent) {
				return preferredSize;
			}

			@Override
			public Dimension minimumLayoutSize(Container parent) {
				return preferredSize;
			}

			@Override
			public void layoutContainer(Container parent) {

				x = leftMargin;

				for (int i = 0; i < parent.getComponentCount(); i++) {

					component = parent.getComponent(i);

					if (component.isVisible()) {

						dimension = component.getPreferredSize();

						component.setBounds(x, 0, dimension.width, preferredSize.height);

						x += dimension.width + 10;

					}

				}

			}

			@Override
			public void addLayoutComponent(String name, Component comp) {

			}
		});
		add(statusBar, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

	}

	public int getLeftMargin() {
		return leftMargin;
	}

	public void setLeftMargin(int leftMargin) {
		this.leftMargin = leftMargin;
	}

	public void addComponentToRight(JComponent component, int margin) {
		add(component, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, margin), 0, 0), 0); // Has to be added at index 0, otherwise it will not be visible
	}

	public JPanel getStatusBar() {
		return statusBar;
	}

}
