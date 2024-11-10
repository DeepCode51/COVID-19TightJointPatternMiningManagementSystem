 package ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * 非法输入的弹窗提示
 * 显示内容为传入的 String msg
 * @author wyqaq
 */
public class MessageDialog extends JDialog {
	public MessageDialog(String msg) {
		
		this.setBounds(0, 0, 250, 150);
		this.setTitle("提示");
		
		double screenWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		double screenHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		this.setLocation((int) ((screenWidth - this.getWidth()) / 2), (int) ((screenHeight - this.getHeight()) / 2));

		
		JPanel panel = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		panel.setLayout(gbl);
		gbc.insets = new Insets(10, 10, 10, 10);
		
		JLabel label = new JLabel(msg);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbl.setConstraints(label, gbc);
		panel.add(label);
		
		JButton button = new JButton("确定");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		gbc.gridy = 1;
		gbl.setConstraints(button, gbc);
		panel.add(button);
		
		this.add(panel);
		this.setVisible(true);
	}
}
