import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import net.rudp.*;

public class NodeThread extends Thread {
	String HomeDir = System.getProperty("user.home");
	String serverFolderPath = HomeDir + System.getProperty("file.separator")
			+ "TOPS_SERVER";
	String commandMessage = "";
	String myID = "";
	String freindID = "";
	String Node_publicIP = null;
	int Node_publicPN = -1;
	int MS_ReceivePortNumber = LoginServer.MS_ReceivePortNumber;
	Node node = null;
	Socket socket = null;

	public NodeThread(Node newNode, String commandMessage) throws IOException {
		this.node = newNode;
		this.commandMessage = commandMessage;
	}

	public NodeThread(String freindIDs, String commandMessage)
			throws IOException {
		this.freindID = freindIDs;
		this.commandMessage = commandMessage;
	}

	public NodeThread(Node newNode, String freindIDs, String commandMessage)
			throws IOException {
		this.node = newNode;
		this.freindID = freindIDs;
		this.commandMessage = commandMessage;
	}

	public NodeThread(String myID, String freindID, String commandMessage)
			throws IOException {
		this.myID = myID;
		this.freindID = freindID;
		this.commandMessage = commandMessage;

	}

	public void setSocket(Socket socket) {
		this.socket = socket;
		return;
	}

	public void setNode(Node node) {
		this.node = node;
		return;
	}

	public void setPublicInfo(String ip, int pn) {
		this.Node_publicIP = ip;
		this.Node_publicPN = pn;
		return;
	}

