package com.gruppe14.explodingkitten.server.main;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class FakeMainGUI {

	public static void main(String[] args) throws RemoteException {
		LocateRegistry.createRegistry(1099);
		MainGUIServer.main(args);
	}
}
