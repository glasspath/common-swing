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
package org.glasspath.common.swing.glasspane;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.glasspath.common.swing.search.SearchField;
import org.glasspath.common.swing.search.SearchField.SearchAdapter;

public class GlassPane extends JPanel {

	private final JFrame frame;
	private final GridBagLayout layout;
	private final SearchField searchField;
	private final JLabel overlayLabel;

	private Component contentComponent = null;
	private int topMargin = 10;
	private int rightMargin = 10;

	public GlassPane(JFrame frame) {

		this.frame = frame;

		setOpaque(false);

		searchField = new SearchField(5, 5, true);
		// searchField.putClientProperty( "FlatLaf.style", "borderWidth: 2" );
		// searchField.putClientProperty( "FlatLaf.style", "borderColor: #AAA" );
		searchField.setButtonPolicy(SearchField.BUTTONS_VISIBLE_ALWAYS);
		searchField.setVisible(false);
		searchField.addSearchListener(new SearchAdapter() {

			@Override
			public void searchCleared() {

				searchField.setVisible(false);

				if (contentComponent != null) {
					contentComponent.requestFocusInWindow();
				}

			}
		});

		overlayLabel = new JLabel();
		overlayLabel.setVisible(false);

		float overlayLabelFontSize = overlayLabel.getFont().getSize2D();
		overlayLabelFontSize = overlayLabelFontSize + 4.0F;
		if (overlayLabelFontSize < 14.0F) {
			overlayLabelFontSize = 14.0F;
		}
		overlayLabel.setFont(overlayLabel.getFont().deriveFont(overlayLabelFontSize));

		layout = new GridBagLayout();
		layout.rowWeights = new double[] { 0.0, 0.0, 0.1, 0.0 };
		layout.rowHeights = new int[] { topMargin, 38, 100, 10 };
		layout.columnWeights = new double[] { 0.0, 0.0, 0.1, 0.0, 0.0 };
		layout.columnWidths = new int[] { 10, 25, 100, 250, rightMargin };
		setLayout(layout);

		add(searchField, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		add(overlayLabel, new GridBagConstraints(0, 1, 5, 2, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

	}

	public Component getContentComponent() {
		return contentComponent;
	}

	public void setContentComponent(Component contentComponent) {
		this.contentComponent = contentComponent;
	}

	public int getTopMargin() {
		return topMargin;
	}

	public void setTopMargin(int topMargin) {
		this.topMargin = topMargin;
	}

	public int getRightMargin() {
		return rightMargin;
	}

	public void setRightMargin(int rightMargin) {
		this.rightMargin = rightMargin;
	}

	public void updateLayout(boolean revalidate) {

		// Calculate title bar height
		int y = getHeight() - frame.getContentPane().getHeight();
		if (y < 0) {
			y = 0;
		}

		if (contentComponent != null) {
			y += contentComponent.getY();
		}

		layout.rowHeights[0] = y + topMargin;
		layout.columnWidths[4] = rightMargin;

		if (revalidate) {
			invalidate();
			revalidate();
			repaint();
		}

	}

	public SearchField getSearchField() {
		return searchField;
	}

	public void showSearchField() {

		updateLayout(false);

		searchField.setVisible(true);

		invalidate();
		revalidate();
		repaint();

		searchField.requestFocusInWindow();

	}

	public void hideSearchField() {

		searchField.clear();

		invalidate();
		revalidate();
		repaint();

	}

	public JLabel getOverlayLabel() {
		return overlayLabel;
	}

	public void showOverlayLabel() {
		showOverlayLabel(null);
	}

	public void showOverlayLabel(String text) {

		updateLayout(false); // TODO?

		if (text != null) {
			overlayLabel.setText(text);
		}

		overlayLabel.setVisible(true);

		invalidate();
		revalidate();
		repaint();

	}

	public boolean isOverlayLabelVisible() {
		return overlayLabel.isVisible();
	}

	public void hideOverlayLabel() {

		overlayLabel.setVisible(false);

		invalidate();
		revalidate();
		repaint();

	}

	@Override
	public boolean contains(int x, int y) {
		if (searchField.isVisible()) {
			return searchField.getBounds().contains(x, y);
		} else {
			return false;
		}
	}

}
