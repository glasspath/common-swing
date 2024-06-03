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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.WindowConstants;

import org.glasspath.common.swing.DesktopUtils;
import org.glasspath.common.swing.FrameContext;
import org.glasspath.common.swing.resources.CommonResources;

public class AboutDialog extends JDialog {

	private final FrameContext context;
	private final ImageIcon image;
	private final File logFile;
	private final File logFileBackup;
	private final ContentPanel contentPanel;
	private final Dimension contentSize;
	private final FooterPanel footerPanel;

	public AboutDialog(FrameContext context, IAbout about) {
		super(context.getFrame());

		this.context = context;
		this.image = about.getAboutImage();
		this.logFile = about.getLogFile();
		this.logFileBackup = about.getLogFileBackup();
		this.contentPanel = new ContentPanel();
		this.contentSize = new Dimension(image.getIconWidth() - 2, image.getIconHeight() - 2);
		this.footerPanel = new FooterPanel();

		setTitle(about.getAboutTitle());
		setModal(true);
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		setLayout(new BorderLayout());
		add(contentPanel, BorderLayout.CENTER);
		add(footerPanel, BorderLayout.SOUTH);

		pack();
		setLocationRelativeTo(context.getFrame());
		setVisible(true);

	}

	private void close() {
		setVisible(false);
		dispose();
	}

	private class ContentPanel extends JPanel {

		private ContentPanel() {
			setOpaque(false);
		}

		@Override
		public void paint(Graphics g) {

			image.paintIcon(this, g, -1, -1); // TODO? (Removes border from splash image)

			super.paint(g);

		}

		@Override
		public Dimension getMinimumSize() {
			return contentSize;
		}

		@Override
		public Dimension getMaximumSize() {
			return contentSize;
		}

		@Override
		public Dimension getPreferredSize() {
			return contentSize;
		}

	}

	private class FooterPanel extends JPanel {

		private FooterPanel() {

			GridBagLayout layout = new GridBagLayout();
			layout.rowWeights = new double[] { 0.0, 0.0 };
			layout.rowHeights = new int[] { 10, 25, 10 };
			layout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.1, 0.0, 0.0 };
			layout.columnWidths = new int[] { 10, 25, 5, 25, 100, 25, 10 };
			setLayout(layout);

			add(new JSeparator(), new GridBagConstraints(0, 0, 7, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

			JButton logFileButton = new JButton(CommonResources.getString("Log")); //$NON-NLS-1$
			add(logFileButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			logFileButton.setEnabled(logFile != null && logFile.exists());
			logFileButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent event) {
					if (logFile != null) {
						DesktopUtils.open(logFile.getAbsolutePath(), context.getFrame());
					}
				}
			});

			JButton logBackupButton = new JButton(CommonResources.getString("BackupOfLog")); //$NON-NLS-1$
			add(logBackupButton, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			logBackupButton.setEnabled(logFileBackup != null && logFileBackup.exists());
			logBackupButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent event) {
					if (logFileBackup != null) {
						DesktopUtils.open(logFileBackup.getAbsolutePath(), context.getFrame());
					}
				}
			});

			JButton closeButton = new JButton(CommonResources.getString("Close")); //$NON-NLS-1$
			add(closeButton, new GridBagConstraints(5, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			closeButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent event) {
					close();
				}
			});

		}

	}

	public interface IAbout {

		public String getAboutTitle();

		public ImageIcon getAboutImage();

		public File getLogFile();

		public File getLogFileBackup();

	}

}
