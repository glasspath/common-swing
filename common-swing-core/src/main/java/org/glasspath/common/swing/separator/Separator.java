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
package org.glasspath.common.swing.separator;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JSeparator;

public class Separator extends JSeparator {

	public Separator() {

	}

	@Override
	public void paint(Graphics g) {
		// super.paint(g);

		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(getForeground());

		float width = 1.0F;
		if (g2d.getTransform().getScaleX() >= 1.5) {
			width = 0.5F;
		}

		if (getOrientation() == JSeparator.VERTICAL) {
			g2d.fill(new Rectangle2D.Float(0, 0, width, getHeight()));
		} else {
			g2d.fill(new Rectangle2D.Float(0, 0, getWidth(), width));
		}

	}

}
