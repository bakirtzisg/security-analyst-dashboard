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
	public void showAttack(AttackVector av)
	{
		tree.addTopNode(av);
		av.shownInTree = true;
	}

	@Override
	public void hideAttack(AttackVector av)
	{
		tree.removeNode(av);
		av.shownInTree = false;
	}

	@Override
	public void removeAttack(AttackVector av)
	{
		tree.removeNode(av);
	}

	@Override
	public boolean isShown(AttackVector av)
	{
		return av.shownInTree;
	}

	@Override
	public boolean checkIfShown(AttackVector av)
	{
		return tree.locateNode(av) != null;
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
		tree.clearAll();
		AttackVectors.forEach(av -> av.shownInTree = false);
	}
}
