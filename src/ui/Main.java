package ui;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.UnsupportedLookAndFeelException;

import service.algorithm.BFS;
import service.algorithm.CloseContact;
import service.algorithm.Encoder;
import service.algorithm.Filter;
import service.algorithm.MillerProjection;
import service.resource.MyObject;
import service.resource.Read;


/*
 *   在轨迹上表示
 *   坐标 预处理
 */


public class Main {
	public static String FILE_SAVE_PATH = "result\\";
	
	public static int MAX_TIME_STAMP = 100;
	public static int MAX_OBJECT_NUM = 4000;
	
	public static int timeWindowLength = 3;
	public static double maxDistance = 400;
	
	public static ArrayList<Integer> source = new ArrayList<Integer>();
	public static ArrayList<MyObject> objs = new ArrayList<MyObject>();
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		objs = Read.readOriginalData(MAX_OBJECT_NUM, MAX_TIME_STAMP);
		Filter.filterAll(objs, MAX_OBJECT_NUM, MAX_TIME_STAMP);
		MillerProjection.projectAll(objs, MAX_OBJECT_NUM, MAX_TIME_STAMP);
		App app = new App();
	}
	
	/**
	 * 暴力对拍方法
	 * 有滤波过程
	 * @param app
	 * @throws IOException
	 */
	public static double calculateDistanceOnly(App app) throws IOException {
		long bg = System.currentTimeMillis();
		
		Main.source.addAll(app.source);
		Main.timeWindowLength = app.timeWindowLength;
		Main.maxDistance = app.maxDistance;
		Encoder.init(Main.maxDistance);
		BFS.UNVISITED_FLAG = Main.MAX_OBJECT_NUM + 5;
		
		objs = Read.readOriginalData(MAX_OBJECT_NUM, MAX_TIME_STAMP);
		Filter.filterAll(objs, MAX_OBJECT_NUM, MAX_TIME_STAMP);
		CloseContact closeContact = new CloseContact(MAX_OBJECT_NUM);
		closeContact.calc(app, objs, MAX_OBJECT_NUM, MAX_TIME_STAMP, timeWindowLength);
		BFS.bfs(closeContact, objs, MAX_OBJECT_NUM, MAX_TIME_STAMP);
		Collections.sort(objs);
		
		long ed = System.currentTimeMillis();
//		System.out.println("cost = " + (ed - bg) + " ms");
		return (double) (ed - bg);
	}
	
	/**
	 * 添加了geohash优化，没有使用字典树
	 * 有滤波过程
	 * @param app
	 * @throws IOException
	 */
	public static double calculate(App app) throws IOException {
		
		long bg = System.currentTimeMillis();
		
		Main.source.addAll(app.source);
		Main.timeWindowLength = app.timeWindowLength;
		Main.maxDistance = app.maxDistance;
		Encoder.init(Main.maxDistance);
		BFS.UNVISITED_FLAG = Main.MAX_OBJECT_NUM + 5;
		
		objs = Read.readOriginalData(MAX_OBJECT_NUM, MAX_TIME_STAMP);
		Filter.filterAll(objs, MAX_OBJECT_NUM, MAX_TIME_STAMP);
		CloseContact closeContact = new CloseContact(MAX_OBJECT_NUM);
		closeContact.calcWithGeohash(app, objs, MAX_OBJECT_NUM, MAX_TIME_STAMP, timeWindowLength);
		BFS.bfs(closeContact, objs, MAX_OBJECT_NUM, MAX_TIME_STAMP);
		Collections.sort(objs);
		
		long ed = System.currentTimeMillis();
//		System.out.println("cost = " + (ed - bg) + " ms");
		return (double) (ed - bg);
	}
	
	/**
	 * 添加了geohash与字典树优化的方法
	 * 有滤波过程
	 * @param app
	 * @throws IOException
	 */
	public static double calculateWithTrie(App app) throws IOException {
		
		long bg = System.currentTimeMillis();
		
		Main.source.addAll(app.source);
		Main.timeWindowLength = app.timeWindowLength;
		Main.maxDistance = app.maxDistance;
		Encoder.init(Main.maxDistance);
		BFS.UNVISITED_FLAG = Main.MAX_OBJECT_NUM + 5;
		
		objs = Read.readOriginalData(MAX_OBJECT_NUM, MAX_TIME_STAMP);
		Filter.filterAll(objs, MAX_OBJECT_NUM, MAX_TIME_STAMP);
		CloseContact closeContact = new CloseContact(MAX_OBJECT_NUM);
		closeContact.calcWithGeohashAndTrie(app, objs, MAX_OBJECT_NUM, MAX_TIME_STAMP, timeWindowLength);
		BFS.bfs(closeContact, objs, MAX_OBJECT_NUM, MAX_TIME_STAMP);
		
		long ed = System.currentTimeMillis();
//		System.out.println("cost = " + (ed - bg) + " ms");
		return (double) (ed - bg);
	}
	
	public static double calculateWithPreprocess(App app) throws IOException {
		
		long bg = System.currentTimeMillis();
		
		Main.source.addAll(app.source);
		Main.timeWindowLength = app.timeWindowLength;
		Main.maxDistance = app.maxDistance;
		Encoder.init(Main.maxDistance);
		BFS.UNVISITED_FLAG = Main.MAX_OBJECT_NUM + 5;
		
		objs = Read.readEnrichedData(MAX_OBJECT_NUM, MAX_TIME_STAMP);
		// enrich时已经过滤过数据了
//		Filter.filterAll(objs, MAX_OBJECT_NUM, MAX_TIME_STAMP);
		CloseContact closeContact = new CloseContact(MAX_OBJECT_NUM);
		closeContact.calcWithGeohashAndTrie(app, objs, MAX_OBJECT_NUM, MAX_TIME_STAMP, timeWindowLength);
		BFS.bfs(closeContact, objs, MAX_OBJECT_NUM, MAX_TIME_STAMP);
		
		long ed = System.currentTimeMillis();
		return (double) (ed - bg);
	}
	
	public static double calculateWithMap(App app) throws IOException {
		
		long bg = System.currentTimeMillis();
		
		Main.source.addAll(app.source);
		Main.timeWindowLength = app.timeWindowLength;
		Main.maxDistance = app.maxDistance;
		Encoder.init(Main.maxDistance);
		BFS.UNVISITED_FLAG = Main.MAX_OBJECT_NUM + 5;
		
		CloseContact closeContact = new CloseContact(MAX_OBJECT_NUM);
		closeContact.calcWithGeohashAndTrie(app, objs, MAX_OBJECT_NUM, MAX_TIME_STAMP, timeWindowLength);
		BFS.bfs(closeContact, objs, MAX_OBJECT_NUM, MAX_TIME_STAMP);
		
		long ed = System.currentTimeMillis();
		saveFile();
		return (double) (ed - bg);
	}
	
	public static void saveFile() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		String fileName = "result_" + df.format(System.currentTimeMillis()) + ".txt";
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_SAVE_PATH + fileName));
			
			String res = "OID\trank\ttime\tOID_from";
			bw.write(res);
			bw.newLine();
			bw.flush();
			for (int i = 1; i <= MAX_OBJECT_NUM; i++) {
				MyObject obj = objs.get(i);
//				res = Integer.toString(i);
				if (obj.rank == 0)
					res = Integer.toString(i) + "\t" + "Source" + "\t" + "#" + "\t" + "#";
				else if (obj.rank == BFS.UNVISITED_FLAG)
					res = Integer.toString(i) + "\t" + "No" + "\t" + "#" + "\t" + "#";
				else
					res = Integer.toString(i) + "\t" + Integer.toString(obj.rank) + "\t" + Integer.toString(obj.timeBecomeCC) + "\t" + Integer.toString(obj.idFrom);
				bw.write(res);
				bw.newLine();
				bw.flush();
			}
			
			bw.close();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			System.out.println("保存结果失败!!!");
		}
	}
}
