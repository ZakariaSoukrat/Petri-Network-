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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.SystemColor;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class Preview extends JPanel implements PropertyChangeListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6657841190121747227L;
	private ImageIcon thumbnail;
    private File file;
    public static final int PREFERRED_WIDTH = 200;
    public static final int PREFERRED_HEIGHT = 200;

    public Preview(final JFileChooser fileChooser) {
        super();
        setPreferredSize(new Dimension(Preview.PREFERRED_WIDTH, Preview.PREFERRED_HEIGHT));
        fileChooser.addPropertyChangeListener(this);
    }

    @Override
	public void propertyChange(final PropertyChangeEvent e) {
    	final String propertyName = e.getPropertyName();
        boolean update = false;

        //If the directory changed, don't show an image.
        if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(propertyName)) {
            this.file = null;
            update = true;

            //If a file became selected, find out which one.
        } else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(propertyName)) {
            this.file = (File) e.getNewValue();
            update = true;
        }

        //Update the preview accordingly.
        if (update) {
            this.thumbnail = null;
            if (isShowing()) {
                loadImage();
                repaint();
            }
        }
    }

    private void loadImage() {
        if (this.file == null) {
            this.thumbnail = null;
            return;
        }
        final FileType fileType = FileType.getAcceptingFileType(this.file, FileType.getAllFileTypes());
        if (fileType == null) {
            this.thumbnail = null;
            return;
        }
        final BufferedImage image = fileType.getPreview(this.file);
        if (image == null) {
            this.thumbnail = null;
            return;
        }
        if (image.getWidth() > image.getHeight()) {
            this.thumbnail = new ImageIcon(image.getScaledInstance(Preview.PREFERRED_WIDTH, -1, Image.SCALE_SMOOTH), "");
        } else {
            this.thumbnail = new ImageIcon(image.getScaledInstance(-1, Preview.PREFERRED_HEIGHT, Image.SCALE_SMOOTH), "");
        }

    }

    @Override
    protected void paintComponent(final Graphics g) {
        if (this.thumbnail == null) {
            loadImage();
            setBackground(SystemColor.control);
            super.paintComponent(g);
        } else {
            setBackground(Color.white);
            super.paintComponent(g);
            int x = getWidth() / 2 - this.thumbnail.getIconWidth() / 2;
            int y = getHeight() / 2 - this.thumbnail.getIconHeight() / 2;

            if (y < 0) {
                y = 0;
            }
            if (x < 0) {
                x = 0;
            }
            this.thumbnail.paintIcon(this, g, x, y);
        }
    }
}
