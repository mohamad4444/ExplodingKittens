package com.gruppe14.explodingkitten.client.gui.controllers;

import com.gruppe14.explodingkitten.client.MainGUI;
import com.gruppe14.explodingkitten.common.Exceptions.FullRoomException;
import com.gruppe14.explodingkitten.common.Exceptions.WrongPasswordException;
import com.gruppe14.explodingkitten.common.data.Account;
import com.gruppe14.explodingkitten.common.data.Vorraum;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class SpielVorraumController extends UnicastRemoteObject implements Initializable, Updatable {

	/**
	 *
	 */
	private static final long serialVersionUID = 7887924739713481474L;

	public SpielVorraumController() throws RemoteException {
		super();
	}

	@FXML
	private TableView<Account> SpielVorraumTable;

	@FXML
	private TableColumn<Account, String> colBenutzerName;

	@FXML
	private TableColumn<Account, Integer> colGesamteSpiele;

	@FXML
	private TableColumn<Account, Integer> colGewonnen;

	@FXML
	private TableColumn<Account, Integer> colVerloren;

	@FXML
	private TextArea chatTextArea;

	@FXML
	private TextField chatTextField;

	@FXML
	private Text roomName;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		colBenutzerName.setCellValueFactory(new PropertyValueFactory<Account, String>("benutzername"));
		colGesamteSpiele.setCellValueFactory(new PropertyValueFactory<Account, Integer>("gewonnen"));
		colGewonnen.setCellValueFactory(new PropertyValueFactory<Account, Integer>("verloren"));
		colVerloren.setCellValueFactory(new PropertyValueFactory<Account, Integer>("gesamteSpiele"));
		update();
	}

	@FXML
	void botHinzufugenEButtonClicked(ActionEvent event) throws FullRoomException, WrongPasswordException, IOException, AlreadyBoundException, NotBoundException {
		MainGUI.client.botHinzufugenE();
	}

	@FXML
	void botHinzufugenSButtonClicked(ActionEvent event) throws FullRoomException, WrongPasswordException, IOException {
		MainGUI.client.botHinzufugenS();
	}

	@FXML
	void sendButtonClicked(ActionEvent event) throws IOException {
		MainGUI.client.sendChatVor(chatTextField.getText());
		chatTextField.setText("");

	}

	@FXML
	void enterPressedChat(ActionEvent event) throws IOException {
		MainGUI.client.sendChatVor(chatTextField.getText());
		chatTextField.setText("");
	}

	@FXML
	void spielStartenButtonClicked(ActionEvent event) throws IOException, AlreadyBoundException, NotBoundException {
		MainGUI.client.spielStarten();
	}

	@FXML
	void zuruckButtonClicked(ActionEvent event) throws IOException, NotBoundException, AlreadyBoundException {
		MainGUI.client.raumVerlassen();
		MainGUI.guiController.gotoLobby();
	}

	@Override
	public void update() {
		Vorraum vorraum = MainGUI.client.zustand.vorraum;
		SpielVorraumTable.setItems(FXCollections.observableList(new LinkedList<>(vorraum.spielerImVorraum.values())));
		chatTextArea.setText("");
		for (String c : vorraum.chats) {
			chatTextArea.appendText(c);
		}

	}
}
