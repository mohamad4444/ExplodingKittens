package com.gruppe14.explodingkitten.common.data.Game;

import com.gruppe14.explodingkitten.common.data.Account;

import java.io.Serializable;
import java.util.LinkedList;

public class Spieler implements Serializable {
	private static final long serialVersionUID = 1L;

	public Account account;
	public volatile LinkedList<Card> handKarten;
	public Spielraum spielraum;

	// SeeTheFuture
	public Card[] top3cards = new Card[3];

	/**
	 * Constructor for spieler
	 *
	 * @param acc for constructing player
	 */
	public Spieler(Account acc) {
		this.account = acc;
		handKarten = new LinkedList<>();
	}

	/**
	 * removes card from players hand
	 *
	 * @param karte card to remove
	 */
	public void removeCard(Card karte) {
		synchronized (handKarten){
		this.handKarten.remove(karte);}
	}

	/**
	 * adds card to players hand
	 *
	 * @param karte to add
	 */
	public void addCard(Card karte) {
		synchronized (handKarten){
		this.handKarten.add(karte);}
	}

	/**
	 * @return name of the player
	 */
	public String getName() {
		return this.account.getBenutzername();
	}

	public int hasCard(Card card) {
		int i = 0;
		for (Card c : this.handKarten) {
			if (c == card) {
				i++;
			}
		}
		return i;

	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Spieler)) {
			return false;
		}
		Spieler vrr = (Spieler) o;
		return this.hashCode() == vrr.hashCode();
	}

	/**
	 * Hashcode for checking equality
	 */
	@Override
	public int hashCode() {
		// SeeTheFuture
		int hashcode = account.hashCode();
		hashcode = hashcode * (handKarten.hashCode() + 1);
		return hashcode;
	}

}