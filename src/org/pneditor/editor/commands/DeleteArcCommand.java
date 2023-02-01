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

import org.pneditor.editor.gpetrinet.GraphicArc;
import org.pneditor.editor.gpetrinet.GraphicPetriNet;
import org.pneditor.petrinet.AbstractNode;
import org.pneditor.util.Command;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class DeleteArcCommand implements Command {

	final private GraphicArc gArc;
	final private GraphicPetriNet gPetriNet;
    private boolean isAlreadyDeleted;

    public DeleteArcCommand(final GraphicArc gArc, final GraphicPetriNet gPetriNet) {
    	this.gPetriNet = gPetriNet;
    	this.gArc = gArc;
    }

    @Override
	public void execute() {
        this.isAlreadyDeleted = !this.gPetriNet.getArcs().contains(this.gArc);
        if (!this.isAlreadyDeleted) {
        	this.gPetriNet.getPetriNet().removeAbstractArc(this.gArc.getArc());
        	this.gPetriNet.removeElement(this.gArc);
        }
    }

    @Override
	public void undo() {
        if (!this.isAlreadyDeleted) {
        	AbstractNode source = gArc.getSource().getNode();
        	AbstractNode destination = gArc.getDestination().getNode();
        	this.gPetriNet.getPetriNet().addArcAgain(this.gArc.getArc(), source, destination);
        	this.gPetriNet.addElement(this.gArc);
        }
    }

    @Override
	public void redo() {
        this.isAlreadyDeleted = !this.gPetriNet.getArcs().contains(this.gArc);
        if (!this.isAlreadyDeleted) {
        	this.gPetriNet.getPetriNet().removeAbstractArc(this.gArc.getArc());
        	this.gPetriNet.removeElement(this.gArc);
        }
    }

    @Override
    public String toString() {
        return "Delete arc";
    }

}
