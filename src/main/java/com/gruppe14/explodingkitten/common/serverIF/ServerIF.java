package com.gruppe14.explodingkitten.common.serverIF;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerIF extends Remote {

	String getDatenbank() throws RemoteException;

	String getLobby() throws RemoteException;

}
