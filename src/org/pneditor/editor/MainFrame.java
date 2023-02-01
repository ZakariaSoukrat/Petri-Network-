/*
 * Copyright (C) 2008-2010 Martin Riesz <riesz.martin at gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.pneditor.editor;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import logger.PNEditorLogger;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	MainFrame(final String title) {
		super(title);
		UIManager.getDefaults().put("ToolTip.hideAccelerator", Boolean.TRUE);
		UIManager.put("swing.boldMetal", Boolean.FALSE);
		UIManager.put("MenuItem.Foreground", Color.WHITE);
		final String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
		try {
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			PNEditorLogger.infoLogs("Couldn't load the Look and Feel");
		}
	}
}
