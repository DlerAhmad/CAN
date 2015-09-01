import java.rmi.Remote;
import java.rmi.RemoteException;


public interface BootstrapingServerInterface extends Remote {
	public String getEntryPeerIp() throws RemoteException;
	public void setEntryPeerIp(String ip) throws RemoteException;
	//public boolean isFirstPeer() throws RemoteException;
	//public void setFirstPeer(boolean bool)throws RemoteException;
	public int getPeerCount() throws RemoteException;
	public void increasePeerCount() throws RemoteException;
	public void decreasePeerCount() throws RemoteException;

}
