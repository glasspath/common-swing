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
package org.glasspath.common.swing.undo;

import java.util.ArrayList;
import java.util.List;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

public class DefaultUndoManager extends UndoManager implements IUndoManager {

	private final List<UndoManagerListener> listeners = new ArrayList<>();

	public DefaultUndoManager() {

	}

	@Override
	public synchronized boolean addEdit(UndoableEdit edit) {
		boolean result = super.addEdit(edit);

		for (UndoManagerListener listener : listeners) {
			listener.editAdded(edit);
		}

		return result;

	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();

		for (UndoManagerListener listener : listeners) {
			listener.undoPerformed();
		}

	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();

		for (UndoManagerListener listener : listeners) {
			listener.redoPerformed();
		}

	}

	@Override
	public void addListener(UndoManagerListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(UndoManagerListener listener) {
		listeners.remove(listener);
	}

	public static interface UndoManagerListener {

		public void editAdded(UndoableEdit edit);

		public void undoPerformed();

		public void redoPerformed();

	}

}
