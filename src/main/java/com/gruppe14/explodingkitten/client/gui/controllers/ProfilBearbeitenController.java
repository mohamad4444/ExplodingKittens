package com.gruppe14.explodingkitten.client.gui.controllers;

import com.gruppe14.explodingkitten.client.Client;
import com.gruppe14.explodingkitten.client.MainGUI;
import com.gruppe14.explodingkitten.common.Exceptions.UsernameAlreadyExistException;
import com.gruppe14.explodingkitten.common.Exceptions.UsernameDoesntExistException;
import com.gruppe14.explodingkitten.common.Exceptions.WrongPasswordException;
import com.gruppe14.explodingkitten.common.data.Account;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;

public class ProfilBearbeitenController {
    Client client = MainGUI.client;

    @FXML
    private AnchorPane loginAnchor;

    @FXML
    private Button returnButton;

    @FXML
    private Button bestaetigenButton;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private PasswordField password2;

    @FXML
    private TextField age;

    @FXML
    private Button delereProfileButton;

    @FXML
    void bestaetigenButtonClicked(ActionEvent event) throws IOException {
        Account account = new Account(username.getText(), password.getText(),
                Integer.parseInt(age.getText()));
        try {
        	client.adjustAccount(account);
            MainGUI.guiController.gotoHauptmenu();

        } catch (UsernameDoesntExistException e) {
            MainGUI.guiController.showAlert("Useranme doesnt exist");
        } catch (UsernameAlreadyExistException e) {
            MainGUI.guiController.showAlert("Useranme already exist");
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (WrongPasswordException e) {
            MainGUI.guiController.showAlert("Password is wrong");
        }
    }

    @FXML
    void loschenButtonClicked(ActionEvent event) throws IOException, AlreadyBoundException {
        try {
        	client.accountLoschen(username.getText(), password.getText());
            MainGUI.guiController.gotoLogin();
        } catch (WrongPasswordException e) {
            MainGUI.guiController.showAlert("Wrong password ");
        } catch (UsernameDoesntExistException e) {
            MainGUI.guiController.showAlert("Username doesnt exist");
        } catch (NotBoundException e) {
            e.printStackTrace();
        }

    }

    @FXML
    void returnButtonClicked(ActionEvent event) throws IOException {
        MainGUI.guiController.gotoHauptmenu();
    }


}
