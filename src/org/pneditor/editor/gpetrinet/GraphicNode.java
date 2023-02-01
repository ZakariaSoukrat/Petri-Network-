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
package org.pneditor.editor.gpetrinet;

import java.awt.Graphics;
import org.pneditor.petrinet.AbstractNode;
import org.pneditor.util.GraphicsTools;
import org.pneditor.util.GraphicsTools.HorizontalAlignment;
import org.pneditor.util.GraphicsTools.VerticalAlignment;

/**
 * Transition, Place, ReferencePlace, Subnet are subclasses of Node
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public abstract class GraphicNode extends GraphicElement {


	public GraphicNode() {
		super();
        setSize(32, 32);
    }
    
	protected void drawLabel(final Graphics g) {
		if (getLabel() != null && !getLabel().equals("")) {
			GraphicsTools.drawString(g, getLabel(), getCenter().x, getEnd().y,
					HorizontalAlignment.CENTER, VerticalAlignment.TOP);
		}
	}
	
	public abstract String getLabel();
	
    public abstract AbstractNode getNode();
    
    @Override
	public boolean isNode() {
    	return true;
    }
}

