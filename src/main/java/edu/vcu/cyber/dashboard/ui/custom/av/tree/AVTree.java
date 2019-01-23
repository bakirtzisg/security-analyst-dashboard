package edu.vcu.cyber.dashboard.ui.custom.av.tree;

import edu.vcu.cyber.dashboard.av.AVTreeVisHandler;
import edu.vcu.cyber.dashboard.av.AttackVector;
import edu.vcu.cyber.dashboard.av.VisHandler;
import edu.vcu.cyber.dashboard.ui.BucketPanel;
import edu.vcu.cyber.dashboard.util.Utils;
import sun.reflect.generics.tree.Tree;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.Objects;
import java.util.function.Consumer;

public class AVTree extends JPanel implements TreeWillExpandListener, MouseListener, ActionListener
{

	private static final String CMD_BUCKET_ADD = "Add to Bucket";
	private static final String CMD_BUCKET_ADD_PATH = "Add path to Bucket";

	private static final String CMD_GRAPH_ADD = "Add to Graph";
	private static final String CMD_GRAPH_ADD_PATH = "Add path to Graph";

	private static final String CMD_DELETE = "Delete";

	private JTree tree;

	private DefaultMutableTreeNode rootNode;
	private AVTreeModel model;

	private JPopupMenu popup;

	private AVTreeVisHandler vis;

	public AVTree()
	{
		setLayout(new BorderLayout());

		createTree();
		createPopupMenu();

		add(new JScrollPane(tree), BorderLayout.CENTER);

		VisHandler.register(vis = new AVTreeVisHandler(this));

		tree.addMouseListener(this);
	}

	private void createTree()
	{
		tree = new JTree();

		rootNode = new DefaultMutableTreeNode("Attack Vectors");
		model = new AVTreeModel(rootNode);
		tree.setCellRenderer(new AVTreeCellRenderer());

		tree.setModel(model);
		tree.setRootVisible(false);

		tree.addTreeWillExpandListener(this);


	}

	private void createPopupMenu()
	{
		popup = new JPopupMenu();

		Utils.addButton(popup, CMD_BUCKET_ADD, "CTRL-B", this);

		popup.add(CMD_BUCKET_ADD).addActionListener(this);
		popup.add(CMD_BUCKET_ADD_PATH).addActionListener(this);

		popup.add(CMD_GRAPH_ADD).addActionListener(this);
		popup.add(CMD_GRAPH_ADD_PATH).addActionListener(this);
		popup.addSeparator();
		popup.add(CMD_DELETE).addActionListener(this);
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


	public void removeAttack(AttackVector av)
	{

		Enumeration children = rootNode.children();

		while (children.hasMoreElements())
		{
			Object obj = children.nextElement();
			if (obj instanceof AVTreeNode && ((AVTreeNode) obj).av.equals(av))
			{
				removeNode((AVTreeNode) obj);
			}
		}

	}

	public void forEachTopLevel(Consumer<AVTreeNode> consumer)
	{
		Enumeration children = rootNode.children();

		while (children.hasMoreElements())
		{
			Object obj = children.nextElement();
			if (obj instanceof AVTreeNode)
			{
				consumer.accept((AVTreeNode) obj);
			}
		}
	}

	public void removeNode(MutableTreeNode node)
	{
		model.removeNodeFromParent(node);
	}


	@Override
	public void treeWillExpand(TreeExpansionEvent evt) throws ExpandVetoException
	{
		AVTreeNode node = resolveNode(evt.getPath());
		if (node != null)
		{
			node.loadChildren(model, null);
		}

	}

	public void refresh()
	{
		tree.revalidate();
	}

	@Override
	public void treeWillCollapse(TreeExpansionEvent evt) throws ExpandVetoException
	{
		AVTreeNode node = resolveNode(evt.getPath());
		if (node != null)
		{
			node.dispose();
		}
	}

	public AVTreeNode resolveNode(Object obj)
	{
		if (obj instanceof AVTreeNode)
		{
			return (AVTreeNode) obj;
		}
		else if (obj instanceof TreePath)
		{
			return resolveNode(((TreePath) obj).getLastPathComponent());
		}
		return null;
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		if (SwingUtilities.isRightMouseButton(e))
		{
			int row = tree.getClosestRowForLocation(e.getX(), e.getY());
			if (row != -1)
			{
				tree.addSelectionRow(row);

				popup.show(e.getComponent(), e.getX(), e.getY());
			}

		}

	}

	@Override
	public void mousePressed(MouseEvent e)
	{

	}

	@Override
	public void mouseReleased(MouseEvent e)
	{

	}

	@Override
	public void mouseEntered(MouseEvent e)
	{

	}

	@Override
	public void mouseExited(MouseEvent e)
	{

	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		switch (e.getActionCommand())
		{
			case CMD_BUCKET_ADD_PATH:
				for (TreePath path : Objects.requireNonNull(tree.getSelectionPaths()))
				{
					Object[] items = path.getPath();
					for (Object obj : items)
					{
						AVTreeNode node = resolveNode(obj);
						if (node != null)
						{
							BucketPanel.instance.addRow(node.av);
						}
					}
				}
			case CMD_BUCKET_ADD:
			{
				for (TreePath path : Objects.requireNonNull(tree.getSelectionPaths()))
				{
					AVTreeNode node = resolveNode(path);
					if (node != null)
					{
						BucketPanel.instance.addRow(node.av);
					}
				}
			}

			break;


			case CMD_GRAPH_ADD_PATH:

			case CMD_GRAPH_ADD:

				break;
			case CMD_DELETE:
				for (TreePath path : Objects.requireNonNull(tree.getSelectionPaths()))
				{
					removeNode(resolveNode(path));
				}
				break;
		}
	}
}
