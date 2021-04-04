package com.gruppe14.explodingkitten.client.gui;

import com.gruppe14.explodingkitten.client.gui.controllers.Updatable;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.NotBoundException;

public class GUIController {
	private final Alert alert = new Alert(AlertType.INFORMATION);
	private final Stage primaryStage;
	public FXMLLoader loader;

	public GUIController(Stage primaryStage2) {
		this.primaryStage = primaryStage2;

	}

	/**updates Current opened gui
	 * 
	 */
	public void updateCurrentGui() {
		Object controller = loader.getController();
		if (controller instanceof Updatable) {
			((Updatable) controller).update();
		}
	}

	public void setStage(Parent root) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				primaryStage.setScene(new Scene(root));
			}
		});
	}

	public Parent load(String fxml) throws IOException {
		this.loader = new FXMLLoader();
		this.loader.setLocation(getClass().getResource(fxml));
		return this.loader.load();

	}

	public void gotoLogin() throws IOException {
		setStage(load("/fxmls/Login.fxml"));
	}

	public void gotoHauptmenu() throws IOException {
		setStage(load("/fxmls/Hauptmenu.fxml"));
	}

	public void gotoSpielRegeln() throws IOException {
		setStage(load("/fxmls/spielregeln/Spielregeln.fxml"));
	}

	public void gotoSpielRaum() throws IOException {
		setStage(load("/fxmls/SpielRaum.fxml"));

	}

	public void gotoRegistry() throws IOException {
		setStage(load("/fxmls/registry.fxml"));
	}

	public void gotoProfilBearbeiten() throws IOException {
		setStage(load("/fxmls/ProfilBearbeiten.fxml"));
	}

	public void gotoSpielVorraum() throws IOException {
		setStage(load("/fxmls/spielVorraum.fxml"));

	}

	public void gotoBestenListe() throws IOException, NotBoundException {
		setStage(load("/fxmls/BestenListe.fxml"));

	}

	public void gotoLobby() throws IOException {
		setStage(load("/fxmls/Lobby.fxml"));

	}

	public void showAlert(String message) throws IOException {
		// alert.setTitle("Error");
		alert.setHeaderText(message);
		alert.setContentText(message);
		alert.showAndWait();
	}

}