package edu.vcu.cyber.dashboard.project;

import edu.vcu.cyber.dashboard.Application;
import edu.vcu.cyber.dashboard.Config;
import edu.vcu.cyber.dashboard.av.AVGraphVisHandler;
import edu.vcu.cyber.dashboard.av.VisHandler;
import edu.vcu.cyber.dashboard.cybok.CybokQueryHandler;
import edu.vcu.cyber.dashboard.cybok.queries.FullAnalysisQuery;
import edu.vcu.cyber.dashboard.data.*;
import edu.vcu.cyber.dashboard.graph.layout.LayeredSectionsLayout;
import edu.vcu.cyber.dashboard.ui.DashboardUI;
import edu.vcu.cyber.dashboard.ui.graphpanel.GraphPanel;
import edu.vcu.cyber.dashboard.util.Attributes;
import edu.vcu.cyber.dashboard.util.CSVParser;
import edu.vcu.cyber.dashboard.util.GraphMLParser;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import scala.collection.immutable.HashMap;
import scala.collection.immutable.Map;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Class containing all the data necessary for the current session.
 * Includes saving the current sessions and loading previous sections
 */
public class AppSession extends GraphHandler
{
	
	private static AppSession instance;
	
	public static Node focusedNode;
	
	public static void setFocus(GraphType graphType)
	{
		instance.focusedGraph = graphType;
	}
	
	public static GraphType getFocusedGraph()
	{
		return instance.focusedGraph;
	}
	
	public static GraphData getFocusedGraphData()
	{
		return instance.getGraph(instance.selectedGraph);
	}
	
	public static void setSelectedGraph(GraphType graphType)
	{
		if (instance.selectedGraph != null && instance.selectedGraph != graphType)
		{
			GraphPanel gp = Application.getInstance().getGui().getGraphPanel(instance.selectedGraph);
			((GraphicGraph) gp.getViewGraph()).graphChanged = true;
			
		}
		instance.selectedGraph = graphType;
	}
	
	public static GraphType getSelectedGraph()
	{
		return instance.selectedGraph;
	}
	
	public static GraphData getSelectedGraphData()
	{
		return instance.getGraph(instance.focusedGraph);
	}
	
	private GraphType selectedGraph;
	private GraphType focusedGraph;
	
	private File topologyGraphFile = new File("data/topology.graphml");
	private File specificationsGraphFile = new File("data/specifications.graphml");
	
	public AppSession()
	{
		super();
		instance = this;
	}
	
	public static AppSession getInstance()
	{
		return instance;
	}
	
	public void load()
	{
		load(topologyGraphFile, specificationsGraphFile);
	}
	
	/**
	 * Loads a previous session
	 */
	public void load(File topGraphFile, File specGraphFile)
	{
		clear();
		
		if (topGraphFile.exists())
		{
			System.out.println("Loading Topology Graph");
			GraphData graph = createIfNotExist(GraphType.TOPOLOGY);
			
			GraphMLParser.parse(topGraphFile, graph);
			
			graph.refreshGraph();
			
			GraphAnalysis.analyseTopologyGraph();
		}
		
		if (specGraphFile.exists())
		{
			System.out.println("Loading Specifications Graph");
			GraphData graph = createIfNotExist(GraphType.SPECIFICATIONS);
			GraphMLParser.parse(specGraphFile, graph);
			
			
			// verify that the graph is valid
			final List<NodeData> invalidNodes = new ArrayList<>();
			graph.getNodes().forEach(node ->
			{
				if (node.hasAttribute("attr.type"))
				{
					String type = node.getAttribute("attr.type");
					if (!type.equals("Structure") && !type.equals("Mission") && !type.equals("Function"))
					{
						invalidNodes.add(node);
					}
				}
				else
				{
					invalidNodes.add(node);
				}
			});
			
			if (!invalidNodes.isEmpty())
			{
				System.out.println("Invalid specification model!");
				System.out.println("The following nodes are invalid: ");
				invalidNodes.forEach(node -> System.out.println(node.getId() + " missing or invalid \"type\" attribute"));
				Config.USE_SPEC_GRAPH = false;
			}
			else
			{
				Config.USE_SPEC_GRAPH = true;
			}
			
			
		}
		else
		{
			Config.USE_SPEC_GRAPH = false;
		}
		
		
		GraphData graphData = createIfNotExist(GraphType.ATTACKS);
		graphData.generateGraph();
		VisHandler.register(new AVGraphVisHandler(Application.getInstance().getSession().getAvGraph()));

//		if (Config.USE_SPEC_GRAPH)
//		{
		Application.getInstance().getGui().setUseSpecGraph(Config.USE_SPEC_GRAPH);
//		}
	}
	
	/**
	 * Saves the current session
	 */
	public void save()
	{
		//TODO: needs implementation
	}
	
	public void setFocusedGraph()
	{
	
	}
	
	public GraphData getTopGraph()
	{
		return getGraph(GraphType.TOPOLOGY);
	}
	
	public GraphData getSpecGraph()
	{
		return getGraph(GraphType.SPECIFICATIONS);
	}
	
	public GraphData getAvGraph()
	{
		return getGraph(GraphType.ATTACKS);
	}
}
