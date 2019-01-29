package edu.vcu.cyber.dashboard.ui;

import com.alee.extended.layout.WrapFlowLayout;
import edu.vcu.cyber.dashboard.av.VisHandler;
import edu.vcu.cyber.dashboard.ui.custom.av.tree.AVTree;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AVTreePanel extends JPanel implements ActionListener
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
		JPanel controlPanel = new JPanel(new BorderLayout());


		filterToolbar = new FilterToolbar(filter -> VisHandler.treeVis().filterAttacks(filter));


		controlPanel.add(filterToolbar, BorderLayout.NORTH);
		add(controlPanel, BorderLayout.NORTH);

		add(tree, BorderLayout.CENTER);

		JPanel checkboxes = new JPanel(new WrapFlowLayout(10, 10));

		JCheckBox cb_deleted = new JCheckBox("Show Deleted");
		JCheckBox cb_filter_children = new JCheckBox("Filter Children");

		cb_deleted.addActionListener(this);
		cb_filter_children.addActionListener(this);

		checkboxes.add(cb_deleted);
		checkboxes.add(cb_filter_children);
		controlPanel.add(checkboxes, BorderLayout.CENTER);


	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		switch (e.getActionCommand())
		{
			case "Show Deleted":

				tree.setShowDeleted(((JCheckBox) e.getSource()).isSelected());

				break;
			case "Filter Children":
				tree.setFilterChildren(((JCheckBox) e.getSource()).isSelected());
				break;
		}
	}
}
