package edu.vcu.cyber.dashboard.data;

import edu.vcu.cyber.dashboard.graph.interpreters.GraphInterpreter;
import edu.vcu.cyber.dashboard.util.Attributes;
import edu.vcu.cyber.dashboard.util.NodeUtil;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.AdjacencyListGraph;
import org.graphstream.stream.Sink;

import javax.swing.*;
import java.util.*;
import java.util.function.Predicate;

public class GraphData implements Sink
{


	public static class GraphKey
	{

		public String attrName;
		public String attrType;
		public String forElement;
		public String id;

		public GraphKey()
		{

		}
	}

	private GraphType graphType;
	private Map<String, NodeData> nodes;
	private Graph graph;
	private boolean isDirectedEdges;
	private GraphInterpreter interpreter;

	private List<NodeData> selectedNodes;
	private List<GraphKey> keys;

	private boolean frozen;
	private boolean refreshing;

	/**
	 * @param graphType determines where the graph is used
	 */
	public GraphData(GraphType graphType)
	{
		this.graphType = graphType;
		this.nodes = new HashMap<>();
		this.selectedNodes = new ArrayList<>();

		interpreter = graphType.getInterpreter();
	}

	public void addKey(GraphKey key)
	{
		if (keys == null)
			keys = new ArrayList<>();
		keys.add(key);
	}

	public String getKeyName(String id)
	{
		for (GraphKey key : keys)
			if (key.id.equals(id))
				return key.attrName;
		return id;
	}

	public String getKeyId(String name)
	{
		for (GraphKey key : keys)
			if (key.attrName.equals(name))
				return key.id;
		return name;
	}

	public List<GraphKey> getKeys()
	{
		return keys;
	}

	public void clear()
	{
		nodes.clear();
		selectedNodes.clear();
	}

	public void setSelectedNodes(List<Node> nodes)
	{
		clearSelected();
		for (Node node : nodes)
		{
			if (!node.hasAttribute(Attributes.NODE_SELECTED))
				node.addAttribute(Attributes.NODE_SELECTED);
			selectedNodes.add(getNode(node));
		}
	}

	public void addSelectedNodes(Node... nodes)
	{
//		this.selectedNodes.clear();
		for (Node node : nodes)
		{
			NodeData nd = getNode(node);
			if (!selectedNodes.contains(nd))
				selectedNodes.add(nd);
		}
	}

	public boolean isFrozen()
	{
		return frozen;
	}

	public void toggleFrozen()
	{
		frozen = !frozen;
	}

//	public void clearSelectedNodes()
//	{
//		clearSelected();
//		selectedNodes.clear();
//	}

	public NodeData getLastSelectedNode()
	{
		if (!selectedNodes.isEmpty())
		{
			return selectedNodes.get(selectedNodes.size() - 1);
		}
		return null;
	}

	public List<NodeData> getSelectedNodes()
	{
		return selectedNodes;
	}

	/**
	 * @return the graph object
	 */
	public synchronized Graph getGraph()
	{
		if (graph == null || refreshing)
			generateGraph();
		return graph;
	}

	public String getDisplayString()
	{
		String output = "Data size: " + graph.getNodeSet().size();

		if (!selectedNodes.isEmpty())
			output += "   |   Selected: " + selectedNodes.size();

		if (frozen)
			output += "   |   [Layout Frozen]";

		return output;
	}

	/**
	 * @return the type of graph that it was created as
	 */
	public GraphType getGraphType()
	{
		return graphType;
	}

	/**
	 * sets whether the edges in the graph should include a direction
	 */
	public void setDirectedEdges(boolean isDirectedEdges)
	{
		this.isDirectedEdges = isDirectedEdges;
	}

	/**
	 * creates and returns a node with the given id
	 */
	public NodeData addNode(String id)
	{
		return nodes.computeIfAbsent(id, item -> new NodeData(id));
	}

	/**
	 * creates and returns a node with the given id
	 */
	public NodeData addNode(NodeData node)
	{
		return nodes.computeIfAbsent(node.getId(), n -> node);
	}

	/**
	 * Creates an edge between two nodes
	 */
	public void addEdge(String source, String target)
	{
		NodeData sourceNode = nodes.get(source);
		NodeData targetNode = nodes.get(target);
		if (sourceNode != null && targetNode != null)
		{
			targetNode.addSource(sourceNode);
			sourceNode.addTarget(targetNode);
		}
	}

	public void refreshGraph()
	{
		refreshing = true;
		SwingUtilities.invokeLater(this::getGraph);
	}

	public void generateGraph()
	{
		generateGraph(false);
	}

	/**
	 * Uses the node data provided to generate a full graph
	 */
	public void generateGraph(boolean clear)
	{
		if (clear && graph != null)
		{
			clear();
		}
		if (graph == null)
		{
			graph = new AdjacencyListGraph(graphType.name());
		}
		graph.removeSink(this);

		if (interpreter != null)
		{
			interpreter.init(graph);
		}

		graph.setAttribute(Attributes.GRAPH_ANTIALIAS, true);
		graph.setAttribute(Attributes.GRAPH_STYLESHEET, "url('./src/main/resources/" + graphType.stylesheet + "')");


		nodes.forEach((key, val) -> val.setNode(graph.addNode(key)));
		nodes.forEach((key, val) -> val.sources.forEach(node -> graph.addEdge(key + "-" + node.id, val.id, node.id, isDirectedEdges)));

		graph.addSink(this);

		refreshing = false;
	}


	public boolean hasNode(String id)
	{
		return nodes.get(id) != null;
	}

	/**
	 * Deletes a node from the graph and from the stored data
	 */
	public void removeNode(String id)
	{
		nodes.remove(id);
		graph.removeNode(id);
	}

