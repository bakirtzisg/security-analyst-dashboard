package edu.vcu.cyber.dashboard.av;

import java.util.List;
import java.util.function.Predicate;

/**
 * Abstract representation of an attack vector visualization.
 * This class allows creating new ways to visualize the data without breaking
 * or rewriting the old method.
 */
public abstract class AttackVectorVisualizer
{
	// the filter itself
	protected Predicate<AttackVector> filter;

	// ------------------------------------------
	// ------------ Abstract Methods ------------
	// ------------------------------------------

	/**
	 * Shows the attack on the visualizer
	 */
	public abstract void showAttack(AttackVector av);

	/**
	 * Flags the attack to be removed from the visualizer
	 * Doesn't actually remove it. Call purgeFlagged() to finish removal
	 */
	public abstract void hideAttack(AttackVector av);

	/**
	 * Removes the attack from the visualizer
	 */
	public abstract void removeAttack(AttackVector av);

	/**
	 * @return true if the attack is currently shown on the visualizer
	 */
	public abstract boolean isShown(AttackVector av);

	/**
	 * Verifies the visibility of the node
	 *
	 * @return true if the attack is currently shown on the visualizer
	 */
	public abstract boolean checkIfShown(AttackVector av);

	/**
	 * @return a list of all Attack Vectors shown on the visualizer
	 */
	;;

	public abstract List<AttackVector> getShown();

	/**
	 * Adds the edge related to the relation
	 */
	public abstract void addEdge(Relation relation);

	/**
	 * Removes the edge related to the relation
	 */
	public abstract void removeEdge(Relation relation);

	/**
	 * Clears all items
	 */
	public abstract void clearAll();


	// ------------------------------------------
	// ----------- Functional Methods -----------
	// ------------------------------------------

	public void attemptAddAttack(AttackVector av)
	{
		if (!isShown(av) && av.canShow())
		{
			showAttack(av);
		}
	}

	/**
	 * Called when the visualizer is removed or switched from
	 * Removes all attack vectors from the visualizer
	 */
	public void dispose()
	{
		clearAll();
	}

	/**
	 * Called when the visualizer is first set.
	 * Adds all the attack vectors that should be shown to the visualizer
	 */
	public void populate()
	{
		AttackVectors.forEach(this::attemptAddAttack);
	}

	/**
	 * Only shows the attack vectors that the filter allows
	 */
	public void filterAttacks(Predicate<AttackVector> filter)
	{
		this.filter = filter;
		AttackVectors.forEach(av ->
		{
			boolean pred = filter == null || filter.test(av);
			boolean shown = isShown(av);
			if (pred && !shown)
				attemptAddAttack(av);
			else if (!pred && shown)
				hideAttack(av);
		});

		purgeFlagged();
	}


	/**
	 * Does the actual node removal
	 */
	public void purgeFlagged()
	{

	}

	public void updateAttackStatus()
	{
		AttackVectors.forEach(av -> av.shown = checkIfShown(av));
	}

	/**
	 * Clears all visibility flags and adds all attacks to the visualizer
	 */
	public void reset()
	{
		filter = null;
		clearAll();
		AttackVectors.forEach(av ->
		{
			av.forceShow = false;
			av.hidden = av.shouldHide();
			attemptAddAttack(av);
		});
	}

	/**
	 * Updates all the attacks based on the filter.
	 * Adds the attacks that should be shown and removes the ones that should not.
	 */
	public void update()
	{
		if (filter != null) // if filter exists, just run the filter
		{
			filterAttacks(filter);
		}
		else // if not filter exists, toggle shown and what not
		{
			AttackVectors.getAllAttackVectors().forEach(av ->
			{
				av.shown = isShown(av);
			});

			purgeFlagged();
		}
	}

	/**
	 * Creates the necessary edges between the provided node
	 * and all the other visible nodes
	 */
	public void resolveEdges(AttackVector node)
	{
		node.relations.forEach(rel ->
		{
			if (!rel.shown && rel.shouldBeVisible())
				addEdge(rel);
		});
	}

	/**
	 * Shows all attacks related to the specified attack
	 *
	 * @param forceShow - whether or not to show all related regardless of deleted or hidden status
	 */
	public void showAllRelated(AttackVector av, boolean forceShow)
	{
		if (av != null)
		{
			resolveEdges(av);

			av.relations.forEach(rel ->
			{
				if (forceShow)
				{
					rel.parent.forceShow = true;
					rel.child.forceShow = true;
				}
				attemptAddAttack(rel.parent);
				attemptAddAttack(rel.child);
			});

		}

	}

}
