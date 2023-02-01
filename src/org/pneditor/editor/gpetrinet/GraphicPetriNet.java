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
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.pneditor.util.CachedGraphics2D;

import logger.PNEditorLogger;

import org.pneditor.petrinet.AbstractArc;
import org.pneditor.petrinet.PetriNetInterface;

/**
 * PetriNet class stores reference to the root subnet and manages a view of
 * currently opened subnet in form of a stack. Default view is only the root
 * subnet opened. Opening and closing subnets does not influence anything other
 * and serves only for informational purposes.
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class GraphicPetriNet {

	private final List<GraphicElement> elements = new LinkedList<>();
	private Point viewTranslation = new Point(0, 0);
	private PetriNetInterface petriNet;
	private final String modelPath = "org.pneditor.petrinet.adapters.";
	private String model = "initial";
	private final String modelAdapterPath = ".PetriNetAdapter";

	/**
	 * Constructor. Creates a new Petri net with empty root subnet.
	 */
	public GraphicPetriNet() {
		try {
			this.petriNet = (PetriNetInterface) Class.forName(this.modelPath + this.model + this.modelAdapterPath)
					.newInstance();
		} catch (InstantiationException e) {
			PNEditorLogger.severeLogs("Cannot instantiate this class");
		} catch (IllegalAccessException e) {
			PNEditorLogger.severeLogs("Cannot access this class");
		} catch (ClassNotFoundException e) {
			PNEditorLogger.severeLogs("Cannot find this class");
		}
	}

	public GraphicPetriNet(final String model) {
		if (model != null) {
			this.model = model;
		}
		try {
			this.petriNet = (PetriNetInterface) Class.forName(this.modelPath + this.model + this.modelAdapterPath)
					.newInstance();
		} catch (InstantiationException e) {
			PNEditorLogger.severeLogs("Cannot instantiate this class");
		} catch (IllegalAccessException e) {
			PNEditorLogger.severeLogs("Cannot access this class");
		} catch (ClassNotFoundException e) {
			PNEditorLogger.severeLogs("Cannot find this class");
		}
	}

	public Point getViewTranslation() {
		return new Point(this.viewTranslation);
	}

	public void setViewTranslation(final Point viewTranslation) {
		this.viewTranslation = new Point(viewTranslation);
	}

	public void setViewTranslationToCenter() {
		final int centerX = Math.round((float) getBounds().getCenterX());
		final int centerY = Math.round((float) getBounds().getCenterY());
		final Point center = new Point(-centerX, -centerY);
		setViewTranslation(center);
	}

	public List<GraphicElement> getElements() {
		return Collections.unmodifiableList(this.elements);
	}

	public GraphicElement getElementByXY(final int x, final int y) {
		for (int i = this.elements.size() - 1; i >= 0; i--) { // Check elements from front to back.
			final GraphicElement element = this.elements.get(i);
			if (element.containsPoint(x, y)) {
				return element;
			}
		}
		return null;
	}

	public void addElement(final GraphicElement element) {
		if (!element.isNode()) {
			this.elements.add(0, element); // background
		} else {
			this.elements.add(element);
		}
	}

	public void removeElement(final GraphicElement element) {
		this.elements.remove(element);
	}

	public void removeElements() {
		this.elements.clear();
	}

	public void addAll(final Set<GraphicElement> elementsToAdd) {
		for (final GraphicElement element : elementsToAdd) {
			addElement(element);
		}
	}

	public void removeAll(final Set<GraphicElement> elementsToRemove) {
		for (final GraphicElement element : elementsToRemove) {
			removeElement(element);
		}
	}

	public Set<GraphicArc> getArcs() {
		final Set<GraphicArc> arcs = new HashSet<>();
		for (final GraphicElement element : this.elements) {
			if (!element.isNode()) {
				final GraphicArc arc = (GraphicArc) element;
				arcs.add(arc);
			}
		}
		return arcs;
	}

	public Set<GraphicPlace> getPlaces() {
		final Set<GraphicPlace> places = new HashSet<>();
		for (final GraphicElement element : this.elements) {
			if (element.isPlace()) {
				final GraphicPlace place = (GraphicPlace) element;
				places.add(place);
			}
		}
		return places;
	}

	public Rectangle getBounds() {
		Rectangle bounds = null;

		for (final GraphicElement element : this.elements) {
			if (bounds == null) {
				bounds = new Rectangle(element.getStart().x, element.getStart().y, element.getWidth(),
						element.getHeight());
			}
			bounds.add(element.getStart().x, element.getStart().y);
			bounds.add(element.getEnd().x, element.getEnd().y);
			bounds.add(element.getStart().x, element.getEnd().y);
			bounds.add(element.getEnd().x, element.getStart().y);
			if (!element.isNode()) {
				final GraphicArc arc = (GraphicArc) element;
				for (final Point breakPoint : arc.getBreakPoints()) {
					bounds.add(breakPoint);
				}
			}
		}
		if (bounds == null) {
			bounds = new Rectangle();
		}
		bounds.width++;
		bounds.height++;
		return bounds;
	}

	public void draw(final Graphics g) {

		for (final GraphicElement element : this.elements) {
			if (element.highlightColor != null) {
				final Color previousColor = element.getColor();

				element.setColor(element.highlightColor);
				element.draw(g, this.petriNet); // TODO

				element.setColor(previousColor);
			} else {
				element.draw(g, this.petriNet); // TODO
			}
		}
	}

	public PetriNetInterface getPetriNet() {
		return this.petriNet;
	}

	public void setPetriNet(final PetriNetInterface petriNet) {
		this.petriNet = petriNet;
	}

	public Set<GraphicArc> getConnectedGraphicArcs(final GraphicNode node) {
		final Set<GraphicArc> graphicArcs = new HashSet<>();
		for (final GraphicElement element : this.elements) {
			if (!element.isNode()) {
				final AbstractArc arc = ((GraphicArc) element).getArc();
				if (arc.getSource().equals(node.getNode()) || arc.getDestination().equals(node.getNode())) {
					graphicArcs.add((GraphicArc) element);
				}
			}
		}
		return graphicArcs;
	}

	/**
	 * Returns an preview image of the subnet with specified marking. Scale image:
	 * image.getScaledInstance(preferredWidth, preferredHeight, Image.SCALE_SMOOTH)
	 * Save image: ImageIO.write(image, "png", file);
	 */

	public BufferedImage getPreview() {
		final CachedGraphics2D cachedGraphics = new CachedGraphics2D();
		cachedGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		for (final GraphicElement element : getElements()) {
			element.draw(cachedGraphics, getPetriNet());
		}
		final Rectangle bounds = cachedGraphics.getIntegerBounds();
		int width = bounds.width;
		int height = bounds.height;
		width = Math.max(width, 1);
		height = Math.max(height, 1);
		final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D imageGraphics = (Graphics2D) bufferedImage.getGraphics();
		imageGraphics.fillRect(0, 0, width, height); // paint the background white
		imageGraphics.translate(-bounds.x, -bounds.y);
		cachedGraphics.applyToGraphics(imageGraphics);
		return bufferedImage;
	}

	public GraphicElement getLastElementAdded() {
		return ((LinkedList<GraphicElement>) this.elements).getLast();
	}

	public GraphicElement getLastArcAdded() {
		return ((LinkedList<GraphicElement>) this.elements).getFirst();
	}
}
