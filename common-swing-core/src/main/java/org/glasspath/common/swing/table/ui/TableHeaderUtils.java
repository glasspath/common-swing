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

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import org.glasspath.common.swing.theme.Theme;

public class TableHeaderUtils {

    private static final CellRendererPane CELL_RENDER_PANE = new CellRendererPane();
    
    private TableHeaderUtils() {

    }

    /**
     * Creates a component that paints the table's header background.
     *
     * @param table the {@link JTable} to create the corner component for.
     * @return a {@link JComponent} that paints the given table's table header background.
     */
 	public static JComponent createCornerComponent(JTable table) {
    	
        return new JComponent() {
        	
            @Override
            protected void paintComponent(Graphics g) {
                paintHeader(g, table, 0, getWidth());
            }
        };
        
    }

    /**
     * Installs a custom {@link Border} on the given table's {@link JTableHeader} that paints any
     * blank area to the right of the last column header with the {@code JTableHeader}'s background.
     *
     * @param table the {@link JTable} from which to get the {@code JTableHeader} to paint the
     *              empty column header space for.
     */
    public static void makeHeaderFillEmptySpace(JTable table) {
        table.getTableHeader().setBorder(createTableHeaderEmptyColumnPainter(table));
    }

    /**
     * Paints the given JTable's table default header background at given
     * x for the given width.
     *
     * @param graphics the {@link Graphics} to paint into.
     * @param table    the table that the header belongs to.
     * @param x        the x coordinate of the table header.
     * @param width    the width of the table header.
     */
    public static void paintHeader(Graphics graphics, JTable table, int x, int width) {
    	
        TableCellRenderer renderer = table.getTableHeader().getDefaultRenderer();
        Component component = renderer.getTableCellRendererComponent(table, "", false, false, -1, table.getColumnCount() - 1); //TODO: Added the -1 because we were getting ArrayOutOfIndex exceptions, don't really know yet how this is used..

        component.setBounds(0, 0, width, table.getTableHeader().getHeight());

        ((JComponent)component).setOpaque(false);
        
        //TODO: Make sure this isn't causing memory leaks
        CELL_RENDER_PANE.paintComponent(graphics, component, null, x, 0, width, table.getTableHeader().getHeight(), true);

        //TODO: This fixes a painting artifact in the upper right corner when using FlatLaf LnF
        //      The vertical divider is now also covered in white.. but this looks better than the artifact..
        if (!Theme.isSystemTheme()) {
            graphics.setColor(table.getTableHeader().getBackground());
            graphics.fillRect(x, 0, width, table.getTableHeader().getHeight() - 1);
        }

    }

    /**
     * Creates a {@link Border} that paints any empty space to the right of the last column header
     * in the given {@link JTable}'s {@link JTableHeader}.
     */
	private static Border createTableHeaderEmptyColumnPainter(JTable table) {
    	
        return new AbstractBorder() {
        	
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            	
                // if this JTableHeader is parented in a JViewport, then paint the table header
                // background to the right of the last column if neccessary.
                JComponent viewport = (JComponent)table.getParent();
                if (viewport != null && table.getWidth() < viewport.getWidth()) {
                    int startX = table.getWidth();
                    int emptyColumnWidth = viewport.getWidth() - table.getWidth();
                    paintHeader(g, table, startX, emptyColumnWidth);
                }
                
            }
        };
        
    }

}