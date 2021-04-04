package com.gruppe14.explodingkitten.common.data;

import com.gruppe14.explodingkitten.common.Exceptions.FullRoomException;
import com.gruppe14.explodingkitten.common.Exceptions.WrongPasswordException;
import com.gruppe14.explodingkitten.common.data.Game.BotEinfach;
import com.gruppe14.explodingkitten.common.data.Game.BotSchwerig;
import com.gruppe14.explodingkitten.common.data.Game.Spieler;
import com.gruppe14.explodingkitten.common.data.Game.Spielraum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Vorraum implements Serializable {
	// Serialization
	private static final long serialVersionUID = 1L;

	// Important Data for Lobby
	public String raumName;
	public String password;

	public Account host;
	public int maxSpielerAnzahl;

	public HashMap<String, Account> spielerImVorraum;
	public LinkedList<String> chats = new LinkedList<>();

	// Vorraum Constructor
	public Vorraum(String name, String password, int spielerAnzahl, Account host) {
		this.raumName = name;
		this.maxSpielerAnzahl = spielerAnzahl;
		this.password = password;
		this.host = host;
		spielerImVorraum = new HashMap<>();
		spielerImVorraum.put(host.getBenutzername(), host);
	}

	public int getMaxSpielerAnzahl() {
		return maxSpielerAnzahl;
	}

	public void setMaxSpielerAnzahl(int maxSpielerAnzahl) {
		this.maxSpielerAnzahl = maxSpielerAnzahl;
	}

	/**
	 * Adds user to gameroom
	 * 
	 * @param account  to add
	 * @param password of gamerrom
	 * @throws WrongPasswordException a wrong password is given
	 * @throws FullRoomException      room is full
	 */
	public void spielerHinzufuegen(Account account, String password) throws WrongPasswordException, FullRoomException {
		if (!this.password.equals(password)) {
			throw new WrongPasswordException("Wrong Password");
		} else if (this.raumistVoll()) {
			throw new FullRoomException("Raum ist voll");
		} else {
			spielerImVorraum.put(account.getBenutzername(), account);
		}
	}

	/**removes a user from room
	 * 
	 * @param account of user to remove
	 */
	public void spielerLoeschen(Account account) {
		spielerImVorraum.remove(account.getBenutzername());
	}
	/**
	 * Adds an easy bot
	 * @param host bot adder
	 * @throws WrongPasswordException room password is wrong
	 * @throws FullRoomException room is full
	 */
	public void addEasyBot(Account host) throws WrongPasswordException, FullRoomException {
		if (host.getBenutzername().equals(this.host.getBenutzername())) {
			int i = 0;
			String botname = "EasyBot";
			while (spielerImVorraum.containsKey(botname)) {
				i++;
				botname = "EasyBot" + i;
			}
			Account botAcc = new Account(botname);
			botAcc.botAccount = true;
			spielerHinzufuegen(botAcc, getPassword());
		}
	}
	/**
	 * adds hard bot
	 * @param host that wants to add bot
	 * @throws WrongPasswordException room password is wrong
	 * @throws FullRoomException room is full
	 */
	public void addHardBot(Account host) throws WrongPasswordException, FullRoomException {
		if (host.getBenutzername().equals(this.host.getBenutzername())) {
			int i = 0;
			String botname = "HardBot";
			while (spielerImVorraum.containsKey(botname)) {
				i++;
				botname = "HardBot" + i;
			}
			Account botAcc = new Account(botname);
			botAcc.botAccount = true;
			spielerHinzufuegen(botAcc, getPassword());
		}

	}

	public Spielraum createSpielraum() {
		ArrayList<Spieler> spieler = new ArrayList<>();
		for (Account acc : spielerImVorraum.values()) {
			if (acc.getBenutzername().contains("EasyBot")) {
				spieler.add(new BotEinfach(acc));
			} else if (acc.getBenutzername().contains("HardBot")) {
				spieler.add(new BotSchwerig(acc));
			} else {
				spieler.add(new Spieler(acc));
			}
		}
		Spielraum sr = new Spielraum(this.raumName,spieler,this);
		return sr;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return raumName;
	}

	public boolean raumistVoll() {
		return spielerImVorraum.size() == maxSpielerAnzahl;
	}

	public boolean playerExists(Account player) {
		return spielerImVorraum.containsKey(player.getBenutzername());

	}

	public void setName(String name) {
		this.raumName = name;
	}

	public int getCurrentplayers() {
		return spielerImVorraum.size();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Vorraum)) {
			return false;
		}
		Vorraum vrr = (Vorraum) o;
		return this.hashCode() == vrr.hashCode();
	}
	/**
	 * Hashcode for checking equality
	 */
	@Override
	public int hashCode() {
		int hashcode = raumName.hashCode();
		hashcode = hashcode * (password.hashCode() + 1);
		hashcode = hashcode * (host.hashCode() + 1);
		hashcode = hashcode * (spielerImVorraum.size()+ 1);
		for(String s:chats) {
			hashcode = hashcode * (s.hashCode()+ 1);
		}
		hashcode = hashcode * (maxSpielerAnzahl);
		return hashcode;
	}
}
