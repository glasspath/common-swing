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

import org.glasspath.common.locale.LocaleUtils;
import org.glasspath.common.locale.LocaleUtils.LanguageTag;
import org.glasspath.common.os.preferences.PreferencesProvider;
import org.glasspath.common.os.preferences.PreferencesProvider.PreferencesProviderListener;
import org.glasspath.common.swing.color.ColorUtils;
import org.glasspath.common.swing.preferences.LanguagePreferenceComboBox.Entry;
import org.glasspath.common.swing.resources.CommonResources;

public class LanguagePreferenceComboBox extends JComboBox<Entry> {

	private final PreferencesProvider provider;
	private final String key;
	private final String defaultValue;
	private final LanguageTag[] languageTags;
	private final Entry systemFormatEntry;
	private final Entry systemDisplayEntry;

	public LanguagePreferenceComboBox(PreferencesProvider provider, String key, String defaultValue) {
		this(provider, key, defaultValue, LanguageTag.values(), true);
	}

	public LanguagePreferenceComboBox(PreferencesProvider provider, String key, String defaultValue, LanguageTag[] languageTags, boolean commitOnChange) {

		this.provider = provider;
		this.key = key;
		this.defaultValue = defaultValue;
		this.languageTags = languageTags;

		setRenderer(new Renderer());

		Locale systemFormatLocale = LocaleUtils.getSystemFormatLocale();
		systemFormatEntry = new Entry(systemFormatLocale.getDisplayLanguage(systemFormatLocale) + ", " + systemFormatLocale.getDisplayCountry(systemFormatLocale)); //$NON-NLS-1$
		addItem(systemFormatEntry);

		Locale systemDisplayLocale = LocaleUtils.getSystemDisplayLocale();
		systemDisplayEntry = new Entry(CommonResources.getString("System") + " (" + systemDisplayLocale.getDisplayLanguage(systemDisplayLocale) + ", " + systemDisplayLocale.getDisplayCountry(systemDisplayLocale) + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		addItem(systemDisplayEntry);

		for (LanguageTag languageTag : languageTags) {
			addItem(new Entry(languageTag.language + ", " + languageTag.country)); //$NON-NLS-1$
		}

		reload();

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
				reload();
			}

			@Override
			public void preferencesStateChanged(boolean enabled) {
				if (!(getClientProperty(PreferencesUtils.PROPERTY_IGNORE_STATE_CHANGED) == Boolean.TRUE)) {
					setEnabled(enabled);
				}
			}
		});

	}

	private void reload() {

		String language = provider.getPreferences().get(key, defaultValue);

		if (LocaleUtils.SYSTEM_FORMAT_LANGUAGE_TAG.equals(language)) {
			setSelectedIndex(0);
		} else if (LocaleUtils.SYSTEM_DISPLAY_LANGUAGE_TAG.equals(language)) {
			setSelectedIndex(1);
		} else {

			int index = getLanguageTagIndex(language);
			if (index >= 0) {
				setSelectedIndex(index + 2);
			}

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

			if (index == 0) {
				value = LocaleUtils.SYSTEM_FORMAT_LANGUAGE_TAG;
			} else if (index == 1) {
				value = LocaleUtils.SYSTEM_DISPLAY_LANGUAGE_TAG;
			} else if (index >= 2) {
				index -= 2;
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

	public String getSelectedLanguageTag() {

		int index = getSelectedIndex();

		if (index == 0) {
			return LocaleUtils.SYSTEM_FORMAT_LANGUAGE_TAG;
		} else if (index == 1) {
			return LocaleUtils.SYSTEM_DISPLAY_LANGUAGE_TAG;
		} else if (index >= 2) {
			index -= 2;
			if (index < languageTags.length) {
				return languageTags[index].tag;
			}
		}

		return defaultValue;

	}

	protected boolean isLanguageSupported(String languageTag) {
		return true;
	}

	public class Entry {

		private String text = ""; //$NON-NLS-1$

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

			if (index < 2) {
				setFont(italicFont);
			} else {
				setFont(defualtFont);
			}

			if (!isSelected) {

				setForeground(ColorUtils.TEXT_COLOR);

				if (index - 2 >= 0 && index - 2 < languageTags.length) {
					if (!isLanguageSupported(languageTags[index - 2].tag)) {
						setForeground(ColorUtils.SEMI_DISABLED_TEXT_COLOR);
					}
				}

			}

			return this;

		}

	}

}
