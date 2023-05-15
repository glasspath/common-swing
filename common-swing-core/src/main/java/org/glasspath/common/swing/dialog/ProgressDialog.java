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
package org.glasspath.common.swing.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import org.glasspath.common.swing.FrameContext;
import org.glasspath.common.swing.console.Console;

public class ProgressDialog extends DefaultDialog {

	public static final int RESULT_UNKNOWN = 0;
	public static final int RESULT_CANCEL = 1;
	public static final int RESULT_OK = 2;

	private final JLabel headerLabel;
	private final JProgressBar progressBar;
	private final JLabel progressLabel;
	private final Console progressConsole;

	private int result = RESULT_UNKNOWN;

	public ProgressDialog(FrameContext context, String title, Icon dialogIcon) {
		this(context, title, dialogIcon, false);
	}

	public ProgressDialog(FrameContext context, String title, Icon dialogIcon, boolean modal) {
		this(context, title, dialogIcon, modal, true);
	}

	public ProgressDialog(FrameContext context, String title, Icon dialogIcon, boolean modal, boolean show) {
		super(context, false, modal);

		setPreferredSize(new Dimension(450, 400));
		setTitle(title);
		if (dialogIcon instanceof ImageIcon) {
			setIconImage(((ImageIcon) dialogIcon).getImage());
		}

		getContentPanel().setBorder(BorderFactory.createEmptyBorder());

		GridBagLayout layout = new GridBagLayout();
		layout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.1, 0.0 };
		layout.rowHeights = new int[] { 10, 32, 5, 18, 1, 20, 10, 75, 10 };
		layout.columnWeights = new double[] { 0.0, 0.1, 0.0 };
		layout.columnWidths = new int[] { 10, 250, 10 };
		getContentPanel().setLayout(layout);

		headerLabel = new JLabel();
		headerLabel.setIconTextGap(10);
		getContentPanel().add(headerLabel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		progressBar = new JProgressBar();
		progressBar.setMaximum(100);
		getContentPanel().add(progressBar, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		progressLabel = new JLabel();
		getContentPanel().add(progressLabel, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 2, 0, 0), 0, 0));

		progressConsole = new Console();
		// progressConsole.setEditable(false);
		progressConsole.getConsoleTextArea().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		getContentPanel().add(progressConsole, new GridBagConstraints(0, 7, 3, 2, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		getFooter().remove(getHelpButton());
		getFooter().remove(getOkButton());

		pack();

		if (context != null) {
			setLocationRelativeTo(context.getFrame());
		}

		if (show) {
			setVisible(true);
		}

	}

	public JLabel getHeaderLabel() {
		return headerLabel;
	}

	public JProgressBar getProgressBar() {
		return progressBar;
	}

	public JLabel getProgressLabel() {
		return progressLabel;
	}

	public Console getProgressConsole() {
		return progressConsole;
	}

	public void updateProgress(String consoleMessage) {
		progressConsole.addLine(consoleMessage);
	}

	public void updateProgress(int progress) {
		progressBar.setValue(progress);
	}

	public int getProgress() {
		return progressBar.getValue();
	}

	public void updateProgress(String progressText, int progress) {
		progressBar.setValue(progress);
		progressLabel.setText(progressText);
	}

	public void updateProgress(String consoleMessage, String progressText, int progress) {

		progressBar.setValue(progress);
		progressLabel.setText(progressText);

		if (consoleMessage != null) {
			progressConsole.addLine(consoleMessage);
		}

	}

	public void updateProgress(String consoleMessage, String itemTextLeft, String itemTextMiddle, String itemTextRight, int itemIndex, int totalItemCount, boolean done) {

		if (totalItemCount > 0) {

			if (done) {
				progressBar.setValue((int) (((double) (itemIndex + 1) / (double) totalItemCount) * 100.0));
			} else {
				progressBar.setValue((int) (((double) itemIndex / (double) totalItemCount) * 100.0));
			}

			progressLabel.setText(itemTextLeft + (itemIndex + 1) + itemTextMiddle + totalItemCount + itemTextRight);

		}

		if (consoleMessage != null) {
			progressConsole.addLine(consoleMessage);
		}

	}

	@Override
	protected void submit() {

	}

	@Override
	protected void cancel() {
		result = RESULT_CANCEL;
		super.cancel();
	}

	public int getResult() {
		return result;
	}

}
