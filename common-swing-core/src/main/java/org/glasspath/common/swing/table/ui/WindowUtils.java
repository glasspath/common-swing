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

import java.awt.Component;
import java.awt.Window;

import javax.swing.SwingUtilities;

/**
 * Utility methods for dealing with {@link Window}s.
 */
public class WindowUtils {

	/**
	 * {@code true} if the given {@link Component}'s has a parent {@link Window}
	 * (i.e. it's not null) and that {@link Window} is currently active
	 * (focused).
	 * 
	 * @param component
	 *            the {@code Component} to check the parent {@code Window}'s
	 *            focus for.
	 * @return {@code true} if the given {@code Component}'s parent
	 *         {@code Window} is currently active.
	 */
	public static boolean isParentWindowFocused(Component component) {
		Window window = SwingUtilities.getWindowAncestor(component);
		return window != null && window.isFocused();
	}

}