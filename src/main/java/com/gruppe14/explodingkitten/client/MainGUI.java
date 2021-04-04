package com.gruppe14.explodingkitten.client;

import com.gruppe14.explodingkitten.client.gui.GUIController;
import com.gruppe14.explodingkitten.common.Exceptions.UsernameDoesntExistException;
import com.gruppe14.explodingkitten.common.Exceptions.WrongPasswordException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;

public class MainGUI extends Application {
	public static Client client;
	public static GUIController guiController;

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * start of application
	 * 
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			client = new Client();
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/fxmls/Login.fxml"));
			Parent root = loader.load();
			primaryStage.setTitle("Exploding Kittens");
			Scene main = new Scene(root);
			primaryStage.setScene(main);
			primaryStage.show();
			guiController = new GUIController(primaryStage);
			guiController.loader = loader;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Stop of application
	 */
	@Override
	public void stop() throws IOException, WrongPasswordException, NotBoundException, UsernameDoesntExistException,
			AlreadyBoundException {
		client.abmenlden();
		System.exit(0);
	}

}