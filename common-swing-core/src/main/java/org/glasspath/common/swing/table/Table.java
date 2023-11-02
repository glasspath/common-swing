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
package org.glasspath.common.swing.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.Printable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.regex.PatternSyntaxException;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.SortOrder;
import javax.swing.TransferHandler;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.glasspath.common.date.DateUtils;
import org.glasspath.common.icons.Icons;
import org.glasspath.common.os.OsUtils;
import org.glasspath.common.swing.color.ColorUtils;
import org.glasspath.common.swing.resources.Resources;
import org.glasspath.common.swing.table.ui.TableUI;
import org.glasspath.common.swing.undo.UndoManager;

public class Table extends JTable implements Filterable {

	// TODO
	public static int MINIMUM_ROW_HEIGHT = 27;

	public static final Color SELECTION_BACKGROUND = new Color(84, 136, 217);
	public static final Color FOCUSED_CELL_BACKGROUND = new Color(44, 96, 177);
	public static final Color DEFAULT_FOREGROUND = ColorUtils.TEXT_COLOR;
	public static final Color SELECTION_FOREGROUND = Color.white;
	public static final Color DISABLED_FOREGROUND = ColorUtils.DISABLED_TEXT_COLOR;
	public static final Color ALTERNATING_BACKGROUND = new Color(245, 245, 247);
	public static final Color GRID_COLOR = new Color(225, 225, 225);
	public static final int MINIMUM_TOOLTIP_DELAY_AFTER_SCROLL = 250;

