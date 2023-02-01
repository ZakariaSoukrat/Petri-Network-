package org.pneditor.petrinet.adapters.SOUKRAT_ELBAGHAZAOUI;

import org.pneditor.petrinet.AbstractTransition;
import org.pneditor.petrinet.models.SOUKRAT_ELBAGHAZAOUI.source.Transition;

/**
 * According to the adapter design pattern, the TransitionAdapter class adapts
 * the Transition class from our model to the AbstractTransition of th PNEditor
 * 
 * @author Zakaria SOUKRAT
 * @author Azhar ELBAGHAZAOUI
 *
 */
public class TransitionAdapter extends AbstractTransition {

	Transition transition;

	/**
	 * @return the transition
	 */
	public Transition getTransition() {
		return transition;
	}

	/**
	 * sets the transition
	 * 
	 * @param transition
	 */
	public void setTransition(Transition transition) {
		this.transition = transition;
	}

	/**
	 * The constructor takes the label of the transition from the super constructor
	 * of AbstractTransition and creates a new transition
	 * 
	 * @param label the label of the transition
	 */
	public TransitionAdapter(String label) {
		super(label);
		transition = new Transition();
	}

}
