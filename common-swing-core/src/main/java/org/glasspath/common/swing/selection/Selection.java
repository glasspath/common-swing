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
package org.glasspath.common.swing.selection;

import java.util.ArrayList;
import java.util.Collection;

public class Selection<E> extends ArrayList<E> {

	private final ArrayList<SelectionListener> listeners = new ArrayList<SelectionListener>();
	
	public void select(E e) {
		if (e != null && !contains(e)) {
			add(e);
			fireSelectionChanged();
		}
	}

	public void deselect(E e) {
		if (contains(e)) {
			remove(e);
			fireSelectionChanged();
		}
	}

	public void selectAll(Collection<? extends E> c) {
		super.addAll(c);
		fireSelectionChanged();
	}

	public void deselectAll() {
		clear();
		fireSelectionChanged();
	}

	public void addSelectionListener(SelectionListener listener) {
		listeners.add(listener);
	}

	public void removeSelectionListener(SelectionListener listener) {
		listeners.remove(listener);
	}

	public void fireSelectionChanged() {
		for (SelectionListener listener : listeners) {
			listener.selectionChanged();
		}
	}

	public void dispose() {
		clear();
		listeners.clear();
	}

}
