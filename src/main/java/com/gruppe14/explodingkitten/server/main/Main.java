package com.gruppe14.explodingkitten.server.main;//package server;

import com.gruppe14.explodingkitten.server.Server;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Main {
	public static void main(String[] args) throws RemoteException, MalformedURLException, AlreadyBoundException {
		LocateRegistry.createRegistry(1099);
		Server s = new Server();
	}
}
