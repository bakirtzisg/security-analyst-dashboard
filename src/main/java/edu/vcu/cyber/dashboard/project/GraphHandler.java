package edu.vcu.cyber.dashboard.project;

import edu.vcu.cyber.dashboard.data.GraphData;
import edu.vcu.cyber.dashboard.data.GraphType;
import edu.vcu.cyber.dashboard.data.NodeData;
import edu.vcu.cyber.dashboard.util.Attributes;
import edu.vcu.cyber.dashboard.util.NodeUtil;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphHandler
{

	private Map<GraphType, GraphData> graphs;

	public GraphHandler()
	{
		graphs = new HashMap<>();
	}

	protected void registerGraph(GraphData graphData)
	{
		GraphType type = graphData.getGraphType();
		graphs.put(type, graphData);
	}

	public GraphData getGraph(GraphType type)
	{
		return graphs.get(type);
	}


	private boolean isAttackSurfaceVisible;

	public void toggleAttackSurfaces()
	{
		if (isAttackSurfaceVisible)
		{
			// hide the description surface nodes

			Graph graph = getGraph(GraphType.TOPOLOGY).getGraph();

			graph.getEdgeSet().removeIf(edge -> edge.hasAttribute(Attributes.ATTACK_SURFACE));
			graph.getNodeSet().removeIf(node -> node.hasAttribute(Attributes.ATTACK_SURFACE));

			graph.getNodeSet().forEach(node -> NodeUtil.removeCssClass(node, "attack_surface_target"));

		}
		else
		{
			// show the description surface nodes

			Graph graph = getGraph(GraphType.TOPOLOGY).getGraph();
			GraphData surfaces = getGraph(GraphType.ATTACK_SURFACE);


			// add the nodes that don't already exist
			for (NodeData asNode : surfaces.getNodes())
			{
				if (graph.getNode(asNode.getId()) == null)
				{
					Node _node = graph.addNode(asNode.getId());
					_node.addAttribute(Attributes.ATTACK_SURFACE);
					NodeUtil.addCssClass(_node, Attributes.ATTACK_SURFACE);
					System.out.println(_node.getId());
				}
			}


			// add the edges that don't already exist
			surfaces.getNodes().forEach(target ->
			{
				target.getSources().forEach(source ->
				{
					String edgeId = target.getId() + "-" + source.getId();
					if (graph.getEdge(edgeId) == null)
					{
						Edge _edge = graph.addEdge(edgeId, source.getId(), target.getId(), true);
						_edge.addAttribute(Attributes.ATTACK_SURFACE);
						NodeUtil.addCssClass(_edge, Attributes.ATTACK_SURFACE);

						Node node = graph.getNode(target.getId());
						if (node != null)
						{
							NodeUtil.addCssClass(node, "attack_surface_target");
						}
					}

				});
			});

		}

		isAttackSurfaceVisible = !isAttackSurfaceVisible;

	}
}
