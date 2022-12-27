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
package org.glasspath.common.swing.table.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.BorderFactory;
import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicTableUI;

import org.glasspath.common.swing.color.ColorUtils;
import org.glasspath.common.swing.theme.Theme;

public class TableUI extends BasicTableUI {

	protected static final Color TABLE_GRID_COLOR = new Color(217, 217, 217);
	protected static final Color SELECTION_ACTIVE_SELECTION_FOREGROUND_COLOR = Color.BLACK;
	protected static final Color SELECTION_ACTIVE_SELECTION_BACKGROUND_COLOR = new Color(84, 136, 217, 25);
	protected static final Color SELECTION_INACTIVE_SELECTION_FOREGROUND_COLOR = Color.BLACK;
	protected static final Color SELECTION_INACTIVE_SELECTION_BACKGROUND_COLOR = new Color(0xc0c0c0);
	protected static final Color SELECTION_ACTIVE_BOTTOM_BORDER_COLOR = new Color(125, 170, 234);
	protected static final Color SELECTION_INACTIVE_BOTTOM_BORDER_COLOR = new Color(224, 224, 224);
	public static final Color TRANSPARENT_COLOR = new Color(0, 0, 0, 0);
	// public static final Font TABLE_FONT = UIManager.getFont("Table.font").deriveFont(11.0f);

	public static Color EVEN_ROW_COLOR;
	static {
		Theme.register(() -> {
			if (Theme.isDark()) {
				EVEN_ROW_COLOR = new Color(64, 68, 69);
			} else {
				EVEN_ROW_COLOR = new Color(245, 245, 247);
			}
		});
	}

	private final boolean striped;
	private final boolean paintSelectedRow;
	private final boolean repaintOnSelectionChange;

	private final FocusAdapter tableFocusListener;

	private Color stripedColor = EVEN_ROW_COLOR;
	private Color gridColor = TABLE_GRID_COLOR;

	public TableUI() {
		this(true, true, false);
	}

	public TableUI(boolean striped) {
		this(striped, true, false);
	}

	public TableUI(boolean striped, boolean paintSelectedRow) {
		this(striped, paintSelectedRow, false);
	}

	public TableUI(boolean striped, boolean paintSelectedRow, boolean repaintOnSelectionChange) {

		this.striped = striped;
		this.paintSelectedRow = paintSelectedRow;
		this.repaintOnSelectionChange = repaintOnSelectionChange;

		tableFocusListener = new FocusAdapter() {

			@Override
			public void focusGained(FocusEvent e) {
				makeTableActive();
			}

			@Override
			public void focusLost(FocusEvent e) {
				makeTableInactive();
			}
		};

	}

	public Color getStripedColor() {
		return stripedColor;
	}

	public void setStripedColor(Color stripedColor) {
		this.stripedColor = stripedColor;
	}

	public Color getGridColor() {
		return gridColor;
	}

	public void setGridColor(Color gridColor) {
		this.gridColor = gridColor;
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);

		table.remove(rendererPane);
		rendererPane = createCustomCellRendererPane();
		table.add(rendererPane);

		table.setOpaque(false);
		// table.setFont(TABLE_FONT);
		table.setGridColor(striped ? gridColor : table.getBackground());
		table.setIntercellSpacing(new Dimension(0, 0));

		table.setShowHorizontalLines(false);
		TableHeaderUtils.makeHeaderFillEmptySpace(table); // TODO: Make sure this isn't causing memory leaks
		TableUtils.makeStriped(table, striped ? stripedColor : table.getBackground(), paintSelectedRow, repaintOnSelectionChange);

		makeTableActive();

	}

	@Override
	protected void installListeners() {
		super.installListeners();
		table.addFocusListener(tableFocusListener);
	}

	@Override
	public void uninstallListeners() {
		super.uninstallListeners();
		table.removeFocusListener(tableFocusListener);
	}

	private void makeTableActive() {
		table.setSelectionBackground(ColorUtils.SELECTION_COLOR_FOCUSSED);
	}

	private void makeTableInactive() {
		table.setSelectionBackground(ColorUtils.SELECTION_COLOR_NOT_FOCUSSED);
	}

	public Border getRowBorder() {
		return BorderFactory.createEmptyBorder(0, 5, 0, 5);
	}

	public Border getSelectedRowBorder() {
		return BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, getSelectedRowBottomHighlight()), BorderFactory.createEmptyBorder(1, 5, 0, 5));
	}

	private Color getSelectedRowBottomHighlight() {
		return WindowUtils.isParentWindowFocused(table) ? SELECTION_ACTIVE_BOTTOM_BORDER_COLOR : SELECTION_INACTIVE_BOTTOM_BORDER_COLOR;
	}

	/**
	 * Creates a custom {@link CellRendererPane} that sets the renderer component to be non-opaque if the associated row isn't selected. This custom {@code CellRendererPane} is needed because a table UI delegate has no prepare renderer like {@link JTable} has.
	 */
	protected CellRendererPane createCustomCellRendererPane() {

		return new CellRendererPane() {

			@Override
			public void paintComponent(Graphics graphics, Component component, Container container, int x, int y, int w, int h, boolean shouldValidate) {

				int rowAtPoint = table.rowAtPoint(new Point(x, y));
				boolean isSelected = table.isRowSelected(rowAtPoint);

				if (component instanceof JComponent) {
					JComponent jComponent = (JComponent) component;
					jComponent.setOpaque(isSelected);
					// jComponent.setBorder(isSelected ? getSelectedRowBorder() : getRowBorder());
					// jComponent.setBackground(isSelected ? table.getSelectionBackground() : TRANSPARENT_COLOR);
				}

				super.paintComponent(graphics, component, container, x, y, w, h, shouldValidate);

			}
		};

	}

}