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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableCellRenderer;

import org.glasspath.common.Common;
import org.glasspath.common.icons.Icons;
import org.glasspath.common.io.file.DefaultFileList;
import org.glasspath.common.io.file.FileList;
import org.glasspath.common.os.OsUtils;
import org.glasspath.common.swing.FrameContext;
import org.glasspath.common.swing.color.ColorUtils;
import org.glasspath.common.swing.dialog.DefaultDialog;
import org.glasspath.common.swing.dialog.DialogUtils;
import org.glasspath.common.swing.file.FileNameDialog;
import org.glasspath.common.swing.resources.CommonResources;
import org.glasspath.common.swing.splitpane.InvisibleSplitPane;

public abstract class FileManagerDialog extends DefaultDialog {

	protected final JComboBox<Category> categoryComboBox;
	protected final JButton addButton;
	protected final JButton editButton;
	protected final JButton copyButton;
	protected final JButton renameButton;
	protected final JButton deleteButton;
	protected final FilesTablePanel filesTablePanel;
	protected final JPanel optionsContentPanel;

	protected final DefaultFileList files = new DefaultFileList();

	public FileManagerDialog(FrameContext context) {

		super(context);

		remove(getHeader());
		remove(getHeaderSeparator());
		setTitle("File Manager");
		setPreferredSize(DIALOG_SIZE_LARGE_WIDE_TALL);
		setKeyListenerEnabled(false);

		getContentPanel().setLayout(new BorderLayout());
		getContentPanel().setBorder(BorderFactory.createEmptyBorder());

		JPanel toolBarPanel = new JPanel();
		toolBarPanel.setLayout(new BoxLayout(toolBarPanel, BoxLayout.LINE_AXIS));
		toolBarPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		toolBarPanel.setBackground(ColorUtils.TITLE_BAR_COLOR);
		getContentPanel().add(toolBarPanel, BorderLayout.NORTH);

		JToolBar toolBar = new JToolBar();
		toolBar.setRollover(true);
		toolBar.setBackground(ColorUtils.TITLE_BAR_COLOR);
		toolBarPanel.add(toolBar);

		categoryComboBox = new JComboBox<>();
		categoryComboBox.setRenderer(new CategoryListCellRenderer());
		categoryComboBox.setMaximumSize(new Dimension(200, 50)); // TODO: Not working..
		toolBarPanel.add(categoryComboBox);
		categoryComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				refresh();
			}
		});

		addButton = createToolBarButton("Add", Icons.plus);
		toolBarPanel.add(Box.createHorizontalStrut(3));
		toolBarPanel.add(addButton);
		addButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Category category = getSelectedCategory();
				if (category != null && category.getSourceDirectory() != null) {

					FileList fileList = new DefaultFileList(category.getSourceDirectory(), category.getFileFilter());

					FileSelectionDialog fileSelectionDialog = new FileSelectionDialog(context, fileList, category.getFileFilter(), category.getPreferredFileFilter(), category.getName()) {

						@Override
						protected Icon getFileIcon(File file) {
							return FileManagerDialog.this.getFileIcon(file);
						}

						@Override
						protected String getFileDescription(File file) {
							return FileManagerDialog.this.getFileDescription(file);
						}
					};
					if (fileSelectionDialog.setVisibleAndGetAction()) {

						File selectedFile = fileSelectionDialog.getSelectedFile();
						if (selectedFile != null) {

							FileNameDialog fileNameDialog = new FileNameDialog(context, selectedFile, true, category.getDirectory(), category.linkedFileExtensions);
							if (fileNameDialog.setVisibleAndGetAction()) {

								File fileCopy = fileNameDialog.getFileWithNewName();

								refresh();

								if (fileCopy != null) {
									selectFile(fileCopy);
								}

							}

						}

					}

				}

			}
		});

		editButton = createToolBarButton("Edit", Icons.squareEditOutline);
		toolBarPanel.add(Box.createHorizontalStrut(3));
		toolBarPanel.add(editButton);
		editButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				editFile(getSelectedCategory(), getSelectedFile());
			}
		});

		copyButton = createToolBarButton(CommonResources.getString("Copy"), Icons.contentCopy); //$NON-NLS-1$
		toolBarPanel.add(Box.createHorizontalStrut(3));
		toolBarPanel.add(copyButton);
		copyButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Category selectedCategory = getSelectedCategory();
				if (selectedCategory != null) {

					File selectedFile = getSelectedFile();
					if (selectedFile != null) {

						FileNameDialog fileNameDialog = new FileNameDialog(context, selectedFile, true, null, selectedCategory.linkedFileExtensions);
						if (fileNameDialog.setVisibleAndGetAction()) {

							File fileCopy = fileNameDialog.getFileWithNewName();

							refresh();

							if (fileCopy != null) {
								selectFile(fileCopy);
							}

							if (copyButton.isEnabled()) {
								copyButton.requestFocusInWindow();
							}

						}

					}

				}

			}
		});

		renameButton = createToolBarButton("Rename", Icons.renameBox);
		toolBarPanel.add(Box.createHorizontalStrut(3));
		toolBarPanel.add(renameButton);
		renameButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Category selectedCategory = getSelectedCategory();
				if (selectedCategory != null) {

					File selectedFile = getSelectedFile();
					if (selectedFile != null) {

						FileNameDialog fileNameDialog = new FileNameDialog(context, selectedFile, false, null, selectedCategory.linkedFileExtensions);
						if (fileNameDialog.setVisibleAndGetAction()) {

							File renamedFile = fileNameDialog.getFileWithNewName();

							refresh();

							if (renamedFile != null) {
								selectFile(renamedFile);
							}

							if (renameButton.isEnabled()) {
								renameButton.requestFocusInWindow();
							}

						}

					}

				}

			}
		});

		deleteButton = createToolBarButton(CommonResources.getString("Delete"), Icons.closeRed); //$NON-NLS-1$
		toolBarPanel.add(Box.createHorizontalStrut(3));
		toolBarPanel.add(deleteButton);
		deleteButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Category selectedCategory = getSelectedCategory();
				if (selectedCategory != null) {

					File selectedFile = getSelectedFile();
					if (selectedFile != null) {

						int chosenOption = JOptionPane.showOptionDialog(context.getFrame(), "Are you sure you want to delete the selected file?", "Delete file?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[] { CommonResources.getString("Yes"), CommonResources.getString("No") }, CommonResources.getString("No")); //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
						if (chosenOption == JOptionPane.YES_OPTION) {

							boolean failed = false;

							if (!OsUtils.deleteFile(selectedFile)) {
								failed = true;
							}

							if (!failed && selectedCategory.getLinkedFileExtensions() != null && selectedCategory.getLinkedFileExtensions().size() > 0) {

								for (String extension : selectedCategory.getLinkedFileExtensions()) {

									File linkedFile = OsUtils.getFileByAddingExtension(selectedFile, extension);
									if (linkedFile != null && linkedFile.exists()) {

										if (!OsUtils.deleteFile(linkedFile)) {
											failed = true;
										}

									}

								}

							}

							if (failed) {
								DialogUtils.showWarningMessage(context.getFrame(), "Deleting failed", "The delete operation failed");
							}

							refresh();

							if (deleteButton.isEnabled()) {
								deleteButton.requestFocusInWindow();
							}

						}

					}

				}

			}
		});

		toolBarPanel.add(Box.createHorizontalGlue());

		JPanel contentPanel = new JPanel();
		getContentPanel().add(contentPanel, BorderLayout.CENTER);

		InvisibleSplitPane mainSplitPanel = new InvisibleSplitPane();
		mainSplitPanel.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		mainSplitPanel.setResizeWeight(0.5);
		mainSplitPanel.setDividerLocation(550);
		getContentPanel().add(mainSplitPanel, BorderLayout.CENTER);

		filesTablePanel = new FilesTablePanel(files) {

			@Override
			protected String getFileDescription(File file) {
				return FileManagerDialog.this.getFileDescription(file);
			}

			@Override
			protected Icon getFileIcon(File file) {
				return FileManagerDialog.this.getFileIcon(file);
			}

			@Override
			protected void selecionChanged(ListSelectionEvent e) {
				updateToolBarButtons();
				refreshOptionPanels();
			}
		};
		filesTablePanel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				editFile(getSelectedCategory(), getSelectedFile());
			}
		});
		mainSplitPanel.setLeftComponent(filesTablePanel);

		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new BorderLayout());
		optionsPanel.add(new OptionsPanelHeader(), BorderLayout.NORTH);
		mainSplitPanel.setRightComponent(optionsPanel);

		optionsContentPanel = new JPanel();
		optionsContentPanel.setLayout(new BorderLayout());
		optionsPanel.add(optionsContentPanel, BorderLayout.CENTER);

		getFooter().remove(getOkButton());
		getCancelButton().setText(CommonResources.getString("Close")); //$NON-NLS-1$

		updateToolBarButtons();

	}

	@Override
	protected void setContentChanged() {
		// Application content is not changed by file manager
	}

	protected void setCategories(List<Category> categories) {
		categoryComboBox.removeAllItems();
		for (Category category : categories) {
			categoryComboBox.addItem(category);
		}
	}

	public Category getSelectedCategory() {
		Object selectedItem = categoryComboBox.getSelectedItem();
		if (selectedItem instanceof Category) {
			return (Category) selectedItem;
		} else {
			return null;
		}
	}

	public void setSelectedCategory(Category category) {
		categoryComboBox.setSelectedItem(category);
	}

	public File getSelectedFile() {
		return filesTablePanel.getSelectedFile();
	}

	protected abstract JComponent getOptionsComponent(Category selectedCategory, File selectedFile);

	protected abstract Icon getFileIcon(File file);

	protected abstract String getFileDescription(File file);

	protected abstract void editFile(Category selectedCategory, File file);

	protected void refresh() {

		Category selectedCategory = getSelectedCategory();

		if (selectedCategory == null) {
			files.setDirectory(null);
		} else {
			files.setDirectory(selectedCategory.getDirectory());
			files.setFileFilter(selectedCategory.getFileFilter());
		}

		addButton.setEnabled(selectedCategory.getDirectory() != null && selectedCategory.getDirectory().isDirectory()
				&& selectedCategory.getSourceDirectory() != null && selectedCategory.getSourceDirectory().isDirectory());

		filesTablePanel.reload();

		refreshOptionPanels();

	}

	protected void refreshOptionPanels() {

		Category selectedCategory = getSelectedCategory();
		File selectedFile = getSelectedFile();

		optionsContentPanel.removeAll();

		JComponent optionsComponent = getOptionsComponent(selectedCategory, selectedFile);
		if (optionsComponent != null) {
			optionsContentPanel.add(optionsComponent, BorderLayout.CENTER);
		}

		optionsContentPanel.invalidate();
		optionsContentPanel.revalidate();
		optionsContentPanel.repaint();

	}

	protected boolean selectFile(File file) {
		return filesTablePanel.selectFile(file);
	}

	protected void updateToolBarButtons() {

		File selectedFile = getSelectedFile();

		editButton.setEnabled(selectedFile != null);
		copyButton.setEnabled(selectedFile != null);
		renameButton.setEnabled(selectedFile != null);
		deleteButton.setEnabled(selectedFile != null);

	}

	protected JButton createToolBarButton(String text, Icon icon) {

		JButton button = new JButton(text);
		button.setIcon(icon);
		button.setEnabled(false);
		// button.setBorderPainted(false);
		button.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				// button.setBorderPainted(button.isEnabled());
				button.repaint();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// button.setBorderPainted(false);
				button.repaint();
			}
		});

		button.setMargin(new Insets(5, 8, 5, 8));

		return button;

	}

	public static class Category {

		private String name = ""; //$NON-NLS-1$
		private File directory = null;
		private File sourceDirectory = null;
		private FileFilter fileFilter = null;
		private FileFilter preferredFileFilter = null;
		private List<String> linkedFileExtensions = null;

		public Category() {
			this("", null, null, null, null, null); //$NON-NLS-1$
		}

		public Category(String name, FileFilter fileFilter, List<String> linkedFileExtensions) {
			this(name, null, null, fileFilter, null, linkedFileExtensions);
		}

		public Category(String name, FileFilter fileFilter, FileFilter preferredFileFilter, List<String> linkedFileExtensions) {
			this(name, null, null, fileFilter, preferredFileFilter, linkedFileExtensions);
		}

		public Category(String name, File directory, File sourceDirectory, FileFilter fileFilter, FileFilter preferredFileFilter, List<String> linkedFileExtensions) {
			this.name = name;
			this.directory = directory;
			this.sourceDirectory = sourceDirectory;
			this.fileFilter = fileFilter;
			this.preferredFileFilter = preferredFileFilter;
			this.linkedFileExtensions = linkedFileExtensions;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public File getDirectory() {
			return directory;
		}

		public void setDirectory(File directory) {
			this.directory = directory;
		}

		public File getSourceDirectory() {
			return sourceDirectory;
		}

		public void setSourceDirectory(File sourceDirectory) {
			this.sourceDirectory = sourceDirectory;
		}

		public List<File> getDirectoryFiles() {
			return getFiles(directory);
		}

		public List<File> getSourceDirectoryFiles() {
			return getFiles(sourceDirectory);
		}

		public FileFilter getFileFilter() {
			return fileFilter;
		}

		public void setFileFilter(FileFilter fileFilter) {
			this.fileFilter = fileFilter;
		}

		public FileFilter getPreferredFileFilter() {
			return preferredFileFilter;
		}

		public void setPreferredFileFilter(FileFilter preferredFileFilter) {
			this.preferredFileFilter = preferredFileFilter;
		}

		public List<String> getLinkedFileExtensions() {
			return linkedFileExtensions;
		}

		public void setLinkedFileExtensions(List<String> linkedFileExtensions) {
			this.linkedFileExtensions = linkedFileExtensions;
		}

		private List<File> getFiles(File dir) {

			List<File> files = new ArrayList<>();

			try {
				if (dir != null && dir.isDirectory()) {
					File[] filteredFiles = dir.listFiles(fileFilter);
					for (File file : filteredFiles) {
						files.add(file);
					}
				}
			} catch (Exception e) {
				Common.LOGGER.error("Exception while getting files: ", e); //$NON-NLS-1$
			}

			return files;

		}

	}

	protected class CategoryListCellRenderer extends DefaultListCellRenderer {

		public CategoryListCellRenderer() {

		}

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

			if (value instanceof Category) {
				setText(((Category) value).getName());
			}

			return this;

		}

	}

	protected static class OptionsPanelHeader extends JComponent {

		private final JTable table = new JTable(new Object[][] { { "" } }, new String[] { "Options" }); //$NON-NLS-1$
		private final JScrollPane tableScrollPane = new JScrollPane(table);
		private final Dimension preferredSize = new Dimension(100, 25);

		public OptionsPanelHeader() {

			setOpaque(true);
			setLayout(new BorderLayout());
			add(tableScrollPane, BorderLayout.NORTH);

		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);

			g.setColor(tableScrollPane.getBackground());
			g.fillRect(0, 0, getWidth(), table.getTableHeader().getHeight() - 1);

			table.getTableHeader().setBackground(ColorUtils.TITLE_BAR_COLOR);
			TableCellRenderer renderer = table.getTableHeader().getDefaultRenderer();
			Component component = renderer.getTableCellRendererComponent(table, "", false, false, 0, 0); //$NON-NLS-1$

			// TODO: This is a quick hack
			if (component instanceof JLabel) {

				JLabel label = (JLabel) component;
				label.setOpaque(true);

				label.setText("Options");
				label.setBounds(0, 0, getWidth() + 1, table.getTableHeader().getHeight());
				label.paint(g);

				label.setText(""); //$NON-NLS-1$
				label.setBounds(0, 0, 1, table.getTableHeader().getHeight());
				label.paint(g);

			}

		}

		@Override
		public Dimension getPreferredSize() {
			return preferredSize;
		}

	}

}
