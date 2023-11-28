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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.glasspath.common.date.DateUtils;
import org.glasspath.common.icons.Icons;
import org.glasspath.common.swing.color.ColorUtils;
import org.glasspath.common.swing.theme.Theme;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXMonthView;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.calendar.DateSelectionModel;
import org.jdesktop.swingx.plaf.basic.BasicDatePickerUI;
import org.jdesktop.swingx.plaf.basic.CalendarRenderingHandler;
import org.jdesktop.swingx.plaf.basic.CalendarState;

import com.formdev.flatlaf.swingx.ui.FlatMonthViewUI;

public class DatePicker extends JXDatePicker {

	private boolean paintClearButton = false;
	private boolean highlightClearButton = false;

	public DatePicker() {
		super();

		if (Theme.isDark()) {
			setBackground(new Color(43, 45, 48));
		}

		setMonthView(createMonthView());

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

		getEditor().addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				if (isMouseOverClearButton(e.getPoint())) {

					clear();

					getEditor().setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
					paintClearButton = false;

					repaint();

				}

			}

			@Override
			public void mouseEntered(MouseEvent e) {

				String text = getEditor().getText();
				paintClearButton = text != null && text.length() > 0;

				repaint();

			}

			@Override
			public void mouseExited(MouseEvent e) {

				getEditor().setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
				paintClearButton = false;

				repaint();

			}
		});

		getEditor().addMouseMotionListener(new MouseAdapter() {

			@Override
			public void mouseMoved(MouseEvent e) {

				String text = getEditor().getText();
				paintClearButton = text != null && text.length() > 0;

				if (paintClearButton && isMouseOverClearButton(e.getPoint())) {
					getEditor().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					highlightClearButton = true;
				} else {
					getEditor().setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
					highlightClearButton = false;
				}

				repaint();

			}
		});

	}

	@Override
	public void setTimeZone(TimeZone tz) {
		super.setTimeZone(tz);
		configureLinkPanel(this); // TODO: Setting time-zone causes issues with link panel background
	}

	public void setFlaggedDates(List<Date> flaggedDates) {
		getMonthView().setFlaggedDates(flaggedDates.toArray(new Date[0]));
	}

	public void clearFlaggedDates() {
		getMonthView().clearFlaggedDates();
	}

	public boolean isPopupVisible() {
		if (getUI() instanceof BasicDatePickerUI) {
			return ((BasicDatePickerUI) getUI()).isPopupVisible();
		} else {
			return false;
		}
	}

	public void closePopup() {
		if (getUI() instanceof BasicDatePickerUI) {
			((BasicDatePickerUI) getUI()).hidePopup();
		}
	}

	public void clear() {
		setDate(null);
		dateChanged();
	}

	protected void dateChanged() {

	}

	private boolean isMouseOverClearButton(Point p) {

		Insets insets = getInsets();
		int popupButtonWidth = getHeight() - insets.top - insets.bottom;

		return p != null && p.x >= getWidth() - popupButtonWidth - 32 && p.x < getWidth() - popupButtonWidth - 8;

	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		Graphics2D g2d = (Graphics2D) g;

		if (paintClearButton) {

			Insets insets = getInsets();
			int popupButtonWidth = getHeight() - insets.top - insets.bottom;

			Color bg = getBackground();
			g2d.setPaint(new GradientPaint(getWidth() - popupButtonWidth - 40, 0, ColorUtils.createTransparentColor(bg, 0), getWidth() - popupButtonWidth - 28, 0, bg));
			g2d.fillRect(getWidth() - popupButtonWidth - 40, insets.top + 1, 32, popupButtonWidth - 2);

			int xIcon = getWidth() - popupButtonWidth - 24;
			int yIcon = (int) Math.round(getHeight() / 2.0) - 8;

			if (highlightClearButton) {
				Icons.closeRed.paintIcon(this, g2d, xIcon, yIcon);
			} else {
				Icons.close.paintIcon(this, g2d, xIcon, yIcon);
			}

		}

	}

	public static JXMonthView createMonthView() {

		JXMonthView monthView = new JXMonthView() {

			@Override
			public void updateUI() {
				setUI(new MonthViewUI());
				invalidate();
			}

			@Override
			public String getDayOfTheWeek(int dayOfWeek) {

				String s = super.getDayOfTheWeek(dayOfWeek);
				if (s != null) {
					if (s.length() > 1) {
						s = s.substring(0, 1).toUpperCase() + s.substring(1, 2).toLowerCase();
					} else if (s.length() > 0) {
						s = s.substring(0, 1).toUpperCase();
					}
				}

				return s;

			}
		};

		configureMonthView(monthView);

		return monthView;

	}

	public static void configureMonthView(JXMonthView monthView) {

		monthView.setBorder(BorderFactory.createEmptyBorder(8, 10, 10, 10));
		monthView.setSelectionMode(DateSelectionModel.SelectionMode.SINGLE_SELECTION);
		monthView.setShowingLeadingDays(true);
		monthView.setShowingTrailingDays(true);
		monthView.setTodayBackground(new Color(84, 136, 217, 150));
		monthView.setMonthStringBackground(new Color(150, 150, 150, 20));
		monthView.setTraversable(true);

	}

	public static void configureLinkPanel(JXDatePicker datePicker) {

		// TODO: For some reason setting the time-zone causes issues with the link-panel background
		// the code below was copied from FlatDatePickerUI, for now we force the background..
		JPanel linkPanel = datePicker.getLinkPanel();
		if (linkPanel instanceof JXPanel && linkPanel.getClass().getName().equals("org.jdesktop.swingx.JXDatePicker$TodayPanel")) {

			((JXPanel) linkPanel).setBackgroundPainter(null);
			linkPanel.setBackground(UIManager.getColor("JXMonthView.background"));

			if (linkPanel.getComponentCount() >= 1 && linkPanel.getComponent(0) instanceof JXHyperlink) {
				JXHyperlink todayLink = (JXHyperlink) linkPanel.getComponent(0);
				todayLink.setUnclickedColor(UIManager.getColor("Hyperlink.linkColor"));
				todayLink.setClickedColor(UIManager.getColor("Hyperlink.visitedColor"));
			}

		}

	}

	protected static class MonthViewUI extends FlatMonthViewUI {

		@Override
		protected CalendarRenderingHandler createRenderingHandler() {
			return new ProxyRenderingHandler(super.createRenderingHandler());
		}

		@Override
		protected void paintDaysOfWeekSeparator(Graphics g, Calendar month) {
			// super.paintDaysOfWeekSeparator(g, month);
		}

		protected static class ProxyRenderingHandler extends RenderingHandler {

			private final CalendarRenderingHandler handler;
			private final JLabel dayLabel;
			private int minDayWidth = 30;

			public ProxyRenderingHandler(CalendarRenderingHandler handler) {
				this.handler = handler;

				dayLabel = new JLabel() {

					@Override
					public Dimension getPreferredSize() {
						Dimension size = super.getPreferredSize();
						if (size.width < minDayWidth) {
							size.width = minDayWidth;
						}
						return size;
					}
				};
				dayLabel.setHorizontalAlignment(JLabel.RIGHT);

			}

			public int getMinDayWidth() {
				return minDayWidth;
			}

			public void setMinDayWidth(int minDayWidth) {
				this.minDayWidth = minDayWidth;
			}

			@Override
			public JComponent prepareRenderingComponent(JXMonthView monthView, Calendar calendar, CalendarState dayState) {
				if (dayState == CalendarState.DAY_OF_WEEK) {

					JComponent comp = handler.prepareRenderingComponent(monthView, calendar, dayState);
					if (comp instanceof JLabel) {
						dayLabel.setText(((JLabel) comp).getText());
						dayLabel.setFont(((JLabel) comp).getFont());
					}

					return dayLabel;

				} else {
					return handler.prepareRenderingComponent(monthView, calendar, dayState);
				}
			}

		}

	}

}
