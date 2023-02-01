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
package org.pneditor.save.xml;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.pneditor.editor.gpetrinet.GraphicArc;
import org.pneditor.editor.gpetrinet.GraphicNode;
import org.pneditor.editor.gpetrinet.GraphicPetriNet;
import org.pneditor.editor.gpetrinet.GraphicPlace;
import org.pneditor.editor.gpetrinet.GraphicTransition;
import org.pneditor.petrinet.AbstractArc;
import org.pneditor.petrinet.AbstractPlace;
import org.pneditor.petrinet.AbstractTransition;
import org.pneditor.petrinet.ResetArcMultiplicityException;
import org.pneditor.petrinet.UnimplementedCaseException;

import logger.PNEditorLogger;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class DocumentImporter {

	private XmlDocument xmlDocument;
	final private Map<Integer, GraphicNode> nodeReferences = new HashMap<>();

	public GraphicPetriNet readFromFile(final File file, final String model)
			throws JAXBException, FileNotFoundException, IOException {
		final JAXBContext ctx = JAXBContext.newInstance(XmlDocument.class);
		final Unmarshaller unmarshaller = ctx.createUnmarshaller();
		final InputStream fileInputStream = Files.newInputStream(Paths.get(file.toURI()));
		this.xmlDocument = (XmlDocument) unmarshaller.unmarshal(fileInputStream);
		fileInputStream.close();
		return getPetriNet(model);
	}

	private GraphicPetriNet getPetriNet(final String model) {
		return getNewPetriNet(this.xmlDocument.petriNet, model);
	}

	private GraphicPetriNet getNewPetriNet(final XmlPetriNet xmlPetriNet, final String model) {
		final GraphicPetriNet gPetriNet = new GraphicPetriNet(model);
		for (final XmlPlace xmlPlace : xmlPetriNet.places) {
			addNewPlace(xmlPlace, gPetriNet);
		}
		for (final XmlTransition xmlTransition : xmlPetriNet.transitions) {
			addNewTransition(xmlTransition, gPetriNet);
		}
		for (final XmlArc xmlArc : xmlPetriNet.arcs) {
			addNewArc(xmlArc, gPetriNet);
		}
		return gPetriNet;
	}

	private void addNewArc(final XmlArc xmlArc, final GraphicPetriNet gPetriNet) {
		AbstractArc arc = null;
		try {
			if ("regular".equals(xmlArc.type)) {
				arc = gPetriNet.getPetriNet().addRegArc(this.nodeReferences.get(xmlArc.sourceId).getNode(),
						this.nodeReferences.get(xmlArc.destinationId).getNode());
				arc.setMultiplicity(xmlArc.multiplicity);
			} else if ("reset".equals(xmlArc.type)) {
				arc = gPetriNet.getPetriNet().addResArc(
						((GraphicPlace) this.nodeReferences.get(xmlArc.sourceId)).getPlace(),
						((GraphicTransition) this.nodeReferences.get(xmlArc.destinationId)).getTransition());
			} else if ("inhibitory".equals(xmlArc.type)) {
				arc = gPetriNet.getPetriNet().addInhibArc(
						((GraphicPlace) this.nodeReferences.get(xmlArc.sourceId)).getPlace(),
						((GraphicTransition) this.nodeReferences.get(xmlArc.destinationId)).getTransition());
				arc.setMultiplicity(xmlArc.multiplicity);
			}
			

			final GraphicArc gArc = new GraphicArc(arc);
			gArc.setDestination(this.nodeReferences.get(xmlArc.destinationId));
			gArc.setSource(this.nodeReferences.get(xmlArc.sourceId));
			final List<Point> breakPoints = new LinkedList<>();
			for (final XmlPoint xmlPoint : xmlArc.breakPoints) {
				breakPoints.add(new Point(xmlPoint.x, xmlPoint.y));
			}
			gArc.setBreakPoints(breakPoints);
			gPetriNet.addElement(gArc);
		} catch (ResetArcMultiplicityException | UnimplementedCaseException e) {
			// should not happen since we're manipulating new objects, except if a behavior
			// is not implemented, which would return UnimplementedCaseException
			PNEditorLogger.severeLogs(e.getMessage());
		} 

	}

	private void addNewPlace(final XmlPlace xmlPlace, final GraphicPetriNet gPetriNet) {
		final AbstractPlace createdPlace = gPetriNet.getPetriNet().addAbstractPlace(xmlPlace.id);
		createdPlace.setLabel(xmlPlace.label);
		createdPlace.setTokens(xmlPlace.tokens);
		final GraphicPlace representation = new GraphicPlace(createdPlace, xmlPlace.x, xmlPlace.y);
		representation.setPlace(createdPlace);
		gPetriNet.addElement(representation);

		this.nodeReferences.put(xmlPlace.id, representation);
	}

	private void addNewTransition(final XmlTransition xmlTransition, final GraphicPetriNet gPetriNet) {
		final AbstractTransition createdTransition = gPetriNet.getPetriNet().addAbstractTransition(xmlTransition.id);
		createdTransition.setLabel(xmlTransition.label);
		final GraphicTransition representation = new GraphicTransition(createdTransition, xmlTransition.x, xmlTransition.y);
		representation.setTransition(createdTransition);
		gPetriNet.addElement(representation);

		this.nodeReferences.put(xmlTransition.id, representation);
	}
}
