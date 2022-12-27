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
package org.glasspath.common.swing.splitpane;

import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JSplitPane;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class InvisibleSplitPane extends JSplitPane {

	private int dividerDragSize = 9;
	private int dividerDragOffset = 4;

	public InvisibleSplitPane() {
		setDividerSize(0);
		setContinuousLayout(true);
	}

	@Override
	public void doLayout() {
		super.doLayout();

		BasicSplitPaneDivider divider = ((BasicSplitPaneUI) getUI()).getDivider();
		Rectangle bounds = divider.getBounds();

		if (orientation == HORIZONTAL_SPLIT) {
			bounds.x -= dividerDragOffset;
			bounds.width = dividerDragSize;
		} else {
			bounds.y -= dividerDragOffset;
			bounds.height = dividerDragSize;
		}

		divider.setBounds(bounds);

	}

	@Override
	public void updateUI() {
		setUI(new SplitPaneWithZeroSizeDividerUI());
		revalidate();
	}

	private class SplitPaneWithZeroSizeDividerUI extends BasicSplitPaneUI {

		@Override
		public BasicSplitPaneDivider createDefaultDivider() {
			return new ZeroSizeDivider(this);
		}

	}

	private class ZeroSizeDivider extends BasicSplitPaneDivider {

		public ZeroSizeDivider(BasicSplitPaneUI ui) {
			super(ui);
		}

		@Override
		public void setBorder(Border border) {

		}

		@Override
		public void paint(Graphics g) {

		}

		@Override
		protected void dragDividerTo(int location) {
			super.dragDividerTo(location + dividerDragOffset);
		}

		@Override
		protected void finishDraggingTo(int location) {
			super.finishDraggingTo(location + dividerDragOffset);
		}

	}

}