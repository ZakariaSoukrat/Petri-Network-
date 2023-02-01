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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import org.pneditor.editor.Root;
import org.pneditor.editor.gpetrinet.GraphicElement;
import org.pneditor.util.Point;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
@SuppressWarnings("serial")
public class Canvas extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {

	private final List<GraphicElement> highlightedElements = new ArrayList<>();
	private Cursor alternativeCursor;
	public Cursor activeCursor;
	public List<Feature> features = new ArrayList<>();
	private final Root root;
	private final ScrollingFeature scrollingFeature;
	private boolean scrollingFeatureInstalled;

	public Canvas(final Root root) {
		super();
		this.root = root;
		setBackground(Color.white);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);

		this.features.add(new ClickFeature(this));
		this.features.add(new PanningFeature(this));
		this.scrollingFeature = new ScrollingFeature(this);
		this.features.add(this.scrollingFeature);
		this.features.add(new DraggingFeature(this));
		this.features.add(new SelectionDrawingFeature(this));
		this.features.add(new TokenFeature(this));
		this.features.add(new EdgeZigzagFeature(this));
		this.features.add(new PlaceTransitionMakerFeature(this));
		this.features.add(new PopupMenuFeature(this));
		this.features.add(new ArcFeature(this));
		this.features.add(new PetriNetFeature(this));
	}

	public Root getRoot() {
		return this.root;
	}

	public int getTranslationX() {
		return this.root.getGraphicPetriNet().getViewTranslation().x + getWidth() / 2;
	}

	public int getTranslationY() {
		return this.root.getGraphicPetriNet().getViewTranslation().y + getHeight() / 2;
	}

	public Point getViewTranslation() {
		return new Point(this.root.getGraphicPetriNet().getViewTranslation());
	}

	public void setViewTranslation(final Point newViewTranslation) {
		this.root.getGraphicPetriNet().setViewTranslation(newViewTranslation.getPoint());
	}

	@Override
	public void paintComponent(final Graphics g) {
		if (!this.scrollingFeatureInstalled) {
			this.root.getDrawingBoard().getHorizontalScrollBar().addAdjustmentListener(this.scrollingFeature);
			this.root.getDrawingBoard().getVerticalScrollBar().addAdjustmentListener(this.scrollingFeature);
			this.scrollingFeatureInstalled = true;
		}

		final Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		super.paintComponent(g);
		g.translate(getTranslationX(), getTranslationY());

		for (final Feature f : this.features) {
			f.drawBackground(g);
		}
		for (final Feature f : this.features) {
			f.drawMainLayer(g);
		}
		for (final Feature f : this.features) {
			f.drawForeground(g);
		}
	}

	@Override
	public void mouseWheelMoved(final MouseWheelEvent e) {
		if (e.getWheelRotation() == 1) {
			if (this.root.isSelectedToolPlace()) {
				this.root.selectToolTransition();
			} else if (this.root.isSelectedToolTransition()) {
				this.root.selectToolArc();
			} else if (this.root.isSelectedToolArc()) {
				this.root.selectToolToken();
			} else {
				this.root.selectToolPlace(); // if selectToolToken is selected, or default case
			}
		} else if (e.getWheelRotation() == -1) {
			if (this.root.isSelectedToolTransition()) {
				this.root.selectToolPlace();
			} else if (this.root.isSelectedToolArc()) {
				this.root.selectToolTransition();
			} else if (this.root.isSelectedToolToken()) {
				this.root.selectToolArc();
			} else {
				this.root.selectToolToken(); // if selectToolPlace is selected or default case
			}
		}
		repaint();
		setHoverEffects(e.getX(), e.getY());
	}

	@Override
	public void mousePressed(final MouseEvent event) {
		final int x = event.getX() - getTranslationX();
		final int y = event.getY() - getTranslationY();
		final MouseEvent mouseEvent = new MouseEvent((Component) event.getSource(), event.getID(), event.getWhen(),
				event.getModifiers(), x, y, event.getXOnScreen(), event.getYOnScreen(), event.getClickCount(),
				event.isPopupTrigger(), event.getButton());

		this.root.setClickedElement(this.root.getGraphicPetriNet().getElementByXY(x, y));

		for (final Feature f : this.features) {
			f.mousePressed(mouseEvent);
		}

		if (mouseEvent.getButton() == MouseEvent.BUTTON3 && this.root.getClickedElement() == null) { 
			// The user did not click on a shape.
			this.root.selectToolSelect();
		}

		setCursor(x, y);
		setHoverEffects(x, y);
	}

	@Override
	public void mouseDragged(final MouseEvent event) {
		final int x = event.getX() - getTranslationX();
		final int y = event.getY() - getTranslationY();

		for (final Feature f : this.features) {
			f.mouseDragged(x, y);
		}

		setHoverEffects(x, y);
	}

	@Override
	public void mouseReleased(final MouseEvent evt) {
		final int x = evt.getX() - getTranslationX();
		final int y = evt.getY() - getTranslationY();

		for (final Feature f : this.features) {
			f.mouseReleased(x, y);
		}

		setHoverEffects(x, y);
		setCursor(x, y);
	}

	@Override
	public void mouseMoved(final MouseEvent evt) {
		final int x = evt.getX() - getTranslationX();
		final int y = evt.getY() - getTranslationY();

		for (final Feature f : this.features) {
			f.mouseMoved(x, y);
		}

		setHoverEffects(x, y);
		setCursor(x, y);
	}

	void setHoverEffects(final int x, final int y) {
		if (!this.highlightedElements.isEmpty()) {
			for (final GraphicElement element : this.highlightedElements) {
				element.setHighlightColor(null);
			}
			this.highlightedElements.clear();
			repaint();
		}
		for (final Feature f : this.features) {
			f.setHoverEffects(x, y);
		}
	}

	void setCursor(final int x, final int y) {
		this.alternativeCursor = null;

		for (final Feature f : this.features) {
			f.setCursor(x, y);
		}

		Cursor cursor;
		if (this.alternativeCursor != null) {
			cursor = this.alternativeCursor;
		} else {
			cursor = this.activeCursor;
		}

		if (getCursor() != cursor) {
			setCursor(cursor);
		}
	}

	@Override
	public void mouseEntered(final MouseEvent evt) {
	}

	@Override
	public void mouseExited(final MouseEvent evt) {
	}

	@Override
	public void mouseClicked(final MouseEvent evt) {
	}

	Cursor getAlternativeCursor() {
		return alternativeCursor;
	}

	void setAlternativeCursor(final Cursor cursor) {
		this.alternativeCursor = cursor;
	}

	List<GraphicElement> getHighlightedElements() {
		return highlightedElements;
	}

}
