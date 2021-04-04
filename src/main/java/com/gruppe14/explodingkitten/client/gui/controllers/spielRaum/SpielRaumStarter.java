package com.gruppe14.explodingkitten.client.gui.controllers.spielRaum;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SpielRaumStarter extends Application {


	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/fxmls/SpielRaum.fxml"));
			Parent root = loader.load();
			primaryStage.setTitle("Exploding Kittens");
			Scene main = new Scene(root);
			primaryStage.setScene(main);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}