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
package org.pneditor.editor.filechooser;

import java.io.File;
import java.io.IOException;
import javax.swing.Icon;
import javax.xml.bind.JAXBException;

import org.pneditor.editor.gpetrinet.GraphicPetriNet;
import org.pneditor.save.xml.DocumentExporter;
import org.pneditor.save.xml.DocumentImporter;
import org.pneditor.util.GraphicsTools;
import org.pneditor.PNEConstantsConfiguration;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class PflowFileType extends FileType {

    @Override
    public String getName() {
        return "PetriFlow";
    }

    @Override
    public String getExtension() {
        return "pflow";
    }

    @Override
    public Icon getIcon() {
        return GraphicsTools.getIcon(PNEConstantsConfiguration.PFLOW);
    }


    @Override
    public void save(final GraphicPetriNet petriNet, final File file) throws FileTypeException {
        try {
            new DocumentExporter(petriNet).writeToFile(file);
        } catch (JAXBException ex) {
            if (!file.exists()) {
                throw new FileTypeException("File not found.");
            } else if (file.canRead()) {
                throw new FileTypeException("Selected file is not compatible.");
            } else {
                throw new FileTypeException("File can not be read.");
            }
        }
    }

    @Override
    public GraphicPetriNet load(final File file, final String model) throws FileTypeException {
        try {
        	final GraphicPetriNet petriNet = new DocumentImporter().readFromFile(file, model);
            petriNet.setViewTranslationToCenter();
            return petriNet;
        } catch (JAXBException ex) {
        	if (!file.exists()) {
                throw new FileTypeException("File not found.");
            } else if (file.canRead()) {
                throw new FileTypeException("Selected file is not compatible.");
            } else {
                throw new FileTypeException("File can not be read.");
            }
        } catch (IOException ex) {
            throw new FileTypeException(ex.getMessage());
        }
    }
}
