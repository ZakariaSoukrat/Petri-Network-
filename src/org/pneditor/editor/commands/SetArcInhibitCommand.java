/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pneditor.editor.commands;

import org.pneditor.editor.gpetrinet.GraphicArc;
import org.pneditor.editor.gpetrinet.GraphicPetriNet;
import org.pneditor.petrinet.AbstractArc;
import org.pneditor.petrinet.AbstractNode;
import org.pneditor.petrinet.AbstractPlace;
import org.pneditor.petrinet.AbstractTransition;
import org.pneditor.petrinet.PetriNetInterface;
import org.pneditor.petrinet.ResetArcMultiplicityException;
import org.pneditor.petrinet.UnimplementedCaseException;
import org.pneditor.util.Command;

import logger.PNEditorLogger;

/**
 *
 * @author Amodez
 */
public class SetArcInhibitCommand implements Command {

	private final GraphicArc gArc;
	private AbstractArc oldArc;
	private AbstractArc newArc;
	private final boolean isNotInhibitory;
	private final GraphicPetriNet gPetriNet;
	private PetriNetInterface petriNet;

	public SetArcInhibitCommand(final GraphicArc gArc, final GraphicPetriNet gPetriNet) {
		this.gArc = gArc;
		this.oldArc = gArc.getArc();
		this.gPetriNet = gPetriNet;
		this.isNotInhibitory = !this.oldArc.isInhibitory();
	}

	@Override
	public void execute() {
		this.petriNet = this.gPetriNet.getPetriNet();
		if (this.isNotInhibitory) {
			this.petriNet.removeAbstractArc(this.oldArc);
			try {
				if (this.oldArc.isReset()) {
					this.newArc = this.petriNet.addInhibArc((AbstractPlace) this.oldArc.getSource(),
							(AbstractTransition) this.oldArc.getDestination());
				}
				if (this.oldArc.isRegular()) {
					this.newArc = this.petriNet.addInhibArc((AbstractPlace) this.oldArc.getSource(),
							(AbstractTransition) this.oldArc.getDestination());
					this.newArc.setMultiplicity(this.oldArc.getMultiplicity());
				}
			} catch (ResetArcMultiplicityException e) {
				// should not happen
				PNEditorLogger.severeLogs(e.getMessage());
			} catch (UnimplementedCaseException e) {
				// should not happen since we're manipulating new objects, except if a behavior
				// is not implemented
				PNEditorLogger.warningLogs(e.getMessage());
			}
			this.gArc.setArc(this.newArc);

		}

	}

	@Override
	public void undo() {
		this.petriNet = this.gPetriNet.getPetriNet();
		if (this.isNotInhibitory) {
			this.petriNet.removeAbstractArc(gArc.getArc());
			final AbstractNode source = gArc.getSource().getNode();
			final AbstractNode destination = gArc.getDestination().getNode();
			oldArc = this.petriNet.addArcAgain(this.oldArc, source, destination);
			this.gArc.setArc(this.oldArc);
		}
	}

	@Override
	public void redo() {
		this.petriNet = this.gPetriNet.getPetriNet();
		if (this.isNotInhibitory) {
			this.petriNet.removeAbstractArc(gArc.getArc());
			final AbstractNode source = gArc.getSource().getNode();
			final AbstractNode destination = gArc.getDestination().getNode();
			newArc = this.petriNet.addArcAgain(this.newArc, source, destination);
			this.gArc.setArc(this.newArc);
		}
	}

	@Override
	public String toString() {
		return "Set arc type to inhibitor arc";
	}

}
