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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import org.pneditor.editor.Root;
import org.pneditor.editor.gpetrinet.GraphicElement;
import org.pneditor.util.Colors;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
class ClickFeature implements Feature {

    private final Root root;

    private Color previousColor;
    
    ClickFeature(final Canvas canvas) {
        this.root = canvas.getRoot();
    }


    @Override
	public void drawBackground(final Graphics g) {
        final GraphicElement element = this.root.getClickedElement();
        if (element != null) {
            this.previousColor = element.getColor();
            element.setColor(Colors.SINGLESELECTED);
        }
    }

    @Override
	public void drawForeground(final Graphics g) {
        final GraphicElement element = this.root.getClickedElement();
        if (element != null) {
            element.setColor(this.previousColor);
        }
    }

    @Override
	public void setHoverEffects(final int x, final int y) {
    }

    @Override
	public void mousePressed(final MouseEvent event) {
    }

    @Override
	public void mouseDragged(final int x, final int y) {
    }

    @Override
	public void mouseReleased(final int x, final int y) {
    }

    @Override
	public void setCursor(final int x, final int y) {
    }

    @Override
	public void drawMainLayer(final Graphics g) {
    }

    @Override
	public void mouseMoved(final int x, final int y) {
    }
}
