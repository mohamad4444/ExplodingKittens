package com.gruppe14.explodingkitten.client.gui.controllers;

import com.gruppe14.explodingkitten.client.MainGUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SpielregelnController implements Initializable {

	@FXML
	private ScrollPane scrollPane;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// scrollPane.setPrefSize(400, 400);
		scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		scrollPane.pannableProperty().set(true);
		scrollPane.setFitToHeight(true);
		scrollPane.setFitToWidth(true);
	}

	@FXML
	void showImage1(ActionEvent event) {
		ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/fxmls/spielregeln/1.png")));
		imageView.setPreserveRatio(false);
		imageView.fitWidthProperty().bind(scrollPane.widthProperty());
		scrollPane.setContent(imageView);
	}

	@FXML
	void showImage2(ActionEvent event) {
		ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/fxmls/spielregeln/2.png")));
		imageView.setPreserveRatio(false);
		imageView.fitWidthProperty().bind(scrollPane.widthProperty());
		scrollPane.setContent(imageView);
	}

	@FXML
	void showImage3(ActionEvent event) {
		ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/fxmls/spielregeln/3.png")));
		imageView.setPreserveRatio(false);
		imageView.fitWidthProperty().bind(scrollPane.widthProperty());
		scrollPane.setContent(imageView);
	}

	@FXML
	void showImage4(ActionEvent event) {
		ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/fxmls/spielregeln/4.png")));
		imageView.setPreserveRatio(false);
		imageView.fitWidthProperty().bind(scrollPane.widthProperty());
		scrollPane.setContent(imageView);
	}

	@FXML
	void showImage5(ActionEvent event) {
		ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/fxmls/spielregeln/5.png")));
		imageView.setPreserveRatio(false);
		imageView.fitWidthProperty().bind(scrollPane.widthProperty());
		scrollPane.setContent(imageView);
	}

	@FXML
	void showImage6(ActionEvent event) {
		ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/fxmls/spielregeln/6.png")));
		imageView.setPreserveRatio(false);
		imageView.fitWidthProperty().bind(scrollPane.widthProperty());
		scrollPane.setContent(imageView);
	}

	@FXML
	void showImage7(ActionEvent event) {
		ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/fxmls/spielregeln/7.png")));
		imageView.setPreserveRatio(false);
		imageView.fitWidthProperty().bind(scrollPane.widthProperty());
		scrollPane.setContent(imageView);
	}

	@FXML
	void returnToMenu(ActionEvent event) throws IOException {
		MainGUI.guiController.gotoHauptmenu();
	}
}
