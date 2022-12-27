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
package org.glasspath.common.swing.qr;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Objects;

import javax.swing.JPanel;

import io.nayuki.qrcodegen.QrCode;

public class QrCodePanel extends JPanel {

	private QrCode qrCode = null;
	private int scale = 1;
	private int borderSize = 1;
	private Color defaultBackground = null;

	private BufferedImage image = null;

	public QrCodePanel() {

	}

	public QrCode getQrCode() {
		return qrCode;
	}

	public void setQrCode(QrCode qrCode) {
		this.qrCode = qrCode;
		updateImage();
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
		updateImage();
	}

	public int getBorderSize() {
		return borderSize;
	}

	public void setBorderSize(int borderSize) {
		this.borderSize = borderSize;
		updateImage();
	}

	public Color getDefaultBackground() {
		return defaultBackground;
	}

	public void setDefaultBackground(Color defaultBackground) {
		this.defaultBackground = defaultBackground;
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		updateImage();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

		int w = getWidth();
		int h = getHeight();

		if (image != null) {

			int x = 0;
			int y = 0;

			if (image.getWidth() > w || image.getHeight() > h) {

				double scaleX = (double) w / (double) image.getWidth();
				double scaleY = (double) h / (double) image.getHeight();

				if (scaleY < scaleX) {
					g2d.scale(scaleY, scaleY);
				} else {
					g2d.scale(scaleX, scaleX);
				}

			} else {

				if (image.getWidth() < w) {
					x = (w - image.getWidth()) / 2;
				}

				if (image.getHeight() < h) {
					y = (h - image.getHeight()) / 2;
				}

			}

			// TODO: Scale down image if it doesn't fit in the panel
			g2d.drawImage(image, x, y, null);

		} else if (defaultBackground != null) {

			g2d.setColor(defaultBackground);
			g2d.fillRect(0, 0, getWidth(), getHeight());

		}

	}

	private void updateImage() {

		if (qrCode != null) {
			if (isEnabled()) {
				image = toImage(qrCode, scale, borderSize, Color.white.getRGB(), Color.black.getRGB());
			} else {
				image = toImage(qrCode, scale, borderSize, getBackground().getRGB(), getBackground().darker().getRGB());
			}
		}

	}

	/*
	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
	
	@Override
	public Dimension getPreferredSize() {
	
		Dimension preferredSize = super.getPreferredSize();
	
		if (image != null) {
			preferredSize.width = image.getWidth();
			preferredSize.height = image.getHeight();
		}
	
		return preferredSize;
	
	}
	*/

	// Copied (and slightly modified) from https://github.com/nayuki/QR-Code-generator/blob/master/java/QrCodeGeneratorDemo.java
	public static BufferedImage toImage(QrCode qr, int scale, int border, int lightColor, int darkColor) {

		Objects.requireNonNull(qr);

		if (scale <= 0 || border < 0) {
			throw new IllegalArgumentException("Value out of range"); //$NON-NLS-1$
		}

		if (border > Integer.MAX_VALUE / 2 || qr.size + border * 2L > Integer.MAX_VALUE / scale) {
			throw new IllegalArgumentException("Scale or border too large"); //$NON-NLS-1$
		}

		final BufferedImage result = new BufferedImage((qr.size + border * 2) * scale, (qr.size + border * 2) * scale, BufferedImage.TYPE_INT_RGB);
		for (int y = 0; y < result.getHeight(); y++) {
			for (int x = 0; x < result.getWidth(); x++) {
				boolean color = qr.getModule(x / scale - border, y / scale - border);
				result.setRGB(x, y, color ? darkColor : lightColor);
			}
		}

		return result;

	}

}
