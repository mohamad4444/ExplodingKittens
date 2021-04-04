package com.gruppe14.explodingkitten.client;

import com.gruppe14.explodingkitten.common.ClientIF;
import com.gruppe14.explodingkitten.common.Exceptions.*;
import com.gruppe14.explodingkitten.common.data.Account;
import com.gruppe14.explodingkitten.common.data.Game.Card;
import com.gruppe14.explodingkitten.common.data.Game.Spieler;
import com.gruppe14.explodingkitten.common.data.Location;
import com.gruppe14.explodingkitten.common.data.Zustand;
import com.gruppe14.explodingkitten.common.serverIF.DatenBankIF;
import com.gruppe14.explodingkitten.common.serverIF.LobbyIF;
import com.gruppe14.explodingkitten.common.serverIF.ServerIF;
import com.gruppe14.explodingkitten.common.serverIF.SpielraumIF;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Client extends UnicastRemoteObject implements ClientIF {
	private static final long serialVersionUID = 2319655083492584390L;
	public final String SERVER_URL = "rmi://localhost:1099/Server";
	public String client_url = "rmi://localhost:1099/";

	public ServerIF server_stub;
	public DatenBankIF datenbank_stub;
	public LobbyIF lobby_stub;
	public SpielraumIF spielraum_stub;

	public Zustand zustand;

	/**
	 * Constructor for client
	 * 
	 * @throws RemoteException       server connection problems
	 * @throws NotBoundException     One of server services is not bound
	 * @throws MalformedURLException wrong url is given
	 */
	public Client() throws RemoteException, NotBoundException, MalformedURLException {
		super();
		server_stub = (ServerIF) Naming.lookup(SERVER_URL);
		datenbank_stub = (DatenBankIF) Naming.lookup(server_stub.getDatenbank());
		lobby_stub = (LobbyIF) Naming.lookup(server_stub.getLobby());
		zustand = new Zustand();
	}

	// Datenbank
	/**
	 * Binds client to Internet and signs itself in server to recieve updates
	 * 
	 * @param username to sign
	 * @param password of username
	 * @throws WrongPasswordException       wrong password is given
	 * @throws UsernameDoesntExistException username doesn't exist in server
	 * @throws NotBoundException            can't find databank
	 * @throws IOException                  gui updating problem
	 */
	public void anmelden(String username, String password)
			throws WrongPasswordException, UsernameDoesntExistException, NotBoundException, IOException {
		client_url = client_url + "Clinet_" + username;
		Naming.rebind(client_url, this);
		Account acc = datenbank_stub.anmelden(username, password, client_url);
		zustand.account = acc;
	}

	/**
	 * removes client from server
	 * 
	 * @throws IOException                  gui update error
	 * @throws WrongPasswordException       wrong password given
	 * @throws UsernameDoesntExistException no such username
	 * @throws NotBoundException            database not bound
	 * @throws AlreadyBoundException        -extra
	 */
	public void abmenlden() throws IOException, WrongPasswordException, UsernameDoesntExistException, NotBoundException,
			AlreadyBoundException {
		if (this.zustand.vorraum == null) {
			datenbank_stub.abmelden(zustand.account, null);
		} else {
			datenbank_stub.abmelden(zustand.account, zustand.vorraum.raumName);
		}
	}

	/**
	 * adjusts account information
	 * 
	 * @param newData desired change in account
	 * @throws IOException                   gui update error
	 * @throws UsernameDoesntExistException  username doesn't exist
	 * @throws UsernameAlreadyExistException username already exists
	 * @throws NotBoundException             Databank not bound
	 * @throws WrongPasswordException        password is wrong
	 */
	public void adjustAccount(Account newData) throws IOException, UsernameDoesntExistException,
			UsernameAlreadyExistException, NotBoundException, WrongPasswordException {
		datenbank_stub.adjustAccount(zustand.account, newData);
	}

	/**
	 * removes an account from database and disconnects client from server
	 * 
	 * @param name     to remove
	 * @param password to verify
	 * @throws WrongPasswordException       wrong password given
	 * @throws UsernameDoesntExistException username doesn't exist
	 * @throws NotBoundException            databank not bound
	 * @throws IOException                  gui error
	 * @throws AlreadyBoundException        -extra
	 */
	public void accountLoschen(String name, String password) throws WrongPasswordException,
			UsernameDoesntExistException, NotBoundException, IOException, AlreadyBoundException {
		datenbank_stub.accountLoschen(name, password);
	}

	/**
	 * adds client to server database
	 * 
	 * @param username to add
	 * @param password to add
	 * @param alter    of user
	 * @throws UsernameAlreadyExistException same username exists
	 * @throws IOException                   gui update error
	 */
	public void register(String username, String password, int alter)
			throws UsernameAlreadyExistException, IOException {
		zustand.account = new Account(username, password, alter);
		datenbank_stub.registrieren(username, password, alter);
	}

	// Vorraum
	/**
	 * starts game room and moves all players to game
	 * 
	 * @throws IOException           gui error
	 * @throws AlreadyBoundException -extra
	 * @throws NotBoundException     -extra
	 */
	public void spielStarten() throws IOException, AlreadyBoundException, NotBoundException {
		spielraum_stub.spielStarten(zustand.account);
	}

	/**
	 * adds a bot to gameroom
	 * 
	 * @throws WrongPasswordException password is wrong
	 * @throws FullRoomException      room is full
	 * @throws IOException            gui update error
	 * @throws NotBoundException      aa
	 * @throws AlreadyBoundException  aa
	 */
	public void botHinzufugenE()
			throws WrongPasswordException, FullRoomException, IOException, AlreadyBoundException, NotBoundException {
		spielraum_stub.botHinzufugenE(zustand.account);

	}

	/**
	 * adds a bot to gameroom
	 * 
	 * @throws WrongPasswordException password is wrong
	 * @throws FullRoomException      room is full
	 * @throws IOException            gui update error
	 */
	public void botHinzufugenS() throws WrongPasswordException, FullRoomException, IOException {
		spielraum_stub.botHinzufugenS(zustand.account);
	}

	/**
	 * exits gameroom
	 * 
	 * @throws IOException           gui update error
	 * @throws NotBoundException     spielraum not bound
	 * @throws AlreadyBoundException -extra
	 */
	public void raumVerlassen() throws IOException, NotBoundException, AlreadyBoundException {
		spielraum_stub.raumVerlassen(zustand.account);
	}

	/**
	 * Sends chat to vorraum
	 * 
	 * @param text to send
	 * @throws IOException gui update error
	 */
	public void sendChatVor(String text) throws IOException {
		spielraum_stub.sendChatVor(text, zustand.account);
	}

	/**
	 * Sends chat to lobby
	 * 
	 * @param text to send
	 * @throws IOException gui update error
	 */
	public void sendChatLo(String text) throws IOException {
		lobby_stub.sendChat(text, zustand.account);
	}

	/**
	 * Sends chat to spielraum
	 * 
	 * @param text to send
	 * @throws IOException gui update error
	 */
	public void sendChatSp(String text) throws IOException {
		spielraum_stub.sendChatSp(text, zustand.account);
	}

	// lobby
	/**
	 * create a vorraum
	 * 
	 * @param raumName      room name
	 * @param password      password
	 * @param spielerAnzahl max player number
	 * @throws IOException                  gui update error
	 * @throws AlreadyBoundException        similar room is already bound
	 * @throws UsernameDoesntExistException username doesn't exist
	 * @throws WrongPasswordException       password is wrong
	 * @throws PlayerNumberException        player number is not between 2 and 5
	 * @throws RoomAlreadyExistException    room with same name exists
	 * @throws NotBoundException            not bound
	 */
	public void raumErstellen(String raumName, String password, int spielerAnzahl)
			throws RoomAlreadyExistException, PlayerNumberException, WrongPasswordException,
			UsernameDoesntExistException, AlreadyBoundException, IOException, NotBoundException {
		String vorraumURL = lobby_stub.raumErstellen(raumName, password, spielerAnzahl, zustand.account);
		spielraum_stub = (SpielraumIF) Naming.lookup(vorraumURL);
	}

	/**
	 * 
	 * @param raumName to join
	 * @param password to of room
	 * @throws WrongPasswordException   wrong password given
	 * @throws FullRoomException        room is full
	 * @throws IOException              gui update error
	 * @throws RoomDoesntExistException room doesn't exist
	 * @throws NotBoundException        room is not bound
	 */
	public void raumBeitreten(String raumName, String password)
			throws WrongPasswordException, FullRoomException, IOException, RoomDoesntExistException, NotBoundException {
		String vorraumURL = lobby_stub.raumBeitreten(raumName, password, zustand.account);
		spielraum_stub = (SpielraumIF) Naming.lookup(vorraumURL);
	}

	/**
	 * Adds user name to lobby
	 * 
	 * @throws IOException gui update error
	 */
	public void joinLobby() throws IOException {
		this.lobby_stub.joinLobby(this.zustand.account);
	}

	/**
	 * removes user name from lobby
	 * 
	 * @throws IOException gui update error
	 */
	public void exitLobby() throws IOException {
		this.lobby_stub.exitLobby(this.zustand.account);
	}

	// Spielraum
	public void drawCard() throws RemoteException, IOException, AlreadyBoundException, NotBoundException {
		this.spielraum_stub.drawCard(this.zustand.account);
	}

	public boolean playCard(Card card, Spieler attackeSpieler, int cardPosition)
			throws RemoteException, IOException, InterruptedException, DEFUSEMustBePlayedException {
		return this.spielraum_stub.playCard(card, this.zustand.account, attackeSpieler, cardPosition);
	}

	public void pickCard(Card card, Spieler attackeSpieler, int cardPosition)
			throws RemoteException, IOException, InterruptedException, DEFUSEMustBePlayedException {
		this.spielraum_stub.pickCard(card, this.zustand.account, attackeSpieler, cardPosition);
	}

	public void playNope() throws RemoteException, IOException, InterruptedException, DEFUSEMustBePlayedException {
		this.spielraum_stub.playNope(null, this.zustand.account);
	}

	/**
	 * Updates data with new data from server
	 * 
	 * @param zustand to update
	 */
	@Override
	public void updateZustand(Zustand zustand) throws IOException {
		this.zustand = zustand;
		if (MainGUI.guiController != null) {
			MainGUI.guiController.updateCurrentGui();
		}
	}

	/**
	 * Changes gui to a specific location
	 * 
	 * @param location to change to
	 */
	@Override
	public void changeGUI(Location location) throws IOException, AlreadyBoundException, NotBoundException {
		if (MainGUI.guiController != null) {
			if (location == Location.Lobby) {
				MainGUI.guiController.gotoLobby();
			} else if (location == Location.Spielraum) {
				MainGUI.guiController.gotoSpielRaum();
			} else if (location == Location.Login) {
				MainGUI.guiController.gotoLogin();
			}
		}
	}
}
