package com.gruppe14.explodingkitten.client.gui.controllers.spielRaum.CardElements;

import com.gruppe14.explodingkitten.common.data.Game.Card;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayerHandElement extends CardElement {
	ArrayList<EventHandler<MouseEvent>> handlers = new ArrayList<>(3);
	static Image fakeImage;
	public static final double boxMaxWidth = 262;
	public static HashMap<String, Double> currentWidth = new HashMap<>();
	String spieler;

	public PlayerHandElement(Card card, String string) {
		super(card, 53.7, 78);
		imageview.setImage(fakeImage);
		imageview.setFitWidth(width);
		imageview.setFitHeight(height);
		this.spieler = string;
		currentWidth.put(this.spieler, currentWidth.get(this.spieler) + width);
		makeAnImageEvent();
		addHandlers();

	}

	public void addHandlers() {
		imageview.addEventFilter(MouseEvent.MOUSE_ENTERED, handlers.get(0));
		imageview.addEventFilter(MouseEvent.MOUSE_EXITED, handlers.get(1));
		imageview.addEventFilter(MouseEvent.MOUSE_CLICKED, handlers.get(2));
	}

	public void removeHandlers() {
		imageview.removeEventFilter(MouseEvent.MOUSE_ENTERED, handlers.get(0));
		imageview.removeEventFilter(MouseEvent.MOUSE_EXITED, handlers.get(1));
	}

	void makeAnImageEvent() {
		EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if (boxMaxWidth / currentWidth.get(spieler) < 1) {
					imageview.setFitWidth(width * (boxMaxWidth / currentWidth.get(spieler)) * 1.2);
					imageview.setFitHeight(height * 1.2);
				} else {
					imageview.setFitWidth(width * 1.2);
					imageview.setFitHeight(height * 1.2);
				}
			}
		};
		EventHandler<MouseEvent> eventHandler2 = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if (boxMaxWidth / currentWidth.get(spieler) < 1) {

					imageview.setFitWidth(width * (boxMaxWidth / currentWidth.get(spieler)));
					imageview.setFitHeight(height);
				} else {

					imageview.setFitWidth(width);
					imageview.setFitHeight(height);
				}

			}
		};
		EventHandler<MouseEvent> eventHandler3 = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if (e.getButton() == MouseButton.PRIMARY) {
					System.out.println(card);

				} else if (e.getButton() == MouseButton.SECONDARY) {
					// Stage s=new Stage() ;
				}
			}
		};
		handlers.add(eventHandler);
		handlers.add(eventHandler2);
		handlers.add(eventHandler3);
	}
}
