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

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import org.pneditor.editor.Root;
import org.pneditor.editor.commands.SetEdgeZigzagPointCommand;
import org.pneditor.editor.gpetrinet.GraphicArc;
import org.pneditor.editor.gpetrinet.GraphicElement;
import org.pneditor.util.Colors;
import org.pneditor.util.GraphicsTools;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
class EdgeZigzagFeature implements Feature {

    private final Canvas canvas;
    private final Root root;
    
    private Point activeBreakPoint;
    private boolean started;
    private final VisualHandle visualHandle = new VisualHandle();
    private final List<VisualHandle> foregroundVisualElements = new ArrayList<>();

    private Point startingMouseLocation;
    private List<Point> oldBreakPoints;
    private GraphicArc arc;

    EdgeZigzagFeature(final Canvas canvas) {
        this.canvas = canvas;
        this.root = canvas.getRoot();
        this.visualHandle.setColor(Colors.POINTING);
        this.visualHandle.setSize(GraphicArc.NEARTOLERANCE, GraphicArc.NEARTOLERANCE);
    }


    @Override
	public void mousePressed(final MouseEvent event) {
    	final int mouseButton = event.getButton();

        if (mouseButton == MouseEvent.BUTTON1
                && this.root.getClickedElement() != null
                && (this.root.isSelectedToolSelect()
                || this.root.isSelectedToolPlace()
                || this.root.isSelectedToolTransition()
                || this.root.isSelectedToolArc()
                || this.root.isSelectedToolToken() && !(this.root.getClickedElement().isPlace()))
                && !this.root.getClickedElement().isNode()) {
            if (!this.root.getSelection().contains(this.root.getClickedElement())) {
                this.root.getSelection().clear();
            }
            final int x = event.getX();
            final int y = event.getY();
            
            this.arc = (GraphicArc) this.root.getGraphicPetriNet().getElementByXY(x, y);

            this.oldBreakPoints = this.arc.getBreakPointsCopy();
            this.startingMouseLocation = new Point(x, y);
            this.activeBreakPoint = this.arc.addOrGetBreakPoint(new Point(this.startingMouseLocation));
            this.started = true;
        }
    }

    @Override
	public void mouseDragged(final int x, final int y) {
        if (this.started) {
            this.activeBreakPoint.move(x, y);
            this.canvas.repaint();
        }
    }

    @Override
	public void mouseReleased(final int x, final int y) {
        if (this.started) {
            this.arc.cleanupUnecessaryBreakPoints();

            boolean change = false;
            if (this.oldBreakPoints.size() != this.arc.getBreakPoints().size()) {
                change = true;
            } else {
                for (int i = 0; i < this.arc.getBreakPoints().size(); i++) {
                    if (!this.arc.getBreakPoints().get(i).equals(this.oldBreakPoints.get(i))) {
                        change = true;
                        break;
                    }
                }
            }
            if (change) {
                this.arc.setBreakPoints(this.oldBreakPoints);
                final Point targetLocation = new Point(x, y);
                this.root.getUndoManager().executeCommand(new SetEdgeZigzagPointCommand(this.arc, this.startingMouseLocation, targetLocation));
            }
            this.started = false;
        }
    }

    @Override
	public void setHoverEffects(final int x, final int y) {
        if (this.root.isSelectedToolSelect()
                || this.root.isSelectedToolPlace()
                || this.root.isSelectedToolTransition()
                || this.root.isSelectedToolArc()
                || this.root.isSelectedToolToken()) {
        	final GraphicElement element = this.root.getGraphicPetriNet().getElementByXY(x, y);
            boolean drawHandle = false;
            if (element != null && !element.isNode()) {
            	final GraphicArc anArc = (GraphicArc) element;
                for (final Point breakPoint : anArc.getBreakPoints()) {
                    final Point mousePos = new Point(x, y);
                    if (GraphicsTools.isPointNearPoint(breakPoint, mousePos, GraphicArc.NEARTOLERANCE)) {
                        if (!this.foregroundVisualElements.contains(this.visualHandle)) {
                            this.foregroundVisualElements.add(this.visualHandle);
                        }
                        this.visualHandle.setCenter(breakPoint.x, breakPoint.y);
                        drawHandle = true;

                        break;
                    }
                }
            }
            if (!drawHandle) {
                this.foregroundVisualElements.remove(this.visualHandle);
            }

            if (element != null) {
                this.canvas.getHighlightedElements().add(element);
                element.setHighlightColor(Colors.POINTING);
                this.canvas.repaint();
            }
        }
    }

    @Override
	public void drawForeground(final Graphics g) {
        for (final VisualHandle element : this.foregroundVisualElements) {
            element.draw(g);
        }
    }

    @Override
	public void setCursor(final int x, final int y) {
    }

    @Override
	public void drawBackground(final Graphics g) {
    }

    @Override
	public void drawMainLayer(final Graphics g) {
    }

    @Override
	public void mouseMoved(final int x, final int y) {
    }
}
