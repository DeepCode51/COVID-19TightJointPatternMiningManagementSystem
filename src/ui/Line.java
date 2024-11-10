package ui;

import java.awt.Color;

public class Line {
	private Point p1;
	private Point p2;
	private Color c;
	
	public Line(Point p1, Point p2, Color c) {
		this.setP1(p1);
		this.setP2(p2);
		this.setC(c);
	}

	public Point getP1() {
		return p1;
	}

	public void setP1(Point p1) {
		this.p1 = p1;
	}

	public Point getP2() {
		return p2;
	}

	public void setP2(Point p2) {
		this.p2 = p2;
	}

	public Color getC() {
		return c;
	}

	public void setC(Color c) {
		this.c = c;
	}
}
