package edu.vcu.cyber.dashboard.util;

import edu.vcu.cyber.dashboard.data.AttackType;
import edu.vcu.cyber.dashboard.data.GraphData;
import edu.vcu.cyber.dashboard.data.GraphType;
import edu.vcu.cyber.dashboard.data.NodeData;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class NodeUtil
{

	public static void printAttribute(Element e, String key)
	{
		System.out.print(e.getId() + " [" + key + " = ");
		if (e.hasAttribute(key))
		{
			Object obj = e.getAttribute(key);
			if (obj instanceof String)
			{
				System.out.print(obj);
			}
			else if (obj instanceof Object[])
			{
				Object[] vals = (Object[]) obj;
				for (int i = 0; i < vals.length; i++)
				{
					System.out.print(vals[i]);
					if (i < vals.length - 1)
					{
						System.out.print(", ");
					}
				}
			}
		}
		System.out.print("]\n");
	}

	public static void clearAllAttributesOf(Graph graph, String key)
	{
		graph.getNodeSet().forEach(n -> n.removeAttribute(key));
		graph.getEdgeSet().forEach(e -> e.removeAttribute(key));
	}

	public static void addCssClass(Element element, String cls)
	{
		if (!element.hasAttribute("ui.cls." + cls))
		{
			addAttributeValue(element, Attributes.STYLE_CLASS, cls);
			element.setAttribute("ui.cls." + cls);
		}
	}

	public static void removeCssClass(Element element, String cls)
	{
		if (element.hasAttribute("ui.cls." + cls))
		{
			removeAttributeValue(element, Attributes.STYLE_CLASS, cls);
			element.removeAttribute("ui.cls." + cls);
		}
	}

	public static void addAttributeValue(Element element, String key, Object cls)
	{
		if (element.hasAttribute(key))
		{
			Object val = element.getAttribute(key);
			if (val instanceof String)
			{
				if (!val.equals(cls))
				{
					element.setAttribute(key, val, cls);
				}
			}
			else
			{
				Object[] current = (Object[]) val;
				for (Object o : current)
				{
					if (cls.equals(o))
						return;
				}
				element.setAttribute(key, current, cls);
			}

		}
		else
		{
			element.setAttribute(key, cls);
		}
	}


	public static void setAttributesForNodeAndEdges(Element element, String key, Object val)
	{
		if (element instanceof Node)
		{
			addAttributeValue(element, key, val);
		}

	}

	public static void removeAttributeValue(Element element, String key, Object cls)
	{
		if (element.hasAttribute(key))
		{
			Object val = element.getAttribute(key);
			if (val instanceof String)
			{
				if (val.equals(cls))
				{
					element.removeAttribute(key);
				}
			}
			else
			{
				Object[] current = (Object[]) val;
				if (current.length == 1)
				{
					if (current[0].equals(val))
					{
						element.removeAttribute(key);
					}
				}
				else
				{
					int i, cur;
					for (i = 0, cur = 0; i < current.length; i++)
					{
						if (!current[i].equals(cls))
						{
							current[cur++] = current[i];
						}
					}
					Object[] newArr = new Object[cur];
					System.arraycopy(current, 0, newArr, 0, newArr.length);
					element.setAttribute(key, newArr);
				}
			}

		}
	}

	public static String generateHtmlString(NodeData node)
	{

		StringBuilder builder = new StringBuilder();

		builder.append("<html>");

		builder.append("<h3>").append(node.getId()).append("</h3>");

		Map<String, Object> attributes = node.getAttributes();

		for (Map.Entry<String, Object> entry : attributes.entrySet())
		{

			builder.append("<p><b>")
					.append(entry.getKey())
					.append(": </b>")
					.append(entry.getValue())
					.append("</p>");

		}


		builder.append("</html>");

		return builder.toString();

	}

	public static List<String> getInfoTextArray(NodeData node)
	{
		List<String> info = new ArrayList<>();
		info.add(node.getId());

		Map<String, Object> attributes = node.getAttributes();

		for (Map.Entry<String, Object> entry : attributes.entrySet())
		{

			if (!entry.getValue().equals(""))
			{

				String key = entry.getKey().replace("attr.", "");

				switch (key)
				{
					case "text":
						info.add(entry.getValue().toString());
						break;
					case "type":
						break;
					default:
						info.add(key + ": " + entry.getValue());

				}
			}

		}

		return info;


	}

	public static void toggleConsumed(GraphData graphData, String nodeId)
	{
		NodeData nodeData = graphData.getNode(nodeId);
		if (nodeData != null && nodeData.getNode() != null)
		{
			Node node = nodeData.getNode();

			AttackType t1 = AttackType.getType(node);

			Iterator<Node> it = node.getNeighborNodeIterator();
			while (it.hasNext())
			{
				Node o = it.next();
				AttackType t2 = AttackType.getType(o);
				if (t1.canConsume(t2) && o.getEdgeSet().size() == 1)
				{
					graphData.flagRemoval(o.getId());
				}
			}

		}

		graphData.removeFlagged();

	}

}
