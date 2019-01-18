package edu.vcu.cyber.dashboard.ui.custom.av.tree;

import javax.swing.*;
import java.awt.*;

public class AVTreePanel extends JPanel
{
	
	private JTree tree;
	
	public AVTreePanel()
	{
		setLayout(new BorderLayout());
		
		createTree();
		
		add(tree, BorderLayout.CENTER);
		
	}
	
	private void createTree()
	{
		tree = new JTree();
	}
	
}
