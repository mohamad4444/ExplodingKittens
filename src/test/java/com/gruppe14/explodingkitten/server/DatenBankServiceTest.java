package com.gruppe14.explodingkitten.server;

import com.gruppe14.explodingkitten.client.Client;
import com.gruppe14.explodingkitten.common.Exceptions.UsernameAlreadyExistException;
import com.gruppe14.explodingkitten.common.Exceptions.UsernameDoesntExistException;
import com.gruppe14.explodingkitten.common.Exceptions.WrongPasswordException;
import com.gruppe14.explodingkitten.common.data.Account;
import com.gruppe14.explodingkitten.common.serverIF.DatenBankIF;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DatenBankServiceTest {

	static Server server;
	static Registry registry;
	static ArrayList<Client> clients=new ArrayList<>();

	@BeforeEach
	void setUP() throws Exception {
		registry = LocateRegistry.createRegistry(1099);
		server = new Server();
	}

	@AfterEach
	void tearDown() throws Exception {
		UnicastRemoteObject.unexportObject(registry, true);
	}

	@Test
	void boundTest() throws Exception {
		DatenBankIF datenbank = (DatenBankIF) Naming.lookup(server.getDatenbank());
	}

	@Test
	void testRegistrieren() throws Exception {
		Server.database.registrieren("aaa", "aaa", 21);
		Account newAccount = new Account("aaa", "aaa", 21);
		assertEquals(newAccount, Server.database.users.get("aaa"));
		assertTrue(Server.database.bestenListe.contains(newAccount));
	}

	@Test()
	void testRegistrieren2() throws Exception {
		Server.database.registrieren("aaa", "aaa", 21);
		try {
			Server.database.registrieren("aaa", "bbb", 44);
			fail();
		} catch (UsernameAlreadyExistException e) {

		}
	}

	@Test
	void testRegistrieren3() throws Exception {
		Server.database.registrieren("aaa", "aaa", 21);
		Server.database.registrieren("bbb", "aaa", 21);
	}

	@Test
	void testVerifyAccount() throws Exception {
		Server.database.registrieren("aaa", "aaa", 21);

		Account newAccount = new Account("aaa", "aaa", 21);
		assertEquals(newAccount, Server.database.verifyAccount("aaa", "aaa"));
	}

	@Test
	void testVerifyAccount1() throws Exception {
		Server.database.registrieren("aaa", "aaa", 21);
		Account newAccount = new Account("aaa", "aaa", 21);
		assertEquals(newAccount, Server.database.verifyAccount("aaa", "aaa"));
		try {
			Server.database.verifyAccount("aaa", "bbb");
			fail();
		} catch (WrongPasswordException e) {

		}
	}

	@Test
	void testVerifyAccount2() throws Exception {
		Server.database.registrieren("aaa", "aaa", 21);
		Account newAccount = new Account("aaa", "aaa", 21);
		assertEquals(newAccount, Server.database.verifyAccount("aaa", "aaa"));
		try {
			Server.database.verifyAccount("aaa4", "bbb");
			fail();
		} catch (UsernameDoesntExistException e) {

		}
	}

	@Test
	void testAnmelden() throws Exception {
		Client client = new Client();
		client.register("aaa", "aaa", 21);
		client.anmelden("aaa", "aaa");
		assertTrue(Server.clients.containsKey("aaa"));
		assertNotNull(client.zustand.account);
	}

	@Test
	void testAbmelden() throws Exception {
		Client client = new Client();
		client.register("aaa", "aaa", 21);
		client.anmelden("aaa", "aaa");
		assertTrue(Server.clients.containsKey("aaa"));
		client.abmenlden();
		assertFalse(Server.clients.containsKey("aaa"));
		assertTrue(Server.database.users.containsKey("aaa"));
	}

	@Test
	void testAccountLoschen() throws Exception {
		Client client = new Client();
		client.register("aaa", "aaa", 21);
		client.anmelden("aaa", "aaa");
		assertTrue(Server.clients.containsKey("aaa"));
		client.accountLoschen("aaa", "aaa");
		assertFalse(Server.clients.containsKey("aaa"));
		assertFalse(Server.database.users.containsKey("aaa"));
	}

	@Test
	void testAdjustAccount() throws Exception {
		Server.database = new DatenBankService();
		Server.database.registrieren("aaa", "aaa", 21);
		Client client=new Client();
		client.anmelden("aaa","aaa");
		Account newAccount = new Account("aaa", "bbb2", 33);
		Account acc = Server.database.verifyAccount("aaa", "aaa");
		Server.database.adjustAccount(acc, newAccount);
		acc = Server.database.verifyAccount("aaa", "bbb2");
		assertEquals("aaa", acc.getBenutzername());
		assertEquals("bbb2", acc.getPassword());
		assertEquals(33, acc.getAlter());
	}

	@Test
	void testAdjustAccount2() throws Exception {
		Server.database = new DatenBankService();
		Server.database.registrieren("aaa", "aaa", 21);
		Server.database.registrieren("bbb", "bbb", 21);
		Client client=new Client();
		client.anmelden("aaa","aaa");
		Account newAccount = new Account("bbb", "542", 33);
		Account acc = Server.database.verifyAccount("aaa", "aaa");
		try {
			Server.database.adjustAccount(acc, newAccount);
			fail();
		} catch (UsernameAlreadyExistException e) {

		}
		acc = Server.database.verifyAccount("bbb", "bbb");
		assertEquals("bbb", acc.getBenutzername());
		assertEquals("bbb", acc.getPassword());
		assertEquals(21, acc.getAlter());
	}

	@Test
	void testAdjustAccount3() throws Exception {
		Server.database = new DatenBankService();
		Server.database.registrieren("aaa", "aaa", 21);
		Server.database.registrieren("fadi", "fadi356", 55);
		Client client=new Client();
		client.anmelden("aaa","aaa");
		Account newAccount = new Account("bbb", "542", 33);

		Account acc = Server.database.verifyAccount("aaa", "aaa");
		Server.database.adjustAccount(acc, newAccount);

		acc = Server.database.verifyAccount("fadi", "fadi356");
		assertEquals("fadi", acc.getBenutzername());
		assertEquals("fadi356", acc.getPassword());
		assertEquals(55, acc.getAlter());
	}

	@Test
	void testUpdateClient() throws Exception {
		Client client = new Client();
		client.register("aaa", "aaa", 18);
		client.anmelden("aaa", "aaa");
		new Client().register("bbb", "bbb", 19);
		new Client().register("bbb1", "bbb", 19);
		assertEquals(Server.database.bestenListe, client.zustand.bestenliste);
		assertEquals(3, client.zustand.bestenliste.size());
	}


}
