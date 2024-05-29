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
package org.glasspath.common.font;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("nls")
public class Fonts {

	public static final List<String> BASIC_FONT_FAMILIES = new ArrayList<>();
	static {
		BASIC_FONT_FAMILIES.add("Arial");
		BASIC_FONT_FAMILIES.add("Courier");
		BASIC_FONT_FAMILIES.add("Halvetica");
		BASIC_FONT_FAMILIES.add("Tahoma");
		BASIC_FONT_FAMILIES.add("Times New Roman");
		BASIC_FONT_FAMILIES.add("Verdana");
	}

	private Fonts() {

	}

	public static List<String> registerBundledFonts(String bundledFontsPath, FontFilter filter) {

		List<String> fontFamilyNames = new ArrayList<>();

		if (bundledFontsPath != null && bundledFontsPath.length() > 0) {
			registerFonts(new File(bundledFontsPath), fontFamilyNames, filter);
		}

		return fontFamilyNames;

	}

	private static void registerFonts(File fontsDir, List<String> fontFamilyNames, FontFilter filter) {

		if (fontsDir != null && fontsDir.isDirectory()) {

			GraphicsEnvironment graphicsEnvirontment = GraphicsEnvironment.getLocalGraphicsEnvironment();

			for (File file : fontsDir.listFiles()) {

				if (file.isDirectory()) {
					registerFonts(file, fontFamilyNames, filter);
				} else if (filter == null || filter.filter(file)) {

					String name = file.getName().toLowerCase();

					if (name.endsWith(".ttf") || name.endsWith(".otf") || name.endsWith(".afm")) {

						try {

							Font font = Font.createFont(Font.TRUETYPE_FONT, file);

							// graphicsEnvirontment.registerFont(font.deriveFont(12.0F));
							graphicsEnvirontment.registerFont(font);

							if (!fontFamilyNames.contains(font.getFamily())) {
								fontFamilyNames.add(font.getFamily());
							}

						} catch (Exception e) {
							e.printStackTrace();
						}

					}

				}

			}

		} else {
			System.err.println("Bundled fonts directory not found: " + fontsDir);
		}

	}

	public interface FontFilter {

		public boolean filter(File file);

	}

}
