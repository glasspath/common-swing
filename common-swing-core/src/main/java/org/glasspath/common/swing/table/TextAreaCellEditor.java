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
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class TextAreaCellEditor extends DefaultCellEditor {

	private final JTextArea textArea;
	private final JScrollPane scrollPane;

	public TextAreaCellEditor() {

		// Install dummy JTextField
		super(new JTextField());

		// Clean up dummy JTextField
		((JTextField) editorComponent).removeActionListener(delegate);

		this.textArea = new JTextArea();
		this.scrollPane = new JScrollPane(textArea);

		editorComponent = textArea;
		clickCountToStart = 2;

		delegate = new EditorDelegate() {

			@Override
			public void setValue(Object value) {
				textArea.setText((value != null) ? value.toString() : "");
			}

			@Override
			public Object getCellEditorValue() {
				return textArea.getText();
			}
		};

	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		super.getTableCellEditorComponent(table, value, isSelected, row, column);
		return scrollPane;
	}

}
