package edu.vcu.cyber.dashboard.ui.custom.av.tree;

import edu.vcu.cyber.dashboard.av.*;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.Enumeration;

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

		VisHandler.register(new AVTreeVisHandler(this));
	}

	private void createTree()
	{
		tree = new CustomTreePainter();

		rootNode = new DefaultMutableTreeNode("Attack Vectors");
		model = new AVTreeModel(rootNode);
		tree.setCellRenderer(new AVTreeCellRenderer());

		tree.setModel(model);
		tree.setRootVisible(false);

		tree.addTreeWillExpandListener(this);


	}

	public void addTopNode(AttackVector av)
	{

		DefaultMutableTreeNode node = new AVTreeNode(rootNode.getChildCount(), 1, av);

		model.insertNodeInto(node, rootNode, rootNode.getChildCount());
		tree.scrollPathToVisible(new TreePath(node.getPath()));
	}

	public AVTreeNode locateNode(AttackVector av)
	{
		Enumeration children = rootNode.children();
		while (children.hasMoreElements())
		{
			Object obj = children.nextElement();
			if (obj instanceof AVTreeNode && ((AVTreeNode) obj).av.equals(av))
			{
				return (AVTreeNode) obj;
			}
		}
		return null;
	}

	public void clearAll()
	{
		rootNode.removeAllChildren();
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

	private static class CustomTreePainter extends JTree
	{
//		@Override
//		public void paintComponent(Graphics g)
//		{
//			g.setColor(getBackground());
//			g.fillRect(0, 0, getWidth(), getHeight());
//			if (getSelectionCount() > 0)
//			{
//				g.setColor(Color.red);
//				for (int i : getSelectionRows())
//				{
//					Rectangle r = getRowBounds(i);
//					g.fillRect(r.x, r.y, getWidth() - r.x, r.height);
//				}
//
//			}
//			super.paintComponent(g);
//			if (getLeadSelectionPath() != null)
//			{
//				Rectangle r = getRowBounds(getRowForPath(getLeadSelectionPath()));
//				g.setColor(hasFocus() ? Color.red.darker() : Color.red);
//				g.drawRect(r.x, r.y, getWidth() - r.x - 1, r.height - 1);
//			}
//		}
	}
}
