package com.gruppe14.explodingkitten.common.data.Game;

import com.gruppe14.explodingkitten.common.Exceptions.DEFUSEMustBePlayedException;
import com.gruppe14.explodingkitten.common.data.Account;
import com.gruppe14.explodingkitten.common.data.Vorraum;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Spielraum implements Serializable {

	private static final long serialVersionUID = 1L;
	public volatile String roomName;
	public volatile Deck cardDeck;
	public volatile Deck discardDeck;
	public volatile ArrayList<Spieler> spielerList;
	public volatile Spieler currentSpieler;
	public volatile int turnsLeft = 1;
	public volatile Vorraum vorraum;
	public volatile boolean waitstarting = false;
	public volatile Card cardPlayed=null;
	public enum State {
		DrawingCard, PlayingDEFUSE, PickingCard, WaitingNope
	}

	public State state = State.DrawingCard;
	public volatile Integer nopePlayed = 0;
	public volatile Spieler attackedPlayer;
	public volatile Card cardpicked = null;
	volatile Lock lock1 = new Lock();
	volatile Lock lock2 = new Lock();
	public volatile boolean cardActivated = false;
	public volatile LinkedList<String> chats = new LinkedList<>();
	// ActivationTime
	public volatile long startTime, endTime;
	// DrawTime
	public volatile long startTimeDraw, endTimeDraw;
	// DEFUSECardTime
	public volatile long starTimetDEFUSE, endTimeDEFUSE;
	public volatile boolean spielEnde = false;

	// Attacked playerd !=null must pick car
	// CurrentPlayerMustPlay draw,or karteausspielen
	// RandomPlayer plays nope if card is played

	/**
	 * Constructor for spielraum
	 *
	 * @param roomName      to give to room
	 * @param spielerImRaum list of players to adds
	 * @param vorraum vorraum
	 */
	public Spielraum(String roomName, ArrayList<Spieler> spielerImRaum, Vorraum vorraum) {
		this.vorraum = vorraum;
		this.roomName = roomName;
		this.spielerList = spielerImRaum;
		double zufallszahl = Math.random();
		int random = (int) (zufallszahl * 10);
		random = random % spielerImRaum.size();
		this.currentSpieler = spielerImRaum.get(random);
		cardDeck = new Deck(spielerImRaum.size());
		discardDeck = new Deck(1);
		kartenverteilen();
		for (int i = 0; i < this.getSize(); i++) {
			Spieler s = this.spielerList.get(i);
			s.spielraum = this;
			if (s instanceof BotEinfach) {
				BotEinfach bot = (BotEinfach) s;
				Thread t = new Thread(bot);
				t.start();
			}
		}
	}

	/**
	 * Gives each player 8 cards and sets up the game
	 */
	public void kartenverteilen() {
		// Step1 adding all cards except ExplodingKittins and DEFUSE
		cardDeck.step1();
		// step 2adding DEFUSE card to game
		cardDeck.step2();
		// step 3 mixing up cards
		cardDeck.step3();
		// give DEFUSE to all
		for (Spieler s : spielerList) {
			s.handKarten.add(Card.DEFUSE);
		}
		// giving each player 7 random cards
		for (int i = 0; i < 7; i++) {
			for (Spieler s : spielerList) {
				s.handKarten.add(cardDeck.removeCard());
			}
		}
		// adding exploding kittens to deck and mixing it
		cardDeck.step4();
	}

	/**
	 * removes player from game
	 *
	 * @param username player to be removed
	 */
	public void spielerEntfernen(String username) {
		if (username.equals(currentSpieler.getName())) {
			turnsLeft = 1;
			endTurn();
			for (Spieler s : spielerList) {
				if (s.getName().equals(username)) {
					int pos = this.getPoistion(s.getName());
					spielerList.remove(s);
					currentSpieler = spielerList.get(pos % this.getSize());
					break;
				}
			}
		} else {
			for (Spieler s : spielerList) {
				if (s.getName().equals(username)) {
					spielerList.remove(s);
					break;
				}
			}
		}

	}

	public int getPoistion(String name) {
		for (int i = 0; i < this.spielerList.size(); i++) {
			Spieler spieler = this.spielerList.get(i);
			if (spieler.getName().equals(name)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * plays a nope card
	 *
	 * @throws InterruptedException in case sleep is stopped
	 * @param username ts
	 */
	public void playNope(String username) throws InterruptedException {
		synchronized (lock2) {
			if (state == State.WaitingNope) {
				Spieler s = this.contains(username);
				this.discardDeck.cards.add(Card.NOPE);
				s.handKarten.remove(Card.NOPE);
				nopePlayed++;
			}
			startTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
		}
	}

	/**
	 * FavorCard- Opponent picking a card
	 *
	 * @param username of player to pick the card
	 * @param card     card to pick
	 */
	public void pickCard(String username, Card card) {
		synchronized (lock2) {
			if (state == State.PickingCard) {
				if (attackedPlayer != null && attackedPlayer.getName().equals(username)) {
					this.cardpicked = card;
					return;
				}
			}
		}
	}

	class Waiter implements Runnable {
		int miliseconds;

		Waiter(int miliseconds) {
			this.miliseconds = miliseconds;
		}

		@Override
		public void run() {
			try {
				waitTimeout(miliseconds);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * waits a number of seconds before activating action
	 *
	 * @param miliseconds to wait for
	 * @throws InterruptedException exit sleep
	 */
	public void waitTimeout(int miliseconds) throws InterruptedException {
		synchronized (lock1) {
			this.startTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
			this.endTime = this.startTime;
			while (endTime - startTime < miliseconds) {
				Thread.sleep(20);
				endTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
			}
			lock1.notifyAll();
		}
	}

	/**
	 * plays a card
	 *
	 * @param username       that wants to play a card
	 * @param karte          to play
	 * @param attackedPlayer picked player for attack
	 * @param cardPosition   position to put pack explodingKiten or card to take
	 * @throws InterruptedException        exit sleep
	 * @throws DEFUSEMustBePlayedException exploding kitten is drawn and DEFUSE must
	 *                                     be played
	 */
	public void karteAsusspielen(String username, Card karte, Spieler attackedPlayer, int cardPosition)
			throws InterruptedException, DEFUSEMustBePlayedException {
		synchronized (lock1) {
			if (this.currentSpieler.getName().equals(username)) {
				switch (state) {
				case DrawingCard:
					if (karte != Card.DEFUSE && karte != Card.EXPLODINGKITTEN) {
						this.currentSpieler.top3cards = new Card[3];
						this.discardDeck.cards.add(karte);
						this.currentSpieler.handKarten.remove(karte);
						cardPlayed=karte;
						this.attackedPlayer = attackedPlayer;
						state = State.WaitingNope;
						// UpdateClient
						lock1.wait();
						if (nopePlayed % 2 == 0) {
							activateCard(username, karte, attackedPlayer, cardPosition);
							cardActivated = true;
						} else {
							cardActivated = false;
						}
						nopePlayed = 0;
						state = State.DrawingCard;
					}
					break;
				case PlayingDEFUSE:
					if (karte != Card.DEFUSE) {
						throw new DEFUSEMustBePlayedException("");
					} else {
						this.currentSpieler.handKarten.remove(Card.EXPLODINGKITTEN);
						this.currentSpieler.handKarten.remove(Card.DEFUSE);
						this.discardDeck.cards.add(Card.DEFUSE);
						cardDeck.cards.add(cardPosition, Card.EXPLODINGKITTEN);
						this.state = State.DrawingCard;
						endTurn();
					}
					break;
				}
			}
		}
	}

	// Activates card
	public void activateCard(String username, Card karte, Spieler attackedPlayer, int cardPosition)
			throws InterruptedException, DEFUSEMustBePlayedException {

		switch (karte) {
		case SEETHEFUTURE:
			currentSpieler.top3cards[0] = cardDeck.cards.get(0);
			currentSpieler.top3cards[1] = cardDeck.cards.get(1);
			currentSpieler.top3cards[2] = cardDeck.cards.get(2);
			break;
		case SHUFFLE:
			this.cardDeck.stapelMischen();
			break;
		case SKIP:
			endTurn();
			break;
		case ATTACK:
			System.out.println("activation"+attackedPlayer.getName());
			System.out.println("activation"+this.getPoistion(attackedPlayer.getName()));
			currentSpieler = this.spielerList.get(this.getPoistion(attackedPlayer.getName()));
			System.out.println(currentSpieler.getName());
			turnsLeft = 2;
			break;
		case FAVOR:
			this.state = State.PickingCard;
			lock1.wait();
			if (this.cardpicked != null) {
				this.attackedPlayer.removeCard(this.cardpicked);
				this.currentSpieler.addCard(this.cardpicked);
			} else {
				if (this.attackedPlayer.handKarten.size() > 0) {
					Random rand = new Random();
					// Obtain a number between [0 - 49].
					int n = rand.nextInt(this.attackedPlayer.handKarten.size());
					this.currentSpieler.addCard(this.attackedPlayer.handKarten.remove(n));
				}
			}
			this.attackedPlayer = null;
			this.cardpicked = null;

			break;
		default:
			break;
		}
	}

	/**
	 * Ends turn for player
	 */
	public void endTurn() {
		turnsLeft--;
		if (turnsLeft == 0) {
			int pos = (this.getCurrentSpielerPos() + 1) % spielerList.size();
			currentSpieler = this.spielerList.get(pos);
			turnsLeft = 1;
		}
	}

	/**
	 * Draws a card from deck
	 *
	 * @param username that wants to draw a card
	 * @return Card drawn
	 * @throws IOException aa
	 * @throws NotBoundException aa
	 * @throws AlreadyBoundException aa
	 */
	public Card karteZiehen(String username) throws IOException, AlreadyBoundException, NotBoundException {
		synchronized (lock1) {
			if (currentSpieler.getName().equals(username) && state == State.DrawingCard) {
				Card card = this.cardDeck.removeCard();
				this.currentSpieler.handKarten.add(card);
				if (card == Card.EXPLODINGKITTEN) {
					if (!currentSpieler.handKarten.contains(Card.DEFUSE)) {
						this.spielerEntfernen(currentSpieler.getName());
						if (this.getSize() == 1) {
							for (Account acc : vorraum.spielerImVorraum.values()) {
								if (acc.getBenutzername().equals(this.currentSpieler.getName())) {
									acc.setGesamteSpiele(acc.getGesamteSpiele() + 1);
									acc.setGewonnen(acc.getGewonnen() + 1);
								} else {
									acc.setGesamteSpiele(acc.getGesamteSpiele() + 1);
									acc.setVerloren(acc.getVerloren() + 1);
								}
							}
							this.spielEnde = true;
						}
						return card;
					} else {
						state = State.PlayingDEFUSE;
						return card;
					}
				} else { // Not ExplodingKitten and ExplodingKitten wasn't drawn
					endTurn();
					return card;
				}
			}
		}
		return null;

	}

	/**
	 * gets the next player
	 *
	 * @return player after currentplayer
	 */
	public Spieler getNextPlayer() {
		return this.spielerList.get((this.getCurrentSpielerPos() + 1) % this.getSize());
	}

	public void spielEnde() {

	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Spielraum)) {
			return false;
		}
		Spielraum vrr = (Spielraum) o;
		return this.hashCode() == vrr.hashCode();
	}

	/**
	 * Hashcode for checking equality
	 */
	@Override
	public int hashCode() {
		int hashcode = roomName.hashCode();
		hashcode = hashcode * (spielerList.hashCode() + 1);
		hashcode = hashcode * (currentSpieler.hashCode() + 1);
		for (String s : chats) {
			hashcode = hashcode * (s.hashCode() + 1);
		}
		return hashcode;
	}

	public int getSize() {
		return this.spielerList.size();
	}

	public Spieler contains(String name) {
		for (Spieler s : this.spielerList) {
			if (s.getName().equals(name)) {
				return s;
			}
		}
		return null;
	}

	public int getCurrentSpielerPos() {
		return this.getPoistion(this.currentSpieler.getName());
	}

	public int getNextPlayer(int i) {
		return (this.getPoistion(this.currentSpieler.getName()) + i) % this.getSize();
	}

}
