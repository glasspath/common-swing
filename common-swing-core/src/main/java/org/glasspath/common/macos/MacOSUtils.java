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

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JRootPane;
import javax.swing.RootPaneContainer;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public class MacOSUtils {

	public static final int FLAG_RUN_ON_MAIN_THREAD = 1;
	public static final int FLAG_APPLY_CUSTOM_TITLE_BAR_HEIGHT = 2;
	public static final int FLAG_CHECK_FULL_WINDOW_CONTENT_IS_TRUE = 4;
	public static final int FLAG_CHECK_FULL_WINDOW_CONTENT_IS_FALSE = 8;
	public static final int FLAG_CHECK_TITLE_BAR_APPEARS_TRANSPARENT_IS_TRUE = 16;
	public static final int FLAG_CHECK_TITLE_BAR_APPEARS_TRANSPARENT_IS_FALSE = 32;
	public static final int FLAG_CHECK_TITLE_VISIBILITY_IS_TRUE = 64;
	public static final int FLAG_CHECK_TITLE_VISIBILITY_IS_FALSE = 128;

	public static final int DEFAULT_HIDDEN_TITLE_BAR_HEIGHT = 22;
	public static final float DEFAULT_CUSTOM_TITLE_BAR_HEIGHT = 48.0F;
	public static final int DEFAULT_FLAGS = 0
			| FLAG_APPLY_CUSTOM_TITLE_BAR_HEIGHT 
			| FLAG_CHECK_FULL_WINDOW_CONTENT_IS_TRUE 
			| FLAG_CHECK_TITLE_BAR_APPEARS_TRANSPARENT_IS_TRUE 
			| FLAG_CHECK_TITLE_VISIBILITY_IS_FALSE;

	public static LibMacOSUtils LIB_MAC_OS_UTILS = null;
	public static FoundationLibrary FOUNDATION_LIBRARY = null;

	private MacOSUtils() {

	}

	public static void hideTitleBar(RootPaneContainer window) {

		JRootPane rootPane = window.getRootPane();

		rootPane.putClientProperty("apple.awt.fullWindowContent", true);
		rootPane.putClientProperty("apple.awt.transparentTitleBar", true);
		rootPane.setBorder(BorderFactory.createEmptyBorder(DEFAULT_HIDDEN_TITLE_BAR_HEIGHT, 0, 0, 0));

	}

	public static int enableWindowDecorations() {
		return enableWindowDecorations(DEFAULT_FLAGS, DEFAULT_CUSTOM_TITLE_BAR_HEIGHT);
	}

	public static int enableWindowDecorations(float titleBarHeight) {
		return enableWindowDecorations(DEFAULT_FLAGS, titleBarHeight);
	}

	public static int enableWindowDecorations(int flags, float titleBarHeight) {

		int result = -1;

		try {

			if (LIB_MAC_OS_UTILS == null && FOUNDATION_LIBRARY == null) {
				LIB_MAC_OS_UTILS = Native.load("libmacos-utils.dylib", LibMacOSUtils.class);
				FOUNDATION_LIBRARY = Native.load("Foundation", FoundationLibrary.class, Map.of(Library.OPTION_STRING_ENCODING, StandardCharsets.UTF_8.name()));
			}

			Pointer classId = FoundationLibrary.INSTANCE.objc_getClass("macos_utils");
			Pointer respondsToSelector = FoundationLibrary.INSTANCE.sel_registerName("respondsToSelector:");
			Pointer selector = FoundationLibrary.INSTANCE.sel_registerName("enableWindowDecorations:titleBarHeight:");

			if (FoundationLibrary.INSTANCE.objc_msgSend(classId, respondsToSelector, selector)) {
				result = FoundationLibrary.INSTANCE.objc_msgSend(classId, selector, flags, titleBarHeight);
			}

		} catch (UnsatisfiedLinkError e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;

	}

	public static int requestWindowStyle(RootPaneContainer window, float titleBarHeight) {
		return requestWindowStyle(window, FLAG_APPLY_CUSTOM_TITLE_BAR_HEIGHT, titleBarHeight);
	}

	public static int requestWindowStyle(RootPaneContainer window, int flags, float titleBarHeight) {

		int result = -1;

		try {

			if (LIB_MAC_OS_UTILS != null && FOUNDATION_LIBRARY != null) {

				Pointer classId = FoundationLibrary.INSTANCE.objc_getClass("macos_utils");
				Pointer respondsToSelector = FoundationLibrary.INSTANCE.sel_registerName("respondsToSelector:");
				Pointer selector = FoundationLibrary.INSTANCE.sel_registerName("requestWindowStyle:flags:titleBarHeight:");

				if (FoundationLibrary.INSTANCE.objc_msgSend(classId, respondsToSelector, selector)) {

					long windowNumber = getWindowNumber(window);
					if (windowNumber >= 0) {
						result = FoundationLibrary.INSTANCE.objc_msgSend(classId, selector, windowNumber, flags, titleBarHeight);
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
		Object object = window.getRootPane().getClientProperty("Window.documentFile");
		if (object instanceof File) {

			String s = ((File) object).getAbsolutePath();
			if (s.startsWith("/macos_utils/window/")) {

				try {
					return Long.parseLong(s.substring(20));
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		} else {

			long windowNumber = ++lastWindowNumber;

			window.getRootPane().putClientProperty("Window.documentFile", new File("/macos_utils/window/" + windowNumber));

			return windowNumber;

		}

		return -1L;

	}

	public interface LibMacOSUtils extends Library {

	}

	public interface FoundationLibrary extends Library {

		FoundationLibrary INSTANCE = Native.load("Foundation", FoundationLibrary.class, Map.of(Library.OPTION_STRING_ENCODING, StandardCharsets.UTF_8.name()));

		Pointer objc_getClass(String className);

		Pointer sel_registerName(String selectorName);

		boolean objc_msgSend(Pointer receiver, Pointer selector, Pointer arg1);

		int objc_msgSend(Pointer receiver, Pointer selector, int arg1, float arg2);

		int objc_msgSend(Pointer receiver, Pointer selector, long arg1, int arg2, float arg3);

	}

}
