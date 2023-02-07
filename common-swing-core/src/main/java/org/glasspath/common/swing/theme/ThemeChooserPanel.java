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
package org.glasspath.common.swing.theme;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.glasspath.common.swing.graphics.NinePatch;

public class ThemeChooserPanel extends JPanel {

	private final JRadioButton lightRadioButton;
	private final JRadioButton darkRadioButton;

	public ThemeChooserPanel() {

		GridBagLayout layout = new GridBagLayout();
		layout.rowWeights = new double[] { 0.0, 0.0, 0.1 };
		layout.rowHeights = new int[] { 40, 210, 10 };
		layout.columnWeights = new double[] { 0.1, 0.1 };
		layout.columnWidths = new int[] { 100, 100 };
		setLayout(layout);

		ButtonGroup buttonGroup = new ButtonGroup();

		lightRadioButton = new JRadioButton("Light");
		buttonGroup.add(lightRadioButton);
		add(lightRadioButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		darkRadioButton = new JRadioButton("Dark");
		buttonGroup.add(darkRadioButton);
		add(darkRadioButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		lightRadioButton.setSelected(true);

		ActionListener actionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				selectionChanged(isDarkThemeSelected());
			}
		};

		lightRadioButton.addActionListener(actionListener);
		darkRadioButton.addActionListener(actionListener);

		JLabel themeLabel = new JLabel("Choose your theme");
		themeLabel.setHorizontalAlignment(JLabel.CENTER);
		add(themeLabel, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

		add(new ThemePreviewPanel(false), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(new ThemePreviewPanel(true), new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

	}

	public boolean isDarkThemeSelected() {
		return darkRadioButton.isSelected();
	}

	public void selectionChanged(boolean darkThemeSelected) {

	}

	private class ThemePreviewPanel extends JPanel {

		private final boolean dark;
		private final NinePatch shadow = new NinePatch(new ImageIcon(getClass().getClassLoader().getResource("org/glasspath/common/swing/graphics/shadow.png")).getImage(), 10, 10); //$NON-NLS-1$

		public ThemePreviewPanel(boolean dark) {

			this.dark = dark;

			setPreferredSize(new Dimension(300, 200));

		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);

			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			shadow.paintNinePatch(g2d, 10 - 7, 10 - 7, getWidth() - 20 + 14, getHeight() - 20 + 14);

			RoundRectangle2D roundRect = new RoundRectangle2D.Double(10, 10, getWidth() - 20, getHeight() - 20, 8, 8);
			g2d.setColor(dark ? new Color(60, 63, 65) : new Color(247, 247, 247));
			g2d.fill(roundRect);

			g2d.setColor(dark ? new Color(55, 55, 55) : new Color(235, 235, 235));
			g2d.drawLine(10, 30, getWidth() - 11, 30);

		}

	}

}
