package edu.vcu.cyber.dashboard.av;

import edu.vcu.cyber.dashboard.ui.custom.av.tree.AVTreePanel;

import java.util.List;

public class AVTreeVisHandler extends AttackVectorVisualizer
{

	private AVTreePanel tree;

	public AVTreeVisHandler(AVTreePanel tree)
	{
		this.tree = tree;
	}

	@Override
	public void dispose()
	{

	}

	@Override
	public void populate()
	{
		tree.populate();
	}

	@Override
	public void showAttack(AttackVector av)
	{
		if (av.canShow())
			tree.addTopNode(av);
	}

	@Override
	public void hideAttack(AttackVector av)
	{
		tree.removeNode(av);
	}

	@Override
	public void removeAttack(AttackVector av)
	{
		tree.removeNode(av);
	}

	@Override
	public boolean isShown(AttackVector av)
	{
		return false;
	}

	@Override
	public List<AttackVector> getShown()
	{
		return null;
	}

	@Override
	public void addEdge(Relation relation)
	{

	}

	@Override
	public void removeEdge(Relation relation)
	{

	}

	@Override
	public void clearAll()
	{

	}
}
