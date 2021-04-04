package com.gruppe14.explodingkitten.client.gui.controllers;

import com.gruppe14.explodingkitten.client.MainGUI;
import com.gruppe14.explodingkitten.common.Exceptions.UsernameDoesntExistException;
import com.gruppe14.explodingkitten.common.Exceptions.WrongPasswordException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;

public class HauptmenuController {

	/**
	 * goes to profilebearbeiten
	 * 
	 */
	@FXML
	void profilBearbeitenButtonClicked(ActionEvent event) throws IOException {
		MainGUI.guiController.gotoProfilBearbeiten();
	}

	/**
	 * goes to bestenliste
	 * 
	 */
	@FXML
	void bestenListeButtonClicked(ActionEvent event) throws IOException, NotBoundException {
		MainGUI.guiController.gotoBestenListe();
	}

	/**
	 * goes to spielregeln
	 * 
	 */
	@FXML
	void spielRegelenButtonClicked(ActionEvent event) throws IOException {
		MainGUI.guiController.gotoSpielRegeln();
	}

	/**
	 * goes to lobby
	 * 
	 */
	@FXML
	void LobbyButtonCLicked(ActionEvent event) throws IOException {
		MainGUI.guiController.gotoLobby();
	}

	/**
	 * goes to login
	 * 
	 * @throws NotBoundException aa
	 */
	@FXML
	void abmeldenButtonClicked(ActionEvent event) throws IOException, UsernameDoesntExistException, NotBoundException, WrongPasswordException, AlreadyBoundException {
		MainGUI.client.abmenlden();
		MainGUI.guiController.gotoLogin();

	}

}
