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
import org.pneditor.editor.Root;
import org.pneditor.editor.commands.AddPlaceCommand;
import org.pneditor.editor.commands.AddTransitionCommand;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class PlaceTransitionMakerFeature implements Feature {

	private final Root root;

	public PlaceTransitionMakerFeature(final Canvas canvas) {
		this.root = canvas.getRoot();
	}

	@Override
	public void mousePressed(final MouseEvent event) {
		final int mouseButton = event.getButton();

		if (mouseButton == MouseEvent.BUTTON1 && this.root.getClickedElement() == null) {
			final int x = event.getX();
			final int y = event.getY();
			if (this.root.isSelectedToolPlace()) {
				this.root.getSelection().clear();
				this.root.getUndoManager().executeCommand(new AddPlaceCommand(x, y, this.root.getGraphicPetriNet()));
				this.root.setClickedElement(this.root.getGraphicPetriNet().getLastElementAdded());
			} else if (this.root.isSelectedToolTransition()) {
				this.root.getSelection().clear();
				this.root.getUndoManager()
						.executeCommand(new AddTransitionCommand(x, y, this.root.getGraphicPetriNet()));
				this.root.setClickedElement(this.root.getGraphicPetriNet().getLastElementAdded());
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
