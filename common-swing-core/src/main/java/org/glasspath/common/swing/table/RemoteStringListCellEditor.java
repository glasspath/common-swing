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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JComboBox;

public class RemoteStringListCellEditor extends JComboBox<String> {

	private final Table table;
	private int row;
	private final int column;

	private boolean updatingValue = false;
	private boolean valueChanged = false;

	public RemoteStringListCellEditor(Table table, int column, String[] stringList) {

		super(stringList);

		this.table = table;
		this.column = column;

		addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent event) {
				submit();
			}

			@Override
			public void focusGained(FocusEvent event) {
				table.stopEditing();
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

	}

	public void setSelectedValue(int index) {
		updatingValue = true;
		setSelectedIndex(index);
		updatingValue = false;
	}

	private void submit() {
		if (valueChanged && row >= 0) {
			valueChanged = false;
			table.getModel().setValueAt(getSelectedIndex(), row, column);
		}
	}

	private void cancel() {
		if (row >= 0) {
			valueChanged = false;
			setSelectedIndex((Integer) table.getModel().getValueAt(row, column));
		}
	}

}
