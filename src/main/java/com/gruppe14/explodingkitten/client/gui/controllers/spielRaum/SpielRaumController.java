package com.gruppe14.explodingkitten.client.gui.controllers.spielRaum;

import com.gruppe14.explodingkitten.client.MainGUI;
import com.gruppe14.explodingkitten.client.gui.controllers.Updatable;
import com.gruppe14.explodingkitten.client.gui.controllers.spielRaum.CardElements.CardElement;
import com.gruppe14.explodingkitten.client.gui.controllers.spielRaum.CardElements.CardElementManager;
import com.gruppe14.explodingkitten.common.Exceptions.DEFUSEMustBePlayedException;
import com.gruppe14.explodingkitten.common.data.Account;
import com.gruppe14.explodingkitten.common.data.Game.Card;
import com.gruppe14.explodingkitten.common.data.Game.Spieler;
import com.gruppe14.explodingkitten.common.data.Game.Spielraum;
import com.gruppe14.explodingkitten.common.data.Vorraum;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class SpielRaumController implements Initializable, Updatable {
	@FXML
	private Button sendButton;
	@FXML
	private TextField chatTextField;
	@FXML
	private TextArea chatTextArea, turnDescriptionChatArea, nopeDescription;
	@FXML
	private Text usernameText, roomName, cardsLeftText;
	@FXML
	public HBox clientPlayerHBox;
	@FXML
	public StackPane discardCards, cardsDeck;
	@FXML
	public VBox play1vbox, play2vbox, play3vbox, play4vbox;

	public ArrayList<Image> images;
	public ArrayList<VBox> playerVboxs;
	public HashMap<String, VBox> spielerVBoxes;
	CardElementManager cardElementManager;

	public Spielraum spielraum;
	public Spieler clientSpieler;
	public Spieler clientSpielerAtStart;
	public int vboxesCounter = 0;

	// Stage stage;
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// stage = new Stage();
		// get/Create Gameroom and clientSpieler
		// spielraum = spielraumErstellen();
		this.spielraum = MainGUI.client.zustand.spielRaum;
		for (Spieler s : spielraum.spielerList) {
			if (s.getName().contentEquals(MainGUI.client.zustand.account.getBenutzername())) {
				this.clientSpieler = s;
				this.clientSpielerAtStart = s;
			}
		}
		// setup chat
		chatTextArea.setEditable(false);
		chatTextArea.setMouseTransparent(true);
		chatTextArea.setFocusTraversable(false);
		turnDescriptionChatArea.setEditable(false);
		turnDescriptionChatArea.setMouseTransparent(true);
		turnDescriptionChatArea.setFocusTraversable(false);
		// load images
		images = new ArrayList<>();
		loadImages();

		// PlayerVBoxes
		playerVboxs = new ArrayList<>();
		spielerVBoxes = new HashMap<>();
		playerVboxs.add(play1vbox);
		playerVboxs.add(play2vbox);
		playerVboxs.add(play3vbox);
		playerVboxs.add(play4vbox);
		// start cardElementManager
		cardElementManager = new CardElementManager(this);
		CardElement.cardElementManager = cardElementManager;

		// add cards to game
		try {
			this.setUpRoom();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// this
	}

	// creates a room (temp till we get room from server)
	public Spielraum spielraumErstellen() {
		ArrayList<Account> accounts = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			accounts.add(new Account("spieler" + i, "spieler" + i, 19));
		}
		Vorraum vorraum = new Vorraum("room1", "room1", 5, accounts.get(0));
		for (int i = 1; i < 5; i++) {
			vorraum.spielerImVorraum.put("spieler" + i, accounts.get(i));
		}
		Spielraum spielraum = vorraum.createSpielraum();
		clientSpieler = spielraum.spielerList.get((spielraum.getCurrentSpielerPos() + 1) % 5);

		return spielraum;

	}

	public void setUpRoom() throws InterruptedException {
		this.roomName.setText(spielraum.roomName);
		this.usernameText.setText(this.clientSpieler.getName());
		assignVboxestoPlayers();
		removeExtraVboxes();
		updateRoom();

	}

