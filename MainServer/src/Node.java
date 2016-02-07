import java.net.*;

import net.rudp.*;


public class Node {
	String id = "";
	String private_ia = null;
	String public_ia = null;
	int private_portNumber = 0;
	int public_portNumber = 0;
	String time = "";
    ReliableSocket prvSocket = null;
    ReliableSocket pubSocket =  null;
    ReliableSocket SocketKing = null;
	
	public Node(){
	}
	
	public Node(String id, String private_ia, int private_portNumber, String public_ia, int public_portNumber, String time){
		this.id = id;
		this.private_ia = private_ia;
		this.private_portNumber = private_portNumber;
		this.public_ia = public_ia;
		this.public_portNumber = public_portNumber;
		this.time = time;
	}
	
	public String getPrivateInfo(){
		return "<pv>#"+private_ia + "#~" + String.valueOf(private_portNumber)+"~<pv>";
	}
	public String getPublicInfo(){
		return "<pb>#"+public_ia + "#~" + String.valueOf(public_portNumber)+"~<pb>";
	}
	
	public void setPublicInfo(String public_ia, int public_portNumber){
		this.public_ia = public_ia;
		this.public_portNumber = public_portNumber;
	}

}
