package org.pneditor.petrinet.models.SOUKRAT_ELBAGHAZAOUI.source;



import java.util.LinkedList;

import exceptions.DoubleArcException;
import exceptions.InexistantObjectException;

public class PetriNet implements IPetriNet {
	LinkedList<Place> listPlace = new LinkedList<Place>();
	LinkedList<Arc> listArc = new LinkedList<Arc>();
	LinkedList<Transition> listTransition = new LinkedList<Transition>();
	LinkedList<ArcInTransition> listArcInTransition = new LinkedList<ArcInTransition>();
	LinkedList<ArcOutTransition> listArcOutTransition = new LinkedList<ArcOutTransition>();

	public PetriNet() {
		this.listPlace = listPlace;
		this.listArc = listArc;
		this.listTransition = listTransition;
		this.listArcInTransition = listArcInTransition;
		this.listArcOutTransition = listArcOutTransition;
	}

	// add a place to our PetriNetwork
	public void addPlace(Place P) {
		this.listPlace.add(P);
	}

	// remove a place from our PetriNetwork, that will also remove all the arcs
	// entering and exiting this place,
	// if the Place does not exist an InexistantObjectException will be thrown
	public void removePlace(Place p) throws InexistantObjectException {
		if (this.listPlace.contains(p)) {
			this.listPlace.remove(p);
			for (ArcInTransition a : p.getArcExitingP()) {
				this.listArc.remove(a);
				this.listArcInTransition.remove(a);
			}
			for (ArcOutTransition a : p.getArcEnteringP()) {
				this.listArc.remove(a);
				this.listArcOutTransition.remove(a);
			}
		} else {
			throw new InexistantObjectException("Place does not exist");
		}
	}

	// add a Transition to our Petri Network
	public void addTransition(Transition T) {
		this.listTransition.add(T);

	}

	// remove a Transition from our Petri Network, that will also remove all the
	// arcs entering and exiting this Transition
	// if the Transition does not exist an InexistantObjectException will be thrown
	public void removeTransition(Transition T) throws InexistantObjectException {
		if (this.listTransition.contains(T)) {
			this.listTransition.remove(T);
			for (ArcInTransition a : T.getEnteringArc()) {
				this.listArc.remove(a);
				this.listArcInTransition.remove(a);
			}
			for (ArcOutTransition a : T.getExitingArc()) {
				this.listArc.remove(a);
				this.listArcOutTransition.remove(a);
			}
		} else {
			throw new InexistantObjectException("Transition does not exist");
		}
	}

	// return tokens number of a given place
	public int getNbrTokens(Place P) {
		return P.getNbrTokens();
	}

	// change the number of tokens of a given place p to a given integer i
	public void setNbrTokens(Place P, int i) {
		P.setNbrTokens(i);

	}

	// add i token to the place P
	public void addTokens(Place P, int i) {
		P.addTokens(i);

	}

	// remove i token from the place P
	public void removeTokens(Place P, int i) {
		P.removeTokens(i);

	}

	// return the weight of a given Arc
	public int getWeight(Arc A) {
		return A.getWeight();
	}

	// change the weight of a given Arc A to a given integer i
	public void setWeight(Arc A, int i) {
		A.setWeight(i);
	}

	// this function will fire the transition T if it is fireable
	public void fire(Transition T) {
		T.fire();

	}

	// this function will fire all the fireable transitions
	public void fireAll() {
		for (Transition T : listTransition) {
			if (T.isFireable()) {
				T.fire();
			}
		}
	}

	// return a linkedList which contains all the places
	public LinkedList<Place> getPlace() {
		return this.listPlace;
	}

	// return a linkedList which contains all the arcs
	public LinkedList<Arc> getArc() {
		return this.listArc;
	}

	// return a linkedList which contains all the Transitions
	public LinkedList<Transition> getTransition() {
		return this.listTransition;
	}

	// return a linkedList which contains all the Arcs entering a Transition
	public LinkedList<ArcInTransition> getArcInTransition() {
		return this.listArcInTransition;
	}

	// return a linkedList which contains all the Arcs exiting a Transition
	public LinkedList<ArcOutTransition> getArcOutTransition() {
		return this.listArcOutTransition;
	}

	// verify the existance of an ArcInTransition to not create double arcs
	public boolean exist(ArcInTransition ai) {
		for (ArcInTransition a : listArcInTransition) {
			if (a.getSource() == ai.getSource()) {
				return true;
			}
		}
		return false;
	}

	// verify the existance of an ArcOutTransition to not create double arcs
	public boolean exist(ArcOutTransition ao) {
		for (ArcOutTransition a : listArcOutTransition) {
			if (a.getSource() == ao.getSource()) {
				return true;
			}
		}
		return false;
	}

