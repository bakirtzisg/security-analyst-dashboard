package edu.vcu.cyber.dashboard.util;

import edu.vcu.cyber.dashboard.av.AttackVector;
import edu.vcu.cyber.dashboard.av.Relation;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

public class Projection
{


	public static void project(Graph graph, AttackVector av)
	{
		Node node = graph.getNode(av.qualifiedName);

		if (node == null)
		{
			node = graph.addNode(av.qualifiedName);
		}

		node.setAttribute(Attributes.HOVER_TEXT, av.description);
		node.setAttribute(Attributes.ATTACK_VECTOR);


		av.size = av.relations.size();

		double size = av.size * Config.AV_NODE_SCALE;

		double nodeSize = Math.sqrt(size) + Config.AV_NODE_MIN_SIZE;
		double layoutWeight = 2.0D / ((Math.sqrt(size) + 1) + 0.015);

		if (av.size > Config.AV_LAYOUT_MIN_SIZE)
			node.setAttribute(Attributes.LAYOUT_WEIGHT, layoutWeight);
		else
			node.setAttribute(Attributes.LAYOUT_WEIGHT, Config.AV_LAYOUT_WEIGHT_DEF);

		node.setAttribute(Attributes.UI_SIZE, nodeSize);

		av.relations.forEach(rel -> addEdge(rel, graph));

		av.violatedComponents.forEach(comp ->
				addViolationEdge(av.qualifiedName, comp, graph));

		node.setAttribute(Attributes.STYLE_CLASS, av.type.css);
		NodeUtil.addCssClass(node, Attributes.CSS_ATTACK_VECTOR);
		NodeUtil.addAttributeValue(node, Attributes.DONT_SAVE, true);
	}

	public static void addViolationEdge(String avid, String comp, Graph graph)
	{

		String eid = avid + "-" + comp;
		Edge edge = graph.getEdge(eid);
		if (edge == null)
		{
			edge = graph.addEdge(eid, avid, comp);
			NodeUtil.addCssClass(edge, Attributes.CSS_ATTACK_VECTOR);
		}

	}

	public static void addEdge(Relation rel, Graph graph)
	{
		Node n1 = graph.getNode(rel.getChild().qualifiedName);
		Node n2 = graph.getNode(rel.getParent().qualifiedName);
		if (n1 != null && n2 != null && !rel.hasEdge(graph))
		{
			Edge edge = graph.addEdge(rel.getEdgeId(), rel.getParent().qualifiedName, rel.getChild().qualifiedName);
			NodeUtil.addCssClass(edge, Attributes.CSS_ATTACK_VECTOR);
		}
	}

	private static boolean hasNode(Graph graph, String id)
	{
		return graph.getNode(id) != null;
	}
}
