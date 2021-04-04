package com.gruppe14.explodingkitten.common.data;

import java.io.Serializable;

public class Account implements Serializable, Comparable<Account> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6460312474339070096L;
	protected String benutzername;
	protected int alter;
	protected int gewonnen;
	protected int verloren;
	protected int gesamteSpiele;
	protected int anzAnmeldeVersuche;
	private String password;
	public String RoomName;
	public boolean botAccount = false;

	/**
	 * constructor for bot account
	 * 
	 * @param benutzername Bot name
	 */
	public Account(String benutzername) {
		this.benutzername = benutzername;
		this.password = "";
		this.alter = 0;
		this.gewonnen = 0;
		this.verloren = 0;
		this.gesamteSpiele = 0;
		this.anzAnmeldeVersuche = 0;
	}

	/**
	 * Constructor for player account
	 * 
	 * @param benutzername a unique username
	 * @param password     password
	 * @param alter        of user
	 */
	public Account(String benutzername, String password, int alter) {
		this.benutzername = benutzername;
		this.password = password;
		this.alter = alter;
		this.gewonnen = 0;
		this.verloren = 0;
		this.gesamteSpiele = 0;
		this.anzAnmeldeVersuche = 0;
	}

	/**
	 * Changes age,password,username of an account with given data
	 * 
	 * @param account new data of account
	 */
	public void updateAccountData(Account account) {
		this.benutzername = account.getBenutzername();
		this.password = account.password;
		this.alter = account.alter;
	}

	public String getBenutzername() {
		return this.benutzername;
	}

	public int getAlter() {
		return this.alter;
	}

	public int getAnzVersuche() {
		return this.anzAnmeldeVersuche;
	}

	public void setBenutzername(String newBenutzername) {
		benutzername = newBenutzername;
	}

	public void setAlter(int alter) {
		this.alter = alter;
	}

	public void setAnzVersuche(int num) {
		this.anzAnmeldeVersuche = num;
	}

	public int getGewonnen() {
		return gewonnen;
	}

	public void setGewonnen(int gewonnen) {
		this.gewonnen = gewonnen;
	}

	public int getVerloren() {
		return verloren;
	}

	public void setVerloren(int verloren) {
		this.verloren = verloren;
	}

	public int getGesamteSpiele() {
		return gesamteSpiele;
	}

	public void setGesamteSpiele(int gesamteSpiele) {
		this.gesamteSpiele = gesamteSpiele;
	}

	public int getAnzAnmeldeVersuche() {
		return anzAnmeldeVersuche;
	}

	public void setAnzAnmeldeVersuche(int anzAnmeldeVersuche) {
		this.anzAnmeldeVersuche = anzAnmeldeVersuche;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Account)) {
			return false;
		}
		Account acc = (Account) o;
		return this.hashCode() == acc.hashCode();
	}

	/**
	 * Hashcode for checking equality
	 */
	@Override
	public int hashCode() {
		int hashcode = this.getBenutzername().hashCode();
		hashcode = hashcode * (this.alter + 1);
		hashcode = hashcode * (this.gesamteSpiele + 1);
		hashcode = hashcode * (this.gewonnen + 1);
		hashcode = hashcode * (this.password.hashCode());
		return hashcode;
	}

	/**
	 * Comparing for sorting bestenliste
	 */
	@Override
	public int compareTo(Account o) {
		if (this.gewonnen > o.gewonnen)
			return 1;
		else if (this.gewonnen < o.gewonnen) {
			return -1;
		} else {
			return 0;
		}
	}

	public String getPassword() {
		return this.password;
	}
}
