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
package org.glasspath.common.swing.file.manager;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.glasspath.common.os.OsUtils;
import org.glasspath.common.swing.table.Table;

public abstract class FilesTablePanel extends JPanel {

	protected final List<File> files;
	protected final Table filesTable;
	private final List<ActionListener> actionListeners = new ArrayList<>();

	public FilesTablePanel(List<File> files) {

		this.files = files;

		setLayout(new BorderLayout());

		filesTable = new Table(new FilesTableModel()) {

			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				JLabel label = (JLabel) super.prepareRenderer(renderer, row, column);
				if (convertColumnIndexToModel(column) == FilesTableModel.NAME) {
					label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 4));
				}
				return label;
			}
		};
		filesTable.setCellSelectionEnabled(false);
		filesTable.setRowHeight(35);
		filesTable.getColumnModel().getColumn(FilesTableModel.NAME).setCellRenderer(new FileNameCellRenderer());
		filesTable.getColumnModel().getColumn(FilesTableModel.TYPE).setCellRenderer(new FileTypeCellRenderer());
		filesTable.getColumnModel().getColumn(FilesTableModel.NAME).setPreferredWidth(350);
		filesTable.getColumnModel().getColumn(FilesTableModel.TYPE).setPreferredWidth(150);
		filesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				selecionChanged(e);
			}
		});
		filesTable.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() >= 2) {
					fireActionPerformed(new ActionEvent(getSelectedFile(), ActionEvent.ACTION_PERFORMED, null));
				}
			}
		});

		JScrollPane filesTableScrollPane = new JScrollPane(filesTable);
		filesTableScrollPane.setBorder(BorderFactory.createEmptyBorder());
		// filesTableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		add(filesTableScrollPane, BorderLayout.CENTER);

	}

	public void addActionListener(ActionListener listener) {
		actionListeners.add(listener);
	}

	public void removeActionListener(ActionListener listener) {
		actionListeners.add(listener);
	}

	private void fireActionPerformed(ActionEvent e) {
		for (ActionListener listener : actionListeners) {
			listener.actionPerformed(e);
		}
	}

	public File getSelectedFile() {

		if (filesTable.getSelectedRowCount() == 1 && filesTable.getSelectedRow() >= 0) {
			int modelIndex = filesTable.convertRowIndexToModel(filesTable.getSelectedRow());
			if (modelIndex >= 0 && modelIndex < files.size()) {
				return files.get(modelIndex);
			}
		}

		return null;

	}

	protected boolean selectFile(File file) {

		filesTable.clearSelection();

		int index = files.indexOf(file);
		if (index >= 0) {

			int viewIndex = filesTable.convertRowIndexToView(index);
			if (viewIndex >= 0) {

				filesTable.getSelectionModel().addSelectionInterval(viewIndex, viewIndex);

				return true;

			}

		}

		return false;

	}

	public void reload() {
		filesTable.reload();
	}

	protected abstract Icon getFileIcon(File file);

	protected abstract String getFileDescription(File file);

	protected abstract void selecionChanged(ListSelectionEvent e);

	protected class FilesTableModel extends AbstractTableModel {

		public static final int NAME = 0;
		public static final int TYPE = 1;

		public FilesTableModel() {

		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

		@Override
		public int getColumnCount() {
			return 2; // TODO?
		}

		@Override
		public String getColumnName(int column) {

			if (column == NAME) {
				return "Name";
			} else if (column == TYPE) {
				return "Type";
			} else {
				return super.getColumnName(column);
			}

		}

		@Override
		public int getRowCount() {
			return files.size();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return getValueAt(rowIndex, columnIndex, files);
		}

		protected Object getValueAt(int rowIndex, int columnIndex, List<File> fromList) {

			File file = fromList.get(rowIndex);
			Object value = null;

			if (columnIndex == NAME) {
				value = OsUtils.getFileNameWithoutExtension(file);
			} else if (columnIndex == TYPE) {
				value = getFileDescription(file);
			}

			return value;

		}

		@Override
		public void setValueAt(Object value, int rowIndex, int columnIndex) {
			super.setValueAt(value, rowIndex, columnIndex);

			// TODO?

			fireTableCellUpdated(rowIndex, columnIndex);

		}

		@Override
		public Class<?> getColumnClass(int colIndex) {

			if (colIndex == NAME) {
				return String.class;
			} else if (colIndex == TYPE) {
				return String.class;
			} else {
				return super.getColumnClass(colIndex);
			}

		}

	}

	protected class FileNameCellRenderer extends DefaultTableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			setIcon(null);

			if (filesTable.convertColumnIndexToModel(column) == 0 && row >= 0) {

				int modelIndex = filesTable.convertRowIndexToModel(row);
				if (modelIndex >= 0 && modelIndex < files.size()) {

					File file = files.get(row);

					setIcon(getFileIcon(file));

				}

			}

			setFont(getFont().deriveFont(14.0F));

			return this;

		}

	}

	protected class FileTypeCellRenderer extends DefaultTableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			setFont(getFont().deriveFont(14.0F));
			return this;
		}

	}

}
