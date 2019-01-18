package edu.vcu.cyber.dashboard.av;

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
		return obj instanceof Relation && (((Relation) obj).parent.equals(parent) && ((Relation) obj).child.equals(child) || ((Relation) obj).parent.equals(child) && ((Relation) obj).child.equals(parent));
	}
}
