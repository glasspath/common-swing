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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.glasspath.common.swing.color.ColorUtils;
import org.glasspath.common.swing.date.DatePicker;

public class DateFilterTools {

	private final JToolBar toolBar;
	private final DatePickersPanel datePickersPanel;
	private final GridBagLayout datePickersPanelLayout;
	private final DatePicker firstDatePicker;
	private final DatePicker secondDatePicker;
	private final Dimension maximumSize = new Dimension(300, 26);

	private final List<ActionListener> actionListeners = new ArrayList<>();

	public DateFilterTools() {

		toolBar = new JToolBar() {

			@Override
			public void updateUI() {
				super.updateUI();
				setBackground(ColorUtils.TITLE_BAR_COLOR);
			}

		};
		toolBar.setBorder(BorderFactory.createEmptyBorder());
		toolBar.setFloatable(false);
		toolBar.setRollover(true);

		/*
		JButton filterButton = new JButton();
		filterButton.setIcon(Icons.loupe);
		filterButton.setToolTipText("Filter");
		toolBar.add(filterButton);
		filterButton.addActionListener(new ActionListener() {
		
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO?
			}
		});
		*/

		firstDatePicker = new DatePicker() {

			@Override
			protected void dateChanged() {
				fireFilterChanged();
			}
		};

		secondDatePicker = new DatePicker() {

			@Override
			protected void dateChanged() {
				fireFilterChanged();
			}
		};

		datePickersPanel = new DatePickersPanel();

		// TODO
		if (firstDatePicker.getFont().getSize() >= 14.0F) {
			maximumSize.width = 400;
			maximumSize.height = 35;
		}

		datePickersPanelLayout = new GridBagLayout();
		datePickersPanelLayout.rowWeights = new double[] { 0.1 };
		datePickersPanelLayout.rowHeights = new int[] { 26 };
		datePickersPanelLayout.columnWeights = new double[] { 0.0, 0.1, 0.1 };
		datePickersPanelLayout.columnWidths = new int[] { 0, 125, 125 };
		datePickersPanel.setLayout(datePickersPanelLayout);

		datePickersPanel.add(firstDatePicker, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		datePickersPanel.add(secondDatePicker, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		toolBar.add(datePickersPanel);

	}

	public void setBackground(Color background) {
		toolBar.setBackground(background);
		datePickersPanel.setBackground(background);
	}

	public void removeSecondDatePicker() {

		datePickersPanel.remove(secondDatePicker);
		datePickersPanelLayout.columnWeights[2] = 0.0;
		datePickersPanelLayout.columnWidths[2] = 0;

		// TODO
		if (firstDatePicker.getFont().getSize() >= 14.0F) {
			maximumSize.width = 200;
		} else {
			maximumSize.width = 150;
		}

	}

	public void addActionListener(ActionListener actionListener) {
		actionListeners.add(actionListener);
	}

	public void removeActionListener(ActionListener actionListener) {
		actionListeners.remove(actionListener);
	}

	private void fireFilterChanged() {
		for (ActionListener listener : actionListeners) {
			listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
		}
		datePickersPanel.repaint();
	}

	public void clearFilter() {
		clearFilter(true);
	}

	public void clearFilter(boolean fireEvent) {

		firstDatePicker.setDate(null);
		secondDatePicker.setDate(null);

		if (fireEvent) {
			fireFilterChanged();
		}

	}

	public Date getDate() {
		return getEarliestDate();
	}

	public void setDate(Date date, boolean fireEvent) {

		firstDatePicker.setDate(date);
		secondDatePicker.setDate(null);

		if (fireEvent) {
			fireFilterChanged();
		}

	}

	public void setDate(long date) {
		firstDatePicker.setDate(new Date(date));
		secondDatePicker.setDate(null);
		fireFilterChanged();
	}

	public void setDates(long dateFrom, long dateTo) {
		firstDatePicker.setDate(new Date(dateFrom));
		secondDatePicker.setDate(new Date(dateTo));
		fireFilterChanged();
	}

	public JToolBar getToolBar() {
		return toolBar;
	}

	public Date getEarliestDate() {
		if (firstDatePicker.getDate() != null && secondDatePicker.getDate() != null) {
			if (firstDatePicker.getDate().before(secondDatePicker.getDate())) {
				return new Date(firstDatePicker.getDate().getTime());
			} else {
				return new Date(secondDatePicker.getDate().getTime());
			}
		} else if (firstDatePicker.getDate() != null) {
			return new Date(firstDatePicker.getDate().getTime());
		} else if (secondDatePicker.getDate() != null) {
			return new Date(secondDatePicker.getDate().getTime());
		} else {
			return null;
		}
	}

	public Date getLatestDate() {
		if (firstDatePicker.getDate() != null && secondDatePicker.getDate() != null) {
			if (firstDatePicker.getDate().after(secondDatePicker.getDate())) {
				return new Date(firstDatePicker.getDate().getTime());
			} else {
				return new Date(secondDatePicker.getDate().getTime());
			}
		} else if (firstDatePicker.getDate() != null) {
			return new Date(firstDatePicker.getDate().getTime());
		} else if (secondDatePicker.getDate() != null) {
			return new Date(secondDatePicker.getDate().getTime());
		} else {
			return null;
		}
	}

	public void setResultCount(int resultCount) {
		if ((firstDatePicker.getDate() != null || secondDatePicker.getDate() != null) && resultCount == 0) {
			firstDatePicker.getEditor().setBackground(datePickersPanel.noResultBackground);
			secondDatePicker.getEditor().setBackground(datePickersPanel.noResultBackground);
			// datePickersPanel.setBackground(datePickersPanel.noResultBackground);
		} else {
			firstDatePicker.getEditor().setBackground(datePickersPanel.defaultBackground);
			secondDatePicker.getEditor().setBackground(datePickersPanel.defaultBackground);
			// datePickersPanel.setBackground(datePickersPanel.defaultBackground);
		}
	}

	private class DatePickersPanel extends JPanel {

		private final Color defaultBackground = Color.white;
		private final Color noResultBackground = new Color(255, 150, 150);
		// private boolean highlightClearIcon = false;

		private DatePickersPanel() {

			// setMaximumSize(maximumSize);
			// setPreferredSize(size);

			// JTextField dummyTextField = new JTextField();
			// setBorder(dummyTextField.getBorder());
			// setBackground(dummyTextField.getBackground());
			/*
			addMouseListener(new MouseAdapter() {
			
				@Override
				public void mousePressed(MouseEvent e) {
					if (isPointOverClearIcon(e.getPoint())) {
						clearFilter();
					}
				}
			
				@Override
				public void mouseEntered(MouseEvent e) {
					updateClearIcon(e.getPoint());
				}
			
				@Override
				public void mouseExited(MouseEvent e) {
					highlightClearIcon = false;
					repaint();
				}
			});
			
			addMouseMotionListener(new MouseAdapter() {
			
				@Override
				public void mouseMoved(MouseEvent e) {
					updateClearIcon(e.getPoint());
				}
			});
			 */
		}
		/*
		private boolean isPointOverClearIcon(Point point) {
			return point.x < 20;
		}
		
		private void updateClearIcon(Point point) {
			highlightClearIcon = isPointOverClearIcon(point);
			repaint();
		}
		
		@Override
		public void paint(Graphics g) {
			super.paint(g);
		
			if (firstDatePicker.getDate()!=null || secondDatePicker.getDate()!=null) {			
				if (highlightClearIcon) {
					g.drawImage(Icons.remove2_16x16.getImage(), 4, 3, null);					
				} else {
					g.drawImage(Icons.delete_simple_16x16.getImage(), 4, 3, null);
				}
			}
		
		}
		 */

		@Override
		public Dimension getMaximumSize() {
			return maximumSize;
		}

		@Override
		public Dimension getPreferredSize() {
			Dimension preferredSize = super.getPreferredSize();
			preferredSize.width = maximumSize.width;
			return preferredSize;
		}

		@Override
		public void updateUI() {
			super.updateUI();
			setBackground(ColorUtils.TITLE_BAR_COLOR);
		}

	}

}
