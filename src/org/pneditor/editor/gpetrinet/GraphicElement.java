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

import org.pneditor.petrinet.PetriNetInterface;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public abstract class GraphicElement {
	
	private Point start = new Point();
    private Point end = new Point();
    private Point center = new Point();

    protected Color color = Color.black;
    protected Color highlightColor;


    public Point getStart() {
        if (this.start == null) {
            this.start = new Point();
        }
        return this.start;
    }

    public Point getEnd() {
        if (this.end == null) {
            this.end = new Point();
        }
        return this.end;
    }

    public void setStart(final int x, final int y) {
        if (this.start == null) {
            this.start = new Point();
        }
        this.start.x = x;
        this.start.y = y;
    }

    public void setEnd(final int x, final int y) {
        if (this.end == null) {
            this.end = new Point();
        }
        this.end.x = x;
        this.end.y = y;
    }

    public int getWidth() {
        return Math.abs(getEnd().x - getStart().x);
    }

    public int getHeight() {
        return Math.abs(getEnd().y - getStart().y);
    }

    private int getCenterX() {
        return getStart().x + (getEnd().x - getStart().x) / 2;
    }

    private int getCenterY() {
        return getStart().y + (getEnd().y - getStart().y) / 2;
    }

    public Point getCenter() {
        if (this.center == null) {
            this.center = new Point();
        }
        if (this.center.x != getCenterX()) {
            this.center.x = getCenterX();
        }
        if (this.center.y != getCenterY()) {
            this.center.y = getCenterY();
        }
        return this.center;
    }

    public void setCenter(final int x, final int y) {
        moveBy(x - getCenter().x, y - getCenter().y);
    }

    public void setCenter(final Point center) {
        setCenter(center.x, center.y);
    }

    public void setSize(final int width, final int height) {
    	final Point prevCenter = getCenter();
        setEnd(getStart().x + width, getStart().y + height);
        setCenter(prevCenter.x, prevCenter.y);
    }

    public void moveBy(final int dx, final int dy) {
        setStart(getStart().x + dx, getStart().y + dy);
        setEnd(getEnd().x + dx, getEnd().y + dy);
    }

    public void setColor(final Color color) {
        this.color = color;
    }

    public Color getColor() {
        return this.color;
    }
    
    public void setHighlightColor(final Color color) {
    	this.highlightColor = color;
    }
    
    public Color getHighlightColor() {
    	return highlightColor;
    }

    public boolean containsPoint(final int x, final int y) {
    	final int l = Math.min(getStart().x, getEnd().x);
    	final int t = Math.min(getStart().y, getEnd().y);
    	final int w = Math.abs(getWidth());
    	final int h = Math.abs(getHeight());
        return x >= l && x < l + w && y >= t && y < t + h;

    }
	
	public abstract void draw(Graphics g, PetriNetInterface petriNet);

	public abstract boolean isNode();
	
	public abstract boolean isPlace();
	
	public abstract boolean isTransition();

}
