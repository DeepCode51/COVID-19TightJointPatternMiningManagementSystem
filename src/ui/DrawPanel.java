package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class DrawPanel extends /*JLayeredPane*/ JPanel {
	public ArrayList<Point> points;
	public ArrayList<Line> segs;
	public ArrayList<IdTag> tags;
	
	public DrawPanel() {
		points = new ArrayList<Point>();
		segs = new ArrayList<Line>();
		tags = new ArrayList<IdTag>();
	}
	
	public DrawPanel(ArrayList<Point> pList, ArrayList<Line> segList, ArrayList<IdTag> tagList) {
		this.points = pList;
		this.segs = segList;
		this.tags = tagList;
	}
	
	public void pointNormalization() {
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double maxX = 0, maxY = 0;
		for (Point p : points) {
			minX = Math.min(minX, p.getX());
			minY = Math.min(minY, p.getY());
			maxX = Math.max(maxX, p.getX());
			maxY = Math.max(maxY, p.getY());
		}
		double dx = maxX - minX;
		double dy = maxY - minY;
		for (Point p : points) {
			p.normalization(dx, dy, minX, minY);
		}
	}
	
	public void paint(Graphics g) {
		
//		points.add(new Point(1, 1, Color.BLACK));
		
//		System.out.println("in");
//		System.out.println("points: " + points.size());
//		System.out.println("segments: " + segs.size());
		
		pointNormalization();
		
		double w = (double) getWidth() - 20D;
		double h = (double) getHeight() - 20D;
		
//		System.out.println("w = " + w + "  h = " + h);
		
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// ±³¾°
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		// »­Ïß¶Î
		g2d.setStroke(new BasicStroke(2));
		double x1, x2, y1, y2;
		for (Line seg : segs) {
			g2d.setColor(seg.getC());
			x1 = w * seg.getP1().getX() + 10;
			y1 = h * seg.getP1().getY() + 10;
			x2 = w * seg.getP2().getX() + 10;
			y2 = h * seg.getP2().getY() + 10;
			g2d.draw(new Line2D.Double(x1, y1, x2, y2));
//			System.out.println(String.format("%f  %f  %f  %f", x1, y1, x2, y2));
		}
		
		// »­µã
		g.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(4));
		for (Point p : points) {
//			g2d.setColor(p.getC());
			double x = w * p.getX() + 10;
			double y = h * p.getY() + 10;
//			g2d.draw((Shape) new Point2D.Double(x, y));
			g2d.draw(new Line2D.Double(x, y, x, y));
		}
		
		double sx = this.getBounds().getX();
		double sy = this.getBounds().getY();
		Point p;
		
		for (IdTag tag : tags) {
			p = points.get(tag.p);
			float x = (float) (w * p.getX() + 10);
			float y = (float) (h * p.getY() + 10);
			if (x >= w)
				x = (float) w - 15;
			else if (x <= 0)
				x = (float) 15;
			if (y >= h)
				y = (float) h - 15;
			else if (y <= 0)
				y = (float) 15;
			g2d.drawString(Integer.toString(tag.id), x, y);
		}
	}
}
