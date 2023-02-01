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
import java.io.File;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import org.pneditor.editor.Root;
import org.pneditor.editor.filechooser.FileType;
import org.pneditor.editor.filechooser.FileTypeException;
import org.pneditor.util.GraphicsTools;
import org.pneditor.PNEConstantsConfiguration;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
@SuppressWarnings("serial")
public class SaveAction extends AbstractAction {

    private final Root root;
    private final List<FileType> fileTypes;

    public SaveAction(final Root root, final List<FileType> fileTypes) {
    	super();
        this.root = root;
        this.fileTypes = fileTypes;
        String name = "Save";
        putValue(NAME, name);
        putValue(SMALL_ICON, GraphicsTools.getIcon(PNEConstantsConfiguration.SAVE16));
        putValue(SHORT_DESCRIPTION, name);
        putValue(MNEMONIC_KEY, KeyEvent.VK_S);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl S"));
    }

    @Override
	public void actionPerformed(final ActionEvent e) {
    	final File file = this.root.getCurrentFile();
        if (file != null) {
            try {
            	final FileType fileType = FileType.getAcceptingFileType(file, this.fileTypes);
                fileType.save(this.root.getGraphicPetriNet(), file);
                this.root.setModified(false);
            } catch (FileTypeException ex) {
                JOptionPane.showMessageDialog(this.root.getParentFrame(), ex.getMessage());
            }
        } else {
            new SaveFileAsAction(this.root, this.fileTypes).actionPerformed(e);
        }

    }

}
