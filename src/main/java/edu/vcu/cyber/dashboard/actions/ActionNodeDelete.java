package edu.vcu.cyber.dashboard.actions;

import edu.vcu.cyber.dashboard.data.AttackVector;
import edu.vcu.cyber.dashboard.data.AttackVectors;
import edu.vcu.cyber.dashboard.data.GraphData;
import edu.vcu.cyber.dashboard.ui.BucketPanel;
import edu.vcu.cyber.dashboard.util.NodeUtil;

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

			graphData.flagRemoval(av.qualifiedName);
		}
		graphData.removeFlagged();
	}

	@Override
	protected void undoAction()
	{
		for (AttackVector av : avList)
		{
//			NodeUtil.toggleConsumed(graphData, av.qualifiedName);
			av.deleted = false;

			av.addToGraph(graphData.getGraph());
		}
//		graphData.removeFlagged();
	}
}
