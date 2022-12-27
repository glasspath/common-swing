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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.glasspath.common.swing.ApplicationContext;
import org.glasspath.common.swing.DataListener;

public class RemoteCellIntegerEditor extends JTextField {

	private final Color errorBackground = new Color(255, 150, 150);

	private final Table table;
	private int row;
	private final int column;

	private boolean updatingText = false;
	private boolean valueChanged = false;

	private boolean error = false;

	public RemoteCellIntegerEditor(ApplicationContext context, Table table, int column) {

		this.table = table;
		this.column = column;

		setHorizontalAlignment(RIGHT);

		getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				if (!updatingText) {
					valueChanged = true;
					row = table.convertRowIndexToModel(table.getSelectedRow());
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				if (!updatingText) {
					valueChanged = true;
					row = table.convertRowIndexToModel(table.getSelectedRow());
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				if (!updatingText) {
					valueChanged = true;
					row = table.convertRowIndexToModel(table.getSelectedRow());
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
					submit();
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

		context.addDataListener(new DataListener() {

			@Override
			public void newDataLoaded() {

			}

			@Override
			public void finishEditing() {
				submit();
			}
		});

		// See https://bugs.openjdk.org/browse/JDK-8298017
		setAutoscrolls(false);

	}

	@Override
	public void setText(String t) {
		updatingText = true;
		super.setText(t);
		updatingText = false;
	}

	private void submit() {
		if (valueChanged && row >= 0) {
			try {
				int value = Integer.parseInt(getText());
				table.getModel().setValueAt(value, row, column);
				valueChanged = false;
			} catch (Exception e) {
				cancel();
			}
		}
	}

	private void cancel() {
		if (row >= 0) {
			setText(table.getModel().getValueAt(row, column).toString());
			valueChanged = false;
		}
	}

	public void setError(boolean error) {
		this.error = error;
	}

	@Override
	public Color getBackground() {
		if (error) {
			return errorBackground;
		} else {
			return super.getBackground();
		}
	}

}
