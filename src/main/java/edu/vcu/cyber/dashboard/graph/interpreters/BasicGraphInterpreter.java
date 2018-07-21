package edu.vcu.cyber.dashboard.graph.interpreters;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

public class BasicGraphInterpreter extends GraphInterpreter
{
	@Override
	public void nodeAdded(String sourceId, long timeId, String nodeId)
	{
		Node node = graph.getNode(nodeId);
		node.setAttribute("ui.label", nodeId);

		if (initalGenerationDone)
		{
			node.addAttribute("modified");
		}
	}


	@Override
	public void edgeAdded(String sourceId, long timeId, String edgeId, String fromNodeId, String toNodeId, boolean directed)
	{

//		Edge edge = graph.getEdge(edgeId);
//
//		edge.setAttribute("layout.weight", 0.25);

	}
}
