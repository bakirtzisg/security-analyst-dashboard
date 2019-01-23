package edu.vcu.cyber.dashboard.ui.custom.av.tree;

import edu.vcu.cyber.dashboard.av.AttackVector;
import edu.vcu.cyber.dashboard.av.Relation;
import edu.vcu.cyber.dashboard.av.VisHandler;
import edu.vcu.cyber.dashboard.data.AttackType;
import edu.vcu.cyber.dashboard.util.AVSorter;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class AVTreeNode extends DefaultMutableTreeNode
{

	private boolean loaded = false;

	private int depth;

	private final int index;

	protected final AttackVector av;

	private boolean isLeaf;
	protected boolean exists;

	public AVTreeNode(int index, int depth, AttackVector av)
	{
		this.index = index;
		this.depth = depth;
		this.av = av;

		if (av.type != AttackType.CVE)
		{
			add(new DefaultMutableTreeNode("Loading...", false));
			setAllowsChildren(true);
			isLeaf = av.relations.size() < 1;
		}
		else
		{
			isLeaf = true;
			setAllowsChildren(false);
		}
		setUserObject(av);


	}

	public AVTreeNode(int index, int depth, AttackVector av, boolean exists)
	{
		this(index, depth, av);
		this.exists = exists;

		if (!isLeaf)
			isLeaf = exists;
	}

	private void setChildren(List<AVTreeNode> children)
	{
		removeAllChildren();
		setAllowsChildren(children.size() > 0);
		for (MutableTreeNode node : children)
		{
			add(node);
		}
		loaded = true;
	}


	public boolean isLeaf()
	{
		return isLeaf;
	}

	private boolean isInPath(AttackVector av)
	{
		return isInPath(getParent(), av);
	}

	private boolean isInPath(TreeNode node, AttackVector av)
	{
		if (node instanceof AVTreeNode)
		{
			AVTreeNode n = (AVTreeNode) node;
			if (n.av.equals(av))
			{
				return true;
			}
			else
			{
				return isInPath(n.getParent(), av);
			}
		}
		return false;
	}

	public void dispose()
	{
		loaded = false;
		removeAllChildren();
	}

	public void loadChildren(final DefaultTreeModel model, final PropertyChangeListener progress)
	{
		if (loaded)
			return;

		SwingWorker<List<AVTreeNode>, Void> worker = new SwingWorker<List<AVTreeNode>, Void>()
		{
			@Override
			protected List<AVTreeNode> doInBackground() throws Exception
			{

				loaded = true;
				setProgress(0);
				List<AVTreeNode> children = new ArrayList<>();

				List<AttackVector> relations = new ArrayList<>();

				Predicate<AttackVector> filter = VisHandler.treeVis().getFilter();

				int i = 0;
				for (Relation rel : av.relations)
				{
					AttackVector other = rel.getOther(av);
					if (filter != null && !filter.test(other))
					{
						continue;
					}
					if (!isInPath(other) && !other.deleted)
					{
						relations.add(other);
					}
					setProgress(100 * i / av.relations.size());
				}

				AVSorter.sort(relations);

				for (AttackVector av : relations)
				{
					children.add(new AVTreeNode(i, depth + 1, av, false));
				}

				setProgress(0);
				return children;
			}

			@Override
			protected void done()
			{
				try
				{
					setChildren(get());
					model.nodeStructureChanged(AVTreeNode.this);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				super.done();
			}
		};
		if (progress != null)
		{
			worker.getPropertyChangeSupport().addPropertyChangeListener("progress", progress);
		}

		worker.execute();

	}

}
