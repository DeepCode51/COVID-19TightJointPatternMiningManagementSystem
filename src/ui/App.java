package ui;

import java.util.ArrayList;
import java.util.Collections;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
//import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
//import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

import javax.swing.BorderFactory;
//import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
//import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;

import service.algorithm.BFS;
import service.resource.MyObject;

public class App {
	
	
	public static String ILLEGAL_TIME_WINDOW_MESSAGE = "时间窗长度必须为正整数";
	public static String ILLEGAL_DISTANCE_MESSAGE = "距离阈值必须为正实数";
	public static String NONE_SOURCE_MESSAGE = "没有初始感染源";
	public static String TIME_COST_MESSAGE = "计算时间为：%.3f 秒";
	public static String FRAME_TITLE = "密接模式挖掘";
	
	public JFrame frame;
	public JPanel leftPanel;
	public JPanel midPanel;
	public JPanel rightPanel;
	
	public DrawPanel drawPanel;
	public JTextArea resultArea;
	public JTextArea sourceArea;
	public JScrollPane scrollPane;
	public JTextField sourceField;
	public JTextField alphaField;
	public JTextField betaField;
	public JScrollBar scrBar;
	public JProgressBar proBar;
	public JComboBox<String> choiceBox;
	
	public ArrayList<Integer> source;
	public String timeWindowText;
	public String distanceText;
//	public ArrayList<Point> points;
//	public ArrayList<Line> segs;
	
	public int timeWindowLength;
	public double maxDistance;
	public int maxRank;

	public ArrayList<Point> points;// = new ArrayList<Point>();
	public ArrayList<Line> segs;// = new ArrayList<Line>();
	public ArrayList<IdTag> tags;
	
	public static String EMPTY_ITEM_TEXT = "空";
	public static String SOURCE_ITEM_TEXT = "感染源";
	public static String SUMMARY_ITEM_TEXT = "概览";
	
	public App() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		timeWindowText = "3";
		distanceText = "400";
		
		source = new ArrayList<Integer>();
		source.add(121);
		source.add(436);
		
		Main.source.addAll(source);
		
		segs = new ArrayList<Line>();
		points = new ArrayList<Point>();
		tags = new ArrayList<IdTag>();
		
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {
			String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
			UIManager.setLookAndFeel(lookAndFeel);
		}
		
