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
import java.io.File;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.pneditor.editor.Root;
import org.pneditor.editor.filechooser.FileChooserDialog;
import org.pneditor.editor.filechooser.FileType;
import org.pneditor.editor.filechooser.FileTypeException;
import org.pneditor.util.GraphicsTools;
import org.pneditor.util.StringTools;
import org.pneditor.PNEConstantsConfiguration;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
@SuppressWarnings("serial")
public class ExportAction extends AbstractAction {

    private final Root root;
    private final List<FileType> fileTypes;

    public ExportAction(final Root root, final List<FileType> fileTypes) {
    	super();
        this.root = root;
        this.fileTypes = fileTypes;
        String name = "Export...";
        putValue(NAME, name);
        putValue(SMALL_ICON, GraphicsTools.getIcon(PNEConstantsConfiguration.EXPORT));
        putValue(SHORT_DESCRIPTION, name);
    }

    @Override
	public void actionPerformed(final ActionEvent e) {
    	final FileChooserDialog chooser = new FileChooserDialog();

        if (this.root.getCurrentFile() != null) {
            chooser.setSelectedFile(new File(StringTools.getExtensionCutOut(this.root.getCurrentFile().getName())));
        }

        for (final FileType fileType : this.fileTypes) {
            chooser.addChoosableFileFilter(fileType);
        }
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setCurrentDirectory(this.root.getCurrentDirectory());

        if (chooser.showDialog(this.root.getParentFrame(), "Export") == JFileChooser.APPROVE_OPTION) {
        	final File file = chooser.getSelectedFile();
        	final FileType chosenFileType = (FileType) chooser.getFileFilter();

            if (!file.exists() || JOptionPane.showOptionDialog(
                    this.root.getParentFrame(),
                    "Selected file already exists. Overwrite?",
                    "Export to " + file.getName(),
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    new String[]{"Overwrite", "Cancel"},
                    "Cancel") == JOptionPane.YES_OPTION) {
                try {
                    chosenFileType.save(this.root.getGraphicPetriNet(), file);
                } catch (FileTypeException ex) {
                    JOptionPane.showMessageDialog(this.root.getParentFrame(), ex.getMessage());
                }
            }
            this.root.setCurrentDirectory(chooser.getCurrentDirectory());
        }
    }
}
