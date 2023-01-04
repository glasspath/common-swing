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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class DialogUtils {

	private DialogUtils() {

	}

	public static void showInformationMessage(JFrame frame, String title, String message) {
		JOptionPane.showOptionDialog(frame, message, title, JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[] { "     Ok     " }, "     Ok     ");
	}

	public static void showWarningMessage(JFrame frame, String title, String message) {
		JOptionPane.showOptionDialog(frame, message, title, JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[] { "     Ok     " }, "     Ok     ");
	}

	public static void showWarningMessage(JFrame frame, String title, String message, Exception exception) {

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		JLabel titleLabel = new JLabel(message);
		titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		panel.add(titleLabel, BorderLayout.NORTH);

		JLabel detailsLabel = new JLabel("<html><body><u>Details</u></body></html>");
		detailsLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5));
		detailsLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		panel.add(detailsLabel, BorderLayout.CENTER);
		detailsLabel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {

				detailsLabel.setText(exception.getLocalizedMessage());

				StringWriter stringWriter = new StringWriter();
				PrintWriter printWriter = new PrintWriter(stringWriter);
				exception.printStackTrace(printWriter);

				JTextArea textArea = new JTextArea(stringWriter.toString());

				JScrollPane scrollPane = new JScrollPane(textArea);
				scrollPane.setPreferredSize(new Dimension(500, 350));
				panel.add(scrollPane, BorderLayout.SOUTH);

				JDialog dialog = getDialog(panel);
				if (dialog != null) {

					dialog.invalidate();
					dialog.validate();

					dialog.pack();
					dialog.setLocationRelativeTo(frame);

				}

			}
		});

		JOptionPane.showOptionDialog(frame, panel, title, JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[] { "     Ok     " }, "     Ok     ");

	}

	public static JDialog getDialog(Component component) {

		if (component != null) {

			Component parent = component.getParent();
			while (parent != null) {

				if (parent instanceof JDialog) {
					return (JDialog) parent;
				}

				parent = parent.getParent();

			}

		}

		return null;

	}

}
