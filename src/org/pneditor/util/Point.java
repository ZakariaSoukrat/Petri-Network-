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
package org.pneditor.util;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class Point {

    private java.awt.Point point;

    public Point(final int x, final int y) {
        this.point = new java.awt.Point(x, y);
    }

    public Point(final java.awt.Point point) {
        this.point = point;
    }

    public Point() {
        this(0, 0);
    }

    public int getX() {
        return this.point.x;
    }

    public int getY() {
        return this.point.y;
    }

    public double distance(final Point pointTo) {
        return this.point.distance(pointTo.point);
    }

    public Point getTranslated(final int dx, final int dy) {
        return new Point(getX() + dx, getY() + dy);
    }

    public Point getTranslated(final Point pointToTranslate) {
        return new Point(getX() + pointToTranslate.getX(), getY() + pointToTranslate.getY());
    }

    public Point getNegative() {
        return new Point(-getX(), -getY());
    }

    public java.awt.Point getPoint() {
        return new java.awt.Point(this.point);
    }

    @Override
    public String toString() {
        return "[" + this.point.x + ", " + this.point.y + "]";
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Point other = (Point) obj;
        if (this.point != other.point && (this.point == null || !this.point.equals(other.point))) { //TODO: check
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
    	return 33 + (this.point != null ? this.point.hashCode() : 0);
    }

}
