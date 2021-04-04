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
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class SpielServiceTest1 {
	static Server server;
	static ArrayList<Client> clients;
	static Registry registry;
	static SpielService spielService;
	static Spielraum spielraum;

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
		clients.get(0).raumErstellen("room1", "room1", 4);
		clients.get(1).raumBeitreten("room1", "room1");
		clients.get(2).raumBeitreten("room1", "room1");
		clients.get(3).raumBeitreten("room1", "room1");
		clients.get(0).spielStarten();
		spielService = Server.spielRaumServices.get(clients.get(0).zustand.spielRaum.vorraum.getName());
		spielraum = spielService.spielraum;
	}

	@AfterEach
	void stopClass() throws RemoteException {
		UnicastRemoteObject.unexportObject(registry, true);
	}

	@Test
	void entTurnTest()
			throws WrongPasswordException, FullRoomException, IOException, RoomDoesntExistException, NotBoundException,
			AlreadyBoundException, RoomAlreadyExistException, PlayerNumberException, UsernameDoesntExistException {

		Spieler currentSpieler = spielraum.currentSpieler;
		int pos = spielraum.getCurrentSpielerPos();
		spielraum.endTurn();
		assertEquals((pos + 1) % spielraum.spielerList.size(), spielraum.getCurrentSpielerPos());
		spielraum.endTurn();
		spielraum.endTurn();
		spielraum.endTurn();
		assertEquals((pos + 4) % spielraum.spielerList.size(), spielraum.getCurrentSpielerPos());
	}

	@Test
	void testSpielerEntfernen()
			throws WrongPasswordException, FullRoomException, IOException, RoomDoesntExistException, NotBoundException,
			AlreadyBoundException, RoomAlreadyExistException, PlayerNumberException, UsernameDoesntExistException {
		Spieler currentSpieler = spielraum.currentSpieler;
		Spieler nextSpieler = spielraum.spielerList
				.get((spielraum.getCurrentSpielerPos() + 1) % spielraum.spielerList.size());
		spielraum.spielerEntfernen(currentSpieler.getName());
		assertFalse(spielraum.spielerList.contains(currentSpieler));
		assertEquals(nextSpieler, spielraum.currentSpieler);
	}

	public Client getCurrent(Spielraum spielraum) {
		for (Client c : clients) {
			if (c.zustand.account.getBenutzername().equals(spielraum.currentSpieler.getName())) {
				return c;
			}
		}
		return null;

	}

	// Drawing ExplodingKitten
	@Test
	void testKarteZiehen()
			throws IOException, AlreadyBoundException, NotBoundException, WrongPasswordException, FullRoomException,
			RoomDoesntExistException, RoomAlreadyExistException, PlayerNumberException, UsernameDoesntExistException {
		// first player - index 0
		Spieler currentSpieler = spielraum.currentSpieler;
		Client currentClient = this.getCurrent(spielraum);
		int handKartenAnzahl = currentSpieler.handKarten.size();
		spielraum.cardDeck.cards.add(0, Card.EXPLODINGKITTEN);
		currentClient.drawCard();
		assertEquals(handKartenAnzahl + 1, currentSpieler.handKarten.size());
		assertEquals(spielraum.currentSpieler, currentSpieler);

	}

	// Drawing Normal Card
	@Test
	void testKarteZiehen15()
			throws IOException, AlreadyBoundException, NotBoundException, WrongPasswordException, FullRoomException,
			RoomDoesntExistException, RoomAlreadyExistException, PlayerNumberException, UsernameDoesntExistException {
		Client currentClient = this.getCurrent(spielraum);
		// first player - index 0
		Spieler currentSpieler = spielraum.currentSpieler;
		Spieler nextSpieler = spielraum.getNextPlayer();

		int handKartenAnzahl = currentSpieler.handKarten.size();
		spielraum.cardDeck.cards.add(0, Card.CATCARD1);
		currentClient.drawCard();
		assertEquals(handKartenAnzahl + 1, currentSpieler.handKarten.size());
		assertEquals(nextSpieler, spielraum.currentSpieler);

	}

	@Test
	void testKarteZiehen1() throws IOException, AlreadyBoundException, NotBoundException {
		Spieler currentSpieler = spielraum.currentSpieler;
		int handKartenAnzahl = currentSpieler.handKarten.size();
		Spieler nextSpieler = spielraum.getNextPlayer();

		spielraum.cardDeck.addCard(Card.EXPLODINGKITTEN, 0);
		Card card = spielraum.karteZiehen(currentSpieler.getName());

		assertEquals(handKartenAnzahl + 1, currentSpieler.handKarten.size());
		if (card == Card.EXPLODINGKITTEN) {
			assertEquals(spielraum.currentSpieler.getName(), currentSpieler.getName());
			assertTrue(spielraum.state == Spielraum.State.PlayingDEFUSE);
		} else {
			fail();
		}
	}

	@Test
	void testKarteZiehen2() throws IOException, AlreadyBoundException, NotBoundException {
		Spieler currentSpieler = spielraum.currentSpieler;
		int cPos = spielraum.getCurrentSpielerPos();
		// removing all defuse cards
		while (spielraum.currentSpieler.handKarten.contains(Card.DEFUSE)) {
			spielraum.currentSpieler.handKarten.remove(Card.DEFUSE);
		}
		int handKartenAnzahl = currentSpieler.handKarten.size();

		Spieler nextSpieler = spielraum.getNextPlayer();

		spielraum.cardDeck.addCard(Card.EXPLODINGKITTEN, 0);
		Card card = spielraum.karteZiehen(currentSpieler.getName());
		assertEquals(handKartenAnzahl + 1, currentSpieler.handKarten.size());

		if (card == Card.EXPLODINGKITTEN) {
			assertEquals(spielraum.currentSpieler, nextSpieler);
			assertEquals(cPos % spielraum.getSize(), spielraum.getCurrentSpielerPos());
			// assertFalse(spielraum.DEFUSEForced);
		} else {
			fail();
		}
	}

	// Testing drawed Exploding kitten and now must play DEFUSE
	@Test
	void testKarteAuspielen() throws InterruptedException, DEFUSEMustBePlayedException, IOException,
			AlreadyBoundException, NotBoundException {
		Spieler currentSpieler = spielraum.currentSpieler;
		int handKartenAnzahl = currentSpieler.handKarten.size();

		spielraum.cardDeck.addCard(Card.EXPLODINGKITTEN, 0);
		Card card = spielraum.karteZiehen(currentSpieler.getName());
		if (card == Card.EXPLODINGKITTEN) {
			assertEquals(spielraum.currentSpieler, currentSpieler);
			// assertTrue(spielraum.DEFUSEForced);
		} else {
			fail();
		}
		assertNull(spielraum.karteZiehen(currentSpieler.getName()));
		try {
			spielraum.karteAsusspielen(currentSpieler.getName(), Card.ATTACK, null, 5);
			fail();
		} catch (DEFUSEMustBePlayedException e) {

		}

		Spieler nextSpieler = spielraum.getNextPlayer();
		spielraum.playNope(currentSpieler.getName());
		spielraum.playNope(nextSpieler.getName());
		assertEquals(0, spielraum.nopePlayed);

		int hadDEFUSE = currentSpieler.hasCard(Card.DEFUSE);
		spielraum.karteAsusspielen(currentSpieler.getName(), Card.DEFUSE, null, 6);

		assertEquals(hadDEFUSE - 1, currentSpieler.hasCard(Card.DEFUSE));
		assertEquals(Card.EXPLODINGKITTEN, spielraum.cardDeck.cards.get(6));
	}

	@Test
	void testKarteAuspielenSeeTheFuture() throws InterruptedException, DEFUSEMustBePlayedException, IOException {
		// Getting curent Spieler
		Spieler currentSpieler = spielraum.currentSpieler;
		Spieler nextSpieler = spielraum.getNextPlayer();
		spielService.playCard(Card.SEETHEFUTURE, currentSpieler.account, null, 0);
		assertEquals(spielraum.cardDeck.cards.get(0), spielraum.currentSpieler.top3cards[0]);
		assertEquals(spielraum.cardDeck.cards.get(1), spielraum.currentSpieler.top3cards[1]);
		assertEquals(spielraum.cardDeck.cards.get(2), spielraum.currentSpieler.top3cards[2]);
	}

	// Testing SeeTheFuture
	@Test
	void testKarteAuspielenSHUFFLE() throws InterruptedException, DEFUSEMustBePlayedException, IOException {
		// Getting curent Spieler
		Spieler currentSpieler = spielraum.currentSpieler;
		LinkedList<Card> cards = new LinkedList<>(spielraum.cardDeck.cards);
		int i = 0;
		for (Card card : cards) {
			if (!spielraum.cardDeck.cards.get(i).equals(card)) {
				fail();
			}
			i++;
		}
		spielService.playCard(Card.SHUFFLE, currentSpieler.account, null, 0);
		i = 0;
		int different = 0;
		for (Card card : cards) {
			if (!spielraum.cardDeck.cards.get(i).equals(card)) {
				different++;
			} else {

			}
			i++;
		}
		System.out.println(different);
		assertTrue(different > 10);

	}

	// Testing SeeTheFuture
	@Test
	void testKarteAuspielenSkip() throws InterruptedException, DEFUSEMustBePlayedException, IOException {
		// Getting curent Spieler
		Spieler currentSpieler = spielraum.currentSpieler;
		Spieler nextSpieler = spielraum.getNextPlayer();
		spielraum.turnsLeft = 2;
		LinkedList<Card> cards = new LinkedList<>(spielraum.cardDeck.cards);
		spielService.playCard(Card.SKIP, currentSpieler.account, null, 0);
		assertEquals(currentSpieler, spielraum.currentSpieler);
		spielService.playCard(Card.SKIP, currentSpieler.account, null, 0);
		assertEquals(nextSpieler, spielraum.currentSpieler);
	}

	@Test
	void testKarteAuspielenATTACK() throws InterruptedException, DEFUSEMustBePlayedException, IOException {
		// Getting curent Spieler
		Spieler currentSpieler = spielraum.currentSpieler;
		Spieler nextSpieler = spielraum.getNextPlayer();
		Spieler thirdPlayer = spielraum.spielerList
				.get((spielraum.getCurrentSpielerPos() + 3) % spielraum.getSize());
		spielraum.turnsLeft = 2;
		LinkedList<Card> cards = new LinkedList<>(spielraum.cardDeck.cards);
		spielService.playCard(Card.ATTACK, currentSpieler.account, thirdPlayer, 0);
		assertEquals(thirdPlayer, spielraum.currentSpieler);
		assertEquals(2, spielraum.turnsLeft);
		spielService.playCard(Card.ATTACK, thirdPlayer.account, nextSpieler, 0);
		assertEquals(nextSpieler, spielraum.currentSpieler);
		assertEquals(2, spielraum.turnsLeft);
	}
	@Test
	void testKarteAuspielenFavor() throws InterruptedException, DEFUSEMustBePlayedException, IOException {
		// Getting curent Spieler
		Spieler currentSpieler = spielraum.currentSpieler;
		Spieler nextSpieler = spielraum.getNextPlayer();
		spielraum.turnsLeft = 2;
		currentSpieler.handKarten.remove();
		currentSpieler.handKarten.add(Card.FAVOR);
		LinkedList<Card> cards = new LinkedList<>(spielraum.cardDeck.cards);
		Thread t = new Thread() {
			public void run() {
				try {
					spielService.playCard(Card.FAVOR, currentSpieler.account, nextSpieler, 0);
				} catch (InterruptedException | DEFUSEMustBePlayedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		t.start();
		while (spielraum.state !=Spielraum.State.PickingCard) {
			Thread.sleep(50);
		}
		assertEquals(nextSpieler, spielraum.attackedPlayer);
		spielService.pickCard(nextSpieler.handKarten.get(4), nextSpieler.account, null, 0);
		t.join();
		assertEquals(currentSpieler, spielraum.currentSpieler);
		assertEquals(7, nextSpieler.handKarten.size());
		assertEquals(8, spielraum.currentSpieler.handKarten.size());
	}

	// Testing SeeTheFuture with 1 Nope
	@Test
	void testPlayNope1() throws InterruptedException, DEFUSEMustBePlayedException {

		// Getting curent Spieler
		Spieler currentSpieler = spielraum.currentSpieler;
		Spieler nextSpieler = spielraum.getNextPlayer();
		Thread clientCurrentSpieler = new Thread() {
			@Override
			public void run() {
				try {
					spielService.playCard(Card.SEETHEFUTURE, currentSpieler.account, null, 0);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DEFUSEMustBePlayedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		Thread client2 = new Thread() {
			@Override
			public void run() {
				try {
					while (spielraum.state != Spielraum.State.WaitingNope) {
						Thread.sleep(20);
					}
					spielService.playNope(Card.NOPE, nextSpieler.account);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DEFUSEMustBePlayedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		// TODO change sleep times to 2000
		clientCurrentSpieler.start();
		client2.start();
		client2.join();
		assertEquals(1, spielraum.nopePlayed);
		clientCurrentSpieler.join();
		assertNull(spielraum.currentSpieler.top3cards[0]);
		assertNull(spielraum.currentSpieler.top3cards[1]);
		assertNull(spielraum.currentSpieler.top3cards[2]);
	}

	// Testing SeeTheFuture 2 Nope Cards
	@Test
	void testPlayNope2() throws InterruptedException, DEFUSEMustBePlayedException {
		// Getting curent Spieler
		Spieler currentSpieler = spielraum.currentSpieler;
		Spieler nextSpieler = spielraum.getNextPlayer();
		Thread clientCurrentSpieler = new Thread() {
			@Override
			public void run() {
				try {
					spielService.playCard(Card.SEETHEFUTURE, currentSpieler.account, null, 0);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DEFUSEMustBePlayedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		Thread client2 = new Thread() {
			@Override
			public void run() {
				try {
					while (spielraum.state != Spielraum.State.WaitingNope) {
						Thread.sleep(20);
					}
					spielService.playNope(Card.NOPE, nextSpieler.account);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DEFUSEMustBePlayedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		Thread clientCurrentSpieler2 = new Thread() {
			@Override
			public void run() {
				try {
					while (spielraum.state != Spielraum.State.WaitingNope) {
						Thread.sleep(20);
					}
					Thread.sleep(1000);
					spielService.playNope(Card.NOPE, nextSpieler.account);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DEFUSEMustBePlayedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		clientCurrentSpieler.start(); // Seethefuture
		client2.start(); // Nope played
		clientCurrentSpieler2.start();// Nope played
		client2.join();
		clientCurrentSpieler2.join();
		assertEquals(2, spielraum.nopePlayed);
		clientCurrentSpieler.join();
		assertEquals(spielraum.cardDeck.cards.get(0), spielraum.currentSpieler.top3cards[0]);
		assertEquals(spielraum.cardDeck.cards.get(1), spielraum.currentSpieler.top3cards[1]);
		assertEquals(spielraum.cardDeck.cards.get(2), spielraum.currentSpieler.top3cards[2]);
	}

	@Test
	void testPlayNope3() throws InterruptedException, DEFUSEMustBePlayedException {
		// Getting curent Spieler
		Spieler currentSpieler = spielraum.currentSpieler;
		Spieler nextSpieler = spielraum.getNextPlayer();
		Thread clientCurrentSpieler = new Thread() {
			@Override
			public void run() {
				try {
					spielService.playCard(Card.SEETHEFUTURE, currentSpieler.account, null, 0);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DEFUSEMustBePlayedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		Thread client2 = new Thread() {
			@Override
			public void run() {
				try {
					while (spielraum.state != Spielraum.State.WaitingNope) {
						Thread.sleep(20);
					}
					Thread.sleep(1500);
					spielService.playNope(Card.NOPE, nextSpieler.account);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DEFUSEMustBePlayedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		Thread clientCurrentSpieler2 = new Thread() {
			@Override
			public void run() {
				try {
					while (spielraum.state != Spielraum.State.WaitingNope) {
						Thread.sleep(20);
					}
					Thread.sleep(1500);
					spielService.playNope(Card.NOPE, nextSpieler.account);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DEFUSEMustBePlayedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		clientCurrentSpieler.start(); // Seethefuture
		client2.start(); // Nope played
		clientCurrentSpieler2.start();// Nope played

		client2.join();
		clientCurrentSpieler2.join();
		assertEquals(2, spielraum.nopePlayed);
		clientCurrentSpieler.join();
		assertEquals(spielraum.cardDeck.cards.get(0), spielraum.currentSpieler.top3cards[0]);
		assertEquals(spielraum.cardDeck.cards.get(1), spielraum.currentSpieler.top3cards[1]);
		assertEquals(spielraum.cardDeck.cards.get(2), spielraum.currentSpieler.top3cards[2]);
	}
}
