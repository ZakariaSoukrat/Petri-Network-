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
package org.pneditor.editor.actions;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;

import org.pneditor.editor.Root;
import org.pneditor.editor.gpetrinet.GraphicArc;
import org.pneditor.editor.gpetrinet.GraphicElement;
import org.pneditor.editor.gpetrinet.GraphicPlace;
import org.pneditor.editor.gpetrinet.GraphicTransition;
import org.pneditor.petrinet.AbstractArc;
import org.pneditor.petrinet.AbstractNode;
import org.pneditor.petrinet.AbstractPlace;
import org.pneditor.petrinet.AbstractTransition;
import org.pneditor.petrinet.PetriNetInterface;
import org.pneditor.petrinet.ResetArcMultiplicityException;
import org.pneditor.petrinet.UnimplementedCaseException;

import logger.PNEditorLogger;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
@SuppressWarnings("serial")
public class ChangeModelAction extends AbstractAction {

	private final Root root;
	private final String model;

	private final static String MODEL_PATH = "org.pneditor.petrinet.adapters.";
	private final static String ADAPTER_PATH = ".PetriNetAdapter";

	public ChangeModelAction(final Root root, final String model) {
		super();
		this.model = model;
		this.root = root;
		putValue(NAME, model);
		putValue(SHORT_DESCRIPTION, "Change model to " + model);
		putValue(MNEMONIC_KEY, (int) (model.charAt(0))); // set the mnemonic key to the first character of the model
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		this.root.setCurrentModel(this.model);
		
		final Map<AbstractNode,AbstractNode> matchingNodes = new HashMap<>();
		try {
			final PetriNetInterface petriNet = (PetriNetInterface) Class.forName(MODEL_PATH + this.model + ADAPTER_PATH).newInstance();
			for (final GraphicElement element : this.root.getGraphicPetriNet().getElements()) {

				if (element.isPlace()) {
					final GraphicPlace gPlace = (GraphicPlace) element;
					final AbstractPlace oldPlace = gPlace.getPlace();
					final AbstractPlace place = petriNet.addAbstractPlace(oldPlace.getId());
					place.setTokens(oldPlace.getTokens());
					place.setLabel(oldPlace.getLabel());
					gPlace.setPlace(place);
					
					matchingNodes.put(oldPlace, place);
				}

				if (element.isTransition()) {
					final GraphicTransition gTransition = (GraphicTransition) element;
					final AbstractTransition oldTransition = gTransition.getTransition();
					final AbstractTransition transition = petriNet.addAbstractTransition(oldTransition.getId());
					transition.setLabel(oldTransition.getLabel());
					gTransition.setTransition(transition);
					
					matchingNodes.put(oldTransition, transition);
				}
			}

			for (final GraphicElement element : this.root.getGraphicPetriNet().getElements()) {

				if (!element.isNode()) {
					final GraphicArc gArc = (GraphicArc) element;
					final AbstractArc oldArc = gArc.getArc();
					try {
						AbstractArc arc;
						final AbstractNode source = matchingNodes.get(oldArc.getSource());
						final AbstractNode destination = matchingNodes.get(oldArc.getDestination());
						
						if (oldArc.isReset()) {
							arc = petriNet.addResArc((AbstractPlace) source,
									(AbstractTransition) destination);
						} else {
							if (oldArc.isInhibitory()) {
								arc = petriNet.addInhibArc((AbstractPlace) source,
										(AbstractTransition) destination);
							} else {
								arc = petriNet.addRegArc(source, destination);
							}
							arc.setMultiplicity(oldArc.getMultiplicity());
						}
						gArc.setArc(arc);
					} catch (UnimplementedCaseException | ResetArcMultiplicityException e1) {
						// ResetArcMultiplicityException should not happen
						
					}
				}
			}
			this.root.getGraphicPetriNet().setPetriNet(petriNet);
			this.root.updateClipboard();

		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e1) {
			PNEditorLogger.severeLogs(e1.getMessage());
		}
	}
}
