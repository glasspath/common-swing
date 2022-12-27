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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Locale;
import java.util.prefs.Preferences;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;

import org.glasspath.common.Common;
import org.glasspath.common.locale.LocaleUtils;
import org.glasspath.common.os.preferences.PreferencesProvider;
import org.glasspath.common.os.preferences.PreferencesProvider.PreferencesProviderListener;
import org.glasspath.common.swing.color.ColorUtils;

public class LanguagePreferenceComboBox extends JComboBox<String> {

	private final PreferencesProvider provider;
	private final String key;
	private final String defaultValue;
	private final List<String> tags = LocaleUtils.getSupportedTags();

	public LanguagePreferenceComboBox(PreferencesProvider provider, String key, String defaultValue) {
		this(provider, key, defaultValue, true);
	}

	public LanguagePreferenceComboBox(PreferencesProvider provider, String key, String defaultValue, boolean commitOnChange) {

		this.provider = provider;
		this.key = key;
		this.defaultValue = defaultValue;

		setRenderer(new Renderer());

		addItem("Automatic");

		for (String tag : LocaleUtils.getSupportedTags()) {
			try {
				Locale locale = LocaleUtils.getLocaleForTag(tag);
				addItem(locale.getDisplayLanguage(locale) + ", " + locale.getDisplayCountry(locale));
			} catch (Exception e) {
				Common.LOGGER.error("Exception while creating locale from supported tag: " + tag, e);
			}
		}

		int index = tags.indexOf(provider.getPreferences().get(key, defaultValue));
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
				int index = tags.indexOf(provider.getPreferences().get(key, defaultValue));
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
				if (index < tags.size()) {
					value = tags.get(index);
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

	protected boolean isLanguageSupported(String languageTag) {
		return true;
	}

	public class Renderer extends DefaultListCellRenderer {

		public Renderer() {

		}

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

			if (!isSelected) {

				setForeground(ColorUtils.TEXT_COLOR);

				if (index - 1 >= 0 && index - 1 < LocaleUtils.getSupportedTags().size()) {
					if (!isLanguageSupported(LocaleUtils.getSupportedTags().get(index - 1))) {
						setForeground(ColorUtils.SEMI_DISABLED_TEXT_COLOR);
					}
				}

			}

			return this;

		}

	}

}
