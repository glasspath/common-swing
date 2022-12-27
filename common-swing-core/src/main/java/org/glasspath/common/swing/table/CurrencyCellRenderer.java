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
package org.glasspath.common.swing.table;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.glasspath.common.format.FormatUtils;
import org.glasspath.common.swing.SwingUtils;

public class CurrencyCellRenderer extends DefaultTableCellRenderer {

	private final CurrencyLabel currencyLabel = new CurrencyLabel();

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		
		JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		currencyLabel.setFont(label.getFont());
		currencyLabel.setText(FormatUtils.CURRENCY_FORMAT.format(Double.parseDouble(value.toString())));
		currencyLabel.setBackground(label.getBackground());
		currencyLabel.setForeground(label.getForeground());
		
		return currencyLabel;
		
	}

	private class CurrencyLabel extends JLabel {

		public CurrencyLabel() {
			setOpaque(true);
			setHorizontalAlignment(JLabel.RIGHT);
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			// g.drawImage(Icons.euro_16x16.getImage(), 2, 1, null);

			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			// g2d.setFont(g2d.getFont().deriveFont(11.0F));
			g2d.setColor(getForeground());

			/*
			FontMetrics fontMetrics = g2d.getFontMetrics();
			Rectangle2D fontRect = fontMetrics.getStringBounds(FormatUtils.CURRENCY_SYMBOL, g2d);
			SwingUtils.drawString(currencyLabel, g2d, FormatUtils.CURRENCY_SYMBOL, 5, (int)((getHeight() / 2) + (fontRect.getHeight() / 2) - 1));
			*/
			int y = getBaseline(getWidth(), getHeight());
			SwingUtils.drawString(currencyLabel, g2d, FormatUtils.CURRENCY_SYMBOL, 5, y);

		}

	}

}
