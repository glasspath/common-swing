/*
 * This file is part of Glasspath Common.
 * Copyright (C) 2011 - 2023 Remco Poelstra
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
package org.glasspath.common.swing.table;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.glasspath.common.swing.resources.CommonResources;

public class ColumnHandler {

	private final JTable table;
	private final TableColumnModel columnModel;
	private final Preferences preferences;
	private final String preferencesKey;
	private final TableColumnModelListener columnModelListener;
	private final MouseAdapter tableHeaderMouseListener;
	private final List<Column> columns = new ArrayList<>();

	private boolean updatingTableColumns = false;

	public ColumnHandler(JTable table, Preferences preferences, String preferencesKey) {

		this.table = table;
		this.columnModel = table.getColumnModel();
		this.preferences = preferences;
		this.preferencesKey = preferencesKey;

		columnModelListener = new TableColumnModelListener() {

			@Override
			public void columnMoved(TableColumnModelEvent e) {

				if (!updatingTableColumns) {

					for (int i = 0; i < columns.size(); i++) {

						if (columns.get(i).preferredIndex >= 0) {
							preferences.putInt(preferencesKey + i + "Index", table.convertColumnIndexToView(i)); //$NON-NLS-1$
						}

					}

					ColumnHandler.this.columnMoved(e);

				}

			}

			@Override
			public void columnMarginChanged(ChangeEvent e) {

				if (!updatingTableColumns) {

					for (int i = 0; i < columns.size(); i++) {

						int width = columns.get(i).tableColumn.getWidth();
						if (width <= 0) {
							width = columns.get(i).defaultWidth;
						}

						preferences.putInt(preferencesKey + i + "Width", width); //$NON-NLS-1$

					}

					ColumnHandler.this.columnMarginChanged(e);

				}

			}

			@Override
			public void columnSelectionChanged(ListSelectionEvent e) {

			}

			@Override
			public void columnRemoved(TableColumnModelEvent e) {

			}

			@Override
			public void columnAdded(TableColumnModelEvent e) {

			}
		};
		columnModel.addColumnModelListener(columnModelListener);

		tableHeaderMouseListener = new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					createMenu().getPopupMenu().show(e.getComponent(), e.getX(), e.getY());
				}
			}
		};
		table.getTableHeader().addMouseListener(tableHeaderMouseListener);

		for (int i = 0; i < columnModel.getColumnCount(); i++) {
			columns.add(new Column(columnModel.getColumn(i)));
		}

	}

	public JTable getTable() {
		return table;
	}

	public Preferences getPreferences() {
		return preferences;
	}

	public String getPreferencesKey() {
		return preferencesKey;
	}

	public void uninstallListeners() {
		columnModel.removeColumnModelListener(columnModelListener);
		table.getTableHeader().removeMouseListener(tableHeaderMouseListener);
	}

	public void columnMoved(TableColumnModelEvent e) {

	}

	public void columnMarginChanged(ChangeEvent e) {

	}

	public void columnVisibilityChanged() {

	}

	public void setColumnDefaultHidden(int columnIndex) {

		Column column = columns.get(columnIndex);
		column.defaultHidden = true;
		column.preferredIndex = -1 - columnIndex;

		if (preferences.getInt(preferencesKey + columnIndex + "Index", Integer.MAX_VALUE) == Integer.MAX_VALUE) { //$NON-NLS-1$
			preferences.putInt(preferencesKey + columnIndex + "Index", column.preferredIndex); //$NON-NLS-1$
		}

	}

	public void setColumnHidden(int columnIndex, boolean hidden) {

		Column column = columns.get(columnIndex);
		column.preferredIndex = preferences.getInt(preferencesKey + columnIndex + "Index", column.preferredIndex); //$NON-NLS-1$

		if (hidden) {

			if (column.preferredIndex >= 0 && getVisibleColumnCount() > 1) {

				int viewIndex = table.convertColumnIndexToView(columnIndex);
				if (viewIndex >= 0) {
					column.preferredIndex = -1 - viewIndex;
				} else {
					column.preferredIndex = -1 - columnIndex;
				}

				columnModel.removeColumn(column.tableColumn);

			}
		} else {

			if (column.preferredIndex < 0) {

				column.tableColumn.setPreferredWidth(preferences.getInt(preferencesKey + columnIndex + "Width", column.defaultWidth)); //$NON-NLS-1$
				columnModel.addColumn(column.tableColumn);

				int viewIndex = preferences.getInt(preferencesKey + columnIndex + "Index", column.preferredIndex); //$NON-NLS-1$
				if (viewIndex < 0) {
					viewIndex = -1 - viewIndex;
				}
				if (viewIndex >= 0 && viewIndex < columnModel.getColumnCount() - 1) {
					columnModel.moveColumn(columnModel.getColumnCount() - 1, viewIndex);
				}

				column.preferredIndex = table.convertColumnIndexToView(columnIndex);

			}

		}

		preferences.putInt(preferencesKey + columnIndex + "Index", column.preferredIndex); //$NON-NLS-1$

		columnVisibilityChanged();

	}

	public void setColumnDisabled(int columnIndex, boolean disabled) {
		columns.get(columnIndex).disabled = disabled;
	}

	public int getVisibleColumnCount() {

		int count = 0;

		for (Column column : columns) {
			if (column.isVisible()) {
				count++;
			}
		}

		return count;

	}

	public void apply() {
		apply(preferencesKey, false);
	}

	public void apply(String preferencesKey, boolean save) {

		updatingTableColumns = true;

		for (int i = 0; i < columns.size(); i++) {

			Column column = columns.get(i);
			column.preferredIndex = preferences.getInt(preferencesKey + i + "Index", column.preferredIndex); //$NON-NLS-1$
			column.tableColumn.setPreferredWidth(preferences.getInt(preferencesKey + i + "Width", column.defaultWidth)); //$NON-NLS-1$

			if (save) {
				preferences.putInt(this.preferencesKey + i + "Index", column.preferredIndex); //$NON-NLS-1$
				preferences.putInt(this.preferencesKey + i + "Width", column.tableColumn.getPreferredWidth()); //$NON-NLS-1$
			}

		}

		List<Column> sortedColumns = new ArrayList<>();
		sortedColumns.addAll(columns);

		Collections.sort(sortedColumns, new Comparator<Column>() {

			@Override
			public int compare(Column o1, Column o2) {
				return Integer.compare(o1.preferredIndex, o2.preferredIndex);
			}
		});

		while (columnModel.getColumnCount() > 0) {
			columnModel.removeColumn(columnModel.getColumn(0));
		}

		for (Column column : sortedColumns) {
			if (column.preferredIndex >= 0 && !column.disabled) {
				columnModel.addColumn(column.tableColumn);
			}
		}

		updatingTableColumns = false;

	}

	public void reset() {

		Column column;
		for (int i = 0; i < columns.size(); i++) {

			column = columns.get(i);

			if (column.defaultHidden) {
				column.preferredIndex = -1 - i;
				preferences.putInt(preferencesKey + i + "Index", column.preferredIndex); //$NON-NLS-1$
			} else {
				column.preferredIndex = 0;
				preferences.remove(preferencesKey + i + "Index"); //$NON-NLS-1$
			}

			preferences.remove(preferencesKey + i + "Width"); //$NON-NLS-1$

		}

	}

	public String getColumnName(int column) {
		return table.getModel().getColumnName(column);
	}

	private JMenu createMenu() {

		JMenu menu = new JMenu();

		for (int i = 0; i < columns.size(); i++) {

			int columnIndex = i;
			Column column = columns.get(columnIndex);

			if (!column.disabled) {

				JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(getColumnName(column.tableColumn.getModelIndex()));
				menu.add(menuItem);
				menuItem.setSelected(!column.isHidden());
				menuItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						setColumnHidden(columnIndex, !column.isHidden());
					}
				});

			}

		}

		menu.addSeparator();

		JMenuItem resetMenuItem = new JMenuItem(CommonResources.getString("Reset")); //$NON-NLS-1$
		menu.add(resetMenuItem);
		resetMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				reset();
				apply();
			}
		});

		return menu;

	}

	public static class Column {

		private TableColumn tableColumn = null;
		private int defaultWidth = 0;
		private boolean defaultHidden = false;
		private int preferredIndex = 0;
		private boolean disabled = false;

		public Column(TableColumn tableColumn) {
			this.tableColumn = tableColumn;
			this.defaultWidth = tableColumn.getPreferredWidth();
		}

		public boolean isHidden() {
			return preferredIndex < 0;
		}

		public boolean isVisible() {
			return preferredIndex >= 0 && !disabled;
		}

	}

}
