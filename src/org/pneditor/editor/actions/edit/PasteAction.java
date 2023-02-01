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
package org.pneditor.editor.actions.edit;

import java.awt.event.ActionEvent;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import org.pneditor.editor.Root;
import org.pneditor.editor.commands.PasteCommand;
import org.pneditor.editor.gpetrinet.GraphicElement;
import org.pneditor.editor.gpetrinet.GraphicPetriNet;
import org.pneditor.util.GraphicsTools;
import org.pneditor.PNEConstantsConfiguration;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
@SuppressWarnings("serial")
public class PasteAction extends AbstractAction {

    private final Root root;

    public PasteAction(final Root root) {
    	super();
        this.root = root;
        String name = "Paste";
        putValue(NAME, name);
        putValue(SMALL_ICON, GraphicsTools.getIcon(PNEConstantsConfiguration.PASTE16));
        putValue(SHORT_DESCRIPTION, name);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl V"));
        setEnabled(false);
    }

    @Override
	public void actionPerformed(final ActionEvent e) {
    	final GraphicPetriNet gPetriNet = this.root.getGraphicPetriNet();
        final Set<GraphicElement> copiedElements = this.root.getClipboard().getContents();
        this.root.setClickedElement(null);
        this.root.getSelection().clear();
        this.root.getUndoManager().executeCommand(new PasteCommand(copiedElements, gPetriNet, this.root));
        //TODO: getViewTranslation()
        this.root.refreshAll();
    }

}
