package org.pneditor.editor.commands;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.pneditor.editor.Root;
import org.pneditor.editor.gpetrinet.GraphicArc;
import org.pneditor.editor.gpetrinet.GraphicElement;
import org.pneditor.editor.gpetrinet.GraphicNode;
import org.pneditor.editor.gpetrinet.GraphicPetriNet;
import org.pneditor.editor.gpetrinet.GraphicPlace;
import org.pneditor.editor.gpetrinet.GraphicTransition;
import org.pneditor.petrinet.PetriNetInterface;
import org.pneditor.util.Command;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class PasteCommand implements Command {

    private final GraphicPetriNet gPetriNet;
    private final Set<GraphicElement> elementsToCopy;
    private Set<GraphicElement> elementsPasted;
	private final Root root;

    public PasteCommand(final Set<GraphicElement> elementsToCopy, final GraphicPetriNet gPetriNet, final Root root) {
    	this.gPetriNet = gPetriNet;
        this.elementsToCopy = elementsToCopy;
        this.root = root;
    }

    @Override
	public void execute() {
    	this.elementsPasted = cloneElements(this.elementsToCopy);
    	this.gPetriNet.addAll(this.elementsPasted);
        this.root.getSelection().addAll(this.elementsPasted);
        final Point translation = calculateTranslationToCenter(this.elementsPasted, this.gPetriNet);
        for (final GraphicElement element : this.elementsPasted) {
            element.moveBy(translation.x, translation.y);
        }
    }

    @Override
	public void undo() {
        this.gPetriNet.removeAll(this.elementsPasted);
    }

    @Override
	public void redo() {
        execute();
    }

    @Override
    public String toString() {
        return "Paste";
    }

    private Point calculateTranslationToCenter(final Set<GraphicElement> elements, final GraphicPetriNet petriNet) {
    	final Point viewTranslation = petriNet.getViewTranslation();
    	final GraphicPetriNet tempPetriNet = new GraphicPetriNet(this.root.getCurrentModel());
        tempPetriNet.addAll(elements);
        final Rectangle bounds = tempPetriNet.getBounds();

        final Point result = new Point();
        result.translate(Math.round(-(float) bounds.getCenterX()), Math.round(-(float) bounds.getCenterY()));
        result.translate(-viewTranslation.x, -viewTranslation.y);
        return result;
    }
    

	private Set<GraphicElement> cloneElements(final Set<GraphicElement> copiedElements) {
		final PetriNetInterface petriNet = this.gPetriNet.getPetriNet();
		final Map<GraphicNode, GraphicNode> nodes = new HashMap<>();
		final Set<GraphicElement> pastedElements = new HashSet<>();
		
		for (final GraphicElement element : copiedElements) {
			if (element.isNode()) {
				GraphicNode gNodeClone;
				final GraphicNode gNode = (GraphicNode) element;
				if (gNode.isPlace()) {
					gNodeClone = ((GraphicPlace) gNode).getClone(petriNet.clonePlace(((GraphicPlace) gNode).getPlace()));
				} else {
					gNodeClone = ((GraphicTransition) gNode).getClone(petriNet.cloneTransition(((GraphicTransition) gNode).getTransition()));
				}
				pastedElements.add(gNodeClone);
				nodes.put(gNode, gNodeClone);
			}
		}
		for (final GraphicElement element : copiedElements) {
			if (!element.isNode()) {
				final GraphicArc gArc = (GraphicArc) element;
				final GraphicNode source = nodes.get(gArc.getSource());
				final GraphicNode destination = nodes.get(gArc.getDestination());
				pastedElements.add(gArc.getClone(petriNet.cloneArc(gArc.getArc(), source.getNode(), destination.getNode()),source,destination));
			}
		}
		return pastedElements;
	}

}
