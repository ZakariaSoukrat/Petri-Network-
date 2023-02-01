package org.pneditor.petrinet.models.SOUKRAT_ELBAGHAZAOUI.source;

import java.util.LinkedList;

import exceptions.DoubleArcException;
import exceptions.InexistantObjectException;

public interface IPetriNet {

	public void addPlace(Place P);

	public void removePlace(Place P) throws InexistantObjectException;

	public void addTransition(Transition T);

	public void removeTransition(Transition T) throws InexistantObjectException;

	public int getNbrTokens(Place P);

	public void setNbrTokens(Place P, int i);

	public int getWeight(Arc A);

	public void setWeight(Arc A, int i);

	public void fire(Transition T);

	public LinkedList<Place> getPlace();

	public LinkedList<Arc> getArc();

	public LinkedList<Transition> getTransition();

	public void addArcOutTransition(ArcOutTransition a) throws DoubleArcException;

	public void addArcInTransition(ArcInTransition a) throws DoubleArcException;

}