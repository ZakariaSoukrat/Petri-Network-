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
import org.pneditor.editor.gpetrinet.GraphicTransition;
import org.pneditor.petrinet.AbstractTransition;
import org.pneditor.util.Command;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class AddTransitionCommand implements Command {

	final private int x, y;
    private AbstractTransition createdTransition;
    final private GraphicPetriNet gPetriNet;
    private GraphicTransition representation;

    public AddTransitionCommand(int x, int y, GraphicPetriNet gPetriNet) {
        this.x = x;
        this.y = y;
        this.gPetriNet = gPetriNet;
    }

    @Override
	public void execute() {
    	this.createdTransition = this.gPetriNet.getPetriNet().addAbstractTransition();
    	this.representation = new GraphicTransition(this.createdTransition, this.x, this.y);
    	this.gPetriNet.addElement(this.representation);
    }

    @Override
	public void undo() {
        new DeleteTransitionCommand(this.representation, this.gPetriNet).execute();
    }

    @Override
	public void redo() {    	
    	final AbstractTransition newTransition = this.gPetriNet.getPetriNet().addAbstractTransition();
    	newTransition.setLabel(this.createdTransition.getLabel()); // in case of a given name on construction, 
    																//we put it back (different models for instance)
    	this.representation.setTransition(newTransition);
    	this.gPetriNet.addElement(this.representation);
    }

    @Override
    public String toString() {
        return "Add transition";
    }

}
