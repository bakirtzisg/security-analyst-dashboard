package edu.vcu.cyber.dashboard.ui.custom;

import javax.swing.*;
import java.awt.*;

public class QuickFrame extends JFrame
{


	public QuickFrame(JPanel container)
	{
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setContentPane(container);
		pack();
	}

	public QuickFrame(JPanel container, int width, int height)
	{
		this(container);
//		setPreferredSize(new Dimension(width, height));
		setSize(width, height);
	}

	public void display()
	{
		setVisible(true);
	}

}
