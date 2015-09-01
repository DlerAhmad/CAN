import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

public class BootstrapingServerImp extends UnicastRemoteObject implements
		BootstrapingServerInterface {

	protected BootstrapingServerImp() throws RemoteException {

	}

	// static boolean firstPeer=true;
	//static Peer theEntryPeer;
	static String entryPeerIp;
	static int peerCount = 0;

	public String getEntryPeerIp() throws RemoteException {
		return entryPeerIp;
	}

	public void setEntryPeerIp(String ip) throws RemoteException {
		entryPeerIp = ip;
	}

	/*
	 * public boolean isFirstPeer()throws RemoteException{ return firstPeer;
	 * 
	 * } public void setFirstPeer(boolean bool)throws RemoteException{
	 * firstPeer=bool; }
	 */
	public static void main(String args[]) throws RemoteException {
		/*
		 * if (args.length!=2){
		 * System.out.println("Usage: java BootstrapServer join {peer ID}");
		 * }else if (args[0]!="join"){
		 * System.out.println("Usage: java BootstrapServer join {peer ID}");
		 * }else{ join(args[1]); }
		 */
	}

	public int getPeerCount() throws RemoteException {
		return peerCount;
	}

	public void increasePeerCount() throws RemoteException {
		peerCount++;
	}

	public void decreasePeerCount() throws RemoteException {
		peerCount--;
	}
}