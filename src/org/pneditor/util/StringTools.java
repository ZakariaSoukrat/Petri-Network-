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
package org.pneditor.util;

import java.io.File;
import java.util.Locale;

/**
 * This class contains some simple string management functions.
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class StringTools {

    /**
     * Returns extension of a file i.e. from File("example.html.txt") returns
     * "txt"
     *
     * @param file file to get the extension from
     * @return extension of the file
     */
    public static String getExtension(final File file) {
        return getExtension(file.getName());
    }

    private static String getExtension(final String filename) {
        String ext = null;
        final int i = filename.lastIndexOf('.');

        if (i > 0 && i < filename.length() - 1) {
            ext = filename.substring(i + 1).toLowerCase(Locale.ENGLISH);
        }
        if (ext != null) {
            return ext;
        }
		return "";
    }

    public static String getExtensionCutOut(final String filename) {
    	final String extension = getExtension(filename);
    	return filename.substring(0, filename.length() - 1 - extension.length());
    }
}
