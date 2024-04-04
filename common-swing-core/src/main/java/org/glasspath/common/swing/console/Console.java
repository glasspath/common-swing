/*
 * This file is part of Glasspath Common.
 * Copyright (C) 2011 - 2023 Remco Poelstra
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
package org.glasspath.common.swing.console;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import org.glasspath.common.swing.color.ColorUtils;
import org.glasspath.common.swing.theme.Theme;

import com.formdev.flatlaf.ui.FlatUIUtils;

public class Console extends JPanel {

	private final JTextArea consoleTextArea;
	private final JScrollPane consoleTextAreaScrollPane;

	private int lineCount = 0;
	private int maxNumberOfLines = 100;

	public Console() {

		setLayout(new BorderLayout());

		consoleTextArea = new JTextArea();
		consoleTextArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		if (Theme.isDark()) {
			consoleTextArea.setBackground(ColorUtils.DARK_31);
		}

		Font font = UIManager.getFont("monospaced.font"); //$NON-NLS-1$
		if (font != null) {
			consoleTextArea.setFont(FlatUIUtils.nonUIResource(font));
		}

		consoleTextAreaScrollPane = new JScrollPane(consoleTextArea);
		consoleTextAreaScrollPane.setBorder(null);
		consoleTextAreaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		add(consoleTextAreaScrollPane, BorderLayout.CENTER);

		consoleTextArea.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				scrollToBottom();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				scrollToBottom();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				scrollToBottom();
			}
		});

	}

	public JTextArea getConsoleTextArea() {
		return consoleTextArea;
	}

	public JScrollPane getConsoleTextAreaScrollPane() {
		return consoleTextAreaScrollPane;
	}

	public void setEditable(boolean editable) {
		consoleTextArea.setEditable(editable);
	}

	private void scrollToBottom() {
		consoleTextAreaScrollPane.getVerticalScrollBar().setValue(consoleTextAreaScrollPane.getVerticalScrollBar().getMaximum());
	}

	public void addLine(String line) {

		consoleTextArea.setText(consoleTextArea.getText() + line + "\n"); //$NON-NLS-1$ //TODO: The line itself can also contain a line-break..

		lineCount++;
		if (maxNumberOfLines > 0 && lineCount >= maxNumberOfLines) {

			try {

				final int firstLineEndOffset = consoleTextArea.getLineEndOffset(0);
				consoleTextArea.replaceRange("", 0, firstLineEndOffset); //$NON-NLS-1$

				lineCount--;

			} catch (BadLocationException e) {
				e.printStackTrace();
			}

		}

	}

	public void clear() {
		consoleTextArea.setText(""); //$NON-NLS-1$
		lineCount = 0;
	}

}
