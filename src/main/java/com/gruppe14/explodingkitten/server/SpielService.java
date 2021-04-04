package com.gruppe14.explodingkitten.server;

import com.gruppe14.explodingkitten.common.ClientIF;
import com.gruppe14.explodingkitten.common.Exceptions.DEFUSEMustBePlayedException;
import com.gruppe14.explodingkitten.common.Exceptions.FullRoomException;
import com.gruppe14.explodingkitten.common.Exceptions.WrongPasswordException;
import com.gruppe14.explodingkitten.common.data.Account;
import com.gruppe14.explodingkitten.common.data.Game.Card;
import com.gruppe14.explodingkitten.common.data.Game.Lock;
import com.gruppe14.explodingkitten.common.data.Game.Spieler;
import com.gruppe14.explodingkitten.common.data.Game.Spielraum;
import com.gruppe14.explodingkitten.common.data.Location;
import com.gruppe14.explodingkitten.common.data.Vorraum;
import com.gruppe14.explodingkitten.common.data.Zustand;
import com.gruppe14.explodingkitten.common.serverIF.SpielraumIF;
import javafx.util.Pair;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class SpielService extends UnicastRemoteObject implements SpielraumIF {
	/**
	 *
	 */
	private static final long serialVersionUID = -4591149201415923092L;
	public String url;
	public Vorraum vorraum;
	public Spielraum spielraum;
	Lock playcardLock = new Lock();
	Lock NopeOrPickLock = new Lock();

	/**
	 * Constructor of SpielService
	 *
	 * @param vorraum to create A vorraum
	 * @throws RemoteException       Server connection problem
	 * @throws MalformedURLException url is not formed correctly
	 * @throws AlreadyBoundException SpielService already bound
	 */
	SpielService(Vorraum vorraum) throws RemoteException, MalformedURLException, AlreadyBoundException {
		super();
		this.vorraum = vorraum;
		url = "rmi://localhost:1099/SpielRaum_" + vorraum.raumName;
		Naming.bind(url, this);
	}

	/**
	 * draws a card to client
	 *
	 * @param account that wants to draw
	 * @return card drawn
	 * @throws IOException aa
	 * @throws NotBoundException aa
	 * @throws AlreadyBoundException aa
	 */
	@Override
	public synchronized Card drawCard(Account account) throws IOException, AlreadyBoundException, NotBoundException {
		spielraum.karteZiehen(account.getBenutzername());
		for (Account acc : vorraum.spielerImVorraum.values()) {
			if (!acc.botAccount) {
				Server.updateClient(acc.getBenutzername(), this.vorraum.raumName);
			}
		}
		return null;
	}

	public void updateClient() throws IOException {
		for (Account acc : vorraum.spielerImVorraum.values()) {
			if (!acc.botAccount) {
				Server.updateClient(acc.getBenutzername(), this.vorraum.raumName);
			}
		}
	}

	/**
	 * Play a card
	 *
	 * @param card    card to play
	 * @param account account that wants to play the card
	 * @return aa
	 * @throws DEFUSEMustBePlayedException aa
	 * @throws InterruptedException        aa
	 * @throws IOException                 aa
	 */
	@Override
	public boolean playCard(Card card, Account account, Spieler attackedPlayer, int cardPosition)
			throws InterruptedException, DEFUSEMustBePlayedException, IOException {
		synchronized (playcardLock) {
			if (card == Card.DEFUSE) {
				spielraum.karteAsusspielen(account.getBenutzername(), card, null, cardPosition);
				updateClient();
			} else if (attackedPlayer != null) {
				Spieler attackedSpieler = this.spielraum.contains(attackedPlayer.getName());
				System.out.println(attackedSpieler.getName());
				// starts here till wait method is called
				Thread t = new Thread() {
					public void run() {
						try {
							spielraum.karteAsusspielen(account.getBenutzername(), card, attackedSpieler, cardPosition);
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (DEFUSEMustBePlayedException e) {
							e.printStackTrace();
						}
					}
				};
				t.start();
				while (spielraum.state != Spielraum.State.WaitingNope) {
					Thread.sleep(50);
				}
				updateClient();

				new Thread() {
					public void run() {
						try {
							spielraum.waitTimeout(2000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}.start();
				// updates client information with who must pick a card
				if (card == Card.FAVOR) {
					while (spielraum.state != Spielraum.State.PickingCard) {
						Thread.sleep(50);
					}
					updateClient();
					new Thread() {
						public void run() {
							try {
								spielraum.waitTimeout(2000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}.start();
				}
				while (spielraum.state != Spielraum.State.DrawingCard) {
					Thread.sleep(50);
				}
				updateClient();
			} else {
				System.out.println("playing a normal card from SpielService");
				// starts here till wait method is called
				Thread t = new Thread() {
					public void run() {
						try {
							spielraum.karteAsusspielen(account.getBenutzername(), card, null, cardPosition);
							System.out.println("finished KarteAusspielen");
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (DEFUSEMustBePlayedException e) {
							e.printStackTrace();
						}
					}
				};
				t.start();
				while (spielraum.state != Spielraum.State.WaitingNope) {
					Thread.sleep(50);
				}
				updateClient();
				System.out.println("Updating client, state is waitingNope");

				new Thread() {
					public void run() {
						try {
							spielraum.waitTimeout(2000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}.start();
				// updates client information with who must pick a card

				while (spielraum.state != Spielraum.State.DrawingCard) {
					Thread.sleep(50);
				}
				updateClient();

			}
			return spielraum.cardActivated;
		}
	}

	@Override
	public void pickCard(Card card, Account account, Spieler attackedPlayer, int cardPosition)
			throws IOException, InterruptedException, DEFUSEMustBePlayedException {
		synchronized (this.NopeOrPickLock) {
			this.spielraum.pickCard(account.getBenutzername(), card);
			this.updateClient();
		}
	}

	@Override
	public synchronized void playNope(Card card, Account account)
			throws IOException, InterruptedException, DEFUSEMustBePlayedException {
		synchronized (this.NopeOrPickLock) {
			this.spielraum.playNope(account.getBenutzername());
			this.updateClient();
		}
	}

	/**
	 * Creates the game room and moves all players from game room to spielraum*
	 *
	 * @param host that wants to create the room
	 * @return false
	 * @throws IOException           gui update error
	 * @throws AlreadyBoundException gameroom is already bound
	 * @throws NotBoundException     gui
	 */
	@Override
	public boolean spielStarten(Account host) throws IOException, AlreadyBoundException, NotBoundException {
		this.spielraum = vorraum.createSpielraum();
		this.spielraum.vorraum = vorraum;
		for (Account acc : vorraum.spielerImVorraum.values()) {
			if (!acc.botAccount) {
				Server.updateClient(acc.getBenutzername(), this.vorraum.raumName);
				Server.clients.get(acc.getBenutzername()).getKey().changeGUI(Location.Spielraum);
			}
		}
		Server.updateClient("all", null);
		return false;
	}

	/**
	 * Adds an easy bot to vorraum
	 *
	 * @param host that wants to add bot
	 * @throws WrongPasswordException wont happen
	 * @throws FullRoomException      room is full
	 * @throws IOException            gui update error
	 */
	@Override
	public void botHinzufugenE(Account host) throws WrongPasswordException, FullRoomException, IOException {
		this.vorraum.addEasyBot(host);
		for (Account acc : vorraum.spielerImVorraum.values()) {
			if (!acc.botAccount) {
				Server.updateClient(acc.getBenutzername(), this.vorraum.raumName);
			}
		}
		Server.updateClient("all", null);
	}

	/**
	 * Adds a hard bot to vorraum
	 *
	 * @param host that wants to add bot
	 * @throws WrongPasswordException wont happen
	 * @throws FullRoomException      room is full
	 * @throws IOException            gui update error
	 */
	@Override
	public void botHinzufugenS(Account host) throws WrongPasswordException, FullRoomException, IOException {
		this.vorraum.addHardBot(host);
		for (Account acc : vorraum.spielerImVorraum.values()) {
			if (!acc.botAccount) {
				Server.updateClient(acc.getBenutzername(), this.vorraum.raumName);
			}
		}
		Server.updateClient("all", null);
	}

	/**
	 * Removes Player from Vorraum and if host unbinds it and removes all
	 *
	 * @param acc Account to remove from game
	 * @throws IOException           gui update error
	 * @throws NotBoundException     Vorraum not bound
	 * @throws AlreadyBoundException Url already bound
	 */
	@Override
	public void raumVerlassen(Account acc) throws IOException, NotBoundException, AlreadyBoundException {
		if (acc.getBenutzername().equals(vorraum.host.getBenutzername())) {
			// forceAllToLeave();
			// Server.spielRaumServices.remove(this.vorraum.raumName);
			// Server.lobby.vorraumList.remove(this.vorraum);
			// Naming.unbind(this.url);
			this.vorraum.spielerLoeschen(acc);
			if (this.spielraum != null)
				this.spielraum.spielerEntfernen(acc.getBenutzername());
			Server.updateClient(acc.getBenutzername(), this.vorraum.raumName);
		} else {
			this.vorraum.spielerLoeschen(acc);
			if (this.spielraum != null)
				this.spielraum.spielerEntfernen(acc.getBenutzername());
			Server.updateClient(acc.getBenutzername(), this.vorraum.raumName);
		}
		Server.updateClient("all", null);
	}

	/**
	 * adds chattext to Vorraum
	 *
	 * @param text aa
	 * @param acc  aa
	 */
	@Override
	public void sendChatVor(String text, Account acc) throws IOException {
		if (this.vorraum.chats.size() >= 100) {
			this.vorraum.chats.removeFirst();
		}
		this.vorraum.chats.add(acc.getBenutzername() + ":" + text + "\n");
		for (Account ac : vorraum.spielerImVorraum.values()) {
			if (!ac.botAccount) {
				Server.updateClient(ac.getBenutzername(), this.vorraum.raumName);
			}
		}
	}

	/**
	 * deletes vorraum and moves all players to lobby
	 *
	 * @throws IOException           gui update error
	 * @throws AlreadyBoundException gui error
	 * @throws NotBoundException     gui error
	 */
	public void forceAllToLeave() throws IOException, AlreadyBoundException, NotBoundException {
		for (Account ac : vorraum.spielerImVorraum.values()) {
			if (!ac.botAccount) {
				Pair<ClientIF, Zustand> clientPair = Server.clients.get(ac.getBenutzername());
				if (clientPair != null) {
					ClientIF client = clientPair.getKey();
					client.changeGUI(Location.Lobby);
				}
			}
		}
		Server.updateClient("all", null);
	}

	/**
	 * adds chattext to Spielraum
	 *
	 * @param text aa
	 * @param acc  aa
	 */
	@Override
	public void sendChatSp(String text, Account acc) throws IOException {
		if (this.spielraum.chats.size() >= 100) {
			this.spielraum.chats.removeFirst();
		}
		this.spielraum.chats.add(acc.getBenutzername() + ":" + text + "\n");
		for (Account ac : vorraum.spielerImVorraum.values()) {
			if (!ac.botAccount) {
				Server.updateClient(ac.getBenutzername(), this.vorraum.raumName);
			}
		}
	}

}
