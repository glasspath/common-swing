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

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class DialogUtils {

	private DialogUtils() {

	}

	public static void showInformationMessage(JFrame frame, String title, String message) {
		JOptionPane.showOptionDialog(frame, message, title, JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[] { "     Ok     " }, "     Ok     ");
	}

	public static void showWarningMessage(JFrame frame, String title, String message) {
		JOptionPane.showOptionDialog(frame, message, title, JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[] { "     Ok     " }, "     Ok     ");
	}

}
