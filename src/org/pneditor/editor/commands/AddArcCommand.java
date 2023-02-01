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
import org.pneditor.petrinet.AbstractArc;
import org.pneditor.petrinet.ResetArcMultiplicityException;
import org.pneditor.petrinet.UnimplementedCaseException;
import org.pneditor.editor.gpetrinet.GraphicNode;
import org.pneditor.util.Command;

import logger.PNEditorLogger;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class AddArcCommand implements Command {

	final private GraphicPetriNet gPetriNet;
	final private GraphicNode source;
	final private GraphicNode destination;
	private AbstractArc createdArc;
	private GraphicArc representation;

	public AddArcCommand(final GraphicPetriNet gPetriNet, final GraphicNode source, final GraphicNode destination) {
		this.gPetriNet = gPetriNet;
		this.source = source;
		this.destination = destination;
	}

	@Override
	public void execute() {
		try {
			this.createdArc = this.gPetriNet.getPetriNet().addRegArc(this.source.getNode(), this.destination.getNode());
		} catch (UnimplementedCaseException e) {
			PNEditorLogger.infoLogs(e.getMessage());
			// do nothing, that means something is not implemented 
			// and that the rest should not happen
		}
		if (this.createdArc != null) {
			this.representation = new GraphicArc(this.createdArc);
			this.representation.setSource(this.source);
			this.representation.setDestination(this.destination);
			this.gPetriNet.addElement(this.representation);
		}
	}

	@Override
	public void undo() {
		new DeleteArcCommand(this.representation, this.gPetriNet).execute();
	}

	@Override
	public void redo() {
		int oldMultiplicity = 0;
		try {
			oldMultiplicity = this.createdArc.getMultiplicity(); // in case the initial multiplicity is the same in the actual model,
															// we save the old one, and give it back when adding again the arc
			execute();
			if (oldMultiplicity > 0) {
				this.createdArc.setMultiplicity(oldMultiplicity);
			}

		} catch (ResetArcMultiplicityException e) {
			execute(); //forget about the multiplicity issue
		}
	}

	@Override
	public String toString() {
		return "Add arc";
	}

}
