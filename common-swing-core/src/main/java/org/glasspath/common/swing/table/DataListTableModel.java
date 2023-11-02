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

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import org.glasspath.common.swing.resources.Resources;
import org.glasspath.common.swing.table.Table.ModelListener;
import org.glasspath.common.swing.undo.UndoManager;

public abstract class DataListTableModel extends AbstractTableModel implements Reorderable {

	private UndoManager undoManager = null;
	private boolean undoRedoing = false;
	private boolean cellButtonUpdate = false;
	private final List<ModelListener> listeners = new ArrayList<>();

	public DataListTableModel() {

	}

	public UndoManager getUndoManager() {
		return undoManager;
	}

	public void setUndoManager(UndoManager undoManager) {
		this.undoManager = undoManager;
	}

	public boolean isUndoRedoing() {
		return undoRedoing;
	}

	public void setUndoRedoing(boolean undoRedoing) {
		this.undoRedoing = undoRedoing;
	}

	public boolean isCellButtonUpdate() {
		return cellButtonUpdate;
	}

	public void setCellButtonUpdate(boolean cellButtonUpdate) {
		this.cellButtonUpdate = cellButtonUpdate;
	}

	@Override
	public void addTableModelListener(TableModelListener listener) {
		super.addTableModelListener(listener);
		if (listener instanceof ModelListener) {
			listeners.add((ModelListener) listener);
		}
	}

	@Override
	public void removeTableModelListener(TableModelListener listener) {
		super.removeTableModelListener(listener);
		if (listener instanceof ModelListener) {
			listeners.remove((ModelListener) listener);
		}
	}

	protected void fireTabelWillChange() {
		for (ModelListener listener : listeners) {
			listener.tableWillChange();
		}
	}

	@Override
	public void setValueAt(Object newValue, int rowIndex, int columnIndex) {
		setValueAt(newValue, rowIndex, columnIndex, null);
	}

	public void setValueAt(Object newValue, int rowIndex, int columnIndex, DataListTableModel tableModel) {
		if (!undoRedoing) {
			Object oldValue = getValueAt(rowIndex, columnIndex);
			if (oldValue != newValue && (oldValue == null || !oldValue.equals(newValue))) {
				undoManager.addEdit(new SetValueUndoable(rowIndex, columnIndex, oldValue, newValue, tableModel));
			}
		}
	}

	@Override
	public void fireTableCellUpdated(int row, int column) {
		setSomethingChanged(true);
		super.fireTableCellUpdated(row, column);
	}

	public abstract void setSomethingChanged(boolean somethingChanged);

	public abstract void dispose();

	protected class SetValueUndoable implements UndoableEdit {

		public final int rowIndex;
		public final int columnIndex;
		private final Object oldValue;
		private final Object newValue;
		private final DataListTableModel tableModel;

		private final ArrayList<ResultingUndoable<?>> resultingUndoables = new ArrayList<ResultingUndoable<?>>();

		private SetValueUndoable(int rowIndex, int columnIndex, Object oldValue, Object newValue, DataListTableModel tableModel) {
			this.rowIndex = rowIndex;
			this.columnIndex = columnIndex;
			this.oldValue = oldValue;
			this.newValue = newValue;
			this.tableModel = tableModel;
		}

		@Override
		public String getPresentationName() {
			return Resources.getString("ChangeField"); //$NON-NLS-1$
		}

		@Override
		public String getRedoPresentationName() {
			return Resources.getString("RedoChangeField"); //$NON-NLS-1$
		}

		@Override
		public String getUndoPresentationName() {
			return Resources.getString("UndoChangeField"); //$NON-NLS-1$
		}

		@Override
		public boolean addEdit(UndoableEdit anEdit) {
			return false;
		}

		public void addResultingUndoable(ResultingUndoable<?> undoable) {
			if (!isUndoRedoing() && (tableModel == null || !tableModel.isUndoRedoing())) {
				resultingUndoables.add(undoable);
			}
		}

		@Override
		public boolean canRedo() {
			return true;
		}

		@Override
		public boolean canUndo() {
			return true;
		}

		@Override
		public void die() {
			if (tableModel != null) {
				tableModel.dispose();
			}
		}

		@Override
		public boolean isSignificant() {
			return true;
		}

		@Override
		public void redo() throws CannotRedoException {
			setUndoRedoing(true);
			if (tableModel != null) {
				tableModel.setUndoRedoing(true);
				tableModel.setValueAt(newValue, rowIndex, columnIndex);
				tableModel.setUndoRedoing(false);
			} else {
				setValueAt(newValue, rowIndex, columnIndex);
			}
			setUndoRedoing(false);
		}

		@Override
		public boolean replaceEdit(UndoableEdit anEdit) {
			return false;
		}

		@Override
		public void undo() throws CannotUndoException {
			setUndoRedoing(true);
			if (tableModel != null) {
				tableModel.setUndoRedoing(true);
				tableModel.setValueAt(oldValue, rowIndex, columnIndex);
			} else {
				setValueAt(oldValue, rowIndex, columnIndex);
			}
			for (ResultingUndoable<?> undoable : resultingUndoables) {
				undoable.undo();
			}
			if (tableModel != null) {
				tableModel.setUndoRedoing(false);
			}
			setUndoRedoing(false);
		}

	}

	protected abstract class ResultingUndoable<E> {

		private final E oldValue;

		public ResultingUndoable(E oldValue) {
			this.oldValue = oldValue;
		}

		public E getOldValue() {
			return oldValue;
		}

		public abstract void undo();

	}

}
