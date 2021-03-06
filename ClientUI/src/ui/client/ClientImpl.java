package ui.client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import rmi.common.Chat;
import rmi.common.Server;

public class ClientImpl extends UnicastRemoteObject implements Chat {

	private String name;
	private Server server;
	private static final long serialVersionUID = 1L;
	private ChatUI chat;

	public ClientImpl(ChatUI chat) throws RemoteException, MalformedURLException, NotBoundException {
		super();
		this.chat = chat;
		server = (Server) Naming.lookup("rmi://127.0.0.1/" + Server.DEFAULT_NAME);
	}

	public boolean isNicknameOkay(String name) throws RemoteException {
		if(server.logIn(name)) {
			server.addUser(name, this);
			this.name = name;
			return true;
		}
		return false;
	}

	@Override
	public void sendUserList(String[] userList) throws RemoteException {
		chat.newUserList(userList);
	}

	@Override
	public void receiveMessage(String message) {
		chat.sendMessage(message);
	}

	public void sendMessage(String message) {
		new Thread(() -> this.send(message)).start();
	}

	private void send(String message) {
		try {
			this.server.sendMessage(this.name, message);
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void logout() {
		try {
			this.server.logOut(this.name);
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}