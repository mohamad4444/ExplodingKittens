package com.gruppe14.explodingkitten.common.serverIF;

import com.gruppe14.explodingkitten.common.Exceptions.*;
import com.gruppe14.explodingkitten.common.data.Account;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.Remote;

public interface LobbyIF extends Remote {

	String raumErstellen(String raumName, String password, int spielerAnzahl, Account account)
			throws RoomAlreadyExistException, PlayerNumberException, WrongPasswordException,
			UsernameDoesntExistException, AlreadyBoundException, IOException;

	String raumBeitreten(String raumName, String password, Account account)
			throws WrongPasswordException, FullRoomException, IOException, RoomDoesntExistException;

	void sendChat(String text, Account account) throws IOException;

	void joinLobby(Account acc) throws IOException;

	void exitLobby(Account acc) throws IOException;

}