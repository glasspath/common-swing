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

import javax.swing.JCheckBox;

public class RemoteCellBooleanEditor extends JCheckBox {

	private final Table table;
	private final int column;

	private int preferredWidth = 150;

	private int row = -1;
	private boolean updatingValue = false;

	public RemoteCellBooleanEditor(Table table, int column) {

		this.table = table;
		this.column = column;

		addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!updatingValue) {
					row = table.convertRowIndexToModel(table.getSelectedRow());
					submit();
				}
			}
		});

	}

	public int getPreferredWidth() {
		return preferredWidth;
	}

	public void setPreferredWidth(int preferredWidth) {
		this.preferredWidth = preferredWidth;
	}

	public void setValue(boolean value) {
		updatingValue = true;
		setSelected(value);
		updatingValue = false;
	}

	private void submit() {
		if (row >= 0) {
			table.getModel().setValueAt(isSelected(), row, column);
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
