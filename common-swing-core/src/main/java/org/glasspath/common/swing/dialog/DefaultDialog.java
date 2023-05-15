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
package org.glasspath.common.swing.dialog;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.glasspath.common.swing.FrameContext;
import org.glasspath.common.swing.color.ColorUtils;
import org.glasspath.common.swing.help.HelpUtils;
import org.glasspath.common.swing.resources.Resources;
import org.glasspath.common.swing.separator.Separator;

public class DefaultDialog extends JDialog {

	public static final Dimension DIALOG_SIZE_SMALL = new Dimension(350, 250);
	public static final Dimension DIALOG_SIZE_SMALL_WIDE = new Dimension(450, 250);
	public static final Dimension DIALOG_SIZE_DEFAULT = new Dimension(790, 450);
	public static final Dimension DIALOG_SIZE_SQUARE = new Dimension(600, 600);
	public static final Dimension DIALOG_SIZE_MEDIUM = new Dimension(800, 600);
	public static final Dimension DIALOG_SIZE_LARGE = new Dimension(1000, 700);
	public static final Dimension DIALOG_SIZE_LARGE_REDUCED = new Dimension(950, 650);
	public static final Dimension DIALOG_SIZE_LARGE_WIDE = new Dimension(1150, 550);
	public static final Dimension DIALOG_SIZE_LARGE_WIDE_REDUCED = new Dimension(1100, 500);
	public static final Dimension DIALOG_SIZE_LARGE_WIDE_TALL = new Dimension(1150, 700);

	protected final FrameContext context;
	private final KeyEventDispatcher keyEventDispatcher;
	private final DefaultDialogHeader header;
	private final Separator headerSeparator;
	private final JPanel contentPanel;
	private final Separator footerSeparator;
	private final JPanel footer;
	private final JButton helpButton;
	private final JButton okButton;
	private final JButton cancelButton;

	private JComponent focusComponent = null;
	private boolean focusComponentBlocksKeyEvents = false;
	private boolean keyListenerEnabled = true;
	private String helpPage = "https://glasspath.org"; //$NON-NLS-1$

	protected boolean submitted = false;

	public DefaultDialog() {
		this(null, true, true);
	}

	public DefaultDialog(FrameContext context) {
		this(context, true, true);
	}

	public DefaultDialog(FrameContext context, boolean showHeader) {
		this(context, showHeader, true);
	}

