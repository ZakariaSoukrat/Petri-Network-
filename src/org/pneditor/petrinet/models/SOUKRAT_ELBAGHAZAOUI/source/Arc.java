package org.pneditor.petrinet.models.SOUKRAT_ELBAGHAZAOUI.source;


public class Arc {
	private int weight;

	public Arc(int weight) {
		if (weight <= 0) {
			this.weight = 1;
		} else {
			this.weight = weight;
		}
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		if (weight <= 0) {
			this.weight = 1;
		} else {
			this.weight = weight;
		}

	}

}