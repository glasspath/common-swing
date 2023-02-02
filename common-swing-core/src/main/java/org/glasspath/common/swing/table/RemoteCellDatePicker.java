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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.glasspath.common.date.DateUtils;
import org.glasspath.common.format.FormatUtils;
import org.glasspath.common.swing.color.ColorUtils;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXMonthView;

public class RemoteCellDatePicker extends JXDatePicker {

	private final Table table;
	private final int column;

	private int preferredWidth = 150;

	private int row = -1;
	private boolean updatingValue = false;
	private boolean valueChanged = false;

	public RemoteCellDatePicker(Table table, int column) {

		this.table = table;
		this.column = column;

		JXMonthView monthView = getMonthView();
		monthView.setBorder(BorderFactory.createEmptyBorder(8, 10, 10, 10));
		monthView.setShowingLeadingDays(true);
		monthView.setShowingTrailingDays(true);
		monthView.setTodayBackground(ColorUtils.createTransparentColor(Table.SELECTION_BACKGROUND, 150));
		monthView.setMonthStringBackground(new Color(150, 150, 150, 20));

		setTimeZone(DateUtils.TIME_ZONE);
		setFormats(FormatUtils.DATE_FORMAT);

		getEditor().getDocument().addDocumentListener(new DocumentListener() {

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
					row = table.convertRowIndexToModel(table.getSelectedRow());
					submit();
				}
			}
		});

		// setLinkPanel(null);

	}

	public int getPreferredWidth() {
		return preferredWidth;
	}

	public void setPreferredWidth(int preferredWidth) {
		this.preferredWidth = preferredWidth;
	}

	public void setValue(Date date) {
		updatingValue = true;
		setDate(date);
		updatingValue = false;
	}

	@Override
	public void setDate(Date date) {
		super.setDate(date);
		if (!updatingValue) {
			submit();
		}
	}

	private void submit() {
		if (valueChanged && row >= 0) {
			valueChanged = false;
			table.getModel().setValueAt(getDate(), row, column);
		}
	}

	private void cancel() {
		if (row >= 0) {
			valueChanged = false;
			setDate((Date) table.getModel().getValueAt(row, column));
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
