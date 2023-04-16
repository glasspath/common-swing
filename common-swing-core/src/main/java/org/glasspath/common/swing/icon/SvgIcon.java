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
package org.glasspath.common.swing.icon;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.net.URI;
import java.net.URL;

import javax.swing.Icon;

import org.glasspath.common.swing.color.ColorUtils;
import org.glasspath.common.swing.theme.Theme;

public class SvgIcon extends FlatSVGIcon {

	public static final IColorFilter DARK = new BasicColorFilter(new Color(150, 150, 150));
	public static final IColorFilter CONTRAST = new BasicColorFilter(Theme.isDark() ? new Color(55, 55, 55) : new Color(100, 100, 100));
	public static final IColorFilter WARNING = new BasicColorFilter(Theme.isDark() ? new Color(150, 25, 25) : new Color(190, 100, 100));
	public static final IColorFilter BLACK = new BasicColorFilter(Color.black);
	public static final IColorFilter WHITE = new BasicColorFilter(Color.white);
	public static final IColorFilter BLUE = new BasicColorFilter(ColorUtils.BLUE);
	public static final IColorFilter PURPLE = new BasicColorFilter(new Color(184, 119, 217));
	public static final IColorFilter GREEN = new BasicColorFilter(new Color(151, 191, 105));
	public static final IColorFilter RED = new BasicColorFilter(new Color(242, 73, 92));
	public static final IColorFilter ORANGE = new BasicColorFilter(new Color(237, 162, 0));
	public static final IColorFilter YELLOW = new BasicColorFilter(new Color(250, 222, 42));
	static {
		ColorFilter.getInstance().add(FlatSVGIcon.currentColor, Theme.isDark() ? new Color(175, 175, 175) : new Color(125, 125, 125));
	}

	protected URL url = null;
	protected int size = 0;
	protected int padding = 0;

	public SvgIcon(URL url) {
		this(16, url);
	}

	public SvgIcon(int size, URL url) {
		this(size, 0, url);
	}

	public SvgIcon(int size, int padding, URL url) {
		this(size, padding, url, false);
	}

	public SvgIcon(int size, int padding, URL url, boolean disabled) {
		
		super(null, size - (padding * 2), size - (padding * 2), 1, false, null, getUri(url));

		this.url = url;
		this.size = size;
		this.padding = padding;
		
		// super(null, -1, -1, 1, false, null, getUri(url));
		// this.size = 24;
		// this.padding = 0;
		
	}

	@Override
	public int getWidth() {
		return size;
	}

	@Override
	public int getHeight() {
		return size;
	}

	@Override
	public int getIconWidth() {
		return size;
	}

	@Override
	public int getIconHeight() {
		return size;
	}

	private static URI getUri(URL url) {
		if (url != null) {
			try {
				return url.toURI();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} else {
			System.err.println("icon url is null"); //$NON-NLS-1$
			return null;
		}
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		super.paintIcon(c, g, x + padding, y + padding);
	}
	
	@Override
	public Icon getDisabledIcon() {
		return new SvgIcon(size, padding, url, true);
	}

	public static class OffsetSvgIcon extends SvgIcon {

		private final int top;
		private final int left;

		public OffsetSvgIcon(int size, int padding, int top, int left, URL url) {
			this(size, padding, top, left, url, false);
		}

		public OffsetSvgIcon(int size, int padding, int top, int left, URL url, boolean disabled) {
			super(size, padding, url, disabled);
			this.top = top;
			this.left = left;
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			super.paintIcon(c, g, x + padding + left, y + padding + top);
		}
		
		@Override
		public Icon getDisabledIcon() {
			return new OffsetSvgIcon(size, padding, top, left, url, true);
		}

	}

}
