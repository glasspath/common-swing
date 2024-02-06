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

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.glasspath.common.icons.Icons;
import org.glasspath.common.swing.color.ColorUtils;
import org.glasspath.common.swing.frame.FrameUtils;
import org.glasspath.common.swing.frame.FrameUtils.FrameRunnable;
import org.glasspath.common.swing.theme.Theme;

public class PaddingPanel extends JPanel {

	public static final Stroke DASHED_LINE_STROKE = new BasicStroke(1.0F, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 0, new float[] { 2.5F, 1.5F }, 0);

	private final JSpinner topSpinner;
	private final JSpinner rightSpinner;
	private final JSpinner bottomSpinner;
	private final JSpinner leftSpinner;
	private final JToggleButton linkTopBottomButton;
	private final JToggleButton linkLeftRightButton;
	private final JToggleButton linkAllButton;

	public PaddingPanel() {
		this(0, 0, 0, 0);
	}

	public PaddingPanel(int top, int right, int bottom, int left) {

		topSpinner = createSpinner(top);
		rightSpinner = createSpinner(right);
		bottomSpinner = createSpinner(bottom);
		leftSpinner = createSpinner(left);
		linkTopBottomButton = createLinkButton();
		linkLeftRightButton = createLinkButton();
		linkAllButton = createLinkButton();

		setOpaque(false);

		GridBagLayout layout = new GridBagLayout();
		layout.rowWeights = new double[] { 0.1, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.1 };
		layout.rowHeights = new int[] { 7, 25, 3, 18, 3, 25, 3, 18, 3, 25, 7 };
		layout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.1, 0.0, 0.1, 0.0, 0.0, 0.0, 0.0 };
		layout.columnWidths = new int[] { 7, 70, 3, 18, 3, 70, 3, 18, 3, 70, 7 };
		setLayout(layout);

		add(topSpinner, new GridBagConstraints(5, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(rightSpinner, new GridBagConstraints(9, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(bottomSpinner, new GridBagConstraints(5, 9, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(leftSpinner, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		add(linkTopBottomButton, new GridBagConstraints(5, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(linkLeftRightButton, new GridBagConstraints(3, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(linkAllButton, new GridBagConstraints(5, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		topSpinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				updateSpinners();
				repaint();
			}
		});
		rightSpinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if (rightSpinner.isEnabled()) {
					repaint();
				}
			}
		});
		bottomSpinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if (bottomSpinner.isEnabled()) {
					repaint();
				}
			}
		});
		leftSpinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if (leftSpinner.isEnabled()) {
					updateSpinners();
					repaint();
				}
			}
		});

		linkTopBottomButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				updateSpinners();
				repaint();
			}
		});
		linkLeftRightButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				updateSpinners();
				repaint();
			}
		});
		linkAllButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				updateSpinners();
				repaint();
			}
		});

		if (right == top && bottom == top && left == top) {
			linkAllButton.setSelected(true);
			updateSpinners();
		}

	}

	protected JSpinner createSpinner(int value) {

		JSpinner spinner = new JSpinner(new SpinnerNumberModel(value, 0, Integer.MAX_VALUE, 1));
		spinner.setPreferredSize(new Dimension(70, 25));
		spinner.setValue(value);

		return spinner;

	}

	protected JToggleButton createLinkButton() {

		JToggleButton button = new JToggleButton();
		button.setMargin(new Insets(1, 1, 1, 1));
		button.setIcon(Icons.linkOff);
		button.setSelectedIcon(Icons.link);

		return button;

	}

	private void updateSpinners() {

		linkTopBottomButton.setVisible(!linkAllButton.isSelected());
		linkLeftRightButton.setVisible(!linkAllButton.isSelected());

		if (linkAllButton.isSelected()) {
			leftSpinner.setEnabled(false);
			leftSpinner.setValue(topSpinner.getValue());
		} else {
			leftSpinner.setEnabled(true);
		}

		if (linkTopBottomButton.isSelected() || linkAllButton.isSelected()) {
			bottomSpinner.setEnabled(false);
			bottomSpinner.setValue(topSpinner.getValue());
		} else {
			bottomSpinner.setEnabled(true);
		}

		if (linkLeftRightButton.isSelected() || linkAllButton.isSelected()) {
			rightSpinner.setEnabled(false);
			rightSpinner.setValue(leftSpinner.getValue());
		} else {
			rightSpinner.setEnabled(true);
		}

	}

	public int getTopPadding() {
		return ((Number) topSpinner.getValue()).intValue();
	}

	public int getRightPadding() {
		return ((Number) rightSpinner.getValue()).intValue();
	}

	public int getBottomPadding() {
		return ((Number) bottomSpinner.getValue()).intValue();
	}

	public int getLeftPadding() {
		return ((Number) leftSpinner.getValue()).intValue();
	}

	@Override
	public void paint(Graphics g) {

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int xLeft = leftSpinner.getX() + (leftSpinner.getWidth() / 2);
		int xMiddle = topSpinner.getX() + (topSpinner.getWidth() / 2);
		int yTop = topSpinner.getY() + (topSpinner.getHeight() / 2);
		int yMiddle = leftSpinner.getY() + (leftSpinner.getHeight() / 2);
		int xRight = rightSpinner.getX() + (rightSpinner.getWidth() / 2);
		int yBottom = bottomSpinner.getY() + (bottomSpinner.getHeight() / 2);
		int width = xRight - xLeft;
		int height = yBottom - yTop;
		
		g2d.setColor(Theme.isDark() ? ColorUtils.GRAY_90 : Color.lightGray);
		Rectangle rect = new Rectangle(xLeft, yTop, width, height);
		g2d.draw(rect);

		g2d.setColor(Theme.isDark() ? Color.gray : new Color(150, 150, 150));
		if (getTopPadding() > 0) {
			rect = new Rectangle(xLeft, yTop, width, getTopPadding());
			g2d.fill(rect);
		}
		if (getRightPadding() > 0) {
			rect = new Rectangle(xRight - getRightPadding(), yTop, getRightPadding(), height);
			g2d.fill(rect);
		}
		if (getBottomPadding() > 0) {
			rect = new Rectangle(xLeft, yBottom - getBottomPadding(), width, getBottomPadding());
			g2d.fill(rect);
		}
		if (getLeftPadding() > 0) {
			rect = new Rectangle(xLeft, yTop, getLeftPadding(), height);
			g2d.fill(rect);
		}

		g2d.setStroke(DASHED_LINE_STROKE);

		if (linkTopBottomButton.isSelected() || linkAllButton.isSelected()) {
			g2d.setColor(Theme.isDark() ? Color.lightGray : Color.darkGray);
		} else {
			g2d.setColor(Theme.isDark() ? Color.gray : Color.lightGray);
		}
		g2d.drawLine(xMiddle, yTop, xMiddle, yBottom);

		if (linkLeftRightButton.isSelected() || linkAllButton.isSelected()) {
			g2d.setColor(Theme.isDark() ? Color.lightGray : Color.darkGray);
		} else {
			g2d.setColor(Theme.isDark() ? Color.gray : Color.lightGray);
		}
		g2d.drawLine(xLeft, yMiddle, xRight, yMiddle);

		super.paint(g);

	}

	public static void main(String[] args) {
		FrameUtils.runTestFrame(false, new FrameRunnable() {

			@Override
			public void run(JFrame frame) {
				frame.setSize(350, 250);
				frame.getContentPane().add(new PaddingPanel(), BorderLayout.CENTER);
			}
		});
	}

}
