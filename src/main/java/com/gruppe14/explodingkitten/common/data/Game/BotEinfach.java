package com.gruppe14.explodingkitten.common.data.Game;

import com.gruppe14.explodingkitten.common.Exceptions.DEFUSEMustBePlayedException;
import com.gruppe14.explodingkitten.common.data.Account;
import com.gruppe14.explodingkitten.server.Server;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.util.HashMap;

public class BotEinfach extends Spieler implements Runnable {
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
	public BotEinfach(Account acc) {
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
				} else if (this.getName().equals(spielraum.currentSpieler.getName())
						&& this.spielraum.state == Spielraum.State.DrawingCard) {
					Server.spielRaumServices.get(this.spielraum.roomName).drawCard(this.account);
				} else if (spielraum.state == Spielraum.State.PlayingDEFUSE) {
					if (this.getName().equals(this.spielraum.currentSpieler.getName())) {
						if (this.hasCard(Card.EXPLODINGKITTEN) == 1) {
							Server.spielRaumServices.get(this.spielraum.roomName).playCard(Card.DEFUSE, this.account,
									null, 0);
						}
					}
				}
			} catch (InterruptedException | IOException | DEFUSEMustBePlayedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AlreadyBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
