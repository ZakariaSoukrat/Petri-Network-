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
package org.pneditor.editor.actions.element;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import org.pneditor.editor.Root;
import org.pneditor.editor.commands.SetLabelCommand;
import org.pneditor.editor.gpetrinet.GraphicNode;
import org.pneditor.util.GraphicsTools;
import org.pneditor.PNEConstantsConfiguration;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
@SuppressWarnings("serial")
public class SetLabelAction extends AbstractAction {

    private final Root root;

    public SetLabelAction(final Root root) {
    	super();
        this.root = root;
        String name = "Set label";
        putValue(NAME, name);
        putValue(SMALL_ICON, GraphicsTools.getIcon(PNEConstantsConfiguration.LABEL));
        putValue(SHORT_DESCRIPTION, name);
		putValue(MNEMONIC_KEY, KeyEvent.VK_L);
        setEnabled(false);
    }

    @Override
	public void actionPerformed(final ActionEvent e) {
        if (this.root.getClickedElement() != null
                && this.root.getClickedElement().isNode()) {
        	final GraphicNode clickedNode = (GraphicNode) this.root.getClickedElement();
        	final String newLabel = JOptionPane.showInputDialog(this.root.getParentFrame(), "New label:", clickedNode.getNode().getLabel());

            if (newLabel != null && !newLabel.equals(clickedNode.getNode().getLabel())) {
                this.root.getUndoManager().executeCommand(new SetLabelCommand(clickedNode, newLabel));
            }

        }
    }
}
