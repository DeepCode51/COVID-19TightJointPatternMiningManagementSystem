package ui;

import java.awt.Color;

public class Point {
	private double x;
	private double y;
	private Color c;
	
	public Point(double x, double y) {
		this.setX(x);
		this.setY(y);
		this.setC(Color.BLACK);
	}
	
	public Point(double x, double y, Color c) {
		this.setX(x);
		this.setY(y);
		this.setC(c);
	}
	
	public void normalization(double dx, double dy, double minX, double minY) {
		this.x = (this.x - minX) / dx;
		this.y = (this.y - minY) / dy;
	}
	
	public double getDistance(Point p) {
		double dx = x - p.getX();
		double dy = y - p.getY();
		return Math.sqrt(dx * dx + dy * dy);
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public Color getC() {
		return c;
	}

	public void setC(Color c) {
		this.c = c;
	}
}
