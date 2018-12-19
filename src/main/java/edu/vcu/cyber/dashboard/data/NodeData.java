package edu.vcu.cyber.dashboard.data;

import edu.vcu.cyber.dashboard.util.NodeUtil;
import org.graphstream.graph.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeData
{
	protected String id;
	protected Map<String, Object> attributes;
	protected List<NodeData> sources;
	protected List<NodeData> targets;
	
	protected boolean hidden;
	
	protected Node graphNode;
	
	public NodeData(String id)
	{
		this.id = id;
		this.attributes = new HashMap<>();
		this.sources = new ArrayList<>();
		this.targets = new ArrayList<>();
	}
	
	public void addSource(NodeData src)
	{
		if (!sources.contains(src))
		{
			sources.add(src);
		}
	}
	
	public void addTarget(NodeData src)
	{
		if (!targets.contains(src))
		{
			targets.add(src);
		}
	}
	
	public boolean isHidden()
	{
		return hidden;
	}
	
	public String getId()
	{
		return id;
	}
	
	public List<NodeData> getSources()
	{
		return sources;
	}
	
	public List<NodeData> getTargets()
	{
		return targets;
	}
	
	
	public void setAttribute(String key, Object val)
	{
		if (graphNode != null)
		{
			graphNode.setAttribute(key, val);
		}
		attributes.put(key, val);
	}
	
	public <T> T getAttribute(String key)
	{
		return (T) attributes.get(key);
	}
	
	public boolean hasAttribute(String key)
	{
		return attributes.containsKey(key);
	}
	
	public boolean hasAttributeValue(Object val)
	{
		return attributes.containsValue(val);
	}
	
	public void removeAttribute(String key)
	{
		if (graphNode != null)
		{
			graphNode.removeAttribute(key);
		}
		attributes.remove(key);
	}
	
	public void addToExistingAttributes(String key, String val)
	{
		if (graphNode != null)
		{
			NodeUtil.addAttributeValue(graphNode, key, val);
		}
	}
	
	public void removeFromExistingAttributes(String key, String val)
	{
		if (graphNode != null)
		{
			NodeUtil.removeAttributeValue(graphNode, key, val);
		}
	}
	
	public void setNode(Node node)
	{
		this.graphNode = node;
		attributes.forEach(node::setAttribute);
	}
	
	public Node getNode()
	{
		return graphNode;
	}
	
	public boolean equals(Object obj)
	{
		return obj instanceof NodeData && ((NodeData) obj).id.equals(id);
	}
	
	public Map<String, Object> getAttributes()
	{
		return attributes;
	}
}
