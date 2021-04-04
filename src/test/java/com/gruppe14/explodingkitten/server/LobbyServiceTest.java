package com.gruppe14.explodingkitten.server;

import com.gruppe14.explodingkitten.client.Client;
import com.gruppe14.explodingkitten.common.Exceptions.*;
import com.gruppe14.explodingkitten.common.data.Vorraum;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class LobbyServiceTest {
	static Server server;
	static ArrayList<Client> clients;
	static Registry registry;

	@BeforeEach
	void setUpBeforeClass() throws Exception {
		registry = LocateRegistry.createRegistry(1099);
		server = new Server();
		clients = new ArrayList<>();
		for (int i = 0; i < 7; i++) {
			Client c = new Client();
			c.register("player" + i, "player" + i, 18);
			c.anmelden("player" + i, "player" + i);
			c.joinLobby();
			clients.add(c);
		}
	}

	@AfterEach
	void stopClass() throws RemoteException {
		UnicastRemoteObject.unexportObject(registry, true);
	}

	@Test
	void testRaumErstellen()
			throws PlayerNumberException, RoomAlreadyExistException, AlreadyBoundException, IOException {
		Server.lobby.raumErstellen("room1", "room1", 5, clients.get(0).zustand.account);
		assertTrue(Server.spielRaumServices.containsKey("room1"));
		Vorraum vorraumData = Server.spielRaumServices.get("room1").vorraum;
		assertTrue(vorraumData.getCurrentplayers() == 1);
	}

	@Test
	void testRaumErstellen2()
			throws PlayerNumberException, RoomAlreadyExistException, AlreadyBoundException, IOException {
		Server.lobby.raumErstellen("room1", "room1", 5, clients.get(0).zustand.account);
		try {
			Server.lobby.raumErstellen("room1", "room1", 5, clients.get(1).zustand.account);
			fail();
		} catch (RoomAlreadyExistException e) {

		}
		assertTrue(Server.spielRaumServices.containsKey("room1"));
		for (Client c : clients) {
			assertEquals(Server.lobby.vorraumList.size(), c.zustand.vorraumList.size());
		}
	}

	@Test
	void testRaumErstellen3()
			throws PlayerNumberException, RoomAlreadyExistException, AlreadyBoundException, IOException {
		try {
			Server.lobby.raumErstellen("room1", "room1", 6, clients.get(1).zustand.account);
			fail();
		} catch (PlayerNumberException e) {

		}
	}

	@Test
	void testRaumErstellen4()
			throws PlayerNumberException, RoomAlreadyExistException, AlreadyBoundException, IOException {
		try {
			Server.lobby.raumErstellen("room1", "room1", 1, clients.get(1).zustand.account);
			fail();
		} catch (PlayerNumberException e) {

		}
	}

	@Test
	void testRaumBeitreten() throws PlayerNumberException, RoomAlreadyExistException, AlreadyBoundException,
			IOException, WrongPasswordException, FullRoomException, RoomDoesntExistException {
		Server.lobby.raumErstellen("room1", "room1", 5, clients.get(0).zustand.account);
		Server.lobby.raumBeitreten("room1", "room1", clients.get(1).zustand.account);
		Vorraum vorraumData = Server.spielRaumServices.get("room1").vorraum;
		assertTrue(vorraumData.spielerImVorraum.containsKey(clients.get(1).zustand.account.getBenutzername()));
		assertTrue(vorraumData.spielerImVorraum.containsKey(clients.get(0).zustand.account.getBenutzername()));
		assertEquals(2, vorraumData.getCurrentplayers());
	}

	@Test
	void testRaumBeitreten2() throws PlayerNumberException, RoomAlreadyExistException, AlreadyBoundException,
			IOException, WrongPasswordException, FullRoomException, RoomDoesntExistException {
		Server.lobby.raumErstellen("room1", "room1", 5, clients.get(0).zustand.account);
		try {
			Server.lobby.raumBeitreten("room1", "room3", clients.get(1).zustand.account);
			fail();
		} catch (WrongPasswordException e) {

		}
		Vorraum vorraumData = Server.spielRaumServices.get("room1").vorraum;
		assertFalse(vorraumData.spielerImVorraum.containsKey(clients.get(1).zustand.account.getBenutzername()));
		assertTrue(vorraumData.spielerImVorraum.containsKey(clients.get(0).zustand.account.getBenutzername()));
		assertEquals(1, vorraumData.getCurrentplayers());
	}

	@Test
	void testRaumBeitreten3() throws PlayerNumberException, RoomAlreadyExistException, AlreadyBoundException,
			IOException, WrongPasswordException, FullRoomException, RoomDoesntExistException {
		Server.lobby.raumErstellen("room1", "room1", 5, clients.get(0).zustand.account);
		Server.lobby.raumBeitreten("room1", "room1", clients.get(1).zustand.account);
		Server.lobby.raumBeitreten("room1", "room1", clients.get(2).zustand.account);
		Server.lobby.raumBeitreten("room1", "room1", clients.get(3).zustand.account);
		Server.lobby.raumBeitreten("room1", "room1", clients.get(4).zustand.account);
		try {
			Server.lobby.raumBeitreten("room1", "room1", clients.get(5).zustand.account);
			fail();
		} catch (FullRoomException e) {

		}
		Vorraum vorraumData = Server.spielRaumServices.get("room1").vorraum;
		assertTrue(vorraumData.spielerImVorraum.containsKey(clients.get(4).zustand.account.getBenutzername()));
		assertTrue(vorraumData.spielerImVorraum.containsKey(clients.get(0).zustand.account.getBenutzername()));
		assertEquals(5, vorraumData.getCurrentplayers());
	}

	@Test
	void testRaumBeitreten4() throws PlayerNumberException, RoomAlreadyExistException, AlreadyBoundException,
			IOException, WrongPasswordException, FullRoomException {
		Server.lobby.raumErstellen("room1", "room1", 5, clients.get(0).zustand.account);
		try {
			Server.lobby.raumBeitreten("room2", "room1", clients.get(4).zustand.account);
			fail();
		} catch (RoomDoesntExistException e) {

		}
	}

	@Test
	void testSendChat() throws IOException {
		Server.lobby.sendChat("hello world", clients.get(0).zustand.account);
		Server.lobby.sendChat("hel44d2", clients.get(1).zustand.account);
		Server.lobby.sendChat("hel55orld", clients.get(0).zustand.account);
		Server.lobby.sendChat("he66rld", clients.get(0).zustand.account);
		Server.lobby.sendChat("hel77world", clients.get(2).zustand.account);
		assertEquals(constuctMessage("hello world", clients.get(0)), Server.lobby.chats.get(0));
		assertEquals(constuctMessage("hel44d2", clients.get(1)), Server.lobby.chats.get(1));
		assertEquals(constuctMessage("hel55orld", clients.get(0)), Server.lobby.chats.get(2));
		assertEquals(constuctMessage("he66rld", clients.get(0)), Server.lobby.chats.get(3));
		assertEquals(constuctMessage("hel77world", clients.get(2)), Server.lobby.chats.get(4));
		for(Client c:clients) {
			assertEquals(Server.lobby.chats, clients.get(0).zustand.chatLo);
		}
	}

	public String constuctMessage(String text, Client client) {
		return client.zustand.account.getBenutzername() + ":" + text + "\n";
	}

	@Test
	void testUpdateClient() throws Exception {
		clients.get(0).raumErstellen("room1", "room1", 5);
		clients.get(2).raumBeitreten("room1", "room1");
		clients.get(3).raumBeitreten("room1", "room1");
		clients.get(1).raumErstellen("room2", "room2", 5);
		clients.get(4).raumBeitreten("room2", "room2");

		clients.get(5).sendChatLo("hello world");
		clients.get(6).sendChatLo("hi there");
		clients.get(5).sendChatLo("hi there");
		for (int i = 5; i < 7; i++) {
			assertEquals(constuctMessage("hello world", clients.get(5)), clients.get(i).zustand.chatLo.get(0));
		}
		for (int i = 0; i < 5; i++) {
			assertFalse(Server.lobby.onlineusers.contains(clients.get(i).zustand.account.getBenutzername()));
		}
		assertTrue(Server.lobby.onlineusers.contains(clients.get(5).zustand.account.getBenutzername()));
		assertEquals(3,clients.get(5).zustand.vorraumList.get(0).getCurrentplayers());
	}

}
