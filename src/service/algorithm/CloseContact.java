package service.algorithm;

import java.util.ArrayList;
import java.util.HashSet;

import service.resource.MyObject;
import ui.App;
import ui.Main;

/**
 * 滑动窗口挖掘密接关系，建图
 * @author wyqaq
 */
public class CloseContact {
	public static int[][] NEIGHBORHOOD = {{1, 1}, {1, 0}, {1, -1},
			{0, 1}, {0, -1}, {-1, 1}, {-1, 0}, {-1, -1}
	};
	
	public int[][] contact;
	
	public CloseContact(int objNum) {
		contact = new int[objNum + 5][objNum + 5];
	}
	
	/**
	 * 任意两点都计算距离
	 * 不作任何优化
	 * @param app
	 * @param objs
	 * @param objNum
	 * @param stampNum
	 * @param windowLen
	 */
	public void calc(App app, ArrayList<MyObject> objs, int objNum, int stampNum, int windowLen) {
		
		int[][] count = new int[objNum + 5][objNum + 5];
		for (int t = 1; t <= stampNum; t++) {
			Encoder.geohashAll(objs, t);
			
			for (int i = 1; i <= objNum; i++) {
				HashSet<String> neighbor = new HashSet<String>();
				neighbor.add(objs.get(i).geohashCode);
				for (int k = NEIGHBORHOOD.length - 1; k >= 0; k--) {
					neighbor.add(Encoder.geohash(objs.get(i).longitude[t] + NEIGHBORHOOD[k][0] * Encoder.longitudeUnit,
							objs.get(i).latitude[t] + NEIGHBORHOOD[k][1] * Encoder.latitudeUnit));
				}
				for (int j = i + 1; j <= objNum; j++) {
					if (objs.get(i).calcDistance(objs.get(j), t) <= Main.maxDistance) {
						count[i][j]++;
						if (count[i][j] >= windowLen) {
							contact[i][j] = t;
							contact[j][i] = t;
						}
					}
					else count[i][j] = 0;
				}
			}
			
			for (int i = 1; i <= objNum; i++) {
				for (int j = 1; j <= objNum; j++) {
					if (i == j)
						continue;
					if (contact[i][j] == t) {
						objs.get(i).edge.get(t).add(j);
					}
				}
			}
			
			app.proBar.setValue(t);
			app.proBar.paintImmediately(0, 0, app.proBar.getSize().width, app.proBar.getSize().height);
		}
	}
	
	/**
	 * 增加了geohash编码
	 * @param app
	 * @param objs
	 * @param objNum
	 * @param stampNum
	 * @param windowLen
	 */
	public void calcWithGeohash(App app, ArrayList<MyObject> objs, int objNum, int stampNum, int windowLen) {
		
		int[][] count = new int[objNum + 5][objNum + 5];
		
		for (int t = 1; t <= stampNum; t++) {
			Encoder.geohashAll(objs, t);
			
			for (int i = 1; i <= objNum; i++) {
				HashSet<String> neighbor = new HashSet<String>();
				neighbor.add(objs.get(i).geohashCode);
				for (int k = NEIGHBORHOOD.length - 1; k >= 0; k--) {
					neighbor.add(Encoder.geohash(objs.get(i).longitude[t] + NEIGHBORHOOD[k][0] * Encoder.longitudeUnit,
							objs.get(i).latitude[t] + NEIGHBORHOOD[k][1] * Encoder.latitudeUnit));
				}
				for (int j = i + 1; j <= objNum; j++) {
					if (objs.get(i).isClose(objs.get(j), t, neighbor)) {
						count[i][j]++;
						if (count[i][j] >= windowLen) {
							contact[i][j] = t;
							contact[j][i] = t;
						}
					}
					else count[i][j] = 0;
				}
			}
			
			for (int i = 1; i <= objNum; i++) {
				for (int j = 1; j <= objNum; j++) {
					if (i == j)
						continue;
					if (contact[i][j] == t) {
						objs.get(i).edge.get(t).add(j);
					}
				}
			}
			
			app.proBar.setValue(t);
			app.proBar.paintImmediately(0, 0, app.proBar.getSize().width, app.proBar.getSize().height);
		}
	}
	
	/**
	 * 增加了geohash编码和字典树
	 * @param app
	 * @param objs
	 * @param objNum
	 * @param stampNum
	 * @param windowLen
	 */
	public void calcWithGeohashAndTrie(App app, ArrayList<MyObject> objs, int objNum, int stampNum, int windowLen) {
		
		int[][] count = new int[objNum + 5][objNum + 5];
		
		for (int t = 1; t <= stampNum; t++) {
			Encoder.geohashAll(objs, t);// 所有对象geohash编码
			Trie trie = new Trie();
			trie.build(objs);// 建trie树
			
			for (int i = 1; i <= objNum; i++) {
				
				HashSet<String> neighbor = new HashSet<String>();// 所在格子与周围邻格的geohash值
				neighbor.add(objs.get(i).geohashCode);
				for (int k = NEIGHBORHOOD.length - 1; k >= 0; k--) {
					neighbor.add(Encoder.geohash(
							objs.get(i).longitude[t]
									+ NEIGHBORHOOD[k][0] * Encoder.longitudeUnit,
							objs.get(i).latitude[t]
									+ NEIGHBORHOOD[k][1] * Encoder.latitudeUnit
					));
				}
				// isVisit=true表示被松弛过
				boolean[] isVisit = new boolean[objNum + 5];
				for (String str : neighbor) {
					TrieNode node = trie.find(str);
					if (node != null) {
						for (Integer j : node.objs) {
							if (j > i && objs.get(i).isClose(objs.get(j), t)) {
								count[i][j]++;
								if (count[i][j] >= windowLen) {
									contact[i][j] = t;
									contact[j][i] = t;
								}
								isVisit[j] = true;
							}
						}
					}
				}
				// 没有被松弛过的点，将count改为0，强制时间窗必须连续
				for (int j = i + 1; j <= objNum; j++) {
					if (!isVisit[j]) {
						count[i][j] = 0;
					}
				}
			}
			// 将边加入邻接表
			for (int i = 1; i <= objNum; i++) {
				for (int j = 1; j <= objNum; j++) {
					if (i == j)
						continue;
					if (contact[i][j] == t) {
						objs.get(i).edge.get(t).add(j);
					}
				}
			}
			
			// 进度条
			app.proBar.setValue(t);
			app.proBar.paintImmediately(0, 0, app.proBar.getSize().width, app.proBar.getSize().height);
		}
	}
}
