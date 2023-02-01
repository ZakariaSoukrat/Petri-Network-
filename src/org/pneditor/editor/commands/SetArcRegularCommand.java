package org.pneditor.editor.commands;

import org.pneditor.editor.gpetrinet.GraphicArc;
import org.pneditor.editor.gpetrinet.GraphicPetriNet;
import org.pneditor.petrinet.AbstractArc;
import org.pneditor.petrinet.AbstractNode;
import org.pneditor.petrinet.PetriNetInterface;
import org.pneditor.petrinet.ResetArcMultiplicityException;
import org.pneditor.petrinet.UnimplementedCaseException;
import org.pneditor.util.Command;

import logger.PNEditorLogger;

public class SetArcRegularCommand implements Command {

	private final GraphicArc gArc;
	private PetriNetInterface petriNet;
	private AbstractArc oldArc;
	private AbstractArc newArc;
	private final boolean isNotRegular;
	private final GraphicPetriNet gPetriNet;

	public SetArcRegularCommand(final GraphicArc gArc, final GraphicPetriNet gPetriNet) {
		this.gArc = gArc;
		this.gPetriNet = gPetriNet;
		this.oldArc = gArc.getArc();
		this.isNotRegular = !this.oldArc.isRegular();
	}

	@Override
	public void execute() {
		this.petriNet = this.gPetriNet.getPetriNet();
		if (this.isNotRegular && this.oldArc.isSourceAPlace()) {
			this.petriNet.removeAbstractArc(this.oldArc);
			try {
				if (this.oldArc.isReset()) {
					this.newArc = this.petriNet.addRegArc(this.oldArc.getSource(),
							this.oldArc.getDestination());
				}
				if (this.oldArc.isInhibitory()) {
					this.newArc = this.petriNet.addRegArc(this.oldArc.getSource(),
							this.oldArc.getDestination());
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
		if (this.isNotRegular) {
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
		if (this.isNotRegular) {
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
