package org.pneditor.petrinet.adapters.SOUKRAT_ELBAGHAZAOUI;

import org.pneditor.petrinet.AbstractPlace;
import org.pneditor.petrinet.models.SOUKRAT_ELBAGHAZAOUI.source.Place;

public class PlaceAdapter extends AbstractPlace {

	/**
	 * According to the adapter design pattern, the PlaceAdapter class adapts the
	 * Place class from our model to the AbstractPlace of th PNEditor PlaceAdapter
	 * 
	 * @author Zakaria SOUKRAT
	 * @author Azhar ELBAGHAZAOUI
	 *
	 */
	private Place place;

	/**
	 * @return the place
	 */
	public Place getPlace() {
		return place;
	}

	/**
	 * set the place
	 * 
	 * @param place
	 */
	public void setPlace(Place place) {
		this.place = place;
	}

	/**
	 * The constructor takes the label of the place from the super constructor of
	 * AbstractPlace and creates a new place with 0 tokens
	 * 
	 * @param label the label of our place
	 */
	public PlaceAdapter(String label) {
		super(label);
		place = new Place(0);
	}

	/**
	 * Adds 1 token to the place
	 */
	@Override
	public void addToken() {
		place.addTokens(1);

	}

	/**
	 * Removes 1 token from the place
	 */
	@Override
	public void removeToken() {
		place.removeTokens(1);

	}

	/**
	 * Returns the number of tokens of the place
	 */
	@Override
	public int getTokens() {
		return place.getNbrTokens();
	}

	/**
	 * Sets the number of tokens of the place
	 * 
	 * @param tokens the new number of tokens
	 */
	@Override
	public void setTokens(int tokens) {
		place.setNbrTokens(tokens);

	}

}
