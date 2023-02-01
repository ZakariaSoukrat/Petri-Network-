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

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import org.pneditor.editor.Root;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
class PanningFeature implements Feature {

    private final Canvas canvas;
    private final Root root;

    private int prevDragX;
    private int prevDragY;
    private boolean panning;

    PanningFeature(final Canvas canvas) {
        this.canvas = canvas;
        this.root = canvas.getRoot();
    }

    @Override
	public void mousePressed(final MouseEvent event) {

    	final int mouseButton = event.getButton();

        if (mouseButton == MouseEvent.BUTTON2
                || mouseButton == MouseEvent.BUTTON1 && event.isControlDown()) {

        	final int realX = event.getX() + this.canvas.getTranslationX();
        	final int realY = event.getY() + this.canvas.getTranslationY();

            this.prevDragX = realX;
            this.prevDragY = realY;
            this.panning = true;
        }
    }

    @Override
	public void mouseDragged(final int x, final int y) {
        if (this.panning) {
        	final int realX = x + this.canvas.getTranslationX();
        	final int realY = y + this.canvas.getTranslationY();

            doThePanning(realX, realY);
            this.canvas.repaint();
            this.prevDragX = realX;
            this.prevDragY = realY;
        }
    }

    @Override
	public void mouseReleased(final int x, final int y) {
        if (this.panning) {
        	final int realX = x + this.canvas.getTranslationX();
        	final int realY = y + this.canvas.getTranslationY();

            doThePanning(realX, realY);
            this.canvas.repaint();
            this.panning = false;
        }
    }

    private void doThePanning(final int mouseX, final int mouseY) {
    	final Point viewTranslation = this.root.getGraphicPetriNet().getViewTranslation();
        viewTranslation.translate(mouseX - this.prevDragX, mouseY - this.prevDragY);
        this.root.getGraphicPetriNet().setViewTranslation(viewTranslation);
    }

    @Override
	public void setCursor(final int x, final int y) {
        if (this.panning) {
            this.canvas.setAlternativeCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
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
