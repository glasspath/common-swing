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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import org.glasspath.common.swing.table.Table;
import org.glasspath.common.swing.theme.Theme;

/**
 * Creates a border for a {@link JViewport} that draws a striped background
 * corresponding to the row positions of the given {@link JTable}.
 */
public class StripedViewportBorder extends AbstractBorder implements ListSelectionListener, PropertyChangeListener {

	private final JViewport viewport;
	private final JTable table;
	private final Color stripeColor;
	private final boolean paintSelectedRow;
	private final boolean repaintOnSelectionChange;
	private final Color gridColor;

	public StripedViewportBorder(JViewport viewport, JTable table, Color stripeColor, boolean paintSelectedRow, boolean repaintOnSelectionChange) {

		this.viewport = viewport;
		this.table = table;
		this.stripeColor = stripeColor;
		this.paintSelectedRow = paintSelectedRow;
		this.repaintOnSelectionChange = repaintOnSelectionChange;
		
		if (Theme.isDark()) {
			gridColor = new Color(60, 60, 60);
		} else {
			gridColor = table.getGridColor();
		}

		table.getSelectionModel().addListSelectionListener(this);
		table.addPropertyChangeListener(this);

	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		paintStripedBackground(g, y);
		paintVerticalGridLines(g, y, height);
	}

	private void paintStripedBackground(Graphics g, int borderY) {

		// get the row index at the top of the clip bounds (the first row
		// to paint).
		Rectangle clip = g.getClipBounds();
		Point viewPosition = viewport.getViewPosition();
		int rowAtPoint = table.rowAtPoint(viewPosition);

		// get the y coordinate of the first row to paint. if there are no
		// rows in the table, start painting at the top of the supplied
		// clipping bounds.
		int topY = rowAtPoint < 0 ? borderY : table.getCellRect(rowAtPoint, 0, true).y - viewPosition.y + borderY;

		// create a counter variable to hold the current row. if there are no
		// rows in the table, start the counter at 0.
		int currentRow = rowAtPoint < 0 ? 0 : rowAtPoint;
		int rowHeight = table.getRowHeight();

		final Border border;
		if (paintSelectedRow && table.getSelectedRowCount() > 1 && table.getUI() instanceof TableUI) {
			border = ((TableUI) table.getUI()).getSelectedRowBorder();
		} else {
			border = null;
		}

		while (topY < clip.y + clip.height) {

			int bottomY = topY + rowHeight;

			g.setColor(getRowColor(currentRow));
			g.fillRect(clip.x, topY, clip.width, rowHeight);

			if (border != null && table.isRowSelected(currentRow)) {
				border.paintBorder(viewport, g, 0, topY, viewport.getWidth(), table.getRowHeight());
			}

			topY = bottomY;

			currentRow++;

		}

	}

	private Color getRowColor(int row) {
		if (paintSelectedRow && table.isRowSelected(row)) {
			return Table.SELECTION_BACKGROUND;
		} else {
			return row % 2 == 0? table.getBackground() : stripeColor;			
		}
	}

	private void paintVerticalGridLines(Graphics g, int y, int height) {

		final Graphics2D g2d = (Graphics2D)g;

		// paint the column grid dividers for the non-existent rows.
		int x = 0 - viewport.getViewPosition().x + viewport.getLocation().x;
		g.setColor(gridColor);

		for (int i = 0; i < table.getColumnCount(); i++) {

			TableColumn column = table.getColumnModel().getColumn(i);

			// increase the x position by the width of the current column.
			x += column.getWidth();
			//g.setColor(table.getGridColor());

			// draw the grid line (not sure what the -1 is for, but BasicTableUI
			// also does it.source
			if (g2d.getTransform().getScaleX() >= 1.5) {
				g2d.scale(0.5, 0.5);
				g.drawLine((x * 2) - 1, y * 2, (x * 2) - 1, (y + height) * 2);
				g2d.scale(2.0, 2.0);
			} else {
				g.drawLine(x - 1, y, x - 1, y + height);
			}

		}

	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (table.getWidth() < viewport.getWidth() || repaintOnSelectionChange) {
			viewport.repaint(); //TODO: This isn't very good for performance..
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		if (evt.getSource().equals(table)) {

			if (evt.getPropertyName().equals("selectionModel")) {

				final ListSelectionModel oldModel = (ListSelectionModel)evt.getOldValue();
				final ListSelectionModel newModel = (ListSelectionModel)evt.getNewValue();
				oldModel.removeListSelectionListener(this);
				newModel.addListSelectionListener(this);

			} else if (evt.getPropertyName().equals("selectionBackground")) {
				viewport.repaint();
			}

		}

	}

}