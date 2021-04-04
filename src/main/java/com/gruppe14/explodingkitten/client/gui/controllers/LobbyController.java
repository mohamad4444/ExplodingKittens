package com.gruppe14.explodingkitten.client.gui.controllers;

import com.gruppe14.explodingkitten.client.MainGUI;
import com.gruppe14.explodingkitten.common.Exceptions.FullRoomException;
import com.gruppe14.explodingkitten.common.Exceptions.RoomDoesntExistException;
import com.gruppe14.explodingkitten.common.Exceptions.WrongPasswordException;
import com.gruppe14.explodingkitten.common.data.Vorraum;
import javafx.beans.property.SimpleStringProperty;
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
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class LobbyController implements Initializable, Updatable {

	private static final long serialVersionUID = -1340239680149854010L;

	@FXML
	private TableView<String> onlineUsersTable;

	@FXML
	private TableColumn<String, String> colOnlineUsers;

	@FXML
	private TableView<Vorraum> raumTable;

	@FXML
	private TableColumn<Vorraum, String> colRaumName;

	@FXML
	private TableColumn<Vorraum, Integer> colAktiveSpieler;
	@FXML
	private TableColumn<Vorraum, Integer> colMaxSpieler;

	@FXML
	private TextArea chatTextArea;
	@FXML
	private TextField chatTextField;

	@FXML
	private TextField raumName;

	@FXML
	private TextField raumPassword;

	@FXML
	private TextField spielerAnzahl;
	@FXML
	private Text username;

	/**
	 * adds client username to lobby,sets up table
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// Setting up GUI
		chatTextArea.setEditable(false);
		// username.setText(GUIController.client.account.getBenutzername());
		colRaumName.setCellValueFactory(new PropertyValueFactory<Vorraum, String>("name"));
		colAktiveSpieler.setCellValueFactory(new PropertyValueFactory<Vorraum, Integer>("currentplayers"));
		colMaxSpieler.setCellValueFactory(new PropertyValueFactory<Vorraum, Integer>("maxSpielerAnzahl"));
		colOnlineUsers.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
		try {
			MainGUI.client.joinLobby();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	/**
	 * removes client from lobby
	 * @param event .
	 * @throws IOException .
	 */
	@FXML
	void zuruckClicked(ActionEvent event) throws IOException {
		MainGUI.guiController.gotoHauptmenu();
		MainGUI.client.exitLobby();
	}

	@FXML
	void actualizeRoomsButtonClicked(ActionEvent event) throws IOException {
		// MainGUI.guiController.gotoLobby();
	}

	@FXML
	void sendChat(ActionEvent event) throws IOException {
		MainGUI.client.sendChatLo(chatTextField.getText());
		chatTextField.setText("");
	}

	@FXML
	void sendButtonClicked(ActionEvent event) throws IOException {
		MainGUI.client.sendChatLo(chatTextField.getText());
		chatTextField.setText("");
	}

	@FXML
	void raumErstellenClicked(ActionEvent event) throws Exception {
		MainGUI.client.raumErstellen(raumName.getText(), raumPassword.getText(),
				Integer.parseInt(spielerAnzahl.getText()));
		MainGUI.guiController.gotoSpielVorraum();
	}

	@FXML
	void raumBeitretenClicked(ActionEvent event) throws NumberFormatException, IOException, FullRoomException,
			WrongPasswordException, AlreadyBoundException, RoomDoesntExistException, NotBoundException {
		MainGUI.client.raumBeitreten(raumName.getText(), raumPassword.getText());
		MainGUI.guiController.gotoSpielVorraum();
	}

	@Override
	public void update() {
		LinkedList<String> chats = MainGUI.client.zustand.chatLo;
		LinkedList<Vorraum> vorraumList = MainGUI.client.zustand.vorraumList;
		List<String> users = MainGUI.client.zustand.users;
		chats = MainGUI.client.zustand.chatLo;
		chatTextArea.setText("");
		for (String c : chats) {
			chatTextArea.appendText(c);
		}
		this.raumTable.setItems(FXCollections.observableList(vorraumList));
		this.onlineUsersTable.setItems(FXCollections.observableList(users));
	}

}
