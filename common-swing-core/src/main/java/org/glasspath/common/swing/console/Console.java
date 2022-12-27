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
