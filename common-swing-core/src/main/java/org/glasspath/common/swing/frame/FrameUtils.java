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
import java.awt.Insets;
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
import org.glasspath.common.swing.border.HidpiMatteBorder;
import org.glasspath.common.swing.border.RoundedLineBorder;
import org.glasspath.common.swing.color.ColorUtils;
import org.glasspath.common.swing.theme.Theme;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatSystemProperties;
import com.formdev.flatlaf.IntelliJTheme;

@SuppressWarnings("nls")
public class FrameUtils {

	private static boolean TEST_LARGE_FONT = false;

	private static int defaultExtendedState = 0;

	private FrameUtils() {

	}

	public static int getDefaultExtendedState() {
		return defaultExtendedState;
	}

	public static void setDefaultExtendedState(int defaultExtendedState) {
		FrameUtils.defaultExtendedState = defaultExtendedState;
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

		if (nativesPath != null) {
			System.setProperty(FlatSystemProperties.NATIVE_LIBRARY_PATH, nativesPath);
			Common.LOGGER.info("FlatLaf library path set to: " + nativesPath);
		}

		if (Theme.isDark()) {
			// UIManager.setLookAndFeel(new FlatDarculaLaf());
			IntelliJTheme.setup(FrameUtils.class.getResourceAsStream("/org/glasspath/themes/dark.theme.json"));
		} else {
			UIManager.setLookAndFeel(new FlatIntelliJLaf());
		}

		if (OsUtils.PLATFORM_WINDOWS) {

			// System.setProperty(FlatSystemProperties.USE_WINDOW_DECORATIONS, "false");
			// System.setProperty(FlatSystemProperties.USE_NATIVE_LIBRARY, "false");
			// UIManager.put("TitlePane.unifiedBackground", false);
			// UIManager.put("FlatLaf.debug.titlebar.showRectangles", true);
			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);

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
		UIManager.getDefaults().put("TableHeader.cellBorder", new HidpiMatteBorder(new Insets(0, 0, 1, 1)));
		// UIManager.put("Table.intercellSpacing", new Dimension(1, 1));
		// UIManager.put("Table.alternateRowColor", TableUI.EVEN_ROW_COLOR);

		// TODO? This was added because we use glasspane's with a search field, pop-ups appeared below the search field.
		// Linux and MacOS seem to use heavy-weight by default, so lets enable it for windows too
		UIManager.put("Popup.forceHeavyWeight", true);

		// Common.LOGGER.info("defaultFont: " + UIManager.getFont("defaultFont"));

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

	public static boolean isFullScreen(JFrame frame) {

		Rectangle frameBounds = frame.getBounds();

		GraphicsDevice[] graphicsDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		for (GraphicsDevice graphicsDevice : graphicsDevices) {

			if (graphicsDevice.getDefaultConfiguration() != null) {

				Rectangle bounds = graphicsDevice.getDefaultConfiguration().getBounds();
				if (bounds != null) {

					if (frameBounds.x == bounds.x && frameBounds.y == bounds.y && frameBounds.width == bounds.width && frameBounds.height == bounds.height) {
						return true;
					}

				}

			}

		}

		return false;

	}

	public static int getMinimalScreenHeight() {

		int height = Integer.MAX_VALUE;

		GraphicsDevice[] graphicsDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		for (GraphicsDevice graphicsDevice : graphicsDevices) {

			if (graphicsDevice.getDefaultConfiguration() != null) {

				Rectangle bounds = graphicsDevice.getDefaultConfiguration().getBounds();
				if (bounds != null && bounds.height < height) {
					height = bounds.height;
				}

			}

		}

		return height < Integer.MAX_VALUE ? height : 0;

	}

	public static void loadFrameDimensions(JFrame frame, Preferences preferences) {
		loadFrameDimensions(frame, preferences, defaultExtendedState);
	}

	public static void loadFrameDimensions(JFrame frame, Preferences preferences, int extendedState) {
		loadFrameDimensions(frame, preferences, 15, 15, 1250, 787, extendedState);
	}

	public static void loadFrameDimensions(JFrame frame, Preferences preferences, int x, int y, int width, int height, int extendedState) {

		int frameX, frameY, frameWidth, frameHeight, frameExtendedState;

		if (preferences != null) {
			frameX = preferences.getInt("frameX", x);
			frameY = preferences.getInt("frameY", y);
			frameWidth = preferences.getInt("frameWidth", width);
			frameHeight = preferences.getInt("frameHeight", height);
			frameExtendedState = preferences.getInt("frameExtendedState", extendedState);
		} else {
			frameX = x;
			frameY = y;
			frameWidth = width;
			frameHeight = height;
			frameExtendedState = extendedState;
		}

		boolean locationValid = false;

		GraphicsDevice[] graphicsDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		for (GraphicsDevice graphicsDevice : graphicsDevices) {

			if (graphicsDevice.getDefaultConfiguration() != null) {

				Rectangle bounds = graphicsDevice.getDefaultConfiguration().getBounds();
				if (bounds != null) {

					if (frameX > bounds.x - 25 && frameX < (bounds.x + bounds.width) - 25) {
						if (frameY < bounds.height - 100) {
							locationValid = true;
							break;
						}
					}

				}

			}

		}

		if (!locationValid) {
			frameX = 15;
			frameY = 15;
			frameWidth = 1250;
			frameHeight = 785;
			frameExtendedState = 0;
		}

		frame.setSize(frameWidth, frameHeight);
		frame.setLocation(frameX, frameY);
		frame.setExtendedState(frameExtendedState);

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
						// frame.setVisible(false);
						System.exit(0);
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
