package edu.vcu.cyber.dashboard.util;

import edu.vcu.cyber.dashboard.av.AttackVector;

import java.util.function.Predicate;

public class FilterPredicate implements Predicate<AttackVector>
{
	public enum FilterMode
	{
		All, Attacks, Description, Contents, Components, Bucket
	}
	
	private final FilterMode filterMode;
	private final String filterText;
	
	public FilterPredicate(String filterText, FilterMode filterMode)
	{
		
		this.filterText = filterText;
		this.filterMode = filterMode;
	}
	
	@Override
	public boolean test(AttackVector av)
	{
		switch (filterMode)
		{
			case All:
				
				return av.qualifiedName.contains(filterText) || av.description.contains(filterText)
						|| av.contents.contains(filterText) || av.violatedComponents.toString().contains(filterText);
			case Attacks:
				return av.qualifiedName.contains(filterText);
			case Description:
				return av.description.contains(filterText);
			case Contents:
				return av.contents.contains(filterText);
			case Components:
				return av.violatedComponents.toString().contains(filterText);
			case Bucket:
				return av.inBucket;
		}
		
		return false;
	}
	
	
}
