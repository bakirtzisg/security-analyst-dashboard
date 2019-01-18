package edu.vcu.cyber.dashboard.actions;

import edu.vcu.cyber.dashboard.av.AttackVector;
import edu.vcu.cyber.dashboard.av.AttackVectors;
import edu.vcu.cyber.dashboard.data.GraphData;

import java.util.ArrayList;
import java.util.List;

public class ActionNodeDelete extends Action
{
	private List<AttackVector> avList;
	private GraphData graphData;
	
	public ActionNodeDelete(GraphData graphData)
	{
		this.graphData = graphData;
		avList = new ArrayList<>();
	}
	
	public void addAttackVector(AttackVector av)
	{
		if (!av.deleted)
		{
			actionSize += 2;
			avList.add(av);
		}
	}
	
	@Override
	protected void doAction()
	{
		for (AttackVector av : avList)
		{
//			NodeUtil.toggleConsumed(graphData, av.qualifiedName);
			av.deleted = true;
			AttackVectors.vis().hideAttack(av);
		}
		AttackVectors.vis().purgeFlagged();
	}
	
	@Override
	protected void undoAction()
	{
		for (AttackVector av : avList)
		{
//			NodeUtil.toggleConsumed(graphData, av.qualifiedName);
			av.deleted = false;
			
			AttackVectors.vis().showAttack(av);

//			av.addToGraph(graphData.getGraph());
		}
//		graphData.removeFlagged();
	}
}
