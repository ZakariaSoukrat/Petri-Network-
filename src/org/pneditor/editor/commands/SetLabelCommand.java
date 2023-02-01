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

import org.pneditor.editor.gpetrinet.GraphicNode;
import org.pneditor.petrinet.AbstractNode;
import org.pneditor.util.Command;

/**
 * Set label to clicked element
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class SetLabelCommand implements Command {

    private AbstractNode node;
    private String newLabel;
    private String oldLabel;
	private GraphicNode gNode;

    public SetLabelCommand(GraphicNode gNode, String newLabel) {
        this.gNode = gNode;
        this.newLabel = newLabel;
        this.oldLabel = gNode.getLabel();
    }

    @Override
	public void execute() {
    	this.node = this.gNode.getNode();
        this.node.setLabel(this.newLabel);
    }

    @Override
	public void undo() {
    	this.node = this.gNode.getNode();
        this.node.setLabel(this.oldLabel);
    }

    @Override
	public void redo() {
        execute();
    }

    @Override
    public String toString() {
        return "Set label to " + this.newLabel;
    }

}
