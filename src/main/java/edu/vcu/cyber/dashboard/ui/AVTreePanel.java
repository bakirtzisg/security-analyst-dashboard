package edu.vcu.cyber.dashboard.ui;

import edu.vcu.cyber.dashboard.av.VisHandler;
import edu.vcu.cyber.dashboard.ui.custom.av.tree.AVTree;

import javax.swing.*;
import java.awt.*;

public class AVTreePanel extends JPanel
{

	private AVTree tree;
	private FilterToolbar filterToolbar;

	public AVTreePanel()
	{
		initComponents();
	}

	public void initComponents()
	{

		setLayout(new BorderLayout());

		tree = new AVTree();
		filterToolbar = new FilterToolbar(filter ->
		{
			VisHandler.treeVis().filterAttacks(filter);
		});

		add(tree, BorderLayout.CENTER);
		add(filterToolbar, BorderLayout.NORTH);


	}

}
