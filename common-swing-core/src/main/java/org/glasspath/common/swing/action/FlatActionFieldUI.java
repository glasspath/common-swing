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
package org.glasspath.common.swing.action;

import static com.formdev.flatlaf.util.UIScale.scale;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;

import com.formdev.flatlaf.ui.FlatArrowButton;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.ui.MigLayoutVisualPadding;
import com.formdev.flatlaf.util.UIScale;

@SuppressWarnings("nls")
public class FlatActionFieldUI extends ComponentUI {

	protected ActionField actionField;
	protected Insets padding;
	protected String arrowType;
	protected Color borderColor;
	protected Color disabledBorderColor;
	protected Color disabledBackground;
	protected Color buttonBackground;
	protected Color buttonArrowColor;
	protected Color buttonDisabledArrowColor;
	protected Color buttonHoverArrowColor;
	protected Color buttonPressedArrowColor;
	protected Color buttonPressedBackground;
	protected FocusListener focusListener;
	protected JButton button;

	public static ComponentUI createUI(JComponent c) {
		return new FlatActionFieldUI();
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);

		actionField = (ActionField) c;
		actionField.setLayout(createLayoutManager());

		padding = UIManager.getInsets("ComboBox.padding");

		arrowType = UIManager.getString("Component.arrowType");
		borderColor = UIManager.getColor("Component.borderColor");
		disabledBorderColor = UIManager.getColor("Component.disabledBorderColor");

		disabledBackground = UIManager.getColor("ComboBox.disabledBackground");

		buttonBackground = UIManager.getColor("ComboBox.buttonBackground");
		buttonArrowColor = UIManager.getColor("ComboBox.buttonArrowColor");
		buttonDisabledArrowColor = UIManager.getColor("ComboBox.buttonDisabledArrowColor");
		buttonHoverArrowColor = UIManager.getColor("ComboBox.buttonHoverArrowColor");
		buttonPressedArrowColor = UIManager.getColor("ComboBox.buttonPressedArrowColor");
		buttonPressedBackground = UIManager.getColor("Button.pressedBackground"); // TODO?

		LookAndFeel.installColors(actionField, "ComboBox.background", "ComboBox.foreground");

		LookAndFeel.installBorder(actionField, "ComboBox.border");
		LookAndFeel.installProperty(actionField, "opaque", Boolean.TRUE);

		MigLayoutVisualPadding.install(actionField);

		focusListener = new FlatUIUtils.RepaintFocusListener(actionField, null);
		actionField.field.addFocusListener(focusListener);
		actionField.field.setName("field");
		actionField.field.setBorder(BorderFactory.createEmptyBorder());
		actionField.field.setOpaque(false);

		button = new FlatArrowButton(SwingConstants.SOUTH, arrowType, buttonArrowColor, buttonDisabledArrowColor, buttonHoverArrowColor, null, buttonPressedArrowColor, buttonPressedBackground) {

			@Override
			protected void paintArrow(Graphics2D g) {

				double x = getWidth() / 2;
				double y = (int) (getHeight() * 0.7);

				Rectangle2D rect = new Rectangle2D.Double(x - 5, y, 2, 2);
				g.fill(rect);

				rect.setRect(x - 1, y, 2, 2);
				g.fill(rect);

				rect.setRect(x + 3, y, 2, 2);
				g.fill(rect);

			}
		};
		button.setName("actionButton");
		actionField.add(button);
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// actionField.field.requestFocusInWindow(); // TODO?

				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						actionField.fireActionPerformed(e);
					}
				});

			}
		});

	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);

		actionField.remove(button);
		button = null;

		actionField.field.removeFocusListener(focusListener);
		focusListener = null;

		borderColor = null;
		disabledBorderColor = null;

		disabledBackground = null;

		buttonBackground = null;
		buttonArrowColor = null;
		buttonDisabledArrowColor = null;
		buttonHoverArrowColor = null;
		buttonPressedArrowColor = null;
		buttonPressedBackground = null;

		if (actionField.getBorder() instanceof UIResource) {
			actionField.setBorder(null);
		}

		MigLayoutVisualPadding.uninstall(actionField);

		actionField = null;
		actionField.setLayout(null);

	}

	protected LayoutManager createLayoutManager() {

		return new LayoutManager() {

			@Override
			public void addLayoutComponent(String name, Component comp) {

			}

			@Override
			public void removeLayoutComponent(Component comp) {

			}

			@Override
			public Dimension preferredLayoutSize(Container parent) {
				return parent.getPreferredSize();
			}

			@Override
			public Dimension minimumLayoutSize(Container parent) {
				return parent.getMinimumSize();
			}

			@Override
			public void layoutContainer(Container parent) {

				Insets insets = actionField.getInsets();
				int x = insets.left;
				int y = insets.top;
				int width = actionField.getWidth() - insets.left - insets.right;
				int height = actionField.getHeight() - insets.top - insets.bottom;

				int actionButtonWidth = button != null ? height : 0;
				boolean ltr = actionField.getComponentOrientation().isLeftToRight();

				Rectangle r = new Rectangle(x + (ltr ? 0 : actionButtonWidth), y, width - actionButtonWidth, height);
				r = FlatUIUtils.subtractInsets(r, UIScale.scale(padding));
				actionField.getField().setBounds(r);

				if (button != null) {
					button.setBounds(x + (ltr ? width - actionButtonWidth : 0), y, actionButtonWidth, height);
				}

			}
		};

	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		Dimension dim = actionField.getField().getPreferredSize();
		dim = FlatUIUtils.addInsets(dim, UIScale.scale(padding));
		if (button != null) {
			dim.width += dim.height;
		}
		return FlatUIUtils.addInsets(dim, actionField.getInsets());
	}

	@Override
	public void update(Graphics g, JComponent c) {

		// fill background if opaque to avoid garbage if user sets opaque to true
		if (c.isOpaque()) {
			FlatUIUtils.paintParentBackground(g, c);
		}

		Graphics2D g2d = (Graphics2D) g;
		Object[] oldRenderingHints = FlatUIUtils.setRenderingHints(g2d);

		int width = c.getWidth();
		int height = c.getHeight();
		float focusWidth = FlatUIUtils.getBorderFocusWidth(c);
		float arc = FlatUIUtils.getBorderArc(c);
		int arrowX = button.getX();
		int arrowWidth = button.getWidth();
		boolean enabled = c.isEnabled();
		boolean isLeftToRight = c.getComponentOrientation().isLeftToRight();

		// paint background
		g2d.setColor(enabled ? c.getBackground() : disabledBackground);
		FlatUIUtils.paintComponentBackground(g2d, 0, 0, width, height, focusWidth, arc);

		// paint arrow button background
		if (enabled) {
			g2d.setColor(buttonBackground);
			Shape oldClip = g2d.getClip();
			if (isLeftToRight) {
				g2d.clipRect(arrowX, 0, width - arrowX, height);
			} else {
				g2d.clipRect(0, 0, arrowX + arrowWidth, height);
			}
			FlatUIUtils.paintComponentBackground(g2d, 0, 0, width, height, focusWidth, arc);
			g2d.setClip(oldClip);
		}

		// paint vertical line between value and arrow button
		g2d.setColor(enabled ? borderColor : disabledBorderColor);
		float lw = scale(1f);
		float lx = isLeftToRight ? arrowX : arrowX + arrowWidth - lw;
		g2d.fill(new Rectangle2D.Float(lx, focusWidth, lw, height - 1 - (focusWidth * 2)));

		paint(g, c);

		FlatUIUtils.resetRenderingHints(g2d, oldRenderingHints);

	}

}