	/**
	 * gets the node on the graph with the given ID
	 */
	public NodeData getNode(String id)
	{
		return nodes.get(id);
	}

	/**
	 * Attempts to find the node data that matches the given node's id
	 */
	public NodeData getNode(Node node)
	{
		return nodes.get(node.getId());
	}

	/**
	 * @return all registered nodes
	 */
	public Collection<NodeData> getNodes()
	{
		return nodes.values();
	}

	/**
	 * gets all the nodes on the graph that matches the given predicate
	 */
	public List<NodeData> getNodesMatching(Predicate<NodeData> predicate)
	{
		List<NodeData> ret = new ArrayList<>();
		for (NodeData node : nodes.values())
		{
			if (predicate.test(node))
			{
				ret.add(node);
			}
		}
		return ret;
	}

	/**
	 * deselects all the nodes on a graph
	 */
	public void clearSelected()
	{
		selectedNodes.forEach(nd ->
		{
			if (nd != null && nd.getNode() != null)
				nd.getNode().removeAttribute(Attributes.NODE_SELECTED);
		});
		this.selectedNodes.clear();
//		for (Node node : graph.getNodeSet())
//		{
//			node.removeAttribute(Attributes.NODE_SELECTED);
//		}
	}

	public void flagRemoval(String id)
	{
		NodeData data = nodes.get(id);
		if (data != null && data.graphNode != null)
		{
			data.graphNode.addAttribute(Attributes.REMOVE);
			data.graphNode = null;
		}
	}

	public void flagRemoval(NodeData data)
	{
		if (data != null && data.graphNode != null)
		{
			data.graphNode.addAttribute(Attributes.REMOVE);
			data.graphNode = null;
		}
	}

	public void removeFlagged()
	{
		graph.getNodeSet().removeIf(node -> node.hasAttribute(Attributes.REMOVE));
	}


	@Override
	public void nodeAdded(String sourceId, long timeId, String nodeId)
	{
		NodeData node = nodes.computeIfAbsent(nodeId, NodeData::new);
		if (node.graphNode == null)
		{
			node.graphNode = graph.getNode(nodeId);
		}

	}

	@Override
	public void nodeRemoved(String sourceId, long timeId, String nodeId)
	{
		NodeData node = nodes.get(nodeId);
		if (node != null)
		{
			node.graphNode = null;
		}
	}

	@Override
	public void edgeAdded(String sourceId, long timeId, String edgeId, String fromNodeId, String toNodeId, boolean directed)
	{
		NodeData from = nodes.get(fromNodeId);
		NodeData to = nodes.get(toNodeId);

		to.addSource(from);
		if (!directed)
		{
			from.addSource(to);
		}
	}

	@Override
	public void edgeRemoved(String sourceId, long timeId, String edgeId)
	{

	}

	@Override
	public void graphCleared(String sourceId, long timeId)
	{
		nodes.clear();
	}

	@Override
	public void stepBegins(String sourceId, long timeId, double step)
	{

	}

	public void applyFilter(Predicate<NodeData> predicate)
	{
		graph.getNodeSet().forEach(node -> NodeUtil.removeCssClass(node, Attributes.CSS_HIDDEN));
		for (NodeData nd : nodes.values())
		{
			boolean hide = predicate.test(nd);
			if (hide)
			{
				if (!nd.hidden)
				{
					NodeUtil.addCssClass(nd.graphNode, Attributes.CSS_HIDDEN);
					nd.graphNode.getEdgeSet().forEach(edge -> NodeUtil.addCssClass(edge, Attributes.CSS_HIDDEN));
				}
			}
			else
			{
				if (nd.hidden)
				{
					NodeUtil.removeCssClass(nd.graphNode, Attributes.CSS_HIDDEN);
					nd.graphNode.getEdgeSet().forEach(edge -> NodeUtil.removeCssClass(edge, Attributes.CSS_HIDDEN));
				}
			}
			nd.hidden = hide;
		}
	}

	@Override
	public void graphAttributeAdded(String sourceId, long timeId, String attribute, Object value)
	{

	}

	@Override
	public void graphAttributeChanged(String sourceId, long timeId, String attribute, Object oldValue, Object newValue)
	{

	}

	@Override
	public void graphAttributeRemoved(String sourceId, long timeId, String attribute)
	{

	}

	@Override
	public void nodeAttributeAdded(String sourceId, long timeId, String nodeId, String attribute, Object value)
	{
	}

	@Override
	public void nodeAttributeChanged(String sourceId, long timeId, String nodeId, String attribute, Object oldValue, Object newValue)
	{
	}

	@Override
	public void nodeAttributeRemoved(String sourceId, long timeId, String nodeId, String attribute)
	{
	}

	@Override
	public void edgeAttributeAdded(String sourceId, long timeId, String edgeId, String attribute, Object value)
	{

	}

	@Override
	public void edgeAttributeChanged(String sourceId, long timeId, String edgeId, String attribute, Object oldValue, Object newValue)
	{

	}

	@Override
	public void edgeAttributeRemoved(String sourceId, long timeId, String edgeId, String attribute)
	{

	}

	public boolean isDirected()
	{
		return isDirectedEdges;
	}

	public void clearFocus()
	{
		graph.getNodeSet().forEach(node -> NodeUtil.removeCssClass(node, Attributes.CSS_FOCUS));
	}

	public void setFocus(String nodeId)
	{
		NodeData nd = nodes.get(nodeId);
		if (nd != null && nd.getNode() != null)
		{
			NodeUtil.addCssClass(nd.getNode(), Attributes.CSS_FOCUS);
		}
	}
}
