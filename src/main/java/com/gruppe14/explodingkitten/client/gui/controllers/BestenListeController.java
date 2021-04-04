package com.gruppe14.explodingkitten.client.gui.controllers;

import com.gruppe14.explodingkitten.client.MainGUI;
import com.gruppe14.explodingkitten.common.data.Account;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class BestenListeController implements Initializable, Updatable {

	@FXML
	private TableView<Account> tableview;

	@FXML
	private TableColumn<Account, String> colName;

	@FXML
	private TableColumn<Account, Integer> colSpiele;

	@FXML
	private TableColumn<Account, Integer> colGewonnen;

	@FXML
	private TableColumn<Account, Integer> colVerloren;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		colName.setCellValueFactory(new PropertyValueFactory<Account, String>("benutzername"));
		colSpiele.setCellValueFactory(new PropertyValueFactory<Account, Integer>("gesamteSpiele"));
		colGewonnen.setCellValueFactory(new PropertyValueFactory<Account, Integer>("gewonnen"));
		colVerloren.setCellValueFactory(new PropertyValueFactory<Account, Integer>("verloren"));
		update();
	}

	@FXML
	void zuruckButtonClicked(ActionEvent event) throws IOException {
		MainGUI.guiController.gotoHauptmenu();
	}

	@Override
	public void update() {
		this.tableview.setItems(FXCollections.observableArrayList(MainGUI.client.zustand.bestenliste));
	}



}
