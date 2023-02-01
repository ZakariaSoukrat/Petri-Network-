package org.pneditor.petrinet.models.SOUKRAT_ELBAGHAZAOUI.source;



public class ArcDrainer extends ArcInTransition {

	public ArcDrainer(Place source, Transition destination) {
		super(1, source, destination);
		// TODO Auto-generated constructor stub
	}

	public boolean isActive() {

		if (super.getSource().getNbrTokens() == 0) {
			return false;
		} else {
			return true;
		}
	}

	// execute the ArcDrainer (emptying the place)
	public void execute() {

		if (isActive() == true) {
			super.getSource().setNbrTokens(0);
		}
	}

}
