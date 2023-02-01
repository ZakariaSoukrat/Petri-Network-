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
package org.pneditor.editor;

import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;

import org.pneditor.editor.actions.edit.RedoAction;
import org.pneditor.editor.actions.edit.UndoAction;
import org.pneditor.util.Command;

/**
 * UndoManager provides the basic undo-redo capability.
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class UndoManager {

    private List<Command> executedCommands = new ArrayList<>();
    private int currentCommandIndex = -1;
    private final Root root;
    private final UndoAction undoAction;
    private final RedoAction redoAction;

    /**
     * Constructs a new UndoManager
     *
     * @param root Root object
     * @param undoAction action for undo button
     * @param redoAction action for redo button
     */
    public UndoManager(final Root root, final UndoAction undoAction, final RedoAction redoAction) {
        this.root = root;
        this.undoAction = undoAction;
        this.redoAction = redoAction;
    }

    /**
     * Execute command in UndoManager.
     *
     * @param command command to be executed
     */
    public void executeCommand(final Command command) {
    	final List<Command> nonRedoedCommands = new ArrayList<>(this.executedCommands.subList(this.currentCommandIndex + 1, this.executedCommands.size()));
        this.executedCommands.removeAll(nonRedoedCommands);
        this.executedCommands.add(command);
        this.currentCommandIndex = this.executedCommands.size() - 1;
        command.execute();
        refresh();
        this.root.setModified(true);
    }

    /**
     * Performs the undo action.
     */
    public void undoCommand() {
        if (isUndoable()) {
        	final Command command = this.executedCommands.get(this.currentCommandIndex);
            command.undo();
            this.currentCommandIndex--;
            refresh();
        }
        this.root.setModified(true);
    }

    /**
     * Performs the redo action.
     */
    public void redoNextCommand() {
        if (isRedoable()) {
        	final Command command = this.executedCommands.get(this.currentCommandIndex + 1);
            command.redo();
            this.currentCommandIndex++;
            refresh();
        }
        this.root.setModified(true);
    }

    /**
     * Determines if undo is possible.
     *
     * @return true if undo action is possible otherwise false
     */
    public boolean isUndoable() {
        return this.currentCommandIndex != -1;
    }

    /**
     * Determines if redo is possible.
     *
     * @return true if redo action is possible otherwise false
     */
    public boolean isRedoable() {
        return this.currentCommandIndex < this.executedCommands.size() - 1;
    }

    /**
     * Erases all commands from the undo manager.
     */
    public void eraseAll() {
        this.executedCommands = new ArrayList<>();
        this.currentCommandIndex = -1;
        refresh();
    }

    private void refresh() {
        this.root.refreshAll();
        if (isUndoable()) {
            this.undoAction.putValue(AbstractAction.SHORT_DESCRIPTION, "Undo: " + this.executedCommands.get(this.currentCommandIndex).toString());
        } else {
            this.undoAction.putValue(AbstractAction.SHORT_DESCRIPTION, "Undo");
        }
        if (isRedoable()) {
            this.redoAction.putValue(AbstractAction.SHORT_DESCRIPTION, "Redo: " + this.executedCommands.get(this.currentCommandIndex + 1).toString());
        } else {
            this.redoAction.putValue(AbstractAction.SHORT_DESCRIPTION, "Redo");
        }
    }

}
