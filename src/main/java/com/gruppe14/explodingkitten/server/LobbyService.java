package com.gruppe14.explodingkitten.server;

import com.gruppe14.explodingkitten.common.Exceptions.*;
import com.gruppe14.explodingkitten.common.data.Account;
import com.gruppe14.explodingkitten.common.data.Vorraum;
import com.gruppe14.explodingkitten.common.serverIF.LobbyIF;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;

public class LobbyService extends UnicastRemoteObject implements LobbyIF {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public LinkedList<String> chats;
	public LinkedList<String> onlineusers;
	public LinkedList<Vorraum> vorraumList;
	public final String URL = "rmi://localhost:1099/LobbyService";

	/**
	 * Constructor for lobby
	 *
	 * @throws RemoteException       server connections problems
	 * @throws MalformedURLException wrong url formation
	 * @throws AlreadyBoundException lobby is already bound
	 */
	public LobbyService() throws RemoteException, MalformedURLException, AlreadyBoundException {
		super();
		Naming.bind(URL, this);
		chats = new LinkedList<>();
		onlineusers = new LinkedList<>();
		vorraumList = new LinkedList<>();

	}

	/**
	 * adding user to lobby onlineusers
	 *
	 * @param acc to add to lobby
	 * @throws IOException gui update error
	 */
	@Override
	public void joinLobby(Account acc) throws IOException {
		this.onlineusers.add(acc.getBenutzername());
		Server.updateClient("all", null);

	}

	/**
	 * remove user to lobby onlineusers
	 *
	 * @param acc to removefrom lobby
	 * @throws IOException gui update error
	 */
	@Override
	public void exitLobby(Account acc) throws IOException {
		this.onlineusers.remove(acc.getBenutzername());
		Server.updateClient("all", null);
	}

	/**
	 * Creates a new Vorraum
	 *
	 * @param raumName      to create
	 * @param password      of the room
	 * @param spielerAnzahl max spieler number
	 * @param host          of the room
	 * @return Room url
	 * @throws PlayerNumberException     if max spieler not between 2 and 5
	 * @throws RoomAlreadyExistException another room with same name exists
	 * @throws AlreadyBoundException     room is already bound in registry
	 * @throws IOException               gui update error
	 */
	@Override
	public String raumErstellen(String raumName, String password, int spielerAnzahl, Account host)
			throws PlayerNumberException, RoomAlreadyExistException, AlreadyBoundException, IOException {
		if (spielerAnzahl < 2 || spielerAnzahl > 5) {
			throw new PlayerNumberException("number should be between 2 and 5");
		} else if (Server.spielRaumServices.containsKey(raumName)) {
			throw new RoomAlreadyExistException("Room Exists");
		} else {
			Vorraum vorraum = new Vorraum(raumName, password, spielerAnzahl, host);
			this.vorraumList.add(vorraum);
			SpielService SpielService = new SpielService(vorraum);
			Server.spielRaumServices.put(raumName, SpielService);
			host.RoomName = raumName;
			exitLobby(host);
			Server.updateClient("all", raumName);
			for (Account acc : Server.spielRaumServices.get(raumName).vorraum.spielerImVorraum.values()) {
				if (!acc.botAccount) {
					Server.updateClient(acc.getBenutzername(), Server.spielRaumServices.get(raumName).vorraum.raumName);
				}
			}
			return SpielService.url;
		}

	}

	/**
	 * joins a vorraum
	 *
	 * @param raumName to join
	 * @param password of the room
	 * @param account  to add to room
	 * @return String of the vorraum_url
	 * @throws WrongPasswordException   wrong password of room is given
	 * @throws FullRoomException        room reached full capacity
	 * @throws IOException              gui update error
	 * @throws RoomDoesntExistException the room doesn't exist
	 */
	@Override
	public String raumBeitreten(String raumName, String password, Account account)
			throws WrongPasswordException, FullRoomException, IOException, RoomDoesntExistException {
		if (!Server.spielRaumServices.containsKey(raumName)) {
			throw new RoomDoesntExistException("");
		} else {
			Server.spielRaumServices.get(raumName).vorraum.spielerHinzufuegen(account, password);
			account.RoomName = raumName;
			exitLobby(account);
			Server.updateClient("all", raumName);
			for (Account acc : Server.spielRaumServices.get(raumName).vorraum.spielerImVorraum.values()) {
				if (!acc.botAccount) {
					Server.updateClient(acc.getBenutzername(), Server.spielRaumServices.get(raumName).vorraum.raumName);
				}
			}
			return Server.spielRaumServices.get(raumName).url;
		}
	}

	/**
	 * Adds a text to LobbyChats
	 *
	 * @param text    to add to LobbyChats
	 * @param account to take chattext from
	 * @throws IOException gui update error
	 */
	@Override
	public void sendChat(String text, Account account) throws IOException {
		if (chats.size() >= 100) {
			chats.removeFirst();
		}
		this.chats.add(account.getBenutzername() + ":" + text + "\n");
		Server.updateClient("all", null);
	}

}
