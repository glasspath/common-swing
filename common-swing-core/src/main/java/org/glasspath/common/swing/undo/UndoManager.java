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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import org.glasspath.common.icons.Icons;
import org.glasspath.common.os.OsUtils;
import org.glasspath.common.swing.ApplicationContext;
import org.glasspath.common.swing.DataListener;

public class UndoManager extends DefaultUndoManager {

	private final ApplicationContext context;
	private AbstractAction undoAction;
	private AbstractAction redoAction;

	public UndoManager(ApplicationContext context) {

		this.context = context;

		context.addDataListener(new DataListener() {

			@Override
			public void newDataLoaded() {
				discardAllEdits();
				updateActions();
			}

			@Override
			public void finishEditing() {

			}
		});

		setLimit(50); // TODO: Make limit configurable in preferences dialog

		undoAction = new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				undo();
				context.undoPerformed();
			}
		};
		undoAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Z, OsUtils.CTRL_OR_CMD_MASK));
		undoAction.putValue(Action.SMALL_ICON, Icons.undo);

		redoAction = new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				redo();
				context.redoPerformed();
			}
		};
		redoAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Y, OsUtils.CTRL_OR_CMD_MASK));
		redoAction.putValue(Action.SMALL_ICON, Icons.redo);

		updateActions();

	}

	public AbstractAction getUndoAction() {
		return undoAction;
	}

	public void setUndoAction(AbstractAction undoAction) {
		this.undoAction = undoAction;
	}

	public AbstractAction getRedoAction() {
		return redoAction;
	}

	public void setRedoAction(AbstractAction redoAction) {
		this.redoAction = redoAction;
	}

	private void updateActions() {

		undoAction.setEnabled(canUndo());
		undoAction.putValue(Action.NAME, getUndoPresentationName());
		undoAction.putValue(Action.SHORT_DESCRIPTION, getUndoPresentationName());

		redoAction.setEnabled(canRedo());
		redoAction.putValue(Action.NAME, getRedoPresentationName());
		redoAction.putValue(Action.SHORT_DESCRIPTION, getRedoPresentationName());

	}

	public UndoableEdit getNextUndoableEdit() {
		return editToBeUndone();
	}

	@Override
	public synchronized boolean addEdit(UndoableEdit anEdit) {
		boolean result = super.addEdit(anEdit);
		updateActions();
		context.setSomethingChanged(true);
		return result;
	}

	@Override
	public synchronized void undo() throws CannotUndoException {
		super.undo();
		updateActions();
		context.setSomethingChanged(true);
	}

	@Override
	public synchronized void redo() throws CannotRedoException {
		super.redo();
		updateActions();
		context.setSomethingChanged(true);
	}

}
