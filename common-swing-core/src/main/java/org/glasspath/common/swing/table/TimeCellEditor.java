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

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;

public class TimeCellEditor extends DefaultCellEditor {

	public TimeCellEditor() {
		super(new TimeComboBox());
		setClickCountToStart(2);
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		TimeComboBox editor = (TimeComboBox) super.getTableCellEditorComponent(table, value, isSelected, row, column);
		if (value != null) {
			for (int i = 0; i < editor.getItemCount(); i++) {
				if (editor.getItemAt(i).equals(value.toString())) {
					editor.setSelectedIndex(i);
				}
			}
		}
		return editor;
	}

	@Override
	public boolean stopCellEditing() {
		if (getCellEditorValue() != null) {
			return super.stopCellEditing();
		} else {
			return false;
		}
	}

	@Override
	public Object getCellEditorValue() {
		return (String) super.getCellEditorValue();
	}

}
