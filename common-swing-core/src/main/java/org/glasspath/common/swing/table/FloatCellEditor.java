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
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import org.glasspath.common.format.FormatUtils;

public class FloatCellEditor extends DefaultCellEditor {

	public FloatCellEditor() {
		super(new JFormattedTextField());
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		JFormattedTextField editor = (JFormattedTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);

		if (value instanceof Number) {

			NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
			numberFormat.setMaximumFractionDigits(2);
			numberFormat.setMinimumFractionDigits(2);
			numberFormat.setMinimumIntegerDigits(1);

			editor.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(numberFormat)));
			editor.setHorizontalAlignment(SwingConstants.RIGHT);
			editor.setValue(value);

		}

		return editor;

	}

	@Override
	public boolean stopCellEditing() {
		try {
			this.getCellEditorValue();
			return super.stopCellEditing();
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public Object getCellEditorValue() {

		String string = (String) super.getCellEditorValue();
		if (string == null || string.length() == 0) {
			return null;
		} else {
			try {
				return FormatUtils.parseFloat(string);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

	}

}