		buildGUI(this);
		drawSourcesOnly();
	}
	
	/**
	 * 添加并设置所有组件
	 * 左半边放置在leftPanel
	 * 中间的放置在midPanel
	 * 右半边防止在rightPanel
	 * Layout为GridBagLayout，使用GridBagConstraint添加约束
	 * @param app
	 */
	public void buildGUI(App app) {
		frame = new JFrame(FRAME_TITLE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		
		leftPanel = new JPanel();
		midPanel = new JPanel();
		rightPanel = new JPanel();
		GridBagLayout leftGBL = new GridBagLayout();
		GridBagLayout midGBL = new GridBagLayout();
		GridBagLayout rightGBL = new GridBagLayout();
		leftPanel.setLayout(leftGBL);
		midPanel.setLayout(midGBL);
		rightPanel.setLayout(rightGBL);
		
		
		
		/*
		 * leftPanel
		 */
		drawPanel = new DrawPanel(points, segs, tags);
		drawPanel.setPreferredSize(new Dimension(700, 700));
		leftGBL.setConstraints(drawPanel, gbc);
		leftPanel.add(drawPanel);
		
		
		
		/*
		 * midPanel
		 */
		gbc.gridheight = 1;
		gbc.gridwidth = 1;

		JLabel resultLabel = new JLabel("结果：");
		resultLabel.setPreferredSize(new Dimension(100, 30));
		resultLabel.setFont(new Font(resultLabel.getFont().getName(), resultLabel.getFont().getStyle(), 18));
		gbc.gridx = 0;
		gbc.gridy = 0;
		midGBL.setConstraints(resultLabel, gbc);
		midPanel.add(resultLabel);
		
		gbc.gridheight = 1;
		gbc.gridwidth = 2;
		
		choiceBox = new JComboBox<String>();
		choiceBox.addItem(EMPTY_ITEM_TEXT);
		choiceBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				// 只处理选中的状态
				if (e.getStateChange() == ItemEvent.SELECTED) {
					String itemText = (String) choiceBox.getSelectedItem();
					if (itemText.equals(EMPTY_ITEM_TEXT)) {
						resultArea.setText("空");
						drawSourcesOnly();
//						drawSingleObject(Main.source.get(0));
					} else if (itemText.equals(SUMMARY_ITEM_TEXT)) {
						try {
							displaySummary();
							drawSourcesOnly();
//							drawSingleObject(Main.source.get(0));
						} catch (CloneNotSupportedException e1) {
							// TODO 自动生成的 catch 块
							e1.printStackTrace();
						}
					} else if (itemText.equals(SOURCE_ITEM_TEXT)) {
						displaySource();
						drawSourcesOnly();
//						drawSingleObject(Main.source.get(0));
					} else {
						String[] strcol = itemText.split(" ");
						displaySingleObject(Integer.parseInt(strcol[0]), itemText);
						drawSingleObject(Integer.parseInt(strcol[0]));
					}
					setScrollAdaptively();
				}
			}
		});
		choiceBox.setPreferredSize(new Dimension(150, 30));
		gbc.gridx = 1;
		gbc.gridy = 0;
		midGBL.setConstraints(choiceBox, gbc);
		midPanel.add(choiceBox);
		
		gbc.gridheight = 1;
		gbc.gridwidth = 3;
		
		resultArea = new JTextArea("空");
		resultArea.setFont(new Font(resultArea.getFont().getName(), resultArea.getFont().getStyle(), 16));
		resultArea.setTabSize(6);
		resultArea.setBackground(frame.getBackground());
		resultArea.setPreferredSize(new Dimension(330, 7000));
		RoundBorder resultRoundBorder = new RoundBorder();
		resultArea.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		scrollPane = new JScrollPane();
		scrollPane.setBorder(BorderFactory.createCompoundBorder(resultRoundBorder, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		scrBar = new JScrollBar(JScrollBar.VERTICAL, 0, 0, 0, 7000);
		scrollPane.setVerticalScrollBar(scrBar);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setWheelScrollingEnabled(true);
		scrollPane.setViewportView(resultArea);
		scrollPane.setPreferredSize(new Dimension(330, 400));
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridheight = 10;
		midGBL.setConstraints(scrollPane, gbc);
		midPanel.add(scrollPane);
		gbc.gridheight = 1;
		
		proBar = new JProgressBar();
		proBar.setPreferredSize(new Dimension(300, 30));
		gbc.gridx = 0;
		gbc.gridy = 11;
		midGBL.setConstraints(proBar, gbc);
		proBar.setMaximum(100);
		midPanel.add(proBar);
		
		
		
		/*
		 * rightPanel
		 */
		JLabel sourceLabel = new JLabel("感染源：");
		sourceLabel.setPreferredSize(new Dimension(300, 30));
		sourceLabel.setFont(new Font(sourceLabel.getFont().getName(), sourceLabel.getFont().getStyle(), 18));
		gbc.gridx = 0;
		gbc.gridy = 0;
		rightGBL.setConstraints(sourceLabel, gbc);
		rightPanel.add(sourceLabel);
		
		sourceArea = new JTextArea("121\t436");
		sourceArea.setFont(new Font(sourceArea.getFont().getName(), sourceArea.getFont().getStyle(), 16));
		sourceArea.setTabSize(6);
		sourceArea.setBackground(Color.WHITE);
		sourceArea.setBorder(BorderFactory.createEmptyBorder(10,  10,  10, 10));
		sourceArea.setPreferredSize(new Dimension(300, 200));
		gbc.gridx = 0;
		gbc.gridy = 1;
		rightGBL.setConstraints(sourceArea, gbc);
		rightPanel.add(sourceArea);
		
		JLabel newSourceLabel = new JLabel("添加新感染源(任意符号分隔)：");
		newSourceLabel.setPreferredSize(new Dimension(300, 30));
		newSourceLabel.setFont(new Font(newSourceLabel.getFont().getName(), newSourceLabel.getFont().getStyle(), 18));
		gbc.gridx = 0;
		gbc.gridy = 2;
		rightGBL.setConstraints(newSourceLabel, gbc);
		rightPanel.add(newSourceLabel);
		
		sourceField = new JTextField("", 0);
		sourceField.setPreferredSize(new Dimension(300, 30));
		gbc.gridx = 0;
		gbc.gridy = 3;
		rightGBL.setConstraints(sourceField, gbc);
		rightPanel.add(sourceField);
		
		gbc.gridwidth = 1;
		
		JButton addButton = new JButton("添加");
		addButton.setFont(new Font(addButton.getFont().getName(), addButton.getFont().getStyle(), 16));
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addSource();
			}
		});
		addButton.setPreferredSize(new Dimension(100, 30));
		gbc.gridx = 0;
		gbc.gridy = 4;
		rightGBL.setConstraints(addButton, gbc);
		rightPanel.add(addButton);
		
		JButton clearButton = new JButton("清空");
		clearButton.setFont(new Font(clearButton.getFont().getName(), clearButton.getFont().getStyle(), 16));
		clearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearSource();
			}
		});
		clearButton.setPreferredSize(new Dimension(100, 30));
		gbc.gridx = 1;
		gbc.gridy = 4;
		rightGBL.setConstraints(clearButton, gbc);
		rightPanel.add(clearButton);
		
		JLabel alphaLabel = new JLabel("时间窗长度：");
		alphaLabel.setPreferredSize(new Dimension(150, 30));
		alphaLabel.setFont(new Font(alphaLabel.getFont().getName(), alphaLabel.getFont().getStyle(), 18));
		gbc.gridx = 0;
		gbc.gridy = 5;
		rightGBL.setConstraints(alphaLabel, gbc);
		rightPanel.add(alphaLabel);
		
		JLabel betaLabel = new JLabel("距离阈值：");
		betaLabel.setPreferredSize(new Dimension(150, 30));
		betaLabel.setFont(new Font(betaLabel.getFont().getName(), betaLabel.getFont().getStyle(), 18));
		gbc.gridx = 1;
		gbc.gridy = 5;
		rightGBL.setConstraints(betaLabel, gbc);
		rightPanel.add(betaLabel);
		
		alphaField = new JTextField("3", 0);
		alphaField.setPreferredSize(new Dimension(150, 30));
		gbc.gridx = 0;
		gbc.gridy = 6;
		rightGBL.setConstraints(alphaField, gbc);
		rightPanel.add(alphaField);
		
		betaField = new JTextField("400", 0);
		betaField.setPreferredSize(new Dimension(150, 30));
		gbc.gridx = 1;
		gbc.gridy = 6;
		rightGBL.setConstraints(betaField, gbc);
		rightPanel.add(betaField);
		
		gbc.gridwidth = 2;
		JButton calButton = new JButton("计 算");
		calButton.setFont(new Font(calButton.getFont().getName(), calButton.getFont().getStyle(), 16));
		calButton.setPreferredSize(new Dimension(150, 30));
		calButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					display();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		gbc.gridx = 0;
		gbc.gridy = 7;
		rightGBL.setConstraints(calButton, gbc);
		rightPanel.add(calButton);
		
		GridBagLayout mainGBL = new GridBagLayout();
		frame.setLayout(mainGBL);
		GridBagConstraints mainGBC = new GridBagConstraints();
		mainGBC.gridx = 0;
		mainGBC.gridy = 0;
		mainGBL.setConstraints(leftPanel, mainGBC);
		frame.add(leftPanel);
		mainGBC.gridx = 3;
		mainGBL.setConstraints(midPanel, mainGBC);
		frame.add(midPanel);
		mainGBC.gridx = 5;
		mainGBL.setConstraints(rightPanel, mainGBC);
		frame.add(rightPanel);
		frame.setSize(1500, 800);
