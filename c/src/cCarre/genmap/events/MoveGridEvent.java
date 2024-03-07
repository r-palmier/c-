package cCarre.genmap.events;

public class MoveGridEvent {
	private double x = 0;
	
	public MoveGridEvent(double x) {
		this.x = x;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}
}