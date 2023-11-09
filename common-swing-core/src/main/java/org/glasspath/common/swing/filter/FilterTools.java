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
package org.glasspath.common.swing.filter;

import java.awt.Dimension;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.glasspath.common.swing.search.SearchField;
import org.glasspath.common.swing.table.Filterable;

public class FilterTools {

	public static final int DEFAULT_WIDTH = 250;

	private Filterable filterable;

	private final JToolBar toolBar;
	private final SearchField searchField;

	private DateFilterTools dateFilterTools = null;

	public FilterTools(Filterable filterable) {
		this(filterable, DEFAULT_WIDTH);
	}

	public FilterTools(Filterable filterable, int width) {

		this.filterable = filterable;
		this.toolBar = new JToolBar();

		toolBar.setOpaque(false);
		toolBar.setFloatable(false);
		toolBar.setRollover(true);
		toolBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 1));

		searchField = new SearchField() {

			@Override
			public void clear() {
				clearFilter();
			}

			@Override
			public Dimension getPreferredSize() {
				Dimension preferredSize = super.getPreferredSize();
				preferredSize.width = width;
				return preferredSize;
			}
		};
		searchField.setMaximumSize(new Dimension(width, searchField.getFont().getSize() >= 14.0F ? 35 : 26)); // TODO?
		toolBar.add(searchField);
		searchField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent e) {
				applyFilter();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				applyFilter();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				applyFilter();
			}
		});

	}

	public JToolBar getToolBar() {
		return toolBar;
	}

	public DateFilterTools getDateFilterTools() {
		return dateFilterTools;
	}

	public void setDateFilterTools(DateFilterTools dateFilterTools) {
		this.dateFilterTools = dateFilterTools;
	}

	public void applyFilter() {

		if (dateFilterTools != null) {

			Date earliestDate = dateFilterTools.getEarliestDate();
			Date latestDate = dateFilterTools.getLatestDate();
			filterable.setFilter(searchField.getText(), earliestDate, latestDate);
			dateFilterTools.setResultCount(filterable.getFilterResultCount());

		} else {
			filterable.setFilter(searchField.getText(), null, null);
		}

		if (searchField.getText().length() > 0 && filterable.getFilterResultCount() == 0) {
			searchField.setBackground(searchField.getNoResultBackground());
		} else {
			searchField.setBackground(searchField.getDefaultBackground());
		}

	}

	public Filterable getFilterable() {
		return filterable;
	}

	public void setFilterable(Filterable filterable) {
		this.filterable = filterable;
	}

	public String getFilter() {
		return searchField.getText();
	}

	public void setFilter(String filter) {
		searchField.setText(filter);
	}

	public void clearFilter() {
		searchField.setText(""); //$NON-NLS-1$
	}

	public JTextField getFilterField() {
		return searchField;
	}

}
