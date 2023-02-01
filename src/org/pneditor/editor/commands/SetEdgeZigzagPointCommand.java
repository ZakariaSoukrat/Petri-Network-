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
import java.util.List;

import org.pneditor.editor.gpetrinet.GraphicArc;
import org.pneditor.util.Command;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class SetEdgeZigzagPointCommand implements Command {

    private final GraphicArc gArc;
    private final Point startingLocation;
    private final Point targetLocation;
    private final List<Point> oldBreakPoints;

    public SetEdgeZigzagPointCommand(final GraphicArc gArc, final Point startingLocation, final Point targetLocation) {
        this.gArc = gArc;
        this.startingLocation = new Point(startingLocation);
        this.targetLocation = new Point(targetLocation);
        this.oldBreakPoints = this.gArc.getBreakPointsCopy();
    }

    @Override
	public void execute() {
        this.gArc.addOrGetBreakPoint(new Point(this.startingLocation)).setLocation(this.targetLocation);
        this.gArc.cleanupUnecessaryBreakPoints();
    }

    @Override
	public void undo() {
        this.gArc.setBreakPoints(this.oldBreakPoints);
    }

    @Override
	public void redo() {
    	execute();
    }

    @Override
    public String toString() {
        return "Set arc break point";
    }

}
