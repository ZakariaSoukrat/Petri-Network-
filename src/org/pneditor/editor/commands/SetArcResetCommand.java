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
import org.pneditor.petrinet.UnimplementedCaseException;
import org.pneditor.util.Command;

import logger.PNEditorLogger;

/**
 *
 * @author jan.tancibok
 */
public class SetArcResetCommand implements Command {

	private final GraphicArc gArc;
	private PetriNetInterface petriNet;
	private AbstractArc oldArc;
	private AbstractArc newArc;
	private final boolean isNotReset;
	private final GraphicPetriNet gPetriNet;


	public SetArcResetCommand(final GraphicArc gArc, final GraphicPetriNet gPetriNet) {
		this.gArc = gArc;
		this.oldArc = gArc.getArc();
		this.gPetriNet = gPetriNet;
		this.isNotReset = !this.oldArc.isReset();
	}

	@Override
	public void execute() {
		this.petriNet = this.gPetriNet.getPetriNet();
		if (this.isNotReset) {
			this.petriNet.removeAbstractArc(this.oldArc);
			try {
				this.newArc = this.petriNet.addResArc((AbstractPlace) this.oldArc.getSource(), (AbstractTransition) this.oldArc.getDestination());
			} catch (UnimplementedCaseException e) {
				PNEditorLogger.infoLogs(e.getMessage()); // should not happen
			}
			this.gArc.setArc(this.newArc);
		}
	}

	@Override
	public void undo() {
		this.petriNet = this.gPetriNet.getPetriNet();
		if (this.isNotReset) {
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
		if (this.isNotReset) {
			this.petriNet.removeAbstractArc(gArc.getArc());
			final AbstractNode source = gArc.getSource().getNode();
			final AbstractNode destination = gArc.getDestination().getNode();
			newArc = this.petriNet.addArcAgain(this.newArc, source, destination);
			this.gArc.setArc(this.newArc);
		}
	}
    @Override
    public String toString() {
        return "Set arc type to reset arc";
    }
}
