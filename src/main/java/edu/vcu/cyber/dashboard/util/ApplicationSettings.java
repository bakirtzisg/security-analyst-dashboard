package edu.vcu.cyber.dashboard.util;

import edu.vcu.cyber.dashboard.av.AttackVector;
import edu.vcu.cyber.dashboard.av.AttackVectors;
import edu.vcu.cyber.dashboard.data.GraphType;
import edu.vcu.cyber.dashboard.project.AppSession;
import edu.vcu.cyber.dashboard.ui.BucketPanel;
import edu.vcu.cyber.dashboard.ui.DashboardUI;
import edu.vcu.cyber.dashboard.ui.graphpanel.GraphPanel;
import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Graph;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.view.Camera;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;

public class ApplicationSettings
{

	/**
	 * loads the saved instance of a graph, does nothing if no save exists
	 * <p>
	 * loads camera position, zoom, and node positions.
	 * once node position is set, that node will be frozen and the autolayout will not work on it.
	 * <p>
	 * TODO: expand on this later to load more information such as
	 * TODO: node attributes, edge attributes, current selections, etc.
	 */
	public static void loadGraph(GraphPanel graphPanel)
	{

		try
		{
			System.out.println("Loading graph: " + graphPanel.getGraphName() + "...");

			File file = new File("./saves", graphPanel.getGraphName() + ".xml");
			if (!file.exists())
			{
				System.out.println("No save for this graph");
				return;
			}


			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
			NodeList list = doc.getElementsByTagName("camera");
			for (int i = 0; i < list.getLength(); i++)
			{
				Element ele = (Element) list.item(i);
				Point3 center = new Point3();
				center.x = Double.valueOf(ele.getAttribute("x"));
				center.y = Double.valueOf(ele.getAttribute("y"));
				center.z = Double.valueOf(ele.getAttribute("z"));

				Camera cam = graphPanel.getViewPanel().getCamera();
				cam.setViewCenter(center.x, center.y, 0);
				cam.setViewPercent(center.z);
			}

			Graph graph = graphPanel.getGraph();


			list = doc.getElementsByTagName("node");
			for (int i = 0; i < list.getLength(); i++)
			{
				Element ele = (Element) list.item(i);
				String id = ele.getAttribute("id");
				Point3 pos = new Point3();
				pos.x = Double.valueOf(ele.getAttribute("x"));
				pos.y = Double.valueOf(ele.getAttribute("y"));

				org.graphstream.graph.Node node = graph.getNode(id);
				if (node == null)
				{
					AttackVector av = AttackVectors.getAttackVector(id);
					if (av != null)
					{
						node = graph.addNode(id);
					}
				}

				if (node != null)
				{
					node.setAttribute("x", pos.x);
					node.setAttribute("y", pos.y);
					node.addAttribute("layout.frozen");
					node.addAttribute(Attributes.HOLD);

				}
			}


			graph.getNodeSet().removeIf(node -> !node.hasAttribute(Attributes.HOLD));
			graph.getNodeSet().forEach(node -> node.removeAttribute(Attributes.HOLD));

			if (graphPanel.getGraphData().getGraphType() == GraphType.TOPOLOGY)
				AttackVectors.updateExisting();


		}
		catch (Exception e)
		{
			System.out.println("Failed!");
			e.printStackTrace();
		}

	}


