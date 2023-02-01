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

import java.awt.*;
import org.pneditor.petrinet.AbstractNode;
import org.pneditor.petrinet.AbstractPlace;
import org.pneditor.petrinet.PetriNetInterface;
import org.pneditor.util.GraphicsTools;
import org.pneditor.util.GraphicsTools.HorizontalAlignment;
import org.pneditor.util.GraphicsTools.VerticalAlignment;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class GraphicPlace extends GraphicNode {
 
	AbstractPlace place;
	
	public GraphicPlace(final AbstractPlace place, final int x, final int y) {
		super();
		this.place = place;
		setCenter(x,y);
	}
	
    public GraphicPlace() {
    	super();
	}

	@Override
    public boolean containsPoint(final int x, final int y) {
        // Check whether (x,y) is inside this oval, using the
        // mathematical equation of an ellipse.
		final double rx = getWidth() / 2.0;   // horizontal radius of ellipse
		final double ry = getHeight() / 2.0;  // vertical radius of ellipse 
		final double cx = getStart().x + rx;   // x-coord of center of ellipse
		final double cy = getStart().y + ry;    // y-coord of center of ellipse
        return (ry * (x - cx)) * (ry * (x - cx)) + (rx * (y - cy)) * (rx * (y - cy)) <= rx * rx * ry * ry;
    }

    
    public AbstractPlace getPlace() {
        return this.place;
    }
    
    @Override
	public AbstractNode getNode() {
    	return this.place;
    }
	
    @Override
    public void draw(final Graphics g, final PetriNetInterface petriNet) {
		drawPlaceBackground(g);
		drawPlaceBorder(g);
		drawLabel(g);
		drawTokens(g, this.place.getTokens());
	}
    
	protected void drawPlaceBackground(final Graphics g) {
		g.setColor(Color.white);
		g.fillOval(getStart().x, getStart().y, getWidth(), getHeight());
	}

	protected void drawPlaceBorder(final Graphics g) {
		g.setColor(this.color);
		g.drawOval(getStart().x, getStart().y, getWidth() - 1, getHeight() - 1);
	}

	protected void drawTokens(final Graphics g, final int tokens) {
		g.setColor(this.color);
		final int x = getCenter().x;
		final int y = getCenter().y;
		final int tokenSpacing = getWidth() / 5;
		
		if (tokens > 9) {
			GraphicsTools.drawString(g, Integer.toString(tokens), x, y, HorizontalAlignment.CENTER,
					VerticalAlignment.CENTER);
		} else {
			
			if (tokens % 2 == 1) {
				drawTokenAsDot(g, x, y);
				if (tokens > 2) {
					drawTokenAsDot(g, x - tokenSpacing, y + tokenSpacing);
					drawTokenAsDot(g, x + tokenSpacing, y - tokenSpacing);
				}
				if (tokens > 4) {
					drawTokenAsDot(g, x - tokenSpacing, y - tokenSpacing);
					drawTokenAsDot(g, x + tokenSpacing, y + tokenSpacing);
				}
				if (tokens > 6) {
					drawTokenAsDot(g, x - tokenSpacing, y);
					drawTokenAsDot(g, x + tokenSpacing, y);
				}
				if (tokens > 8) {
					drawTokenAsDot(g, x, y - tokenSpacing);
					drawTokenAsDot(g, x, y + tokenSpacing);
				}
				
			} else {
				if (tokens > 1) {
					drawTokenAsDot(g, x - tokenSpacing, y + tokenSpacing);
					drawTokenAsDot(g, x + tokenSpacing, y - tokenSpacing);
				}
				if (tokens > 3) {
					drawTokenAsDot(g, x - tokenSpacing, y - tokenSpacing);
					drawTokenAsDot(g, x + tokenSpacing, y + tokenSpacing);
				}
				if (tokens > 5) {
					drawTokenAsDot(g, x - tokenSpacing, y);
					drawTokenAsDot(g, x + tokenSpacing, y);
				}
				if (tokens == 8) {
					drawTokenAsDot(g, x, y - tokenSpacing);
					drawTokenAsDot(g, x, y + tokenSpacing);
				}
			}
		}
	}

	private void drawTokenAsDot(final Graphics g, final int x, final int y) {
		final int tokenSize = getWidth() / 6;
		g.fillOval(x - tokenSize / 2, y - tokenSize / 2, tokenSize, tokenSize);
	}

	@Override
	public String getLabel() {
		return this.place.getLabel();
	}

	@Override
	public boolean isPlace() {
		return true;
	}
	
	public void setPlace(final AbstractPlace place) {
		this.place = place;
	}
	
	public GraphicPlace getClone(final AbstractPlace placeCloned) {
		final GraphicPlace clone = new GraphicPlace();
		clone.setCenter(this.getCenter());
		clone.setPlace(placeCloned);
		return clone;
	}

	@Override
	public boolean isTransition() {
		return false;
	}
}
