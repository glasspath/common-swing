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
import javax.swing.JComboBox;
import javax.swing.JTable;

public class StringListCellEditor extends DefaultCellEditor {

	private final String[] stringList;

	public StringListCellEditor(String[] stringList) {
		super(new JComboBox<String>(stringList));
		this.stringList = stringList;
		setClickCountToStart(2);
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		JComboBox<String> editor = (JComboBox<String>) super.getTableCellEditorComponent(table, value, isSelected, row, column);
		editor.setSelectedIndex((Integer) value);
		return editor;
	}

	@Override
	public Object getCellEditorValue() {
		String value = (String) super.getCellEditorValue();
		for (int i = 0; i < stringList.length; i++) {
			if (stringList[i].equals(value)) {
				return i;
			}
		}
		return -1;
	}

}