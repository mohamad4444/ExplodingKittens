package com.gruppe14.explodingkitten.server.main;

import com.gruppe14.explodingkitten.server.Server;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class MainGUIServer extends Application {
	Server server;

	public static void main(String[] args) {
		launch(args);
	}


	@Override
	public void start(Stage primaryStage) throws Exception {
		Button b = new Button("StartServer");
		Button b2 = new Button("StopServer");
		TextField textField1 = new TextField();
		TextField textField2 = new TextField();
		Text text1 = new Text("Ip Address");
		Text text2 = new Text("Port  Number");
		VBox vBox = new VBox();
		HBox hbox = new HBox();
		HBox hbox2 = new HBox();
		HBox hbox3 = new HBox();
		hbox.getChildren().addAll(b, b2);
		hbox2.getChildren().addAll(text1, textField1);
		hbox3.getChildren().addAll(text2, textField2);
		vBox.getChildren().addAll(hbox2, hbox3, hbox);
		b2.setDisable(true);
		// action event
		EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				try {
					server = new Server();
					b.setDisable(true);
					b2.setDisable(false);
				} catch (RemoteException | MalformedURLException | AlreadyBoundException e1) {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setContentText("ServerConnection Problem");
					alert.showAndWait();
				}
			}
		};
		EventHandler<ActionEvent> event2 = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				try {
					server.unbindAll();
					b.setDisable(false);
					b2.setDisable(true);
				} catch (RemoteException | MalformedURLException | NotBoundException e1) {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setContentText("ServerConnection Problem");
					alert.showAndWait();
				}
			}
		};
		// when button is pressed
		b.setOnAction(event);
		b2.setOnAction(event2);
		Scene scene = new Scene(vBox);
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	@Override
	public void stop() {
//		try {
//			server.unbindAll();
//		} catch (RemoteException | MalformedURLException | NotBoundException e) {
//		}
		System.exit(0);
	}

}