//		frame.pack();
		double screenWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		double screenHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		frame.setLocation((int) ((screenWidth - frame.getWidth()) / 2), (int) ((screenHeight - frame.getHeight()) / 2));
		frame.setVisible(true);
	}
	
	/**
	 * 清空所有的source
	 */
	public void clearSource() {
		sourceArea.setText("空");
		source.clear();
	}
	
	/**
	 * 以任意的非数字字符分割
	 * 去除非法输入（负数，编号不存在的整数等）
	 * 非法输入的newSource无法加入source中，但不会提示错误
	 */
	public void addSource() {
		String text = "";
		try {
			text = sourceField.getText();
		} catch (NullPointerException e) {
			return;
		}
		int x = 0;
		int len = text.length();
		ArrayList<Integer> newSource = new ArrayList<Integer>();
		for (int i = 0; i < len; i++) {
			if (Character.isDigit(text.charAt(i))) {
				x = x * 10 + text.charAt(i) - '0';
			}
			else if (x > 0 && x <= Main.MAX_OBJECT_NUM) {
				newSource.add(x);
				x = 0;
			}
		}
		if (x > 0 && x <= Main.MAX_OBJECT_NUM)
			newSource.add(x);
		
		source.addAll(newSource);
		Collections.sort(source);
		int size = source.size();
		if (size > 1) {
			for (int i = size - 1; i > 0; i--) {
				if ((int) source.get(i) == (int) source.get(i - 1)) {
					source.remove(i);
				}
			}
		}
		
		size = source.size();
		text = "";
		for (int i = 0; i < size; i++) {
			if (i > 0 && i % 5 == 0) {
				text += "\n";
			}
			text += Integer.toString(source.get(i)) + "\t";
		}
		sourceArea.setText(text);
		
		sourceField.setText("");
	}
	
	/**
	 * 点击计算要调用此函数
	 * 然后检查alpha, beta值是否合法
	 * 对于非法输入有弹窗提示
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void display() throws IOException {
		proBar.setEnabled(true);
		proBar.setValue(0);
		
		timeWindowLength = 0;
		timeWindowText = alphaField.getText();
		try {
			timeWindowLength = Integer.parseInt(timeWindowText);
		} catch (NumberFormatException e) {
			new MessageDialog(App.ILLEGAL_TIME_WINDOW_MESSAGE);
			return;
		}
		if (timeWindowLength <= 0) {
			new MessageDialog(App.ILLEGAL_TIME_WINDOW_MESSAGE);
			return;
		}
		
		maxDistance = 0;
		distanceText = betaField.getText();
		try {
			maxDistance = Double.parseDouble(distanceText);
		} catch (NumberFormatException e) {
			new MessageDialog(App.ILLEGAL_DISTANCE_MESSAGE);
			return;
		}
		if (maxDistance <= 0) {
			new MessageDialog(App.ILLEGAL_DISTANCE_MESSAGE);
			return;
		}
		
		if (source.isEmpty()) {
			new MessageDialog(App.NONE_SOURCE_MESSAGE);
			return;
		}
		
		double runTime;// 单位：毫秒
//		runTime = Main.calculateDistanceOnly(this);
//		runTime = Main.calculate(this);
//		runTime = Main.calculateWithTrie(this);
//		runTime = Main.calculateWithPreprocess(this);
		runTime = Main.calculateWithMap(this);
		
		choiceBox.removeAllItems();
		choiceBox.addItem(SUMMARY_ITEM_TEXT);
		choiceBox.addItem(SOURCE_ITEM_TEXT);
		String itemText = "";
		for (int i = 1; i <= Main.MAX_OBJECT_NUM; i++) {
			itemText = Integer.toString(i) + " 号对象";
			choiceBox.addItem(itemText);
		}

//		drawSingleObject(121);
		new MessageDialog(String.format(App.TIME_COST_MESSAGE, runTime / 1000D));
	}
	
	/**
	 * 自适应地决定是否显示滚动条
	 * 并更改滚动条范围
	 */
	public void setScrollAdaptively() {
		int lineCnt = resultArea.getLineCount();
		if (lineCnt > 15) {
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			scrBar.setMaximum(lineCnt);
			resultArea.setPreferredSize(new Dimension(300, lineCnt * 25));
		} else {
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		}
		// 光标移动到最前，使得滚动条处于顶端
		resultArea.setCaretPosition(0);
//		scrBar.setValue(scrBar.getMaximum());
	}
	
	/**
	 * 展示概况
	 * @throws CloneNotSupportedException
	 */
	public void displaySummary() throws CloneNotSupportedException {
		@SuppressWarnings("unchecked")
		ArrayList<MyObject> objsCopy = (ArrayList<MyObject>) Main.objs.clone();
		Collections.sort(objsCopy);
		resultArea.setText("感染源：\n");
		String result = "";
		maxRank = 0;
		int rk = 0, cnt = 0;
		boolean lastLineFlag = false;
		for (MyObject obj : objsCopy) {
			if (obj.rank != rk) {
				if (obj.rank == BFS.UNVISITED_FLAG)
					break;
				if (cnt != 0) {
					result += "\n";
					resultArea.append(result);
					result = "";
				}
				result += Integer.toString(obj.rank) + " 度密接：\n";
				cnt = 0;
				rk = obj.rank;
				maxRank = rk;
				resultArea.append(result);
				result = "";
			}
			if (cnt < 4) {
				result += Integer.toString(obj.id) + "\t";
				cnt++;
				lastLineFlag = false;
			} else {
				result += Integer.toString(obj.id) + "\n";
				resultArea.append(result);
				result = "";
				cnt = 0;
				lastLineFlag = true;
			}
		}
		if (!lastLineFlag) {
			resultArea.append(result);
		}
	}
	
	/**
	 * 展示感染源对象的id
	 */
	public void displaySource() {
		resultArea.setText("感染源：\n");
		String result = "";
		int cnt = 0;
		for (Integer src : Main.source) {
			if (cnt < 4) {
				result += Integer.toString(src) + "\t";
				cnt++;
			} else {
				result += Integer.toString(src) + "\n";
				cnt = 0;
			}
		}
		resultArea.append(result);
	}
	
	/**
	 * 展示单个对象的详细信息
	 * @param index
	 * @param itemText
	 */
	public void displaySingleObject(int index, String itemText) {
		MyObject obj = Main.objs.get(index);
		if (obj.rank == BFS.UNVISITED_FLAG) {
			String titleText = itemText + ": 非密接\n";
			resultArea.setText(titleText);
		} else if (obj.rank == 0){
			String titleText = itemText + ": 感染源\n";
			resultArea.setText(titleText);
		} else {
			String titleText = itemText + ": " + Integer.toString(obj.rank) + " 度密接\n";
			resultArea.setText(titleText);
			resultArea.append("成为密接的时间: " + Integer.toString(obj.timeBecomeCC) + "\n");
			resultArea.append(String.format("被%d号对象感染\n", obj.idFrom));
		}
		resultArea.append("轨迹如下:\n");
		for (int t = 1; t <= Main.MAX_TIME_STAMP; t++) {
			resultArea.append(String.format("经度: %.6f   纬度: %.6f\n", obj.longitude[t], obj.latitude[t]));
		}
	}
	
