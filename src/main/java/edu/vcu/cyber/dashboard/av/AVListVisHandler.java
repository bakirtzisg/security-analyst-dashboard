package edu.vcu.cyber.dashboard.av;

import edu.vcu.cyber.dashboard.ui.custom.av.list.AVListPanel;

import java.util.List;
import java.util.function.Predicate;

public class AVListVisHandler extends AttackVectorVisualizer
{
	
	private AVListPanel list;
	
	public AVListVisHandler(AVListPanel panel)
	{
		this.list = panel;
	}
	
	@Override
	public void dispose()
	{
		list.dispose();
	}
	
	@Override
	public void populate()
	{
//		list.clearAll();
//		AttackVectors.forEach(this::showAttack);
//		list.refresh();
		list.populate();
	}
	
	public void filterAttacks(Predicate<AttackVector> filter)
	{
		this.filter = filter;
		list.filter(filter);
//		AttackVectors.forEach(av ->
//		{
//			boolean pred = filter == null || filter.test(av);
//			if (pred && !av.shown)
//				showAttack(av);
//			else if (!pred && av.shown)
//				hideAttack(av);
//		});
		
		purgeFlagged();
	}
	
	@Override
	public void showAttack(AttackVector av)
	{
//		if (av.canShow() && !av.shown)
		{
			list.addRow(av);
			av.shown = true;
		}
	}
	
	@Override
	public void hideAttack(AttackVector av)
	{
		list.removeRow(av);
		av.shown = false;
	}
	
	@Override
	public void removeAttack(AttackVector av)
	{
		list.removeRow(av);
		av.shown = false;
	}
	
	@Override
	public boolean isShown(AttackVector av)
	{
		return list.contains(av);
	}
	
	@Override
	public List<AttackVector> getShown()
	{
		return null;
	}
	
	@Override
	public void addEdge(Relation relation)
	{
		// TODO: Not sure how to do this one yet
	}
	
	@Override
	public void removeEdge(Relation relation)
	{
		// TODO: Not sure how to do this one yet
	}
	
	@Override
	public void clearAll()
	{
		list.clearAll();
	}
}
