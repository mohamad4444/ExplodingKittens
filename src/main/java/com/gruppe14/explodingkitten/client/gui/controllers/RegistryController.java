package com.gruppe14.explodingkitten.client.gui.controllers;

import com.gruppe14.explodingkitten.client.Client;
import com.gruppe14.explodingkitten.client.MainGUI;
import com.gruppe14.explodingkitten.common.Exceptions.UsernameAlreadyExistException;
import com.gruppe14.explodingkitten.common.Exceptions.UsernameDoesntExistException;
import com.gruppe14.explodingkitten.common.Exceptions.WrongPasswordException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.util.ResourceBundle;

public class RegistryController implements Initializable {
	Client client;

	@FXML
	private TextField username;

	@FXML
	private PasswordField password;

	@FXML
	private PasswordField password2;

	@FXML
	private TextField age;

	public FXMLLoader fxmlLoader;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		client = MainGUI.client;
	}

	@FXML
	void bestaetigenButtonClicked(ActionEvent event) throws IOException, WrongPasswordException,
			UsernameDoesntExistException, NotBoundException, AlreadyBoundException {

		try {
			client.register(username.getText(), password.getText(), Integer.parseInt(age.getText()));
			client.anmelden(username.getText(), password.getText());
			MainGUI.guiController.gotoHauptmenu();
		} catch (NumberFormatException e) {
			MainGUI.guiController.showAlert("Enter Number");
		} catch (UsernameAlreadyExistException e) {
			MainGUI.guiController.showAlert("Username aLready exist");

		}

	}

	@FXML
	void ZuruckButtonCLicked(ActionEvent event) throws IOException {
		MainGUI.guiController.gotoLogin();
	}

}