	public static final Border DEFAULT_CELL_BORDER = BorderFactory.createEmptyBorder(0, 4, 0, 4);
	public static final Border DEFAULT_CELL_EDITOR_BORDER = BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(SELECTION_BACKGROUND), BorderFactory.createEmptyBorder(0, 4, 0, 4));
	public static final Border CELL_BUTTON_CELL_BORDER = BorderFactory.createEmptyBorder(0, 4, 0, 25);

	private final ModelListener modelListener;
	private final RowSorterListener rowSorterListener;

	private TableRowSorter<TableModel> sorter = null;
	private int dateColumn1ForFilter = -1;
	private int dateColumn2ForFilter = -1;

	private RowFilter<Object, Object> baseFilter;

	private final HashMap<Integer, JButton> cellButtons = new HashMap<Integer, JButton>();

	private boolean columnHidingAllowed = true;
	private final HashMap<Integer, TableColumn> hiddenColumns = new HashMap<Integer, TableColumn>();

	private final ArrayList<TableListener> listeners = new ArrayList<TableListener>();
	private boolean reloading = false;

	private boolean alternatingBackgroundEnabled = true;

	private final KeyAdapter keyListener = new KeyAdapter() {

		@Override
		public void keyPressed(KeyEvent e) {
			if (!isEditing() && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				clearSelection();
			}
		}
	};

	private UndoManager undoManager = null;
	private Preferences preferences = null;
	private String preferencesKey = null;
	private boolean loadingColumnSettings = false;
	private long lastScroll = 0;

	public Table() {
		this(null);
	}

	public Table(TableModel model) {

		super(null); // We set the model later

		this.modelListener = new ModelListener() {

			@Override
			public void tableWillChange() {
				fireTableWillChange();
			}

			@Override
			public void tableChanged(TableModelEvent event) {
				fireTableChanged();
			}
		};

		this.rowSorterListener = new RowSorterListener() {

			@Override
			public void sorterChanged(RowSorterEvent event) {
				if (event.getType() == RowSorterEvent.Type.SORTED) {
					fireTableChanged();
				}
			}
		};

		setModel(model);

		if (getRowHeight() < MINIMUM_ROW_HEIGHT) {
			setRowHeight(MINIMUM_ROW_HEIGHT);
		}

		// TODO: Also lose focus when menu's are shown etc.
		// This property also causes editCellAt to break..
		// putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		putClientProperty("JTable.autoStartsEdit", false); //$NON-NLS-1$

		setCellSelectionEnabled(true);
		// setSelectionBackground(SELECTION_BACKGROUND);
		setSelectionBackground(FOCUSED_CELL_BACKGROUND);
		setSelectionForeground(SELECTION_FOREGROUND);

		setShowHorizontalLines(false);
		setShowVerticalLines(false);
		setGridColor(GRID_COLOR);

		setAutoResizeMode(AUTO_RESIZE_OFF);
		setFillsViewportHeight(true);

		setDefaultRenderer(Boolean.class, new BooleanCellRenderer());
		setDefaultRenderer(Date.class, new DateCellRenderer());
		setDefaultRenderer(Float.class, new DecimalFormatCellRenderer());
		setDefaultRenderer(Double.class, new DecimalFormatCellRenderer());

		setDefaultEditor(Float.class, new FloatCellEditor());
		setDefaultEditor(Double.class, new FloatCellEditor());

		setDragEnabled(true);
		setDropMode(DropMode.INSERT_ROWS);
		setTransferHandler(new TableRowTransferHandler(this));

		// TODO
		getTableHeader().setBackground(ColorUtils.TITLE_BAR_COLOR);

		getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent event) {

				// TODO? Can be called many times!
				updateCellButtons();

				if (!event.getValueIsAdjusting()) {
					fireTableChanged();
				}
			}
		});

		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				updateCellButtons();
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				lastScroll = System.currentTimeMillis();
			}
		});

		getTableHeader().addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					if (columnHidingAllowed && getModel().getColumnCount() > 0) {

						JPopupMenu popupMenu = new JPopupMenu();

						for (int i = 0; i < getModel().getColumnCount(); i++) {

							final int modelColumnIndex = i;
							final boolean hidden = hiddenColumns.containsKey(modelColumnIndex);

							final JCheckBoxMenuItem item = new JCheckBoxMenuItem(getModel().getColumnName(i));
							item.setSelected(!hidden);
							item.addActionListener(new ActionListener() {

								@Override
								public void actionPerformed(ActionEvent e) {
									if (hidden) {
										showColumn(modelColumnIndex);
									} else {
										hideColumn(modelColumnIndex);
									}
								}
							});
							popupMenu.add(item);

						}

						popupMenu.show(e.getComponent(), e.getX(), e.getY());

					}
				}
			}
		});

		getColumnModel().addColumnModelListener(new TableColumnModelListener() {

			@Override
			public void columnAdded(TableColumnModelEvent e) {
				updateCellButtons();
			}

			@Override
			public void columnMarginChanged(ChangeEvent e) {
				updateCellButtons();
			}

			@Override
			public void columnMoved(TableColumnModelEvent e) {
				updateCellButtons();
			}

			@Override
			public void columnRemoved(TableColumnModelEvent e) {
				updateCellButtons();
			}

			@Override
			public void columnSelectionChanged(ListSelectionEvent e) {
				updateCellButtons();
			}
		});

		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() > 1 && getSelectedColumnCount() == 1) {
					int modelColumnIndex = convertColumnIndexToModel(getSelectedColumn());
					if (cellButtons.containsKey(modelColumnIndex)) {
						cellButtons.get(modelColumnIndex).doClick();
					}
				}
			}
		});

		addKeyListener(keyListener);

	}

	@Override
	public void setModel(TableModel model) {

		TableModel oldModel = getModel();
		if (oldModel != null) {
			oldModel.removeTableModelListener(modelListener);
		}

		// The model processes it's listeners in reverse order, so add this listener before the table installs the model
		// otherwise the table's rowCount is not up to date when we update our own listeners
		if (model != null) {

			model.addTableModelListener(modelListener);

			super.setModel(model);

		} else {
			super.setModel(new DefaultTableModel()); // TODO?
		}

		if (sorter != null) {
			sorter.removeRowSorterListener(rowSorterListener);
		}

		if (model != null) {

			if (model instanceof DataListTableModel) {
				((DataListTableModel) model).setUndoManager(undoManager);
			}

			sorter = new TableRowSorter<TableModel>(model) {

				@Override
				public void toggleSortOrder(int column) {
					final List<? extends SortKey> sortKeys = getSortKeys();
					if (sortKeys.size() > 0) {
						if (sortKeys.get(0).getSortOrder() == SortOrder.DESCENDING) {
							setSortKeys(null);
							return;
						}
					}
					super.toggleSortOrder(column);
				}
			};
			sorter.addRowSorterListener(rowSorterListener);
			setRowSorter(sorter);

		} else {
			setRowSorter(null);
		}

	}

	@Override
	public void updateUI() {
		setUI(new TableUI());
		getTableHeader().setBackground(ColorUtils.TITLE_BAR_COLOR);
	}

	// updateUI() is called before class members are initialized, so we cannot use a flag
	// to control the UI creation, if a class want's to use the default UI is should
	// override updateUI() and call this method to use the original UI.
	public void superUpdateUI() {
		super.updateUI();
	}

	public void addTableListener(TableListener listener) {
		listeners.add(listener);
	}

	public void removeTableListener(TableListener listener) {
		listeners.remove(listener);
	}

	public void fireTableWillChange() {
		if (!reloading) {
			for (TableListener listener : listeners) {
				listener.tableWillChange();
			}
		}
	}

	public void fireTableChanged() {
		if (!reloading) {
			for (TableListener listener : listeners) {
				listener.tableChanged();
			}
		}
	}

	public void setPreferences(Preferences preferences, String preferencesKey) {
		this.preferences = preferences;
		this.preferencesKey = preferencesKey;
		loadColumnSettingsFromPreferences();
	}

	public boolean isColumnHidingAllowed() {
		return columnHidingAllowed;
	}

	public void setColumnHidingAllowed(boolean columnHidingAllowed) {
		this.columnHidingAllowed = columnHidingAllowed;
	}

	public void hideColumn(int modelColumnIndex) {
		if (columnHidingAllowed) {
			int columnViewIndex = convertColumnIndexToView(modelColumnIndex);
			if (columnViewIndex >= 0) {
				TableColumn column = getColumnModel().getColumn(columnViewIndex);
				removeColumn(column);
				hiddenColumns.put(modelColumnIndex, column);
			}
			storeColumnSettingsToPreferences();
		}
	}

	public void showColumn(int modelColumnIndex) {
		if (hiddenColumns.containsKey(modelColumnIndex)) {
			addColumn(hiddenColumns.get(modelColumnIndex));
			hiddenColumns.remove(modelColumnIndex);
			for (int i = 0; i < getColumnCount(); i++) {
				if (convertColumnIndexToModel(i) > modelColumnIndex) {
					getColumnModel().moveColumn(getColumnCount() - 1, i);
					break;
				}
			}
		}
		storeColumnSettingsToPreferences();
	}

	private void loadColumnSettingsFromPreferences() {
		if (preferences != null && preferencesKey != null && preferencesKey.length() > 0) {
			loadingColumnSettings = true;
			for (int i = getModel().getColumnCount() - 1; i >= 0; i--) {
				if (preferences.getBoolean(preferencesKey + "HideColumn" + i, false)) { //$NON-NLS-1$
					hideColumn(i);
				}
			}
			loadingColumnSettings = false;
		}
	}

	private void storeColumnSettingsToPreferences() {
		if (!loadingColumnSettings && preferences != null && preferencesKey != null && preferencesKey.length() > 0) {
			for (int i = 0; i < getModel().getColumnCount(); i++) {
				preferences.putBoolean(preferencesKey + "HideColumn" + i, hiddenColumns.containsKey(i)); //$NON-NLS-1$
			}
		}
	}

	public HashMap<Integer, TableColumn> getHiddenColumns() {
		return hiddenColumns;
	}

	public boolean isAlternatingBackgroundEnabled() {
		return alternatingBackgroundEnabled;
	}

	public void setAlternatingBackgroundEnabled(boolean alternatingBackgroundEnabled) {
		this.alternatingBackgroundEnabled = alternatingBackgroundEnabled;
	}

	@Override
	public Color getForeground() {
		return isEnabled() ? super.getForeground() : DISABLED_FOREGROUND;
	}

	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {

		JComponent comp = (JComponent) super.prepareRenderer(renderer, row, column);

		// comp.setFont(getFont());

		if (isRowSelected(row)) {
			comp.setBackground(SELECTION_BACKGROUND);
			comp.setForeground(SELECTION_FOREGROUND);
			if (getCellSelectionEnabled() && getSelectedRowCount() == 1 && getSelectedColumnCount() == 1 && getSelectedColumn() == column) {
				comp.setBackground(FOCUSED_CELL_BACKGROUND);
			}
		} else if (alternatingBackgroundEnabled) {
			comp.setForeground(isEnabled() ? DEFAULT_FOREGROUND : DISABLED_FOREGROUND);
			comp.setBackground(row % 2 == 0 ? getBackground() : TableUI.EVEN_ROW_COLOR);
		}

		if (getSelectedRowCount() == 1 && row == getSelectedRow() && cellButtons.containsKey(convertColumnIndexToModel(column))) {
			comp.setBorder(CELL_BUTTON_CELL_BORDER);
		} else {
			comp.setBorder(DEFAULT_CELL_BORDER);
		}

		return comp;

	}

	@Override
	public Component prepareEditor(TableCellEditor editor, int row, int column) {

		JComponent comp = (JComponent) super.prepareEditor(editor, row, column);
		if (comp != null) {

			comp.setFont(getFont());

			if (alternatingBackgroundEnabled) {
				comp.setForeground(isEnabled() ? DEFAULT_FOREGROUND : DISABLED_FOREGROUND);
				comp.setBackground(row % 2 == 0 ? getBackground() : TableUI.EVEN_ROW_COLOR);
			}

			/*
			Border border = comp.getBorder();
			if (border != null) {
				if (!(border instanceof CompoundBorder)) {
					comp.setBorder(BorderFactory.createCompoundBorder(border, DEFAULT_CELL_BORDER));
				}
			} else {
				comp.setBorder(DEFAULT_CELL_BORDER);
			}
			*/
			comp.setBorder(DEFAULT_CELL_EDITOR_BORDER);

		}

		return comp;

	}

	public TableRowSorter<TableModel> getSorter() {
		return sorter;
	}

	public void setDateColumn1ForFilter(int dateColumnForFilter) {
		this.dateColumn1ForFilter = dateColumnForFilter;
	}

	public void setDateColumn2ForFilter(int dateColumnForFilter) {
		this.dateColumn2ForFilter = dateColumnForFilter;
	}

	public void setBaseFilter(RowFilter<Object, Object> baseFilter) {
		this.baseFilter = baseFilter;
	}

	@Override
	public void setFilter(String filterText, Date from, Date to) {

		try {

			// TODO?
			int[] indices = new int[getColumnCount()];
			for (int i = 0; i < indices.length; i++) {
				indices[i] = i;
			}

			RowFilter<Object, Object> textFilter = RowFilter.regexFilter("(?i)" + filterText, indices); //$NON-NLS-1$

			boolean datesFiltered = false;

			if (dateColumn1ForFilter >= 0 && from != null && to != null) {

				from.setTime(from.getTime() - 1);
				to.setTime(DateUtils.getDayAfterInMillis(to.getTime()));

				if (dateColumn2ForFilter >= 0) {

					List<RowFilter<Object, Object>> filters = new ArrayList<RowFilter<Object, Object>>(2);
					filters.add(textFilter);
					if (baseFilter != null) {
						filters.add(baseFilter);
					}

					List<RowFilter<Object, Object>> dateFilters = new ArrayList<RowFilter<Object, Object>>(2);
					dateFilters.add(RowFilter.dateFilter(RowFilter.ComparisonType.AFTER, from, dateColumn1ForFilter));
					dateFilters.add(RowFilter.dateFilter(RowFilter.ComparisonType.BEFORE, to, dateColumn1ForFilter));
					RowFilter<Object, Object> dateFilter1 = RowFilter.andFilter(dateFilters);

					dateFilters = new ArrayList<RowFilter<Object, Object>>(2);
					dateFilters.add(RowFilter.dateFilter(RowFilter.ComparisonType.AFTER, from, dateColumn2ForFilter));
					dateFilters.add(RowFilter.dateFilter(RowFilter.ComparisonType.BEFORE, to, dateColumn2ForFilter));
					RowFilter<Object, Object> dateFilter2 = RowFilter.andFilter(dateFilters);

					dateFilters = new ArrayList<RowFilter<Object, Object>>(2);
					dateFilters.add(dateFilter1);
					dateFilters.add(dateFilter2);
					filters.add(RowFilter.orFilter(dateFilters));

					RowFilter<Object, Object> filter = RowFilter.andFilter(filters);
					sorter.setRowFilter(filter);

				} else {

					List<RowFilter<Object, Object>> filters = new ArrayList<RowFilter<Object, Object>>(3);
					filters.add(textFilter);
					if (baseFilter != null) {
						filters.add(baseFilter);
					}
					filters.add(RowFilter.dateFilter(RowFilter.ComparisonType.AFTER, from, dateColumn1ForFilter));
					filters.add(RowFilter.dateFilter(RowFilter.ComparisonType.BEFORE, to, dateColumn1ForFilter));
					RowFilter<Object, Object> filter = RowFilter.andFilter(filters);
					sorter.setRowFilter(filter);

				}

				datesFiltered = true;

			}

			if (!datesFiltered) {
				if (baseFilter != null) {
					List<RowFilter<Object, Object>> filters = new ArrayList<RowFilter<Object, Object>>(3);
					filters.add(textFilter);
					filters.add(baseFilter);
					RowFilter<Object, Object> filter = RowFilter.andFilter(filters);
					sorter.setRowFilter(filter);
				} else {
					sorter.setRowFilter(textFilter);
				}
			}

		} catch (PatternSyntaxException e) {
			// TODO: How to handle this exception?
			// e.printStackTrace();
			return;
		}

	}

	@Override
	public int getFilterResultCount() {
		return getRowCount();
	}

	public void createCellButon(final int columnIndex, final ActionListener actionListener) {

		JButton button = new JButton();
		cellButtons.put(columnIndex, button);
		button.addKeyListener(keyListener);
		button.setIcon(Icons.dotsHorizontal);
		button.setOpaque(false);
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				stopEditing();
				int row = getSelectedRow();
				actionListener.actionPerformed(e);

				if (getModel() != null && getModel() instanceof DataListTableModel) {
					((DataListTableModel) getModel()).setCellButtonUpdate(true);
					setValueAt(getValueAt(row, columnIndex), row, columnIndex);
					((DataListTableModel) getModel()).setCellButtonUpdate(false);
				} else {
					setValueAt(getValueAt(row, columnIndex), row, columnIndex);
				}

			}
		});

	}

	public void reload() {
		reloading = true;
		clearSelection();
		if (getModel() != null && getModel() instanceof AbstractTableModel) {
			((AbstractTableModel) getModel()).fireTableDataChanged();
		}
		reloading = false;
		fireTableChanged();
	}

	public void stopEditing() {
		if (getCellEditor() != null) {
			getCellEditor().stopCellEditing();
		}
	}

	public void installUpDownKeyBindings(JComponent component) {

		component.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				selectPreviousRow();
			}
		});

		component.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				selectNextRow();
			}
		});

	}

	public void selectNextRow() {

		int row = getSelectedRow() + 1;
		if (row >= getRowCount()) {
			row = 0;
		}

		if (row >= 0 && row < getRowCount()) {
			getSelectionModel().setSelectionInterval(row, row);
			scrollToSelection();
		}

	}

	public void selectPreviousRow() {

		int row = getSelectedRow() - 1;
		if (row < 0) {
			row = getRowCount() - 1;
		}

		if (row >= 0 && row < getRowCount()) {
			getSelectionModel().setSelectionInterval(row, row);
			scrollToSelection();
		}

	}

	public void scrollToSelection() {

		int selectedRow = getSelectedRow();
		if (selectedRow >= 0) {

			Rectangle rect = getCellRect(selectedRow, 0, true);
			if (rect != null) {
				scrollRectToVisible(rect);
			}

		}

	}

	private void updateCellButtons() {

		for (Component component : getComponents()) {
			if (cellButtons.containsValue(component)) {
				remove(component);
			}
		}

		if (getSelectedRowCount() == 1) {
			for (int i = 0; i < getModel().getColumnCount(); i++) {
				int viewIndex = convertColumnIndexToView(i);
				if (viewIndex >= 0 && cellButtons.containsKey(i)) {

					JButton cellButton = cellButtons.get(i);

					Rectangle bounds = getCellRect(getSelectedRow(), viewIndex, false);
					cellButton.setBounds(bounds.x + bounds.width - 24, bounds.y + 1, 23, bounds.height - 2);

					add(cellButton);

				}
			}
		}

	}

	@Override
	public String getToolTipText(MouseEvent e) {

		// TODO: During scrolling the tool-tip is shown in weird locations, this is a bit of a hack to fix that
		if (System.currentTimeMillis() > lastScroll + MINIMUM_TOOLTIP_DELAY_AFTER_SCROLL) {

			int row = rowAtPoint(e.getPoint());
			int col = columnAtPoint(e.getPoint());

			Object value = null;
			if (row >= 0 && col >= 0) {
				value = getValueAt(row, col);
			}

			if (value == null || value.toString().length() == 0) {
				return null;
			} else {
				// TODO: Dates are shown in wrong time-zone
				// TODO: Use text from renderer
				return value.toString();
			}

		} else {
			return null;
		}

	}

	public UndoManager getUndoManager() {
		return undoManager;
	}

	public void setUndoManager(UndoManager undoManager) {
		this.undoManager = undoManager;
		if (getModel() != null && getModel() instanceof DataListTableModel) {
			((DataListTableModel) getModel()).setUndoManager(undoManager);
		}
	}

	public void createMenu(JMenu menu) {

		JMenuItem selectAllItem = new JMenuItem(Resources.getString("SelectAll")); //$NON-NLS-1$
		selectAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, OsUtils.CTRL_OR_CMD_MASK));
		selectAllItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				selectAll();
			}
		});
		menu.add(selectAllItem);

		JMenuItem clearSelectionItem = new JMenuItem(Resources.getString("ClearSelection")); //$NON-NLS-1$
		clearSelectionItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, OsUtils.CTRL_OR_CMD_MASK | OsUtils.SHIFT_MASK));
		clearSelectionItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				stopEditing();
				clearSelection();
			}
		});
		menu.add(clearSelectionItem);

	}

	@Override
	public Printable getPrintable(PrintMode printMode, MessageFormat headerFormat, MessageFormat footerFormat) {
		return new TablePrintable(this, printMode, headerFormat, footerFormat);
	}

	public class TableRowTransferHandler extends TransferHandler {

		private final DataFlavor localObjectFlavor = new ActivationDataFlavor(ArrayList.class, DataFlavor.javaJVMLocalObjectMimeType, "Integer Row Index"); //$NON-NLS-1$
		private final Table table;
		private final Reorderable tableModel;

		public TableRowTransferHandler(Table table) {
			this.table = table;
			if (table.getModel() instanceof Reorderable) {
				this.tableModel = (Reorderable) table.getModel();
			} else {
				this.tableModel = null;
			}
		}

		@Override
		protected Transferable createTransferable(JComponent c) {
			assert (c == table);
			// return new DataHandler(new Integer(table.getSelectedRow()), localObjectFlavor.getMimeType());

			ArrayList<Integer> selectedRows = new ArrayList<Integer>();
			for (int row : table.getSelectedRows()) {
				selectedRows.add(row);
			}

			return new DataHandler(selectedRows, localObjectFlavor.getMimeType());
		}

		@Override
		public boolean canImport(TransferHandler.TransferSupport info) {

			// TODO: Reordering of multiple rows is not yet working
			if (table.getSelectedRowCount() != 1) {
				return false;
			}

			boolean canImport = tableModel != null && info.getComponent() == table && info.isDrop() && info.isDataFlavorSupported(localObjectFlavor);
			table.setCursor(canImport ? DragSource.DefaultMoveDrop : DragSource.DefaultMoveNoDrop);
			return canImport;

		}

		@Override
		public int getSourceActions(JComponent c) {
			return TransferHandler.COPY_OR_MOVE;
		}

		@Override
		public boolean importData(TransferHandler.TransferSupport info) {

			boolean dataImported = false;

			JTable.DropLocation dropLocation = (JTable.DropLocation) info.getDropLocation();

			int toIndexView = dropLocation.getRow();
			int toIndexModel;
			int max = table.getRowCount();
			if (toIndexView < 0) {
				toIndexView = 0;
				toIndexModel = convertRowIndexToModel(toIndexView);
			} else if (toIndexView >= max) {
				toIndexModel = convertRowIndexToModel(toIndexView - 1) + 1;
			} else {
				toIndexModel = convertRowIndexToModel(toIndexView);
			}

			table.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

			try {

				@SuppressWarnings("unchecked")
				ArrayList<Integer> selectedRows = (ArrayList<Integer>) info.getTransferable().getTransferData(localObjectFlavor);

				/*
				 * proefje.. Collections.sort(selectedRows, new Comparator<Integer>() {
				 * 
				 * @Override public int compare(Integer o1, Integer o2) { return Integer.compare(o2, o1); } });
				 */

				for (int fromIndexModel : selectedRows) {

					fromIndexModel = convertRowIndexToModel(fromIndexModel);

					// Integer fromIndex = (Integer)info.getTransferable().getTransferData(localObjectFlavor);
					if (fromIndexModel > -1 && fromIndexModel != toIndexModel) {

						ReorderUndoable undoable = new ReorderUndoable(table, tableModel, fromIndexModel, toIndexModel);

						// table.getSelectionModel().removeSelectionInterval(fromIndex, fromIndex);
						tableModel.reorder(fromIndexModel, toIndexModel);

						sorter.sort();

						if (toIndexModel > fromIndexModel) {
							toIndexModel--;
						}
						// table.getSelectionModel().addSelectionInterval(toIndex, toIndex);
						toIndexView = convertRowIndexToView(toIndexModel);
						table.getSelectionModel().setSelectionInterval(toIndexView, toIndexView);

						if (undoManager != null) {
							undoManager.addEdit(undoable);
						}

						dataImported = true;

					}

				}

			} catch (Exception e) {
				// TODO: Handle exception
				e.printStackTrace();
			}

			return dataImported;

		}

		@Override
		protected void exportDone(JComponent c, Transferable t, int act) {
			table.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

	}

	public static interface ModelListener extends TableModelListener {

		public void tableWillChange();

	}

}
