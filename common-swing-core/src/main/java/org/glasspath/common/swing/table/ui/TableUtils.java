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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;

/**
 * A collection of utility methods to be used with {@link JTable}.
 */
public class TableUtils {

    private TableUtils() {
        // no constructor - utility class.
    }

    /**
     * Add's striping to the background of the given {@link JTable}. The actual striping is
     * installed on the containing {@link JScrollPane}'s {@link JViewport}, so if this table is not
     * added to a {@code JScrollPane}, then no stripes will be painted. This method can be called
     * before the given table is added to a scroll pane, though, as a {@link PropertyChangeListener}
     * will be installed to handle "ancestor" changes.
     *
     * @param table      the table to paint row stripes for.
     * @param stipeColor the color of the stripes to paint.
     */
    public static void makeStriped(JTable table, Color stipeColor, boolean paintSelectedRow, boolean repaintOnSelectionChange) {
    	
        table.addPropertyChangeListener("ancestor", createAncestorPropertyChangeListener(table, stipeColor, paintSelectedRow, repaintOnSelectionChange));
        
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

            JScrollPane scrollPane = (JScrollPane)table.getParent().getParent();
            scrollPane.setViewportBorder(new StripedViewportBorder(scrollPane.getViewport(), table, stipeColor, paintSelectedRow, repaintOnSelectionChange));
            scrollPane.getViewport().setOpaque(false);
            scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, TableHeaderUtils.createCornerComponent(table));
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            
        }
        
    }

}