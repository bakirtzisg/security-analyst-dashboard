package edu.vcu.cyber.dashboard.ui.docking;

import javax.swing.*;
import java.awt.*;

public class DockingTest extends JFrame
{


	public static void main(String[] args)
	{
		new DockingTest().test();
	}


	public void test()
	{
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		JButton meh = new JButton("Hello");
		meh.setPreferredSize(new Dimension(200, 100));
		JButton meh2 = new JButton("sdfgsdg");
		meh.setPreferredSize(new Dimension(200, 100));

		DockingPanel contentPane = new DockingPanel(new DockWrapper(meh, "Something"), null);

		JComponent tmp = new DockWrapper(new JTextField("World"), "else");
		contentPane.dock(tmp, 4);
		contentPane.undock(tmp);

		frame.setContentPane(contentPane);

		frame.pack();
		frame.setVisible(true);
	}

}
