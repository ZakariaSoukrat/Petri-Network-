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
import java.awt.Point;

import org.pneditor.petrinet.PetriNetInterface;
import org.pneditor.util.GraphicsTools;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class PreviewArc extends GraphicElement {

	private GraphicNode destination;
	private GraphicNode source;

	public PreviewArc(GraphicNode source) {
		setSource(source);
		setStart(source.getCenter().x, source.getCenter().y);
		setEnd(source.getCenter().x, source.getCenter().y);
	}

	@Override
	public final boolean isNode() {
		return false;
	}

	public final void draw(Graphics g) {
		this.color = Color.BLACK;
		g.setColor(this.color);
		drawSegmentedLine(g);
		Point arrowTip = computeArrowTipPoint();
		drawArrow(g, arrowTip);

	}

	@Override
	public final void draw(Graphics g, PetriNetInterface petriNet) {
		draw(g);
	}

	protected final void drawArrow(Graphics g, Point arrowTip) {
		GraphicsTools.drawArrow(g, getStart().x, getStart().y, arrowTip.x, arrowTip.y);
	}

	protected final void drawSegmentedLine(Graphics g) {
		g.setColor(this.color);
		Point previous = getStart();
		g.drawLine(previous.x, previous.y, getEnd().x, getEnd().y);
	}

	protected final Point computeArrowTipPoint() {
		Point arrowTip = new Point(getEnd());
		if (getDestination() != null) { // Thanks to http://www.cs.unc.edu/~mcmillan/comp136/Lecture6/Lines.html
			int x0 = getStart().x;
			int y0 = getStart().y;
			int x1 = getEnd().x;
			int y1 = getEnd().y;

			int dy = y1 - y0;
			int dx = x1 - x0;
			int stepx, stepy;

			if (dy < 0) {
				dy = -dy;
				stepy = -1;
			} else {
				stepy = 1;
			}
			if (dx < 0) {
				dx = -dx;
				stepx = -1;
			} else {
				stepx = 1;
			}
			dy <<= 1;
			dx <<= 1;

			if (dx > dy) {
				int fraction = dy - (dx >> 1);
				while (x0 != x1) {
					if (fraction >= 0) {
						y0 += stepy;
						fraction -= dx;
					}
					x0 += stepx;
					fraction += dy;
					if (getDestination().containsPoint(x0, y0)) {
						return arrowTip;
					}
					arrowTip = new Point(x0, y0);
				}
			} else {
				int fraction = dx - (dy >> 1);
				while (y0 != y1) {
					if (fraction >= 0) {
						x0 += stepx;
						fraction -= dy;
					}
					y0 += stepy;
					fraction += dx;
					if (getDestination().containsPoint(x0, y0)) {
						return arrowTip;
					}
					arrowTip = new Point(x0, y0);
				}
			}
		}
		return arrowTip;
	}

	public final GraphicNode getDestination() {
		return this.destination;
	}

	public final void setDestination(GraphicNode destination) {
		this.destination = destination;
	}

	public final GraphicNode getSource() {
		return this.source;
	}

	public final void setSource(GraphicNode source) {
		this.source = source;
	}

	@Override
	public final Point getStart() {
		return getSource() != null ? getSource().getCenter() : super.getStart();
	}

	@Override
	public final Point getEnd() {
		return getDestination() != null ? getDestination().getCenter() : super.getEnd();
	}

	@Override
	public final boolean isPlace() {
		return false;
	}

	@Override
	public final boolean isTransition() {
		return false;
	}

}
