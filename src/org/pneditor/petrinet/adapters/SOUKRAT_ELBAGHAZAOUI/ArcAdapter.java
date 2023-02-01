package org.pneditor.petrinet.adapters.SOUKRAT_ELBAGHAZAOUI;

import org.pneditor.petrinet.AbstractArc;
import org.pneditor.petrinet.AbstractNode;
import org.pneditor.petrinet.AbstractPlace;
import org.pneditor.petrinet.AbstractTransition;
import org.pneditor.petrinet.ResetArcMultiplicityException;
import org.pneditor.petrinet.models.SOUKRAT_ELBAGHAZAOUI.source.Arc;
import org.pneditor.petrinet.models.SOUKRAT_ELBAGHAZAOUI.source.ArcDrainer;
import org.pneditor.petrinet.models.SOUKRAT_ELBAGHAZAOUI.source.ArcInTransition;
import org.pneditor.petrinet.models.SOUKRAT_ELBAGHAZAOUI.source.ArcOutTransition;
import org.pneditor.petrinet.models.SOUKRAT_ELBAGHAZAOUI.source.ArcZero;
import org.pneditor.petrinet.models.SOUKRAT_ELBAGHAZAOUI.source.Place;
import org.pneditor.petrinet.models.SOUKRAT_ELBAGHAZAOUI.source.Transition;

/**
 * According to the adapter design pattern, the ArcAdapter class adapts the Arc
 * class from our model to the AbstractArc of th PNEditor ArcAdapter
 * 
 * @author Zakaria SOUKRAT
 * @author Azhar ELBAGHAZAOUI
 *
 */
public class ArcAdapter extends AbstractArc {

	private AbstractPlace p;
	private AbstractTransition t;
	private Arc arc;

	public Arc getArc() {
		return arc;
	}

	public void setA(Arc arc) {
		this.arc = arc;
	}

	/**
	 * this constructor takes an Arc instance to be adapted and AbstractPlace and
	 * AbstractTransition instances which we will use for the return
	 * 
	 * @param p AbstractPlace instance
	 * @param t AbstractTransition instace
	 * @param a Arc instance
	 */
	public ArcAdapter(AbstractPlace p, AbstractTransition t, Arc a) {
		this.p = p;
		this.t = t;
		this.arc = a;
	}

	/**
	 * Return the source of our arc which is a place or a transition
	 */
	@Override
	public AbstractNode getSource() {
		if (this.arc instanceof ArcInTransition) {
			return this.p;
		}
		if (this.arc instanceof ArcOutTransition) {
			return this.t;
		}
		return null;
	}

	/**
	 * Return the destination of our arc which is a place or a transition
	 **/
	@Override
	public AbstractNode getDestination() {
		if (this.arc instanceof ArcInTransition) {
			return this.t;
		}
		if (this.arc instanceof ArcOutTransition) {
			return this.p;
		}
		return null;
	}

	/**
	 * Return true if the arc is ArcDrainer or false otherwise
	 */
	@Override
	public boolean isReset() {
		if (this.arc instanceof ArcDrainer)
			return true;
		return false;
	}

	/**
	 * Return true if the arc is neither ArcDrainer or ZeroArc
	 */
	@Override
	public boolean isRegular() {
		if (this.arc instanceof ArcDrainer || this.arc instanceof ArcZero)
			return false;
		return true;
	}

	/**
	 * Return true if the arc is ArcZero or false otherwise
	 */
	@Override
	public boolean isInhibitory() {
		if (this.arc instanceof ArcZero)
			return true;
		return false;
	}

	/**
	 * Return the weight of the arc
	 */
	@Override
	public int getMultiplicity() throws ResetArcMultiplicityException {

		return this.arc.getWeight();
	}

	/**
	 * Changes the weight of the arc
	 * 
	 * @param multiplicity the new value of the arc multiplicity
	 */
	@Override
	public void setMultiplicity(int multiplicity) throws ResetArcMultiplicityException {
		this.arc.setWeight(multiplicity);

	}

}
