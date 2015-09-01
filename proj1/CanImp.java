import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Scanner;

public class CanImp extends UnicastRemoteObject implements CANInterface {

	private static int count = 0;

	private int splitCounter = 0;
	private Coordinate coordinate = new Coordinate(0, 0, 0, 0);

	private String identifier;
	private String ip;
	private ArrayList<Neighbor> neighbors = new ArrayList<Neighbor>();
	private Hashtable<String, Integer> hashTable = new Hashtable<String, Integer>();
	DataInputStream input;
	DataOutputStream output;

	ServerSocket serverSocket;

	protected CanImp(String ip) throws RemoteException {
		this.ip = ip;
		try {
			Registry registry = LocateRegistry.getRegistry(Constants.port);
			//System.out.println("registry located.");
			registry.rebind(this.ip, (CANInterface) this);
			//System.out.println("new peer " + this.ip + " registered at "+ this.ip);
		} catch (RemoteException ex) {
			System.out.println("Exception in Regisry.");
			System.out.println(ex.toString());
		}
		/*
		try {
			// System.out.println(can.getIdentifier()+" is listening for connection..");
			//serverSocket = new ServerSocket(Constants.socketPort);

			// System.out.println(can.getIdentifier()+" is listening for connection part 2");

		} catch (Exception ex) {
			System.out.println("Exception in Server socket.");

			ex.printStackTrace();
		}

		 */
	}

	public void setSplitCounter(int splitCounter) {
		this.splitCounter = splitCounter;
	}

	public int getSplitCounter() {
		return splitCounter;
	}

	public void increaseSplitCounter() {
		this.splitCounter++;
	}

	private int xHashCode(String keyword) {
		int index = 1;
		int sum = 0;
		while (index < keyword.length()) {
			Character c = keyword.charAt(index);
			sum = sum + c.hashCode();
			index += 2;
		}
		return sum % 10;
	}

	public boolean contains(String key) {
		return this.hashTable.containsKey(key);
	}

	public void showMessage(String message) {
		System.out.println(message);
	}

	private int yHashCode(String keyword) {
		int index = 0;
		int sum = 0;
		while (index < keyword.length()) {
			Character c = keyword.charAt(index);
			sum = sum + c.hashCode();
			index += 2;
		}
		return sum % 10; // for the moment
	}

	public void setIdentifier(String id) {
		this.identifier = id;
	}

	public String getIdentifier() {
		return this.identifier;

	}

