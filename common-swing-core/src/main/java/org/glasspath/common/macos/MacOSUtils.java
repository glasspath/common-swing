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
package org.glasspath.common.macos;

import java.awt.Color;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JRootPane;
import javax.swing.RootPaneContainer;

import org.glasspath.common.Common;
import org.glasspath.common.GlasspathSystemProperties;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public class MacOSUtils {

	public static LibMacOSUtils LIB_MACOS_UTILS = null;

	// Setup flags
	public static final int FLAG_RUN_ON_MAIN_THREAD = 1;

	// Custom title bar flags
	public static final int FLAG_SET_CUSTOM_TITLE_BAR_HEIGHT = 2;
	public static final int FLAG_CHECK_FULL_WINDOW_CONTENT_IS_TRUE = 4;
	public static final int FLAG_CHECK_FULL_WINDOW_CONTENT_IS_FALSE = 8;
	public static final int FLAG_CHECK_TITLE_BAR_APPEARS_TRANSPARENT_IS_TRUE = 16;
	public static final int FLAG_CHECK_TITLE_BAR_APPEARS_TRANSPARENT_IS_FALSE = 32;
	public static final int FLAG_CHECK_TITLE_VISIBILITY_IS_TRUE = 64;
	public static final int FLAG_CHECK_TITLE_VISIBILITY_IS_FALSE = 128;

	// Background color flags
	public static final int FLAG_SET_BACKGROUND_COLOR = 256;

	// Vibrant background flags
	public static final int FLAG_SET_VIBRANT_BACKGROUND = 512;
	public static final int FLAG_VIBRANT_BACKGROUND_DARK = 1024;

	public static final int DEFAULT_HIDDEN_TITLE_BAR_HEIGHT = 22;
	public static final int DEFAULT_CUSTOM_TITLE_BAR_HEIGHT = 48;
	public static final Color DEFAULT_BACKGROUND_COLOR = Color.gray;

	public static final int DEFAULT_CUSTOM_TITLE_BAR_FLAGS = 0
			| FLAG_SET_CUSTOM_TITLE_BAR_HEIGHT 
			| FLAG_CHECK_FULL_WINDOW_CONTENT_IS_TRUE 
			| FLAG_CHECK_TITLE_BAR_APPEARS_TRANSPARENT_IS_TRUE 
			| FLAG_CHECK_TITLE_VISIBILITY_IS_FALSE;

	private MacOSUtils() {

	}
	
	public static void load() {
		
		if (LIB_MACOS_UTILS == null) {
			
			File libFile = null;
			
			String assemblyResolvePath = System.getProperty(GlasspathSystemProperties.NATIVE_LIBRARY_PATH);
			if (assemblyResolvePath != null) {
				libFile = new File(assemblyResolvePath, "libmacos-utils.dylib"); //$NON-NLS-1$
			}
			
			if (libFile == null || !libFile.exists()) {
				libFile = new File("libmacos-utils.dylib"); //$NON-NLS-1$
			}
			
			if (libFile != null && libFile.exists()) {
				Common.LOGGER.info("Loading libmacos-utils.dylib from: " + libFile.getAbsolutePath()); //$NON-NLS-1$
				LIB_MACOS_UTILS = Native.load(libFile.getAbsolutePath(), LibMacOSUtils.class);
			} else {
				Common.LOGGER.error("Cannot load libmacos-utils.dylib from: " + libFile); //$NON-NLS-1$
			}
			
		}
				
	}

	public static void hideTitleBar(RootPaneContainer window, boolean hideTitle, boolean createBorder) {

		JRootPane rootPane = window.getRootPane();

		rootPane.putClientProperty("apple.awt.fullWindowContent", true); //$NON-NLS-1$
		rootPane.putClientProperty("apple.awt.transparentTitleBar", true); //$NON-NLS-1$

		if (hideTitle) {
			rootPane.putClientProperty("apple.awt.windowTitleVisible", false); //$NON-NLS-1$
		}

		// TODO: Add window listener and switch borders when going from/to full screen?
		// An alternative is to set the window background color through the methods below
		if (createBorder) {
			rootPane.setBorder(BorderFactory.createEmptyBorder(DEFAULT_HIDDEN_TITLE_BAR_HEIGHT, 0, 0, 0));
		}

	}

	public static int enableWindowDecorations() {
		return enableWindowDecorations(0, DEFAULT_CUSTOM_TITLE_BAR_HEIGHT, DEFAULT_BACKGROUND_COLOR);
	}

	public static int enableWindowDecorations(float titleBarHeight) {
		return enableWindowDecorations(DEFAULT_CUSTOM_TITLE_BAR_FLAGS, titleBarHeight, DEFAULT_BACKGROUND_COLOR);
	}

	public static int enableWindowDecorations(Color backgroundColor) {
		return enableWindowDecorations(FLAG_SET_BACKGROUND_COLOR, DEFAULT_CUSTOM_TITLE_BAR_HEIGHT, backgroundColor);
	}

	public static int enableWindowDecorations(float titleBarHeight, Color backgroundColor) {
		return enableWindowDecorations(DEFAULT_CUSTOM_TITLE_BAR_FLAGS | FLAG_SET_BACKGROUND_COLOR, titleBarHeight, backgroundColor);
	}

	public static int enableWindowDecorations(int flags, float titleBarHeight, Color backgroundColor) {

		int result = -1;

		try {

			load();

			if (FoundationLibrary.INSTANCE != null && LIB_MACOS_UTILS != null) {

				Pointer classId = FoundationLibrary.INSTANCE.objc_getClass("macos_utils"); //$NON-NLS-1$
				Pointer respondsToSelector = FoundationLibrary.INSTANCE.sel_registerName("respondsToSelector:"); //$NON-NLS-1$
				Pointer selector = FoundationLibrary.INSTANCE.sel_registerName("enableWindowDecorations:titleBarHeight:backgroundColor:"); //$NON-NLS-1$

				if (FoundationLibrary.INSTANCE.objc_msgSend(classId, respondsToSelector, selector)) {
					result = FoundationLibrary.INSTANCE.objc_msgSend(classId, selector, flags, titleBarHeight, backgroundColor != null ? backgroundColor.getRGB() : 0);
				}

			}

		} catch (UnsatisfiedLinkError e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;

	}

	public static int requestWindowStyle(RootPaneContainer window, float titleBarHeight) {
		return requestWindowStyle(window, FLAG_SET_CUSTOM_TITLE_BAR_HEIGHT, titleBarHeight, DEFAULT_BACKGROUND_COLOR);
	}

	public static int requestWindowStyle(RootPaneContainer window, Color backgroundColor) {
		return requestWindowStyle(window, FLAG_SET_BACKGROUND_COLOR, DEFAULT_CUSTOM_TITLE_BAR_HEIGHT, backgroundColor);
	}

	public static int requestWindowStyle(RootPaneContainer window, float titleBarHeight, Color backgroundColor) {
		return requestWindowStyle(window, FLAG_SET_CUSTOM_TITLE_BAR_HEIGHT | FLAG_SET_BACKGROUND_COLOR, titleBarHeight, backgroundColor);
	}

	public static int requestWindowStyle(RootPaneContainer window, int flags, float titleBarHeight, Color backgroundColor) {

		int result = -1;

		try {

			load();

			if (FoundationLibrary.INSTANCE != null && LIB_MACOS_UTILS != null) {

				Pointer classId = FoundationLibrary.INSTANCE.objc_getClass("macos_utils"); //$NON-NLS-1$
				Pointer respondsToSelector = FoundationLibrary.INSTANCE.sel_registerName("respondsToSelector:"); //$NON-NLS-1$
				Pointer selector = FoundationLibrary.INSTANCE.sel_registerName("requestWindowStyle:flags:titleBarHeight:backgroundColor:"); //$NON-NLS-1$

				if (FoundationLibrary.INSTANCE.objc_msgSend(classId, respondsToSelector, selector)) {

					long windowNumber = getWindowNumber(window);
					if (windowNumber >= 0) {
						result = FoundationLibrary.INSTANCE.objc_msgSend(classId, selector, windowNumber, flags, titleBarHeight, backgroundColor != null ? backgroundColor.getRGB() : 0);
					}

				}

			}

		} catch (UnsatisfiedLinkError e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;

	}

	// TODO: This is a ugly hack for identifying NSWindow's, for now we generate our own window numbers and we abuse the representedURL field for passing this number..
	private static long lastWindowNumber = -1;
	private static synchronized long getWindowNumber(RootPaneContainer window) {

		// On MacOS the "Window.documentFile" is stored in the representedURL field of the NSWindow
		Object object = window.getRootPane().getClientProperty("Window.documentFile"); //$NON-NLS-1$
		if (object instanceof File) {

			String s = ((File) object).getAbsolutePath();
			if (s.startsWith("/macos_utils/window/")) { //$NON-NLS-1$

				try {
					return Long.parseLong(s.substring(20));
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		} else {

			long windowNumber = ++lastWindowNumber;

			window.getRootPane().putClientProperty("Window.documentFile", new File("/macos_utils/window/" + windowNumber)); //$NON-NLS-1$ //$NON-NLS-2$

			return windowNumber;

		}

		return -1L;

	}
	
	public interface LibMacOSUtils extends Library {


	}

	public interface FoundationLibrary extends Library {

		FoundationLibrary INSTANCE = Native.load("Foundation", FoundationLibrary.class, Map.of(Library.OPTION_STRING_ENCODING, StandardCharsets.UTF_8.name())); //$NON-NLS-1$

		Pointer objc_getClass(String className);

		Pointer sel_registerName(String selectorName);

		boolean objc_msgSend(Pointer receiver, Pointer selector, Pointer arg1);

		int objc_msgSend(Pointer receiver, Pointer selector, int arg1, float arg2, int arg3);

		int objc_msgSend(Pointer receiver, Pointer selector, long arg1, int arg2, float arg3, int arg4);

	}

}
