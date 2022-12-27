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
package org.glasspath.common.swing.file;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.glasspath.common.icons.Icons;
import org.glasspath.common.os.OsUtils;
import org.glasspath.common.swing.FrameContext;
import org.glasspath.common.swing.color.ColorUtils;
import org.glasspath.common.swing.dialog.DefaultDialog;
import org.glasspath.common.swing.dialog.DialogUtils;

public class FileNameDialog extends DefaultDialog {

	private final File file;
	private final boolean copy;
	private final File newParentDir;
	private final List<String> linkedFileExtensions;
	private final JTextField fileNameTextField;
	private final Color defaultBackground;

	private File fileWithNewName = null;

	public FileNameDialog(FrameContext context, File file) {
		this(context, file, false);
	}

	public FileNameDialog(FrameContext context, File file, boolean copy) {
		this(context, file, copy, null);
	}

	public FileNameDialog(FrameContext context, File file, boolean copy, File newParentDir) {
		this(context, file, copy, newParentDir, null);
	}

	public FileNameDialog(FrameContext context, File file, boolean copy, File newParentDir, List<String> linkedFileExtensions) {

		super(context);

		this.file = file;
		this.copy = copy;
		this.newParentDir = newParentDir;
		this.linkedFileExtensions = linkedFileExtensions;

		setPreferredSize(new Dimension(500, 204));
		getContentPanel().setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

		if (copy) {
			setTitle("Copy file");
			setIconImage(Icons.contentCopy.getImage());
			getHeader().setIcon(Icons.contentCopyXLarge);
			getHeader().setTitle("Copy file");
		} else {
			setTitle("Rename file");
			setIconImage(Icons.renameBox.getImage());
			getHeader().setIcon(Icons.renameBoxXLarge);
			getHeader().setTitle("Rename file");
		}

		fileNameTextField = new JTextField();
		if (copy) {
			fileNameTextField.setText(OsUtils.getNextAvailableFileName(file, newParentDir));
		} else {
			fileNameTextField.setText(OsUtils.getFileNameWithoutExtension(file));
		}
		fileNameTextField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				validateFileName();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				validateFileName();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				validateFileName();
			}
		});

		defaultBackground = fileNameTextField.getBackground();

		getContentPanel().add(fileNameTextField);
		getContentPanel().add(Box.createRigidArea(new Dimension(5, 500)));

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowOpened(WindowEvent e) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						fileNameTextField.requestFocusInWindow();
						fileNameTextField.selectAll();
					}
				});
			}
		});

		validateFileName();

	}

	private void validateFileName() {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				boolean fileNameValid = isFileNameValid();

				getOkButton().setEnabled(fileNameValid);
				fileNameTextField.setBackground(fileNameValid ? defaultBackground : ColorUtils.INVALID_INPUT_BACKGROUND);

			}
		});

	}

	private boolean isFileNameValid() {

		boolean fileNameValid = OsUtils.isNewFileNameAllowed(file, fileNameTextField.getText(), newParentDir, true);

		if (fileNameValid && linkedFileExtensions != null && linkedFileExtensions.size() > 0) {

			for (String extension : linkedFileExtensions) {

				extension = extension.trim();

				File linkedFile = OsUtils.getFileWithOtherExtension(file, extension);
				if (linkedFile != null && linkedFile.exists()) {

					String linkedFileName = fileNameTextField.getText() + (extension.length() == 0 ? "" : "." + extension); //$NON-NLS-1$ //$NON-NLS-2$
					if (!OsUtils.isNewFileNameAllowed(file, linkedFileName, newParentDir, false)) {
						fileNameValid = false;
						break;
					}

				}

			}

		}

		return fileNameValid;

	}

	@Override
	protected void submit() {

		if (isFileNameValid()) {

			boolean failed = false;
			File newFile = null;

			File targetFile = OsUtils.getFileWithNewName(file, fileNameTextField.getText(), newParentDir, true);

			if (copy) {
				newFile = OsUtils.copyFile(file, targetFile);
			} else {
				newFile = OsUtils.renameFile(file, targetFile);
			}

			if (newFile == null) {
				failed = true;
			}

			if (!failed && linkedFileExtensions != null && linkedFileExtensions.size() > 0) {

				for (String extension : linkedFileExtensions) {

					extension = extension.trim();

					File linkedFile = OsUtils.getFileWithOtherExtension(file, extension);
					if (linkedFile != null && linkedFile.exists()) {

						String linkedFileName = fileNameTextField.getText() + (extension.length() == 0 ? "" : "." + extension); //$NON-NLS-1$ //$NON-NLS-2$
						targetFile = OsUtils.getFileWithNewName(linkedFile, linkedFileName, newParentDir, false);

						if (copy) {
							if (OsUtils.copyFile(linkedFile, targetFile) == null) {
								failed = true;
							}
						} else {
							if (OsUtils.renameFile(linkedFile, targetFile) == null) {
								failed = true;
							}
						}

					}

				}

			}

			if (!failed) {
				fileWithNewName = newFile;
			} else {

				// TODO: Renaming/copying multiple files can theoretically fail halfway, should we try to restore the original names?

				if (copy) {
					DialogUtils.showWarningMessage(context.getFrame(), "Copy failed", "The copy operation failed");
				} else {
					DialogUtils.showWarningMessage(context.getFrame(), "Rename failed", "The rename operation failed");
				}

			}

			super.submit();

		}

	}

	public File getFileWithNewName() {
		return fileWithNewName;
	}

}
