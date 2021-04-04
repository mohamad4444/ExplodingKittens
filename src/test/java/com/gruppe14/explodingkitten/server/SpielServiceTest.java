package com.gruppe14.explodingkitten.server;

import com.gruppe14.explodingkitten.client.Client;
import com.gruppe14.explodingkitten.common.Exceptions.*;
import com.gruppe14.explodingkitten.common.data.Game.Card;
import com.gruppe14.explodingkitten.common.data.Game.Spieler;
import com.gruppe14.explodingkitten.common.data.Game.Spielraum;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SpielServiceTest {
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
			clients.add(c);
		}
	}

	@AfterEach
	void stopClass() throws RemoteException {
		UnicastRemoteObject.unexportObject(registry, true);
	}

	@Test
	void testVorraumLookUp()
			throws AlreadyBoundException, NotBoundException, WrongPasswordException, FullRoomException, IOException,
			RoomDoesntExistException, RoomAlreadyExistException, PlayerNumberException, UsernameDoesntExistException {
		clients.get(0).raumErstellen("room1", "room1", 5);
		clients.get(1).raumBeitreten("room1", "room1");
	}

	@Test
	void testBotHinzufugenE() throws Exception {
		clients.get(0).raumErstellen("room1", "room1", 5);
		clients.get(0).botHinzufugenE();
		clients.get(0).botHinzufugenS();
		clients.get(0).botHinzufugenS();
		assertEquals(4, Server.spielRaumServices.get("room1").vorraum.getCurrentplayers());

	}

	@Test
	void testBotHinzufugenE1() throws Exception {
		clients.get(0).raumErstellen("room1", "room1", 5);
		clients.get(0).botHinzufugenE();
		clients.get(0).botHinzufugenS();
		clients.get(0).botHinzufugenS();
		clients.get(0).botHinzufugenS();
		try {
			clients.get(0).botHinzufugenS();
			fail();
		} catch (FullRoomException e) {

		}
		assertEquals(5, Server.spielRaumServices.get("room1").vorraum.getCurrentplayers());
	}

	@Test
	void testBotHinzufugenE2() throws Exception {
		clients.get(0).raumErstellen("room1", "room1", 5);
		clients.get(1).raumBeitreten("room1", "room1");
		clients.get(2).raumBeitreten("room1", "room1");
		clients.get(0).botHinzufugenE();
		clients.get(2).botHinzufugenS();
		assertEquals(4, Server.spielRaumServices.get("room1").vorraum.getCurrentplayers());
	}

	@Test
	void testSpielStarten()
			throws WrongPasswordException, FullRoomException, IOException, RoomDoesntExistException, NotBoundException,
			RoomAlreadyExistException, PlayerNumberException, UsernameDoesntExistException, AlreadyBoundException {
		clients.get(0).raumErstellen("room1", "room1", 5);
		clients.get(1).raumBeitreten("room1", "room1");
		clients.get(2).raumBeitreten("room1", "room1");
		clients.get(0).botHinzufugenE();
		clients.get(2).botHinzufugenS();
		assertNull(Server.spielRaumServices.get("room1").spielraum);
		clients.get(0).spielStarten();
		assertEquals(4, Server.spielRaumServices.get("room1").spielraum.spielerList.size());

	}

	@Test
	void testRaumVerlassen() throws Exception {
		clients.get(0).raumErstellen("room1", "room1", 5);
		clients.get(1).raumBeitreten("room1", "room1");
		clients.get(2).raumBeitreten("room1", "room1");
		clients.get(0).botHinzufugenE();
		// TODOraumverlassen
		assertEquals(1, Server.lobby.vorraumList.size());
		clients.get(0).raumVerlassen();
		assertEquals(1, Server.lobby.vorraumList.size());
	}

	@Test
	void testRaumVerlassen2() throws Exception {
		clients.get(0).raumErstellen("room1", "room1", 5);
		clients.get(1).raumBeitreten("room1", "room1");
		clients.get(2).raumBeitreten("room1", "room1");
		clients.get(0).botHinzufugenE();
		assertEquals(4, Server.spielRaumServices.get("room1").vorraum.getCurrentplayers());
		assertEquals(1, Server.lobby.vorraumList.size());

		clients.get(1).raumVerlassen();

		assertEquals(1, Server.lobby.vorraumList.size());
		assertEquals(3, Server.spielRaumServices.get("room1").vorraum.getCurrentplayers());

	}

	@Test
	void testSendChat() throws Exception {
		clients.get(0).raumErstellen("room1", "room1", 5);
		clients.get(1).raumBeitreten("room1", "room1");
		clients.get(2).raumBeitreten("room1", "room1");
		clients.get(0).sendChatVor("hello world");
		clients.get(1).sendChatVor("he555ld");
		assertTrue(Server.spielRaumServices.get("room1").vorraum.chats.get(0).contains("hello world"));

	}

	@Test
	void spielRaumKarteVerteilen() throws Exception {
		clients.get(0).raumErstellen("room1", "room1", 4);
		clients.get(1).raumBeitreten("room1", "room1");
		clients.get(2).raumBeitreten("room1", "room1");
		clients.get(3).raumBeitreten("room1", "room1");
		clients.get(0).spielStarten();
		Spielraum spielraum = clients.get(0).zustand.spielRaum;
		for (Spieler s : spielraum.spielerList) {
			assertTrue(s.handKarten.contains(Card.DEFUSE));
			assertFalse(s.handKarten.contains(Card.EXPLODINGKITTEN));
			assertEquals(8, s.handKarten.size());
		}
		int explodingKittensNumber = 0;
		for (Card c : spielraum.cardDeck.cards) {
			if (c == Card.EXPLODINGKITTEN) {
				explodingKittensNumber++;
			}
		}
		assertEquals(spielraum.getSize() - 1, explodingKittensNumber);

	}


	@Test
	void testUpdateClient()
			throws WrongPasswordException, FullRoomException, IOException, RoomDoesntExistException, NotBoundException,
			RoomAlreadyExistException, PlayerNumberException, UsernameDoesntExistException, AlreadyBoundException {
		clients.get(0).raumErstellen("room1", "room1", 5);
		clients.get(1).raumBeitreten("room1", "room1");
		clients.get(2).raumBeitreten("room1", "room1");
		clients.get(0).sendChatVor("hello world");
		clients.get(1).sendChatVor("he555ld");

		clients.get(0).spielStarten();
		for (int i = 0; i < 3; i++) {
			assertEquals(Server.clients.get("player" + i).getValue(), clients.get(i).zustand);
		}

	}

}
