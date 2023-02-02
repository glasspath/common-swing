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

import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.JFormattedTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import org.glasspath.common.format.FormatUtils;
import org.glasspath.common.swing.ApplicationContext;
import org.glasspath.common.swing.ContentListener;

public class RemoteCellFloatEditor extends JFormattedTextField {

	private final Table table;
	private final int column;

	private int preferredWidth = 150;

	private int row = -1;
	private boolean updatingValue = false;
	private boolean valueChanged = false;

	public RemoteCellFloatEditor(ApplicationContext context, Table table, int column) {

		this.table = table;
		this.column = column;

		NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
		numberFormat.setMaximumFractionDigits(2);
		numberFormat.setMinimumFractionDigits(2);
		numberFormat.setMinimumIntegerDigits(1);

		setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(numberFormat)));
		setHorizontalAlignment(SwingConstants.RIGHT);

		getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				if (!updatingValue) {
					valueChanged = true;
					row = table.convertRowIndexToModel(table.getSelectedRow());
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				if (!updatingValue) {
					valueChanged = true;
					row = table.convertRowIndexToModel(table.getSelectedRow());
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				if (!updatingValue) {
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

		context.addContentListener(new ContentListener() {

			@Override
			public void contentOpened() {

			}

			@Override
			public void contentClosing() {
				submit();
			}
		});

	}

	public int getPreferredWidth() {
		return preferredWidth;
	}

	public void setPreferredWidth(int preferredWidth) {
		this.preferredWidth = preferredWidth;
	}

	public void setValue(double value) {
		updatingValue = true;
		setText(FormatUtils.CURRENCY_FORMAT.format(value));
		updatingValue = false;
	}

	private void submit() {
		if (valueChanged && row >= 0) {

			String stringValue = getText();
			if (stringValue != null && stringValue.length() >= 0) {

				try {
					table.getModel().setValueAt(FormatUtils.parseFloat(stringValue), row, column);
					valueChanged = false;
				} catch (Exception e) {
					cancel();
				}

			} else {
				cancel();
			}

		}
	}

	private void cancel() {
		if (row >= 0) {
			valueChanged = false;
			setValue(table.getModel().getValueAt(row, column));
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
