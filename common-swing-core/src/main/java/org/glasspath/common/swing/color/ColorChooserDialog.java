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
package org.glasspath.common.swing.color;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import org.glasspath.common.swing.SwingUtils;

public class ColorChooserDialog extends JDialog {

	public static final Color NULL_COLOR = new Color(0, 0, 0, 0);

	private JColorChooser chooserPane;
	private JButton cancelButton;

	public ColorChooserDialog(Dialog owner, String title, boolean modal, Component c, JColorChooser chooserPane, ActionListener okListener, ActionListener cancelListener) throws HeadlessException {
		super(owner, title, modal);
		initColorChooserDialog(c, chooserPane, okListener, cancelListener);
	}

	public ColorChooserDialog(Frame owner, String title, boolean modal, Component c, JColorChooser chooserPane, ActionListener okListener, ActionListener cancelListener) throws HeadlessException {
		super(owner, title, modal);
		initColorChooserDialog(c, chooserPane, okListener, cancelListener);
	}

	protected void initColorChooserDialog(Component c, JColorChooser chooserPane, ActionListener okListener, ActionListener cancelListener) {

		this.chooserPane = chooserPane;

		Locale locale = getLocale();
		String okString = UIManager.getString("ColorChooser.okText", locale); //$NON-NLS-1$
		String cancelString = UIManager.getString("ColorChooser.cancelText", locale); //$NON-NLS-1$
		String resetString = UIManager.getString("ColorChooser.resetText", locale); //$NON-NLS-1$

		chooserPane.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));

		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(chooserPane, BorderLayout.CENTER);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		JButton okButton = new JButton(okString);
		getRootPane().setDefaultButton(okButton);
		okButton.getAccessibleContext().setAccessibleDescription(okString);
		okButton.setActionCommand("OK"); //$NON-NLS-1$
		okButton.addActionListener(new ActionListener() {

			@Override
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
				hide();
			}
		});
		if (okListener != null) {
			okButton.addActionListener(okListener);
		}
		buttonPane.add(okButton);

		cancelButton = new JButton(cancelString);
		cancelButton.getAccessibleContext().setAccessibleDescription(cancelString);

		Action cancelKeyAction = new AbstractAction() {

			@Override
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
				hide();
			}
		};
		KeyStroke cancelKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		InputMap inputMap = cancelButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = cancelButton.getActionMap();
		if (inputMap != null && actionMap != null) {
			inputMap.put(cancelKeyStroke, "cancel"); //$NON-NLS-1$
			actionMap.put("cancel", cancelKeyAction); //$NON-NLS-1$
		}

		cancelButton.setActionCommand("cancel"); //$NON-NLS-1$
		cancelButton.addActionListener(new ActionListener() {

			@Override
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
				hide();
			}
		});
		if (cancelListener != null) {
			cancelButton.addActionListener(cancelListener);
		}
		buttonPane.add(cancelButton);

		JButton resetButton = new JButton(resetString);
		resetButton.getAccessibleContext().setAccessibleDescription(resetString);
		resetButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		});
		int mnemonic = SwingUtils.getUIDefaultsInt("ColorChooser.resetMnemonic", locale, -1); //$NON-NLS-1$
		if (mnemonic != -1) {
			resetButton.setMnemonic(mnemonic);
		}
		buttonPane.add(resetButton);
		contentPane.add(buttonPane, BorderLayout.SOUTH);

		if (JDialog.isDefaultLookAndFeelDecorated()) {
			boolean supportsWindowDecorations = UIManager.getLookAndFeel().getSupportsWindowDecorations();
			if (supportsWindowDecorations) {
				getRootPane().setWindowDecorationStyle(JRootPane.COLOR_CHOOSER_DIALOG);
			}
		}
		applyComponentOrientation(((c == null) ? getRootPane() : c).getComponentOrientation());

		pack();
		setLocationRelativeTo(c);

		this.addWindowListener(new Closer());

	}

	@Override
	@SuppressWarnings("deprecation")
	public void show() {
		super.show();
	}

	public void reset() {
		chooserPane.setColor(NULL_COLOR);
	}

	class Closer extends WindowAdapter implements Serializable {

		@Override
		@SuppressWarnings("deprecation")
		public void windowClosing(WindowEvent e) {
			cancelButton.doClick(0);
			Window w = e.getWindow();
			w.hide();
		}
	}

	static class DisposeOnClose extends ComponentAdapter implements Serializable {

		@Override
		public void componentHidden(ComponentEvent e) {
			Window w = (Window) e.getComponent();
			w.dispose();
		}
	}

}