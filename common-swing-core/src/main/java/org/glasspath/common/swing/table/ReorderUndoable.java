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

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import org.glasspath.common.swing.resources.CommonResources;

public class ReorderUndoable implements UndoableEdit {

	private final Table table;
	private final Reorderable reorderable;
	private final int fromIndex;
	private final int toIndex;

	public ReorderUndoable(Table table, Reorderable reorderable, int fromIndex, int toIndex) {
		this.table = table;
		this.reorderable = reorderable;
		this.fromIndex = fromIndex;
		this.toIndex = toIndex;
	}

	@Override
	public String getPresentationName() {
		return CommonResources.getString("MoveRow"); //$NON-NLS-1$
	}

	@Override
	public String getRedoPresentationName() {
		return CommonResources.getString("RedoMoveRow"); //$NON-NLS-1$
	}

	@Override
	public String getUndoPresentationName() {
		return CommonResources.getString("UndoMoveRow"); //$NON-NLS-1$
	}

	@Override
	public boolean addEdit(UndoableEdit anEdit) {
		return false;
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

	}

	@Override
	public boolean isSignificant() {
		return true;
	}

	@Override
	public void redo() throws CannotRedoException {

		reorderable.reorder(fromIndex, toIndex);

		int toIndexView;
		if (toIndex > fromIndex) {
			toIndexView = table.convertRowIndexToView(toIndex - 1);
		} else {
			toIndexView = table.convertRowIndexToView(toIndex);
		}
		table.getSelectionModel().setSelectionInterval(toIndexView, toIndexView);

	}

	@Override
	public boolean replaceEdit(UndoableEdit anEdit) {
		return false;
	}

	@Override
	public void undo() throws CannotUndoException {

		if (toIndex > fromIndex) {
			reorderable.reorder(toIndex - 1, fromIndex);
		} else {
			reorderable.reorder(toIndex, fromIndex + 1);
		}

		int fromIndexView = table.convertRowIndexToView(fromIndex);
		table.getSelectionModel().setSelectionInterval(fromIndexView, fromIndexView);

	}

}
