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

import static java.awt.RenderingHints.KEY_TEXT_ANTIALIASING;
import static java.awt.RenderingHints.KEY_TEXT_LCD_CONTRAST;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.geom.Rectangle2D;
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.glasspath.common.os.OsUtils;

@SuppressWarnings("nls")
public class SwingUtils {

	private static final SwingUtils instance = new SwingUtils();

	private SwingUtils() {

	}

	public static boolean isControlOrCmdDown(InputEvent e) {
		if (OsUtils.PLATFORM_MACOS) {
			return e.isMetaDown();
		} else {
			return e.isControlDown();
		}
	}

	public static void installFocusJumpListenerOnJTextField(final JTextField fromTextField, final JTextField toTextField, final int jumpAtTextLength) {

		fromTextField.getDocument().addDocumentListener(new DocumentListener() {

			String previousText = "";

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				previousText = fromTextField.getText();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				if (fromTextField.getText().length() == jumpAtTextLength && previousText.length() < jumpAtTextLength) {
					toTextField.requestFocusInWindow();
				}
				previousText = fromTextField.getText();
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				previousText = fromTextField.getText();
			}
		});

	}

	public static void installSelectAllOnFocusListenerOnJTextField(final JTextField textField) {

		textField.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				textField.selectAll();
			}
		});

	}

	public static void installTextLengthLimitOnJTextField(JTextField textField, int textLengthLimit) {
		textField.setDocument(instance.new TextLengthLimitDocument(textLengthLimit));
	}

	private class TextLengthLimitDocument extends PlainDocument {

		private int limit;

		public TextLengthLimitDocument(int limit) {
			super();
			this.limit = limit;
		}

		@Override
		public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
			if (str == null) {
				return;
			} else if ((getLength() + str.length()) <= limit) {
				super.insertString(offset, str, attr);
			}
		}

	}

	// Copied (and modified) from deprecated sun.swing.SwingUtilities2
	public static void drawString(JComponent c, Graphics2D g2d, String text, float x, float y) {

		final Object aaHint = (c == null) ? null : c.getClientProperty(KEY_TEXT_ANTIALIASING);
		if (aaHint != null) {

			Object oldContrast = null;
			Object oldAAValue = g2d.getRenderingHint(KEY_TEXT_ANTIALIASING);
			if (aaHint != oldAAValue) {
				g2d.setRenderingHint(KEY_TEXT_ANTIALIASING, aaHint);
			} else {
				oldAAValue = null;
			}

			Object lcdContrastHint = c.getClientProperty(KEY_TEXT_LCD_CONTRAST);
			if (lcdContrastHint != null) {
				oldContrast = g2d.getRenderingHint(KEY_TEXT_LCD_CONTRAST);
				if (lcdContrastHint.equals(oldContrast)) {
					oldContrast = null;
				} else {
					g2d.setRenderingHint(KEY_TEXT_LCD_CONTRAST, lcdContrastHint);
				}
			}

			g2d.drawString(text, x, y);

			if (oldAAValue != null) {
				g2d.setRenderingHint(KEY_TEXT_ANTIALIASING, oldAAValue);
			}
			if (oldContrast != null) {
				g2d.setRenderingHint(KEY_TEXT_LCD_CONTRAST, oldContrast);
			}

		} else {
			g2d.drawString(text, (int) x, (int) y);
		}

	}

	public static void drawString(JComponent c, Graphics2D g2d, String text, float x, float y, float measuredWidth, float measuredHeight, float maxWidth) {

		final Shape oldClip = g2d.getClip();

		if (measuredWidth > maxWidth) {

			final Rectangle clip;
			if (oldClip instanceof Rectangle) {
				final Rectangle oldClipRect = (Rectangle) oldClip;
				int width = (int) (x + maxWidth) - oldClipRect.x - 10;
				if (width < 1) {
					width = 1;
				}
				clip = new Rectangle(oldClipRect.x, oldClipRect.y, width, oldClipRect.height);
			} else {
				int width = (int) maxWidth - 10;
				if (width < 1) {
					width = 1;
				}
				clip = new Rectangle((int) x, (int) (y - 100), width, 200);
			}
			g2d.setClip(clip);

		}

		final Object aaHint = (c == null) ? null : c.getClientProperty(KEY_TEXT_ANTIALIASING);
		if (aaHint != null) {

			Object oldContrast = null;
			Object oldAAValue = g2d.getRenderingHint(KEY_TEXT_ANTIALIASING);
			if (aaHint != oldAAValue) {
				g2d.setRenderingHint(KEY_TEXT_ANTIALIASING, aaHint);
			} else {
				oldAAValue = null;
			}

			Object lcdContrastHint = c.getClientProperty(KEY_TEXT_LCD_CONTRAST);
			if (lcdContrastHint != null) {
				oldContrast = g2d.getRenderingHint(KEY_TEXT_LCD_CONTRAST);
				if (lcdContrastHint.equals(oldContrast)) {
					oldContrast = null;
				} else {
					g2d.setRenderingHint(KEY_TEXT_LCD_CONTRAST, lcdContrastHint);
				}
			}

			g2d.drawString(text, x, y);

			if (oldAAValue != null) {
				g2d.setRenderingHint(KEY_TEXT_ANTIALIASING, oldAAValue);
			}
			if (oldContrast != null) {
				g2d.setRenderingHint(KEY_TEXT_LCD_CONTRAST, oldContrast);
			}

		} else {
			g2d.drawString(text, (int) x, (int) y);
		}

		g2d.setClip(oldClip);

		if (measuredWidth > maxWidth) {
			g2d.fillRect((int) x + (int) maxWidth - 7, (int) y - 1, 1, 1);
			g2d.fillRect((int) x + (int) maxWidth - 4, (int) y - 1, 1, 1);
			g2d.fillRect((int) x + (int) maxWidth - 1, (int) y - 1, 1, 1);
		}

	}

	public static String fitTextToWidth(String s, Graphics2D g2d, int width) {

		final FontMetrics fontMetrics = g2d.getFontMetrics();
		Rectangle2D fontRect = fontMetrics.getStringBounds(s, g2d);

		if (fontRect.getWidth() <= width) {
			return s;
		} else {

			while (fontRect.getWidth() > width && s.length() > 0) {

				s = s.substring(0, s.length() - 1);

				// Remove trailing spaces
				while (s.endsWith(" ") && s.length() > 0) {
					s = s.substring(0, s.length() - 1);
				}

				fontRect = fontMetrics.getStringBounds(s + "...", g2d);

			}

			return s + "...";

		}

	}

	// Copied from deprecated sun.swing.SwingUtilities2
	public static int getUIDefaultsInt(Object key, Locale l, int defaultValue) {
		Object value = UIManager.get(key, l);

		if (value instanceof Integer) {
			return ((Integer) value).intValue();
		}
		if (value instanceof String) {
			try {
				return Integer.parseInt((String) value);
			} catch (NumberFormatException nfe) {
			}
		}
		return defaultValue;
	}

}
