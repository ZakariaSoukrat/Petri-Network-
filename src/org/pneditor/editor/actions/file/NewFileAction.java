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
package org.pneditor.editor.actions.file;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import org.pneditor.editor.Root;
import org.pneditor.editor.gpetrinet.GraphicPetriNet;
import org.pneditor.util.GraphicsTools;
import org.pneditor.PNEConstantsConfiguration;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
@SuppressWarnings("serial")
public class NewFileAction extends AbstractAction {

    private final Root root;

    public NewFileAction(final Root root) {
    	super();
        this.root = root;
        String name = "New";
        putValue(NAME, name);
        putValue(SMALL_ICON, GraphicsTools.getIcon(PNEConstantsConfiguration.NEW16));
        putValue(SHORT_DESCRIPTION, name);
        putValue(MNEMONIC_KEY, KeyEvent.VK_N);
    }

    @Override
	public void actionPerformed(final ActionEvent e) {
        if (!this.root.isModified() || JOptionPane.showOptionDialog(
                this.root.getParentFrame(),
                "Any unsaved changes will be lost. Continue?",
                "New file",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                new String[]{"New file", "Cancel"},
                "Cancel") == JOptionPane.YES_OPTION) {
            this.root.setGraphicPetriNet(new GraphicPetriNet(this.root.getCurrentModel()));
            this.root.setClickedElement(null);
            this.root.refreshAll();
            this.root.getUndoManager().eraseAll();
            this.root.setCurrentFile(null);
            this.root.setModified(false);
        }
    }
}
