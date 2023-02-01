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

import java.util.HashSet;
import java.util.Set;

import org.pneditor.editor.gpetrinet.GraphicElement;
import org.pneditor.editor.gpetrinet.GraphicPetriNet;
import org.pneditor.util.Command;

/**
 * Delete clicked and selected elements
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class DeleteElementsCommand implements Command {

    private Set<Command> deleteAllElements = new HashSet<>();

    public DeleteElementsCommand(final Set<GraphicElement> elementsToDelete, final GraphicPetriNet petriNet) {
        for (final GraphicElement element : elementsToDelete) {
            this.deleteAllElements.add(new DeleteElementCommand(element, petriNet));
        }
    }

    @Override
	public void execute() {
        for (final Command deleteElement : this.deleteAllElements) {
            deleteElement.execute();
        }
    }

    @Override
	public void undo() {
        for (final Command deleteElement : this.deleteAllElements) {
            deleteElement.undo();
        }
    }

    @Override
	public void redo() {
        for (final Command deleteElement : this.deleteAllElements) {
            deleteElement.redo();
        }
    }

    @Override
    public String toString() {
        if (this.deleteAllElements.size() == 1) {
            return this.deleteAllElements.iterator().next().toString();
        }
        return "Delete elements";
    }

}
