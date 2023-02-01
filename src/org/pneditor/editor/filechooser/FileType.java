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

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;

import org.pneditor.editor.gpetrinet.GraphicPetriNet;
import org.pneditor.util.StringTools;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public abstract class FileType extends FileFilter {

    public static Set<FileType> getAllFileTypes() {
    	final Set<FileType> allFileTypes = new HashSet<>();
        allFileTypes.add(new EpsFileType());
        allFileTypes.add(new PflowFileType());
        allFileTypes.add(new PngFileType());
        allFileTypes.add(new ViptoolPnmlFileType());
        return allFileTypes;
    }

    public abstract String getExtension();

    public abstract String getName();

    public abstract void save(GraphicPetriNet petriNet, File file) throws FileTypeException;

    public abstract GraphicPetriNet load(File file, String model) throws FileTypeException;

    public abstract Icon getIcon();

    public BufferedImage getPreview(File file) {
        try {
        	final GraphicPetriNet gPetriNet = load(file, null); //TODO: may need to add the model for the load if models are different
        	return gPetriNet.getPreview();
        } catch (FileTypeException ex) {
            return null;
        }
    }

    @Override
	public String getDescription() {
        return getName() + " (*." + getExtension() + ")";
    }

    @Override
    public boolean accept(final File file) {
        if (file.isDirectory()) { //Show also directories in the filters
            return true;
        }

        final String extension = StringTools.getExtension(file);
        return extension != null && extension.equals(getExtension());
    }

    public static FileType getAcceptingFileType(final File file, final Collection<FileType> fileTypes) {
        for (final FileType fileType : fileTypes) {
            if (fileType.accept(file)) {
                return fileType;
            }
        }
        return null;
    }
}
