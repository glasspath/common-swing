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
import org.glasspath.common.locale.LocaleUtils.CurrencyCode;
import org.glasspath.common.os.preferences.PreferencesProvider;
import org.glasspath.common.os.preferences.PreferencesProvider.PreferencesProviderListener;
import org.glasspath.common.swing.color.ColorUtils;
import org.glasspath.common.swing.preferences.CurrencyPreferenceComboBox.Entry;
import org.glasspath.common.swing.resources.CommonResources;

public class CurrencyPreferenceComboBox extends JComboBox<Entry> {

	private final PreferencesProvider provider;
	private final String key;
	private final String defaultValue;
	private final boolean showSymbols;
	private final Entry automaticEntry;
	private final CurrencyCode[] currencyCodes = CurrencyCode.values();
	private CurrencyCode automaticCurrencyCode = null;

	public CurrencyPreferenceComboBox(PreferencesProvider provider, String key, String defaultValue) {
		this(provider, key, defaultValue, true);
	}

	public CurrencyPreferenceComboBox(PreferencesProvider provider, String key, String defaultValue, boolean commitOnChange) {
		this(provider, key, defaultValue, commitOnChange, true);
	}

	public CurrencyPreferenceComboBox(PreferencesProvider provider, String key, String defaultValue, boolean commitOnChange, boolean showSymbols) {

		this.provider = provider;
		this.key = key;
		this.defaultValue = defaultValue;
		this.showSymbols = showSymbols;

		setRenderer(new Renderer());

		automaticEntry = new Entry(CommonResources.getString("Automatic")); //$NON-NLS-1$
		addItem(automaticEntry);

		for (CurrencyCode currencyCode : currencyCodes) {
			if (showSymbols) {
				addItem(new Entry(currencyCode.code + ", " + currencyCode.symbol)); //$NON-NLS-1$
			} else {
				addItem(new Entry(currencyCode.code));
			}
		}

		int index = getCurrencyCodeIndex(provider.getPreferences().get(key, defaultValue));
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
				int index = getCurrencyCodeIndex(provider.getPreferences().get(key, defaultValue));
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

	public void setAutomaticLocale(Locale locale, boolean showCurrencyDetails) {

		automaticCurrencyCode = LocaleUtils.getCurrencyCodeForLocale(locale);
		if (automaticCurrencyCode != null) {

			if (showCurrencyDetails) {
				if (showSymbols) {
					automaticEntry.text = CommonResources.getString("Automatic") + " (" + automaticCurrencyCode.code + ", " + automaticCurrencyCode.symbol + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				} else {
					automaticEntry.text = CommonResources.getString("Automatic") + " (" + automaticCurrencyCode.code + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
			} else {
				automaticEntry.text = CommonResources.getString("Automatic"); //$NON-NLS-1$
			}

			invalidate();
			validate();
			repaint();

		} else {
			automaticEntry.text = CommonResources.getString("Automatic"); //$NON-NLS-1$
		}

	}

	private int getCurrencyCodeIndex(String code) {

		if (code != null) {

			for (int i = 0; i < currencyCodes.length; i++) {
				if (code.equals(currencyCodes[i].code)) {
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

	public CurrencyCode getSelectedCurrencyCode() {

		int index = getSelectedIndex();

		if (index == 0) {
			return automaticCurrencyCode;
		} else if (index > 0) {
			index--;
			if (index < currencyCodes.length) {
				return currencyCodes[index];
			}
		}

		return null;

	}

	public void commit() {

		if (!provider.isUpdatingPreferences()) {

			String value = defaultValue;

			int index = getSelectedIndex();
			if (index > 0) {
				index--;
				if (index < currencyCodes.length) {
					value = currencyCodes[index].code;
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

	protected boolean isCurrencySupported(String currencyCode) {
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

			if (index == 0) {
				setFont(italicFont);
			} else {
				setFont(defualtFont);
			}

			if (!isSelected) {

				setForeground(ColorUtils.TEXT_COLOR);

				if (index - 1 >= 0 && index - 1 < currencyCodes.length) {
					if (!isCurrencySupported(currencyCodes[index - 1].code)) {
						setForeground(ColorUtils.SEMI_DISABLED_TEXT_COLOR);
					}
				}

			}

			return this;

		}

	}

}
