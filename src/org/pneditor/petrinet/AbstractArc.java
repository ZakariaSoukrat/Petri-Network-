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
package org.pneditor.petrinet;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public abstract class AbstractArc  {

	public abstract AbstractNode getSource();

	public abstract AbstractNode getDestination();

	public boolean isSourceAPlace() {
		return getSource().isPlace();
	}

	public abstract boolean isReset();
	
	public abstract boolean isRegular();

	public abstract boolean isInhibitory();
	
	public abstract int getMultiplicity() throws ResetArcMultiplicityException;

	public abstract void setMultiplicity(int multiplicity) throws ResetArcMultiplicityException;

}