	/**
	 * Saves the graph info for the focused graph.
	 * <p>
	 * Saves the camera position and zoom
	 * Saves the position of every node
	 * <p>
	 * TODO: expand on this later to save more information such as
	 * TODO: node attributes, edge attributes, current selections, etc.
	 */
	public static void saveGraph(GraphPanel graphPanel)
	{
		try
		{
			System.out.println("Saving graph: " + graphPanel.getGraphName() + "...");

			File file = new File("./saves/", graphPanel.getGraphName() + ".xml");
			if (!file.exists())
			{
				new File("./saves/").mkdirs();
				file.createNewFile();
			}

			Graph graph = graphPanel.getViewGraph();


			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			Document doc = db.newDocument();
			Element root = doc.createElement("save");

			Element viewerSettings = doc.createElement("viewer");
			Camera camera = graphPanel.getViewPanel().getCamera();
			Element e = doc.createElement("camera");
			Point3 camCenter = camera.getViewCenter();
			double zoom = camera.getViewPercent();
			e.setAttribute("x", camCenter.x + "");
			e.setAttribute("y", camCenter.y + "");
			e.setAttribute("z", zoom + "");
			viewerSettings.appendChild(e);


			root.appendChild(viewerSettings);

			Element nodes = doc.createElement("nodes");

			graph.getNodeSet().forEach(node ->
			{
				if (!node.hasAttribute(Attributes.DONT_SAVE))
				{
					Element ele = doc.createElement("node");

					ele.setAttribute("id", node.getId());

					Point3 pos = Toolkit.nodePointPosition(node);
					ele.setAttribute("x", pos.x + "");
					ele.setAttribute("y", pos.y + "");

					nodes.appendChild(ele);
				}
			});

			root.appendChild(nodes);
			doc.appendChild(root);


			Transformer tr = TransformerFactory.newInstance().newTransformer();
			tr.setOutputProperty(OutputKeys.INDENT, "yes");
			tr.setOutputProperty(OutputKeys.METHOD, "xml");
			tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

			tr.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(file)));

