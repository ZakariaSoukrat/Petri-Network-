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
import javax.swing.JPopupMenu;
import org.pneditor.editor.Root;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class PopupMenuFeature implements Feature {

    private final Canvas canvas;
    private final Root root;

    public PopupMenuFeature(final Canvas canvas) {
        this.canvas = canvas;
        this.root = canvas.getRoot();
    }

    @Override
	public void mousePressed(final MouseEvent event) {
    	final int mouseButton = event.getButton();
        if (mouseButton == MouseEvent.BUTTON3) {
        	final int realX = event.getX() + this.canvas.getTranslationX();
        	final int realY = event.getY() + this.canvas.getTranslationY();
            
            if (this.root.getClickedElement() != null
                    && (this.root.isSelectedToolSelect()
                    || this.root.isSelectedToolPlace()
                    || this.root.isSelectedToolTransition()
                    || this.root.isSelectedToolArc()
                    || (this.root.isSelectedToolToken() && !this.root.getClickedElement().isPlace()))) { 
            	//if token selected, right click disallowed on Place (because it's removing tokens normally)
                if (this.root.getClickedElement().isPlace()) {
                    showPopup(this.root.getPlacePopup(), realX, realY);
                    if (!this.root.getSelection().contains(this.root.getClickedElement())) {
                        this.root.getSelection().clear();
                    }
                } else if (this.root.getClickedElement().isTransition()) {
                    showPopup(this.root.getTransitionPopup(), realX, realY);
                    if (!this.root.getSelection().contains(this.root.getClickedElement())) {
                        this.root.getSelection().clear();
                    }
                } else if (!this.root.getClickedElement().isNode()) {
                    showPopup(this.root.getArcPopup(), realX, realY);
                    if (!this.root.getSelection().contains(this.root.getClickedElement())) {
                        this.root.getSelection().clear();
                    }
                }
            }

            if (this.root.getClickedElement() == null
                    && this.root.isSelectedToolSelect()) {
                showPopup(this.root.getCanvasPopup(), realX, realY);
            }
        }
    }

    private void showPopup(final JPopupMenu popupMenu, final int clickedX, final int clickedY) {
        popupMenu.show(this.canvas, clickedX - 10, clickedY - 2);
    }

    @Override
	public void drawForeground(final Graphics g) {
    }

    @Override
	public void drawBackground(final Graphics g) {
    }

    @Override
	public void mouseDragged(final int x, final int y) {
    }

    @Override
	public void mouseReleased(final int x, final int y) {
    }

    @Override
	public void setHoverEffects(final int x, final int y) {
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
