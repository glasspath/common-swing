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
package org.glasspath.common.swing.tools;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JToolBar;

import org.glasspath.common.swing.FrameContext;
import org.glasspath.common.swing.color.ColorUtils;

public abstract class AbstractTools<T extends FrameContext> {

	protected final T context;
	protected final JMenu menu;
	protected final JToolBar toolBar;

	public AbstractTools(T context, String text) {

		this.context = context;

		menu = new JMenu(text);

		toolBar = new JToolBar(text) {

			@Override
			public void updateUI() {
				super.updateUI();
				setBackground(ColorUtils.TITLE_BAR_COLOR);
			}
		};
		toolBar.setOpaque(false);
		toolBar.setRollover(true);

	}

	public JMenu getMenu() {
		return menu;
	}

	public JToolBar getToolBar() {
		return toolBar;
	}

	public boolean isToolBarVisible() {
		return toolBar.isVisible();
	}

	public void setToolBarVisible(boolean visible) {
		setToolBarVisible(visible, true);
	}

	public void setToolBarVisible(boolean visible, boolean revalidateFrame) {

		toolBar.setVisible(visible);

		if (revalidateFrame) {
			revalidateFrame();
		}

	}

	protected void revalidateFrame() {

		if (context != null) {

			JFrame frame = context.getFrame();

			frame.invalidate(); // TODO: Remove?
			frame.revalidate();
			frame.repaint();

		}

	}

}
