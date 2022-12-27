/*
 * This file is part of Glasspath Aerialist.
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.text.JTextComponent;

public class UISearchHandler {

	public static final Color HIGHLIGHT_COLOR = new Color(84, 136, 217);
	public static final Color OCCURENCE_COLOR = new Color(84, 136, 217, 100);

	public static final int MODE_ADD_HIGHLIGHTS = 0;
	public static final int MODE_SEARCH = 1;

	private final Container container;

	private HighlightPainter occurenceHighlighter = new DefaultHighlightPainter(OCCURENCE_COLOR);
	private HighlightPainter searchHighlighter = new DefaultHighlightPainter(HIGHLIGHT_COLOR);
	private String searchText = "";
	private Thread searchThread = null;
	private boolean threadPaused = false;
	private boolean exit = false;
	private Object activeHighlight = null;

	public UISearchHandler(Container container) {
		this.container = container;
	}

	public void searchNext(String text) {

		if (exit && isSearchThreadAlive()) {
			System.err.println("UISearchHandler: TODO: searchNext() called while searchThread is exiting");
		} else if (text != null && text.length() > 0) {

			// TODO: Handle changing of search text
			searchText = text.toLowerCase();

			exit = false;
			threadPaused = false;

			if (!isSearchThreadAlive()) {

				searchThread = new Thread(new Runnable() {

					@Override
					public void run() {

						parseContainer(container, MODE_ADD_HIGHLIGHTS);
						parseContainer(container, MODE_SEARCH);
						clearHighlights(container);

					}
				});
				searchThread.start();

			}

		}

	}

	private boolean isSearchThreadAlive() {
		return searchThread != null && searchThread.isAlive();
	}

	public void cancelSearch() {
		exit = true;
		threadPaused = false;
	}

	private void parseContainer(Container container, int mode) {

		for (int i = 0; i < container.getComponentCount(); i++) {

			if (exit) {
				return;
			}

			Component component = container.getComponent(i);

			if (component instanceof JTextComponent) {

				JTextComponent textComponent = (JTextComponent) component;

				String text = textComponent.getText().toLowerCase();

				int index = text.indexOf(searchText);

				while (!exit && index >= 0) {

					if (mode == MODE_SEARCH) {
						threadPaused = true;
					}

					int textIndex = index;
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {

							if (mode == MODE_ADD_HIGHLIGHTS) {

								try {
									textComponent.getHighlighter().addHighlight(textIndex, textIndex + searchText.length(), occurenceHighlighter);
								} catch (BadLocationException e1) {
									e1.printStackTrace();
								}

							} else if (mode == MODE_SEARCH) {

								try {
									activeHighlight = textComponent.getHighlighter().addHighlight(textIndex, textIndex + searchText.length(), searchHighlighter);
								} catch (BadLocationException e1) {
									e1.printStackTrace();
								}

								textFound(textComponent, searchText, textIndex);

							}

						}
					});

					while (!exit && threadPaused) {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					if (!exit && mode == MODE_SEARCH) {

						Object removeHighlight = activeHighlight;
						if (removeHighlight != null) {

							SwingUtilities.invokeLater(new Runnable() {

								@Override
								public void run() {
									textComponent.getHighlighter().removeHighlight(removeHighlight);
								}
							});

						}

					}

					if (!exit) {
						index = text.indexOf(searchText, index + searchText.length() + 1);
					}

				}

			} else if (component instanceof Container) {
				parseContainer((Container) component, mode);
			}

		}

	}

	private void clearHighlights(Container container) {

		for (int i = 0; i < container.getComponentCount(); i++) {

			Component component = container.getComponent(i);

			if (component instanceof JTextComponent) {

				JTextComponent textComponent = (JTextComponent) component;
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						textComponent.getHighlighter().removeAllHighlights();
					}
				});

			} else if (component instanceof Container) {
				clearHighlights((Container) component);
			}

		}

	}

	public void textFound(JTextComponent component, String text, int index) {

	}

}
