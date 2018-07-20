package edu.vcu.cyber.dashboard.graph.listeners;

import edu.vcu.cyber.dashboard.data.GraphData;
import edu.vcu.cyber.dashboard.data.NodeData;
import edu.vcu.cyber.dashboard.graph.renderer.HoverMenuRenderer;
import edu.vcu.cyber.dashboard.project.AppSession;
import edu.vcu.cyber.dashboard.ui.graphpanel.GraphPanel;
import edu.vcu.cyber.dashboard.util.Attributes;
import edu.vcu.cyber.dashboard.util.ApplicationSettings;
import edu.vcu.cyber.dashboard.util.GraphExporter;
import edu.vcu.cyber.dashboard.util.NodeUtil;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.view.View;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public abstract class GraphActionListener
{

	protected GraphData graphData;
	protected Graph graph;
	protected View view;

	protected Node currentNode;

	protected GraphPanel graphPanel;


	private NodeData hoveredNode;

	public GraphData getGraphData()
	{
		return graphData;
	}

	public void init(GraphData graphData, View view, GraphPanel graphPanel)
	{
		this.graphData = graphData;
		this.graph = graphData.getGraph();
		this.view = view;
		this.graphPanel = graphPanel;

	}

	public void onNodeMoved(Node node)
	{
		view.freezeElement((GraphicElement) node, true);
	}

	public void onSelection(List<Node> selectedNodes)
	{
		graphData.setSelectedNodes(selectedNodes);
	}

	public void onSingleClick(Node node)
	{
		currentNode = node;
		if (node != null)
		{

			graphData.clearSelected();
			graphData.addSelectedNodes(node);
		}
		else
		{
			graphData.clearSelected();
		}
	}

	public void onDoubleClick(Node node)
	{
		currentNode = node;
	}

	public boolean onMouseHover(Node node, double mx, double my)
	{
		if (node != null)
		{

			AppSession.setFocus(graphData.getGraphType());

			NodeData nd = graphData.getNode(node.getId());
			if (nd != null && !nd.isHidden())
			{
				List<String> infoArr = new ArrayList<>();
				Node n = graph.getNode(node.getId());
				if (n != null && n.hasAttribute(Attributes.HOVER_TEXT))
				{
					infoArr.add(n.getAttribute(Attributes.HOVER_TEXT));
				}
				else
				{
					infoArr.addAll(NodeUtil.getInfoTextArray(nd));
				}
				if (!infoArr.contains(node.getId()))
				{
					infoArr.add(0, node.getId());
				}
				HoverMenuRenderer.display(node.getId(), infoArr, (int) mx, (int) my, graphData.getGraphType());
				return true;
			}
			else if (hoveredNode != null)
			{
				HoverMenuRenderer.hide();
				return true;
			}
			hoveredNode = null;

		}
		else if (hoveredNode != null)
		{
			HoverMenuRenderer.hide();
			hoveredNode = null;
			return true;
		}
		HoverMenuRenderer.hide();

		return true;
	}

	public void onMousePressed(Node node)
	{
		currentNode = node;
	}

	public void onMouseRelease(Node node)
	{
		currentNode = null;
	}

	public void onMouseEnter()
	{
		currentNode = null;
	}

	public void onMouseExit()
	{
		currentNode = null;
	}

	public void onKeyPress(KeyEvent e)
	{
		if (e.isControlDown())
		{
			if (e.getKeyCode() == KeyEvent.VK_S) // Save graph settings
			{
				try
				{

					ApplicationSettings.saveGraph(graphPanel);
				} catch (Exception e1)
				{
					e1.printStackTrace();
				}
			}
			else if (e.getKeyCode() == KeyEvent.VK_L) // Load saved graph settings
			{
				try
				{
					ApplicationSettings.loadGraph(graphPanel);
				} catch (Exception e1)
				{
					e1.printStackTrace();
				}
			}
			else if (e.getKeyCode() == KeyEvent.VK_E) // Export graph
			{
				try
				{
					GraphExporter.exportGraph(graphData);
				} catch (Exception e1)
				{
					e1.printStackTrace();
				}
			}
			else if (e.getKeyCode() == KeyEvent.VK_F)
			{
				graphData.toggleFrozen();
				boolean frozen = graphData.isFrozen();
				graphPanel.getViewGraph().getNodeSet().forEach(node ->
				{
					if (frozen)
						node.setAttribute("layout.frozen", frozen);
					else
						node.removeAttribute("layout.frozen");

				});
				((GraphicGraph) graphPanel.getViewGraph()).graphChanged = true;
			}
			else if (e.getKeyCode() == KeyEvent.VK_G)
			{
				growSelection();
			}
		}
	}

	private void growSelection()
	{

		GraphicGraph graph = (GraphicGraph) graphPanel.getViewGraph();

		List<NodeData> selected = graphData.getSelectedNodes();
		System.out.println("Selected: " + selected.size());
		List<Node> newSelection = new ArrayList<>();
		for (NodeData sel : selected)
		{
			Node node = graph.getNode(sel.getId());
			if (node != null)
			{
				if (!newSelection.contains(node))
					newSelection.add(node);

				for (Edge edge : node.getEdgeSet())
				{
					Node _node = edge.getOpposite(node);
					if (!newSelection.contains(_node))
					{
						newSelection.add(_node);
					}
				}

			}
		}

		System.out.println("Grow: " + newSelection.size());

		onSelection(newSelection);

	}

}
