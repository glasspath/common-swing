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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.glasspath.common.swing.FrameContext;

public class LoginDialog extends DefaultDialog {

	public static final int RESULT_CANCEL = 0;
	public static final int RESULT_OK = 1;

	protected JTextField usernameTextField = null;
	protected JPasswordField passwordPasswordField = null;

	private int result = RESULT_CANCEL;

	public LoginDialog(FrameContext context, String username, String password, boolean usernameEditable) {
		super(context);

		setTitle("Login");
		getHeader().setTitle("Login");
		setPreferredSize(DIALOG_SIZE_SMALL_WIDE);

		GridBagLayout layout = new GridBagLayout();
		layout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.1 };
		layout.rowHeights = new int[] { 7, 25, 7, 25, 7 };
		layout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.1, 0.0 };
		layout.columnWidths = new int[] { 7, 75, 8, 250, 7 };
		getContentPanel().setLayout(layout);

		usernameTextField = new JTextField(username == null ? "" : username);
		usernameTextField.setEditable(usernameEditable);
		getContentPanel().add(new JLabel("Username"), new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		getContentPanel().add(usernameTextField, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		passwordPasswordField = new JPasswordField(password == null ? "" : password);
		getContentPanel().add(new JLabel("Password"), new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		getContentPanel().add(passwordPasswordField, new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		getOkButton().setText("Login"); // TODO

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowActivated(WindowEvent e) {
				if (usernameTextField.getText() == null || usernameTextField.getText().length() == 0) {
					usernameTextField.requestFocusInWindow();
				} else {
					passwordPasswordField.requestFocusInWindow();
				}
			}
		});

	}

	public int login() {

		pack();
		setLocationRelativeTo(context.getFrame());
		setVisible(true);

		return result;

	}

	@Override
	protected void submit() {
		result = RESULT_OK;
		super.submit();
	}

	@Override
	protected void cancel() {
		result = RESULT_CANCEL;
		super.cancel();
	}

	public int getResult() {
		return result;
	}

	public String getUsername() {
		return usernameTextField.getText();
	}

	public String getPassword() {
		return new String(passwordPasswordField.getPassword());
	}

}
