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
package org.glasspath.common.swing.border;

import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.MenuSelectionManager;

import org.glasspath.common.os.OsUtils;
import org.glasspath.common.swing.color.ColorChooserDialog;
import org.glasspath.common.swing.color.ColorChooserPanel;
import org.glasspath.common.swing.color.ColorChooserPanel.ColorEvent;

public class BorderMenu extends JMenu {

	public enum BorderMenuType {
		TOP, RIGHT, BOTTOM, LEFT, VERTICAL, HORIZONTAL, OUTSIDE, ALL, NONE;
	}

	private final Action borderMenuTypeAction;
	private final Action borderMenuWidthAction;

	public BorderMenu() {
		this(null, null, null, false);
	}

	public BorderMenu(Action borderMenuTypeAction, Action borderMenuWidthAction, Action borderColorAction, boolean menuBarMenu) {
		super("Border");

		this.borderMenuTypeAction = borderMenuTypeAction;
		this.borderMenuWidthAction = borderMenuWidthAction;

		JMenuItem topMenuItem = new JMenuItem("Top");
		topMenuItem.setIcon(new BorderIcon(true, false, false, false, false, false));
		add(topMenuItem);
		topMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				borderMenuTypeActionPerformed(e, BorderMenuType.TOP);
			}
		});

		JMenuItem leftMenuItem = new JMenuItem("Left");
		leftMenuItem.setIcon(new BorderIcon(false, true, false, false, false, false));
		add(leftMenuItem);
		leftMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				borderMenuTypeActionPerformed(e, BorderMenuType.LEFT);
			}
		});

		JMenuItem bottomMenuItem = new JMenuItem("Bottom");
		bottomMenuItem.setIcon(new BorderIcon(false, false, true, false, false, false));
		add(bottomMenuItem);
		bottomMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				borderMenuTypeActionPerformed(e, BorderMenuType.BOTTOM);
			}
		});

		JMenuItem rightMenuItem = new JMenuItem("Right");
		rightMenuItem.setIcon(new BorderIcon(false, false, false, true, false, false));
		add(rightMenuItem);
		rightMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				borderMenuTypeActionPerformed(e, BorderMenuType.RIGHT);
			}
		});

		addSeparator();

		JMenuItem verticalMenuItem = new JMenuItem("Vertical");
		verticalMenuItem.setIcon(new BorderIcon(false, false, false, false, true, false));
		add(verticalMenuItem);
		verticalMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				borderMenuTypeActionPerformed(e, BorderMenuType.VERTICAL);
			}
		});

		JMenuItem horizontalMenuItem = new JMenuItem("Horizontal");
		horizontalMenuItem.setIcon(new BorderIcon(false, false, false, false, false, true));
		add(horizontalMenuItem);
		horizontalMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				borderMenuTypeActionPerformed(e, BorderMenuType.HORIZONTAL);
			}
		});

		addSeparator();

		JMenuItem outsideMenuItem = new JMenuItem("Outside");
		outsideMenuItem.setIcon(new BorderIcon(true, true, true, true, false, false));
		add(outsideMenuItem);
		outsideMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				borderMenuTypeActionPerformed(e, BorderMenuType.OUTSIDE);
			}
		});

		JMenuItem allMenuItem = new JMenuItem("All");
		allMenuItem.setIcon(new BorderIcon(true, true, true, true, true, true));
		add(allMenuItem);
		allMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				borderMenuTypeActionPerformed(e, BorderMenuType.ALL);
			}
		});

		JMenuItem noneMenuItem = new JMenuItem("None");
		noneMenuItem.setIcon(new BorderIcon(false, false, false, false, false, false));
		add(noneMenuItem);
		noneMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				borderMenuTypeActionPerformed(e, BorderMenuType.NONE);
			}
		});

		addSeparator();

		JMenu borderWidthMenu = new JMenu("Border width");
		add(borderWidthMenu);

		JMenuItem borderWidth1PxMenuItem = new JMenuItem("1px");
		borderWidthMenu.add(borderWidth1PxMenuItem);
		borderWidth1PxMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				borderMenuWidthActionPerformed(e, 1);
			}
		});

		JMenuItem borderWidth2PxMenuItem = new JMenuItem("2px");
		borderWidthMenu.add(borderWidth2PxMenuItem);
		borderWidth2PxMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				borderMenuWidthActionPerformed(e, 2);
			}
		});

		JMenuItem borderWidth3PxMenuItem = new JMenuItem("3px");
		borderWidthMenu.add(borderWidth3PxMenuItem);
		borderWidth3PxMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				borderMenuWidthActionPerformed(e, 3);
			}
		});

		JMenuItem borderWidth4PxMenuItem = new JMenuItem("4px");
		borderWidthMenu.add(borderWidth4PxMenuItem);
		borderWidth4PxMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				borderMenuWidthActionPerformed(e, 4);
			}
		});

		JMenuItem borderWidth5PxMenuItem = new JMenuItem("5px");
		borderWidthMenu.add(borderWidth5PxMenuItem);
		borderWidth5PxMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				borderMenuWidthActionPerformed(e, 5);
			}
		});

		if (menuBarMenu && OsUtils.PLATFORM_MACOS) {

			JMenuItem editColorMenuItem = new JMenuItem("Border color");
			editColorMenuItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					JColorChooser colorChooser = new JColorChooser();

					JDialog dialog = new ColorChooserDialog(getFrame(), "Border color", true, null, colorChooser, new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {

							final Color color;
							if (ColorChooserDialog.NULL_COLOR.equals(colorChooser.getColor())) {
								color = null;
							} else {
								color = colorChooser.getColor();
							}

							borderColorAction.actionPerformed(new ColorEvent(e, color));

						}
					}, null);

					dialog.setLocationRelativeTo(getFrame());
					dialog.setVisible(true);
					dialog.requestFocusInWindow();

				}
			});
			add(editColorMenuItem);

		} else {

			JMenu editColorMenu = new JMenu("Border color");

			ColorChooserPanel colorChooserPanel = new ColorChooserPanel(null) {

				@Override
				protected Frame getFrame() {
					return BorderMenu.this.getFrame();
				}
			};
			editColorMenu.add(colorChooserPanel);
			colorChooserPanel.getColorChooser().getActionButton().addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					MenuSelectionManager.defaultManager().clearSelectedPath();
				}
			});
			colorChooserPanel.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					MenuSelectionManager.defaultManager().clearSelectedPath();

					if (borderColorAction != null) {
						borderColorAction.actionPerformed(e);
					}

				}
			});
			add(editColorMenu);

		}

	}

	protected Frame getFrame() {
		return null;
	}

	private void borderMenuTypeActionPerformed(ActionEvent e, BorderMenuType borderMenuType) {
		if (borderMenuTypeAction != null) {
			borderMenuTypeAction.actionPerformed(new BorderMenuTypeEvent(e, borderMenuType));
		}
	}

	private void borderMenuWidthActionPerformed(ActionEvent e, int width) {
		if (borderMenuWidthAction != null) {
			borderMenuWidthAction.actionPerformed(new BorderMenuWidthEvent(e, width));
		}
	}

	public static class BorderMenuTypeEvent extends ActionEvent {

		public final BorderMenuType borderMenuType;

		public BorderMenuTypeEvent(ActionEvent e, BorderMenuType borderMenuType) {
			super(e.getSource(), e.getID(), e.getActionCommand());
			this.borderMenuType = borderMenuType;
		}

	}

	public static class BorderMenuWidthEvent extends ActionEvent {

		public final int width;

		public BorderMenuWidthEvent(ActionEvent e, int width) {
			super(e.getSource(), e.getID(), e.getActionCommand());
			this.width = width;
		}

	}

}
