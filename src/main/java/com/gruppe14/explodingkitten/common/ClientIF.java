package com.gruppe14.explodingkitten.common;

import com.gruppe14.explodingkitten.common.data.Location;
import com.gruppe14.explodingkitten.common.data.Zustand;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;

public interface ClientIF extends Remote {

	void updateZustand(Zustand zustand) throws IOException;
	void changeGUI(Location location) throws IOException, AlreadyBoundException, NotBoundException;

}
