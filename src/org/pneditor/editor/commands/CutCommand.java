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

import java.util.Set;

import org.pneditor.editor.gpetrinet.GraphicElement;
import org.pneditor.editor.gpetrinet.GraphicPetriNet;
import org.pneditor.util.Command;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class CutCommand implements Command {

	final private Command deleteElements;

    public CutCommand(final Set<GraphicElement> elementsToDelete, final GraphicPetriNet petriNet) {
        this.deleteElements = new DeleteElementsCommand(elementsToDelete, petriNet);
    }

    @Override
	public void execute() {
        this.deleteElements.execute();
    }

    @Override
	public void undo() {
        this.deleteElements.undo();
    }

    @Override
	public void redo() {
        this.deleteElements.redo();
    }

    @Override
    public String toString() {
        return "Cut";
    }

}
