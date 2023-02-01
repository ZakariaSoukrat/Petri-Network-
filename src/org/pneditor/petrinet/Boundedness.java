/*
 * Copyright (C) 2012 milka
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
package org.pneditor.petrinet;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * @author milka
 */
public class Boundedness {

	final private ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
	final private PetriNetInterface petriNet;
	private Stack<Map<AbstractPlace, Integer>> markingsStack;
	private boolean isUnboundedness;

	public Boundedness(final PetriNetInterface petriNet) {
		this.petriNet = petriNet;
	}

	public boolean isBounded() throws ResetArcMultiplicityException {

		this.isUnboundedness = false;

		final Map<AbstractPlace, Integer> initMap = initFill();
		this.markingsStack = new Stack<>();
		this.markingsStack.push(initMap);

		final Set<AbstractTransition> executableTransitions = getAllEnabledTransitions(initMap);
		for (final AbstractTransition t : executableTransitions) {
			if (checkBranchBoundedness(initMap, t)) {
				this.markingsStack.pop();
			}
		}
		return this.isUnboundedness;
	}

	private boolean checkBranchBoundedness(final Map<AbstractPlace, Integer> marking,
			final AbstractTransition transition) throws ResetArcMultiplicityException {

		if (this.isUnboundedness) {
			return false;
		}

		final Map<AbstractPlace, Integer> newMarking = new ConcurrentHashMap<>(marking);
		fire(newMarking, transition);

		for (final Map<AbstractPlace, Integer> oldMarking : this.markingsStack) {
			if (isOmega(newMarking, oldMarking)) {
				this.isUnboundedness = true;
				return false;
			}
		}

		if (!this.markingsStack.contains(newMarking)) {
			this.markingsStack.push(newMarking);
			final Set<AbstractTransition> executableTransitions = getAllEnabledTransitions(newMarking);
			for (final AbstractTransition t : executableTransitions) {
				if (checkBranchBoundedness(newMarking, t)) {
					this.markingsStack.pop();
				}
			}
			return true;
		}

		return false;

	}

	private boolean isOmega(final Map<AbstractPlace, Integer> newMarking,
			final Map<AbstractPlace, Integer> oldMarking) {

		boolean isOneSharplyHigher = false;

		for (final AbstractPlace newMarkingAbstractPlace : newMarking.keySet()) {

			final int newTokens = newMarking.get(newMarkingAbstractPlace);

			AbstractPlace oldMarkingAbstractPlace = null;
			for (final AbstractPlace place : oldMarking.keySet()) {
				if (place.equals(newMarkingAbstractPlace)) {
					oldMarkingAbstractPlace = place;
					break;
				}
			}

			final int oldTokens = oldMarking.get(oldMarkingAbstractPlace);

			if (newTokens < oldTokens) {
				return false;
			} else if (newTokens > oldTokens) {
				isOneSharplyHigher = true;
			}

		}
		return isOneSharplyHigher;
	}

	private Map<AbstractPlace, Integer> initFill() {
		final Map<AbstractPlace, Integer> marking = new ConcurrentHashMap<>();
		for (final AbstractPlace place : this.petriNet.getPlaces()) {
			marking.put(place, place.getTokens());
		}
		return marking;
	}

	private boolean fire(final Map<AbstractPlace, Integer> marking, final AbstractTransition transition)
			throws ResetArcMultiplicityException {
		boolean success;
		this.lock.writeLock().lock();
		try {
			if (isEnabled(marking, transition)) {
				for (final AbstractArc arc : this.petriNet.getConnectedArcs(transition)) {
					if (arc.isSourceAPlace()) {
						if (arc.isReset()) { // reset arc consumes them all
							marking.put((AbstractPlace) arc.getSource(), 0);
						}
						if (arc.isRegular()) {
							marking.put((AbstractPlace) arc.getSource(),
									marking.get(arc.getSource()) - arc.getMultiplicity());
						}

					}
				}
				for (final AbstractArc arc : this.petriNet.getConnectedArcs(transition)) {
					if (!arc.isSourceAPlace()) {
						marking.put((AbstractPlace) arc.getDestination(),
								marking.get(arc.getDestination()) + arc.getMultiplicity());
					}
				}
				success = true;
			} else {
				success = false;
			}
		} finally {
			this.lock.writeLock().unlock();
		}
		return success;
	}

	private boolean isEnabled(final Map<AbstractPlace, Integer> marking, final AbstractTransition transition)
			throws ResetArcMultiplicityException {
		boolean isEnabled = true;
		this.lock.readLock().lock();
		try {
			for (final AbstractArc arc : this.petriNet.getConnectedArcs(transition)) {
				if (arc.isSourceAPlace() && !arc.isReset()) {
					// reset arc is always fireable
					// but can be blocked by other arcs

					if (arc.isRegular()) { 
						if (marking.get(arc.getSource()) < arc.getMultiplicity()) {// normal arc
							isEnabled = false;
							break;
						}
					} else {
						if (marking.get(arc.getSource()) >= arc.getMultiplicity()) {// inhibitory arc
							isEnabled = false;
							break;
						}
					}

				}
			}
		} finally {
			this.lock.readLock().unlock();
		}
		return isEnabled;
	}

	public Set<AbstractTransition> getAllEnabledTransitions(final Map<AbstractPlace, Integer> marking)
			throws ResetArcMultiplicityException {
		final Set<AbstractTransition> enabledTransitions = new HashSet<>();
		this.lock.readLock().lock();
		try {
			for (final AbstractTransition transition : this.petriNet.getTransitions()) {
				if (isEnabled(marking, transition)) {
					enabledTransitions.add(transition);
				}
			}
		} finally {
			this.lock.readLock().unlock();
		}
		return enabledTransitions;
	}
}
