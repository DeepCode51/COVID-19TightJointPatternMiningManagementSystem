package service.algorithm;

import java.util.ArrayList;

import service.resource.MyObject;

/**
 * 字典树
 * @author wyqaq
 */
public class Trie {
	public TrieNode root;
	
	public Trie() {
		root = null;
	}
	
	/**
	 * 从根节点开始寻找
	 * @param str
	 * @return
	 */
	public TrieNode find(String str) {
		int len = str.length(), next;
		TrieNode node = root;
		for (int i = 0; i < len; i++) {
			next = Encoder.charToInt(str.charAt(i));
			if (node.child[next] == null)
				return null;// 子树为空，则不存在对应的对象，返回null
			node = node.child[next];
		}
		return node;// 返回查找到的结点
	}
	
	/**
	 * 添加对象和结点
	 * @param obj
	 */
	public void add(MyObject obj) {
		TrieNode node = root;
		String str = obj.geohashCode;
		int len = str.length(), next;
		for (int i = 0; i < len; i++) {
			next = Encoder.charToInt(str.charAt(i));
			if (node.child[next] == null) { // 先查看该节点是否存在，若不存在则new一个
				node.child[next] = new TrieNode();
			}
			node = node.child[next];
		}
		node.objs.add(obj.id);
	}
	
	/**
	 * 建树
	 * @param objs
	 */
	public void build(ArrayList<MyObject> objs) {
		root = new TrieNode();
		for (MyObject obj : objs) {
			add(obj);
		}
	}
}
