package edu.vcu.cyber.dashboard.ui.custom.av.list;

import edu.vcu.cyber.dashboard.av.AttackVector;

import javax.swing.*;
import java.util.function.Predicate;

import static edu.vcu.cyber.dashboard.ui.custom.av.list.AVListPanel.hideChecked;

public class AVTableRowFilter extends RowFilter<AVTableModel, Object>
{
	private final AVTableModel model;
	
	private Predicate<AttackVector> pred;
	
	public AVTableRowFilter(AVTableModel model)
	{
		this.model = model;
	}
	
	public void setFilter(Predicate<AttackVector> pred)
	{
		this.pred = pred;
	}
	
	@Override
	public boolean include(Entry<? extends AVTableModel, ?> entry)
	{
		AttackVector av = model.rows.get((Integer) entry.getIdentifier()).av;
		if ((av.deleted || av.inBucket) && hideChecked || !av.canShow())
		{
			return false;
		}
		return pred == null || pred.test(av);
	}
}