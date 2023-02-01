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
package org.pneditor.editor.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import org.pneditor.editor.Root;
import org.pneditor.util.GraphicsTools;
import org.pneditor.PNEConstantsConfiguration;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
@SuppressWarnings("serial")
public class AboutAction extends AbstractAction {

    private final Root root;

    public AboutAction(final Root root) {
    	super();
        this.root = root;
        String name = "About...";
        putValue(NAME, name);
        putValue(SMALL_ICON, GraphicsTools.getIcon(PNEConstantsConfiguration.ABOUT16));
        putValue(SHORT_DESCRIPTION, name);
    }

    @Override
	public void actionPerformed(final ActionEvent e) {
        JOptionPane.showOptionDialog(
                this.root.getParentFrame(),
                this.root.getAppLongName() + "\n"
                + "http://www.pneditor.org/\n"
                + "\n"
                + "Author: Martin Riesz\n"
                + "Contributors:\n"
                + "Milka Knapereková (boundedness algorithm)\n"
                + "Ján Tančibok (reset and inhibitor arc types)\n"
                + "\n"
                + "Contributions are welcome. Just send a pull request on GitHub."
                + "\n"
                + "This program is free software: you can redistribute it and/or modify\n"
                + "it under the terms of the GNU General Public License as published by\n"
                + "the Free Software Foundation, either version 3 of the License, or\n"
                + "(at your option) any later version.\n"
                + "\n"
                + "This program is distributed in the hope that it will be useful,\n"
                + "but WITHOUT ANY WARRANTY; without even the implied warranty of\n"
                + "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n"
                + "GNU General Public License for more details.\n"
                + "You should have received a copy of the GNU General Public License\n"
                + "along with this program.  If not, see <http://www.gnu.org/licenses/>.",
                "About",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"OK"},
                "OK");
    }

}
