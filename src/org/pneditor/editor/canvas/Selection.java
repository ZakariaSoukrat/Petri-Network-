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
package org.pneditor.editor.canvas;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.pneditor.editor.gpetrinet.GraphicElement;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class Selection implements Iterable<GraphicElement> {

	private final Set<GraphicElement> selectedElements = new HashSet<>();
    private SelectionChangedListener selectionChangedListener;

    public void setSelectionChangedListener(final SelectionChangedListener selectionChangedListener) {
        this.selectionChangedListener = selectionChangedListener;
    }

    public void clear() {
        this.selectedElements.clear();
        selectionChanged();
    }

    public void add(final GraphicElement element) {
        this.selectedElements.add(element);
        selectionChanged();
    }

    public void addAll(final Collection<? extends GraphicElement> elements) {
        this.selectedElements.addAll(elements);
        selectionChanged();
    }

    public boolean isEmpty() {
        return this.selectedElements.isEmpty();
    }

    public boolean contains(final GraphicElement element) {
        return this.selectedElements.contains(element);
    }

    public void selectionChanged() {
        if (this.selectionChangedListener != null) {
            this.selectionChangedListener.selectionChanged();
        }
    }

    @Override
	public Iterator<GraphicElement> iterator() {
        return this.selectedElements.iterator();
    }

    public Set<GraphicElement> getElements() {
        return this.selectedElements;
    }

}
