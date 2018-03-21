package project;

public class ParkingSpot {
	int x1;
	int y1;
	int x2;
	int y2;
	int spotNumber;
	public ParkingSpot(int x1, int y1, int x2, int y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.spotNumber = spotNumber;
	}
	public int getX1() {
		return this.x1;
	}
	public void setX1(int x) {
		this.x1 = x;
	}
	public int getY1() {
		return this.y1;
	}
	public void setY1(int y) {
		this.y1 = y;
	}
	public int getX2() {
		return this.x2;
	}
	public void setX2(int x) {
		this.x2 = x;
	}
	public int getY2() {
		return this.y2;
	}
	public void setY2(int y) {
		this.y2 = y;
	}
	public int getSpotNumber() {
		return spotNumber;
	}
	public void setSpotNumber(int n) {
		this.spotNumber = n;
	}
public void getSpot(int x1, int x2, int y1, int y2) {
		ParkingSpotLocator h = new ParkingSpotLocator();
		for(int i = 0; i < h.original.size(); i++) {
			if(h.original.get(i).getX1() == x1 && h.original.get(i).getX2() == x2 && h.original.get(i).getY1() == y1 && h.original.get(i).getY2() == y2) {
				
			}
		}
	}
}