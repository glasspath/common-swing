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
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.glasspath.common.swing.button.ActionButtonPanel;

public class ColorChooser extends ActionButtonPanel {

	public static final String ACTION_EVENT_COMMAND = "ColorChanged"; //$NON-NLS-1$

	public final JTextField colorTextField;
	private final List<ActionListener> actionListeners = new ArrayList<>();

	private Color selectedColor = null;
	private boolean updatingColorTextField = false;

	public ColorChooser() {

		super();

		colorTextField = new JTextField();
		colorTextField.setPreferredSize(new Dimension(25, 25));
		addComponent(colorTextField);
		colorTextField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				parseColorTextField();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				parseColorTextField();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				parseColorTextField();
			}
		});

		actionButton.setIcon(null);
		actionButton.setPreferredSize(new Dimension(25, 25));
		actionButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				JColorChooser colorChooser = new JColorChooser();
				colorChooser.setColor(selectedColor);

				JDialog dialog = new ColorChooserDialog(getFrame(), "Edit color", true, null, colorChooser, new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {

						final Color color;
						if (ColorChooserDialog.NULL_COLOR.equals(colorChooser.getColor())) {
							color = null;
						} else {
							color = colorChooser.getColor();
						}

						selectedColor = color;
						updateColorTextField();
						actionButton.repaint();
						fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ACTION_EVENT_COMMAND));

					}
				}, null);

				dialog.setLocationRelativeTo(getFrame());
				dialog.setVisible(true);
				dialog.requestFocusInWindow();

			}
		});

	}
	
	protected Frame getFrame() {
		return null;
	}

	public JTextField getColorTextField() {
		return colorTextField;
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

	public void setSelectedColor(Color selectedColor) {
		this.selectedColor = selectedColor;
		updateColorTextField();
		actionButton.repaint();
	}

	@Override
	protected void paintButton(Graphics g) {

		if (selectedColor != null) {
			g.setColor(selectedColor);
			g.fillRect(6, 6, actionButton.getWidth() - 12, actionButton.getHeight() - 12);
		}

	}

	private void updateColorTextField() {

		updatingColorTextField = true;

		if (selectedColor != null) {
			colorTextField.setText(ColorUtils.toHex(selectedColor));
		} else {
			colorTextField.setText(""); //$NON-NLS-1$
		}

		updatingColorTextField = false;

	}

	private void parseColorTextField() {

		if (!updatingColorTextField) {

			selectedColor = ColorUtils.fromHex(colorTextField.getText());
			actionButton.repaint();

			fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ACTION_EVENT_COMMAND));

		}

	}

}
