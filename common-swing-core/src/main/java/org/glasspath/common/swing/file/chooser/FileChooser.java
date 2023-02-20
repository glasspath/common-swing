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
package org.glasspath.common.swing.file.chooser;

import java.awt.Component;
import java.awt.Frame;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;

import org.glasspath.common.Common;

public class FileChooser extends JFileChooser {

	private boolean saveAction = false;
	private String extension;

	public FileChooser() {
		super();
	}

	public FileChooser(File file) {
		super(file);
	}

	public FileChooser(String baseDir) {
		super(baseDir);
	}

	public void setSaveAction(boolean saveAction) {
		this.saveAction = saveAction;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	@Override
	public void approveSelection() {

		if (saveAction && extension != null && extension.length() >= 3 && !getSelectedFile().getAbsolutePath().endsWith("." + extension)) { //$NON-NLS-1$
			setSelectedFile(new File(getSelectedFile().getAbsolutePath() + "." + extension)); //$NON-NLS-1$
		}

		if (validateFile(getSelectedFile())) {
			super.approveSelection();
		}

	}

	private boolean validateFile(File file) {
		if (saveAction && file.exists()) {
			return showFileExistsDialog(this);
		} else {
			return true;
		}
	}

	public static boolean showFileExistsDialog(Component parentComponent) {
		return JOptionPane.showOptionDialog(parentComponent, "File already exists, replace file?", "Replace file?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[] { "Yes", "No" }, "No") == JOptionPane.YES_OPTION;
	}

	public static String browseForFile(String fileExtension, Icon fileIcon, boolean save, Frame frame, Preferences preferences, String preferencesKey) {
		return browseForFile(fileExtension, fileIcon, save, frame, preferences, preferencesKey, null);
	}

	public static String browseForImageFile(Icon fileIcon, boolean save, Frame frame, Preferences preferences, String preferencesKey) {

		List<String> imageFileExtionsion = new ArrayList<>();
		imageFileExtionsion.add("png"); //$NON-NLS-1$
		imageFileExtionsion.add("jpg"); //$NON-NLS-1$
		imageFileExtionsion.add("jpeg"); //$NON-NLS-1$

		return FileChooser.browseForFileWithExtensions(imageFileExtionsion, fileIcon, save, frame, preferences, preferencesKey);

	}

	public static String browseForFile(String fileExtension, Icon fileIcon, boolean save, Frame frame, Preferences preferences, String preferencesKey, String suggestedFileName) {

		File startDir;
		if (suggestedFileName == null || suggestedFileName.length() == 0) {
			startDir = new File(preferences.get(preferencesKey, System.getProperty("user.home")) + "/untitled"); //$NON-NLS-1$
		} else {
			startDir = new File(preferences.get(preferencesKey, System.getProperty("user.home")) + "/" + suggestedFileName); //$NON-NLS-1$ //$NON-NLS-2$
		}

		FileChooser fileChooser = new FileChooser();
		fileChooser.setSelectedFile(startDir);
		fileChooser.setDialogTitle("Choose file");
		fileChooser.setExtension(fileExtension);

		if (fileExtension != null) {

			FileFilter fileFilter = new FileFilter() {

				@Override
				public boolean accept(File f) {

					if (f.isDirectory()) {
						return true;
					}

					String extension = getExtension(f.getName());
					if (extension != null && extension.equals(fileExtension)) {
						return true;
					}

					return false;
				}

				@Override
				public String getDescription() {
					return "." + fileExtension + " files";
				}
			};

			fileChooser.setSaveAction(save);
			fileChooser.addChoosableFileFilter(fileFilter);
			fileChooser.setFileFilter(fileFilter);
			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.setFileView(new FileView() {

				@Override
				public String getName(File f) {
					return null;
				}

				@Override
				public String getDescription(File f) {
					return null;
				}

				@Override
				public Boolean isTraversable(File f) {
					return null;
				}

				@Override
				public String getTypeDescription(File f) {
					return "." + fileExtension + " files";
				}

				@Override
				public Icon getIcon(File f) {

					Icon icon = null;

					String extension = getExtension(f.getName());
					if (extension != null && extension.equals(fileExtension)) {
						icon = fileIcon;
					}

					return icon;

				}
			});

		}

		int chosenAction;
		if (save) {
			chosenAction = fileChooser.showSaveDialog(frame);
		} else {
			chosenAction = fileChooser.showOpenDialog(frame);
		}

		String filePath = null;
		if (chosenAction == JFileChooser.APPROVE_OPTION) {

			try {
				filePath = fileChooser.getSelectedFile().getAbsolutePath();
				preferences.put(preferencesKey, fileChooser.getSelectedFile().getParent());
			} catch (Exception e) {
				Common.LOGGER.error("Exception while browsing for file", e); //$NON-NLS-1$
				e.printStackTrace();
			}

		}

		return filePath;

	}

	public static String browseForFileWithExtensions(List<String> fileExtensions, Icon fileIcon, boolean save, Frame frame, Preferences preferences, String preferencesKey) {

		File startDir = new File(preferences.get(preferencesKey, System.getProperty("user.home"))); //$NON-NLS-1$

		FileChooser fileChooser = new FileChooser(startDir);
		fileChooser.setDialogTitle("Choose file"); // TODO: Translate
		// fileChooser.setExtension(fileExtension);

		if (fileExtensions != null && fileExtensions.size() > 0) {

			FileFilter fileFilter = new FileFilter() {

				@Override
				public boolean accept(File f) {

					if (f.isDirectory()) {
						return true;
					}

					String extension = getExtension(f.getName());
					if (extension != null && fileExtensions != null && fileExtensions.contains(extension)) {
						return true;
					}

					return false;

				}

				@Override
				public String getDescription() {
					return getDescriptionForFileExtensions(fileExtensions);
				}
			};

			fileChooser.setSaveAction(save);
			fileChooser.addChoosableFileFilter(fileFilter);
			fileChooser.setFileFilter(fileFilter);
			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.setFileView(new FileView() {

				@Override
				public String getName(File f) {
					return null;
				}

				@Override
				public String getDescription(File f) {
					return null;
				}

				@Override
				public Boolean isTraversable(File f) {
					return null;
				}

				@Override
				public String getTypeDescription(File f) {
					return getDescriptionForFileExtensions(fileExtensions);
				}

				@Override
				public Icon getIcon(File f) {

					Icon icon = null;

					String extension = getExtension(f.getName());
					if (extension != null && fileExtensions != null && fileExtensions.contains(extension)) {
						icon = fileIcon;
					}

					return icon;

				}
			});

		}

		int chosenAction;
		if (save) {
			chosenAction = fileChooser.showSaveDialog(frame);
		} else {
			chosenAction = fileChooser.showOpenDialog(frame);
		}

		String filePath = null;
		if (chosenAction == JFileChooser.APPROVE_OPTION) {

			try {
				filePath = fileChooser.getSelectedFile().getAbsolutePath();
				preferences.put(preferencesKey, fileChooser.getSelectedFile().getParent());
			} catch (Exception e) {
				Common.LOGGER.error("Exception while browsing for file", e); //$NON-NLS-1$
				e.printStackTrace();
			}

		}

		return filePath;

	}

	private static String getDescriptionForFileExtensions(List<String> fileExtensions) {

		String description = ""; //$NON-NLS-1$

		for (String extension : fileExtensions) {
			description += extension + ", "; //$NON-NLS-1$
		}

		if (description.endsWith(", ")) { //$NON-NLS-1$
			description = description.substring(0, description.length() - 2);
		}

		return description + " files";

	}

	public static String getExtension(String file) {

		String ext = null;
		int i = file.lastIndexOf('.');

		if (i > 0 && i < file.length() - 1) {
			ext = file.substring(i + 1).toLowerCase();
		}

		return ext;

	}

	public static String browseForDir(Frame frame, Preferences preferences, String preferencesKey, String suggestedDirName) {

		File startDir;
		if (suggestedDirName == null || suggestedDirName.length() == 0) {
			startDir = new File(preferences.get(preferencesKey, System.getProperty("user.home"))); //$NON-NLS-1$
		} else {
			startDir = new File(preferences.get(preferencesKey, System.getProperty("user.home")) + "/" + suggestedDirName); //$NON-NLS-1$ //$NON-NLS-2$
		}

		FileChooser fileChooser = new FileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setCurrentDirectory(startDir);
		fileChooser.setDialogTitle("Choose directory");

		int chosenAction = fileChooser.showOpenDialog(frame);

		String dirPath = null;
		if (chosenAction == JFileChooser.APPROVE_OPTION) {

			try {
				dirPath = fileChooser.getSelectedFile().getAbsolutePath();
				preferences.put(preferencesKey, fileChooser.getSelectedFile().getAbsolutePath());
			} catch (Exception e) {
				Common.LOGGER.error("Exception while browsing for file", e); //$NON-NLS-1$
				e.printStackTrace();
			}

		}

		return dirPath;

	}

}