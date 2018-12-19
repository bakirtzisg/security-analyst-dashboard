package edu.vcu.cyber.dashboard.graph.layout;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.*;

public class LayeredSectionsLayout
{

	private static final String ATTR_SECTION = "type";
	private static final String ATTR_PLACED = "temp.placed";

	private static final double LayoutWidth = 50D;
	private static final double LayerSpacing = 3D;

	private List<LayoutSection> sectionList;
	private Map<String, LayoutSection> sections;

	private Graph graph;
	double posY = 0;

	public LayeredSectionsLayout(Graph graph)
	{
		sections = new HashMap<>();
		sectionList = new ArrayList<>();
		this.graph = graph;
	}

	/**
	 * adds a section to be used
	 */
	public void registerSections(String... titles)
	{
		for (String title : titles)
		{
			LayoutSection section = new LayoutSection(title);
			sections.put(title, section);
			sectionList.add(section);
		}
	}


	/**
	 * places each node in the graph
	 */
	public void computePositions()
	{
		sortSections();


		sectionList.forEach((section) ->
		{
			posY -= LayerSpacing* 2;
			section.layers.forEach(layer ->
			{
				posY -= LayerSpacing ;

				double spaceX = LayoutWidth / (layer.size() + 1);
				double posX = spaceX;
				for (Node node : layer)
				{
					node.setAttribute("x", posX);
					node.setAttribute("y", posY);
					posX += spaceX;
				}
			});
		});

		cleanup();

		calculateBounds();
	}

	/**
	 * makes each section calculate the max and mins for the layer
	 */
	public void calculateBounds()
	{
		sections.forEach((key, section) -> section.computeBounds());
	}


	/**
	 * finds the next layer of nodes to be placed
	 */
	private List<Node> getNextLayer(LayoutSection section)
	{

		List<Node> layer = new ArrayList<>();

		layerCheck:
		for (Node node : section.nodes)
		{
			if (!node.hasAttribute(ATTR_PLACED))
			{
				for (Edge edge : node.getEnteringEdgeSet())
				{
					Node other = edge.getOpposite(node);
					if (!other.hasAttribute(ATTR_PLACED))
					{
						continue layerCheck;
					}
				}
				layer.add(node);
			}
		}
		layer.forEach(node -> node.addAttribute(ATTR_PLACED));

		return layer;
	}

	/**
	 * sets the x-position of the node to the average of the connected edges
	 */
	private void averageLayer(List<Node> layer, boolean dir)
	{
		for (Node node : layer)
		{
			double sumX = 0;
			int count = 0;

			Collection<Edge> edges = !dir ? node.getEnteringEdgeSet() : node.getLeavingEdgeSet();


			for (Edge edge : edges)
			{
				Node other = edge.getOpposite(node);
				double px = other.getAttribute("x");
				sumX += px;
				count++;
			}
			if (count > 0)
			{
				node.setAttribute("x", sumX / (count));
			}
		}

	}

	/**
	 * averages the x-positions of all the nodes in a section based on its edges
	 */
	private void averageSection(LayoutSection sec, boolean dir)
	{
		if (dir)
		{
			for (int j = 0; j < sec.layers.size(); j++)
			{
				averageLayer(sec.layers.get(j), dir);
			}
		}
		else
		{
			for (int j = sec.layers.size() - 1; j >= 0; j--)
			{
				averageLayer(sec.layers.get(j), dir);
			}
		}
	}

	/**
	 * Spaces each node within a layout out
	 */
	private void spaceLayers()
	{
		sectionList.forEach(section ->
		{
			section.layers.forEach(layer ->
			{
				layer.sort((n1, n2) ->
				{
					double x1 = n1.getAttribute("x");
					double x2 = n2.getAttribute("x");
					return (int) (x1 - x2);

				});


				double spaceX = LayoutWidth / (layer.size() + 1);
				double posX = spaceX;
				for (Node node : layer)
				{
					node.setAttribute("x", posX);

					posX += spaceX;
				}
			});
		});
	}

	/**
	 * Cleans up the locations of each node
	 */
	private void cleanup()
	{

		for (int i = 0; i < 2; i++)
		{
			for (int k = 0; k < sectionList.size(); k++)
			{
				averageSection(sectionList.get(k), false);
			}
			spaceLayers();
			for (int k = 0; k < sectionList.size(); k++)
			{
				averageSection(sectionList.get(k), true);
			}
			spaceLayers();
			for (int k = sectionList.size() - 1; k >= 0; k--)
			{
				averageSection(sectionList.get(k), false);
			}
			spaceLayers();
			for (int k = sectionList.size() - 1; k >= 0; k--)
			{
				averageSection(sectionList.get(k), true);
			}
			spaceLayers();

		}


		graph.getNodeSet().forEach(node -> node.removeAttribute(ATTR_PLACED));
	}

	private void sortSections()
	{
		for (Node node : graph.getNodeSet())
		{
			String type = node.getAttribute(ATTR_SECTION);
			LayoutSection section = sections.get(type);

			if (section != null)
			{
				section.nodes.add(node);
			}
			else
			{
				System.err.println("Section doesn't exist! " + type);
			}
		}

		sectionList.forEach((section) ->
		{
			section.layers.clear();

			List<Node> layer;
			while (!(layer = getNextLayer(section)).isEmpty())
			{
				section.layers.add(layer);
			}
		});
	}

	public List<LayoutSection> getSectionList()
	{
		return sectionList;
	}

	public static class LayoutSection
	{
		public final String title;
		public double boundTop = 0;
		public double boundBottom = 0;

		private final List<Node> nodes;
		private final List<List<Node>> layers;

		LayoutSection(String title)
		{
			this.title = title;
			this.nodes = new ArrayList<>();
			this.layers = new ArrayList<>();
		}

		private void computeBounds()
		{
			boundTop = Double.NEGATIVE_INFINITY;
			List<Node> layer = layers.get(0);
			layer.forEach(node ->
			{
				double py = node.getAttribute("y");
				boundTop = Math.max(boundTop, py);
			});

			boundBottom = boundTop;
			layer = layers.get(layers.size() - 1);
			layer.forEach(node ->
			{
				double py = node.getAttribute("y");
				boundBottom = Math.min(boundBottom, py);
			});
		}
	}


}
