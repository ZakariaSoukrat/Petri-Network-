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
import org.pneditor.editor.commands.DeleteArcCommand;
import org.pneditor.editor.commands.SetArcMultiplicityCommand;
import org.pneditor.editor.gpetrinet.GraphicArc;
import org.pneditor.petrinet.ResetArcMultiplicityException;
import org.pneditor.util.GraphicsTools;
import org.pneditor.PNEConstantsConfiguration;

import logger.PNEditorLogger;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
@SuppressWarnings("serial")
public class SetArcMultiplicityAction extends AbstractAction {

	private final Root root;

	public SetArcMultiplicityAction(final Root root) {
		super();
		this.root = root;
		String name = "Set arc multiplicity";
		putValue(NAME, name);
		putValue(SMALL_ICON, GraphicsTools.getIcon(PNEConstantsConfiguration.MULTIPLICITY));
		putValue(SHORT_DESCRIPTION, name);
		putValue(MNEMONIC_KEY, KeyEvent.VK_M);
		setEnabled(false);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (this.root.getClickedElement() != null && !this.root.getClickedElement().isNode()) {
			final GraphicArc arc = (GraphicArc) this.root.getClickedElement();
			int multiplicity = 0; // TODO !!
			try {
				multiplicity = arc.getArc().getMultiplicity();
			} catch (ResetArcMultiplicityException e1) {
				PNEditorLogger.severeLogs(e1.getMessage());
			}
			final String response = JOptionPane.showInputDialog(this.root.getParentFrame(), "Multiplicity:",
					multiplicity);
			if (response != null) {
				try {
					multiplicity = Integer.parseInt(response);
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(this.root.getParentFrame(), ex.getMessage() + " is not a number");
				}
			}

			try {
				if (arc.getArc().getMultiplicity() != multiplicity) {
					if (multiplicity < 1) {
						this.root.getUndoManager()
								.executeCommand(new DeleteArcCommand(arc, this.root.getGraphicPetriNet()));
					} else {
						this.root.getUndoManager().executeCommand(new SetArcMultiplicityCommand(arc, multiplicity));
					}
				}
			} catch (ResetArcMultiplicityException e1) {
				PNEditorLogger.severeLogs(e1.getMessage());
			}

		}
	}
}
