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
package org.glasspath.common.swing.phone;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.glasspath.common.swing.color.ColorUtils;
import org.glasspath.common.swing.frame.FrameUtils;
import org.glasspath.common.swing.frame.FrameUtils.FrameRunnable;
import org.glasspath.common.swing.theme.Theme;

public class PhonePanel extends JPanel {

	private int margin = 25;
	private double aspectRatio = 0.5;
	private boolean appOutlinePainted = false;
	private Icon actionIcon = null;
	private Icon centerIcon = null;

	public PhonePanel() {

	}

	public int getMargin() {
		return margin;
	}

	public void setMargin(int margin) {
		this.margin = margin;
	}

	public double getAspectRatio() {
		return aspectRatio;
	}

	public void setAspectRatio(double aspectRatio) {
		this.aspectRatio = aspectRatio;
	}

	public boolean isAppOutlinePainted() {
		return appOutlinePainted;
	}

	public void setAppOutlinePainted(boolean appOutlinePainted) {
		this.appOutlinePainted = appOutlinePainted;
	}

	public Icon getActionIcon() {
		return actionIcon;
	}

	public void setActionIcon(Icon actionIcon) {
		this.actionIcon = actionIcon;
	}

	public Icon getCenterIcon() {
		return centerIcon;
	}

	public void setCenterIcon(Icon centerIcon) {
		this.centerIcon = centerIcon;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		double y = margin;
		double h = getHeight() - (margin * 2);
		double w = h * aspectRatio;
		double x = (getWidth() - w) / 2;
		double r = h * 0.1;
		double t = h * 0.01;

		RoundRectangle2D roundRect = new RoundRectangle2D.Double(x, y, w, h, r, r);
		g2d.setColor(Theme.isDark() ? new Color(35, 35, 35) : Color.lightGray);

		g2d.setStroke(new BasicStroke(2.0F));
		g2d.draw(roundRect);

		double offset = Math.round(h * 0.015);
		roundRect = new RoundRectangle2D.Double(x + offset, y + offset, w - (offset * 2), h - (offset * 2), r - (offset * 2), r - (offset * 2));
		g2d.draw(roundRect);

		/*
		double hPower = h * 0.075;
		double wPower = hPower * 0.125;
		roundRect = new RoundRectangle2D.Double(x - wPower, y + (3 * hPower), wPower, hPower, 0, 0);
		g2d.fill(roundRect);
		*/

		double wSpeaker = w * 0.275;
		double hSpeaker = wSpeaker * 0.1;
		roundRect = new RoundRectangle2D.Double(x + (w / 2) - (wSpeaker / 2), y + h - (3.25 * hSpeaker), wSpeaker, hSpeaker, hSpeaker, hSpeaker);
		g2d.fill(roundRect);

		if (appOutlinePainted) {

			double xOffset = w * 0.035;
			double yOffset = h * 0.065;

			roundRect = new RoundRectangle2D.Double(x + xOffset, y + yOffset, w - (xOffset * 2), h - (yOffset * 2), r * 0.5, r * 0.5);
			g2d.setStroke(new BasicStroke((float) (t * 0.75)));
			g2d.setColor(ColorUtils.BLUE);
			g2d.draw(roundRect);

		}

		if (actionIcon != null) {

			double xIcon = x + w - (actionIcon.getIconWidth() + (h * 0.03));
			double yIcon = y + (h * 0.09);

			actionIcon.paintIcon(this, g2d, (int) xIcon, (int) yIcon);

			double wArc = actionIcon.getIconWidth() + (2 * (actionIcon.getIconWidth() * 0.75));
			double xArc = xIcon - (actionIcon.getIconWidth() * 0.75);
			double yArc = yIcon - (actionIcon.getIconWidth() * 0.75);

			Arc2D arc = new Arc2D.Double(xArc, yArc, wArc, wArc, 0, 360, Arc2D.CHORD);
			g2d.setStroke(new BasicStroke(3.0F));
			g2d.setColor(ColorUtils.createTransparentColor(ColorUtils.BLUE, 150));
			g2d.draw(arc);

		}

		if (centerIcon != null) {

			double xIcon = x + ((w / 2) - (centerIcon.getIconWidth() / 2));
			double yIcon = y + ((h / 2) - (centerIcon.getIconHeight() / 2));

			centerIcon.paintIcon(this, g2d, (int) xIcon, (int) yIcon);

		}

	}

	public static void main(String[] args) {
		FrameUtils.runTestFrame(false, new FrameRunnable() {

			@Override
			public void run(JFrame frame) {
				frame.setSize(500, 500);
				frame.getContentPane().add(new PhonePanel(), BorderLayout.CENTER);
			}
		});
	}

}
