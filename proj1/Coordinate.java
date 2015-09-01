import java.io.Serializable;


public class Coordinate implements Serializable {
	double xStart;
	double xEnd;
	double yStart;
	double yEnd;
	
	public Coordinate(double xStart,double xEnd,double yStart,double yEnd){
		this.xStart=xStart;
		this.xEnd=xEnd;
		this.yStart=yStart;
		this.yEnd=yEnd;
	}

	@Override
	public String toString() {
		return "[" + xStart + ", " + xEnd + ", "
				+ yStart + ", " + yEnd + "]";
	}

}
