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
package org.pneditor.workflow;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import org.pneditor.petrinet.models.initial.Transition;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class WorkflowLogger {

    private static String defaultLogDirectory = System.getProperty("user.home") + File.separator+"logs";

    public static void log(final String dirName, final String workflowFilename, final String caseId, final Transition transition, final String userId) throws IOException {
    	final File directory = new File(dirName);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        final File file = new File(dirName + File.separator + workflowFilename + ".log");
        final OutputStream fileOutputStream = Files.newOutputStream(Paths.get(file.toURI()));
        final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        final PrintStream out = new PrintStream(bufferedOutputStream); 
        out.println(caseId + "  " + new Date().getTime() + "    " + userId + "  " + transition.getLabel());
        bufferedOutputStream.close();
        fileOutputStream.close();
    }

    public static void log(final String workflowFilename, final String caseId, final Transition transition, final String userId) throws IOException {
        log(defaultLogDirectory, workflowFilename, caseId, transition, userId);
    }

}
