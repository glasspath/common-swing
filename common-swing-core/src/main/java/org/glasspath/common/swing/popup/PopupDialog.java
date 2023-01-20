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
package org.glasspath.common.swing.popup;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JPopupMenu;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.glasspath.common.os.OsUtils;

public class PopupDialog extends JPopupMenu {

	private final JDialog dialog;
	private final PropertyChangeListener focusListener;

	private boolean popupVisible = false;

	public PopupDialog() {
		this(OsUtils.PLATFORM_WINDOWS);
	}

	public PopupDialog(boolean invokeOnDialog) {

		if (invokeOnDialog) {

			// A JPopupMenu with focusable content causes the underlying
			// window to flicker when the pop-up is shown, so we use a
			// hidden dialog to invoke the pop-up
			dialog = new JDialog();
			dialog.setUndecorated(true);
			dialog.setPreferredSize(new Dimension(1, 1));
			dialog.pack();

		} else {
			dialog = null;
		}

		focusListener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent e) {
				focusChanged(e.getNewValue());
			}
		};

		addPopupMenuListener(new PopupMenuListener() {

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

				// Remove it from the MenuSelectionManager immediately, normally this
				// would close the pop-up, but we override menuSelectionChanged
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						MenuSelectionManager.defaultManager().clearSelectedPath();
						popupDialogShown();
					}
				});

			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						popupDialogHidden();
					}
				});
			}

			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {

			}
		});

	}

	@Override
	public void menuSelectionChanged(boolean isIncluded) {
		// Don't close this pop-up (this allows for example a JComboBox inside
		// this pop-up to show it's drop-down without closing this pop-up)
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		if (visible && !popupVisible) {
			KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("permanentFocusOwner", focusListener); //$NON-NLS-1$
		} else if (!visible && popupVisible) {
			if (dialog != null) {
				dialog.setVisible(false);
			}
			KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener("permanentFocusOwner", focusListener); //$NON-NLS-1$
		}

		popupVisible = visible;

	}

	@Override
	public void show(Component invoker, int x, int y) {

		if (dialog != null) {

			Point location = invoker.getLocationOnScreen();
			location.x += x;
			location.y += y;

			dialog.setLocation(location);
			dialog.setVisible(true);

			super.show(dialog, 0, 0);

		} else {
			super.show(invoker, x, y);
		}

	}

	public void popupDialogShown() {

	}

	public void popupDialogHidden() {

	}

	public boolean isCloseAllowed() {
		return true;
	}

	private void focusChanged(Object focusOwner) {

		if (popupVisible && isCloseAllowed() && focusOwner instanceof Component) {

			Component parent = (Component) focusOwner;

			while (parent != null) {

				if (parent == this) {
					return;
				}

				parent = parent.getParent();

			}

			setVisible(false);

		}

	}

}
