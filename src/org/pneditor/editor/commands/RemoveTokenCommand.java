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

import org.pneditor.editor.gpetrinet.GraphicPlace;
import org.pneditor.util.Command;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class RemoveTokenCommand implements Command {

	final private GraphicPlace gPlace;

    public RemoveTokenCommand(final GraphicPlace gPlace) {
        this.gPlace = gPlace;
    }

    @Override
	public void execute() {
    	this.gPlace.getPlace().removeToken();
    }

    @Override
	public void undo() {
    	this.gPlace.getPlace().addToken();
    }

    @Override
	public void redo() {
        execute();
    }

    @Override
    public String toString() {
        return "Remove token";
    }

}
