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
 * Set tokens to clicked place node
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class SetTokensCommand implements Command {

    private final GraphicPlace gPlace;
    
    private final int newValue;
    private final int oldValue;

    public SetTokensCommand(final GraphicPlace gPlace, final int tokens) {
        this.gPlace = gPlace;
        this.newValue = tokens;
        this.oldValue = gPlace.getPlace().getTokens();
    }

    @Override
	public void execute() {
    	this.gPlace.getPlace().setTokens(this.newValue);
    }

    @Override
	public void undo() {
    	this.gPlace.getPlace().setTokens(this.oldValue);
    }

    @Override
	public void redo() {
        this.gPlace.getPlace().setTokens(this.newValue);
    }

    @Override
    public String toString() {
        return "Set tokens";
    }
}
