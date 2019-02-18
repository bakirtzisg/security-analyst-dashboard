package edu.vcu.cyber.dashboard.graph.listeners;

import edu.vcu.cyber.dashboard.Config;
import edu.vcu.cyber.dashboard.actions.ActionManager;
import edu.vcu.cyber.dashboard.actions.ActionNodeDelete;
import edu.vcu.cyber.dashboard.av.AttackVector;
import edu.vcu.cyber.dashboard.av.AttackVectors;
import edu.vcu.cyber.dashboard.data.*;
import edu.vcu.cyber.dashboard.project.AppSession;
import edu.vcu.cyber.dashboard.ui.AttackVectorInfoPanel;
import edu.vcu.cyber.dashboard.util.Attributes;
import edu.vcu.cyber.dashboard.util.NodeUtil;
import edu.vcu.cyber.dashboard.data.SystemAnalysis;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.List;

public class IBDActionListener extends GraphActionListener
{

	private AppSession session;

	public IBDActionListener(AppSession project)
	{
		this.session = project;
	}


	@Override
	public void onSelection(List<Node> selectedNodes)
	{
		super.onSelection(selectedNodes);
		setSelected(selectedNodes.toArray(new Node[]{}));
	}

	@Override
	public void onSingleClick(Node node)
	{
//		resetGraphAttributes(graph);
		if (node == null)
		{
			setSelected();
		}
		else
		{
			setSelected(node);

//			resetGraphAttributes(graph);
//			SwingUtilities.invokeLater(() ->
//			{
//				markPaths(graphData, node);
//			});
		}
		super.onSingleClick(node);
	}

	private void setSelected(Node... nodes)
	{
//		graphData.clearSelected();
//		graphData.clearSelectedNodes();
		GraphData other;
		if (graphData.getGraphType() == GraphType.TOPOLOGY)
		{
			other = session.getSpecGraph();
		}
		else
		{
			other = session.getTopGraph();
		}

		resetGraphAttributes(graphData.getGraph());
		if (Config.USE_SPEC_GRAPH)
		{
			resetGraphAttributes(other.getGraph());
			other.clearSelected();
		}

		SwingUtilities.invokeLater(() ->
		{


			if (nodes != null)
			{
				for (Node node : nodes)
				{
					if (!node.hasAttribute(Attributes.ATTACK_VECTOR))
					{
						markPaths(graphData, node);
						node.setAttribute(Attributes.NODE_SELECTED);

						if (Config.USE_SPEC_GRAPH)
						{
							NodeData data = other.getNode(node.getId());
							if (data != null)
							{
								data.getNode().setAttribute(Attributes.NODE_SELECTED);
								markPaths(other, data.getNode());
							}
						}
					}
				}
			}
		});

	}

	@Override
	public void onDoubleClick(Node node)
	{
		if (node != null)
		{
			if (graph.getNode(node.getId()) != null)
			{
				if (graph.getNode(node.getId()).hasAttribute(Attributes.ATTACK_VECTOR))
				{
					AttackVector av = AttackVectors.getAttackVector(node.getId());
					if (av != null)
					{
						AttackVectorInfoPanel.display(av);
					}
				}
				else
				{
					resetGraphAttributes(graph);
					SwingUtilities.invokeLater(() ->
					{

//						String inputFile = new File("data/topology.graphml").getAbsolutePath();
//						CybokQueryHandler.sendQuery(new TaxanomicSearch(inputFile, node.getId()));
//						CybokQueryHandler.sendQuery(new AttackVectorQuery(inputFile));
						GraphData avGraph = session.getAvGraph();

						AttackVectors.showInGraph(av -> av.violatedComponents.contains(node.getId()));
					});
				}
			}

		}
	}


	private static void resetGraphAttributes(Graph graph)
	{
		NodeUtil.clearAllAttributesOf(graph, Attributes.PATH_MARK);
		NodeUtil.clearAllAttributesOf(graph, Attributes.CSS_ATTACK_PATH);
//		graph.getEdgeSet().forEach(e -> NodeUtil.removeCssClass(e, Attributes.ATTACK_SURFACE));
		graph.getEdgeSet().forEach(IBDActionListener::clear);
		graph.getNodeSet().forEach(IBDActionListener::clear);
	}

