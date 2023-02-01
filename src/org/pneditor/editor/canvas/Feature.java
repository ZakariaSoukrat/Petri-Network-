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
package org.pneditor.editor.canvas;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public interface Feature {

    void drawForeground(Graphics g);

    void drawMainLayer(Graphics g);

    void drawBackground(Graphics g);

    void mousePressed(MouseEvent event);

    void mouseDragged(int x, int y);

    void mouseReleased(int x, int y);

    void mouseMoved(int x, int y);

    void setHoverEffects(int x, int y);

    void setCursor(int x, int y);
}
