package com.gruppe14.explodingkitten.common.serverIF;

import com.gruppe14.explodingkitten.common.Exceptions.UsernameAlreadyExistException;
import com.gruppe14.explodingkitten.common.Exceptions.UsernameDoesntExistException;
import com.gruppe14.explodingkitten.common.Exceptions.WrongPasswordException;
import com.gruppe14.explodingkitten.common.data.Account;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;

public interface DatenBankIF extends Remote {
	Account anmelden(String username, String password, String url) throws
            WrongPasswordException, UsernameDoesntExistException, NotBoundException, IOException;

	void registrieren(String name, String password, int alter)
			throws UsernameAlreadyExistException, IOException;

	void adjustAccount(Account olddata, Account newData) throws UsernameAlreadyExistException,
			UsernameDoesntExistException, WrongPasswordException, IOException, NotBoundException;

	Account accountLoschen(String name, String password) throws WrongPasswordException,
			UsernameDoesntExistException, NotBoundException, IOException, AlreadyBoundException;

	void abmelden(Account acc, String roomName) throws IOException, NotBoundException, AlreadyBoundException;

}
