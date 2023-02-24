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
package org.glasspath.common.swing;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import org.glasspath.common.Common;
import org.glasspath.common.swing.dialog.DialogUtils;
import org.glasspath.common.swing.resources.Resources;

public class DesktopUtils {

	private DesktopUtils() {

	}

	public static JMenuItem createLaunchFileMenuItem(String path, JFrame frame) {

		JMenuItem item = new JMenuItem(Resources.getString("OpenFile")); //$NON-NLS-1$
		item.setEnabled(path != null && path.length() > 0);
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				open(path, frame);
			}
		});

		return item;

	}

	public static void open(String path, JFrame frame) {
		if (path != null && path.length() > 0) {
			open(new File(path), frame);
		}
	}

	public static void open(File file, JFrame frame) {
		open(file, frame, "File could not be opened", "The file could not be opened..");
	}

	public static void open(File file, JFrame frame, String failedTitle, String failedMessage) {

		if (Desktop.isDesktopSupported() && file != null) {

			// TODO: https://bugs.openjdk.org/browse/JDK-8270269, occurs after using c++/cli/c# functions from UwpShareUtils, for now we just create a new thread
			new Thread(new Runnable() {

				@Override
				public void run() {

					try {
						Desktop.getDesktop().open(file);
					} catch (IOException e) {

						Common.LOGGER.error("IOException while launching file explorer for file: " + file.getAbsolutePath(), e); //$NON-NLS-1$

						SwingUtilities.invokeLater(new Runnable() {

							@Override
							public void run() {
								DialogUtils.showWarningMessage(frame, failedTitle, failedMessage);
							}
						});

					}

				}
			}).start();

		}

	}

	public static void browse(String url) {
		if (Desktop.isDesktopSupported() && url != null) {
			try {
				browse(new URI(url));
			} catch (Exception e) {
				Common.LOGGER.error("Exception while launching browser", e); //$NON-NLS-1$
				e.printStackTrace();
			}
		}
	}

	public static void browse(URI uri) {

		if (Desktop.isDesktopSupported() && uri != null) {

			// TODO: https://bugs.openjdk.org/browse/JDK-8270269, occurs after using c++/cli/c# functions from UwpShareUtils, for now we just create a new thread
			new Thread(new Runnable() {

				@Override
				public void run() {

					try {
						Desktop.getDesktop().browse(uri);
					} catch (Exception e) {
						Common.LOGGER.error("Exception while launching browser", e); //$NON-NLS-1$
						e.printStackTrace();
					}

				}

			}).start();

		}

	}

	public static void mail(URI uri) {

		if (Desktop.isDesktopSupported() && uri != null) {

			// TODO: https://bugs.openjdk.org/browse/JDK-8270269, occurs after using c++/cli/c# functions from UwpShareUtils, for now we just create a new thread
			new Thread(new Runnable() {

				@Override
				public void run() {

					try {
						Desktop.getDesktop().mail(uri);
					} catch (Exception e) {
						Common.LOGGER.error("Exception while launching mail", e); //$NON-NLS-1$
						e.printStackTrace();
					}

				}

			}).start();

		}

	}

	/*
	public static String getMyDocumentsPath() {
		// TODO: This is really slow, for now this method is avoided
		return new JFileChooser().getFileSystemView().getDefaultDirectory().getAbsolutePath();
	}
	*/

}
