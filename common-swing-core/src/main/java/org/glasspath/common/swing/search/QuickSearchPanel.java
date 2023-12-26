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
package org.glasspath.common.swing.search;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public abstract class QuickSearchPanel extends JPanel {

	protected final SearchField searchField;
	protected final JPanel contentPanel;

	public QuickSearchPanel() {

		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setLayout(new BorderLayout());

		searchField = new SearchField() {

			@Override
			public void clear() {
				setText(""); //$NON-NLS-1$
			}
		};
		searchField.setPreferredSize(new Dimension(100, 25));
		add(searchField, BorderLayout.NORTH);
		searchField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				apply();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				apply();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				apply();
			}

			private void apply() {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						setFilter(searchField.getText());
					}
				});
			}
		});

		contentPanel = new JPanel();
		contentPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
		add(contentPanel, BorderLayout.CENTER);

	}

	public SearchField getSearchField() {
		return searchField;
	}

	public JPanel getContentPanel() {
		return contentPanel;
	}

	public abstract void setFilter(String filter);
	
}
