package edu.vcu.cyber.dashboard.util;

import edu.vcu.cyber.dashboard.data.GraphData;
import org.graphstream.graph.Graph;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.naming.directory.AttributeInUseException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;

public class GraphExporter
{
	public static void exportGraph(GraphData graphData, File file)
	{
		try
		{

			if (file.exists() || file.createNewFile())
			{
				System.out.println("Exporting graph: " + graphData.getGraph().getId() + "...");

				Graph graph = graphData.getGraph();

				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();

				Document doc = db.newDocument();
				Element root = doc.createElement("graphml");
				root.setAttribute("xmlns", "http://graphml.graphdrawing.org/xmlns");
				root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
				root.setAttribute("xsi:schemaLocation", "http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd");


				if (graphData.getKeys() != null)
				{
					graphData.getKeys().forEach(key ->
					{
						Element n = doc.createElement("key");
						n.setAttribute("attr.name", key.attrName);
						n.setAttribute("attr.type", key.attrType);
						n.setAttribute("for", key.forElement);
						n.setAttribute("id", key.id);
						root.appendChild(n);
					});
				}

				Element graphNode = doc.createElement("graph");
				graphNode.setAttribute("edgedefault", graphData.isDirected() ? "directed" : "undirected");

				graphData.getNodes().forEach(node ->
				{
					if (!node.hasAttribute(Attributes.ATTACK_SURFACE))
					{
						Element ele = doc.createElement("node");
						if (node.hasAttribute("ui.label"))
							ele.setAttribute("id", node.getAttribute("ui.label"));
						else
							ele.setAttribute("id", node.getId());

						node.getAttributes().forEach((key, val) ->
						{
							if (key.startsWith("attr."))
							{
								key = key.substring(5);
								Element data = doc.createElement("data");
								data.setAttribute("key", key);
								data.setTextContent(val.toString());

								ele.appendChild(data);
							}
						});

						graphNode.appendChild(ele);
					}
				});

				graph.getEdgeSet().forEach(edge ->
				{
					Element e = doc.createElement("edge");
					e.setAttribute("source", edge.getNode0().getId());
					e.setAttribute("target", edge.getNode1().getId());

					graphNode.appendChild(e);
				});

				root.appendChild(graphNode);

				doc.appendChild(root);

				Transformer tr = TransformerFactory.newInstance().newTransformer();
				tr.setOutputProperty(OutputKeys.INDENT, "yes");
				tr.setOutputProperty(OutputKeys.METHOD, "xml");
				tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

				tr.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(file)));

				System.out.println("Exported to " + file.getAbsolutePath());
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

	}

	/**
	 * Exports a full graph to an XML file
	 */
	public static void exportGraph(GraphData graphData)
	{
		try
		{

			File file = new File("./export/", graphData.getGraph().getId() + ".xml");
			if (!file.exists())
			{
				new File("./export/").mkdirs();
				file.createNewFile();
			}

			exportGraph(graphData, file);


		}
		catch (Exception e)
		{
			e.printStackTrace();
		}


	}
}
