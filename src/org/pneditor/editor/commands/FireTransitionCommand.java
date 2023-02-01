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
package org.pneditor.editor.commands;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.pneditor.editor.gpetrinet.GraphicPetriNet;
import org.pneditor.editor.gpetrinet.GraphicPlace;
import org.pneditor.editor.gpetrinet.GraphicTransition;
import org.pneditor.petrinet.ResetArcMultiplicityException;
import org.pneditor.util.Command;

import logger.PNEditorLogger;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class FireTransitionCommand implements Command {

	final private GraphicTransition transition;
	final private GraphicPetriNet gPetriNet;
	final private Map<GraphicPlace,Integer> firedPlaces = new HashMap<>();

	public FireTransitionCommand(final GraphicTransition gTransition, final GraphicPetriNet gPetriNet) {
		this.transition = gTransition;
		this.gPetriNet = gPetriNet;
	}

	@Override
	public void execute() { //TODO: Temporary solution to remove the undoFire from the PetriNet
		for (final GraphicPlace place : this.gPetriNet.getPlaces()) {
			this.firedPlaces.put(place, place.getPlace().getTokens());
		}
		try {
			this.gPetriNet.getPetriNet().fire(this.transition.getTransition());
		} catch (ResetArcMultiplicityException e) {
			PNEditorLogger.severeLogs(e.getMessage());
		}
		
		final Iterator<Entry<GraphicPlace, Integer>> iter = this.firedPlaces.entrySet().iterator(); //to avoid ConcurrentModificationExceptions
		while (iter.hasNext()) {
			final Map.Entry<GraphicPlace, Integer> pair = iter.next();
			if (pair.getKey().getPlace().getTokens() == pair.getValue()) {
				iter.remove();
			}
		}
	}

	@Override
	public void undo() {
		for (final Map.Entry<GraphicPlace, Integer> entry : this.firedPlaces.entrySet()) {
			entry.getKey().getPlace().setTokens(entry.getValue());
		}
	}

	@Override
	public void redo() {
		execute();
	}

	@Override
	public String toString() {
		return "Fire transition";
	}

}
