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
import org.pneditor.editor.commands.SetTokensCommand;
import org.pneditor.editor.gpetrinet.GraphicPlace;
import org.pneditor.util.GraphicsTools;
import org.pneditor.PNEConstantsConfiguration;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
@SuppressWarnings("serial")
public class SetTokensAction extends AbstractAction {

	private final Root root;

	public SetTokensAction(final Root root) {
		super();
		this.root = root;
		String name = "Set tokens";
		putValue(NAME, name);
		putValue(SMALL_ICON, GraphicsTools.getIcon(PNEConstantsConfiguration.TOKENS));
		putValue(SHORT_DESCRIPTION, name);
		putValue(MNEMONIC_KEY, KeyEvent.VK_T);
		setEnabled(false);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (this.root.getClickedElement() != null && this.root.getClickedElement().isPlace()) {
			final GraphicPlace place = (GraphicPlace) this.root.getClickedElement();
			int tokens = place.getPlace().getTokens();

			final String response = JOptionPane.showInputDialog(this.root.getParentFrame(), "Tokens:", tokens);
			if (response != null) {
				try {
					tokens = Integer.parseInt(response);
				} catch (NumberFormatException exception) {
					JOptionPane.showMessageDialog(this.root.getParentFrame(),
							exception.getMessage() + " is not a number");
				}

				if (tokens < 0) {
					tokens = place.getPlace().getTokens(); // restore old value
					JOptionPane.showMessageDialog(this.root.getParentFrame(), "Number of tokens must be non-negative");
					// TODO : check with model?
				}
			}

			if (place.getPlace().getTokens() != tokens) {
				this.root.getUndoManager().executeCommand(new SetTokensCommand(place, tokens));
			}

		}
	}
}
