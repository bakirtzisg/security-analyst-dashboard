package edu.vcu.cyber.dashboard.ui.custom.av.tree;

import edu.vcu.cyber.dashboard.av.AttackVector;
import edu.vcu.cyber.dashboard.av.AttackVectors;
import edu.vcu.cyber.dashboard.av.Relation;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import java.awt.*;

public class AVTreePanel extends JPanel implements TreeWillExpandListener
{

	private JTree tree;

	private DefaultMutableTreeNode rootNode;
	private AVTreeModel model;

	public AVTreePanel()
	{
		setLayout(new BorderLayout());

		createTree();

		add(new JScrollPane(tree), BorderLayout.CENTER);

	}

	private void createTree()
	{
		tree = new JTree();

		rootNode = new DefaultMutableTreeNode("root");
		model = new AVTreeModel(rootNode);

		tree.setModel(model);
		tree.setRootVisible(false);

		tree.addTreeWillExpandListener(this);


	}

	public void populate()
	{
		AttackVectors.forEach(av ->
		{
			rootNode.add(new DefaultMutableTreeNode(av));
		});
	}

	public void addTopNode(AttackVector av)
	{

		DefaultMutableTreeNode node = new AVTreeNode(rootNode.getChildCount(), 1, av);

		model.insertNodeInto(node, rootNode, rootNode.getChildCount());
		tree.scrollPathToVisible(new TreePath(node.getPath()));
	}

	public void removeNode(AttackVector av)
	{
		rootNode.remove(new DefaultMutableTreeNode(av));
	}


	@Override
	public void treeWillExpand(TreeExpansionEvent evt) throws ExpandVetoException
	{
		Object lobj = evt.getPath().getLastPathComponent();
		if (lobj instanceof AVTreeNode)
		{
			((AVTreeNode) lobj).loadChildren(model, null);
		}

	}

	@Override
	public void treeWillCollapse(TreeExpansionEvent evt) throws ExpandVetoException
	{

	}
}
