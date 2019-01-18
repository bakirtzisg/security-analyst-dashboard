package edu.vcu.cyber.dashboard.actions;

import edu.vcu.cyber.dashboard.av.AttackVector;
import edu.vcu.cyber.dashboard.ui.BucketPanel;

import java.util.ArrayList;
import java.util.List;

public class ActionAVToBucket extends Action
{

	private List<AttackVector> avList;

	public ActionAVToBucket()
	{
		avList = new ArrayList<>();
	}

	public void addAttackVector(AttackVector av)
	{
		if (!av.inBucket)
		{
			actionSize += 2;
			avList.add(av);
		}
	}

	@Override
	protected void doAction()
	{
		avList.forEach(av -> BucketPanel.instance.addRow(av));
	}

	@Override
	protected void undoAction()
	{
		avList.forEach(av -> BucketPanel.instance.removeRow(av));
	}
}
