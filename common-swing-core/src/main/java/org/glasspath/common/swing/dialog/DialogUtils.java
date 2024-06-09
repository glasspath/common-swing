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
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import org.glasspath.common.swing.resources.CommonResources;
import org.jdesktop.swingx.JXBusyLabel;

public class DialogUtils {

	// TODO?
	public static final String TAB = "    "; //$NON-NLS-1$

	private DialogUtils() {

	}

	public static void showInformationMessage(JFrame frame, String title, String message) {
		JOptionPane.showOptionDialog(frame, message, title, JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[] { TAB + CommonResources.getString("Ok") + TAB }, TAB + CommonResources.getString("Ok") + TAB); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static void showWarningMessage(JFrame frame, String title, String message) {
		JOptionPane.showOptionDialog(frame, message, title, JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[] { TAB + CommonResources.getString("Ok") + TAB }, TAB + CommonResources.getString("Ok") + TAB); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static void showWarningMessage(JFrame frame, String title, String message, Throwable throwable) {

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		JLabel titleLabel = new JLabel(message);
		titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		panel.add(titleLabel, BorderLayout.NORTH);

		JLabel detailsLabel = new JLabel("<html><body><u>" + CommonResources.getString("Details") + "</u></body></html>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		detailsLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5));
		detailsLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		panel.add(detailsLabel, BorderLayout.CENTER);
		detailsLabel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {

				detailsLabel.setText(throwable.getLocalizedMessage());

				StringWriter stringWriter = new StringWriter();
				PrintWriter printWriter = new PrintWriter(stringWriter);
				throwable.printStackTrace(printWriter);

				JTextArea textArea = new JTextArea(stringWriter.toString());

				JScrollPane scrollPane = new JScrollPane(textArea);
				scrollPane.setPreferredSize(new Dimension(500, 350));
				panel.add(scrollPane, BorderLayout.SOUTH);

				JDialog dialog = getDialog(panel);
				if (dialog != null) {
					packDialog(dialog);
				}

			}
		});

		JOptionPane.showOptionDialog(frame, panel, title, JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[] { TAB + CommonResources.getString("Ok") + TAB }, TAB + CommonResources.getString("Ok") + TAB); //$NON-NLS-1$ //$NON-NLS-2$

	}

	public static void packDialog(JDialog dialog) {

		Point location = dialog.getLocation();
		Dimension size = dialog.getSize();
		Dimension newSize = dialog.getPreferredSize();

		if (newSize.width != size.width) {
			location.x -= (newSize.width - size.width) / 2;
		}
		if (newSize.height != size.height) {
			location.y -= (newSize.height - size.height) / 2;
		}
		dialog.setLocation(location);

		dialog.pack();

	}

	public static JDialog showBusyMessage(JFrame frame, String title, String message, boolean modal) {

		JXBusyLabel label = new JXBusyLabel();
		label.setText(message);
		label.setBusy(true);

		return showOptionDialog(frame, label, title, JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[] { TAB + CommonResources.getString("Close") + TAB }, TAB + CommonResources.getString("Close") + TAB, modal); //$NON-NLS-1$ //$NON-NLS-2$

	}

	// Mostly copied from JOptionPane.java, adapted to return a JDialog
	public static JDialog showOptionDialog(Window window, Object message, String title, int optionType, int messageType, Icon icon, Object[] options, Object initialValue, boolean modal) throws HeadlessException {

		JOptionPane pane = new JOptionPane(message, messageType, optionType, icon, options, initialValue);
		pane.setInitialValue(initialValue);
		pane.setComponentOrientation(window.getComponentOrientation());

		JDialog dialog = createDialog(window, pane, JRootPane.PLAIN_DIALOG, modal, title);

		pane.selectInitialValue();

		return dialog;

	}

	// Mostly copied from JOptionPane.java, adapted to return a JDialog
	public static JDialog createDialog(Window window, JOptionPane pane, int style, boolean modal, String title) throws HeadlessException {

		JDialog dialog;

		if (window instanceof Frame) {
			dialog = new JDialog((Frame) window, title, modal);
		} else {
			dialog = new JDialog((Dialog) window, title, modal);
		}

		dialog.setComponentOrientation(pane.getComponentOrientation());

		Container contentPane = dialog.getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(pane, BorderLayout.CENTER);

		dialog.setResizable(false);
		if (JDialog.isDefaultLookAndFeelDecorated()) {
			boolean supportsWindowDecorations = UIManager.getLookAndFeel().getSupportsWindowDecorations();
			if (supportsWindowDecorations) {
				dialog.setUndecorated(true);
				pane.getRootPane().setWindowDecorationStyle(style);
			}
		}
		dialog.pack();
		dialog.setLocationRelativeTo(window);

		PropertyChangeListener listener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				// Let the defaultCloseOperation handle the closing
				// if the user closed the window without selecting a button
				// (newValue = null in that case). Otherwise, close the dialog.
				if (dialog.isVisible() && event.getSource() == pane && (event.getPropertyName().equals(JOptionPane.VALUE_PROPERTY)) && event.getNewValue() != null && event.getNewValue() != JOptionPane.UNINITIALIZED_VALUE) {
					dialog.setVisible(false);
				}
			}
		};

		WindowAdapter adapter = new WindowAdapter() {

			private boolean gotFocus = false;

			@Override
			public void windowClosing(WindowEvent we) {
				pane.setValue(null);
			}

			@Override
			public void windowClosed(WindowEvent e) {
				pane.removePropertyChangeListener(listener);
				dialog.getContentPane().removeAll();
			}

			@Override
			public void windowGainedFocus(WindowEvent we) {
				// Once window gets focus, set initial focus
				if (!gotFocus) {
					pane.selectInitialValue();
					gotFocus = true;
				}
			}
		};

		dialog.addWindowListener(adapter);
		dialog.addWindowFocusListener(adapter);
		dialog.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentShown(ComponentEvent ce) {
				// reset value to ensure closing works properly
				pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
			}
		});

		pane.addPropertyChangeListener(listener);

		return dialog;

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
