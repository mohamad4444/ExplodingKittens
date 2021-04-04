package com.gruppe14.explodingkitten.client.gui.controllers.spielRaum.CardElements;

import com.gruppe14.explodingkitten.common.Exceptions.DEFUSEMustBePlayedException;
import com.gruppe14.explodingkitten.common.data.Game.Card;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.util.ArrayList;

public class ClientHandElement extends CardElement {
	public static final double boxMaxWidth = 899;
	public static double currentWidth;
	ArrayList<EventHandler<MouseEvent>> handlers = new ArrayList<>(3);
	String spieler;
	ClientHandElement(Card card,String spieler) {
		super(card, 128, 186);
		this.spieler=spieler;
		imageview.setFitWidth(width);
		imageview.setFitHeight(height);
		currentWidth = currentWidth + width;
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
		imageview.removeEventFilter(MouseEvent.MOUSE_CLICKED, handlers.get(2));
	}

	void makeAnImageEvent() {
		EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if (boxMaxWidth / currentWidth < 1) {
					imageview.setFitWidth(width * (boxMaxWidth / currentWidth) * 1.2);
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
				if (boxMaxWidth / currentWidth < 1) {

					imageview.setFitWidth(width * (boxMaxWidth / currentWidth));
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
					try {
						CardElementManager.controller.karteAusspielen(card, spieler);
					} catch (IOException | InterruptedException | DEFUSEMustBePlayedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
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
