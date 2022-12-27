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
package org.glasspath.common.swing.action;

import java.awt.Cursor;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;

public class ActionLabel extends JLabel {

	private final AbstractAction action;
	private final List<ActionListener> actionListeners = new ArrayList<>();

	public ActionLabel() {
		this(null, ""); //$NON-NLS-1$
	}

	public ActionLabel(String text) {
		this(null, text);
	}

	public ActionLabel(AbstractAction action) {
		this(action, (String) action.getValue(Action.SHORT_DESCRIPTION));
	}

	public ActionLabel(AbstractAction action, String text) {
		super(text);

		this.action = action;

		if (action != null) {
			setIcon((Icon) action.getValue(Action.SMALL_ICON));
			actionListeners.add(action);
		}

		setBorder(BorderFactory.createEmptyBorder(5, 7, 5, 0));
		// setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		// setFocusable(true);

		addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				if (isEnabled()) {
					// requestFocusInWindow();
					fireActionPerformed();
				}
			}
		});

		updateCursor();

	}

	@Override
	public boolean isEnabled() {
		if (action != null) {
			return action.isEnabled();
		} else {
			return super.isEnabled();
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		updateCursor();
	}

	public void updateCursor() {
		if (isEnabled()) {
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		} else {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	public void addActionListener(ActionListener listener) {
		actionListeners.add(listener);
	}

	public void removeActionListener(ActionListener listener) {
		actionListeners.remove(listener);
	}

	private void fireActionPerformed() {
		for (ActionListener listener : actionListeners) {
			listener.actionPerformed(null);
		}
	}

}
