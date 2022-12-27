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
package org.glasspath.common.swing.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

public class Panel extends JPanel {

	private int minWidth = 0;
	private int minHeight = 0;

	public Panel() {
		super(new BorderLayout());
	}

	public Panel(int minWidth, int minHeight) {
		super(new BorderLayout());

		this.minWidth = minWidth;
		this.minHeight = minHeight;

	}

	@Override
	public Dimension getPreferredSize() {

		Dimension size = super.getPreferredSize();

		if (size.width < minWidth) {
			size.width = minWidth;
		}

		if (size.height < minHeight) {
			size.height = minHeight;
		}

		return size;

	}

}
