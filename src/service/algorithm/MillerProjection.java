package service.algorithm;

import java.util.ArrayList;

import service.resource.MyObject;

/**
 * 米勒投影
 * @author wyqaq
 */
public class MillerProjection {

//	public static double EARTH_PERIMETER = 6371.393 * 1000 * Math.PI * 2D;
	public static double EARTH_PERIMETER = 6371.393 * Math.PI * 2D;
	public static double MAP_WIDTH = EARTH_PERIMETER;
	public static double MAP_HEIGHT = EARTH_PERIMETER / 2D;
	public static double MILLER_CONST = 2.3;// 米勒投影中的一个常数
	
	public static void projectAll(ArrayList<MyObject> objs, int objNum, int stampNum) {
		for (int i = 1; i <= objNum; i++)
			projectAll(objs.get(i), stampNum);
	}
	
	public static void projectAll(MyObject obj, int stampNum) {
		for (int t = 1; t <= stampNum; t++) {
			project(obj, t);
		}
	}
	
	public static void project(MyObject obj, int time) {
		double x = Math.toRadians(obj.longitude[time]);
		double y = Math.toRadians(obj.latitude[time]);
		y = 1.25 * Math.log(Math.tan(0.25 * Math.PI + 0.4 * y));
		x = MAP_WIDTH / 2D + MAP_WIDTH / (2D * Math.PI) * x;
		y = MAP_HEIGHT / 2D - MAP_HEIGHT / (2D * MILLER_CONST) * y;
		obj.x[time] = x;
		obj.y[time] = y;
	}
}