	public void setCoordinate(double xStart, double xEnd, double yStart,
			double yEnd) {
		coordinate.xStart = xStart;
		coordinate.xEnd = xEnd;
		coordinate.yStart = yStart;
		coordinate.yEnd = yEnd;

	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

	public void setIP(String ip) {
		this.ip = ip;
	}

	public String getIP() {
		return ip;
	}

	public void setXStart(double xS) throws RemoteException {
		this.coordinate.xStart = xS;
	}

	public void setXEnd(double xE) throws RemoteException {
		this.coordinate.xEnd = xE;
	}

	public void setYStart(double yS) throws RemoteException {
		this.coordinate.yStart = yS;
	}

	public void setYEnd(double yE) throws RemoteException {
		this.coordinate.yEnd = yE;
	}

	public void addToHashtable(String key, int value) throws RemoteException {
		this.hashTable.put(key, value);
	}

	public double getXStart() throws RemoteException {
		return this.coordinate.xStart;
	}

	public double getXEnd() throws RemoteException {
		return this.coordinate.xEnd;
	}

	public double getYStart() throws RemoteException {
		return this.coordinate.yStart;
	}

	public double getYEnd() throws RemoteException {
		return this.coordinate.yEnd;
	}

	public void addAllNeighbors(ArrayList<Neighbor> neighborsList)
			throws RemoteException {
		this.neighbors.addAll(neighborsList);
	}

	public void updateNeighbors() throws RemoteException{
		Iterator<Neighbor> iterator =this.neighbors.iterator();
		while(iterator.hasNext()){
			Neighbor neighbor=iterator.next();
			if(this.getXStart()>=neighbor.getXStart() && this.getXEnd()<=neighbor.getXEnd()){
				if((this.getYStart()<=neighbor.getYEnd()+0.01)||(this.getYEnd()>=neighbor.getYStart()+0.01)){
					neighbor=iterator.next();
				}

			}else if(this.getYStart()>=neighbor.getYStart() && this.getYEnd()<=neighbor.getYEnd()){
				if((this.getXStart()<=neighbor.getXEnd()+0.01)||(this.getXEnd()>=neighbor.getXStart()+0.01)){
					neighbor=iterator.next();
				}
			}else{
				iterator.remove();
			}
		}
	}

	public void addNeighbor(Neighbor neighbor) throws RemoteException {
		this.neighbors.add(neighbor);
	}

	public void setHashTable(CANInterface can) throws RemoteException {

		//System.out.println("Inside the splitting method..");

		double xCoorlength = this.coordinate.xEnd - this.coordinate.xStart;
		double yCoorlength = this.coordinate.yEnd - this.coordinate.yStart;

		// 2 scenarios. if coordinate is square(x=y) or rectangle (x<y)
		if (splitCounter % 2 == 0) {
			//System.out.println("my zone is a square");
			can.setXStart(this.coordinate.xStart + (xCoorlength / 2));
			can.setXEnd(this.coordinate.xEnd);

			this.coordinate.xEnd = (this.coordinate.xStart + (xCoorlength / 2)) - 0.01;

			can.setYStart(this.coordinate.yStart);
			can.setYEnd(this.coordinate.yEnd);

		} else if (splitCounter % 2 == 1) {
			//System.out.println("my zone is a rectangle");

			can.setYStart(this.coordinate.yStart + (yCoorlength / 2));
			can.setYEnd(this.coordinate.yEnd);

			this.coordinate.yEnd = (this.coordinate.yStart + (yCoorlength / 2)) - 0.01;

			can.setXStart(this.coordinate.xStart);
			can.setXEnd(this.coordinate.xEnd);

		} else {
			System.out.println("Oh.. this is impossible to happen while splitting!");
		}

		Enumeration<String> keys = this.hashTable.keys();

		// copying half of the pairs from peer1 to peer2

		for (int i = 1; i <= this.hashTable.size(); i++) {

			String key = keys.nextElement().toString();
			int x = xHashCode(key);
			int y = yHashCode(key);
			if (x >= can.getXStart() && x <= can.getXEnd()
					&& y >= can.getYStart() && y <= can.getYEnd()) {
				can.addToHashtable(key, hashTable.get(key));
				this.hashTable.remove(key);
				//System.out.println(key + " has been copied from "+ this.identifier + " to " + can.getIdentifier());
			}

		}

		// updating the neighbors list
		// this is NOT this simple. more through algorithm is needed here - page
		// 163 (note the periodic refreshing)

		//	System.out.println("doing can.addAllNeighbors(this.neighbors); ...");
		can.addAllNeighbors(this.neighbors);
		//this.updateNeighbors();
		//can.updateNeighbors();

		//	System.out.println("doing can.addNeighbor(this); ...");
		can.addNeighbor(new Neighbor(this.getIdentifier(), this.getIP(), this.getCoordinate()));

		//	System.out.println("doing this.neighbors.add(can); ...");
		this.neighbors.add(new Neighbor(can.getIdentifier(), can.getIP(), can.getCoordinate()));

		// increasing the both peer split times - will be used in time of
		// splitting
		//System.out.println("this peer spilt time: " + this.splitCounter);
		//System.out.println("new peer split time: " + can.getSplitCounter());

		this.splitCounter++;
		can.setSplitCounter(this.splitCounter);

		// this.neighbors.addAll(neighbors);
		// this.neighbors.add(can);
		// can.neighbors.add(this);
	}

	public void view() {
		System.out.println("\nPeer ID: " + this.identifier + "\nPeer IP: "
				+ this.ip + "\nPeer Coordination: " + this.coordinate
				+ "\nPeer Neighbors: " + this.neighbors+"\n");
	}

	@Override
	public void route(String Ip, int x, int y) throws RemoteException {
		boolean isTargetFound = false;

		CANInterface newPeer = null;
		CANInterface targetPeer = null;

		// check if the coordinates belong to this peer
		if (x >= this.coordinate.xStart && x <= this.coordinate.xEnd
				&& y >= this.coordinate.yStart && y <= this.coordinate.yEnd) {

			//System.out.println(this.identifier+ ":This coordination belong to me");

			// if so this peer needs to make socket connection to the new peer
			/*
			 * try { System.out.println("connceting to "+Ip+"..."); Socket
			 * socket = new Socket(Ip, 8000);
			 * System.out.println("connceted to "+Ip); input = new
			 * DataInputStream(socket.getInputStream()); output = new
			 * DataOutputStream(socket.getOutputStream());
			 * 
			 * output.writeInt(1); // sending signal to new peer to say target
			 * peer has been found socket.close(); } catch (ConnectException e)
			 * {
			 * 
			 * e.printStackTrace(); } catch (IOException e) {
			 * 
			 * e.printStackTrace(); }
			 */
			// now this peer needs to split its zone and send a half to the new
			// peer
			// locating the new peer in RMI server

			try {
				Registry registry = LocateRegistry.getRegistry(Ip,Constants.port);
				//System.out.println(Ip+ " registry was located.. \nlooking the remote object up...");
				newPeer = (CANInterface) registry.lookup(Ip);
				//System.out.println("New peer " + Ip + " was located.");
				this.setHashTable(newPeer);
				//newPeer.setHashTable(new CanImp("some ip"));
				//System.out.println("hash table is splited.");
				// showing peer info after successful join
				newPeer.view();
				isTargetFound=true;
			} catch (Exception ex) {
				System.err.println(ex.toString());
				newPeer.showMessage("\nCAN failed joing the new peer "+ Ip+ "\n");
			}
			// setting up the new peer coordinate, hash table and neighbors list

		} else {

			//System.out.println(this.identifier+ "checking if coordinate x,y belongs to one of my immediate neighbor");
			// check if the coordinate x,y belongs to one of my immediate
			// neighbor

			Iterator<Neighbor> iterator;
			iterator = this.neighbors.iterator();
			while (iterator.hasNext()) {
				Neighbor neighbor = iterator.next();

				if ((x >= neighbor.getXStart() && x <= neighbor.getXEnd())
						&& (y >= neighbor.getYStart() && y <= neighbor.getYEnd())) {

					// the new peer coordinates belong to one of my immediate
					// neighbor

					//	System.out.println("the new peer coordinates belong to one of my immediate neighbor");
					try {
						Registry registry = LocateRegistry.getRegistry(neighbor.getIP(), Constants.port);
						targetPeer = (CANInterface) registry.lookup(neighbor.getIP());

						//	System.out.println("neighbor " + Ip + " was located.");
						targetPeer.route(Ip, x, y);
						isTargetFound = true;
						//		System.out.println("\njoin request was forwarded to the neighboring peer at "+ targetPeer.getIP()+"\n");

					} catch (Exception ex) {
						System.err.println(ex.toString());
					}
				}
			}
		}
		// if target is not any of neighbors
		if (!isTargetFound) { 
			//System.out.println("Seems the target peer is NOT any of the neigboring peers.");

			// sending the join request to the closest peer
			// finding the closest peer to target peer
			Iterator<Neighbor> iterator = this.neighbors.iterator();
			Neighbor p = (Neighbor) iterator.next();
			Neighbor closestPeer = p;
			for (int index = 2; index <= this.neighbors.size(); index++) {
				p = (Neighbor) iterator.next();
				if (Math.abs((x + y)- (p.coordinate.xStart + p.coordinate.yStart)) < (Math
						.abs((x + y) - (closestPeer.coordinate.xStart + closestPeer.coordinate.yStart)))) {
					closestPeer = p;
				}
			}
			// sending the join request to the closest peer to the target peer
			try {
				//	System.out.println("sending the join request to the closest peer to the target peer...");

				Registry registry = LocateRegistry.getRegistry(closestPeer.getIP(), Constants.port);
				//	System.out.println(closestPeer.getIP() + " registry was located.. \nlooking the remote object up...");
				targetPeer = (CANInterface) registry.lookup(closestPeer.getIP());
				//	System.out.println("New peer " + targetPeer.getIP()+ " was located.");

				targetPeer.route(Ip,x,y);
				;// still dont know what is the value so just put 0?
				//		System.out.println("\njoin request was forwarded to the closest peer at "+ targetPeer.getIP()+"\n");

				// newPeer.setHashTable(new CanImp("some ip"));

				//	System.out.println(Ip+ " join request was forwarded to the closest peer at "+ targetPeer.getIP());
			} catch (Exception ex) {
				System.err.println(ex.toString());
			}

			isTargetFound = false;
		}

	}

	public void insert(String keyword, String ip, String route) throws RemoteException {
		//updating the route

		route=route +"->"+this.identifier+"@"+this.ip ;

		boolean isTargetFound = false;
		//CANInterface originPeer=null;
		int xCoor = xHashCode(keyword);
		int yCoor = yHashCode(keyword);

		// checking if the target is the current peer
		if (xCoor >= this.coordinate.xStart && xCoor <= this.coordinate.xEnd && yCoor >= this.coordinate.yStart&& yCoor <= this.coordinate.yEnd) {

			//System.out.println("Seems the current peer needs to store the key.");
			// so just put 0?
			//System.out.println("the keyword has been inserted in the current peer "+ this.identifier);
			//displaying the peer on which the key is stored
			try {
				Registry registry = LocateRegistry.getRegistry(ip,Constants.port);
				//System.out.println(ip+ " registry was located.. \nlooking the remote object up...");
				CANInterface originPeer = (CANInterface) registry.lookup(ip);
				//System.out.println("New peer " + ip + " was located.");
				try{
					this.hashTable.put(keyword, 0); // still dont know what is the value
					originPeer.showMessage("\nThe key was stored in "+this.identifier +" @" +this.getIP()+ " and this is the routing path: \n " +route+"\n");

				}catch (Exception ex){
					originPeer.showMessage("\n CAN failed to insert "+keyword+" !\n");

				}
				// showing peer info after successful join


			} catch (Exception ex) {
				System.err.println(ex.toString());
				//	originPeer.showMessage(Constants.failMsg);
			}

			isTargetFound = true;
		} else { // checking if the target is one of the neighboring peer

			//System.out.println("checking the neighboring peers coordination...");
			Iterator<Neighbor> iterator = this.neighbors.iterator();

			while (iterator.hasNext()) { // iterating through all neighbors
				// coordinates

				Neighbor neighbor = (Neighbor) iterator.next();

				if (xCoor >= neighbor.getXStart()
						&& xCoor <= neighbor.getXEnd()
						&& yCoor >= neighbor.getYStart()
						&& yCoor <= neighbor.getYEnd()) {

					//	System.out.println("Seems the target peer is one of the neigboring peers.");
					// getting the target object through rmi
					try {
						Registry registry = LocateRegistry.getRegistry(neighbor.getIP(), Constants.port);
						//	System.out.println(neighbor.getIP()+ " registry was located.. \nlooking the remote object up...");
						CANInterface targetPeer = (CANInterface) registry.lookup(neighbor.getIP());
						//	System.out.println("New peer " + targetPeer.getIP()+ " was located.");

						targetPeer.insert(keyword,ip,route);
						//targetPeer.addToHashtable(keyword, 0);// still dont know
						// what is the
						// value so just
						// put 0?

						// newPeer.setHashTable(new CanImp("some ip"));

						//System.out.println(keyword + " was inserted to "	+ targetPeer.getIP());
					} catch (Exception ex) {
						System.err.println(ex.toString());
					}

					isTargetFound = true;
				}
			}
		}

		// if target is not any of neighbors
		if (!isTargetFound) {
			//	System.out.println("sending the insert request to the closest peer to the target peer...");

			// sending the insert request to the closest peer

			// finding the closest peer to target peer
			Iterator<Neighbor> iterator = this.neighbors.iterator();
			Neighbor p = (Neighbor) iterator.next();
			Neighbor closestPeer = p;
			for (int index = 2; index <= this.neighbors.size(); index++) {
				p = (Neighbor) iterator.next();
				if (Math.abs((xCoor + yCoor)- (p.coordinate.xStart + p.coordinate.yStart)) < (Math.abs((xCoor + yCoor)- (p.coordinate.xStart + closestPeer.coordinate.yStart)))) {
					closestPeer = p;
				}
			}

			// sending the join request to the closest peer to the target peer
			try {
				//System.out.println("sending the insert request to the closest peer to the target peer...");

				Registry registry = LocateRegistry.getRegistry(closestPeer.getIP(), Constants.port);

				//System.out.println(closestPeer.getIP()+ " registry was located.. \nlooking the remote object up...");

				CANInterface targetPeer = (CANInterface) registry.lookup(closestPeer.getIP());

				//System.out.println("New peer " + targetPeer.getIP()+ " was located.");

				targetPeer.insert(keyword,ip,route);
				// still dont know what is the value so just put 0?

				// newPeer.setHashTable(new CanImp("some ip"));

				//		System.out.println(keyword+ " insert request was forwarded to the closest peer at "+ targetPeer.getIP());
			} catch (Exception ex) {
				System.err.println(ex.toString());
			}

			isTargetFound = false;
		}

	}

	public void search(String keyword, String ip, String route) {

		route=route +"->"+this.identifier+"@"+this.ip ;

		boolean isTargetFound = false;
		int xCoor = xHashCode(keyword);
		int yCoor = yHashCode(keyword);

		// checking if the target is the current peer
		if (xCoor >= this.coordinate.xStart && xCoor <= this.coordinate.xEnd
				&& yCoor >= this.coordinate.yStart && yCoor <= this.coordinate.yEnd) {

			//System.out.println("Seems the current peer has the key.");

			try {
				Registry registry = LocateRegistry.getRegistry(ip,Constants.port);
				//System.out.println(ip+ " registry was located.. \nlooking the remote object up...");
				CANInterface originPeer = (CANInterface) registry.lookup(ip);
				//System.out.println("New peer " + ip + " was located.");

				// showing peer info after successful join


				if (this.hashTable.containsKey(keyword)) {
					originPeer.showMessage("\nThe key was found in "+this.identifier +" @" +this.ip+ " and this is the routing path: \n " +route+"\n");
				} else {
					originPeer.showMessage("\nCAN failed to find "+ keyword+"!\n");
				}
				isTargetFound = true;
			} catch (Exception ex) {
				System.err.println(ex.toString());
				//	originPeer.showMessage(Constants.failMsg);
			}

		} else { // checking if the target is one of the neighboring peer

		//	System.out.println("checking the neighboring peers coordination...");
			Iterator<Neighbor> iterator = this.neighbors.iterator();

			while (iterator.hasNext()) { // iterating through all neighbors
				// coordinates

				Neighbor neighbor = (Neighbor) iterator.next();

				if (xCoor >= neighbor.coordinate.xStart
						&& xCoor <= neighbor.coordinate.xEnd
						&& yCoor >= neighbor.coordinate.yStart
						&& yCoor <= neighbor.coordinate.yEnd) {

					//		System.out.println("Seems the target peer is one of the neigboring peers.");

					try {
						Registry registry = LocateRegistry.getRegistry(neighbor.getIP(), Constants.port);
						//			System.out.println(neighbor.getIP()+ " registry was located.. \nlooking the remote object up...");
						CANInterface targetPeer = (CANInterface) registry.lookup(neighbor.getIP());
						//				System.out.println("New peer " + targetPeer.getIP()+ " was located.");

						targetPeer.search(keyword, ip, route);

					} catch (Exception ex) {
						System.err.println(ex.toString());
					}

					isTargetFound = true;

				}
			}
		}
		// if target is not any of neighbors
		if (!isTargetFound) {

			//	System.out.println("sending the search request to the closest peer to the target peer...");

			// sending the search request to the closest peer

			// finding the closest peer to target peer
			Iterator<Neighbor> iterator = this.neighbors.iterator();
			Neighbor p = (Neighbor) iterator.next();
			Neighbor closestPeer = p;
			for (int index = 2; index <= this.neighbors.size(); index++) {

				p = (Neighbor) iterator.next();
				if (Math.abs((xCoor + yCoor)- (p.coordinate.xStart + p.coordinate.yStart)) < (Math.abs((xCoor + yCoor)- (p.coordinate.xStart + closestPeer.coordinate.yStart)))) {
					closestPeer = p;
				}
			}

			// sending the join request to the closest peer to the target peer
			try {
				//				System.out.println("sending the search request to the closest peer to the target peer...");

				Registry registry = LocateRegistry.getRegistry(closestPeer.getIP(), Constants.port);
				//System.out.println(closestPeer.getIP()+ " registry was located.. \nlooking the remote object up...");
				CANInterface targetPeer = (CANInterface) registry.lookup(closestPeer.getIP());
				//System.out.println("New peer " + targetPeer.getIP()+ " was located.");
				targetPeer.search(keyword, ip, route);
				//		System.out.println(keyword+ " search request was forwarded to the closest peer at "+ targetPeer.getIP());

			} catch (Exception ex) {
				System.err.println(ex.toString());
			}
			isTargetFound = false;

		}
	}

	public int nextInt() throws RemoteException, IOException {

		return input.readInt();

	}
}

/*
 * 
 * int bootsrtapServerPort; BootstrapingServerInterface bootstrapingServer;
 * DataInputStream inputFromServer; DataOutputStream outputToServer;
 * 
 * 
 * private int xHashCode(String keyword) { int index = 1; int sum = 0; while
 * (index < keyword.length()) { Character c = keyword.charAt(index); sum = sum +
 * c.hashCode(); index += 2; } return sum % 10; }
 * 
 * private int yHashCode(String keyword) { int index = 0; int sum = 0; while
 * (index < keyword.length()) { Character c = keyword.charAt(index); sum = sum +
 * c.hashCode(); index += 2; } return sum % 10; // for the moment }
 * 
 * private void makeTheHashtableHalf(Peer peer1,Peer peer2){ Hashtable
 * halfHashtable; double xCoor=peer1.coordinate.xEnd-peer1.coordinate.xStart;
 * double yCoor=peer1.coordinate.yEnd-peer1.coordinate.yStart;
 * 
 * 
 * // 2 scenarios. if coordinate is square(x=y) or rectangle (x<y)
 * if(xCoor==yCoor){ peer2.coordinate.xStart =peer1.coordinate.xStart +
 * (xCoor/2); peer2.coordinate.xEnd =peer1.coordinate.xEnd;
 * peer1.coordinate.xEnd=peer2.coordinate.xStart-0.01;
 * peer2.coordinate.yStart=peer1.coordinate.yStart;
 * peer2.coordinate.yEnd=peer1.coordinate.yEnd;
 * 
 * }else if (xCoor<yCoor){ peer2.coordinate.yStart =peer1.coordinate.yStart +
 * (yCoor/2); peer2.coordinate.yEnd =peer1.coordinate.yEnd;
 * peer1.coordinate.yEnd=peer2.coordinate.yStart-0.01;
 * peer2.coordinate.xStart=peer1.coordinate.xStart;
 * peer2.coordinate.xEnd=peer1.coordinate.xEnd;
 * 
 * }else{
 * System.out.println("Oh.. this is impossible to happen while splitting!"); }
 * 
 * Enumeration keys=peer1.hashTable.keys();
 * 
 * //copying half of the pairs from peer1 to peer2 for(int i=1;
 * i<=peer1.hashTable.size();i++){ String key =keys.nextElement().toString();
 * int x=xHashCode(key); int y=yHashCode(key); if(x>=peer2.coordinate.xStart &&
 * x<=peer2.coordinate.xEnd && y>=peer2.coordinate.yStart &&
 * y<=peer2.coordinate.yEnd){ peer2.hashTable.put(key,peer1.hashTable.get(key));
 * peer1.hashTable.remove(key); System.out.println(key +
 * " has been copied from "+ peer1.identifier +" to "+ peer2.identifier); }
 * 
 * }
 * 
 * // updating the neighbors list //this is NOT this simple. more through
 * algorithm is needed here - page 163 (note the periodic refreshing)
 * peer2.neighbors.addAll(peer1.neighbors); peer2.neighbors.add(peer1);
 * peer1.neighbors.add(peer2); }
 * 
 * 
 * public void search(String keyword) {
 * 
 * boolean isTargetFound=false; int xCoor=xHashCode(keyword); int
 * yCoor=yHashCode(keyword); // checking if the target is the current peer if
 * (xCoor>=peer.coordinate.xStart && xCoor<=peer.coordinate.xEnd &&
 * yCoor>=peer.coordinate.yStart && yCoor<=peer.coordinate.yEnd){
 * System.out.println("Seems the current peer needs to store the key.");
 * 
 * if(peer.hashTable.containsKey(keyword)){
 * System.out.println("the keyword has been found in the current peer "
 * +peer.identifier );
 * 
 * }else{ System.out.println("Search Failed!"); }
 * 
 * 
 * }else{ //checking if the target is one of the neighboring peer
 * 
 * System.out.println("checking the neighboring peers coordination...");
 * 
 * while(!isTargetFound){ //iterating through all neighbors coordinates Iterator
 * iterator=peer.neighbors.iterator(); Peer p=(Peer)iterator.next();
 * 
 * if (xCoor>=p.coordinate.xStart && xCoor<=p.coordinate.xEnd &&
 * yCoor>=p.coordinate.yStart && yCoor<=p.coordinate.yEnd){
 * System.out.println("Seems the target peer is one of the neigboring peers.");
 * if(p.hashTable.containsKey(keyword)){
 * System.out.println("the keyword has been found in the current peer "
 * +p.identifier );
 * 
 * }else{ System.out.println("Search Failed!"); }
 * 
 * isTargetFound=true; } } } //if target is not any of neighbors
 * if(!isTargetFound){
 * System.out.println("Seems the target peer is NOT any of the neigboring peers."
 * );
 * 
 * // sending the search request to the closest peer
 * 
 * //finding the closest peer to target peer Iterator
 * iterator=peer.neighbors.iterator(); Peer p=(Peer)iterator.next(); Peer
 * closestPeer= p; for(int index=2; index<= peer.neighbors.size(); index++){
 * p=(Peer)iterator.next();
 * if(Math.abs((xCoor+yCoor)-(p.coordinate.xStart+p.coordinate.yStart))<
 * (Math.abs
 * ((xCoor+yCoor)-(p.coordinate.xStart+closestPeer.coordinate.yStart)))){
 * closestPeer=p; } }
 * 
 * //sending the join request to the closest peer to the target peer try {
 * Socket socket = new Socket(closestPeer.ip, 8000); inputFromServer = new
 * DataInputStream(socket.getInputStream()); outputToServer = new
 * DataOutputStream(socket.getOutputStream());
 * 
 * outputToServer.writeInt(2); // search request code =2 // More data needs to
 * be sent here System.out.println("Join request sent to closest peer at " +
 * closestPeer.ip); } catch (ConnectException e) {
 * System.err.println("Connection Error to entry peer"); e.printStackTrace(); }
 * catch (IOException e) {
 * System.err.println("IO Error while connecting to entry peer");
 * e.printStackTrace(); } }
 * 
 * 
 * 
 * 
 * }
 * 
 * public void view() { if(peer==null){ System.out.println(
 * "You need to join first before your you can view this peer's info!"); }else{
 * System.out.println("Peer ID: "+peer.identifier+"\nPeer IP: "+peer.ip+
 * "\nPeer Coordination: "+peer.coordinate
 * +"\nPeer Neighbors: "+peer.neighbors); }
 * 
 * }
 * 
 * 
 * public String routIt(String ip,int x, int y){
 * 
 * 
 * return null; }
 * 
 * public String join(String ip) throws RemoteException,UnknownHostException {
 * // connecting to bootstrapping server try { Registry registry =
 * LocateRegistry.getRegistry("glados.cs.rit.edu"); bootstrapingServer =
 * (BootstrapingServerInterface) registry .lookup("BootstrapingServer");
 * System.out.println("BootstrapingServer " + bootstrapingServer +
 * " was located."); } catch (Exception ex) { System.err.println(ex.toString());
 * } System.out.println("peer count =  "+ bootstrapingServer.getPeerCount());
 * 
 * 
 * //String Ip = InetAddress.getLocalHost().getHostAddress(); // getting the
 * peer IP
 * 
 * bootstrapingServer.increasePeerCount();
 * //System.out.println("creating the new peer.."); //peer = new Peer();
 * //creating the new peer //peer.setIP(Ip); // setting the new peer ip
 * //peer.setIdentifier("p"+bootstrapingServer.getPeerCount()); //setting the
 * new peer id
 * 
 * if (bootstrapingServer.getPeerCount()==1) { // when the peer is the first
 * peer to join
 * 
 * bootstrapingServer.setEntryPeerIp(ip);
 * 
 * return "firstPeer"; //meaning the peer is the first peer in the system
 * 
 * } else { // When the peer is not the first peer to join
 * 
 * // no need to store the entire entry peer in BSS. only the Ip.
 * 
 * System.out.println("inside the else/  the peer is not the first peer to join")
 * ; System.out.println("getting the entry peer from BSS info...");
 * 
 * Double i = Math.random() * 10; Double j = Math.random() * 10;
 * 
 * int x = i.intValue(); int y = j.intValue();
 * 
 * String entryPeerIp = bootstrapingServer.getEntryPeerIp(); // get entry //
 * Peer from // BootStraping // server return routIt(entryPeerIp,x,y); } /*
 * Coordinate entryPeerCoordinate = entryPeer.getCoordinate(); String
 * entryPeerIp = entryPeer.ip;
 */

// updating the neighbors list here

// Establishing a client/server connection between the new peer and
// entry peer
/*
 * try { System.out.println("trying to connect to entry peer at " +entryPeerIp);
 * 
 * Socket socket = new Socket(entryPeerIp, 9876); System.out.println(
 * "if you see this. it means connection to entry peer established!");
 * inputFromServer = new DataInputStream(socket.getInputStream());
 * outputToServer = new DataOutputStream(socket.getOutputStream());
 * outputToServer.write(0); // join request sent to entry peer
 * 
 * 
 * // generating the random coordination
 * 
 * Double i = Math.random() * 10; Double j = Math.random() * 10;
 * 
 * int x = i.intValue(); int y = j.intValue(); // sending the generated
 * coordinate for the new peer to entry peer. outputToServer.write(x);
 * outputToServer.write(y);
 * 
 * } catch (ConnectException e) {
 * System.err.println("Connection Error to entry peer"); e.printStackTrace(); }
 * catch (IOException e) {
 * System.err.println("IO Error while connecting to entry peer");
 * e.printStackTrace(); } // System.out.println("Connected to entry peer at "+
 * entryPeerIp); /* // generating a random coordinate for the new peer to join
 * Double i = Math.random() * 10; Double j = Math.random() * 10;
 * 
 * int x = i.intValue(); int y = j.intValue();
 * 
 * 
 * if ((x >= entryPeerCoordinate.xStart && x <= entryPeerCoordinate.xEnd) && (y
 * >= entryPeerCoordinate.yStart && y <= entryPeerCoordinate.yEnd)) {
 * 
 * System.out.println("seems like the entry peer is to split");
 * 
 * // Splitting the zone here
 * 
 * makeTheHashtableHalf(entryPeer,peer);
 * 
 * 
 * //updating the neighbors list here
 * 
 * //adding all the new peer's neighbors /*
 * peer.neighbors.addAll(entryPeer.neighbors); peer.neighbors.add(entryPeer);
 * entryPeer.neighbors.add(peer);
 */

/*
 * 
 * }else{ // join request needs to be forwarded
 * System.out.println("Forwarding join request to the neighboring peers..");
 * boolean isTargetPeerFound=false; int k=0; /* while(!isTargetPeerFound){
 * System.out.println(
 * "join request is to be sent to the closest neighbor to the target peer");
 * //check if the coordinate x,y belongs to one of the entry immediate neighbor
 * 
 * if (entryPeer.neigbors[k]!=null){
 * if((x>=entryPeer.neigbors[k].coordinate.xStart && x<=
 * entryPeer.neigbors[k].coordinate.xEnd)&&
 * (y>=entryPeer.neigbors[k].coordinate.yStart &&
 * y<=entryPeer.neigbors[k].coordinate.yEnd )){ // Establish connection try {
 * Socket socket = new Socket(entryPeer.neigbors[k].ip, 8000); inputFromServer =
 * new DataInputStream(socket.getInputStream()); outputToServer = new
 * DataOutputStream(socket.getOutputStream()); outputToServer.writeInt(0); //
 * join request code =0
 * System.out.println("Join request sent to closest peer at " +
 * entryPeer.neigbors[k].ip); } catch (ConnectException e) {
 * System.err.println("Connection Error to entry peer"); e.printStackTrace(); }
 * catch (IOException e) {
 * System.err.println("IO Error while connecting to entry peer");
 * e.printStackTrace(); }
 * 
 * isTargetPeerFound=true; } } }
 * 
 * // if the coordinate x,y DOES NOT belong to one of the entry immediate
 * neighbor
 * 
 * if(!isTargetPeerFound){ //NO NEED TO FLOOD THE JOIN REQUEST. THE REQUEST ONLY
 * NEEDS TO BE SENT TO THE CLOSET IMMEDIATE NEIGBOR !
 * 
 * //finding the closest peer to target peer
 * 
 * //if (entryPeer.neigbors[0]!=null){ Peer closestPeer=entryPeer.neigbors[0];
 * //}
 * 
 * for(int index=1;i<entryPeer.neigbors.length;k++){ if
 * (entryPeer.neigbors[index]!=null){
 * if(Math.abs((x+y)-(entryPeer.neigbors[index
 * ].coordinate.xStart+entryPeer.neigbors[index].coordinate.yStart))<
 * (Math.abs((
 * x+y)-(closestPeer.coordinate.xStart+closestPeer.coordinate.yStart)))){
 * closestPeer=entryPeer.neigbors[index]; } } } //sending the join request to
 * the closest peer to the target peer try { Socket socket = new
 * Socket(closestPeer.ip, 8000); inputFromServer = new
 * DataInputStream(socket.getInputStream()); outputToServer = new
 * DataOutputStream(socket.getOutputStream()); outputToServer.writeInt(0); //
 * join request code =0
 * System.out.println("Join request sent to closest peer at " + closestPeer.ip);
 * } catch (ConnectException e) {
 * System.err.println("Connection Error to entry peer"); e.printStackTrace(); }
 * catch (IOException e) {
 * System.err.println("IO Error while connecting to entry peer");
 * e.printStackTrace(); }
 * 
 * 
 * //sending join request to all neighboring peers /* for(int
 * k=0;i<entryPeer.neigbors.length;k++){ if (entryPeer.neigbors[k]!=null){ try {
 * Socket socket = new Socket(entryPeer.neigbors[k].ip, 8000); inputFromServer =
 * new DataInputStream(socket.getInputStream()); outputToServer = new
 * DataOutputStream(socket.getOutputStream()); outputToServer.writeInt(0); //
 * join request code =0
 * 
 * } catch (ConnectException e) {
 * System.err.println("Connection Error to entry peer"); e.printStackTrace(); }
 * catch (IOException e) {
 * System.err.println("IO Error while connecting to entry peer");
 * e.printStackTrace(); } }
 * 
 * } }
 */
// }

// }

// peer.start();
// }

