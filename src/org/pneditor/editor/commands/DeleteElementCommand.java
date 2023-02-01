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
import org.pneditor.editor.gpetrinet.GraphicElement;
import org.pneditor.editor.gpetrinet.GraphicPetriNet;
import org.pneditor.editor.gpetrinet.GraphicPlace;
import org.pneditor.editor.gpetrinet.GraphicTransition;
import org.pneditor.util.Command;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class DeleteElementCommand implements Command {

    private Command deleteElement;

    public DeleteElementCommand(final GraphicElement element, final GraphicPetriNet petriNet) {
        if (element.isPlace()) {
            this.deleteElement = new DeletePlaceCommand((GraphicPlace) element, petriNet);
        } else if (element.isTransition()) {
            this.deleteElement = new DeleteTransitionCommand((GraphicTransition) element, petriNet);
        } else if (!element.isNode()) {
            this.deleteElement = new DeleteArcCommand((GraphicArc) element, petriNet);
        }
    }

    @Override
	public void execute() {
        if (this.deleteElement != null) {
            this.deleteElement.execute();
        }
    }

    @Override
	public void undo() {
        if (this.deleteElement != null) {
            this.deleteElement.undo();
        }
    }

    @Override
	public void redo() {
        if (this.deleteElement != null) {
            this.deleteElement.redo();
        }
    }

    @Override
    public String toString() {
        return this.deleteElement.toString();
    }
}