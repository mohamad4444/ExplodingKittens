package com.gruppe14.explodingkitten.common.data.Game;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;

public class Deck implements Serializable {
	private static final long serialVersionUID = 1L;
	public LinkedList<Card> cards;
	public int playersNumber;

	/**
	 * Constructor for deck
	 * 
	 * @param playersNumber number of players in room
	 */
	public Deck(int playersNumber) {
		cards = new LinkedList<>();
		this.playersNumber = playersNumber;

	}

	/**
	 * Adds all cards to deck except defuse and exploding kittens
	 */
	public void step1() {
		for (Card card : Card.values()) {
			if (card != Card.EXPLODINGKITTEN && card != Card.DEFUSE) {
				add(card, 4);
			}
		}
		add(Card.NOPE, 1);
		add(Card.SEETHEFUTURE, 1);
		// add(Card.EXPLODINGKITTEN, 4);
		// add(Card.DEFUSE,6)
	}

	/**
	 * Adds remaining defuse cards pased on players number
	 */
	public void step2() {
		if (playersNumber == 2 || playersNumber == 3) {
			add(Card.DEFUSE, 2);
		} else {
			add(Card.DEFUSE, 6 - playersNumber);

		}
	}

	/**
	 * shuffles card deck
	 */
	public void step3() {
		Collections.shuffle(cards);
	}

	/**
	 * adds exploding kittens to deck and shuffles
	 */
	public void step4() {
		add(Card.EXPLODINGKITTEN, playersNumber - 1);
		Collections.shuffle(cards);
	}

	/**
	 * Adds multiple cards of same type to deck
	 * 
	 * @param card   type of card to add
	 * @param number how many times
	 */
	public void add(Card card, int number) {
		for (int i = 0; i < number; i++) {
			cards.add(card);
		}
	}

	/**
	 * shuffles deck
	 */
	public void stapelMischen() {
		Collections.shuffle(cards);
	}

	/**
	 * removes card on top
	 * 
	 * @return card removed
	 */
	public Card removeCard() {
		return cards.pop();
	}

	/**
	 * removes explodingkitten from deck in case a player leaves
	 */
	public void removeExplodingKitten() {
		cards.remove(Card.EXPLODINGKITTEN);
	}

	/**
	 * Adds an ExplodingKitten to a specified location
	 * 
	 * @param card     explodingKitten to add
	 * @param location location of explodingKitten
	 */
	public void addCard(Card card, int location) {
		cards.add(location, card);
	}

	public int containsCard(Card card) {
		int i = 0;
		for (Card c : this.cards) {
			if (c.equals(card)) {
				i++;
			}
		}
		return i;
	}

	public boolean shufflePlayedAfterlastDEFUSE() {
		int lastDefuse = -1;
		for (int i = 0; i < this.cards.size(); i++) {
			Card c = this.cards.get(i);
			if (c == Card.DEFUSE) {
				lastDefuse = i;
			}
		}
		if (lastDefuse != -1) {
			for (int i = lastDefuse; i < this.cards.size(); i++) {
				Card c = this.cards.get(i);
				if (c == Card.SHUFFLE) {
					return true;
				}
			}
		}
		return false;
	}
}
