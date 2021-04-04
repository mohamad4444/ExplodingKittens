package com.gruppe14.explodingkitten.client.gui.controllers.spielRaum.CardElements;

import com.gruppe14.explodingkitten.common.data.Game.Card;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;

public class DrawCardElement extends CardElement {

	Image realImage;
	public static Image fakeImage;
	EventHandler<MouseEvent> event;

	DrawCardElement(Card card) {
		super(card, 215, 311.2);
		//TODO
		imageview.setImage(fakeImage);
		imageview.setFitWidth(width);
		imageview.setFitHeight(height);
		makeAnImageEvent();
		addHandler();
	}

	public void addHandler() {
		imageview.addEventFilter(MouseEvent.MOUSE_CLICKED, event);

	}

	public void removeHandlers() {
		imageview.removeEventFilter(MouseEvent.MOUSE_CLICKED, event);
	}

	void makeAnImageEvent() {
		EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if (e.getButton() == MouseButton.PRIMARY) {
					try {
						CardElementManager.controller.karteZiehen(DrawCardElement.this.card);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (AlreadyBoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (NotBoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else if (e.getButton() == MouseButton.SECONDARY) {
					// Stage s=new Stage() ;
				}
			}
		};

		this.event = eventHandler;

	}
}
