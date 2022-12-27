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
package org.glasspath.common.swing.date;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Date;

import javax.swing.BorderFactory;

import org.glasspath.common.date.DateUtils;
import org.glasspath.common.format.FormatUtils;
import org.glasspath.common.swing.color.ColorUtils;
import org.glasspath.common.swing.table.Table;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXMonthView;

public class DatePicker extends JXDatePicker {

	public DatePicker() {

		JXMonthView monthView = getMonthView();
		monthView.setBorder(BorderFactory.createEmptyBorder(8, 10, 10, 10));
		monthView.setShowingLeadingDays(true);
		monthView.setShowingTrailingDays(true);
		monthView.setTodayBackground(ColorUtils.createTransparentColor(Table.SELECTION_BACKGROUND, 150)); // TODO
		monthView.setMonthStringBackground(new Color(150, 150, 150, 20));

		setTimeZone(DateUtils.TIME_ZONE);

		setFormats(FormatUtils.DATE_FORMAT);

		addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dateChanged();
			}
		});

		getEditor().addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {

				Date date = getDate();
				if (date == null) {
					date = new Date(DateUtils.getLocalTimeInGmtTime());
				}

				if (e.getKeyCode() == KeyEvent.VK_UP && !e.isControlDown()) {
					setDate(DateUtils.getDayAfter(date));
					dateChanged();
				} else if (e.getKeyCode() == KeyEvent.VK_DOWN && !e.isControlDown()) {
					setDate(DateUtils.getDayBefore(date));
					dateChanged();
				}

			}
		});

		// setLinkPanel(null);

	}

	protected void dateChanged() {

	}

}
