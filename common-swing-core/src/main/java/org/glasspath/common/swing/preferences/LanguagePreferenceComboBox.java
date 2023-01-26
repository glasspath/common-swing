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
package org.glasspath.common.swing.preferences;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.prefs.Preferences;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;

import org.glasspath.common.locale.LocaleUtils.LanguageTag;
import org.glasspath.common.os.preferences.PreferencesProvider;
import org.glasspath.common.os.preferences.PreferencesProvider.PreferencesProviderListener;
import org.glasspath.common.swing.color.ColorUtils;
import org.glasspath.common.swing.preferences.LanguagePreferenceComboBox.Entry;

public class LanguagePreferenceComboBox extends JComboBox<Entry> {

	private final PreferencesProvider provider;
	private final String key;
	private final String defaultValue;
	private final LanguageTag[] languageTags;
	private final Entry automaticEntry;

	public LanguagePreferenceComboBox(PreferencesProvider provider, String key, String defaultValue) {
		this(provider, key, defaultValue, LanguageTag.values(), true);
	}

	public LanguagePreferenceComboBox(PreferencesProvider provider, String key, String defaultValue, LanguageTag[] languageTags, boolean commitOnChange) {

		this.provider = provider;
		this.key = key;
		this.defaultValue = defaultValue;
		this.languageTags = languageTags;

		setRenderer(new Renderer());

		automaticEntry = new Entry("Automatic");
		addItem(automaticEntry);

		for (LanguageTag languageTag : languageTags) {
			addItem(new Entry(languageTag.language + ", " + languageTag.country));
		}

		int index = getLanguageTagIndex(provider.getPreferences().get(key, defaultValue));
		if (index >= 0) {
			setSelectedIndex(index + 1);
		}

		if (commitOnChange) {

			addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					commit();
				}
			});

		}

		provider.addListener(new PreferencesProviderListener() {

			@Override
			public void preferencesChanged(Preferences preferences) {
				int index = getLanguageTagIndex(provider.getPreferences().get(key, defaultValue));
				if (index > 0) {
					setSelectedIndex(index + 1);
				}
			}

			@Override
			public void preferencesStateChanged(boolean enabled) {
				if (!(getClientProperty(PreferencesUtils.PROPERTY_IGNORE_STATE_CHANGED) == Boolean.TRUE)) {
					setEnabled(enabled);
				}
			}
		});

	}

	public void setAutomaticLocale(Locale locale, boolean showLocaleDetails) {

		if (locale != null) {

			if (showLocaleDetails) {
				automaticEntry.text = locale.getDisplayLanguage(locale) + ", " + locale.getDisplayCountry(locale);
			} else {
				automaticEntry.text = "Automatic";
			}

			invalidate();
			validate();
			repaint();

		} else {
			automaticEntry.text = "Automatic";
		}

	}

	private int getLanguageTagIndex(String tag) {

		if (tag != null) {

			for (int i = 0; i < languageTags.length; i++) {
				if (tag.equals(languageTags[i].tag)) {
					return i;
				}
			}

		}

		return -1;

	}

	public String getKey() {
		return key;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void commit() {

		if (!provider.isUpdatingPreferences()) {

			String value = defaultValue;

			int index = getSelectedIndex();
			if (index > 0) {
				index--;
				if (index < languageTags.length) {
					value = languageTags[index].tag;
				}
			}

			if (value != null) {
				if (value.equals(defaultValue)) {
					provider.getPreferences().remove(key);
				} else {
					provider.getPreferences().put(key, value);
				}
			} else {
				provider.getPreferences().remove(key);
			}

		}

	}

	public LanguageTag getSelectedLanguageTag() {

		LanguageTag languageTag = null;

		int index = getSelectedIndex();
		if (index > 0) {
			index--;
			if (index < languageTags.length) {
				languageTag = languageTags[index];
			}
		}

		return languageTag;

	}

	protected boolean isLanguageSupported(String languageTag) {
		return true;
	}

	public class Entry {

		private String text = "";

		public Entry(String text) {
			this.text = text;
		}

		@Override
		public String toString() {
			return text;
		}

	}

	public class Renderer extends DefaultListCellRenderer {

		private final Font defualtFont;
		private final Font italicFont;

		public Renderer() {
			defualtFont = getFont();
			italicFont = defualtFont.deriveFont(Font.ITALIC);
		}

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

			if (index == 0) {
				setFont(italicFont);
			} else {
				setFont(defualtFont);
			}

			if (!isSelected) {

				setForeground(ColorUtils.TEXT_COLOR);

				if (index - 1 >= 0 && index - 1 < languageTags.length) {
					if (!isLanguageSupported(languageTags[index - 1].tag)) {
						setForeground(ColorUtils.SEMI_DISABLED_TEXT_COLOR);
					}
				}

			}

			return this;

		}

	}

}
