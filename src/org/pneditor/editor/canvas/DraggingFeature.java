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

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import org.pneditor.editor.Root;
import org.pneditor.editor.commands.MoveElementCommand;
import org.pneditor.editor.commands.MoveElementsCommand;
import org.pneditor.editor.gpetrinet.GraphicElement;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
class DraggingFeature implements Feature {

    private final Canvas canvas;
    private final Root root;

    DraggingFeature(final Canvas canvas) {
        this.canvas = canvas;
        this.root = canvas.getRoot();
    }

    private GraphicElement draggedElement;
    private Point deltaPosition;
    private int prevDragX;  // During dragging, these record the x and y coordinates of the
    private int prevDragY;  //    previous position of the mouse.

    @Override
	public void mousePressed(final MouseEvent event) {
    	final boolean doubleclick = event.getClickCount() == 2;
        if (!doubleclick) {
        	final int mouseButton = event.getButton();
            if (mouseButton == MouseEvent.BUTTON1
                    && this.root.getClickedElement() != null
                    && (this.root.isSelectedToolSelect()
                    || this.root.isSelectedToolPlace()
                    || this.root.isSelectedToolTransition())
                    && this.root.getClickedElement().isNode()) {
                if (!this.root.getSelection().contains(this.root.getClickedElement())) {
                    this.root.getSelection().clear();
                }
                final int x = event.getX();
                final int y = event.getY();
                this.draggedElement = this.root.getGraphicPetriNet().getElementByXY(x, y);
                this.deltaPosition = new Point();
                this.prevDragX = x;
                this.prevDragY = y;
            }
        }
    }

    @Override
	public void mouseDragged(final int x, final int y) {
        if (this.draggedElement != null) {
            doTheMoving(x, y);
            this.canvas.repaint();  // redraw canvas to show shape in new position
            this.deltaPosition.translate(x - this.prevDragX, y - this.prevDragY);
            this.prevDragX = x;
            this.prevDragY = y;
        }
    }

    @Override
	public void mouseReleased(final int x, final int y) {
        if (this.draggedElement != null) {
            doTheMoving(x, y);
            this.deltaPosition.translate(x - this.prevDragX, y - this.prevDragY);
            saveTheMoving();
            this.canvas.repaint();
            this.draggedElement = null;  // Dragging is finished.
        }
    }

    private void doTheMoving(final int mouseX, final int mouseY) {
        if (this.root.getSelection().isEmpty()) {
            this.draggedElement.moveBy(mouseX - this.prevDragX, mouseY - this.prevDragY);
        } else {
            for (final GraphicElement selectedElement : this.root.getSelection()) {
                selectedElement.moveBy(mouseX - this.prevDragX, mouseY - this.prevDragY);
            }
        }
    }

    private void saveTheMoving() {
        if (!this.deltaPosition.equals(new Point(0, 0))) {
            if (this.root.getSelection().isEmpty()) {
                this.draggedElement.moveBy(-this.deltaPosition.x, -this.deltaPosition.y);  //move back to original position
                this.root.getUndoManager().executeCommand(new MoveElementCommand(this.draggedElement, this.deltaPosition));
            } else {
                for (final GraphicElement selectedElement : this.root.getSelection()) {
                    selectedElement.moveBy(-this.deltaPosition.x, -this.deltaPosition.y); //move back to original positions
                }
                this.root.getUndoManager().executeCommand(new MoveElementsCommand(this.root.getSelection().getElements(), this.deltaPosition));
            }
        }
    }

    @Override
	public void setCursor(final int x, final int y) {

        if (this.root.isSelectedToolSelect()
                || this.root.isSelectedToolPlace()
                || this.root.isSelectedToolTransition()) {
        	
        	final GraphicElement element = this.root.getGraphicPetriNet().getElementByXY(x, y);
            if (element != null && element.isNode()) {
                this.canvas.setAlternativeCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }
        }
    }

    @Override
	public void drawForeground(final Graphics g) {
    }

    @Override
	public void drawBackground(final Graphics g) {
    }

    @Override
	public void setHoverEffects(final int x, final int y) {
    }

    @Override
	public void drawMainLayer(final Graphics g) {
    }

    @Override
	public void mouseMoved(final int x, final int y) {
    }
}
