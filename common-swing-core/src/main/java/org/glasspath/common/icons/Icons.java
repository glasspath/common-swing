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
package org.glasspath.common.icons;

import java.net.URL;

import javax.swing.ImageIcon;

import org.glasspath.common.swing.icon.SvgIcon;
import org.glasspath.common.swing.icon.SvgIcon.OffsetSvgIcon;

@SuppressWarnings("nls")
public class Icons {

	public static final Icons INSTANCE = new Icons();
	public static final ClassLoader CLASS_LOADER = INSTANCE.getClass().getClassLoader();

	private Icons() {

	}

	private static URL getSvg(String name) {
		return CLASS_LOADER.getResource("org/glasspath/common/icons/svg/" + name);
	}

	public static final ImageIcon null_16x16 = new ImageIcon(CLASS_LOADER.getResource("org/glasspath/common/icons/16x16/null_16x16.png"));

	// SVG
	public static final SvgIcon alert = new SvgIcon(getSvg("alert.svg"));
	public static final SvgIcon alertLarge = new SvgIcon(22, 0, getSvg("alert.svg"));
	public static final SvgIcon chevronDown = new SvgIcon(getSvg("chevron-down.svg"));
	public static final SvgIcon chevronLeft = new SvgIcon(getSvg("chevron-left.svg"));
	public static final SvgIcon chevronLeftBlue = new SvgIcon(getSvg("chevron-left.svg"));
	public static final SvgIcon chevronRight = new SvgIcon(getSvg("chevron-right.svg"));
	public static final SvgIcon chevronRightBlue = new SvgIcon(getSvg("chevron-right.svg"));
	public static final SvgIcon chevronUp = new SvgIcon(getSvg("chevron-up.svg"));
	public static final SvgIcon close = new SvgIcon(getSvg("close.svg"));
	public static final SvgIcon closeRed = new SvgIcon(getSvg("close.svg"));
	public static final SvgIcon cogBlue = new SvgIcon(getSvg("cog.svg"));
	public static final SvgIcon cogBlueMask = new OffsetSvgIcon(10, 0, 6, 6, getSvg("cog.svg"));
	public static final SvgIcon contentCopy = new SvgIcon(getSvg("content-copy.svg"));
	public static final SvgIcon contentCopyXLarge = new SvgIcon(36, 0, getSvg("content-copy.svg"));
	public static final SvgIcon contentSave = new SvgIcon(getSvg("content-save.svg"));
	public static final SvgIcon dotsHorizontal = new OffsetSvgIcon(16, 0, 4, 0, getSvg("dots-horizontal.svg"));
	public static final SvgIcon fileDocumentOutline = new SvgIcon(getSvg("file-document-outline.svg"));
	public static final SvgIcon formTextbox = new SvgIcon(getSvg("form-textbox.svg"));
	public static final SvgIcon link = new SvgIcon(getSvg("link.svg"));
	public static final SvgIcon linkGreen = new SvgIcon(getSvg("link.svg"));
	public static final SvgIcon linkOff = new SvgIcon(getSvg("link-off.svg"));
	public static final SvgIcon loupe = new SvgIcon(getSvg("loupe.svg"));
	public static final SvgIcon magnify = new SvgIcon(getSvg("magnify.svg"));
	public static final SvgIcon plus = new SvgIcon(getSvg("plus.svg"));
	public static final SvgIcon redo = new SvgIcon(getSvg("redo.svg"));
	public static final SvgIcon renameBox = new SvgIcon(getSvg("rename-box.svg"));
	public static final SvgIcon renameBoxXLarge = new SvgIcon(36, 0, getSvg("rename-box.svg"));
	public static final SvgIcon squareEditOutline = new SvgIcon(getSvg("square-edit-outline.svg"));
	public static final SvgIcon squareEditOutlineWhite = new SvgIcon(getSvg("square-edit-outline.svg"));
	public static final SvgIcon undo = new SvgIcon(getSvg("undo.svg"));

	static {
		alert.setColorFilter(SvgIcon.ORANGE);
		alertLarge.setColorFilter(SvgIcon.ORANGE);
		chevronLeftBlue.setColorFilter(SvgIcon.BLUE);
		chevronRightBlue.setColorFilter(SvgIcon.BLUE);
		closeRed.setColorFilter(SvgIcon.RED);
		cogBlue.setColorFilter(SvgIcon.BLUE);
		cogBlueMask.setColorFilter(SvgIcon.BLUE);
		linkGreen.setColorFilter(SvgIcon.GREEN);
		plus.setColorFilter(SvgIcon.GREEN);
		redo.setColorFilter(SvgIcon.BLUE);
		renameBox.setColorFilter(SvgIcon.BLUE);
		renameBoxXLarge.setColorFilter(SvgIcon.BLUE);
		squareEditOutline.setColorFilter(SvgIcon.BLUE);
		squareEditOutlineWhite.setColorFilter(SvgIcon.WHITE);
		undo.setColorFilter(SvgIcon.BLUE);
	}

}
