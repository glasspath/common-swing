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
	public static final Color PURPLE = new Color(184, 119, 217);
	public static final Color GREEN = new Color(151, 191, 105);
	public static final Color RED = new Color(242, 73, 92);
	public static final Color ORANGE = new Color(237, 162, 0);
	public static final Color YELLOW = new Color(250, 222, 42);

	public static final Color DARK_31 = new Color(31, 30, 37);
	public static final Color DARK_32 = new Color(32, 31, 38);
	public static final Color DARK_35 = new Color(35, 34, 41);
	public static final Color DARK_35_B = new Color(35, 34, 40);
	public static final Color DARK_37 = new Color(37, 36, 43);
	public static final Color DARK_38 = new Color(38, 40, 43);
	public static final Color DARK_40 = new Color(40, 41, 45);
	public static final Color DARK_41 = new Color(41, 42, 46);
	public static final Color DARK_43 = new Color(43, 45, 48);
	public static final Color DARK_44 = new Color(44, 43, 48);
	public static final Color DARK_48 = new Color(48, 47, 53);
	public static final Color DARK_48_B = new Color(48, 50, 53);
	public static final Color DARK_50 = new Color(50, 52, 55);
	public static final Color DARK_52 = new Color(52, 54, 56);

	public static final Color GRAY_5 = new Color(5, 5, 5);
	public static final Color GRAY_25 = new Color(25, 25, 25);
	public static final Color GRAY_30 = new Color(30, 30, 30);
	public static final Color GRAY_35 = new Color(35, 35, 35);
	public static final Color GRAY_40 = new Color(40, 40, 40);
	public static final Color GRAY_42 = new Color(42, 42, 42);
	public static final Color GRAY_50 = new Color(50, 50, 50);
	public static final Color GRAY_55 = new Color(55, 55, 55);
	public static final Color GRAY_58 = new Color(58, 58, 58);
	public static final Color GRAY_60 = new Color(60, 60, 60);
	public static final Color GRAY_75 = new Color(75, 75, 75);
	public static final Color GRAY_80 = new Color(80, 80, 80);
	public static final Color GRAY_90 = new Color(90, 90, 90);
	public static final Color GRAY_100 = new Color(100, 100, 100);
	public static final Color GRAY_125 = new Color(125, 125, 125);
	public static final Color GRAY_150 = new Color(150, 150, 150);
	public static final Color GRAY_155 = new Color(155, 155, 155);
	public static final Color GRAY_175 = new Color(175, 175, 175);
	public static final Color GRAY_180 = new Color(180, 180, 180);
	public static final Color GRAY_187 = new Color(187, 187, 187);
	public static final Color GRAY_190 = new Color(190, 190, 190);
	public static final Color GRAY_192 = new Color(192, 192, 192);
	public static final Color GRAY_200 = new Color(200, 200, 200);
	public static final Color GRAY_210 = new Color(210, 210, 210);
	public static final Color GRAY_217 = new Color(217, 217, 217);
	public static final Color GRAY_220 = new Color(220, 220, 220);
	public static final Color GRAY_224 = new Color(224, 224, 224);
	public static final Color GRAY_225 = new Color(225, 225, 225);
	public static final Color GRAY_230 = new Color(230, 230, 230);
	public static final Color GRAY_235 = new Color(235, 235, 235);
	public static final Color GRAY_238 = new Color(238, 238, 238);
	public static final Color GRAY_245 = new Color(245, 245, 245);
	public static final Color GRAY_247 = new Color(247, 247, 247);
	public static final Color GRAY_248 = new Color(248, 248, 248);
	public static final Color GRAY_250 = new Color(250, 250, 250);
	public static final Color GRAY_254 = new Color(254, 254, 254);
	
	public static Color TITLE_BAR_COLOR;
	public static Color SELECTION_COLOR_FOCUSSED;
	public static Color SELECTION_COLOR_NOT_FOCUSSED; // TODO: Remove?
	public static Color TEXT_COLOR;
	public static Color SEMI_DISABLED_TEXT_COLOR;
	public static Color DISABLED_TEXT_COLOR;
	public static Color INVALID_INPUT_BACKGROUND;
	static {
		Theme.register(() -> {
			if (Theme.isDark()) {
				TITLE_BAR_COLOR = DARK_44;
				SELECTION_COLOR_FOCUSSED = new Color(75, 110, 175);
				SELECTION_COLOR_NOT_FOCUSSED = new Color(75, 110, 175);
				TEXT_COLOR = GRAY_187;
				SEMI_DISABLED_TEXT_COLOR = GRAY_155;
				DISABLED_TEXT_COLOR = GRAY_125;
				INVALID_INPUT_BACKGROUND = new Color(255, 150, 150);
			} else {
				TITLE_BAR_COLOR = GRAY_247;
				SELECTION_COLOR_FOCUSSED = new Color(221, 232, 248);
				SELECTION_COLOR_NOT_FOCUSSED = new Color(221, 232, 248);
				TEXT_COLOR = Color.black;
				SEMI_DISABLED_TEXT_COLOR = GRAY_125;
				DISABLED_TEXT_COLOR = GRAY_192;
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
