/*
 * Copyright (C) 2012 milka
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
package org.pneditor.editor.actions.algorithms;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import org.pneditor.editor.Root;
import org.pneditor.petrinet.ResetArcMultiplicityException;

import logger.PNEditorLogger;

/**
 *
 * @author milka
 */
@SuppressWarnings("serial")
public class BoundednessAction extends AbstractAction {

    private final Root root;

    public BoundednessAction(final Root root) {
    	super();
        this.root = root;
        String name = "Boundedness";
        putValue(NAME, name);
        putValue(SHORT_DESCRIPTION, name);
        setEnabled(true);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        try {
			if (this.root.getPetriNet().isBounded()) {
			    JOptionPane.showMessageDialog(this.root.getParentFrame(), "PetriNet is NOT bounded ", "Algorithm output", JOptionPane.INFORMATION_MESSAGE);
			} else {
			    JOptionPane.showMessageDialog(this.root.getParentFrame(), "PetriNet is bounded", "Algorithm output", JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (HeadlessException e1) {
			PNEditorLogger.warningLogs(e1.getMessage());
		} catch (ResetArcMultiplicityException e2) {
			PNEditorLogger.severeLogs(e2.getMessage());
		}
    }



}
