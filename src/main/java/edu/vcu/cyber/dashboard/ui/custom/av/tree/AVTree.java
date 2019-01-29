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
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class AVTree extends JPanel implements TreeWillExpandListener, MouseListener, ActionListener, KeyListener
{

	private static final String CMD_BUCKET_ADD = "Add to Bucket";
	private static final String CMD_BUCKET_ADD_PATH = "Add path to Bucket";

	private static final String CMD_GRAPH_ADD = "Add to Graph";
	private static final String CMD_GRAPH_ADD_PATH = "Add path to Graph";

	private static final String CMD_GOTO_WEBSITE = "Website";
	private static final String CMD_DELETE = "Delete";

	private boolean showDeleted;
	private boolean filterChildren;

	private Predicate<AttackVector> filter;

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
		tree.addKeyListener(this);


		showDeleted = false;
		filterChildren = false;
	}

	private void createTree()
	{
		tree = new JTree();

		rootNode = new DefaultMutableTreeNode("Attack Vectors");
		model = new AVTreeModel(rootNode);
		tree.setCellRenderer(new AVTreeCellRenderer());

		tree.setModel(model);
		tree.setRootVisible(true);

		tree.addTreeWillExpandListener(this);


	}

	private void createPopupMenu()
	{
		popup = new JPopupMenu();

		Utils.addButton(popup, CMD_BUCKET_ADD, "Ctrl + B", this);
		Utils.addButton(popup, CMD_BUCKET_ADD_PATH, "Ctrl + Shift + B", this);
		Utils.addButton(popup, CMD_GOTO_WEBSITE, "Opens the website for this attack", this);

//		popup.add(CMD_GRAPH_ADD).addActionListener(this);
//		popup.add(CMD_GRAPH_ADD_PATH).addActionListener(this);
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

	public void setFilter(Predicate<AttackVector> filter)
	{
		this.filter = filter;
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


	public void refresh()
	{
		tree.revalidate();
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


	public void updateVisibleNodes()
	{
		updateNode(rootNode);
	}

	public void updateNode(MutableTreeNode node)
	{
		Enumeration en = node.children();
		while (en.hasMoreElements())
		{
			MutableTreeNode tn = (MutableTreeNode) en.nextElement();
			if (tn instanceof AVTreeNode)
			{
				if (!canShow(((AVTreeNode) tn).av))
				{
					removeNode(tn);
					continue;
				}
			}
			updateNode(tn);
		}
	}

	public void setFilterChildren(boolean filterChildren)
	{
		this.filterChildren = filterChildren;
		updateVisibleNodes();
	}

	public void setShowDeleted(boolean showDeleted)
	{
		this.showDeleted = showDeleted;
		updateVisibleNodes();
	}

	private boolean canShow(AttackVector av)
	{
		return (!av.deleted || showDeleted) && (!filterChildren && (filter == null || filter.test(av)));
	}

	private void addSelectedToBucket(boolean addPath)
	{
		for (TreePath path : Objects.requireNonNull(tree.getSelectionPaths()))
		{
			AVTreeNode node = resolveNode(path);
			if (node != null)
			{
				BucketPanel.instance.addRow(node.av);
			}
			if (addPath)
			{
				Object[] items = path.getPath();
				for (Object obj : items)
				{
					node = resolveNode(obj);
					if (node != null)
					{
						BucketPanel.instance.addRow(node.av);
					}
				}
			}
		}
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

	@Override
	public void treeWillCollapse(TreeExpansionEvent evt) throws ExpandVetoException
	{
		AVTreeNode node = resolveNode(evt.getPath());
		if (node != null)
		{
			node.dispose();
		}
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
				addSelectedToBucket(true);
				break;

			case CMD_BUCKET_ADD:
				addSelectedToBucket(false);
				break;

			case CMD_GOTO_WEBSITE:
				Object obj = tree.getLastSelectedPathComponent();
				if(obj instanceof AVTreeNode)
				{
					Utils.openWebPage(((AVTreeNode) obj).av.getURI());
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

	@Override
	public void keyTyped(KeyEvent e)
	{

	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_B)
		{
			if (e.isShiftDown()) // ctrl + shift + B = add path to bucket
			{
				addSelectedToBucket(true);
			}
			else // ctrl + B = add to bucket
			{
				addSelectedToBucket(false);
			}
		}
		else if (e.getKeyCode() == KeyEvent.VK_DELETE)
		{
			for (TreePath path : Objects.requireNonNull(tree.getSelectionPaths()))
			{
				removeNode(resolveNode(path));
			}
		}

	}

	@Override
	public void keyReleased(KeyEvent e)
	{

	}
}
