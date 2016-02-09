import java.io.*;
import java.net.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.rudp.*;

public class Manage_MESSAGE extends Thread{
	static HashMap<String, Node> nodeList = new HashMap();

	String printMsg = "";
	String printID = "";
	String connectTime = "";
	static HashMap<String, ArrayList<String>> waitingListForRequestAddFreind = new HashMap<>();
	static HashMap<String, ArrayList<String>> waitingListForAllowAddFreind = new HashMap<>();
	ArrayList<NodeThread> nodeThreadArr = new ArrayList<NodeThread>();
	
	Pattern commandPattern = Pattern.compile("'.*'");
	Pattern idPattern = Pattern.compile("!.*!");
	Pattern ipPattern = Pattern.compile("#.*#");
	Pattern pnPattern = Pattern.compile("~.*~");
	Pattern fidPattern = Pattern.compile("@.*@");
	Pattern fnamePattern = Pattern.compile(":.*:");
	Pattern modPattern = Pattern.compile("-.*-");
	Pattern expPattern = Pattern.compile("=.*=");

	Socket socket = null;
	Node socketNode = null;
	public Manage_MESSAGE(Socket socket, Node node ){
		this.socket = socket;
		this.socketNode = node;
	}
	
	public String getMSG() {
		return "[SERVER: RECEIVE MESSAGE] " + printMsg + " from " + printID
				+ " at " + connectTime;
	}

	public String getTime() {
		long time = System.currentTimeMillis();
		SimpleDateFormat dayTime = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String strTime = dayTime.format(new Date(time));
		return strTime;
	}
	
	public String getPatternfromMSG(String message, Pattern p) {
		Pattern pattern = p;
		Matcher m = pattern.matcher(message);

		boolean find = false;

		String rstr = null;

		while (m.find()) {
			rstr = m.group();
			find = true;
		}

		if (find)
			rstr = rstr.substring(1, rstr.length() - 1);
		if(rstr == null)
			return "-1";
		else
			return rstr;
	}

	public void readMESSAGE(String message) throws Exception {
			String commandMessage = getPatternfromMSG(message, commandPattern);
			System.out.println("command : " + commandMessage);
			
			NodeThread NT = null;
			
			switch (commandMessage) {
			case "MS_Done":
				String idMessage =  getPatternfromMSG(message, idPattern);
				String fidMessage = getPatternfromMSG(message, fidPattern);
				
				printID = idMessage;
				printMsg = commandMessage;
				connectTime = getTime();
				System.out.println(getMSG());

				Node node = nodeList.get(idMessage);
				NT = new NodeThread(node, fidMessage, commandMessage);
				NT.setSocket(socket);
				
				NT.start();
				NT.join();

				break;

			case "MS_Connection":
				 idMessage =  getPatternfromMSG(message, idPattern);
				 String ipMessage =   getPatternfromMSG(message, ipPattern);
				 fidMessage = getPatternfromMSG(message, fidPattern);
				 LoginServer.Node_privateIP = ipMessage;
				 String pnMessage = getPatternfromMSG(message, pnPattern);
				 LoginServer. Node_privatePN =  Integer.valueOf(pnMessage);
				 
				 System.out.println("fid Message : " + fidMessage);
				 
				printID = idMessage;
				printMsg = commandMessage;
				connectTime = getTime();
				System.out.println(getMSG());
				Node newnode = new Node(idMessage,LoginServer.Node_publicIP , LoginServer.Node_privatePN, LoginServer.Node_privateIP,LoginServer.Node_publicPN,connectTime);
				nodeList.put(idMessage, newnode);

				NT = new NodeThread(newnode,fidMessage,  commandMessage);
				NT.setSocket(socket);
				NT.setPublicInfo(newnode.public_ia, newnode.public_portNumber);
				
				
				NT.start();
				NT.join();
				
				break;

			case "MS_Unconnection":
				idMessage =  getPatternfromMSG(message, idPattern);
				fidMessage = getPatternfromMSG(message, fidPattern);
				
				printID = idMessage;
				printMsg = commandMessage;
				connectTime = getTime();
				System.out.println(getMSG());

				Node oldnode = nodeList.get(idMessage);
				nodeList.remove(idMessage);
				System.out.println("NodeList " + nodeList + " key : " + nodeList.containsKey(idMessage));
				NT = new NodeThread(oldnode, fidMessage,
						commandMessage);
				NT.setSocket(socket);
				NT.setPublicInfo(socketNode.public_ia, socketNode.public_portNumber);
				
				NT.start();
				NT.join();

				break;

			case "MS_CheckOnlineFreind":
				idMessage =  getPatternfromMSG(message, idPattern);
				fidMessage = getPatternfromMSG(message, fidPattern);

				printID = idMessage;
				printMsg = commandMessage;
				connectTime = getTime();
				System.out.println(getMSG());

				NT = new NodeThread(fidMessage, commandMessage);
				NT.setSocket(socket);
				
				node = nodeList.get(idMessage);
				NT.setNode(node);
				
				NT.setPublicInfo(socketNode.public_ia, socketNode.public_portNumber);
				
				NT.start();
				NT.join();

				break;

			case "MS_CheckOnlineFreinds":
				idMessage =  getPatternfromMSG(message, idPattern);
				fidMessage = getPatternfromMSG(message, fidPattern);
				
				printID = idMessage;
				printMsg = commandMessage;
				connectTime = getTime();
				System.out.println(getMSG());

				NT = new NodeThread(idMessage, fidMessage,
						commandMessage);
				NT.setSocket(socket);	

				node = nodeList.get(idMessage);
				NT.setNode(node);
				
				NT.setPublicInfo(socketNode.public_ia, socketNode.public_portNumber);
				
				NT.start();
				NT.join();

				break;

			case "MS_RequestAddFreind":
				idMessage =  getPatternfromMSG(message, idPattern);
				fidMessage = getPatternfromMSG(message, fidPattern);

				printID = idMessage;
				printMsg = commandMessage;
				System.out.println(getMSG());

				
				
				NT = new NodeThread(idMessage, fidMessage,
						commandMessage);
				NT.setSocket(socket);
				NT.setPublicInfo(socketNode.public_ia, socketNode.public_portNumber);
				
				
				NT.start();
				NT.join();

				break;

			case "MS_AllowAddFreind":
				idMessage =  getPatternfromMSG(message, idPattern);
				fidMessage = getPatternfromMSG(message, fidPattern);

				printID = idMessage;
				printMsg = commandMessage;
				System.out.println(getMSG());

				NT = new NodeThread(idMessage, fidMessage,
						commandMessage);
				NT.setSocket(socket);
				NT.setPublicInfo(socketNode.public_ia, socketNode.public_portNumber);
				
				NT.start();
				NT.join();

				break;
			}
		}
}
