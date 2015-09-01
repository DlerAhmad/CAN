import java.io.Serializable;
import java.rmi.RemoteException;


public class Neighbor implements Serializable{
	
	String identifier;
	String ip;
	Coordinate coordinate;

	public Neighbor(String id, String ip, Coordinate coordinate){
		this.identifier=id;
		this.ip=ip;
		this.coordinate=coordinate;
	}
	public String getIP(){
		return this.ip;
	}
	public void setXStart(double xS) throws RemoteException{
		this.coordinate.xStart=xS;
	}
	public void setXEnd(double xE) throws RemoteException{
		this.coordinate.xEnd=xE;
	}
	public void setYStart(double yS) throws RemoteException{
		this.coordinate.yStart= yS;
	}
	public void setYEnd(double yE) throws RemoteException{
		this.coordinate.yEnd=yE;
	}
	
	public double getXStart() throws RemoteException{
		return this.coordinate.xStart;
	}
	public double getXEnd() throws RemoteException{
		return this.coordinate.xEnd;
	}
	public double getYStart() throws RemoteException{
		return this.coordinate.yStart;
	}
	public double getYEnd() throws RemoteException{
		return this.coordinate.yEnd;
	}
	public String toString(){
		return (this.identifier + "@ " +this.ip);
	}
}
