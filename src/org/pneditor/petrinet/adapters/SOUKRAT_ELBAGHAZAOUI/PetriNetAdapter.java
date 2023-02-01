package org.pneditor.petrinet.adapters.SOUKRAT_ELBAGHAZAOUI;

import org.pneditor.petrinet.AbstractArc;

import org.pneditor.petrinet.AbstractNode;
import org.pneditor.petrinet.AbstractPlace;
import org.pneditor.petrinet.AbstractTransition;
import org.pneditor.petrinet.PetriNetInterface;
import org.pneditor.petrinet.ResetArcMultiplicityException;
import org.pneditor.petrinet.UnimplementedCaseException;
import org.pneditor.petrinet.models.SOUKRAT_ELBAGHAZAOUI.source.Arc;
import org.pneditor.petrinet.models.SOUKRAT_ELBAGHAZAOUI.source.ArcDrainer;
import org.pneditor.petrinet.models.SOUKRAT_ELBAGHAZAOUI.source.ArcInTransition;
import org.pneditor.petrinet.models.SOUKRAT_ELBAGHAZAOUI.source.ArcOutTransition;
import org.pneditor.petrinet.models.SOUKRAT_ELBAGHAZAOUI.source.ArcZero;
import org.pneditor.petrinet.models.SOUKRAT_ELBAGHAZAOUI.source.PetriNet;
import org.pneditor.petrinet.models.SOUKRAT_ELBAGHAZAOUI.source.Place;
import org.pneditor.petrinet.models.SOUKRAT_ELBAGHAZAOUI.source.Transition;
import exceptions.DoubleArcException;
import exceptions.InexistantObjectException;

/**
 * According to the adapter design pattern, the PetriNetAdapter class adapts the
 * PetriNet class from our model to the PetriNetInterface of the PNEditor
 * 
 * @author Zakaria SOUKRAT
 * @author Azhar ELBAGHAZAOUI
 *
 */

public class PetriNetAdapter extends PetriNetInterface {

	private PetriNet myPetri;
	private int idPlace = 0;
	private int idTransition = 0;

	public PetriNetAdapter() {

		this.myPetri = new PetriNet();
	}

	/**
	 * Returns the adapted place and add it to our PetriNet The place is labled as
	 * ("Place : id of the place" )
	 */

	@Override
	public AbstractPlace addPlace() {
		PlaceAdapter place = new PlaceAdapter("Place : " + idPlace);
		Place P = place.getPlace();
		myPetri.addPlace(P);
		idPlace += 1;
		return place;
	}

	/**
	 * Returns the adapted Transition and add it to our PetriNet The transition is
	 * labled as ("Transition : id of the transtion" )
	 */
	@Override
	public AbstractTransition addTransition() {
		TransitionAdapter transition = new TransitionAdapter("Transition : " + idTransition);
		Transition T = transition.getTransition();
		myPetri.addTransition(T);
		idTransition += 1;
		return transition;
	}

	/**
	 * Returns the adapted Arc and add it to our PetriNet
	 * 
	 * @param source      the source of the arc
	 * @param destination the destination of the arc
	 */
	@Override
	public AbstractArc addRegularArc(AbstractNode source, AbstractNode destination) throws UnimplementedCaseException {

		PlaceAdapter p;
		TransitionAdapter t;
		if (source instanceof AbstractPlace && destination instanceof AbstractTransition) {
			p = (PlaceAdapter) source;
			t = (TransitionAdapter) destination;
			Arc a = new ArcInTransition(1, p.getPlace(), t.getTransition());
			try {
				myPetri.addArcInTransition((ArcInTransition) a);
			} catch (DoubleArcException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ArcAdapter arc = new ArcAdapter(p, t, a);
			return arc;
		}

		else if (source instanceof AbstractTransition && destination instanceof AbstractPlace) {
			p = (PlaceAdapter) destination;
			t = (TransitionAdapter) source;
			Arc a = new ArcOutTransition(1, t.getTransition(), p.getPlace());
			try {
				myPetri.addArcOutTransition((ArcOutTransition) a);
			} catch (DoubleArcException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			ArcAdapter arc = new ArcAdapter(p, t, a);
			return arc;
		}
		return null;
	}

	/**
	 * this method adds a ZeroArc to our PetriNet
	 * 
	 * @param place      the source of the arc
	 * @param transition the destination of the arc
	 */
	@Override
	public AbstractArc addInhibitoryArc(AbstractPlace place, AbstractTransition transition)
			throws UnimplementedCaseException {
		PlaceAdapter p = (PlaceAdapter) place;
		TransitionAdapter t = (TransitionAdapter) transition;
		ArcZero a = new ArcZero(p.getPlace(), t.getTransition());
		myPetri.addArcZero(a);
		ArcAdapter arc = new ArcAdapter(p, t, a);
		return arc;
	}

	/**
	 * This method adds a DrainerArc to our PetriNet
	 * 
	 * @param place      source of the arc
	 * @param transition of the arc
	 */
	@Override
	public AbstractArc addResetArc(AbstractPlace place, AbstractTransition transition)
			throws UnimplementedCaseException {
		PlaceAdapter p = (PlaceAdapter) place;
		TransitionAdapter t = (TransitionAdapter) transition;
		ArcDrainer a = new ArcDrainer(p.getPlace(), t.getTransition());
		myPetri.addArcDrainer(a);
		ArcAdapter arc = new ArcAdapter(p, t, a);
		return arc;
	}

	/**
	 * Removes a place from the petri network
	 * 
	 * @param place the place to be removed
	 */
	@Override
	public void removePlace(AbstractPlace place) {
		Place p = ((PlaceAdapter) place).getPlace();
		try {
			myPetri.removePlace(p);
		} catch (InexistantObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Removes a transition from our petri network
	 * 
	 * @param transtion the transition to be removed
	 */
	@Override
	public void removeTransition(AbstractTransition transition) {
		Transition t = ((TransitionAdapter) transition).getTransition();
		try {
			myPetri.removeTransition(t);
		} catch (InexistantObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Removes arc from our petri network
	 * 
	 * @param the arc to be removed
	 *
	 */
	@Override
	public void removeArc(AbstractArc arc) {
		Arc a = ((ArcAdapter) arc).getArc();

		if (a instanceof ArcInTransition) {
			try {
				myPetri.removeArcInTransition((ArcInTransition) a);
			} catch (InexistantObjectException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (a instanceof ArcOutTransition) {
			try {
				myPetri.removeArcOutTransition((ArcOutTransition) a);
			} catch (InexistantObjectException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Returns true if the transition is firebale and false otherwise
	 * 
	 * @param transition
	 */
	@Override
	public boolean isEnabled(AbstractTransition transition) throws ResetArcMultiplicityException {
		Transition t = ((TransitionAdapter) transition).getTransition();
		return t.isFireable();
	}

	/**
	 * Fires the transition
	 * 
	 * @param transition
	 */
	@Override
	public void fire(AbstractTransition transition) throws ResetArcMultiplicityException {
		Transition t = ((TransitionAdapter) transition).getTransition();
		t.fire();

	}

}
