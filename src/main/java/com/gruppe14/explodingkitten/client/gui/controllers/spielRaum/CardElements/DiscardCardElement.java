package com.gruppe14.explodingkitten.client.gui.controllers.spielRaum.CardElements;

import com.gruppe14.explodingkitten.common.data.Game.Card;

public class DiscardCardElement extends CardElement {

	public DiscardCardElement(Card  card) {
		super(card, 215, 311.2);
		imageview.setFitWidth(width);
		imageview.setFitHeight(height);
	}

}
