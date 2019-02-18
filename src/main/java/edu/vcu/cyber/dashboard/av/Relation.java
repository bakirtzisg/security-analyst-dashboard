package edu.vcu.cyber.dashboard.av;

import org.graphstream.graph.Graph;

public class Relation
{

	protected final AttackVector parent;
	protected final AttackVector child;

	protected boolean shown;

	protected boolean isMember;
	protected boolean isDirectionless;

	protected final String edgeId;


	public Relation(AttackVector parent, AttackVector child)
	{
		this.parent = parent;
		this.child = child;

		edgeId = parent.qualifiedName + "-" + child.qualifiedName;

		isMember = parent.type == child.type;
	}

	/**
	 * @return the parent attack
	 */
	public AttackVector getParent()
	{
		return parent;
	}

	/**
	 * @return the child attack
	 */
	public AttackVector getChild()
	{
		return child;
	}

	/**
	 * @return whether or not this relation is current shown in the visualizer
	 */
	public boolean isShown()
	{
		return shown;
	}

	/**
	 * Used by the visualizer to set if it's currently visible
	 */
	public void setShown(boolean shown)
	{
		this.shown = shown;
	}

	/**
	 * @return true only if both the parent and child are shown
	 */
	public boolean shouldBeVisible()
	{
		return parent.shown && child.shown;
	}

	/**
	 * @return true if the given AttackVector is the parent for this relation
	 */
	public boolean isParent(AttackVector av)
	{
		return parent.uid == av.uid;
	}

	public AttackVector getOther(AttackVector av)
	{
		return av.equals(parent) ? child : parent;
	}

	public boolean hasEdge(Graph graph)
	{
		return graph.getEdge(edgeId) != null;
	}

	/**
	 * @return an edge ID that should be unique to just this relation
	 */
	public String getEdgeId()
	{
		return edgeId;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Relation)
		{
			Relation rel = (Relation) obj;
			return (rel.parent.equals(parent) && rel.child.equals(child)) || (rel.parent.equals(child) && rel.child.equals(parent));
		}
		return false;
	}
}
