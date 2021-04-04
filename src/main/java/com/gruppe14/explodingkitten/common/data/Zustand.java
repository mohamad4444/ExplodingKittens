package com.gruppe14.explodingkitten.common.data;

import com.gruppe14.explodingkitten.common.data.Game.Spieler;
import com.gruppe14.explodingkitten.common.data.Game.Spielraum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Zustand implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Datenbank
	public Account account;
	public LinkedList<Account> bestenliste;
	// Lobby
	public LinkedList<String> chatLo;
	public LinkedList<Vorraum> vorraumList;
	public List<String> users;
	// Vorraum
	public Vorraum vorraum;
	// Spielraum
	public Spielraum spielRaum;
	public Spieler spieler;

	/**
	 * 
	 * Updatable Information to give to Client
	 * 
	 */
	public Zustand() {
		account = new Account("aa", "aa", 44);
		bestenliste = new LinkedList<>();
		chatLo = new LinkedList<>();
		vorraumList = new LinkedList<>();
		users = new LinkedList<>();
		vorraum = new Vorraum("rr", "rr", 5, account);
		ArrayList<Spieler> s = new ArrayList<>();
		s.add(new Spieler(new Account("a", "a", 5)));
		spielRaum = new Spielraum(this.vorraum.raumName, s,vorraum);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Zustand)) {
			return false;
		}
		Zustand vrr = (Zustand) o;
		return this.hashCode() == vrr.hashCode();
	}

	/**
	 * Hashcode for checking equality
	 */
	@Override
	public int hashCode() {
		int hashcode = account.hashCode();
		hashcode = hashcode * (bestenliste.hashCode() + 1);
		hashcode = hashcode * (chatLo.hashCode() + 1);
		hashcode = hashcode * (vorraumList.hashCode() + 1);
		hashcode = hashcode * (users.size() + 1);
		hashcode = hashcode * (vorraum.hashCode());
		hashcode = hashcode * (spielRaum.hashCode());
		return hashcode;
	}
}
