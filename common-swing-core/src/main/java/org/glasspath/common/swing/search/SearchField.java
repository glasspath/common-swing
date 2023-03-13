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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.glasspath.common.icons.Icons;
import org.glasspath.common.swing.SwingUtils;
import org.glasspath.common.swing.color.ColorUtils;
import org.glasspath.common.swing.resources.Resources;

public class SearchField extends JTextField {

	public static final int BUTTONS_VISIBLE_WHEN_NEEDED = 0;
	public static final int BUTTONS_VISIBLE_ALWAYS = 1;

	private final int leftMargin;
	private final int rightMargin;
	private final boolean navigationButtonsEnabled;
	private final SearchButtons searchButtons;
	private final List<SearchListener> listeners = new ArrayList<>();
	private int buttonPolicy = BUTTONS_VISIBLE_WHEN_NEEDED;
	private Color defaultBackground = getBackground();

	public SearchField() {
		this(0, 0, false);
	}

	public SearchField(int leftMargin, int rightMargin) {
		this(leftMargin, rightMargin, false);
	}

	public SearchField(int leftMargin, int rightMargin, boolean navigationButtonsEnabled) {

		this.leftMargin = leftMargin;
		this.rightMargin = rightMargin;
		this.navigationButtonsEnabled = navigationButtonsEnabled;
		this.searchButtons = new SearchButtons();

		setLayout(null);
		add(searchButtons);

		setMargin(new Insets(0, leftMargin + 22, 0, rightMargin + searchButtons.getPreferredSize().width));

		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				searchNext();
			}
		});

		// putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT, new JButton("X"));

		// See https://bugs.openjdk.org/browse/JDK-8298017
		setAutoscrolls(false);

	}

	@Override
	public void doLayout() {
		super.doLayout();

		int w = navigationButtonsEnabled ? 65 : 25;
		searchButtons.setBounds(getWidth() - (w + rightMargin), 0, w, getHeight());

	}

	public void addSearchListener(SearchListener listener) {
		listeners.add(listener);
	}

	public void removeSearchListener(SearchListener listener) {
		listeners.remove(listener);
	}

	public int getButtonPolicy() {
		return buttonPolicy;
	}

	public void setButtonPolicy(int buttonPolicy) {
		this.buttonPolicy = buttonPolicy;
	}

	public void clear() {
		setText(""); //$NON-NLS-1$
		fireSearchCleared();
	}

	public void searchNext() {
		fireSearchNext(getText());
	}

	public void searchPrevious() {
		fireSearchPrevious(getText());
	}

	private void fireSearchCleared() {
		for (SearchListener listener : listeners) {
			listener.searchCleared();
		}
	}

	private void fireSearchNext(String text) {
		for (SearchListener listener : listeners) {
			listener.searchNext(text);
		}
	}

	private void fireSearchPrevious(String text) {
		for (SearchListener listener : listeners) {
			listener.searchPrevious(text);
		}
	}

	public Color getDefaultBackground() {
		return defaultBackground;
	}

	public Color getNoResultBackground() {
		return ColorUtils.INVALID_INPUT_BACKGROUND;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		int yIcon = (int) Math.round(getHeight() / 2.0) - 8;
		Icons.magnify.paintIcon(this, g, leftMargin + 6, yIcon);

		if (getText().length() == 0) {

			int yText = getBaseline(getWidth(), getHeight());

			g.setColor(Color.lightGray);
			SwingUtils.drawString(this, (Graphics2D) g, Resources.getString("Search"), leftMargin + 27, yText); //$NON-NLS-1$

		}

	}

	private class SearchButtons extends JComponent {

		public static final int BUTTON_NONE = 0;
		public static final int BUTTON_CLEAR = 1;
		public static final int BUTTON_NEXT = 2;
		public static final int BUTTON_PREVIOUS = 3;

		private int highlightButton = BUTTON_NONE;
		private boolean buttonPressed = false;

		private SearchButtons() {

			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

			addMouseListener(new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent e) {

					buttonPressed = true;
					repaint();

					switch (getButtonAtMouse(e.getPoint())) {

					case BUTTON_CLEAR:
						clear();
						break;

					case BUTTON_NEXT:
						searchNext();
						break;

					case BUTTON_PREVIOUS:
						searchPrevious();
						break;

					default:
						break;
					}

				}

				@Override
				public void mouseReleased(MouseEvent e) {
					buttonPressed = false;
					repaint();
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					highlightButton = getButtonAtMouse(e.getPoint());
					buttonPressed = false;
					repaint();
				}

				@Override
				public void mouseExited(MouseEvent e) {
					highlightButton = BUTTON_NONE;
					buttonPressed = false;
					repaint();
				}
			});

			addMouseMotionListener(new MouseAdapter() {

				@Override
				public void mouseMoved(MouseEvent e) {
					highlightButton = getButtonAtMouse(e.getPoint());
					buttonPressed = false;
					repaint();
				}
			});

		}

		private int getButtonAtMouse(Point p) {

			if (p != null) {

				if (p.x > getWidth() - 25) {
					return BUTTON_CLEAR;
				} else if (navigationButtonsEnabled) {

					if (p.x > getWidth() - 45 && p.x < getWidth() - 25) {
						return BUTTON_NEXT;
					} else if (p.x > getWidth() - 65 && p.x < getWidth() - 45) {
						return BUTTON_PREVIOUS;
					}

				}

			}

			return BUTTON_NONE;

		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);

			Graphics2D g2d = (Graphics2D) g;

			if (buttonPolicy == BUTTONS_VISIBLE_ALWAYS || (buttonPolicy == BUTTONS_VISIBLE_WHEN_NEEDED && getText().length() > 0)) {

				int yIcon = (int) Math.round(getHeight() / 2.0) - 8;
				Composite oldComposite = g2d.getComposite();

				if (highlightButton == BUTTON_CLEAR) {
					Icons.closeRed.paintIcon(this, g2d, getWidth() - 21, yIcon);
				} else {
					Icons.close.paintIcon(this, g2d, getWidth() - 21, yIcon);
				}

				if (navigationButtonsEnabled) {

					if (highlightButton == BUTTON_NEXT) {

						if (buttonPressed) {
							g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25F));
						}
						Icons.chevronRightBlue.paintIcon(this, g2d, getWidth() - 41, yIcon);
						g2d.setComposite(oldComposite);

					} else {
						Icons.chevronRight.paintIcon(this, g2d, getWidth() - 41, yIcon);
					}

					if (highlightButton == BUTTON_PREVIOUS) {

						if (buttonPressed) {
							g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25F));
						}
						Icons.chevronLeftBlue.paintIcon(this, g2d, getWidth() - 61, yIcon);
						g2d.setComposite(oldComposite);

					} else {
						Icons.chevronLeft.paintIcon(this, g2d, getWidth() - 61, yIcon);
					}

				}

			}

		}

	}

	public static interface SearchListener {

		public void searchCleared();

		public void searchNext(String text);

		public void searchPrevious(String text);

	}

	public static class SearchAdapter implements SearchListener {

		@Override
		public void searchCleared() {

		}

		@Override
		public void searchNext(String text) {

		}

		@Override
		public void searchPrevious(String text) {

		}

	}

}
