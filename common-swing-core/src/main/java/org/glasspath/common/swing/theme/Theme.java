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
package org.glasspath.common.swing.theme;

import java.awt.Color;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JButton;

@SuppressWarnings("nls")
public enum Theme {

	THEME_DEFAULT("", 0), // FlatLaf light
	THEME_SYSTEM("system", 1), // System
	THEME_DARK("dark", 2); // FlatLaf dark

	public static final String PREFERENCES_KEY = "theme";
	public static final String ARGUMENTS_KEY = "-theme:";
	public static final Color DARK_BORDER_COLOR = new Color(100, 100, 100);
	public static final Color LIGHT_BORDER_COLOR = new Color(200, 200, 200);

	private static final List<Listener> listeners = new ArrayList<>();

	private final String id;
	private final int value;

	private Theme(String id, int value) {
		this.id = id;
		this.value = value;
	}

	public String getId() {
		return id;
	}

	public int getValue() {
		return value;
	}

	public static Theme theme = THEME_DEFAULT;

	public static Theme get() {
		return theme;
	}

	public static String parseArgument(String argument) {

		if (argument != null) {

			argument = argument.trim().toLowerCase();

			if (argument.startsWith(ARGUMENTS_KEY)) {

				String themeArgument = argument.substring(ARGUMENTS_KEY.length());
				if (THEME_DEFAULT.id.equals(themeArgument) || THEME_SYSTEM.id.equals(themeArgument) || THEME_DARK.id.equals(themeArgument)) {
					return themeArgument;
				}

			}

		}

		return null;

	}

	public static void load(Preferences preferences) {
		load(preferences.get(PREFERENCES_KEY, THEME_DEFAULT.id));
	}

	public static void load(String themeId) {

		if (THEME_SYSTEM.id.equals(themeId)) {
			theme = THEME_SYSTEM;
		} else if (THEME_DARK.id.equals(themeId)) {
			theme = THEME_DARK;
		} else {
			theme = THEME_DEFAULT;
		}

		update();

	}

	public static boolean isDefaultTheme() {
		return theme == THEME_DEFAULT;
	}

	public static boolean isSystemTheme() {
		return theme == THEME_SYSTEM;
	}

	public static boolean isDark() {
		return theme == THEME_DARK;
	}

	public static void styleActionButton(JButton button) {

		button.setMargin(new Insets(0, 2, 0, 2));

		if (!Theme.isSystemTheme()) {
			if (Theme.isDark()) {
				button.setBorder(BorderFactory.createLineBorder(DARK_BORDER_COLOR));
			} else {
				button.setBorder(BorderFactory.createLineBorder(LIGHT_BORDER_COLOR));
			}
		}

	}

	public static void register(Listener listener) {
		listeners.add(listener);
		listener.update();
	}

	public static void update() {
		for (Listener listener : listeners) {
			listener.update();
		}
	}

	public static interface Listener {

		public void update();

	}

}
