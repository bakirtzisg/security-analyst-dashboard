package edu.vcu.cyber.dashboard.data;

import edu.vcu.cyber.dashboard.ui.custom.av.AttackVectorVisualizer;

import java.util.*;
import java.util.function.Predicate;

public class AttackVectors
{
	
	private static AttackVectorVisualizer vis;
	
	public static void setVisualizer(AttackVectorVisualizer vis)
	{
		AttackVectors.vis = vis;
	}
	
	public static AttackVectorVisualizer vis()
	{
		return vis;
	}
	
	
	private static Map<String, AttackVector> attacks = new HashMap<>();
	private static Map<String, List<AttackVector>> componentMap = new HashMap<>();
	
	private static Predicate<AttackVector> filter;
	
	public static boolean showDeletedNodes;
	public static boolean showHiddenNodes;
	public static boolean showCVENodes;
	
	public AttackVectors()
	{
		attacks = new HashMap<>();
		componentMap = new HashMap<>();
	}
	
	public static AttackVector addAttack(AttackVector att, String violated)
	{
		AttackVector attack = attacks.computeIfAbsent(att.qualifiedName, key -> att);
		attack = attacks.getOrDefault(attack.qualifiedName, attack);
		if (!attack.violatedComponents.contains(violated))
			attack.violatedComponents.add(violated);
//		attacks.put(attack.qualifiedName, attack);
		
		List<AttackVector> attackList = componentMap.computeIfAbsent(violated, key -> new ArrayList<>());
		attackList.add(attack);
		
		return attack;
	}
	
	public static void printStats()
	{
		System.out.printf("Attack Vector Count: %d\nComponents: %d\n", attacks.size(), componentMap.size());
	}
	
	public static AttackVector getAttackVector(String id)
	{
		return attacks.get(id);
	}
	
	public static Collection<AttackVector> getAllAttackVectors()
	{
		return attacks.values();
	}
	
	public static Collection<AttackVector> getAttackVectorsByComponent(String component)
	{
		return componentMap.computeIfAbsent(component, key -> new ArrayList<>());
	}
	
	public static void filter(Predicate<AttackVector> predicate)
	{
		if (vis != null)
			vis.filterAttacks(predicate);
//		attacks.values().forEach(av -> av.hidden = predicate.test(av));
	}
	
	public static void hideAttacks(Predicate<AttackVector> predicate)
	{
		for (AttackVector av : attacks.values())
		{
			av.hidden = predicate.test(av);
		}
	}
	
	public static void showInGraph(GraphData graphData, Predicate<AttackVector> predicate)
	{
		
		if (vis != null)
			vis.filterAttacks(predicate);
//		filter = predicate;
//		for (AttackVector av : attacks.values())
//		{
//			boolean pred = predicate.test(av);
//			NodeData existing = graphData.getNode(av.qualifiedName);
//			boolean shown = existing != null && existing.getNode() != null;
//			av.shown = pred;
//			if (shown && !pred)
//			{
//				graphData.flagRemoval(av.qualifiedName);
//			}
//			else if (!shown && pred)
//			{
//				av.addToGraph(graphData.getGraph());
//			}
//		}
//
//		graphData.removeFlagged();
	}
	
	public static void updateExisting(GraphData graphData)
	{
		if (vis != null)
			vis.update();
//		graphData.getNodes().forEach(node ->
//		{
//			AttackVector av = AttackVectors.getAttackVector(node.getId());
//			if (av != null)
//			{
//				av.addToGraph(graphData.getGraph());
//			}
//		});
	}
	
	public static void update(GraphData graphData)
	{
		if (vis != null)
			vis.update();
//		if (filter != null)
//		{
//			showInGraph(graphData, filter);
//		}
//		else
//		{
//			for (AttackVector av : attacks.values())
//			{
//				if (av.shown)
//				{
//					av.addToGraph(graphData.getGraph());
//				}
//				else
//				{
//					graphData.flagRemoval(av.qualifiedName);
//				}
//			}
//		}
	}
	
	/**
	 * @param attackId
	 * @param graphData
	 */
	public static void showAllRelated(String attackId, GraphData graphData)
	{
		AttackVector attack = AttackVectors.getAttackVector(attackId);
		
		if (attack.related_cwe != null)
			for (String rel : attack.related_cwe)
			{
				AttackVector av1 = attacks.get(rel);
				if (av1 != null)
				{
					av1.hidden = false;
					av1.addToGraph(graphData.getGraph());
				}
			}
		if (attack.related_cve != null)
			for (String rel : attack.related_cve)
			{
				AttackVector av1 = attacks.get(rel);
				if (av1 != null)
				{
					av1.hidden = false;
					av1.addToGraph(graphData.getGraph());
				}
			}
		if (attack.related_capec != null)
			for (String rel : attack.related_capec)
			{
				AttackVector av1 = attacks.get(rel);
				if (av1 != null)
				{
					av1.hidden = false;
					av1.addToGraph(graphData.getGraph());
				}
			}
		
		
		search:
		for (AttackVector av : attacks.values())
		{
			if (av == null)
				continue;
			if (av.related_cwe != null)
				for (String rel : av.related_cwe)
				{
					if (rel.equals(attackId))
					{
						av.hidden = false;
						av.addToGraph(graphData.getGraph());
						continue search;
					}
				}
			if (av.related_cve != null)
				for (String rel : av.related_cve)
				{
					if (rel.equals(attackId))
					{
						av.hidden = false;
						av.addToGraph(graphData.getGraph());
						continue search;
					}
				}
			if (av.related_capec != null)
				for (String rel : av.related_capec)
				{
					if (rel.equals(attackId))
					{
						av.hidden = false;
						av.addToGraph(graphData.getGraph());
						continue search;
					}
				}
		}
		
		attack.addToGraph(graphData.getGraph());
		
	}
	
//	public static void computeSizes()
//	{
//		attacks.values().forEach(av ->
//		{
//
//			AttackVector related;
//			if (av.related_cwe != null)
//				for (String s : av.related_cwe)
//				{
//					related = attacks.get(s);
//					if (related != null)
//					{
//						av.size++;
//						related.size++;
//					}
//				}
//			if (av.related_capec != null)
//				for (String s : av.related_capec)
//				{
//					related = attacks.get(s);
//					if (related != null)
//					{
//						av.size++;
//						related.size++;
//					}
//				}
//			if (av.related_cve != null)
//				for (String s : av.related_cve)
//				{
//					related = attacks.get(s);
//					if (related != null)
//					{
//						av.size++;
//						related.size++;
//					}
//				}
//			if (av.type == AttackType.CAPEC)
//			{
////				av.size += (av.related_cwe.length + av.related_cve.length) * 5 - 5;
//			}
//		});
//	}
	
}
