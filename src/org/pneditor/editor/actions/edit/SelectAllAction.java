package org.pneditor.editor.actions.edit;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import org.pneditor.editor.Root;
import org.pneditor.editor.canvas.Selection;
import org.pneditor.editor.gpetrinet.GraphicPetriNet;

/**
 *
 * @author matmas
 */
@SuppressWarnings("serial")
public class SelectAllAction extends AbstractAction {

	private final Root root;
	
    public SelectAllAction(final Root root) {
    	super();
    	this.root = root;
        String name = "Select All";
        putValue(NAME, name);
        putValue(SHORT_DESCRIPTION, name);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl A"));
        setEnabled(false);
    }
    
    @Override
    public void actionPerformed(final ActionEvent e) {
    	final GraphicPetriNet petriNet = this.root.getGraphicPetriNet();

    	final Selection selection = this.root.getSelection();
        selection.clear();
        selection.addAll(petriNet.getElements());

        this.root.refreshAll();
    }

}
