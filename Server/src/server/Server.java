package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable{
	
	private static final int PORT=9090;
	private ServerSocket serverSocket;
	
	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}
	
	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(PORT);
			System.out.println("Server accept");
			socketConnection();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				serverSocket.close();
				System.out.println("Server close");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void socketConnection() {
		try {
			Socket socket = serverSocket.accept();
			SocketServer socketServer = new SocketServer(socket);
			socketServer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}