//	public void drawSource() {
//		for (MyObject obj : Main.objs) {
//			if (obj.rank == 1) {
//				
//			}
//		}
//	}
	
	public void drawSourcesOnly() {
		points.clear();
		segs.clear();
		tags.clear();
		for (Integer index : Main.source) {
			MyObject obj = Main.objs.get(index);
			
			ArrayList<Point> p = new ArrayList<Point>();
			ArrayList<Line> l = new ArrayList<Line>();
//			ArrayList<IdTag> tg = new ArrayList<IdTag>();
			
			for (int t = 1; t <= Main.MAX_TIME_STAMP; t++)
				p.add(new Point(obj.x[t], obj.y[t], Color.RED));
			for (int i = 1; i < Main.MAX_TIME_STAMP; i++)
				l.add(new Line(p.get(i - 1), p.get(i), Color.RED));

			tags.add(new IdTag(points.size(), obj.id));
			points.addAll(p);
			segs.addAll(l);
		}
		drawPanel.repaint();
	}
	
	public void drawSingleObject(int index) {
		MyObject obj = Main.objs.get(index);
//		MyObject obj = Main.objs.get(index);
		
		points.clear();
		segs.clear();
		tags.clear();
		
		if (obj.rank == BFS.UNVISITED_FLAG) {
			for (int t = 1; t <= Main.MAX_TIME_STAMP; t++) {
				points.add(new Point(obj.x[t], obj.y[t], Color.GREEN));
			}
			for (int i = 1; i < Main.MAX_TIME_STAMP; i++) {
				segs.add(new Line(points.get(i - 1), points.get(i), Color.GREEN));
			}
			tags.add(new IdTag(0, obj.id));
			drawPanel.repaint();
			return;
		}
		
		for (int t = 1; t <= Main.MAX_TIME_STAMP; t++) {
			points.add(new Point(obj.x[t], obj.y[t], Color.RED));
		}
		for (int i = 1; i < Main.MAX_TIME_STAMP; i++) {
			segs.add(new Line(points.get(i - 1), points.get(i), Color.RED));
		}
		tags.add(new IdTag(0, obj.id));
		
		int size1, size2, ext = 1;
		for (MyObject o : Main.objs) {
			size1 = points.size();
			if (o.idFrom == index) {
				int t1 = Math.max(1, o.timeBecomeCC - Main.timeWindowLength + 1 - ext);
				int t2 = Math.min(Main.MAX_TIME_STAMP, o.timeBecomeCC + ext);
				for (int t = t1; t <= t2; t++)
					points.add(new Point(o.x[t], o.y[t], Color.BLUE));
			} else if (o.id == obj.idFrom) {
				int t1 = Math.max(1, obj.timeBecomeCC - Main.timeWindowLength + 1 - ext);
				int t2 = Math.min(Main.MAX_TIME_STAMP, obj.timeBecomeCC + ext);
				for (int t = t1; t <= t2; t++)
					points.add(new Point(o.x[t], o.y[t], Color.ORANGE));
			}
			size2 = points.size();
			if (size1 < size2) {
				tags.add(new IdTag(size1, o.id));
				for (int i = size1 + 1; i < size2; i++) {
					segs.add(new Line(points.get(i - 1), points.get(i), points.get(i).getC()));
				}
			}
		}
		
//		drawPanel = new DrawPanel(points, segs);
//		drawPanel.points = points;
//		drawPanel.segs = segs;
		drawPanel.repaint();
//		System.out.println("repaint");
	}
	
//	public void drawSource(int index) {
//		
//	}
}
