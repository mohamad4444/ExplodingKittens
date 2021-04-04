package com.gruppe14.explodingkitten.client.gui.controllers.spielRaum.CardElements;

import com.gruppe14.explodingkitten.client.gui.controllers.spielRaum.SpielRaumController;
import com.gruppe14.explodingkitten.common.data.Game.Card;
import com.gruppe14.explodingkitten.common.data.Game.Spieler;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.util.HashMap;

public class CardElementManager {
	public static SpielRaumController controller;
	HashMap<StackPane, ClientHandElement> clientHandElements = new HashMap<>();
	HashMap<String, HashMap<StackPane, PlayerHandElement>> playersHandElements = new HashMap<>();
	HashMap<StackPane, DrawCardElement> drawCardElements = new HashMap<>();

	public CardElementManager(SpielRaumController spielRaumController) {
		controller = spielRaumController;
		playersHandElements = new HashMap<>();

		for (Spieler s : controller.spielraum.spielerList) {
			if (!s.getName().equals(controller.clientSpieler.getName())) {
				playersHandElements.put(s.getName(), new HashMap<>());
				PlayerHandElement.currentWidth.put(s.getName(), 0.0);
			}
		}
		DrawCardElement.fakeImage = controller.images.get(12);
		PlayerHandElement.fakeImage = controller.images.get(12);
	}

	public void clear() {
		ClientHandElement.currentWidth = 0;
		for (Spieler s : controller.spielraum.spielerList) {
			if (!s.getName().equals(controller.clientSpielerAtStart.getName())) {
				playersHandElements.put(s.getName(), new HashMap<>());
				PlayerHandElement.currentWidth.put(s.getName(), 0.0);
			}
		}
		clientHandElements = new HashMap<>();
		drawCardElements = new HashMap<>();
	}

	public void addCardToDeck(Card card) {
		DrawCardElement drawCardElement = new DrawCardElement(card);
		controller.cardsDeck.getChildren().add(drawCardElement.stackpane);
		drawCardElements.put(drawCardElement.stackpane, drawCardElement);
	}

	public void addCardToPlayer(Card c, String spielerName) {
		PlayerHandElement playerHandElement = new PlayerHandElement(c, spielerName);
		HBox hbox = (HBox) controller.spielerVBoxes.get(spielerName).getChildren().get(1);
		hbox.getChildren().add(playerHandElement.stackpane);
		playersHandElements.get(spielerName).put(playerHandElement.stackpane, playerHandElement);
		fixPlayerHandCardSizes(spielerName);

	}

	public void addCardToDiscard(Card card) {
		DiscardCardElement discardCardElement = new DiscardCardElement(card);
		controller.discardCards.getChildren().add(discardCardElement.stackpane);
	}

	public void addCardToClient(Card card) {
		ClientHandElement clientHandElement = new ClientHandElement(card, controller.clientSpieler.getName());
		controller.clientPlayerHBox.getChildren().add(clientHandElement.stackpane);
		clientHandElements.put(clientHandElement.stackpane, clientHandElement);
		fixCLientHandCardSizes();
	}

	public void fixCLientHandCardSizes() {
		for (ClientHandElement element : clientHandElements.values()) {
			if ((ClientHandElement.boxMaxWidth / ClientHandElement.currentWidth) < 1) {
				element.imageview
						.setFitWidth(element.width * (ClientHandElement.boxMaxWidth / ClientHandElement.currentWidth));
			} else {
				element.imageview.setFitWidth(element.width);
			}
		}
	}

	public void fixPlayerHandCardSizes(String spielerName) {
		for (PlayerHandElement element : playersHandElements.get(spielerName).values()) {
			if ((PlayerHandElement.boxMaxWidth / PlayerHandElement.currentWidth.get(spielerName)) < 1) {
				element.imageview.setFitWidth(
						element.width * (PlayerHandElement.boxMaxWidth / PlayerHandElement.currentWidth.get(spielerName)));
			} else {
				element.imageview.setFitWidth(element.width);
			}
		}
	}
}
