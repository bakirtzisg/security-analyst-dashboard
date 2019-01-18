package edu.vcu.cyber.dashboard.av;

import edu.vcu.cyber.dashboard.data.AttackType;
import edu.vcu.cyber.dashboard.data.GraphData;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class AttackVectors
{
	
	private static AttackVectorVisualizer vis;
	
	public static void setVisualizer(AttackVectorVisualizer nvis)
	{
		if (vis != null)
			vis.dispose();
		
		vis = nvis;
		
		vis.populate();
	}
	
	public static AttackVectorVisualizer vis()
	{
		return vis;
	}
	
	
	private static Map<String, AttackVector> attacks = new HashMap<>();
	
	public static int numCapec = 0;
	public static int numCwe = 0;
	public static int numCve = 0;
	public static int numEdges = 0;
	
	
	public AttackVectors()
	{
		attacks = new HashMap<>();
	}
	
	public static AttackVector addAttack(AttackVector att, String violated)
	{
		AttackVector attack = attacks.computeIfAbsent(att.qualifiedName, key -> att);
		attack = attacks.getOrDefault(attack.qualifiedName, attack);
		if (!attack.violatedComponents.contains(violated))
			attack.violatedComponents.add(violated);
		
		return attack;
	}
	
	public static void printStats()
	{
		System.out.printf("Attack Vectors[size: %d, CAPEC: %d, CWE: %d, CVE: %d, edges: %d", attacks.size(), numCapec, numCwe, numCve, numEdges);
	}
	
	public static AttackVector getAttackVector(String id)
	{
		return attacks.get(id);
	}
	
	public static Collection<AttackVector> getAllAttackVectors()
	{
		return attacks.values();
	}
	
	public static void filter(Predicate<AttackVector> predicate)
	{
		if (vis != null)
			vis.filterAttacks(predicate);
	}
	
	public static void hideAttacks(Predicate<AttackVector> predicate)
	{
		for (AttackVector av : attacks.values())
		{
			av.hidden = predicate.test(av);
		}
	}
	
	public static void showInGraph(Predicate<AttackVector> predicate)
	{
		if (vis != null)
			vis.filterAttacks(predicate);
	}
	
	public static void updateExisting()
	{
		if (vis != null)
			vis.update();
	}
	
	public static void update()
	{
		if (vis != null)
			vis.update();
	}
	
	public static void showRelated(String attackId, boolean force)
	{
		AttackVector av = AttackVectors.getAttackVector(attackId);
		if (av != null)
		{
			showRelated(av, force);
		}
	}
	
	public static void showRelated(AttackVector av, boolean force)
	{
		if (vis != null)
			vis.showAllRelated(av, force);
	}
	
	public static void resolveEdges(String attackId)
	{
		AttackVector av = AttackVectors.getAttackVector(attackId);
		if (av != null)
		{
			resolveEdges(av);
		}
	}
	
	public static void resolveEdges(AttackVector av)
	{
	
	}
	
	
	public static void resolveAttacks()
	{
		numCapec = numCwe = numCve = numEdges = 0;
		forEach(av ->
		{
			if (av.type == AttackType.CAPEC) numCapec++;
			if (av.type == AttackType.CWE) numCwe++;
			if (av.type == AttackType.CVE) numCve++;
			
			addRelation(av, av.related_capec);
			addRelation(av, av.related_cwe);
			addRelation(av, av.related_cve);
			
			av.related_capec = null;
			av.related_cve = null;
			av.related_cwe = null;
		});
	}
	
	private static void addRelation(AttackVector parent, String[] related)
	{
		for (String rel : related)
		{
			AttackVector child = attacks.get(rel);
			if (child != null)
			{
				Relation relation = new Relation(parent, child);
				parent.addRelation(relation);
				child.addRelation(relation);
			}
		}
	}
	
	public static void forEach(final Consumer<AttackVector> consumer)
	{
		attacks.forEach((key, av) -> consumer.accept(av));
	}
	
	public static void clearAll()
	{
		attacks.clear();
	}
}
