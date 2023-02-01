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
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.Icon;

import org.pneditor.editor.gpetrinet.GraphicPetriNet;
import org.pneditor.util.GraphicsTools;
import org.pneditor.PNEConstantsConfiguration;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class PngFileType extends FileType {

    @Override
    public String getExtension() {
        return "png";
    }

    @Override
    public String getName() {
        return "Portable Network Graphics";
    }

    @Override
    public Icon getIcon() {
        return GraphicsTools.getIcon(PNEConstantsConfiguration.PNG);
    }

    @Override
    public GraphicPetriNet load(final File file, final String model) throws FileTypeException {
        throw new UnsupportedOperationException("Loading not supported.");
    }

    @Override
    public BufferedImage getPreview(final File file) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(file);
        } catch (IOException ex) {
        }
        return image;
    }

    @Override
    public void save(final GraphicPetriNet petriNet, final File file) throws FileTypeException {
        try {
            final BufferedImage bufferedImage = petriNet.getPreview();
            ImageIO.write(bufferedImage, "png", file);
        } catch (IOException ex) {
            throw new FileTypeException(ex.getMessage());
        }
    }

}
