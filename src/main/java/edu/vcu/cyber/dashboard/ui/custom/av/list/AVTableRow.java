package edu.vcu.cyber.dashboard.ui.custom.av.list;

import edu.vcu.cyber.dashboard.av.AttackVector;
import static edu.vcu.cyber.dashboard.ui.custom.av.list.AVListPanel.*;


/**
 * Object created for every entry to the table
 */
public class AVTableRow
{
	// the AttackVector associated with this row
	AttackVector av;
	
	AVTableRow(AttackVector av)
	{
		this.av = av;
	}
	
	/**
	 * converts the input index to the respective output value
	 */
	Object getValue(int index)
	{
		switch (index)
		{
			case 0:
				return av.inBucket;
			case 1:
				return av.deleted;
			case 2:
				return av.qualifiedName;
			case 3:
				return av.description;
			default:
				return "--";
		}
	}
	
	/**
	 * attempts to set the specified value
	 */
	void setValue(Object val, int col)
	{
		switch (col)
		{
			case 0: // important
				av.inBucket = (boolean) val;
				break;
			case 1: // ignore
				av.deleted = (boolean) val;
				break;
		}
		
		if ((av.inBucket || av.deleted) && hideChecked)
		{
			inst.removeRow(av);
		}
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof AttackVector)
		{
			return ((AttackVector) obj).qualifiedName.equals(av.qualifiedName);
		}
		return obj instanceof AVTableRow && av.qualifiedName.equals(((AVTableRow) obj).av.qualifiedName);
	}
}