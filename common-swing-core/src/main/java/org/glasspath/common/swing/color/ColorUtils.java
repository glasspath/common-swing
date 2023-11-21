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
package org.glasspath.common.swing.color;

import java.awt.Color;

import org.glasspath.common.swing.theme.Theme;

@SuppressWarnings("nls")
public class ColorUtils {

	public static final Color TRANSPARENT = new Color(0, 0, 0, 0);
	public static final Color BLUE = new Color(87, 148, 242);
	public static final Color DARK_BLUE = new Color(31, 96, 196);
	public static final Color GREEN = new Color(151, 191, 105);
	public static final Color RED = new Color(242, 73, 92);
	public static final Color ORANGE = new Color(237, 162, 0);
	public static final Color YELLOW = new Color(250, 222, 42);

	public static Color TITLE_BAR_COLOR;
	public static Color SELECTION_COLOR_FOCUSSED;
	public static Color SELECTION_COLOR_NOT_FOCUSSED; // TODO: Remove?
	public static Color TEXT_COLOR;
	public static Color SEMI_DISABLED_TEXT_COLOR;
	public static Color DISABLED_TEXT_COLOR;
	public static Color SUMMARY_CHART_BAR_COLOR;
	public static Color INVALID_INPUT_BACKGROUND;
	static {
		Theme.register(() -> {
			if (Theme.isDark()) {
				TITLE_BAR_COLOR = new Color(60, 63, 65);
				SELECTION_COLOR_FOCUSSED = new Color(75, 110, 175);
				SELECTION_COLOR_NOT_FOCUSSED = new Color(75, 110, 175);
				TEXT_COLOR = new Color(187, 187, 187);
				SEMI_DISABLED_TEXT_COLOR = new Color(155, 155, 155);
				DISABLED_TEXT_COLOR = new Color(125, 125, 125);
				SUMMARY_CHART_BAR_COLOR = new Color(100, 100, 100);
				INVALID_INPUT_BACKGROUND = new Color(255, 150, 150);
			} else {
				TITLE_BAR_COLOR = new Color(247, 247, 247);
				SELECTION_COLOR_FOCUSSED = new Color(221, 232, 248);
				SELECTION_COLOR_NOT_FOCUSSED = new Color(221, 232, 248);
				TEXT_COLOR = Color.black;
				SEMI_DISABLED_TEXT_COLOR = new Color(125, 125, 125);
				DISABLED_TEXT_COLOR = Color.lightGray;
				SUMMARY_CHART_BAR_COLOR = new Color(192, 192, 192);
				INVALID_INPUT_BACKGROUND = new Color(255, 150, 150);
			}
		});
	}

	public static Color createTransparentColor(Color fromColor, int alpha) {
		return new Color(fromColor.getRed(), fromColor.getGreen(), fromColor.getBlue(), alpha);
	}

	public static String toHex(Object object) {
		if (object instanceof Color) {
			return toHex((Color) object);
		} else {
			return null;
		}
	}

	public static String toHex(Color color) {
		if (color == null) {
			return null;
		} else {
			if (color.getAlpha() < 255) {
				return String.format("#%02X%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
			} else {
				return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
			}
		}
	}

	public static Color fromHex(String hex) {

		if (hex != null && hex.length() >= 7) {

			try {

				Integer intval = Integer.decode(hex);
				int i = intval.intValue();

				if (hex.length() == 7) {
					return new Color((i >> 16) & 0xFF, (i >> 8) & 0xFF, i & 0xFF);
				} else if (hex.length() == 9) {
					return new Color((i >> 24) & 0xFF, (i >> 16) & 0xFF, (i >> 8) & 0xFF, i & 0xFF);
				} else {
					return null;
				}

			} catch (Exception e) {
				return null;
			}

		} else {
			return null;
		}

	}

}