			System.out.println("Saved to " + file.getAbsolutePath());

		}
		catch (Exception e)
		{
			System.out.println("Failed!");
			e.printStackTrace();
		}
	}

	/**
	 * Saves the current state of the UI and all of the graphs
	 * <p>
	 * TODO: include bucket and node additions
	 */
	public static void saveAll(DashboardUI ui)
	{
		try
		{

			System.out.println("Saving UI data...");

			saveGraph(ui.getTopGraphPanel());
			saveGraph(ui.getSpecGraphPanel());
			saveGraph(ui.getAvGraphPanel());
			saveAttacks();

			File file = new File("./saves/", "ui.xml");
			if (!file.exists())
			{
				new File("./saves/").mkdirs();
				file.createNewFile();
			}

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			Document doc = db.newDocument();
			Element root = doc.createElement("save");

			Element field = doc.createElement("field");
			Dimension size = ui.getSize();
			field.setAttribute("key", "window");
			field.setAttribute("width", size.getWidth() + "");
			field.setAttribute("height", size.getHeight() + "");
			root.appendChild(field);


			JSplitPane sp = ui.getBucketSplitPane();
			if (sp != null)
			{
				field = doc.createElement("field");
				field.setAttribute("key", "bucket_split");
				field.setAttribute("value", sp.getDividerLocation() + "");
				root.appendChild(field);
			}

			field = doc.createElement("field");
			field.setAttribute("key", "tab");
			field.setAttribute("value", ui.getGraphTabs().getSelectedIndex() + "");
			root.appendChild(field);

			field = doc.createElement("field");
			field.setAttribute("key", "graph_split");
			field.setAttribute("value", ui.getGraphSplitPane().getDividerLocation() + "");
			root.appendChild(field);


			doc.appendChild(root);


			Transformer tr = TransformerFactory.newInstance().newTransformer();
			tr.setOutputProperty(OutputKeys.INDENT, "yes");
			tr.setOutputProperty(OutputKeys.METHOD, "xml");
			tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

			tr.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(file)));

			System.out.println("Saved to " + file.getAbsolutePath());

		}
		catch (Exception e)
		{
			System.out.println("Failed!");
			e.printStackTrace();
		}
	}

	/**
	 * loads a previous state of the ui and graphs
	 * <p>
	 * TODO: include bucket and node additions
	 */
	public static void loadAll(DashboardUI ui)
	{
		try
		{

			File file = new File("./saves", "ui.xml");
			if (!file.exists())
			{
				System.out.println("No save data");
				return;
			}


			ui.showBucket(false);
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
			NodeList list = doc.getElementsByTagName("field");
			for (int i = 0; i < list.getLength(); i++)
			{
				Element ele = (Element) list.item(i);
				String key = ele.getAttribute("key");
				switch (key)
				{
					case "graph_split":
						ui.getGraphSplitPane().setDividerLocation(Integer.parseInt(ele.getAttribute("value")));
						break;
					case "bucket_split":
						// TODO: if key exists, add the bucket panel
						ui.showBucket(true);

						if (ui.getBucketSplitPane() != null)
						{
							ui.getBucketSplitPane().setDividerLocation(Integer.parseInt(ele.getAttribute("value")));
						}
						break;
					case "tab":
						ui.getGraphTabs().setSelectedIndex(Integer.parseInt(ele.getAttribute("value")));
						break;
					case "window":
						double w = Double.parseDouble(ele.getAttribute("width"));
						double h = Double.parseDouble(ele.getAttribute("height"));
						Dimension size = new Dimension((int) w, (int) h);

//						ui.setPreferredSize(size);
//						ui.setSize(size);
						ui.getContentPane().setPreferredSize(size);
						ui.pack();
						break;
				}

			}

			loadAttacks();
			loadGraph(ui.getTopGraphPanel());
			loadGraph(ui.getSpecGraphPanel());
			loadGraph(ui.getAvGraphPanel());

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void saveAttacks()
	{
		try
		{

			System.out.println("Saving Attack data...");


			File file = new File("./saves/", "av.xml");
			if (!file.exists())
			{
				new File("./saves/").mkdirs();
				file.createNewFile();
			}

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			Document doc = db.newDocument();
			Element root = doc.createElement("save");

			AttackVectors.getAllAttackVectors().forEach(av ->
			{
				Element ele = doc.createElement("attack");
				ele.setAttribute("id", av.qualifiedName);
				ele.setAttribute("bucket", av.inBucket + "");
				ele.setAttribute("deleted", av.deleted + "");
				ele.setAttribute("size", av.size + "");
				ele.setAttribute("shown", av.shown + "");
				ele.setAttribute("hidden", av.hidden + "");

				root.appendChild(ele);
			});


			doc.appendChild(root);


			Transformer tr = TransformerFactory.newInstance().newTransformer();
			tr.setOutputProperty(OutputKeys.INDENT, "yes");
			tr.setOutputProperty(OutputKeys.METHOD, "xml");
			tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

			tr.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(file)));

			System.out.println("Saved to " + file.getAbsolutePath());

		}
		catch (Exception e)
		{
			System.out.println("Failed!");
			e.printStackTrace();
		}
	}

	public static void loadAttacks()
	{
		System.out.println("Loading attack data...");
		try
		{
			File file = new File("./saves", "av.xml");
			if (!file.exists())
			{
				System.out.println("No save data");
				return;
			}

			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
			NodeList list = doc.getElementsByTagName("attack");
			for (int i = 0; i < list.getLength(); i++)
			{
				Element ele = (Element) list.item(i);
				String id = ele.getAttribute("id");

				AttackVector av = AttackVectors.getAttackVector(id);
				if (av != null)
				{
					boolean bucket = Boolean.parseBoolean(ele.getAttribute("bucket"));
					av.deleted = Boolean.parseBoolean(ele.getAttribute("deleted"));
					av.size = Integer.parseInt(ele.getAttribute("size"));
					av.shown = Boolean.parseBoolean(ele.getAttribute("shown"));
					av.hidden = Boolean.parseBoolean(ele.getAttribute("hidden"));

					if (bucket)
					{
						BucketPanel.instance.addRow(av);
					}
					else
					{
						BucketPanel.instance.removeRow(av);
					}
				}

			}

			AttackVectors.update();


		}
		catch (Exception e)
		{
			System.out.println("Failed!");
			e.printStackTrace();
		}

//		System.out.println("Done!");
	}


}
