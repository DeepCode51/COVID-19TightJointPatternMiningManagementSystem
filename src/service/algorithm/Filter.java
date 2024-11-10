package service.algorithm;

import java.util.ArrayList;

import service.resource.MyObject;

/**
 * 滤波器
 * 过滤漂移的点
 * 更正为前后最近的无漂移点的中值
 * @author wyqaq
 */
public class Filter {
	
	public static double OUTLIER_THRESHOLD = 5;
	
	public static void filterAll(ArrayList<MyObject> objs, int objNum, int stampNum) {
		for (int i = 1; i <= objNum; i++) {
			filter(objs.get(i), stampNum);
		}
	}
	
	public static void filter(MyObject obj, int stampNum) {
		// 经纬度的异常点
		ArrayList<Integer> lngOutlier = new ArrayList<Integer>();
		ArrayList<Integer> latOutlier = new ArrayList<Integer>();
		
		if (!findOutlier(obj, stampNum, lngOutlier, latOutlier))
			return;// 若无异常点，则不修正，直接退出

		double val;
		int p, l, r;
		
		while (!lngOutlier.isEmpty()) {
			p = 0;
			l = lngOutlier.get(0);
			r = l;
			// 连续的异常点，将同时被联合修正，而非单独修正
			while (p < lngOutlier.size() - 1 && lngOutlier.get(p + 1) == r + 1) {
				p++;
				r++;
			}
			// 左右端点特判
			if (l == 1) {
				val = obj.longitude[r + 1];
			} else if (r == stampNum) {
				val = obj.longitude[l - 1];
			} else {
				val = (obj.longitude[l - 1] + obj.longitude[r + 1]) / 2;
			}
			for (int t = l; t <= r; t++) {
				obj.longitude[t] = val;
				lngOutlier.remove(0);
			}
		}
		
		while (!latOutlier.isEmpty()) {
			p = 0;
			l = latOutlier.get(0);
			r = l;
			// 连续的异常点，将同时被联合修正，而非单独修正
			while (p < latOutlier.size() - 1 && latOutlier.get(p + 1) == r + 1) {
				p++;
				r++;
			}
			// 左右端点特判
			if (l == 1) {
				val = obj.latitude[r + 1];
			} else if (r == stampNum) {
				val = obj.latitude[l - 1];
			} else {
				val = (obj.latitude[l - 1] + obj.latitude[r + 1]) / 2;
			}
			for (int t = l; t <= r; t++) {
				obj.latitude[t] = val;
				latOutlier.remove(0);
			}
		}
	}
	
	/**
	 * 检查有无漂移的点
	 * 经纬度独立地检测
	 * @param obj
	 * @param stampNum
	 * @param lng
	 * @param lat
	 * @return
	 * true：存在漂移的点，漂移的点存放于lng与lat中，按时间顺序
	 * false：不存在
	 */
	public static boolean findOutlier(MyObject obj, int stampNum, ArrayList<Integer> lng, ArrayList<Integer> lat) {
		
		double avgLat = 0, avgLng = 0;
		for (int t = 1; t <= stampNum; t++) {
			avgLng += obj.longitude[t];
			avgLat += obj.latitude[t];
		}
		avgLng /= (double) stampNum;
		avgLat /= (double) stampNum;
		
		for (int t = 1; t <= stampNum; t++) {
			if (Math.abs(obj.longitude[t] - avgLng) >= Filter.OUTLIER_THRESHOLD)
				lng.add(t);
			if (Math.abs(obj.latitude[t] - avgLat) >= Filter.OUTLIER_THRESHOLD)
				lat.add(t);
		}
		
		if (lng.size() + lat.size() > 0)
			return true;
		return false;
	}
}
