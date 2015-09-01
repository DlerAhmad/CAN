import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class CanClient {
	// private static Peer peer;
	private static CanImp can;

	/*
	 * public void listen() { try { System.out.println(peer.identifier +
	 * " is listening for connection.."); ServerSocket serverSocket = new
	 * ServerSocket(9876); Socket socket = new Socket();
	 * System.out.println(peer.identifier +
	 * " is listening for connection part 2");
	 * 
	 * socket = serverSocket.accept(); System.out.println(peer.identifier +
	 * " is listening for connection part 3");
	 * 
	 * input = new BufferedReader(new
	 * InputStreamReader(socket.getInputStream())); output = new
	 * DataOutputStream(socket.getOutputStream());
	 * 
	 * String code= input.readLine(); //int code = input.readInt();
	 * System.out.println("Request" + code + " has been recieved.");
	 * 
	 * } catch (Exception e) { e.printStackTrace(); }
	 * 
	 * }
	 */

	public static void main(String args[]) throws RemoteException,UnknownHostException {
		
		BootstrapingServerInterface bootstrapingServer = null;
		CANInterface entryPeer = null;
		int peerCount;
		DataInputStream input;
		DataOutputStream output;

		// Can can = new Can();
		/*
		CANInterface remoteCan = null;

		try {
			Registry registry1 = LocateRegistry
					.getRegistry("glados.cs.rit.edu",Constants.port);
			remoteCan = (CANInterface) registry1.lookup("remoteCan");
			System.out.println("remoteCan " + remoteCan + " was located.");
		} catch (Exception ex) {
			System.err.println(ex.toString());
		}
*/
		// System.out.println("peer count =  "+ remoteCan.getPeerCount());

		System.out.println("Welcome to the CAN!\n**********");
		System.out
				.println("Please tell me what to do. You may give the following commands:");
		System.out
				.println("join\nleave\nview\ninsert [keyword]\nsearch [keyword]\nexit");

		Scanner in = new Scanner(System.in);

		String command;

		while (true) {
			command = in.next();
			if (command.equals("exit")) {
				System.out.println("Thanks for using CAN.");
				System.exit(0);
			} else if (command.equals("join")) {
				if(can!=null){
					System.out.println("This peer is already joined.");
				}else{

				// creating a new peer
				can = new CanImp(InetAddress.getLocalHost().getHostAddress());
				//can.setIP(InetAddress.getLocalHost().getHostAddress());
				// Registering the new peer with RMI
				/*
				try {
					Registry registry = LocateRegistry.getRegistry(can.getIP());
					System.out.println("registry located.");
					registry.rebind(can.getIP(), can);
					System.out.println("new peer " + can.getIP()
							+ " registered.");
				} catch (RemoteException ex) {
					System.out.println("Exception in Regisry.");
					System.out.println(ex.toString());
				}
				*/
				// peer.ip = InetAddress.getLocalHost().getHostAddress();

				// System.out.println(command + " was given.");

				// connecting to bootstrapping server
				try {
					
					//System.out.println("Trying to connect BSS...");
					Registry registry2 = LocateRegistry.getRegistry("glados.cs.rit.edu",Constants.port);
					bootstrapingServer = (BootstrapingServerInterface) registry2.lookup("BootstrapingServer");
					//System.out.println("BootstrapingServer "+ bootstrapingServer + " was located.");
				
				} catch (Exception ex) {
					System.err.println(ex.toString());
				}
				bootstrapingServer.increasePeerCount();
				peerCount = bootstrapingServer.getPeerCount();

				can.setIdentifier("p" + peerCount);
				//System.out.println("peer count =  " + peerCount);

				// String result = remoteCan.join(peer.ip);
				if (peerCount == 1) {
					//System.out.println("seems you are the first peer in the CAN..");
					//System.out.println("setting the new peer coordinate..");
					can.setCoordinate(0, 10, 0, 10);
					// setting the new peer as the entry peer
					bootstrapingServer.setEntryPeerIp(can.getIP());
					//System.out.println(can.getIdentifier() + " joind the CAN.");
					can.view();
				} else {
					// contacting entry peer
					try {
					//	System.out.println("contacting entry peer.."+bootstrapingServer.getEntryPeerIp());
						Registry registry3 = LocateRegistry.getRegistry(bootstrapingServer.getEntryPeerIp(),Constants.port);
					//	System.out.println("contacting entry peer 2..");
						entryPeer = (CANInterface) registry3 .lookup(bootstrapingServer.getEntryPeerIp());
					//	System.out.println("entryPeer "+ entryPeer.getIdentifier() + " was located.");
					} catch (Exception ex) {
						System.err.println(ex.toString());
					}
					// generating the x,y coordinate for the new peer
					Double i = Math.random() * 10;
					Double j = Math.random() * 10;

					int x = i.intValue();
					int y = j.intValue();
					System.out.println("random coordination= " + x + "," + y);
					// sending the new peer ip along with the random x,y to the
					// entry peer

					// at this point the new peer needs to listen for the target
					// peer connection
					entryPeer.route(can.getIP(), x, y);
					/*
					try {
						/*
						 * System.out.println(can.getIdentifier()+
						 * " is listening for connection.."); ServerSocket
						 * serverSocket =new ServerSocket(8000); Socket socket ;
						 * System.out.println(can.getIdentifier()+
						 * " is listening for connection part 2");
						 * 
						 * socket=serverSocket.accept();
						 * 
						 * 
						 * 
						 * System.out.println(can.getIdentifier()+
						 * " is listening for connection part 3");
						 * 
						 * input = new DataInputStream(socket.getInputStream());
						 * output = new
						 * DataOutputStream(socket.getOutputStream());
						 */
						
						/*
						Socket socket = can.serverSocket.accept();

						System.out.println("connected to "
								+ socket.getLocalAddress().getHostAddress());

						input = new DataInputStream(socket.getInputStream());
						output = new DataOutputStream(socket.getOutputStream());
						int code = input.readInt();

						System.out.println("Request" + code
								+ " has been recieved.");
						if (code == 0) {
							System.out.println("Joining Failed!");

						} else {
							// at this point we have found the target peer
							// target peer needs to split its zone

							System.out.println("Joining successful!");

						}

						socket.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					
*/
				}
				}

			} else if (command.equals("view")) {
				if (can == null) {
					System.out
							.println("You need to join first before your you can view this peer's info!");
				} else {
					can.view();
				}

			} else if (command.equals("insert")) {
				if (can == null) {
					System.out.println("You need to join first before your you can insert!");
				} else {
					can.insert(in.next(), can.getIP(),"");
				}

			} else if (command.equals("search")) {
				if (can == null) {
					System.out.println("You need to join first before your you can insert!");
				} else {
					can.search(in.next(),can.getIP(),"");
				}
			} else if (command.equals("leave")) {
				System.out.println("The 'leave' command is not available yet!");

			} else {
				System.out.println("The given command is not valid!");
			}

			System.out.println("Can is waiting for your next command..");
		}

		/*
		 * System.out.println("here=" + can.xHashCode("ssmmppllee"));
		 * System.out.println("here=" + can.yHashCode("ssmmppllee"));
		 * System.out.println(InetAddress.getLocalHost().getHostAddress());
		 * 
		 * Hashtable<String,Integer> hashTable=new Hashtable<String,Integer>();
		 * Hashtable<String,Integer> hashTable2=new Hashtable<String,Integer>();
		 * 
		 * hashTable.put("first",1); hashTable.put("second",2);
		 * hashTable.put("third",3); hashTable.put("forth",4);
		 * hashTable.put("fifth",5); hashTable.put("sixth",6);
		 * hashTable.put("seventh",7);
		 * 
		 * 
		 * Set<Entry<String, Integer>> set=new HashSet(); Enumeration
		 * keys=hashTable.keys();
		 * 
		 * set=hashTable.entrySet();
		 * 
		 * Iterator iterator; iterator=set.iterator();
		 * 
		 * int hashTableHalfSize=(hashTable.size()+1)/2;
		 * //System.out.println(keys.toString()); for(int i=1;
		 * i<=hashTableHalfSize;i++){ //System.out.println(keys.nextElement());
		 * String key= keys.nextElement().toString();
		 * hashTable2.put(key,hashTable.get(key)); hashTable.remove(key);
		 * //iterator.next(); // iterator.remove(); }
		 * 
		 * 
		 * set=hashTable.entrySet(); /* Iterator iterator;
		 * iterator=set.iterator(); // hashTable2.put(iterator.next());
		 * iterator.remove(); iterator.next(); iterator.remove();
		 * iterator.next(); iterator.remove(); iterator.next();
		 * iterator.remove(); iterator.next(); iterator.remove();
		 * iterator.next(); iterator.remove();
		 */
		// hashTable2.putAll(hashTable);
		/*
		 * System.out.println(hashTable.toString());
		 * System.out.println(hashTable2.toString());
		 * //System.out.println(set.toString());
		 * 
		 * 
		 * System.out.println((hashTable.size()+1)/2);
		 */

	}

}
