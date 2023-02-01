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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.pneditor.petrinet.AbstractArc;
import org.pneditor.petrinet.PetriNetInterface;
import org.pneditor.petrinet.ResetArcMultiplicityException;
import org.pneditor.util.GraphicsTools;
import org.pneditor.util.GraphicsTools.HorizontalAlignment;
import org.pneditor.util.GraphicsTools.VerticalAlignment;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class GraphicArc extends GraphicElement {

	private AbstractArc arc;
	private final List<Point> breakPoints = new LinkedList<>();
	private GraphicNode destination;
	private GraphicNode source;

	public GraphicArc() {
		super();
	}

	public GraphicArc(final AbstractArc arc) {
		super();
		this.arc = arc;
	}

	public GraphicArc getClone(final AbstractArc arcToClone, final GraphicNode newSource, final GraphicNode newDestination) {
		final GraphicArc gArcClone = new GraphicArc(arcToClone);
		gArcClone.source = newSource;
		gArcClone.destination = newDestination;
		return gArcClone;
	}

	public AbstractArc getArc() {
		return this.arc;
	}

	@Override
	public boolean isNode() {
		return false;
	}

	public List<Point> getBreakPoints() {
		return Collections.unmodifiableList(this.breakPoints);
	}

	public List<Point> getBreakPointsCopy() {
		final List<Point> newBreakPoints = new LinkedList<>();
		for (final Point breakPoint : this.breakPoints) {
			newBreakPoints.add(breakPoint.getLocation()); // getLocation because Point is mutable
		}
		return newBreakPoints;
	}

	public void setBreakPoints(final List<Point> breakPoints) {
		this.breakPoints.clear();
		for (final Point breakPoint : breakPoints) {
			this.breakPoints.add(breakPoint.getLocation()); // getLocation because Point is mutable
		}
	}

	public void draw(final Graphics g) {
		this.color = Color.BLACK;
		g.setColor(this.color);
		drawSegmentedLine(g);
		final Point arrowTip = computeArrowTipPoint();
		if (this.arc.isReset()) {
			drawArrowDouble(g, arrowTip);
		} else {
			try {
				if (this.arc.getMultiplicity() >= 2) {
					drawMultiplicityLabel(g, arrowTip, this.arc.getMultiplicity());
				}
			} catch (ResetArcMultiplicityException e) {
				// not happening
			}
			if (this.arc.isInhibitory()) {
				drawCircle(g, arrowTip);
			} else {
				drawArrow(g, arrowTip);
			}
		}

	}

	@Override
	public void draw(final Graphics g, final PetriNetInterface petriNet) {
		draw(g);
	}

	protected void drawArrow(final Graphics g, final Point arrowTip) {
		final Point lastBreakPoint = getLastBreakPoint();
		GraphicsTools.drawArrow(g, lastBreakPoint.x, lastBreakPoint.y, arrowTip.x, arrowTip.y);
	}

	protected void drawArrowDouble(final Graphics g, final Point arrowTip) {
		final Point lastBreakPoint = getLastBreakPoint();
		/*
		 * int dx =lastBreakPoint.x - arrowTip.x; int dy =lastBreakPoint.y - arrowTip.y;
		 * int px = 8; int py = (int) ((dy/dx) * px); GraphicsTools.drawArrow(g,
		 * lastBreakPoint.x, lastBreakPoint.y, arrowTip.x, arrowTip.y);
		 */
		GraphicsTools.drawArrowDouble(g, lastBreakPoint.x, lastBreakPoint.y, arrowTip.x, arrowTip.y);
	}

	protected void drawCircle(final Graphics g, final Point arrowTip) {
		final Point lastBreakPoint = getLastBreakPoint();
		GraphicsTools.drawCircle(g, lastBreakPoint.x, lastBreakPoint.y, arrowTip.x, arrowTip.y);
	}

	protected void drawMultiplicityLabel(final Graphics g, final Point arrowTip, final int multiplicity) {
		final Point labelPoint = getLabelPoint(arrowTip);
		GraphicsTools.drawString(g, Integer.toString(multiplicity), labelPoint.x, labelPoint.y,
				HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM);
	}

	protected void drawSegmentedLine(final Graphics g) {
		g.setColor(this.color);
		Point previous = getStart();
		for (final Point breakPoint : getBreakPoints()) {
			g.drawLine(previous.x, previous.y, breakPoint.x, breakPoint.y);
			previous = breakPoint;
		}
		g.drawLine(previous.x, previous.y, getEnd().x, getEnd().y);
	}

	protected final Point computeArrowTipPoint() {
		Point arrowTip = new Point(getEnd());
		if (getDestination() != null) { // Thanks to http://www.cs.unc.edu/~mcmillan/comp136/Lecture6/Lines.html
			int x0 = getLastBreakPoint().x;
			int y0 = getLastBreakPoint().y;
			final int x1 = getEnd().x;
			final int y1 = getEnd().y;

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

	protected Point getLastBreakPoint() {
		Point last = getStart();
		for (final Point breakPoint : this.breakPoints) {
			last = breakPoint;
		}
		return last;
	}

	protected Point getLabelPoint(final Point arrowTip) {
		final Point labelPoint = new Point();
		if (this.breakPoints.isEmpty()) {
			labelPoint.x = getStart().x + (arrowTip.x - getStart().x) * 2 / 3;
			labelPoint.y = getStart().y + (arrowTip.y - getStart().y) * 2 / 3 - 3;
		} else {
			final Point lastBreakPoint = this.breakPoints.get(this.breakPoints.size() - 1);
			labelPoint.x = lastBreakPoint.x + (arrowTip.x - lastBreakPoint.x) * 1 / 2;
			labelPoint.y = lastBreakPoint.y + (arrowTip.y - lastBreakPoint.y) * 1 / 2 - 3;
		}
		return labelPoint;
	}

	public GraphicNode getDestination() {
		return this.destination;
	}

	public void setDestination(final GraphicNode destination) {
		this.destination = destination;
	}

	public GraphicNode getSource() {
		return this.source;
	}

	public void setSource(final GraphicNode source) {
		this.source = source;
	}

	public static final int NEARTOLERANCE = 10;

	@Override
	public boolean containsPoint(final int x, final int y) {
		final Point testPos = new Point(x, y);
		Point previous = getStart();
		for (final Point breakPoint : this.breakPoints) {
			if (GraphicsTools.isPointNearSegment(previous, breakPoint, testPos, NEARTOLERANCE)) {
				return true;
			}
			previous = breakPoint;
		}
		return GraphicsTools.isPointNearSegment(previous, getEnd(), testPos, NEARTOLERANCE);
	}

	public Point addOrGetBreakPoint(final Point newPoint) {
		for (final Point breakPoint : this.breakPoints) {
			if (GraphicsTools.isPointNearPoint(newPoint, breakPoint, NEARTOLERANCE)) {
				return breakPoint;
			}
		}

		if (this.breakPoints.isEmpty()) {
			this.breakPoints.add(newPoint);
		} else {
			Point previous = getStart();
			for (int i = 0; i < this.breakPoints.size(); i++) {
				if (GraphicsTools.isPointNearSegment(previous, this.breakPoints.get(i), newPoint, NEARTOLERANCE)) {
					this.breakPoints.add(i, newPoint);
					return newPoint;
				}
				previous = this.breakPoints.get(i);
			}
			if (GraphicsTools.isPointNearSegment(previous, getEnd(), newPoint, NEARTOLERANCE)) {
				this.breakPoints.add(newPoint);
			}
		}
		return newPoint;
	}

	public void addDistantBreakPointToEnd(final Point newPoint) {
		this.breakPoints.add(newPoint);
	}

	public void addDistantBreakPointToBeginning(final Point newPoint) {
		this.breakPoints.add(0, newPoint);
	}

	public void cleanupUnecessaryBreakPoints() {
		Point previous = getStart();
		for (int i = 0; i < this.breakPoints.size(); i++) {
			final Point current = this.breakPoints.get(i);
			final Point next = i < (this.breakPoints.size() - 1) ? this.breakPoints.get(i + 1) : getEnd();
			final int tolerance = Math.round(0.1f * (float) previous.distance(next));
			if (GraphicsTools.isPointNearSegment(previous, next, current, tolerance)) {
				this.breakPoints.remove(i--);
			} else {
				previous = this.breakPoints.get(i);
			}
		}
	}

	@Override
	public Point getStart() {
		return getSource() == null ? super.getStart() : getSource().getCenter();
	}

	@Override
	public Point getEnd() {
		return getDestination() == null ? super.getEnd() : getDestination().getCenter();
	}

	@Override
	public boolean isPlace() {
		return false;
	}

	@Override
	public boolean isTransition() {
		return false;
	}

	public void setArc(final AbstractArc arc) {
		this.arc = arc;
	}

}
