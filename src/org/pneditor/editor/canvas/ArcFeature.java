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
import java.util.ArrayList;
import java.util.List;

import org.pneditor.editor.Root;
import org.pneditor.editor.commands.AddArcCommand;
import org.pneditor.editor.gpetrinet.*;
import org.pneditor.util.Colors;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
class ArcFeature implements Feature {

	private final Canvas canvas;
	private final Root root;

	private GraphicNode sourceElement;
	private PreviewArc connectingArc;
	private final List<GraphicElement> backgroundElements = new ArrayList<>();
	private boolean started;
	private GraphicPetriNet graphicPetriNet;
	
	ArcFeature(final Canvas canvas) {
		this.canvas = canvas;
		this.root = canvas.getRoot();
	}

	@Override
	public void mousePressed(final MouseEvent event) {
		final int mouseButton = event.getButton();

		if (mouseButton == MouseEvent.BUTTON1 && this.root.isSelectedToolArc() && this.root.getClickedElement() != null
				&& this.root.getClickedElement().isNode() && !this.started) {

			final int x = event.getX();
			final int y = event.getY();

			this.graphicPetriNet = this.root.getGraphicPetriNet();
			this.sourceElement = (GraphicNode) this.graphicPetriNet.getElementByXY(x, y);
			this.connectingArc = new PreviewArc(this.sourceElement);
			this.backgroundElements.add(this.connectingArc);
			this.started = true;
		}
	}

	@Override
	public void mouseDragged(final int x, final int y) {
		if (this.root.getGraphicPetriNet() != this.graphicPetriNet) {
			cancelDragging();
		}
		if (this.started) {
			final GraphicElement element = this.root.getGraphicPetriNet().getElementByXY(x, y);
			final GraphicNode targetElement = element != null && element.isNode() ? (GraphicNode) element : null;

			if (targetElement != null && (this.sourceElement.isPlace() && !targetElement.isPlace()
					|| !this.sourceElement.isPlace() && targetElement.isPlace())) {
				this.connectingArc.setEnd(targetElement.getCenter().x, targetElement.getCenter().y);
				this.connectingArc.setDestination(targetElement);
			} else {
				this.connectingArc.setEnd(x, y);
				this.connectingArc.setSource(null);
				this.connectingArc.setDestination(null);
			}
			this.root.repaintCanvas();
		}
	}

	@Override
	public void mouseMoved(final int x, final int y) {
		mouseDragged(x, y);
	}

	@Override
	public void mouseReleased(final int x, final int y) {
		if (this.root.getGraphicPetriNet() != this.graphicPetriNet) {
			cancelDragging();
		}

		if (this.started) {
			final GraphicElement element = this.root.getGraphicPetriNet().getElementByXY(x, y);
			this.connectingArc.setEnd(x, y);
			final GraphicNode targetElement = element != null && element.isNode() ? (GraphicNode) element : null;

			if (this.sourceElement != targetElement) {
				if (targetElement != null) {
					if (this.sourceElement.isPlace() && !targetElement.isPlace()
							|| !this.sourceElement.isPlace() && targetElement.isPlace()) {

						this.root.getUndoManager().executeCommand(
								new AddArcCommand(this.root.getGraphicPetriNet(), this.sourceElement, targetElement));

						// last element added to GraphicPetriNet is the arc
						this.root.setClickedElement(this.root.getGraphicPetriNet().getLastArcAdded());

					}
				}
				cancelDragging();
			}
		}
	}

	@Override
	public void setHoverEffects(final int x, final int y) {
		if (this.root.isSelectedToolArc()) {
			final GraphicElement targetElement = this.root.getGraphicPetriNet().getElementByXY(x, y);
			final List<GraphicElement> highlightedElements = canvas.getHighlightedElements();
			if (this.started) { // Connecting to something...
				if (targetElement == null) { // Connecting to air
					highlightedElements.add(this.sourceElement);
					this.sourceElement.setHighlightColor(Colors.POINTING);
					this.root.repaintCanvas();
				} else { // Connecting to solid element
					if (this.sourceElement.isPlace() && targetElement.isTransition()
							|| this.sourceElement.isTransition() && targetElement.isPlace()) {
						highlightedElements.add(this.sourceElement);
						highlightedElements.add(targetElement);
						this.sourceElement.setHighlightColor(Colors.CONNECTING);
						targetElement.setHighlightColor(Colors.CONNECTING);
						this.root.repaintCanvas();
					} else if (this.sourceElement == targetElement) {
						highlightedElements.add(this.sourceElement);
						this.sourceElement.setHighlightColor(Colors.POINTING);
						this.root.repaintCanvas();
					} else if (targetElement.isNode()) { // Wrong combination
						highlightedElements.add(this.sourceElement);
						highlightedElements.add(targetElement);
						this.sourceElement.setHighlightColor(Colors.DISALLOWED);
						targetElement.setHighlightColor(Colors.DISALLOWED);
						this.root.repaintCanvas();
					}
				}
			} else {
				if (targetElement != null) {
					highlightedElements.add(targetElement);
					targetElement.setHighlightColor(Colors.POINTING);
					this.root.repaintCanvas();
				}
			}
		}
	}

	@Override
	public void drawBackground(final Graphics g) {
		for (final GraphicElement element : this.backgroundElements) {
			element.draw(g, this.root.getPetriNet());
		}
	}

	@Override
	public void drawForeground(final Graphics g) {
	}

	@Override
	public void setCursor(final int x, final int y) {
	}

	@Override
	public void drawMainLayer(final Graphics g) {
	}

	private void cancelDragging() {
		this.backgroundElements.remove(this.connectingArc);
		this.started = false;
		this.root.repaintCanvas();
	}
}