	public static void sendMSG_king(Node node, String message) {
		try {

			ReliableSocketOutputStream outputStream = (ReliableSocketOutputStream) node.SocketKing
					.getOutputStream();
			PrintWriter outputBuffer = new PrintWriter(outputStream);

			outputBuffer.println(message);
			outputBuffer.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendMSG_public(Node node, String message) {
		try {
			ReliableSocketOutputStream outputStream = (ReliableSocketOutputStream) node.pubSocket
					.getOutputStream();
			PrintWriter outputBuffer = new PrintWriter(outputStream);

			outputBuffer.println(message);
			outputBuffer.flush();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void run() {
		switch (commandMessage) {
		case "MS_Done":
			break;
		case "MS_Connection":
			try {
				sendAlertMSG(node, commandMessage);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				confirmConnectionMSG(node, commandMessage);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case "MS_Unconnection":
			try {
				sendAlertMSG(node, commandMessage);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case "MS_CheckOnlineFreind":
			try {
				checkNode();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case "MS_CheckOnlineFreinds":
			try {
				checkNodes();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case "MS_RequestAddFreind":
			try {
				requestaddFreind(myID, freindID);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		case "MS_AllowAddFreind":
			try {
				allowaddFreind(myID, freindID);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}

	}

	public void confirmConnectionMSG(Node newNode, String commandMessage)
			throws Exception {
		if (commandMessage.equals("MS_Connection")) {
			System.out.println("Confirm + "
					+ socket.getInetAddress().getHostAddress() + " "
					+ socket.getPort());
			String confirmMSG = "'MS_ConfirmConnection'"
					+ node.getPrivateInfo() + node.getPublicInfo();

			System.out.println("comfirmMSG : " +confirmMSG );
			try {
				node.prvSocket = new ReliableSocket();
				node.prvSocket.connect(new InetSocketAddress(node.private_ia,
						node.private_portNumber), 10000);

			} catch (Exception e) {
				System.out.println("???????????????????????????????");
			}

			node.pubSocket = (ReliableSocket) socket;

			if (node.prvSocket.isConnected()) {
				node.SocketKing = node.prvSocket;
				System.out.println("Private ");
			} else {
				node.SocketKing = node.pubSocket;
				System.out.println("Public ");
			}

			sendMSG_king(node, confirmMSG);

		}
	}

	public void sendAlertMSG(Node newNode, String commandMessage)
			throws Exception {
		String freindInfo = "";
		if (commandMessage.equals("MS_Connection")) {
			freindInfo = "'MS_NoticeOnlineFreind'" + "@" + newNode.id + "@"
					+ newNode.getPrivateInfo() + newNode.getPublicInfo();
		} else if (commandMessage.equals("MS_Unconnection")) {
			freindInfo = "'MS_NoticeOfflineFreind'" + "@" + newNode.id + "@";
		}
		StringTokenizer st = new StringTokenizer(freindID, ";");
		String freindId = "";

		while (st.hasMoreTokens()) {
			boolean online = false;
			freindId = st.nextToken();
			if (Manage_MESSAGE.nodeList.containsKey(freindId)) {
				System.out.println("NOTICE " + freindId);
				sendMSG_king(Manage_MESSAGE.nodeList.get(freindId), freindInfo);
			}

		}
		// send_ds.close();
	}

	public void checkNode() throws Exception {
		boolean online;
		String freindInfo = "";

		String freindInfos = "'MS_ResultOfCheckOnlineFreind'" + "@";
		online = false;

		Iterator it = Manage_MESSAGE.nodeList.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			if (key.equals(freindID)) {
				freindInfo = Manage_MESSAGE.nodeList.get(key).getPrivateInfo()
						+ Manage_MESSAGE.nodeList.get(key).getPublicInfo();
				freindInfos += (freindID + "@" + freindInfo);
				online = true;
				break;
			}

		}
		if (online == false) {
			freindInfo = "OFFLINE";
			freindInfos += (freindID + "@ " + freindInfo);
		}
		sendMSG_king(node, freindInfos);

	}

	public void checkNodes() throws Exception {
		try {
			boolean online;
			String freindInfo = "";

			String freindId = "";
			String freindInfos = "'MS_ResultOfCheckOnlineFreinds'";
			StringTokenizer st = new StringTokenizer(freindID, ";");
			while (st.hasMoreTokens()) {
				online = false;
				freindId = st.nextToken();
				Iterator it = Manage_MESSAGE.nodeList.keySet().iterator();
				while (it.hasNext()) {
					String key = (String) it.next();
					if (key.equals(freindId)) {
						freindInfo = Manage_MESSAGE.nodeList.get(key)
								.getPrivateInfo()
								+ Manage_MESSAGE.nodeList.get(key)
										.getPublicInfo();
						freindInfos += (";" + "@" + freindId + "@" + freindInfo + ";");
						online = true;
						break;
					}

				}
				if (online == false) {
					freindInfo = "OFFLINE";
					freindInfos += (";" + "@" + freindId + "@" + freindInfo + ";");
				}

			}

			sendMSG_king(node, freindInfos);

			if (Manage_MESSAGE.waitingListForRequestAddFreind
					.containsKey(node.id)) {
				ArrayList<String> idArr = Manage_MESSAGE.waitingListForRequestAddFreind
						.get(node.id);

				for (String id : idArr) {
					requestaddFreind(id, node.id);

					Manage_MESSAGE.waitingListForRequestAddFreind
							.remove(node.id);
				}
			}

			if (Manage_MESSAGE.waitingListForAllowAddFreind
					.containsKey(node.id)) {
				ArrayList<String> idArr = Manage_MESSAGE.waitingListForAllowAddFreind
						.get(node.id);

				for (String id : idArr) {
					requestaddFreind(id, node.id);

					Manage_MESSAGE.waitingListForAllowAddFreind.remove(node.id);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void requestaddFreind(String myId, String freindId)
			throws IOException {
		try {
			String addFreindStr = "'MS_RequestAddFreind'" + "!" + myId + "!";

			Node freindNode = Manage_MESSAGE.nodeList.get(freindId);

			System.out
					.println(" *** Request add Freind : "
							+ freindNode.public_ia + " "
							+ freindNode.public_portNumber);
			System.out.println(" *** Request add Freind : "
					+ freindNode.private_ia + " "
					+ freindNode.private_portNumber);

			sendMSG_king(freindNode, addFreindStr);
		} catch (Exception e) {
			ArrayList<String> idArr = null;
			if (Manage_MESSAGE.waitingListForRequestAddFreind.get(freindId) == null) {
				idArr = new ArrayList<String>();
			} else {
				idArr = Manage_MESSAGE.waitingListForRequestAddFreind
						.get(freindId);
			}

			idArr.add(myId);
			Manage_MESSAGE.waitingListForRequestAddFreind.put(freindId, idArr);
		}
	}

	public void allowaddFreind(String myId, String freindId) throws IOException {
		try {

			String addFreindStr = "'MS_AllowAddFreind'" + "!" + myId + "!";

			Node freindNode = Manage_MESSAGE.nodeList.get(freindId);

			System.out
					.println(" *** Allow add Freind : " + freindNode.public_ia
							+ " " + freindNode.public_portNumber);
			System.out.println(" *** Allow add Freind : "
					+ freindNode.private_ia + " "
					+ freindNode.private_portNumber);

			sendMSG_king(freindNode, addFreindStr);


			String freindInfo = "'MS_NoticeOnlineFreind'" + "@" + freindNode.id
					+ "@" + freindNode.getPrivateInfo()
					+ freindNode.getPublicInfo();
			Node myNode = Manage_MESSAGE.nodeList.get(myId);

			addFreindStr = "'MS_AllowAddFreind'" + "!" + freindId + "!";

			sendMSG_king(myNode, addFreindStr);

		} catch (Exception e) {
			ArrayList<String> idArr = null;
			if (Manage_MESSAGE.waitingListForAllowAddFreind.get(freindId) == null) {
				idArr = new ArrayList<String>();
			} else {
				idArr = Manage_MESSAGE.waitingListForAllowAddFreind
						.get(freindId);
			}

			idArr.add(myId);
			Manage_MESSAGE.waitingListForAllowAddFreind.put(freindId, idArr);

		}

	}
}
