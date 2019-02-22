package edu.vcu.cyber.dashboard.av;

import edu.vcu.cyber.dashboard.ui.custom.av.tree.AVTree;

import java.util.List;
import java.util.function.Predicate;

public class AVTreeVisHandler extends AttackVectorVisualizer
{

	private AVTree tree;

	public AVTreeVisHandler(AVTree tree)
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
		tree.removeAttack(av);
		av.shownInTree = false;
	}

	@Override
	public void removeAttack(AttackVector av)
	{
		tree.removeAttack(av);
		av.shownInTree = false;
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

	public void purgeFlagged()
	{
		tree.refresh();

	}

}