//		Thread t=new Thread() {
//			@Override
//			public void run() {
//				updateRoom();
//			}
//		};

	public String spielerToAttack = null;

	@FXML
	void player1Clicked(MouseEvent event)
			throws RemoteException, IOException, InterruptedException, DEFUSEMustBePlayedException {
		MainGUI.guiController.showAlert("player selected");
		Text text = (Text) this.play1vbox.getChildren().get(0);
		spielerToAttack = text.getText();
	}

	@FXML
	void player2Clicked(MouseEvent event)
			throws RemoteException, IOException, InterruptedException, DEFUSEMustBePlayedException {
		MainGUI.guiController.showAlert("player selected");
		Text text = (Text) this.play2vbox.getChildren().get(0);
		spielerToAttack = text.getText();
	}

	@FXML
	void player3Clicked(MouseEvent event)
			throws RemoteException, IOException, InterruptedException, DEFUSEMustBePlayedException {
		MainGUI.guiController.showAlert("player selected");
		Text text = (Text) this.play3vbox.getChildren().get(0);
		spielerToAttack = text.getText();
	}

	@FXML
	void player4Clicked(MouseEvent event)
			throws RemoteException, IOException, InterruptedException, DEFUSEMustBePlayedException {
		MainGUI.guiController.showAlert("player selected");
		Text text = (Text) this.play4vbox.getChildren().get(0);
		spielerToAttack = text.getText();
	}

	// AssignVboxes to Spieler
	public void assignVboxestoPlayers() {
		for (Spieler s : spielraum.spielerList) {
			if (!s.getName().equals(clientSpieler.getName())) {
				spielerVBoxes.put(s.getName(), playerVboxs.get(vboxesCounter));
				vboxesCounter++;
			}
		}
	}

	// Remove extra PlayerVBoxes
	public void removeExtraVboxes() {
		for (int i = vboxesCounter; i < 4; i++) {
			playerVboxs.get(i).setVisible(false);
		}
	}

	@FXML
	void enterPressed(ActionEvent event) throws IOException {

		MainGUI.client.sendChatSp(this.chatTextField.getText());
		this.chatTextField.setText("");

	}

	@FXML
	void sendButtonClicked(ActionEvent event) throws IOException {
		MainGUI.client.sendChatSp(this.chatTextField.getText());
		this.chatTextField.setText("");
	}

	public void updateRoom() {
		clearGUI();
		updateDeck();
		updateDiscard();
		updatePlayersInformation();
		updateClientInformation();
		this.turnDescriptionChatArea.setText(this.spielraum.currentSpieler.getName() + " is drawing a card");
		for (String c : spielraum.chats) {
			chatTextArea.appendText(c);
		}
	}

	// empty gui elements
	public void clearGUI() {
		cardElementManager.clear();
		chatTextArea.clear();
		turnDescriptionChatArea.clear();
		clientPlayerHBox.getChildren().clear();
		for (VBox vbox : this.spielerVBoxes.values()) {
			HBox hbox = (HBox) vbox.getChildren().get(1);
			hbox.getChildren().clear();
		}
		discardCards.getChildren().clear();
		cardsDeck.getChildren().clear();
	}

	// add cards to Deck
	public void updateDeck() {
		this.cardsLeftText.setText("Cards Left:" + this.spielraum.cardDeck.cards.size());
		for (Card c : this.spielraum.cardDeck.cards) {
			cardElementManager.addCardToDeck(c);
		}
	}

	// add cards to Deck
	public void updateDiscard() {
		for (Card c : this.spielraum.discardDeck.cards) {
			cardElementManager.addCardToDiscard(c);
		}
	}

	// updates players
	public void updatePlayersInformation() {
		for (String spielerName : this.spielerVBoxes.keySet()) {
			Text text = (Text) this.spielerVBoxes.get(spielerName).getChildren().get(0);
			if (spielraum.contains(spielerName) != null) {
				text.setText(spielerName);
				Spieler spieler = this.spielraum.contains(spielerName);
				for (Card c : spieler.handKarten) {
					cardElementManager.addCardToPlayer(c, spieler.getName());
				}
			} else {
				text.setStrikethrough(true);
			}
		}
	}

	// update client information
	public void updateClientInformation() {
		if (spielraum.contains(this.clientSpielerAtStart.getName()) != null) {
			for (Card c : this.clientSpieler.handKarten) {
				cardElementManager.addCardToClient(c);
			}
		} else {
			this.usernameText.setStrikethrough(true);
		}
	}

	@FXML
	void zuruckButtonClicked(ActionEvent event) throws IOException {
		MainGUI.guiController.gotoSpielVorraum();
	}

	// this method is used when the user clicks on handcard
	public void karteAusspielen(Card card, String spieler)
			throws RemoteException, IOException, InterruptedException, DEFUSEMustBePlayedException {
		if (card == Card.NOPE) {
			if (this.spielraum.state == Spielraum.State.WaitingNope) {
				MainGUI.client.playNope();
			}
		} else if (this.spielraum.attackedPlayer != null
				&& this.clientSpieler.getName().equals(this.spielraum.attackedPlayer.getName())) {
			if (this.spielraum.state == Spielraum.State.PickingCard) {
				MainGUI.client.pickCard(card, null, 0);
			}
		} else if (this.clientSpieler.getName().equals(this.spielraum.currentSpieler.getName())) {
			Spieler[] attacked = new Spieler[1];
			for (Spieler s : this.spielraum.spielerList) {
				if (s.getName().equals(spieler)) {
					attacked[0] = s;
					break;
				}
			}
			if (this.spielraum.state == Spielraum.State.PlayingDEFUSE) {
				switch (card) {
				case DEFUSE:
					if (this.clientSpieler.hasCard(Card.EXPLODINGKITTEN) == 1) {
						Stage stage = new Stage();
						VBox vbox = new VBox();
						vbox.setAlignment(Pos.CENTER);
						TextField positionInput = new TextField();
						Button button = new Button();
						int[] position = new int[1];
						// action event
						EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
							public void handle(ActionEvent e) {
								position[0] = Integer.parseInt(positionInput.getText());
								stage.close();
							}
						};
						// when button is pressed
						button.setOnAction(event);
						button.setText("Confirm");
						vbox.getChildren().addAll(positionInput, button);
						stage.setTitle("Return Exploding Kitten");
						Scene main = new Scene(vbox);
						stage.setScene(main);
						stage.showAndWait();
						boolean card2 = MainGUI.client.playCard(card, null, position[0]);
					}
					break;
				}
			} else if (this.spielraum.state == Spielraum.State.DrawingCard) {
				switch (card) {
				case SEETHEFUTURE:
					Task<Void> sleeper = new Task<Void>() {
						@Override
						protected Void call() throws Exception {
							try {
								MainGUI.client.playCard(card, null, 0);
							} catch (InterruptedException e) {
							}
							return null;
						}
					};
					new Thread(sleeper).start();
					sleeper.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
						@Override
						public void handle(WorkerStateEvent event) {
							if (spielraum.cardActivated) {
								Stage stage = new Stage();
								int size = 0;
								for (int i = 0; i < 3; i++) {
									if (clientSpieler.top3cards[i] != null) {
										size = i + 1;
									}
								}
								HBox hbox = new HBox();
								ArrayList<VBox> vboxes = new ArrayList<>();
								for (int i = 0; i < size; i++) {
									VBox vbox = new VBox();
									vbox.setAlignment(Pos.CENTER);
									vboxes.add(vbox);
									Text text = new Text("card Location:" + i);
									ImageView imageview1 = new ImageView(images.get(clientSpieler.top3cards[i].label));
									imageview1.setFitHeight(140);
									imageview1.setFitWidth(90);
									vbox.getChildren().addAll(text, imageview1);
								}
								hbox.getChildren().addAll(vboxes);
								stage.setTitle("Top3Cards");
								Scene main = new Scene(hbox);
								stage.setScene(main);
								stage.showAndWait();
							}
						}
					});
					break;
				case ATTACK:
				case FAVOR:
					if (this.spielerToAttack == null) {
						MainGUI.guiController.showAlert("must pick a player");
					} else if (spielraum.contains(this.spielerToAttack) == null) {
						MainGUI.guiController.showAlert("pick an active player");
					} else {
						Spieler attackedSpieler = this.spielraum.contains(spielerToAttack);
						Task<Void> sleeper1 = new Task<Void>() {
							@Override
							protected Void call() throws Exception {
								try {
									MainGUI.client.playCard(card, attackedSpieler, 0);
								} catch (InterruptedException e) {
								}
								return null;
							}
						};
						new Thread(sleeper1).start();
						this.spielerToAttack = null;
					}
					break;
				default:
					Task<Void> sleeper3 = new Task<Void>() {
						@Override
						protected Void call() throws Exception {
							try {
								MainGUI.client.playCard(card, null, 0);
							} catch (InterruptedException e) {
							}
							return null;
						}
					};
					new Thread(sleeper3).start();
					break;
				}
			}
		}

	}

	// this method is used when user clicks on deck
	public void karteZiehen(Card card) throws RemoteException, IOException, AlreadyBoundException, NotBoundException {
		if (this.spielraum.state == Spielraum.State.DrawingCard) {
			MainGUI.client.drawCard();
		}

	}

	private void loadImages() {
		// 0=ExplodingKitten,1=DEFUSE,2=Favor,3=Nope,4=AttackCard,5=ShuffleCard,6=SkipCard,7-11=normal,12=cardBack,13=seethefuture
		images.add(new Image(getClass().getResourceAsStream("/fxmls/images/ExplodingKittenCard.png"), 1210, 810, true,
				true));
		images.add(new Image(getClass().getResourceAsStream("/fxmls/images/DifuseCard.png"), 1210, 810, true, true));
		images.add(new Image(getClass().getResourceAsStream("/fxmls/images/FavorCard.jpg"), 1210, 810, true, true));
		images.add(new Image(getClass().getResourceAsStream("/fxmls/images/NopeCard.png"), 1210, 810, true, true));
		images.add(new Image(getClass().getResourceAsStream("/fxmls/images/AttackCard.jpg"), 1210, 810, true, true));
		images.add(new Image(getClass().getResourceAsStream("/fxmls/images/ShuffleCard.png"), 1210, 810, true, true));
		images.add(new Image(getClass().getResourceAsStream("/fxmls/images/SkipCard.png"), 1210, 810, true, true));
		images.add(new Image(getClass().getResourceAsStream("/fxmls/images/NormalCard1.png"), 1210, 810, true, true));
		images.add(new Image(getClass().getResourceAsStream("/fxmls/images/NormalCard2.png"), 1210, 810, true, true));
		images.add(new Image(getClass().getResourceAsStream("/fxmls/images/NormalCard3.png"), 1210, 810, true, true));
		images.add(new Image(getClass().getResourceAsStream("/fxmls/images/NormalCard4.png"), 1210, 810, true, true));
		images.add(new Image(getClass().getResourceAsStream("/fxmls/images/NormalCard5.png"), 1210, 810, true, true));
		images.add(new Image(getClass().getResourceAsStream("/fxmls/images/card_back.png"), 1210, 810, true, true));
		images.add(new Image(getClass().getResourceAsStream("/fxmls/images/SeeTheFuture.png"), 1210, 810, true, true));
	}

	boolean lostShown = false;

	@Override
	public void update() {
		this.spielraum = MainGUI.client.zustand.spielRaum;
		this.clientSpieler = this.spielraum.contains(MainGUI.client.zustand.account.getBenutzername());
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (spielraum.spielEnde == true && clientSpieler != null && lostShown == false) {
					lostShown = true;
					try {
						MainGUI.guiController.showAlert("congratulations you have won");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (clientSpieler == null && lostShown == false) {
					lostShown = true;
					try {
						MainGUI.guiController.showAlert("you have lost, leave the game");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				updateRoom();
				if (spielraum.state == Spielraum.State.WaitingNope) {
					SpielRaumController.this.nopeDescription.setText("");
					SpielRaumController.this.nopeDescription.appendText("play Nope Quickly");
					SpielRaumController.this.nopeDescription.appendText("\nnopes played:" + spielraum.nopePlayed);
				} else if (spielraum.state == Spielraum.State.PickingCard) {
					SpielRaumController.this.nopeDescription.setText("");
					SpielRaumController.this.nopeDescription
							.appendText(spielraum.attackedPlayer.getName() + ":\n Pick card Quickly");
				} else {
					SpielRaumController.this.nopeDescription.setText("");
				}
			}
		});
	}
}
