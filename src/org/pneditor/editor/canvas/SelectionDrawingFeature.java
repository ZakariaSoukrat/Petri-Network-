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
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;
import org.pneditor.editor.Root;
import org.pneditor.editor.gpetrinet.GraphicElement;
import org.pneditor.util.Colors;


/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
class SelectionDrawingFeature implements Feature {

    private final Canvas canvas;
    private final Root root;

    private boolean selecting;
    private final VisualSelection visualSelection = new VisualSelection();
    private final Set<GraphicElement> previousSelection = new HashSet<>();
    
    SelectionDrawingFeature(Canvas canvas) {
        this.canvas = canvas;
        this.root = canvas.getRoot();
    }

    @Override
	public void mousePressed(final MouseEvent event) {

    	final int mouseButton = event.getButton();

        if (mouseButton == MouseEvent.BUTTON1
                && this.root.getClickedElement() == null
                && this.root.isSelectedToolSelect()) {
        	final int x = event.getX();
        	final int y = event.getY();
            this.selecting = true;
            this.visualSelection.setStart(x, y);
            this.visualSelection.setEnd(x, y);
            this.canvas.repaint();
            if (event.isShiftDown()) {
                this.previousSelection.addAll(this.root.getSelection().getElements());
            } else {
                this.root.getSelection().clear();
                this.previousSelection.clear();
            }
        }
    }

    @Override
	public void mouseDragged(final int x, final int y) {
        if (this.selecting) {
            this.visualSelection.setEnd(x, y);
            this.canvas.repaint();
        }
    }

    @Override
	public void mouseReleased(final int x, final int y) {
        if (this.selecting) {
            this.selecting = false;
            this.canvas.repaint();
        }
    }

    @Override
	public void setHoverEffects(final int x, final int y) {
        for (final GraphicElement selectedElement : this.root.getSelection()) {
            this.canvas.getHighlightedElements().add(selectedElement);
            selectedElement.setHighlightColor(Colors.SELECTED);
        }

        if (this.selecting) {
            this.root.getSelection().clear();
            this.root.getSelection().addAll(this.previousSelection);
            for (final GraphicElement visualElement : this.root.getGraphicPetriNet().getElements()) {
                if (this.visualSelection.containsPoint(visualElement.getCenter().x, visualElement.getCenter().y)) {
                    addElementToSelection(visualElement);
                }
            }
            this.canvas.repaint();
        }
    }

    private void addElementToSelection(final GraphicElement element) {
        this.canvas.getHighlightedElements().add(element);
        element.setHighlightColor(Colors.SELECTED);

        this.root.getSelection().add(element);
    }

    @Override
	public void drawForeground(final Graphics g) {
        if (this.selecting) {
            this.visualSelection.draw(g);
        }
    }

    @Override
	public void drawBackground(final Graphics g) {
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
