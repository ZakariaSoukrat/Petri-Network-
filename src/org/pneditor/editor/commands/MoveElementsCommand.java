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

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import org.pneditor.editor.gpetrinet.GraphicElement;
import org.pneditor.util.Command;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class MoveElementsCommand implements Command {

    private final Set<Command> moveElements = new HashSet<>();

    public MoveElementsCommand(final Set<GraphicElement> elements, final Point deltaPosition) {
        for (final GraphicElement element : elements) {
            this.moveElements.add(new MoveElementCommand(element, deltaPosition));
        }
    }

    @Override
	public void execute() {
        for (final Command moveElement : this.moveElements) {
            moveElement.execute();
        }
    }

    @Override
	public void undo() {
        for (final Command moveElement : this.moveElements) {
            moveElement.undo();
        }
    }

    @Override
	public void redo() {
        for (final Command moveElement : this.moveElements) {
            moveElement.redo();
        }
    }

    @Override
    public String toString() {
        if (this.moveElements.size() == 1) {
            return this.moveElements.iterator().next().toString();
        }
        return "Move elements";
    }

}
