package edu.vcu.cyber.dashboard.av;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class VisHandler
{

	private static List<AttackVectorVisualizer> visualizers = new ArrayList<>();

	private static AVGraphVisHandler avGraphVisHandler;
	private static AVTreeVisHandler avTreeVisHandler;

	public static AVGraphVisHandler graphVis()
	{
		return avGraphVisHandler;
	}

	public static AVTreeVisHandler treeVis()
	{
		return avTreeVisHandler;
	}


	// the filter itself
	protected Predicate<AttackVector> filter;

	public static void dataUpdate()
	{
		dispose();
		populate();
	}

	public static void register(AttackVectorVisualizer vis)
	{
		visualizers.add(vis);
		if (vis instanceof AVGraphVisHandler)
			avGraphVisHandler = (AVGraphVisHandler) vis;
		else if (vis instanceof AVTreeVisHandler)
			avTreeVisHandler = (AVTreeVisHandler) vis;
	}

	/**
	 * Called when the visualizer is removed or switched from
	 * Removes all attack vectors from the visualizer
	 */
	public static void dispose()
	{
		for (AttackVectorVisualizer vis : visualizers)
		{
			vis.dispose();
		}
	}

	/**
	 * Called when the visualizer is first set.
	 * Adds all the attack vectors that should be shown to the visualizer
	 */
	public static void populate()
	{
		for (AttackVectorVisualizer vis : visualizers)
		{
			vis.populate();
		}
	}

	/**
	 * Only shows the attack vectors that the filter allows
	 */
	public static void filterAttacks(Predicate<AttackVector> filter)
	{
		for (AttackVectorVisualizer vis : visualizers)
		{
			vis.filterAttacks(filter);
		}
	}

	/**
	 * Shows the attack on the visualizer
	 */
	public static void showAttack(AttackVector av)
	{

		for (AttackVectorVisualizer vis : visualizers)
		{
			vis.showAttack(av);
		}
	}

	/**
	 * Flags the attack to be removed from the visualizer
	 * Doesn't actually remove it. Call purgeFlagged() to finish removal
	 */
	public static void hideAttack(AttackVector av)
	{

		for (AttackVectorVisualizer vis : visualizers)
		{
			vis.hideAttack(av);
		}
	}

	/**
	 * Removes the attack from the visualizer
	 */
	public static void removeAttack(AttackVector av)
	{

		for (AttackVectorVisualizer vis : visualizers)
		{
			vis.removeAttack(av);
		}
	}

	/**
	 * Adds the edge related to the relation
	 */
	public static void addEdge(Relation relation)
	{
		for (AttackVectorVisualizer vis : visualizers)
		{
			vis.dispose();
		}
	}

	/**
	 * Removes the edge related to the relation
	 */
	public static void removeEdge(Relation relation)
	{

		for (AttackVectorVisualizer vis : visualizers)
		{
			vis.dispose();
		}
	}

	/**
	 * Clears all items
	 */
	public static void clearAll()
	{

		for (AttackVectorVisualizer vis : visualizers)
		{
			vis.dispose();
		}
	}

	/**
	 * Does the actual node removal
	 */
	public static void purgeFlagged()
	{

		for (AttackVectorVisualizer vis : visualizers)
		{
			vis.dispose();
		}
	}

	public static void updateAttackStatus()
	{
		for (AttackVectorVisualizer vis : visualizers)
		{
			vis.dispose();
		}
	}

	/**
	 * Clears all visibility flags and adds all attacks to the visualizer
	 */
	public static void reset()
	{
		for (AttackVectorVisualizer vis : visualizers)
		{
			vis.dispose();
		}
	}

	/**
	 * Updates all the attacks based on the filter.
	 * Adds the attacks that should be shown and removes the ones that should not.
	 */
	public static void update()
	{

		for (AttackVectorVisualizer vis : visualizers)
		{
			vis.dispose();
		}
	}

	/**
	 * Creates the necessary edges between the provided node
	 * and all the other visible nodes
	 */
	public static void resolveEdges(AttackVector node)
	{

		for (AttackVectorVisualizer vis : visualizers)
		{
			vis.dispose();
		}
	}

	/**
	 * Shows all attacks related to the specified attack
	 *
	 * @param forceShow - whether or not to show all related regardless of deleted or hidden status
	 */
	public static void showAllRelated(AttackVector av, boolean forceShow)
	{

		for (AttackVectorVisualizer vis : visualizers)
		{
			vis.dispose();
		}
	}


}
