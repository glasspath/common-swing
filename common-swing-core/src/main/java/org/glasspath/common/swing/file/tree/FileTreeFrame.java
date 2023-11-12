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
package org.glasspath.common.swing.file.tree;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.glasspath.common.swing.border.HidpiMatteBorder;
import org.glasspath.common.swing.color.ColorUtils;
import org.glasspath.common.swing.frame.FrameUtils;

public class FileTreeFrame extends JFrame {

	private final Preferences preferences;
	private final JPanel toolBarPanel;
	private FileTreePanel fileTreePanel = null;

	public FileTreeFrame(String preferencesKey) {

		// TODO
		preferences = Preferences.userNodeForPackage(FileTreeFrame.class);

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		getRootPane().setBackground(ColorUtils.TITLE_BAR_COLOR);

		toolBarPanel = new JPanel();
		toolBarPanel.setBackground(ColorUtils.TITLE_BAR_COLOR);
		toolBarPanel.setLayout(new BoxLayout(toolBarPanel, BoxLayout.LINE_AXIS));
		toolBarPanel.setBorder(BorderFactory.createCompoundBorder(new HidpiMatteBorder(new Insets(0, 0, 1, 0)), BorderFactory.createEmptyBorder(5, 5, 10, 10)));
		getContentPane().add(toolBarPanel, BorderLayout.NORTH);

		// TODO
		FrameUtils.loadFrameDimensions(this, preferences, 15, 15, 500, 750, 0);

		addWindowListener(new WindowAdapter() {

			boolean inited = false;

			@Override
			public void windowActivated(WindowEvent e) {

				// mainPanel.requestFocusInWindow();

				if (!inited) {

					addComponentListener(new ComponentAdapter() {

						@Override
						public void componentResized(ComponentEvent e) {
							FrameUtils.saveFrameDimensions(FileTreeFrame.this, preferences);
						}

						@Override
						public void componentMoved(ComponentEvent e) {
							FrameUtils.saveFrameDimensions(FileTreeFrame.this, preferences);
						}
					});

					if (fileTreePanel != null) {
						fileTreePanel.getTree().requestFocusInWindow();
					}

					inited = true;

				}

			}

			@Override
			public void windowClosing(WindowEvent event) {
				exit();
			}
		});

	}

	public FileTreePanel getFileTreePanel() {
		return fileTreePanel;
	}

	public void setFileTreePanel(FileTreePanel fileTreePanel) {

		if (this.fileTreePanel != null) {

			toolBarPanel.removeAll();

			getContentPane().remove(this.fileTreePanel);

		}

		this.fileTreePanel = fileTreePanel;

		if (fileTreePanel != null) {

			toolBarPanel.add(fileTreePanel.getToolBar());
			toolBarPanel.add(Box.createHorizontalGlue());
			toolBarPanel.add(fileTreePanel.getFilterTools().getToolBar());

			getContentPane().add(fileTreePanel, BorderLayout.CENTER);

		}

	}

	public void exit() {
		FrameUtils.saveFrameDimensions(this, preferences);
		setVisible(false);
	}

}
