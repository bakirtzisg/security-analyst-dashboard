package edu.vcu.cyber.dashboard.util;

import edu.vcu.cyber.dashboard.av.AttackVector;

import java.util.Collections;
import java.util.List;

public class AVSorter
{

	public static void sort(List<AttackVector> list)
	{
		Collections.sort(list, (c1, c2) ->
		{
			if (c1.type == c2.type)
			{
				return c1.qualifiedName.compareTo(c2.qualifiedName);
			}
			else
				return c1.type.canConsume(c2.type) ? -1 : 1;


		});
	}

}
