package com.gruppe14.explodingkitten.common.serverIF;

import com.gruppe14.explodingkitten.common.Exceptions.DEFUSEMustBePlayedException;
import com.gruppe14.explodingkitten.common.Exceptions.FullRoomException;
import com.gruppe14.explodingkitten.common.Exceptions.WrongPasswordException;
import com.gruppe14.explodingkitten.common.data.Account;
import com.gruppe14.explodingkitten.common.data.Game.Card;
import com.gruppe14.explodingkitten.common.data.Game.Spieler;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SpielraumIF extends Remote {

	boolean spielStarten(Account host) throws IOException, AlreadyBoundException, NotBoundException;
	void botHinzufugenE(Account host) throws WrongPasswordException, FullRoomException, IOException;
	void botHinzufugenS(Account host) throws WrongPasswordException, FullRoomException, IOException;
	void raumVerlassen(Account acc) throws IOException, NotBoundException, AlreadyBoundException;
	void sendChatSp(String text, Account acc) throws IOException;
	Card drawCard(Account account) throws RemoteException, IOException, AlreadyBoundException, NotBoundException;
	void sendChatVor(String chat, Account account) throws IOException;

	boolean playCard(Card card, Account account, Spieler attackedPlayer, int cardPosition) throws RemoteException, InterruptedException, DEFUSEMustBePlayedException, IOException;

	void pickCard(Card card, Account account, Spieler attackedPlayer, int cardPosition)
			throws RemoteException, IOException, InterruptedException, DEFUSEMustBePlayedException;
	void playNope(Card card, Account account)
			throws RemoteException, IOException, InterruptedException, DEFUSEMustBePlayedException;
}
