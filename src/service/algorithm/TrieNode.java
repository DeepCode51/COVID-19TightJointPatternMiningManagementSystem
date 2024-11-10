package service.algorithm;

import java.util.ArrayList;

/**
 * trie树结点
 * @author wyqaq
 */
public class TrieNode {
	public ArrayList<Integer> objs;// 该结点对应的对象的OID
	public TrieNode[] child;// 儿子
	
	public TrieNode() {
		objs = new ArrayList<Integer>();
		child = new TrieNode[Encoder.BASE_LENGTH];
		for (int i = 0; i < Encoder.BASE_LENGTH; i++)
			child[i] = null;
	}
}
