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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.TableCellRenderer;

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

	public static Color EVEN_ROW_COLOR;
	static {
		Theme.register(() -> {
			if (Theme.isDark()) {
				EVEN_ROW_COLOR = new Color(38, 40, 43);
			} else {
				EVEN_ROW_COLOR = new Color(245, 245, 247);
			}
		});
	}

	private static final CellRendererPane CELL_RENDER_PANE = new CellRendererPane();

	private final boolean striped;
	private final boolean paintSelectedRow;
	private final boolean repaintOnSelectionChange;

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
		table.setGridColor(striped ? gridColor : table.getBackground());
		table.setIntercellSpacing(new Dimension(0, 0));

		table.setShowHorizontalLines(false);
		makeHeaderFillEmptySpace(table); // TODO: Make sure this isn't causing memory leaks
		makeStriped(table, striped ? stripedColor : table.getBackground(), paintSelectedRow, repaintOnSelectionChange);

	}

	protected CellRendererPane createCustomCellRendererPane() {

		return new CellRendererPane() {

			@Override
			public void paintComponent(Graphics graphics, Component component, Container container, int x, int y, int w, int h, boolean shouldValidate) {

				/*
				if (component instanceof JComponent) {

					int rowAtPoint = table.rowAtPoint(new Point(x, y));
					boolean isSelected = table.isRowSelected(rowAtPoint);

					((JComponent) component).setOpaque(isSelected);

				}
				*/

				super.paintComponent(graphics, component, container, x, y, w, h, shouldValidate);

			}
		};

	}

	public static void makeStriped(JTable table, Color stipeColor, boolean paintSelectedRow, boolean repaintOnSelectionChange) {

		table.addPropertyChangeListener("ancestor", createAncestorPropertyChangeListener(table, stipeColor, paintSelectedRow, repaintOnSelectionChange)); //$NON-NLS-1$

		// install a listener to cause the whole table to repaint when a column is resized. we do
		// this because the extended grid lines may need to be repainted. this could be cleaned up,
		// but for now, it works fine.
		for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).addPropertyChangeListener(createAncestorPropertyChangeListener(table, stipeColor, paintSelectedRow, repaintOnSelectionChange));
		}

	}

	private static PropertyChangeListener createAncestorPropertyChangeListener(JTable table, Color stipeColor, boolean paintSelectedRow, boolean repaintOnSelectionChange) {

		return new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				// indicate that the parent of the JTable has changed.
				parentDidChange(table, stipeColor, paintSelectedRow, repaintOnSelectionChange);
			}
		};

	}

	private static void parentDidChange(JTable table, Color stipeColor, boolean paintSelectedRow, boolean repaintOnSelectionChange) {

		// if the parent of the table is an instance of JViewport, and that JViewport's parent is
		// a JScrollpane, then install the custom BugFixedViewportLayout.
		if (table.getParent() instanceof JViewport && table.getParent().getParent() instanceof JScrollPane) {

			JScrollPane scrollPane = (JScrollPane) table.getParent().getParent();
			scrollPane.setViewportBorder(new StripedViewportBorder(scrollPane.getViewport(), table, stipeColor, paintSelectedRow, repaintOnSelectionChange));
			scrollPane.getViewport().setOpaque(false);
			scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, createCornerComponent(table));
			scrollPane.setBorder(BorderFactory.createEmptyBorder());

		}

	}

	public static JComponent createCornerComponent(JTable table) {

		return new JComponent() {

			@Override
			protected void paintComponent(Graphics g) {
				paintHeader(g, table, 0, getWidth());
			}
		};

	}

	public static void makeHeaderFillEmptySpace(JTable table) {
		table.getTableHeader().setBorder(createTableHeaderEmptyColumnPainter(table));
	}

	public static void paintHeader(Graphics graphics, JTable table, int x, int width) {

		TableCellRenderer renderer = table.getTableHeader().getDefaultRenderer();
		Component component = renderer.getTableCellRendererComponent(table, "", false, false, -1, table.getColumnCount() - 1); // TODO: Added the -1 because we were getting ArrayOutOfIndex exceptions, don't really know yet how this is used.. //$NON-NLS-1$

		component.setBounds(0, 0, width, table.getTableHeader().getHeight());

		((JComponent) component).setOpaque(false);

		// TODO: Make sure this isn't causing memory leaks
		CELL_RENDER_PANE.paintComponent(graphics, component, null, x, 0, width, table.getTableHeader().getHeight(), true);

		// TODO: This fixes a painting artifact in the upper right corner when using FlatLaf LnF
		// The vertical divider is now also covered in white.. but this looks better than the artifact..
		if (!Theme.isSystemTheme()) {
			graphics.setColor(table.getTableHeader().getBackground());
			graphics.fillRect(x, 0, width, table.getTableHeader().getHeight() - 1);
		}

	}

	private static Border createTableHeaderEmptyColumnPainter(JTable table) {

		return new AbstractBorder() {

			@Override
			public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {

				// if this JTableHeader is parented in a JViewport, then paint the table header
				// background to the right of the last column if neccessary.
				JComponent viewport = (JComponent) table.getParent();
				if (viewport != null && table.getWidth() < viewport.getWidth()) {
					int startX = table.getWidth();
					int emptyColumnWidth = viewport.getWidth() - table.getWidth();
					paintHeader(g, table, startX, emptyColumnWidth);
				}

			}
		};

	}

}