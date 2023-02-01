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
import java.awt.Rectangle;
import java.io.File; 
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.pneditor.editor.gpetrinet.GraphicArc;
import org.pneditor.editor.gpetrinet.GraphicElement;
import org.pneditor.editor.gpetrinet.GraphicPetriNet;
import org.pneditor.editor.gpetrinet.GraphicPlace;
import org.pneditor.editor.gpetrinet.GraphicTransition;
import org.pneditor.petrinet.AbstractArc;
import org.pneditor.petrinet.ResetArcMultiplicityException;

import logger.PNEditorLogger;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public final class DocumentExporter {

	private final XmlDocument xmlDocument = new XmlDocument();
	
	public DocumentExporter(final GraphicPetriNet petriNet) {
		this.xmlDocument.petriNet = getXmlPetriNet(petriNet);
		final Rectangle bounds = petriNet.getBounds();
		this.xmlDocument.left = bounds.x;
		this.xmlDocument.top = bounds.y;
	}

	public void writeToFile(final File file) throws JAXBException {
		final JAXBContext ctx = JAXBContext.newInstance(XmlDocument.class);
		final Marshaller marshaller = ctx.createMarshaller();
		marshaller.setProperty("jaxb.formatted.output", true);
		try {
			marshaller.marshal(this.xmlDocument, Files.newOutputStream(Paths.get(file.toURI())));
		} catch (IOException e) {
			PNEditorLogger.severeLogs(e.getMessage());
		}
	}

	public XmlPetriNet getXmlPetriNet(final GraphicPetriNet petriNet) {
		final XmlPetriNet xmlPetriNet = new XmlPetriNet();
		xmlPetriNet.label = "PetriNet";
		for (final GraphicElement element : petriNet.getElements()) {
			if (element.isTransition()) {
				xmlPetriNet.addTransition(getXmlTransition((GraphicTransition) element));
			} else if (element.isPlace()) {
				xmlPetriNet.addPlace(getXmlPlace((GraphicPlace) element));
			} else if (!element.isNode()) {
				xmlPetriNet.addArc(getXmlArc((GraphicArc) element));
			}
		}
		return xmlPetriNet;
	}

	private XmlPlace getXmlPlace(final GraphicPlace place) {
		final XmlPlace xmlPlace = new XmlPlace();
		xmlPlace.id = place.getPlace().getId();
		xmlPlace.x = place.getCenter().x;
		xmlPlace.y = place.getCenter().y;
		xmlPlace.label = place.getLabel();
		xmlPlace.tokens = place.getPlace().getTokens();
		return xmlPlace;
	}

	private XmlTransition getXmlTransition(final GraphicTransition transition) {
		final XmlTransition xmlTransition = new XmlTransition();
		xmlTransition.id = transition.getTransition().getId();
		xmlTransition.x = transition.getCenter().x;
		xmlTransition.y = transition.getCenter().y;
		xmlTransition.label = transition.getLabel();
		return xmlTransition;
	}

	private XmlArc getXmlArc(final GraphicArc gArc) {
		final AbstractArc arc = gArc.getArc();
		final XmlArc xmlArc = new XmlArc();
		xmlArc.sourceId = arc.getSource().getId();
		xmlArc.destinationId = arc.getDestination().getId();
		xmlArc.type = arc.isRegular() ? "regular" : arc.isInhibitory() ? "inhibitory" : "reset";
		if (!arc.isReset()) {
			try {
				xmlArc.multiplicity = arc.getMultiplicity();
			} catch (ResetArcMultiplicityException e) {
				PNEditorLogger.severeLogs(e.getMessage());
			}
		}
		final List<Point> breakPoints = gArc.getBreakPoints();
		for (final Point point : breakPoints) {
			final XmlPoint xmlPoint = new XmlPoint();
			xmlPoint.x = point.x;
			xmlPoint.y = point.y;
			xmlArc.breakPoints.add(xmlPoint);
		}
		return xmlArc;
	}
}
