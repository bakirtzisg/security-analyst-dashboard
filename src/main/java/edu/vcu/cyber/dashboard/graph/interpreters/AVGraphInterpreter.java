package edu.vcu.cyber.dashboard.graph.interpreters;

import edu.vcu.cyber.dashboard.data.AttackType;
import edu.vcu.cyber.dashboard.util.NodeUtil;
import org.graphstream.graph.Node;

public class AVGraphInterpreter extends GraphInterpreter
{

	public static boolean showNodeID = false;

	@Override
	public void nodeAdded(String sourceId, long timeId, String nodeId)
	{
		Node node = graph.getNode(nodeId);
		AttackType type = AttackType.getType(nodeId);
		if (node != null)
		{
			NodeUtil.addAttributeValue(node, "ui.class", type.name().toLowerCase());
			node.setAttribute("ui.label", nodeId);
		}
	}

	public void generationComplete()
	{

		graph.getNodeSet().removeIf(node -> node.getEdgeSet().isEmpty());

		graph.getNodeSet().forEach(node ->
		{
			int size = node.getLeavingEdgeSet().size();
			node.setAttribute("ui.size", 4D + Math.sqrt(size));
		});

	}
}
