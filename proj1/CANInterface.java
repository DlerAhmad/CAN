import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Hashtable;

public interface CANInterface extends Remote {

	public void setIdentifier(String id)throws RemoteException;
	public String getIdentifier()throws RemoteException;
	public void setCoordinate(double xStart,double xEnd,double yStart, double yEnd)throws RemoteException;
	public Coordinate getCoordinate()throws RemoteException;
	public void setIP(String ip)throws RemoteException;
	public String getIP()throws RemoteException;
	
	public void setXStart(double xS) throws RemoteException;
	public void setXEnd(double xE) throws RemoteException;
	public void setYStart(double yS) throws RemoteException;
	public void setYEnd(double yE) throws RemoteException;
	
	public double getXStart() throws RemoteException;
	public double getXEnd() throws RemoteException;
	public double getYStart() throws RemoteException;
	public double getYEnd() throws RemoteException;
	
	public void addAllNeighbors(ArrayList<Neighbor> neighborsList) throws RemoteException;
	public void addNeighbor(Neighbor neighbor) throws RemoteException;
	public void updateNeighbors() throws RemoteException;
	
	public void addToHashtable(String key, int value) throws RemoteException;

	public void setHashTable(CANInterface can)throws RemoteException;
	public int nextInt()throws RemoteException,IOException;
	public void increaseSplitCounter() throws RemoteException;
	public int getSplitCounter()throws RemoteException;
	public void setSplitCounter(int splitCounter) throws RemoteException;
	public boolean contains(String key)throws RemoteException;
	public void showMessage(String message) throws RemoteException;

//	public String toString() throws RemoteException;
	public void route(String Ip, int x, int y)throws RemoteException;
	public void insert(String keyword, String ip, String route)throws RemoteException;
	public void search(String keyword, String ip, String route)throws RemoteException;
	public void view()throws RemoteException;
	//public String join () throws RemoteException;
	//public void leave(String peerIdentifier)throws RemoteException;
	
	

}
