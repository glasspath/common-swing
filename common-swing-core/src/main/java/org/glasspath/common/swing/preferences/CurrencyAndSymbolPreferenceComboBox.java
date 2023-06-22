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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.JTextField;

import org.glasspath.common.locale.LocaleUtils.CurrencyCode;
import org.glasspath.common.os.preferences.PreferencesProvider;
import org.glasspath.common.swing.SwingUtils;

public class CurrencyAndSymbolPreferenceComboBox extends JComponent {

	private final PreferencesProvider provider;
	private final String symbolKey;
	private final String defaultSymbolValue;
	protected final CurrencyPreferenceComboBox currencyComboBox;
	protected final SymbolTextField symbolTextField;

	public CurrencyAndSymbolPreferenceComboBox(PreferencesProvider provider, String currencyKey, String defaultCurrencyValue, String symbolKey, String defaultSymbolValue, boolean commitOnChange) {

		this.provider = provider;
		this.symbolKey = symbolKey;
		this.defaultSymbolValue = defaultSymbolValue;

		setLayout(new BorderLayout());

		currencyComboBox = new CurrencyPreferenceComboBox(provider, currencyKey, defaultCurrencyValue, commitOnChange, false);
		add(currencyComboBox, BorderLayout.CENTER);
		currencyComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				updateSymbol();
			}
		});

		symbolTextField = new SymbolTextField();
		symbolTextField.setText(provider.getPreferences().get(symbolKey, defaultSymbolValue));
		add(symbolTextField, BorderLayout.EAST);

		updateSymbol();

	}

	protected void updateSymbol() {

		CurrencyCode currencyCode = currencyComboBox.getSelectedCurrencyCode();
		if (currencyCode != null) {
			symbolTextField.automaticText = currencyCode.symbol;
		} else {
			symbolTextField.automaticText = null;
		}

		symbolTextField.repaint();

	}

	public void setAutomaticLocale(Locale locale, boolean showCurrencyDetails) {
		currencyComboBox.setAutomaticLocale(locale, showCurrencyDetails);
		updateSymbol();
	}

	public void commit() {

		currencyComboBox.commit();

		String symbol = symbolTextField.getText();

		if (symbol != null && symbol.length() > 0) {
			if (symbol.equals(defaultSymbolValue)) {
				provider.getPreferences().remove(symbolKey);
			} else {
				provider.getPreferences().put(symbolKey, symbol);
			}
		} else {
			provider.getPreferences().remove(symbolKey);
		}

	}

	protected static class SymbolTextField extends JTextField {

		protected String automaticText = null;

		public SymbolTextField() {

		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);

			if (getText().length() == 0 && automaticText != null) {

				int yText = getBaseline(getWidth(), getHeight());

				g.setColor(Color.gray);
				SwingUtils.drawString(this, (Graphics2D) g, automaticText, 9, yText);

			}

		}

	}

}
