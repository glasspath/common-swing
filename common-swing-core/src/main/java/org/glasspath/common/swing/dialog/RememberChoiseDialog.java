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
package org.glasspath.common.swing.dialog;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

import org.glasspath.common.swing.resources.Resources;

public class RememberChoiseDialog {

	private final JCheckBox checkBox;
	private int choise = -1;

	public RememberChoiseDialog() {
		checkBox = new JCheckBox(Resources.getString("ApplyChoiseToSelection")); //$NON-NLS-1$
	}

	public boolean isChoiseRemebered() {
		return checkBox.isSelected();
	}

	public int getChoise() {
		return choise;
	}

	public int showYesNoOptionDialog(Component parentComponent, String title, String text) {
		Object[] params = { text, checkBox };
		choise = JOptionPane.showOptionDialog(parentComponent, params, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[] { Resources.getString("Yes"), Resources.getString("No") }, Resources.getString("No")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return choise;
	}

	public void clear() {
		checkBox.setSelected(false);
		choise = -1;
	}

}
