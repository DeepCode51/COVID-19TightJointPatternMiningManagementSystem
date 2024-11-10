package service.algorithm;

import java.util.ArrayList;

import service.resource.MyObject;
import ui.Main;

/**
 * Encoder
 * 采用geohash编码
 * @author wyqaq
 */
public class Encoder {
	
	public static String BASE_32 = "0123456789bcdefghjkmnpqrstuvwxyz";
	// 0123456789   bcdefgh   jk     mn     pqrstuvwxyz
	// 0 - 9        10 - 16   17-18  19-20  21-31
	public static int BASE_LENGTH = 32;
//	public static int MAX_ENCODING_LENGTH = 40;
//	public static int MAX_SINGLE_ENCODING_LENGTH = 20;
	
	// 不同编码位数对应的误差，单位：米
	public static double[] DISTANCE_LEVEL = {
			0, 2500000, 630000, 78000, 20000, 2400, 610, 76, 19
	};
	
	public static int encodingLength;
	public static int binaryEncodingLength;
	public static int parsingLength;
	public static double longitudeUnit;
	public static double latitudeUnit;
	
	public static int charToInt(char ch) {
		if (Character.isDigit(ch))
			return (int) (ch - '0');
		if (ch < 'i')
			return (int) (ch - 'b') + 10;
		if (ch < 'l')
			return (int) (ch - 'j') + 17;
		if (ch < 'o')
			return (int) (ch - 'm') + 19;
		return (int) (ch - 'p') + 21;
	}
	
	/**
	 * 确定当前阈值（精度）范围内需要的编码位数
	 * @param dis
	 */
	public static void init(double dis) {
		for (int i = DISTANCE_LEVEL.length - 1; i >= 0; i--) {
			if (dis <= DISTANCE_LEVEL[i]) {
				encodingLength = i;
				binaryEncodingLength = encodingLength * 5;
				parsingLength = (binaryEncodingLength + 1) >> 1;
				int latBit = binaryEncodingLength >> 1;
				latitudeUnit = 180D / (double) (1 << latBit);
				int lngBit = binaryEncodingLength - latBit;
				longitudeUnit = 360D / (double) (1 << lngBit);
				break;
			}
		}
	}
	
	public static void geohashAll(ArrayList<MyObject> objs, int curr) {
		for (int i = 1; i <= Main.MAX_OBJECT_NUM; i++) {
			objs.get(i).geohashCode = new String(geohash(objs.get(i).longitude[curr], objs.get(i).latitude[curr]));
		}
	}
	
	/**
	 * geohash编码单个经纬度坐标
	 * @param lng
	 * @param lat
	 * @return
	 */
	public static String geohash(double lng, double lat) {
		String strcode = "";
		int[] bcode = new int[binaryEncodingLength + 1];
		encode(bcode, -180.0, 180.0, lng, 0);// 经度
		encode(bcode, -90.0, 90.0, lat, 1);// 纬度
		
		for (int i = 0, p; i + 4 < binaryEncodingLength; i += 5) {
			p = 0;
			for (int j = 0; j < 5; j++)
				p = ((p << 1) | bcode[i + j]);
			strcode += BASE_32.charAt(p);
		}
		
		return strcode;
	}
	
	/**
	 * 统一了经纬度的编码转换过程；
	 * 位于数轴左区间则编码为0，右区间为1
	 * @param code
	 * @param l
	 * @param r
	 * @param val
	 * @param tag
	 */
	public static void encode(int[] code, double l, double r, double val, int tag) {
		double mid;
		for (int i = 0; i < parsingLength; i++) {
			mid = (l + r) / 2;
			if (val < mid) {
				code[(i << 1) | tag] = 0;
				r = mid;
			} else {
				code[(i << 1) | tag] = 1;
				l = mid;
			}
		}
	}
}
