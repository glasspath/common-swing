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
package org.glasspath.common.swing.frame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.glasspath.common.Common;
import org.glasspath.common.os.OsUtils;
import org.glasspath.common.swing.border.RoundedLineBorder;
import org.glasspath.common.swing.color.ColorUtils;
import org.glasspath.common.swing.theme.Theme;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatSystemProperties;

@SuppressWarnings("nls")
public class FrameUtils {

	private static boolean TEST_LARGE_FONT = false;

	private FrameUtils() {

	}

	public static void setSystemLookAndFeelProperties(String appTitle) {

		// System.setProperty("sun.java2d.uiScale", "1.0");

		if (OsUtils.PLATFORM_MACOS) {

			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("apple.awt.application.name", appTitle);
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", appTitle);

			if (Theme.isDark()) {
				System.setProperty("apple.awt.application.appearance", "system");
			}

		} else if (OsUtils.PLATFORM_LINUX) {

			// System.setProperty("sun.java2d.opengl", "True");

		}

	}

	public static void installLookAndFeel(String nativesPath) throws UnsupportedLookAndFeelException {

		// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		if (nativesPath != null) {
			System.setProperty(FlatSystemProperties.NATIVE_LIBRARY_PATH, nativesPath);
			Common.LOGGER.info("FlatLaf library path set to: " + nativesPath);
		}

		// System.setProperty(FlatSystemProperties.USE_WINDOW_DECORATIONS, "false");

		if (Theme.isDark()) {
			UIManager.setLookAndFeel(new FlatDarculaLaf());
		} else {
			UIManager.setLookAndFeel(new FlatIntelliJLaf());
		}

		if (OsUtils.PLATFORM_WINDOWS) {

			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);

			// Default seems to be true in latest FlatLaf releases
			// UIManager.put("TitlePane.unifiedBackground", true);

		}

		// TODO: Larger font's result in a scale factor > 1.0, this causes problems with SVG icons.
		// Maybe the larger scale factor is preferred?
		System.setProperty(FlatSystemProperties.UI_SCALE_ENABLED, "false");

		if (TEST_LARGE_FONT) {

			Font defaultFont = UIManager.getFont("defaultFont");
			if (defaultFont != null) {
				UIManager.put("defaultFont", defaultFont.deriveFont(15.0F));
				UIManager.put("Table.font", defaultFont.deriveFont(15.0F));
			}

		}

		Font tableFont = UIManager.getFont("Table.font");
		if (tableFont != null && tableFont.getSize() >= 12) {
			UIManager.put("Table.font", tableFont.deriveFont(tableFont.getSize() - 1.0F));
		}

		UIManager.put("ScrollBar.minimumThumbSize", new Dimension(8, 16));
		UIManager.put("TextComponent.arc", 5);
		UIManager.put("TitledBorder.border", new RoundedLineBorder());
		// UIManager.put("Table.intercellSpacing", new Dimension(1, 1));
		// UIManager.put("Table.alternateRowColor", TableUI.EVEN_ROW_COLOR);

		// TODO? This was added because we use glasspane's with a search field, pop-ups appeared below the search field.
		// Linux and MacOS seem to use heavy-weight by default, so lets enable it for windows too
		UIManager.put("Popup.forceHeavyWeight", true);

		Common.LOGGER.info("defaultFont: " + UIManager.getFont("defaultFont"));

	}

	public static void applyMenuBarStyle(JMenuBar menuBar) {

		if (OsUtils.PLATFORM_LINUX) {

			// The dark theme is still not very dark on Linux and looks better with a slightly darker menu bar
			if (!Theme.isDark()) {

				// Doesn't seem to matter what color we set, as long as its not null..
				menuBar.setBackground(ColorUtils.TITLE_BAR_COLOR);

			}

		}

		menuBar.setBorder(BorderFactory.createEmptyBorder());

	}

	public static void saveFrameDimensions(JFrame frame, Preferences preferences) {

		preferences.putInt("frameExtendedState", frame.getExtendedState());

		if (frame.getExtendedState() != JFrame.MAXIMIZED_BOTH) {
			preferences.putInt("frameX", frame.getX());
			preferences.putInt("frameY", frame.getY());
			preferences.putInt("frameWidth", frame.getWidth());
			preferences.putInt("frameHeight", frame.getHeight());
		}

	}

	public static void loadFrameDimensions(JFrame frame, Preferences preferences) {
		loadFrameDimensions(frame, preferences, true);
	}

	public static void loadFrameDimensions(JFrame frame, Preferences preferences, boolean loadExtendedState) {

		int frameExtendedState = preferences.getInt("frameExtendedState", 0);
		int frameX = preferences.getInt("frameX", 15);
		int frameY = preferences.getInt("frameY", 15);
		int frameWidth = preferences.getInt("frameWidth", 1250);
		int frameHeight = preferences.getInt("frameHeight", 785);

		boolean locationValid = false;

		final GraphicsDevice[] graphicsDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();

		Rectangle bounds;
		for (GraphicsDevice graphicsDevice : graphicsDevices) {

			if (graphicsDevice.getDefaultConfiguration() != null && graphicsDevice.getDefaultConfiguration().getBounds() != null) {

				bounds = graphicsDevice.getDefaultConfiguration().getBounds();

				if (frameX > bounds.x - 25 && frameX < (bounds.x + bounds.width) - 25) {
					if (frameY < bounds.height - 100) {
						locationValid = true;
						break;
					}
				}

			}

		}

		if (!locationValid) {

			if (loadExtendedState) {
				preferences.putInt("frameExtendedState", 0);
			} else {
				frameExtendedState = 0;
			}

			frameX = 15;
			frameY = 15;
			frameWidth = 1250;
			frameHeight = 785;

		}

		frame.setSize(frameWidth, frameHeight);
		frame.setLocation(frameX, frameY);

		if (loadExtendedState) {
			frame.setExtendedState(frameExtendedState);
		}

	}

	public static void loadExtendedState(JFrame frame, Preferences preferences) {
		frame.setExtendedState(preferences.getInt("frameExtendedState", 0));
	}

	public static void runTestFrame(boolean dark, FrameRunnable runnable) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				if (dark) {
					Theme.load("dark");
				}

				try {
					FrameUtils.installLookAndFeel(null);
				} catch (Exception e) {
					Common.LOGGER.error("Exception while setting look and feel", e);
					e.printStackTrace();
				}

				JFrame frame = new JFrame();
				frame.setTitle("Test Frame");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setSize(800, 600);
				frame.getContentPane().setLayout(new BorderLayout());

				JMenuBar menuBar = new JMenuBar();
				frame.setJMenuBar(menuBar);

				JMenu fileMenu = new JMenu("File");
				menuBar.add(fileMenu);

				JMenuItem exitMenuItem = new JMenuItem("Exit");
				fileMenu.add(exitMenuItem);
				exitMenuItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						frame.setVisible(false);
					}
				});

				runnable.run(frame);

				frame.setLocationRelativeTo(null);
				frame.setVisible(true);

			}
		});

	}

	public interface FrameRunnable {

		public void run(JFrame frame);

	}

}
