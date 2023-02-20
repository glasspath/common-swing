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
package org.glasspath.common.swing.table;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.glasspath.common.swing.ApplicationContext;
import org.glasspath.common.swing.ContentListener;
import org.glasspath.common.swing.color.ColorUtils;

public class RemoteCellTextAreaEditor extends JTextArea {

	private final Table table;
	private final int column;
	private int modelIndex = -1;
	private int preferredWidth = 150;
	private boolean updatingValue = false;
	private boolean valueChanged = false;
	private boolean valueInvalid = false;

	public RemoteCellTextAreaEditor(ApplicationContext context, Table table, int column) {

		this.table = table;
		this.column = column;

		getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				if (!updatingValue) {
					valueChanged = true;
					modelIndex = table.convertRowIndexToModel(table.getSelectedRow());
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				if (!updatingValue) {
					valueChanged = true;
					modelIndex = table.convertRowIndexToModel(table.getSelectedRow());
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				if (!updatingValue) {
					valueChanged = true;
					modelIndex = table.convertRowIndexToModel(table.getSelectedRow());
				}
			}
		});

		addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {

			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					// submit();
				} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					cancel();
				}
			}
		});

		addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				submit();
			}

			@Override
			public void focusGained(FocusEvent e) {
				table.stopEditing();
			}
		});

		context.addContentListener(new ContentListener() {

			@Override
			public void contentOpened() {

			}

			@Override
			public void contentClosing() {
				submit();
			}
		});

		// See https://bugs.openjdk.org/browse/JDK-8298017
		setAutoscrolls(false);

	}

	public int getPreferredWidth() {
		return preferredWidth;
	}

	public void setPreferredWidth(int preferredWidth) {
		this.preferredWidth = preferredWidth;
	}

	@Override
	public void setText(String t) {

		submit();

		updatingValue = true;
		super.setText(t);
		updatingValue = false;

	}

	private void submit() {
		if (valueChanged && modelIndex >= 0) {
			valueChanged = false;
			table.getModel().setValueAt(getText(), modelIndex, column);
		}
	}

	private void cancel() {
		if (modelIndex >= 0) {
			valueChanged = false;
			setText(table.getModel().getValueAt(modelIndex, column).toString());
		}
	}

	public void setValueInvalid(boolean valueInvalid) {
		this.valueInvalid = valueInvalid;
	}

	@Override
	public Color getBackground() {
		if (valueInvalid) {
			return ColorUtils.INVALID_INPUT_BACKGROUND;
		} else {
			return super.getBackground();
		}
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension size = super.getPreferredSize();
		if (preferredWidth > 0) {
			size.width = preferredWidth;
		}
		return size;
	}

}
