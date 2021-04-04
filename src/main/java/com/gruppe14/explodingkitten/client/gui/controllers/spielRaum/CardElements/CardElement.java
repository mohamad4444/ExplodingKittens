package com.gruppe14.explodingkitten.client.gui.controllers.spielRaum.CardElements;

import com.gruppe14.explodingkitten.common.data.Game.Card;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public abstract class CardElement {
	public double width;
	public double height;

	Card card;
	StackPane stackpane;
	ImageView imageview;
	public Image realImage;
	public static CardElementManager cardElementManager;

	public CardElement(Card card, double width, double height) {
		this.card = card;
		this.realImage = CardElementManager.controller.images.get(card.label);
		this.width = width;
		this.height = height;
		this.imageview = new ImageView(realImage);
		this.stackpane = new StackPane(this.imageview);
	}
}