	// add an ArcInTransition (arc entering to a transition)
	// if an arc already exit between the source and destination an exception will
	// be thrown
	public void addArcInTransition(ArcInTransition a) throws DoubleArcException {
		if (this.exist(a) == false) {
			this.listArcInTransition.add(a);
			this.listArc.add(a);
			a.getDestination().addEnteringArc(a);
			a.getSource().addExitingArc(a);
		} else {
			throw new DoubleArcException("An Arc already exist beetwen " + a.getSource().toString() + " and "
					+ a.getDestination().toString());
		}
	}

	// add an exiting Arc from a given Transition (destination)
	// if an arc already exit between the source and destination an exception will
	// be thrown
	public void addArcOutTransition(ArcOutTransition a) throws DoubleArcException {
		if (this.exist(a) == false) {
			this.listArcOutTransition.add(a);
			a.getSource().addExitingArc(a);
			a.getDestination().addEnteringArc(a);
			this.listArc.add(a);
		} else {
			throw new DoubleArcException("An Arc already exist beetwen " + a.getSource().toString() + " and "
					+ a.getDestination().toString());
		}
	}

	// remove an ArcOutTransition from the PetriNet
	// if the ArcOutTransition does not exist in the PetriNet an InexistantException
	// will be thrown
	public void removeArcOutTransition(ArcOutTransition a) throws InexistantObjectException {
		if (this.listArcOutTransition.contains(a)) {
			this.listArcOutTransition.remove(a);
			this.listArc.remove(a);
			a.getSource().removeExitingArc(a);
			a.getDestination().removeEnteringArc(a);
		} else {
			throw new InexistantObjectException("ArcOutTransition does not exist");
		}
	}

	// remove an ArcInTransition from the PetriNet
	// if the ArcOutTransition does not exist in the PetriNet an InexistantException
	// will be thrown
	public void removeArcInTransition(ArcInTransition a) throws InexistantObjectException {
		if (this.listArcInTransition.contains(a)) {
			this.listArcInTransition.remove(a);
			this.listArc.remove(a);
			a.getSource().removeExitingArc(a);
			a.getDestination().removeEnteringArc(a);
		} else {
			throw new InexistantObjectException("ArcInTransition does not exist");
		}
	}

	// add an ArcZero to the PetriNet
	public void addArcZero(ArcZero a) {
		a.getDestination().addEnteringArc(a);
		this.listArcInTransition.add(a);
		this.listArc.add(a);
	}

	// add an ArcDrainer to the PetriNet
	public void addArcDrainer(ArcDrainer a) {
		a.getDestination().addEnteringArc(a);
		this.listArcInTransition.add(a);
		this.listArc.add(a);
	}

	// toString() as described in the plan of the test
	public String toString() {
		String res = "Petri Network \n " + this.listPlace.size() + " Places \n " + this.listTransition.size()
				+ " Transitions \n " + this.listArc.size() + " Arcs \n" + "Liste Place : \n";
		for (Place p : this.listPlace) {
			res = res + p.toString();
		}
		res = res + "Liste des Transitions : \n";
		for (Transition t : this.listTransition) {
			res = res + t.toString();
		}
		res = res + "Liste des Arcs : \n";
		for (ArcInTransition a : this.listArcInTransition) {
			res = res + "Arc " + a.getWeight() + " (place avec " + a.getSource().getNbrTokens()
					+ " vers Transition ) \n";
		}
		for (ArcOutTransition a : this.listArcOutTransition) {
			res = res + "Arc " + a.getWeight() + " (Transition  vers  place avec " + a.getDestination().getNbrTokens()
					+ ") \n";
		}
		return res;

	}

	/**
	 * this is a main function to test the project
	 * 
	 * public static void main(String[] args) { PetriNet myPetriNet = new
	 * PetriNet(); Place P1 = new Place(2); myPetriNet.addPlace(P1); Place P2 = new
	 * Place(2); myPetriNet.addPlace(P2); Transition T = new Transition();
	 * myPetriNet.addTransition(T); ArcInTransition ai = new
	 * ArcInTransition(1,myPetriNet.listPlace.get(0),myPetriNet.listTransition.get(0));
	 * myPetriNet.addArcInTransition(ai); ArcOutTransition ao = new
	 * ArcOutTransition(1,myPetriNet.listTransition.get(0),myPetriNet.listPlace.get(1));
	 * myPetriNet.addArcOutTransition(ao);
	 * 
	 * myPetriNet.fire(T); System.out.println(myPetriNet.toString()); }
	 * 
	 */

}