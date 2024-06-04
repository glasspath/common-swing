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
package org.glasspath.common.swing.padding;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;

import org.glasspath.common.swing.FrameContext;
import org.glasspath.common.swing.dialog.DefaultDialog;
import org.glasspath.common.swing.resources.CommonResources;

public class PaddingDialog extends DefaultDialog {

	private final PaddingPanel paddingPanel;

	public PaddingDialog(FrameContext context, int top, int right, int bottom, int left) {

		super(context);

		setTitle(CommonResources.getString("Padding")); //$NON-NLS-1$
		setPreferredSize(DIALOG_SIZE_SMALL);

		remove(getHeader());
		remove(getHeaderSeparator());
		getRootPane().setBackground(getContentPanel().getBackground());

		getContentPanel().setLayout(new BorderLayout());
		getContentPanel().setBorder(BorderFactory.createEmptyBorder(5, 25, 5, 25));

		this.paddingPanel = new PaddingPanel(top, right, bottom, left);
		getContentPanel().add(paddingPanel, BorderLayout.CENTER);

		pack();
		setLocationRelativeTo(context.getFrame());

	}

	public PaddingPanel getPaddingPanel() {
		return paddingPanel;
	}

}
