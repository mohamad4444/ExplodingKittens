package com.gruppe14.explodingkitten.common.data.Game;

import com.gruppe14.explodingkitten.common.Exceptions.DEFUSEMustBePlayedException;
import com.gruppe14.explodingkitten.common.data.Account;
import com.gruppe14.explodingkitten.server.Server;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.util.HashMap;

public class BotSchwerig extends Spieler implements Runnable {
	double chanceforExplodingKitten;
	Spieler spielerWithMostCards;
	int oldDEFUSENumber = 0;
	boolean firstEntry = true;
	boolean DEFUSEPlayed = false;

	public double getExplodingKittenChance() {
		int explodingKittensNumber = this.spielraum.getSize() - 1;
		int cardSize = this.spielraum.cardDeck.cards.size();
		return explodingKittensNumber / cardSize;
	}

	HashMap<Integer, Card> cardPriorities = new HashMap<>();

	// 0=ExplodingKitten,1=DEFUSE,2=Favor,3=Nope,4=AttackCard,5=ShuffleCard,6=SkipCard,7-11=normal,12=cardBack,13=SeeTheFuture
	// 0-10;
	public Card getCardAccToPrior() {
		for (int i = 0; i < 10; i++) {
			Card card = cardPriorities.get(i);
			if (this.handKarten.contains(card)) {
				return card;
			}

		}
		return null;
	}

	/**
	 * constructor for bot
	 * 
	 * @param acc a temp account for bot
	 */
	public BotSchwerig(Account acc) {
		super(acc);
	}

