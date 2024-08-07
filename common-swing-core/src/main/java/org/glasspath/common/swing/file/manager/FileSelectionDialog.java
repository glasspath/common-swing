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
package org.glasspath.common.swing.file.manager;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileFilter;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;

import org.glasspath.common.io.file.FileList;
import org.glasspath.common.swing.FrameContext;
import org.glasspath.common.swing.dialog.DefaultDialog;
import org.glasspath.common.swing.resources.CommonResources;

public abstract class FileSelectionDialog extends DefaultDialog {

	protected final JCheckBox showAllFilesCheckBox;
	protected final FilesTablePanel filesTablePanel;

	protected final FileList fileList;

	public FileSelectionDialog(FrameContext context, FileList fileList, String title) {
		this(context, fileList, null, null, title);
	}

	public FileSelectionDialog(FrameContext context, FileList fileList, FileFilter defaultFileFilter, FileFilter preferredFileFilter, String title) {

		super(context);

		this.fileList = fileList;

		getHeader().setTitle(title != null ? title : CommonResources.getString("SelectFile")); //$NON-NLS-1$
		remove(getHeaderSeparator());
		setTitle(CommonResources.getString("SelectFile")); //$NON-NLS-1$
		setPreferredSize(DIALOG_SIZE_SQUARE);
		setKeyListenerEnabled(false);

		getContentPanel().setLayout(new BorderLayout());
		getContentPanel().setBorder(BorderFactory.createEmptyBorder());

		if (defaultFileFilter != null && preferredFileFilter != null) {

			showAllFilesCheckBox = new JCheckBox(CommonResources.getString("ShowAllFiles")); //$NON-NLS-1$
			showAllFilesCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 7, 2, 0));
			getFooter().add(showAllFilesCheckBox, 0);
			showAllFilesCheckBox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (showAllFilesCheckBox.isSelected()) {
						fileList.setFileFilter(defaultFileFilter);
					} else {
						fileList.setFileFilter(preferredFileFilter);
					}
					filesTablePanel.reload();
				}
			});

			fileList.setFileFilter(preferredFileFilter);

		} else {
			showAllFilesCheckBox = null;
		}

		filesTablePanel = new FilesTablePanel(fileList) {

			@Override
			protected String getFileDescription(File file) {
				return FileSelectionDialog.this.getFileDescription(file);
			}

			@Override
			protected Icon getFileIcon(File file) {
				return FileSelectionDialog.this.getFileIcon(file);
			}

			@Override
			protected void selecionChanged(ListSelectionEvent e) {
				selectionChanged();
			}
		};
		filesTablePanel.filesTable.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() >= 2) {
					submit();
				}
			}
		});
		getContentPanel().add(filesTablePanel, BorderLayout.CENTER);

		selectionChanged();

	}

	@Override
	protected void setContentChanged() {
		// Application content is not changed by file selection dialog
	}

	private void selectionChanged() {

		File selectedFile = getSelectedFile();

		getOkButton().setEnabled(selectedFile != null);

	}

	public File getSelectedFile() {
		return filesTablePanel.getSelectedFile();
	}

	protected abstract Icon getFileIcon(File file);

	protected abstract String getFileDescription(File file);

	@Override
	protected void submit() {
		if (getOkButton().isEnabled()) {
			super.submit();
		}
	}

}
