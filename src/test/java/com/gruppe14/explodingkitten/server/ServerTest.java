package com.gruppe14.explodingkitten.server;

import com.gruppe14.explodingkitten.common.serverIF.DatenBankIF;
import com.gruppe14.explodingkitten.common.serverIF.LobbyIF;
import com.gruppe14.explodingkitten.common.serverIF.ServerIF;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(OrderAnnotation.class)
class ServerTest {
	static Server server;
	static Registry registry;

	@BeforeAll
	static void setupClass() throws RemoteException {
		registry = LocateRegistry.createRegistry(1099);
	}

	@AfterAll
	static void stopClass() throws RemoteException {
	    UnicastRemoteObject.unexportObject(registry, true);
	}

	@AfterEach()
	void destroy() throws RemoteException, MalformedURLException, NotBoundException {
		server.unbindAll();
	}

	@Test
	void testServer() throws RemoteException, MalformedURLException, AlreadyBoundException {
		server = new Server();
	}
	
	@Test
	void InterfacesBoundTest() throws MalformedURLException, RemoteException, NotBoundException, AlreadyBoundException {
		server = new Server();
		ServerIF serverif = (ServerIF) Naming.lookup(Server.url);
	}

	@Test
	void OpeningRegistryTwiceTest()
			throws RemoteException, MalformedURLException, AlreadyBoundException, NotBoundException {
		server = new Server();
		Exception exception = assertThrows(AlreadyBoundException.class, () -> {
			new Server();
		});

	}

	@Test
	void testStartServices() throws RemoteException, MalformedURLException, AlreadyBoundException {
		server = new Server();
	}

	@Test
	void testShutDown() throws RemoteException, MalformedURLException, AlreadyBoundException, NotBoundException {
		server = new Server();
		server.unbindAll();
		server = new Server();
	}

	@Test
	void testGetDatenbank() throws MalformedURLException, RemoteException, NotBoundException, AlreadyBoundException {
		server = new Server();
		DatenBankIF databank = (DatenBankIF) Naming.lookup(server.getDatenbank());
	}

	@Test
	void testGetLobby() throws MalformedURLException, RemoteException, NotBoundException, AlreadyBoundException {
		server = new Server();
		LobbyIF lobby = (LobbyIF) Naming.lookup(server.getLobby());
	}
}
