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


import org.pneditor.editor.gpetrinet.GraphicElement;
import org.pneditor.petrinet.PetriNetInterface;

import java.awt.Graphics;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */

class VisualHandle extends GraphicElement {

    public void draw(final Graphics g) {
        g.setColor(this.color);
        g.drawRect(Math.min(getStart().x, getEnd().x), Math.min(getStart().y, getEnd().y), getWidth(), getHeight());
    }

	@Override
	public void draw(final Graphics g, final PetriNetInterface petriNet) {
		draw(g);
	}
	
	@Override
	public boolean isNode() {
		return false;
	}

	@Override
	public boolean isPlace() {
		return false;
	}

	@Override
	public boolean isTransition() {
		return false;
	}
}
