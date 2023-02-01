package org.pneditor.petrinet.models.SOUKRAT_ELBAGHAZAOUI.source;


public class ArcOutTransition extends Arc {
    private Transition source;
    private Place destination;
    
	public ArcOutTransition(int weight, Transition source, Place destination) {
		super(weight);
		this.source = source;
		this.destination = destination;
	}

	public Transition getSource() {
		return this.source;
	}
	
	public Place getDestination() {
		return this.destination;
	}
}

