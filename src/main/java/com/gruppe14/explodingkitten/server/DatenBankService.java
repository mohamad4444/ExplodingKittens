package com.gruppe14.explodingkitten.server;

import com.gruppe14.explodingkitten.common.ClientIF;
import com.gruppe14.explodingkitten.common.Exceptions.UsernameAlreadyExistException;
import com.gruppe14.explodingkitten.common.Exceptions.UsernameDoesntExistException;
import com.gruppe14.explodingkitten.common.Exceptions.WrongPasswordException;
import com.gruppe14.explodingkitten.common.data.Account;
import com.gruppe14.explodingkitten.common.data.Location;
import com.gruppe14.explodingkitten.common.data.Zustand;
import com.gruppe14.explodingkitten.common.serverIF.DatenBankIF;
import javafx.util.Pair;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedList;

public class DatenBankService extends UnicastRemoteObject implements DatenBankIF {
	private static final long serialVersionUID = 1L;
	public final String URL = "rmi://localhost:1099/DataBankService";
	HashMap<String, Account> users;
	LinkedList<Account> bestenListe;

	/**
	 * constructor of DatabankService
	 * 
	 * @throws RemoteException       server connection problems
	 * @throws MalformedURLException wrong url formation
	 */
	public DatenBankService() throws RemoteException, MalformedURLException {
		super();
		Naming.rebind(URL, this);
		users = new HashMap<>();
		this.bestenListe = new LinkedList<>();
	}

	/**
	 * registers ClientIF in server for updating
	 * 
	 * @param username -username of client
	 * @param password -password of client
	 * @param url      of ClientiF
	 * @return the account from datenbank
	 */
	@Override
	public Account anmelden(String username, String password, String url)
			throws WrongPasswordException, UsernameDoesntExistException, NotBoundException, IOException {
		Account acc = verifyAccount(username, password);
		Server.addClient(username, url);
		Server.updateClient("all", null);
		return acc;
	}

	/**
	 * removes client form lobby,server,gameroom
	 * 
	 * @param acc      to remove
	 * @param roomName to find the right spielRaumService
	 */
	@Override
	public void abmelden(Account acc, String roomName) throws IOException, NotBoundException, AlreadyBoundException {
		Server.removeClient(acc.getBenutzername());
		Server.lobby.onlineusers.remove(acc.getBenutzername());
		if (Server.spielRaumServices.get(roomName) != null) {
			Server.spielRaumServices.get(roomName).raumVerlassen(acc);
		}
		Server.updateClient("all", null);
	}

	/**
	 * Checks user credentials
	 * 
	 * @param username to check
	 * @param password to check
	 * @return Account from Database
	 * @throws WrongPasswordException       if the password is wrong
	 * @throws UsernameDoesntExistException username is not in datenbank
	 */
	public Account verifyAccount(String username, String password)
			throws WrongPasswordException, UsernameDoesntExistException {
		if (users.containsKey(username)) {
			Account useraccount = users.get(username);
			if (useraccount.getPassword().equals(password)) {
				return useraccount;
			} else {
				throw new WrongPasswordException("Wrong Password");
			}
		} else {
			throw new UsernameDoesntExistException("Wrong username");
		}

	}

	/**
	 *
	 * @param username to register
	 * @param password to register
	 * @param alter of user
	 * @throws UsernameAlreadyExistException same username exists in Database
	 * @throws IOException GUI updating exception
	 */
	@Override
	public void registrieren(String username, String password, int alter)
			throws UsernameAlreadyExistException, IOException {
		if (this.users.containsKey(username)) {
			throw new UsernameAlreadyExistException("username exists");
		} else {
			Account newAccount = new Account(username, password, alter);
			this.users.put(username, newAccount);
			this.bestenListe.add(newAccount);
			Server.updateClient("all", null);
		}

	}

	/**
	 *	remove an account from Database and all related data of client from server
	 *
	 * @param username to remove
	 * @param password to remove
	 * @return removed account
	 * @throws WrongPasswordException wrong password given
	 * @throws UsernameDoesntExistException wrong username given
	 * @throws IOException gui update failed
	 * @throws AlreadyBoundException .
	 * @throws NotBoundException .
	 */
	@Override
	public Account accountLoschen(String username, String password) throws WrongPasswordException,
			UsernameDoesntExistException, IOException, AlreadyBoundException, NotBoundException {
		verifyAccount(username, password);
		Account acc = this.users.remove(username);
		this.bestenListe.remove(acc);
		Server.clients.get(username).getKey().changeGUI(Location.Login);
		Server.clients.remove(acc.getBenutzername());
		Server.updateClient("all", null);
		return acc;
	}

	/**adjusts an account
	 * @param oldData old account data
	 * @param newData new account data
	 * @throws UsernameAlreadyExistException changing account username to a username that is already used
	 * @throws WrongPasswordException password is wrong
	 * @throws UsernameDoesntExistException wrong username given
	 * @throws IOException updating gui error
	 */
	@Override
	public void adjustAccount(Account oldData, Account newData)
			throws UsernameAlreadyExistException, WrongPasswordException, UsernameDoesntExistException, IOException {
		if (!(oldData.getBenutzername().equals(newData.getBenutzername()))
				&& this.users.containsKey(newData.getBenutzername())) {
			throw new UsernameAlreadyExistException("");
		} else {
			verifyAccount(oldData.getBenutzername(), oldData.getPassword());
			Pair<ClientIF, Zustand> client = Server.clients.remove(oldData.getBenutzername());
			Account acc = this.users.remove(oldData.getBenutzername());
			this.bestenListe.remove(acc);
			acc.updateAccountData(newData);
			this.bestenListe.add(acc);
			this.users.put(acc.getBenutzername(), acc);
			Server.clients.put(acc.getBenutzername(), client);
			Server.updateClient("all", null);
		}

	}
}
