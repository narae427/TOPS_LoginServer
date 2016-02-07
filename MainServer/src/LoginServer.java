import java.io.*;
import java.net.*;
import java.util.concurrent.*;

import net.rudp.*;

public class LoginServer implements Runnable {
	ReliableServerSocket serverSocket = null;
	static ReliableSocket clientSocket = null;
	final ExecutorService pool = Executors.newFixedThreadPool(100);

	Thread serverThread = null;

	volatile boolean started = false;
	volatile boolean stopped = false;

	static Manage_MESSAGE MM = null;

	static String Node_publicIP = null;
	static String Node_privateIP = null;
	static int Node_publicPN = 0;
	static int Node_privatePN = 0;
	static int MS_ReceivePortNumber = 8089;
	static int MS_SendPortNumber = 8989;

	public LoginServer(int port) throws Exception {
		serverSocket = new ReliableServerSocket(port);

		serverThread = new Thread(this);

		clientSocket = new ReliableSocket();

		System.out.println("START");
	}

	public void start() {
		serverThread.start();
	}

	public void stop() {
		stopped = true;
	}

	public void run() {
		try {
			started = true;
			while (!stopped) {
				Socket connectionSocket = serverSocket.accept();
				pool.execute(new Request(connectionSocket));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static class Request implements Runnable {

		final Socket socket;

		public Request(Socket socket) {
			this.socket = socket;
		}

		public void run() {

			try {
				System.out.println("Processing request from <"
						+ socket.getInetAddress() + " " + socket.getPort()
						+ ">");
				InputStreamReader inputStream = new InputStreamReader(
						socket.getInputStream());

				BufferedReader buffReader = new BufferedReader(inputStream);

				while (true) {
					String line = buffReader.readLine();

					if (line != null) {
						System.out.println("ECHO: " + line);

						Node_publicIP = socket.getInetAddress()
								.getHostAddress();
						Node_publicPN = socket.getPort();

						String message = new String(line).trim();

						System.out.println("NODE IP : "
								+ Node_publicIP.toString() + "  PN : "
								+ Node_publicPN);
						System.out.println("message : " + message);

						// if(message.contains("MS_Unconnection")) break;
						// /////////////////////////////////////////////////////////////////////

						Node node = new Node();
						node.setPublicInfo(Node_publicIP, Node_publicPN);
						MM = new Manage_MESSAGE(socket, node);
						MM.readMESSAGE(message);

					}
					// System.out.println("Request from <"+socket.getInetAddress().getHostAddress()+"> finished.");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws Exception {

		LoginServer LS = new LoginServer(MS_ReceivePortNumber);
		LS.start();
		Thread.sleep(1000);
	}
}
