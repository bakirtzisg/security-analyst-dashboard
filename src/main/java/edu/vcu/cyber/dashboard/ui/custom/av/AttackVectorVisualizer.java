package edu.vcu.cyber.dashboard.ui.custom.av;

import edu.vcu.cyber.dashboard.data.AttackVector;
import edu.vcu.cyber.dashboard.data.AttackVectors;

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
	
	/**
	 * Called when the visualizer is removed or switched from
	 * Removes all attack vectors from the visualizer
	 */
	public abstract void dispose();
	
	/**
	 * Called when the visualizer is first set.
	 * Adds all the attack vectors that should be shown to the visualizer
	 */
	public abstract void populate();
	
	/**
	 * Only shows the attack vectors that the filter allows
	 */
	public abstract void filterAttacks(Predicate<AttackVector> filter);
	
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
	 * @return true if the attack is currently shown on the visualizer
	 */
	public abstract boolean isShown(AttackVector av);
	
	/**
	 * @return a list of all Attack Vectors shown on the visualizer
	 */
	public abstract List<AttackVector> getShown();
	
	/**
	 * Does the actual node removal
	 */
	public void purgeFlagged()
	{
	
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
				if (av.shown)
				{
					showAttack(av);
				}
				else
				{
					hideAttack(av);
				}
			});
			
			purgeFlagged();
		}
	}
	
}
