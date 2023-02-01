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

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import org.pneditor.editor.Root;
import org.pneditor.editor.commands.AddTokenCommand;
import org.pneditor.editor.commands.FireTransitionCommand;
import org.pneditor.editor.commands.RemoveTokenCommand;
import org.pneditor.editor.gpetrinet.GraphicElement;
import org.pneditor.editor.gpetrinet.GraphicPlace;
import org.pneditor.editor.gpetrinet.GraphicTransition;
import org.pneditor.petrinet.PetriNetInterface;
import org.pneditor.petrinet.ResetArcMultiplicityException;
import org.pneditor.util.Colors;
import org.pneditor.util.GraphicsTools;
import org.pneditor.PNEConstantsConfiguration;

import logger.PNEditorLogger;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
class TokenFeature implements Feature {

	private final Canvas canvas;
	private final Root root;

	private final Cursor tokenCursor;
	private final Cursor fireCursor;

	TokenFeature(final Canvas canvas) {
		this.canvas = canvas;
		this.root = canvas.getRoot();
		this.tokenCursor = GraphicsTools.getCursor(PNEConstantsConfiguration.TOKEN, new Point(16, 0));
		this.fireCursor = GraphicsTools.getCursor(PNEConstantsConfiguration.FIRE, new Point(16, 0));
	}

	@Override
	public void mousePressed(final MouseEvent event) {

		if (this.root.getClickedElement() != null && this.root.isSelectedToolToken()) {
			final int x = event.getX();
			final int y = event.getY();
			final int mouseButton = event.getButton();
			final GraphicElement targetElement = this.root.getGraphicPetriNet().getElementByXY(x, y);

			if (targetElement.isPlace()) {
				final GraphicPlace place = (GraphicPlace) targetElement;
				if (mouseButton == MouseEvent.BUTTON1) {
					this.root.getUndoManager().executeCommand(new AddTokenCommand(place));
				} else if (mouseButton == MouseEvent.BUTTON3 && place.getPlace().getTokens() > 0) {
					this.root.getUndoManager().executeCommand(new RemoveTokenCommand(place));
				}

			} else if (targetElement.isTransition()) {
				final GraphicTransition transition = (GraphicTransition) targetElement;
				if (mouseButton == MouseEvent.BUTTON1) {
					try {
						final PetriNetInterface petriNet = this.root.getPetriNet();
						if (petriNet.isEnabled(transition.getTransition())) {
							this.root.getUndoManager().executeCommand(
									new FireTransitionCommand(transition, this.root.getGraphicPetriNet()));
						}
					} catch (ResetArcMultiplicityException e) {
						PNEditorLogger.severeLogs(e.getMessage());
					}
				}
			}
		}
	}

	@Override
	public void setHoverEffects(final int x, final int y) {
		final GraphicElement targetElement = this.root.getGraphicPetriNet().getElementByXY(x, y);

		if (this.root.isSelectedToolToken() && targetElement != null) {
			if (targetElement.isPlace()) {
				this.canvas.getHighlightedElements().add(targetElement);
				targetElement.setHighlightColor(Colors.POINTING);
				this.canvas.repaint();
			} else if (targetElement.isTransition()) {
				try {
					final PetriNetInterface petriNet = this.root.getPetriNet();
					if (petriNet.isEnabled(((GraphicTransition) targetElement).getTransition())) {
						this.canvas.getHighlightedElements().add(targetElement);
						targetElement.setHighlightColor(Colors.PERMITTED);
						this.canvas.repaint();
					} else {
						this.canvas.getHighlightedElements().add(targetElement);
						targetElement.setHighlightColor(Colors.DISALLOWED);
						this.canvas.repaint();
					}
				} catch (ResetArcMultiplicityException e) {
					PNEditorLogger.severeLogs(e.getMessage());
				}
			}
		}
	}

	@Override
	public void drawForeground(final Graphics g) {

		if (this.root.isSelectedToolToken()) {
			for (final GraphicElement element : this.root.getGraphicPetriNet().getElements()) {
				if (element.isTransition()) {
					final GraphicTransition transition = (GraphicTransition) element;
					try {
						final PetriNetInterface petriNet = this.root.getPetriNet();
						if (petriNet.isEnabled(transition.getTransition())) {
							g.setColor(Colors.PERMITTED);
						} else {
							g.setColor(Colors.DISALLOWED);
						}
					} catch (ResetArcMultiplicityException e) {
						PNEditorLogger.severeLogs(e.getMessage());
					}
					((Graphics2D) g).setStroke(new BasicStroke(2f));
					g.drawRect(transition.getStart().x + 1, transition.getStart().y + 1, transition.getWidth() - 3,
							transition.getHeight() - 3);
					((Graphics2D) g).setStroke(new BasicStroke(1f));
				}
			}
		}
	}

	@Override
	public void setCursor(final int x, final int y) {
		final GraphicElement targetElement = this.root.getGraphicPetriNet().getElementByXY(x, y);

		if (this.root.isSelectedToolToken() && targetElement != null) {
			if (targetElement.isPlace()) {
				this.canvas.setAlternativeCursor(this.tokenCursor);
			} else if (targetElement.isTransition()) {
				this.canvas.setAlternativeCursor(this.fireCursor);
			}
		}

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
	public void drawMainLayer(final Graphics g) {
	}

	@Override
	public void mouseMoved(final int x, final int y) {
	}
}
