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
package org.pneditor.editor;

import java.util.HashSet;
import java.util.Set;

import org.pneditor.editor.gpetrinet.GraphicArc;
import org.pneditor.editor.gpetrinet.GraphicElement;
import org.pneditor.editor.gpetrinet.GraphicNode;
import org.pneditor.editor.gpetrinet.GraphicPetriNet;


/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class LocalClipboard {

    private final GraphicPetriNet gPetriNet;

    LocalClipboard() {
    	this.gPetriNet = new GraphicPetriNet();
    }

    LocalClipboard(final String model) {
    	this.gPetriNet = new GraphicPetriNet(model);
    }

	public void setContents(final Set<GraphicElement> elements) {
        this.gPetriNet.removeElements();
        this.gPetriNet.addAll(elements);
        final Set<GraphicElement> filteredElements = filterOutDisconnectedArcs(elements);
        this.gPetriNet.removeElements();
        this.gPetriNet.addAll(filteredElements);
    }

    public Set<GraphicElement> getContents() {
        return new HashSet<GraphicElement>(this.gPetriNet.getElements());
    }

    public boolean isEmpty() {
        return this.gPetriNet.getElements().isEmpty();
    }

    private Set<GraphicElement> filterOutDisconnectedArcs(final Set<GraphicElement> elements) {
    	final Set<GraphicElement> filteredElements = new HashSet<>();
    	final Set<GraphicNode> nodes = getNodes(elements);
        for (final GraphicNode node : nodes) {
        	final Set<GraphicArc> connectedArcEdges = this.gPetriNet.getConnectedGraphicArcs(node);
            for (final GraphicArc connectedArcEdge : connectedArcEdges) {
                if (nodes.contains(connectedArcEdge.getSource()) && nodes.contains(connectedArcEdge.getDestination())) {
                    filteredElements.add(connectedArcEdge);
                }
            }
        }
        filteredElements.addAll(nodes);
        return filteredElements;
    }

    public Set<GraphicNode> getNodes(final Set<GraphicElement> elements) {
    	final Set<GraphicNode> nodes = new HashSet<>();
        for (final GraphicElement element : elements) {
            if (element.isNode()) {
                nodes.add((GraphicNode) element);
            }
        }
        return nodes;
    }

}
