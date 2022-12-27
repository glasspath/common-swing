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
package org.glasspath.common.swing.graphics;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class NinePatch {

	private BufferedImage[] images;
	private int dx1, dy1, dx2, dy2, wx, wy;

	
	public NinePatch(Image img, int dx, int dy) {
		this(img, dx, dy, dx, dy);
	}
	
	public NinePatch(Image img, int dx1, int dy1, int dx2, int dy2) {
		
		if (img == null) {
			throw new IllegalArgumentException();
		}
		
		this.images = new BufferedImage[9];
		this.dx1 = dx1;
		this.dy1 = dy1;
		this.dx2 = dx2;
		this.dy2 = dy2;
		this.wx = img.getWidth(null)-(dx1+dx2);
		this.wy = img.getHeight(null)-(dy1+dy2);

		BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_4BYTE_ABGR);
		bi.getGraphics().drawImage(img, 0, 0, null);
		
		int w = bi.getWidth();
		int h = bi.getHeight();
		
		if (dx1 > 0 && dy1 > 0) images[0] = bi.getSubimage(1, 1, dx1, dy1);
		if (dy1 > 0)			images[1] = bi.getSubimage(dx1, 1, wx, dy1);
		if (dx2 > 0 && dy1 > 0) images[2] = bi.getSubimage(w-dx2 - 1, 1, dx2, dy1);
		
		if (dx1 > 0) 			images[3] = bi.getSubimage(1, dy1, dx1, wy);
								images[4] = bi.getSubimage(dx1, dy1,wx, wy);
		if (dx2 > 0)			images[5] = bi.getSubimage(w-dx2 - 1,dy1,dx2,wy);
		
		if (dx1 > 0 && dy2 > 0) images[6] = bi.getSubimage(1,h-dy2 - 1,dx1,dy2);
		if (dy2 > 0)			images[7] = bi.getSubimage(dx1,h-dy2 - 1,wx,dy2);
		if (dx2 > 0 && dy2 > 0) images[8] = bi.getSubimage(w-dx2 - 1,h-dy2 - 1, dx2, dy2);
		
	}
	
	public void paintNinePatch(Graphics g, int x, int y, int sx, int sy) {
		
		if (images[0] != null) g.drawImage(images[0], x, y, null);
		if (images[1] != null) g.drawImage(images[1], x+dx1, y, sx-(dx1+dx2), dy1, null);
		if (images[2] != null) g.drawImage(images[2], x+sx-dx2, y, null);
		
		if (images[3] != null) g.drawImage(images[3], x, y+dy1,dx1,sy-(dy1+dy2), null);
		if (images[4] != null) g.drawImage(images[4], x+dx1, y+dy1, sx-(dx1+dx2), sy-(dy1+dy2), null);
		if (images[5] != null) g.drawImage(images[5], x+sx-dx2, y+dy1, dx2, sy-(dy1+dy2), null);
		
		if (images[6] != null) g.drawImage(images[6], x, y+sy-dy2, null);
		if (images[7] != null) g.drawImage(images[7], x+dx1, y+sy-dy2, sx-(dx1+dx2), dy2, null);
		if (images[8] != null) g.drawImage(images[8], x+sx-dx2, y+sy-dy2, null);
		
	}
	
}

