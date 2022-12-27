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
package org.glasspath.common.swing.button;

import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.glasspath.common.swing.theme.Theme;

public class ActionButtonPanel extends JPanel {

	public final JButton actionButton;

	public ActionButtonPanel() {

		setOpaque(false);

		final GridBagLayout layout = new GridBagLayout();
		layout.rowWeights = new double[] { 0.0 };
		layout.rowHeights = new int[] { 25 };
		layout.columnWeights = new double[] { 0.1, 0.0, 0.0 };
		layout.columnWidths = new int[] { 50, Theme.isSystemTheme() ? 3 : 0, 25 };
		setLayout(layout);

		actionButton = new JButton() {

			@Override
			public void paint(Graphics g) {
				super.paint(g);
				paintButton(g);
			}
		};
		actionButton.setMargin(new Insets(0, 3, 0, 3));
		add(actionButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

	}

	public JButton getActionButton() {
		return actionButton;
	}

	public void addComponent(JComponent component) {
		add(component, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 0, 1, 0), 0, 0));
	}

	public void addComponent(JComponent component, int anchor, int fill, Insets insets) {
		add(component, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, anchor, fill, insets, 0, 0));
	}

	protected void paintButton(Graphics g) {

	}

}