	private static void clear(Element e)
	{
		NodeUtil.removeCssClass(e, Attributes.CSS_PATH);
		NodeUtil.removeCssClass(e, Attributes.CSS_UNFOCUSED);
		NodeUtil.removeCssClass(e, Attributes.CSS_ATTACK_PATH);
	}

	private static void markPaths(GraphData graphData, Node node)
	{
		node = graphData.getGraph().getNode(node.getId());

		resetGraphAttributes(graphData.getGraph());

		if (graphData.getGraphType() == GraphType.SPECIFICATIONS)
		{

			markPaths(node, true);
			markPaths(node, false);
		}
		else if (!node.hasAttribute(Attributes.ATTACK_VECTOR))
		{

			SystemAnalysis.getAttackChains(node.getId(), chains ->
			{
				if (chains != null)
				{
					for (String[] path : chains)
					{
						markPath(graphData.getGraph(), path);
					}
				}
			});

//			List<String[]> paths = SystemAnalysis.attackPaths.get(node.getId());
//			if (paths != null)
//			{
//				for (String[] path : paths)
//				{
//					markPath(graphData.getGraph(), path);
//				}
//			}
		}
	}

	private static void markPath(Graph graph, String[] path)
	{
//		for (int i = 0; i < path.length; i++)
//		{
//			System.out.printf("%s, ", path[i]);
//		}
//		System.out.println("\n");

		for (int i = 0; i < path.length; i++)
		{
			Node node = graph.getNode(path[i]);
			if (node != null && !node.hasAttribute(Attributes.CSS_ATTACK_PATH))
			{
				NodeUtil.addCssClass(node, Attributes.CSS_ATTACK_PATH);
				node.setAttribute(Attributes.CSS_ATTACK_PATH);
			}
			if (i > 0)
			{
				String edgeId = path[i - 1] + "-" + path[i];
//				System.out.printf("[%s -> %s]\n", path[i - 1], path[i]);
				Edge edge = graph.getEdge(edgeId);
				if (edge != null)// && !edge.hasAttribute(Attributes.CSS_ATTACK_PATH))
				{
					if (edge.hasAttribute(Attributes.ATTACK_SURFACE))
					{
						NodeUtil.addCssClass(edge, Attributes.ATTACK_SURFACE);
					}
					else
					{
						NodeUtil.addCssClass(edge, Attributes.CSS_ATTACK_PATH);
					}
					edge.setAttribute(Attributes.CSS_ATTACK_PATH);

				}
			}
		}

	}


	private static void markPaths(Node node, boolean dir)
	{
		if (node.hasAttribute(Attributes.PATH_MARK))
			return;

		node.addAttribute(Attributes.PATH_MARK);

		NodeUtil.removeCssClass(node, Attributes.CSS_UNFOCUSED);
		NodeUtil.addCssClass(node, Attributes.CSS_PATH);

		Collection<Edge> edgeSet = dir ? node.getEnteringEdgeSet() : node.getLeavingEdgeSet();

		for (Edge edge : edgeSet)
		{
			if (edge.isDirected())
			{
				Node other = edge.getOpposite(node);

				NodeUtil.removeCssClass(edge, Attributes.CSS_UNFOCUSED);
				NodeUtil.addCssClass(edge, Attributes.CSS_PATH);

				markPaths(other, dir);
			}
		}

		node.removeAttribute(Attributes.PATH_MARK);
	}

	public void onKeyPress(KeyEvent evt)
	{
		super.onKeyPress(evt);
		switch (evt.getKeyCode())
		{
			case KeyEvent.VK_DELETE: // delete -> delete the selected nodes from the graph

				for (NodeData node : graphData.getSelectedNodes())
				{
					Node n = graph.getNode(node.getId());
					if (n != null && n.hasAttribute(Attributes.ATTACK_VECTOR))
						graph.removeNode(node.getNode().getId());
				}

				graphData.clearSelected();
				break;

		}
	}
}
