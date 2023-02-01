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

import org.pneditor.editor.gpetrinet.GraphicPetriNet;
import org.pneditor.editor.gpetrinet.GraphicPlace;
import org.pneditor.petrinet.AbstractPlace;
import org.pneditor.util.Command;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class AddPlaceCommand implements Command {

	final private GraphicPetriNet gPetriNet ;
	final private int x, y;
    private AbstractPlace createdPlace;
    private GraphicPlace representation;

    public AddPlaceCommand(final int x, final int y, final GraphicPetriNet gPetriNet) {
        this.x = x;
        this.y = y;
        this.gPetriNet = gPetriNet;
    }

    @Override
	public void execute() {
    	this.createdPlace = this.gPetriNet.getPetriNet().addAbstractPlace();
    	this.representation = new GraphicPlace(this.createdPlace, this.x, this.y);
    	this.gPetriNet.addElement(this.representation);
    }

    @Override
	public void undo() {
        new DeletePlaceCommand(this.representation, this.gPetriNet).execute();
    }

    @Override
	public void redo() {
    	final AbstractPlace newPlace = this.gPetriNet.getPetriNet().addAbstractPlace();
    	newPlace.setLabel(this.createdPlace.getLabel()); // in case of a given name on construction, we put it back 
    	newPlace.setTokens(this.createdPlace.getTokens()); // same as before, except with tokens
        this.representation.setPlace(newPlace);
        this.gPetriNet.addElement(this.representation);
    }

    @Override
    public String toString() {
        return "Add place";
    }

    public AbstractPlace getCreatedPlace() {
        return this.createdPlace;
    }
}
