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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.glasspath.common.os.preferences.Pref;
import org.glasspath.common.os.preferences.PreferencesProvider;
import org.glasspath.common.os.preferences.PreferencesProvider.PreferencesProviderListener;

public class PreferencesUtils {

	public static final String PROPERTY_IGNORE_STATE_CHANGED = "preferencesProvider.ignoreStateChanged"; //$NON-NLS-1$

	private PreferencesUtils() {

	}

	public static JTextField createTextField(PreferencesProvider provider, Pref pref) {
		return createTextField(provider, pref.getKey(), pref.getDefaultStringValue(), JTextField.LEFT);
	}

	public static JTextField createTextField(PreferencesProvider provider, Pref pref, int alignment) {
		return createTextField(provider, pref.getKey(), pref.getDefaultStringValue(), alignment);
	}

	public static JTextField createTextField(PreferencesProvider provider, String key, String defaultValue, int alignment) {

		JTextField textField = new JTextField();
		textField.setHorizontalAlignment(alignment);
		configureTextField(textField, provider, key, defaultValue);

		return textField;

	}

	public static void configureTextField(JTextField textField, PreferencesProvider provider, String key, String defaultValue) {

		textField.setText(provider.getPreferences().get(key, defaultValue));

		textField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				update();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				update();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				update();
			}

			private void update() {

				if (!provider.isUpdatingPreferences()) {

					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							String value = textField.getText();
							if (defaultValue.equals(value)) {
								provider.getPreferences().remove(key);
							} else {
								provider.getPreferences().put(key, value);
							}
						}
					});

				}

			}

		});

		provider.addListener(new PreferencesProviderListener() {

			@Override
			public void preferencesChanged(Preferences preferences) {
				textField.setText(provider.getPreferences().get(key, defaultValue));
			}

			@Override
			public void preferencesStateChanged(boolean enabled) {
				if (!(textField.getClientProperty(PROPERTY_IGNORE_STATE_CHANGED) == Boolean.TRUE)) {
					textField.setEnabled(enabled);
				}
			}
		});

	}

	public static JCheckBox createCheckBox(PreferencesProvider provider, Pref pref, String text) {
		return createCheckBox(provider, pref.getKey(), pref.getDefaultBooleanValue(), text);
	}

	public static JCheckBox createCheckBox(PreferencesProvider provider, String key, boolean defaultValue, String text) {

		JCheckBox checkBox = new JCheckBox(text);
		configureCheckBox(checkBox, provider, key, defaultValue);

		return checkBox;

	}

	public static void configureCheckBox(JCheckBox checkBox, PreferencesProvider provider, String key, boolean defaultValue) {

		checkBox.setSelected(provider.getPreferences().getBoolean(key, defaultValue));

		checkBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!provider.isUpdatingPreferences()) {
					boolean value = checkBox.isSelected();
					if (value == defaultValue) {
						provider.getPreferences().remove(key);
					} else {
						provider.getPreferences().putBoolean(key, value);
					}
				}
			}
		});

		provider.addListener(new PreferencesProviderListener() {

			@Override
			public void preferencesChanged(Preferences preferences) {
				checkBox.setSelected(provider.getPreferences().getBoolean(key, defaultValue));
			}

			@Override
			public void preferencesStateChanged(boolean enabled) {
				if (!(checkBox.getClientProperty(PROPERTY_IGNORE_STATE_CHANGED) == Boolean.TRUE)) {
					checkBox.setEnabled(enabled);
				}
			}
		});

	}

	public static <T> JComboBox<T> createComboBox(PreferencesProvider provider, Pref pref, T[] items) {
		return createComboBox(provider, pref.getKey(), pref.getDefaultIntValue(), items, null);
	}

	public static <T> JComboBox<T> createComboBox(PreferencesProvider provider, Pref pref, T[] items, ListCellRenderer<T> renderer) {
		return createComboBox(provider, pref.getKey(), pref.getDefaultIntValue(), items, renderer);
	}

	public static <T> JComboBox<T> createComboBox(PreferencesProvider provider, String key, int defaultValue, T[] items, ListCellRenderer<T> renderer) {

		JComboBox<T> comboBox = new JComboBox<>();
		if (items != null) {
			for (T item : items) {
				comboBox.addItem(item);
			}
		}
		configureComboBox(comboBox, provider, key, defaultValue);

		if (renderer != null) {
			comboBox.setRenderer(renderer);
		}

		return comboBox;

	}

	public static void configureComboBox(JComboBox<?> comboBox, PreferencesProvider provider, String key, int defaultValue) {

		int index = provider.getPreferences().getInt(key, defaultValue);
		if (index >= 0 && index < comboBox.getItemCount()) {
			comboBox.setSelectedIndex(index);
		}

		comboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!provider.isUpdatingPreferences()) {
					int value = comboBox.getSelectedIndex();
					if (value == defaultValue) {
						provider.getPreferences().remove(key);
					} else {
						provider.getPreferences().putInt(key, value);
					}
				}
			}
		});

		provider.addListener(new PreferencesProviderListener() {

			@Override
			public void preferencesChanged(Preferences preferences) {
				comboBox.setSelectedIndex(provider.getPreferences().getInt(key, defaultValue));
			}

			@Override
			public void preferencesStateChanged(boolean enabled) {
				if (!(comboBox.getClientProperty(PROPERTY_IGNORE_STATE_CHANGED) == Boolean.TRUE)) {
					comboBox.setEnabled(enabled);
				}
			}
		});

	}

}
