package com.gruppe14.explodingkitten.client.gui.controllers;

import com.gruppe14.explodingkitten.client.Client;
import com.gruppe14.explodingkitten.client.MainGUI;
import com.gruppe14.explodingkitten.common.Exceptions.UsernameDoesntExistException;
import com.gruppe14.explodingkitten.common.Exceptions.WrongPasswordException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
	@FXML
	public Button loginButton, registerButton;
	@FXML
	public TextField username;
	@FXML
	public PasswordField password;
	Client client;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		client = MainGUI.client;
	}

	@FXML
	public void loginButtonClicked(ActionEvent event) throws IOException {
		try {
			client.anmelden(username.getText(), password.getText());
			MainGUI.guiController.gotoHauptmenu();
		} catch (RemoteException | MalformedURLException | NotBoundException e) {
			MainGUI.guiController.showAlert("Server is not working ");
		} catch (WrongPasswordException e) {
			MainGUI.guiController.showAlert("Password is wrong");
		} catch (UsernameDoesntExistException e) {
			MainGUI.guiController.showAlert("Username doesnt exist");

		}


	}

	@FXML
	public void registerButtonClicked(ActionEvent event) throws IOException {
		MainGUI.guiController.gotoRegistry();
	}

}
