package com.gruppe14.explodingkitten.server;

import com.gruppe14.explodingkitten.common.ClientIF;
import com.gruppe14.explodingkitten.common.data.Zustand;
import com.gruppe14.explodingkitten.common.serverIF.ServerIF;
import javafx.util.Pair;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class Server extends UnicastRemoteObject implements ServerIF {
	private static final long serialVersionUID = 1002020651075595249L;
	public static HashMap<String, Pair<ClientIF, Zustand>> clients;
	public static HashMap<String, Zustand> zustands;
	public static DatenBankService database;
	public static LobbyService lobby;
	public static HashMap<String, SpielService> spielRaumServices;
	public static final String url = "rmi://localhost:1099/Server";

	/**
	 *Constructor for server
	 * @throws RemoteException server connection problems
	 * @throws MalformedURLException wrong url formation
	 * @throws AlreadyBoundException lobby already bound
	 */
	public Server() throws RemoteException, MalformedURLException, AlreadyBoundException {
		Naming.rebind(url, this);
		database = new DatenBankService();
		lobby = new LobbyService();
		spielRaumServices = new HashMap<>();
		clients = new HashMap<>();
		System.err.println("Server ready");

	}

	/**
	 * gets the dabase url
	 * @return URl of Database
	 * @throws RemoteException server connection problems
	 */
	@Override
	public String getDatenbank() throws RemoteException {
		return database.URL;
	}

	/**
	 *
	 * gets the lobby url
	 * @return URl of lobby
	 * @throws RemoteException server connection problems
	 */
	@Override
	public String getLobby() throws RemoteException {
		return lobby.URL;
	}

	/**
	 *adds Client to Server clients
	 * @param username to add
	 * @param url for binding ClientIF
	 * @throws MalformedURLException wrong url given
	 * @throws RemoteException server connection problems
	 * @throws NotBoundException Client is not bound
	 */
	public static void addClient(String username, String url)
			throws MalformedURLException, RemoteException, NotBoundException {
		Zustand zustand = new Zustand();
		ClientIF client = (ClientIF) Naming.lookup(url);
		Server.clients.put(username, new Pair<ClientIF, Zustand>(client, zustand));
	}

	/**removes client from server
	 *
	 * @param username to remove client
	 */
	public static void removeClient(String username) {
		Server.clients.remove(username);
	}

	/**Updates all information and gives it to the client;
	 * @param username to update
	 * @param roomName where the user exists
	 * @throws IOException gui update error
	 */
	public static void updateClient(String username, String roomName) throws IOException {
		switch (username) {
		case "all":
			for (String s : clients.keySet()) {
				Zustand zustand = clients.get(s).getValue();
				zustand.account = Server.database.users.get(s);
				zustand.bestenliste = Server.database.bestenListe;
				zustand.chatLo = Server.lobby.chats;
				zustand.vorraumList = Server.lobby.vorraumList;
				zustand.users = Server.lobby.onlineusers;
				clients.get(s).getKey().updateZustand(zustand);
			}
			break;
		default:
			Zustand zustand = clients.get(username).getValue();
			zustand.account = Server.database.users.get(username);
			zustand.bestenliste = Server.database.bestenListe;
			zustand.chatLo = Server.lobby.chats;
			zustand.vorraumList = Server.lobby.vorraumList;
			zustand.users = Server.lobby.onlineusers;
			if (Server.spielRaumServices.get(roomName) != null) {
				zustand.spielRaum = Server.spielRaumServices.get(roomName).spielraum;
				zustand.vorraum = Server.spielRaumServices.get(roomName).vorraum;
			}
			if (clients.get(username) != null) {
				clients.get(username).getKey().updateZustand(zustand);
			}
		}
	}

	/**Unbind RemoteInterfaces from server side from registry
	 * @throws RemoteException server connection problems
	 * @throws MalformedURLException url is wrong
	 * @throws NotBoundException not bound exception
	 */
	public void unbindAll() throws RemoteException, MalformedURLException, NotBoundException {
		Naming.unbind(database.URL);
		Naming.unbind(lobby.URL);
		for (SpielService s : spielRaumServices.values()) {
			Naming.unbind(s.url);
		}
		System.err.println("Server unbound");
	}
}