	/**
	 * start of bot playing the game
	 */
	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(1000);
				if (this.spielraum.spielerList.size() == 1) {
					break;
				} else if (this.spielraum.contains(this.getName()) == null) {
					break;
				}
				// MustPickACard
				if (this.spielraum.state == Spielraum.State.PickingCard
						&& this.getName().equals(spielraum.attackedPlayer.getName())) {
					if (this.handKarten.contains(Card.CATCARD1)) {
						Server.spielRaumServices.get(this.spielraum.roomName).pickCard(Card.CATCARD1, this.account,
								null, 0);
					} else if (this.handKarten.contains(Card.CATCARD2)) {
						Server.spielRaumServices.get(this.spielraum.roomName).pickCard(Card.CATCARD2, this.account,
								null, 0);
					} else if (this.handKarten.contains(Card.CATCARD3)) {
						Server.spielRaumServices.get(this.spielraum.roomName).pickCard(Card.CATCARD3, this.account,
								null, 0);
					} else if (this.handKarten.contains(Card.CATCARD4)) {
						Server.spielRaumServices.get(this.spielraum.roomName).pickCard(Card.CATCARD4, this.account,
								null, 0);
					} else if (this.handKarten.contains(Card.CATCARD5)) {
						Server.spielRaumServices.get(this.spielraum.roomName).pickCard(Card.CATCARD5, this.account,
								null, 0);
					} else if (this.handKarten.contains(Card.SHUFFLE)) {
						Server.spielRaumServices.get(this.spielraum.roomName).pickCard(Card.SHUFFLE, this.account, null,
								0);
					} else if (this.handKarten.contains(Card.SEETHEFUTURE)) {
						Server.spielRaumServices.get(this.spielraum.roomName).pickCard(Card.SEETHEFUTURE, this.account,
								null, 0);
					} else if (this.handKarten.contains(Card.SKIP)) {
						Server.spielRaumServices.get(this.spielraum.roomName).pickCard(Card.SKIP, this.account, null,
								0);
					} else if (this.handKarten.contains(Card.ATTACK)) {
						Server.spielRaumServices.get(this.spielraum.roomName).pickCard(Card.ATTACK, this.account, null,
								0);
					} else if (this.handKarten.contains(Card.NOPE)) {
						Server.spielRaumServices.get(this.spielraum.roomName).pickCard(Card.NOPE, this.account, null,
								0);
					} else if (this.handKarten.contains(Card.DEFUSE)) {
						Server.spielRaumServices.get(this.spielraum.roomName).pickCard(Card.DEFUSE, this.account, null,
								0);
					} else if (this.handKarten.contains(Card.FAVOR)) {
						Server.spielRaumServices.get(this.spielraum.roomName).pickCard(Card.FAVOR, this.account, null,
								0);
					}
					// DrawingOrPlayingAcard
				} else if (this.getName().equals(spielraum.currentSpieler.getName())
						&& this.spielraum.state == Spielraum.State.DrawingCard) {
					if (Server.spielRaumServices.get(this.spielraum.roomName) == null) {
						break;
					} else {
						// Determining Choice
						if (firstEntry == true) {
							DEFUSEPlayed = false;
						}
						firstEntry = false;
						int newDEFUSENumber = this.spielraum.discardDeck.containsCard(Card.DEFUSE);
						if (newDEFUSENumber != this.oldDEFUSENumber) {
							this.oldDEFUSENumber = newDEFUSENumber;
							DEFUSEPlayed = true;
						}
						double changeOfDrawingExplodingKitten = this.getExplodingKittenChance();
						boolean shufflePlayedAfterlastDEFUSE = this.spielraum.discardDeck
								.shufflePlayedAfterlastDEFUSE();
						Spieler toAttack = null;
						int cardNumber = 0;
						for (Spieler s : this.spielraum.spielerList) {
							if (!s.getName().equals(this.getName())) {
								if (s.handKarten.size() >= cardNumber) {
									toAttack = s;
									cardNumber = s.handKarten.size();
								}
							}
						}
						// less than 40% no DEFUSE played and shuffle played==false
						// less than 40% and no DEFUSE played and shuffle played==true
						// less than 40% and DEFUSE played and shuffle played==true

						if (changeOfDrawingExplodingKitten < 0.9
								&& (DEFUSEPlayed == false || shufflePlayedAfterlastDEFUSE)) {
							Server.spielRaumServices.get(this.spielraum.roomName).drawCard(this.account);
							firstEntry = true;
							// less than 40% and DEFUSE played and shuffle played==false
						} else if (changeOfDrawingExplodingKitten < 0.9 && DEFUSEPlayed == true
								&& shufflePlayedAfterlastDEFUSE == false) {
							cardPriorities.put(0, Card.SHUFFLE);
							cardPriorities.put(1, Card.SKIP);
							cardPriorities.put(2, Card.ATTACK);
							cardPriorities.put(3, Card.FAVOR);
							for (int i = 4; i < 11; i++) {
								cardPriorities.put(i, Card.CATCARD1);
							}
							Card card = this.getCardAccToPrior();
							if (card == null) {
								Server.spielRaumServices.get(this.spielraum.roomName).drawCard(this.account);
							} else {
								switch (card) {
								case SHUFFLE:
								case SKIP:
									Server.spielRaumServices.get(this.spielraum.roomName).playCard(card, this.account,
											null, 0);
									break;
								case ATTACK:
								case FAVOR:
									Server.spielRaumServices.get(this.spielraum.roomName).playCard(card, this.account,
											toAttack, 0);
									break;
								default:
									Server.spielRaumServices.get(this.spielraum.roomName).playCard(card, this.account,
											null, 0);
									break;
								}

							}
							// ExplodingKittenChance >40%
						} else {
							if (this.top3cards[0] != null) {
								if (this.top3cards[0] != Card.EXPLODINGKITTEN) {
									Server.spielRaumServices.get(this.spielraum.roomName).drawCard(this.account);
								}
								this.top3cards = new Card[3];
							} else {
								cardPriorities.put(0, Card.SEETHEFUTURE);
								cardPriorities.put(1, Card.SKIP);
								cardPriorities.put(2, Card.ATTACK);
								cardPriorities.put(3, Card.FAVOR);
								for (int i = 4; i < 11; i++) {
									cardPriorities.put(i, Card.CATCARD1);
								}
								Card card = this.getCardAccToPrior();
								if (card == null) {
									Server.spielRaumServices.get(this.spielraum.roomName).drawCard(this.account);
								} else {
									switch (card) {
									case SHUFFLE:
									case SKIP:
										Server.spielRaumServices.get(this.spielraum.roomName).playCard(card,
												this.account, null, 0);
										break;
									case ATTACK:
									case FAVOR:
										Server.spielRaumServices.get(this.spielraum.roomName).playCard(card,
												this.account, toAttack, 0);
										break;
									default:
										Server.spielRaumServices.get(this.spielraum.roomName).playCard(card,
												this.account, null, 0);
										break;
									}

								}
							}
						}
					}
				} else if (spielraum.state == Spielraum.State.PlayingDEFUSE) {
					Server.spielRaumServices.get(this.spielraum.roomName).playCard(Card.DEFUSE, this.account, null, 0);

				}
			} catch (InterruptedException | IOException | DEFUSEMustBePlayedException | AlreadyBoundException
					| NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
