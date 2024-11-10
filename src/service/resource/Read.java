package service.resource;

import java.io.*;
import java.util.ArrayList;

import service.algorithm.Encoder;
import service.algorithm.Filter;
import service.algorithm.MillerProjection;
import ui.Main;

/**
 * 读入数据集
 * 数据集格式：
 * 对象,时间戳,经度,纬度
 * @author wyqaq
 */
public class Read {
	
	public static String ORIGINAL_FILE_PATH = "data\\data.txt";
	public static String ENRICHED_FILE_PATH = "data\\data_enriched.txt";
//	public static String ENRICHED_LINE_FORMAT = "%.6f,%.6f,%.6f,%.6f,%s";
	public static String ENRICHED_LINE_FORMAT = "%.6f,%.6f,%.6f,%.6f";
	
	// format: lng,lat,x,y,geohash
	
	public static ArrayList<MyObject> readOriginalData(int objNum, int stampNum) throws IOException {
		String strbuff;
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(ORIGINAL_FILE_PATH)));
		
		ArrayList<MyObject> objs = new ArrayList<MyObject>();
		objs.add(new MyObject(0));
		
		for (int i = 1; i <= objNum; i++) {
			MyObject obj = new MyObject(i);
			for (int t = 1; t <= stampNum; t++) {
				strbuff = br.readLine();
				String[] data = strbuff.split(",");
				obj.longitude[t] = Double.parseDouble(data[2]);
				obj.latitude[t] = Double.parseDouble(data[3]);
			}
			objs.add(obj);
		}
		
		br.close();
		return objs;
	}
	
	public static ArrayList<MyObject> readEnrichedData(int objNum, int stampNum) throws IOException {
		String strbuff;
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(ENRICHED_FILE_PATH)));
		
		ArrayList<MyObject> objs = new ArrayList<MyObject>();
		objs.add(new MyObject(0));
		
		for (int i = 1; i <= objNum; i++) {
			MyObject obj = new MyObject(i);
			for (int t = 1; t <= stampNum; t++) {
				strbuff = br.readLine();
				String[] data = strbuff.split(",");
				obj.longitude[t] = Double.parseDouble(data[0]);
				obj.latitude[t] = Double.parseDouble(data[1]);
				obj.x[t] = Double.parseDouble(data[2]);
				obj.y[t] = Double.parseDouble(data[3]);
//				obj.geo[t] = data[4];
			}
			objs.add(obj);
		}
		
		br.close();
		return objs;
	}
	
	public static void enrich(ArrayList<MyObject> objs, int objNum, int stampNum) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(Read.ENRICHED_FILE_PATH));
		String strbuff, geohashCode = "";
		
		for (int i = 1; i <= objNum; i++) {
			MyObject obj = objs.get(i);
			MillerProjection.projectAll(obj, stampNum);
			for (int t = 1; t <= stampNum; t++) {
				geohashCode = Encoder.geohash(obj.longitude[t], obj.latitude[t]);
//				strbuff = String.format(Read.ENRICHED_LINE_FORMAT,
//						obj.longitude[t], obj.latitude[t], obj.x[t], obj.y[t], geohashCode);
				strbuff = String.format(Read.ENRICHED_LINE_FORMAT,
						obj.longitude[t], obj.latitude[t], obj.x[t], obj.y[t]);
				
				bw.write(strbuff);
				bw.newLine();
				bw.flush();
			}
		}
		
		bw.close();
	}
	
	public static void main(String[] ags) throws IOException {
		ArrayList<MyObject> objs = Read.readOriginalData(Main.MAX_OBJECT_NUM, Main.MAX_TIME_STAMP);
//		Encoder.init(Main.maxDistance);
		Filter.filterAll(objs, Main.MAX_OBJECT_NUM, Main.MAX_TIME_STAMP);
		Read.enrich(objs, Main.MAX_OBJECT_NUM, Main.MAX_TIME_STAMP);
		System.out.println("over");
	}
}