	public DefaultDialog(FrameContext context, boolean showHeader, boolean modal) {
		super(context != null ? context.getFrame() : null);

		this.context = context;

		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
		setPreferredSize(DIALOG_SIZE_DEFAULT);

		if (showHeader) {
			getRootPane().setBackground(ColorUtils.TITLE_BAR_COLOR);
		}

		if (modal) {
			setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
		} else {
			setModalityType(Dialog.ModalityType.MODELESS);
		}

		keyEventDispatcher = new KeyEventDispatcher() {

			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {

				if (e.getID() == KeyEvent.KEY_PRESSED && isActive()) {

					boolean blockKeyEvents;

					Component focusOwner = getFocusOwner();
					if (focusComponent != null && focusOwner == focusComponent && !focusComponentBlocksKeyEvents) {
						blockKeyEvents = false;
					} else if (focusOwner instanceof JTextArea) {
						blockKeyEvents = true;
					} else {
						blockKeyEvents = false;
					}

					if (e.getKeyCode() == KeyEvent.VK_ENTER) {

						if (blockKeyEvents) {
							// Don't close the dialog
						} else {
							submit();
						}

					} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {

						if (blockKeyEvents) {
							// Don't close the dialog
						} else {
							cancel();
						}

					}

				}

				return false;

			}
		};

		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentShown(ComponentEvent e) {

				installKeyboardListener();

				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				if (getSize().width > screenSize.width || getSize().height > screenSize.height) {

					setSize(screenSize);

					if (context != null) {
						setLocationRelativeTo(context.getFrame());
					}

				}

				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						setInitialFocusComponent();
					}
				});

			}
		});

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				closeDialog();
			}
		});

		if (showHeader) {

			header = new DefaultDialogHeader();
			getContentPane().add(header);

			headerSeparator = new Separator();
			headerSeparator.setMinimumSize(new Dimension(100, 4));
			getContentPane().add(headerSeparator);

		} else {
			header = null;
			headerSeparator = null;
		}

		contentPanel = new JPanel();
		contentPanel.setMinimumSize(new Dimension(0, 0));
		contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 7, 7, 7));
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));

		getContentPane().add(contentPanel);

		footerSeparator = new Separator();
		footerSeparator.setMinimumSize(new Dimension(100, 4));
		getContentPane().add(footerSeparator);

		footer = new JPanel();
		footer.setLayout(new BoxLayout(footer, BoxLayout.LINE_AXIS));
		footer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		helpButton = new JButton(Resources.getString("Help")); //$NON-NLS-1$
		footer.add(helpButton);
		helpButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				HelpUtils.showHelp(helpPage);
			}
		});
		helpButton.setVisible(false); // TODO?

		footer.add(Box.createHorizontalGlue());

		okButton = new JButton(Resources.getString("Ok")); //$NON-NLS-1$
		footer.add(okButton);
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				submit();
			}
		});

		footer.add(Box.createRigidArea(new Dimension(5, 5)));

		cancelButton = new JButton(Resources.getString("Cancel")); //$NON-NLS-1$
		footer.add(cancelButton);
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});

		getContentPane().add(footer);

		pack();

		if (context != null) {
			setLocationRelativeTo(context.getFrame());
		}

	}

	protected void setInitialFocusComponent() {
		if (focusComponent != null) {
			focusComponent.requestFocusInWindow();
		}
	}

	public boolean setVisibleAndGetAction() {

		pack();
		setLocationRelativeTo(context.getFrame());
		setVisible(true);

		return submitted;

	}

	public DefaultDialogHeader getHeader() {
		return header;
	}

	public Separator getHeaderSeparator() {
		return headerSeparator;
	}

	public JPanel getContentPanel() {
		return contentPanel;
	}

	public Separator getFooterSeparator() {
		return footerSeparator;
	}

	public JPanel getFooter() {
		return footer;
	}

	public JButton getHelpButton() {
		return helpButton;
	}

	public JButton getOkButton() {
		return okButton;
	}

	public JButton getCancelButton() {
		return cancelButton;
	}

	public String getHelpPage() {
		return helpPage;
	}

	public void setHelpPage(String helpPage) {
		this.helpPage = helpPage;
	}

	public JComponent getFocusComponent() {
		return focusComponent;
	}

	public void setFocusComponent(JComponent focusComponent) {
		setFocusComponent(focusComponent, false);
	}

	public void setFocusComponent(JComponent focusComponent, boolean focusComponentBlocksKeyEvents) {
		this.focusComponent = focusComponent;
		this.focusComponentBlocksKeyEvents = focusComponentBlocksKeyEvents;
	}

	protected void submit() {
		setContentChanged();
		submitted = true;
		closeDialog();
	}

	protected void setContentChanged() {
		if (context != null) {
			context.setContentChanged(true);
		}
	}

	protected void cancel() {
		closeDialog();
	}

	public void close() {
		closeDialog();
	}

	protected void closeDialog() {

		uninstallKeyboardListener();
		setVisible(false);

		// TODO: This is used by sub-classes to remove installed listeners,
		// we should probably do this differently..
		dispose();

	}

	public void setKeyListenerEnabled(boolean keyListenerEnabled) {
		this.keyListenerEnabled = keyListenerEnabled;
	}

	private void installKeyboardListener() {
		if (keyListenerEnabled) {
			KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyEventDispatcher);
		}
	}

	private void uninstallKeyboardListener() {
		KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(keyEventDispatcher);
	}

	public static Dimension adjustSize(Dimension size, int adjustWidth, int adjustHeight) {
		return new Dimension(size.width + adjustWidth, size.height + adjustHeight);
	}

}
