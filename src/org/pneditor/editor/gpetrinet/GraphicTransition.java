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

import java.awt.Color;
import java.awt.Graphics;

import org.pneditor.petrinet.AbstractNode;
import org.pneditor.petrinet.AbstractTransition;
import org.pneditor.petrinet.PetriNetInterface;
/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class GraphicTransition extends GraphicNode {

	AbstractTransition transition;
    
    public GraphicTransition(final AbstractTransition transition , final int x, final int y){
    	this.transition = transition;
    	setCenter(x,y);
    }
    
    public GraphicTransition() {
		// TODO Auto-generated constructor stub
	}

	public AbstractTransition getTransition() {
    	return this.transition;
    }
    
    @Override
	public AbstractNode getNode() {
    	return this.transition;
    }
    
	public void draw(final Graphics g) {
		g.setColor(Color.white);
		g.fillRect(getStart().x, getStart().y, getWidth(), getHeight());
		g.setColor(this.color);
		g.drawRect(getStart().x, getStart().y, getWidth() - 1, getHeight() - 1);
		drawLabel(g);
	}

	@Override
	public void draw(final Graphics g, final PetriNetInterface petriNet) {
		draw(g);
	}

	@Override
	public String getLabel() {
		return this.transition.getLabel();
	}

	@Override
	public boolean isPlace() {
		return false;
	}
	
	public void setTransition(final AbstractTransition transition) {
		this.transition = transition;
	}
	
	public GraphicTransition getClone(final AbstractTransition transitionCloned) {
		final GraphicTransition clone = new GraphicTransition();
		clone.setCenter(this.getCenter());
		clone.setTransition(transitionCloned);
		return clone;
	}

	@Override
	public boolean isTransition() {
		return true;
	}
}
