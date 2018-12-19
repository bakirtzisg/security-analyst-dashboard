package edu.vcu.cyber.dashboard.util;

import edu.vcu.cyber.dashboard.data.GraphData;
import edu.vcu.cyber.dashboard.data.NodeData;
import edu.vcu.cyber.dashboard.data.GraphType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class GraphMLParser
{
	
	public static GraphData parse(File file, GraphType graphType)
	{
		GraphData graphData = new GraphData(graphType);
		return parse(file, graphData);
	}
	
	public static GraphData parse(File file, GraphData graphData)
	{
		try
		{
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			
			NodeList nodeList = doc.getElementsByTagName("graph");
			for (int i = 0; i < nodeList.getLength(); i++)
			{
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE)
				{
					String edgeDef = ((Element) node).getAttribute("edgedefault");
					graphData.setDirectedEdges("directed".equals(edgeDef));
				}
			}
			
			nodeList = doc.getElementsByTagName("key");
			for (int i = 0; i < nodeList.getLength(); i++)
			{
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE)
				{
					GraphData.GraphKey key = new GraphData.GraphKey();
					key.attrName = ((Element) node).getAttribute("attr.name");
					key.attrType = ((Element) node).getAttribute("attr.type");
					key.forElement = ((Element) node).getAttribute("for");
					key.id = ((Element) node).getAttribute("id");
					graphData.addKey(key);
				}
			}
			
			
			nodeList = doc.getElementsByTagName("node");
			for (int i = 0; i < nodeList.getLength(); i++)
			{
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE)
				{
					String nodeId = ((Element) node).getAttribute("id");
					
					NodeData ndata = graphData.addNode(nodeId);
					NodeList dataList = ((Element) node).getElementsByTagName("data");
					for (int j = 0; j < dataList.getLength(); j++)
					{
						Node data = dataList.item(j);
						if (data.getNodeType() == Node.ELEMENT_NODE)
						{
							String key = ((Element) data).getAttribute("key");
							String value = data.getTextContent();
							ndata.setAttribute("attr." + key, value);
						}
					}
				}
			}
			
			nodeList = doc.getElementsByTagName("edge");
			for (int i = 0; i < nodeList.getLength(); i++)
			{
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE)
				{
					String source = ((Element) node).getAttribute("source");
					String target = ((Element) node).getAttribute("target");
					
					graphData.addEdge(source, target);
				}
			}
			
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return graphData;
	}
	
	
}
