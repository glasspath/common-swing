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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Date;
import java.util.TimeZone;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.glasspath.common.swing.color.ColorUtils;
import org.glasspath.common.swing.date.DatePicker;
import org.glasspath.common.swing.date.DatePicker.MonthView;
import org.glasspath.common.swing.theme.Theme;
import org.jdesktop.swingx.JXDatePicker;

public class RemoteCellDatePicker extends JXDatePicker {

	private final Table table;
	private final int column;
	private int modelIndex = -1;
	private int preferredWidth = 150;
	private boolean updatingValue = false;
	private boolean valueChanged = false;

	public RemoteCellDatePicker(Table table, int column) {

		this.table = table;
		this.column = column;

		if (Theme.isDark()) {
			setBackground(ColorUtils.DARK_43);
		}

		setMonthView(new MonthView());

		getEditor().getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				if (!updatingValue) {
					valueChanged = true;
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				if (!updatingValue) {
					valueChanged = true;
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				if (!updatingValue) {
					valueChanged = true;
				}
			}
		});

		getEditor().addKeyListener(new KeyListener() {

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

		addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!updatingValue) {
					valueChanged = true;
					submit();
				}
			}
		});

	}

	@Override
	public void setTimeZone(TimeZone tz) {
		super.setTimeZone(tz);
		DatePicker.configureLinkPanel(this); // TODO: Setting time-zone causes issues with link panel background
	}

	public int getPreferredWidth() {
		return preferredWidth;
	}

	public void setPreferredWidth(int preferredWidth) {
		this.preferredWidth = preferredWidth;
	}

	public void setValue(Date date, int modelIndex) {
		setValue(date, modelIndex, false);
	}

	public void setValue(Date date, int modelIndex, boolean submit) {

		// TODO: This was added to ensure that changes or submitted when
		// changing selection, this however causes problems with undo/redo
		// submit();

		updatingValue = true;
		setDate(date);
		this.modelIndex = modelIndex;
		updatingValue = false;

		if (submit) {
			valueChanged = true;
			submit();
		}

	}

	private void submit() {
		if (valueChanged && modelIndex >= 0) {
			valueChanged = false;
			table.getModel().setValueAt(getDate(), modelIndex, column);
		}
	}

	private void cancel() {
		if (modelIndex >= 0) {
			valueChanged = false;
			setDate((Date) table.getModel().getValueAt(modelIndex, column));
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
