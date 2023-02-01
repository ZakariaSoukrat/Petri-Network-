package org.pneditor.petrinet.models.SOUKRAT_ELBAGHAZAOUI.source;



import java.util.LinkedList;

public class Place {
	private int nbrTokens;
	private LinkedList<ArcOutTransition> arcEnteringP;
	private LinkedList<ArcInTransition> arcExitingP;

	// the constructor
	public Place(int nbrTokens) {
		if (nbrTokens < 0) {
			System.out.println("!! Tokens Number can not be inferior to zero, the place will be created with "
					+ -nbrTokens + " Token");
		}
		this.nbrTokens = Math.abs(nbrTokens);
		this.arcEnteringP = new LinkedList<ArcOutTransition>();
		this.arcExitingP = new LinkedList<ArcInTransition>();
	}

	public LinkedList<ArcOutTransition> getArcEnteringP() {
		return arcEnteringP;
	}

	public LinkedList<ArcInTransition> getArcExitingP() {
		return arcExitingP;
	}

	public void addExitingArc(ArcInTransition a) {
		this.arcExitingP.add(a);
	}

	public void addEnteringArc(ArcOutTransition a) {
		this.arcEnteringP.add(a);
	}

	public void removeEnteringArc(ArcOutTransition a) {
		this.arcEnteringP.remove(a);
	}

	public void removeExitingArc(ArcInTransition a) {
		this.arcExitingP.remove(a);
	}

	public int getNbrTokens() {
		return nbrTokens;
	}

	// this function change the number of tokens of a place
	// if the argument is inferior to 0, the absolute value will be considered
	public void setNbrTokens(int nbrTokens) {
		if (nbrTokens < 0) {
			System.out
					.println("!! Tokens Number can not be inferior to zero, NumberTokens will be set to " + -nbrTokens);
		}
		this.nbrTokens = Math.abs(nbrTokens);
	}

	// this function add a number of tokens to a place
	// if the argument is inferior to 0, the absolute value will be considered
	public void addTokens(int nbrTokens) {
		if (nbrTokens < 0) {
			System.out.println("!! We can only add a positive number of Tokens,  " + -nbrTokens + " will be added");
		}
		this.nbrTokens = this.nbrTokens + Math.abs(nbrTokens);
	}

	// this function remove a number of tokens from the place
	// if the place nbrTokens is inferior to the argument, nbrTokens will be set to
	// 0
	public void removeTokens(int nbrTokens) {
		if (this.nbrTokens < nbrTokens) {
			System.out.println(
					"!! Tokens number to remove must be inferior to the current tokens number !!! Tokens number will be set to zero !");
			this.nbrTokens = 0;
		} else {
			if (nbrTokens < 0) {
				System.out
						.println("Tokens number to remove must be greater than 0, the absolute value will be removed");
			}
			this.nbrTokens = this.nbrTokens - Math.abs(nbrTokens);
		}
	}

	// toString() as described in the test plan
	public String toString() {
		String res = "Place avec " + this.nbrTokens + " Jetons , " + this.arcEnteringP.size() + " Arc Entrant " + ","
				+ this.arcExitingP.size() + " Arc Sortant" + "\n";
		return res;
	}

}