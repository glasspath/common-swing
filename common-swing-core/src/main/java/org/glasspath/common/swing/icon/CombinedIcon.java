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
package org.glasspath.common.swing.icon;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

public class CombinedIcon implements Icon {

	private Icon first;
	private Icon second;

	public CombinedIcon(Icon first, Icon second) {
		this.first = first;
		this.second = second;
	}

	@Override
	public int getIconHeight() {
		return first.getIconHeight();
	}

	@Override
	public int getIconWidth() {
		return first.getIconHeight();
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		first.paintIcon(c, g, x, y);
		second.paintIcon(c, g, x, y);
	}

}
