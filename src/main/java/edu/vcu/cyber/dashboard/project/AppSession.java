package edu.vcu.cyber.dashboard.project;

import edu.vcu.cyber.dashboard.Application;
import edu.vcu.cyber.dashboard.cybok.CybokQueryHandler;
import edu.vcu.cyber.dashboard.cybok.queries.FullAnalysisQuery;
import edu.vcu.cyber.dashboard.data.AttackVector;
import edu.vcu.cyber.dashboard.data.AttackVectors;
import edu.vcu.cyber.dashboard.data.GraphData;
import edu.vcu.cyber.dashboard.data.GraphType;
import edu.vcu.cyber.dashboard.ui.graphpanel.GraphPanel;
import edu.vcu.cyber.dashboard.util.CSVParser;
import edu.vcu.cyber.dashboard.util.GraphMLParser;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import scala.collection.immutable.HashMap;
import scala.collection.immutable.Map;

import javax.swing.*;
import java.io.File;
import java.util.Collection;

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
			
			// analyze topology graph
			CybokQueryHandler.sendQuery(new FullAnalysisQuery(topGraphFile.getAbsolutePath()));
		}
		
		if (specGraphFile.exists())
		{
			System.out.println("Loading Specifications Graph");
			GraphData graph = createIfNotExist(GraphType.SPECIFICATIONS);
			graph.refreshGraph();
			GraphMLParser.parse(specGraphFile, graph);
		}
		
		GraphData graphData = createIfNotExist(GraphType.ATTACKS);
		graphData.generateGraph();
		
		
		/*
		CSVParser.readCSV(new File("./data/", "attacks.csv"));
		if (!CybokQueryHandler.isCybokInstalled())
		{
			registerGraph(GraphMLParser.parse(new File("./data/", "attack_surface.graphml"), GraphType.ATTACK_SURFACE));
//			registerGraph(GraphMLParser.parse(new File("./data/", "fcs_attack_vector_graph.graphml"), GraphType.ATTACKS));
		}
		else
		{
			AttackVectors.getAllAttackVectors().forEach(av -> av.hidden = true);
		}
		
		
		GraphData graphData = new GraphData(GraphType.ATTACKS);
		graphData.generateGraph();
		registerGraph(graphData);
		
		if (!CybokQueryHandler.isCybokInstalled())
		{
			Graph graph = graphData.getGraph();
			AttackVectors.computeSizes();
			Collection<AttackVector> attackVectors = AttackVectors.getAllAttackVectors();
			attackVectors.forEach(av -> av.addToGraph(graph));
		}
		else
		{*/
//		CybokQueryHandler.sendQuery(new FullAnalysisQuery(topologyGraphFile.getAbsolutePath()));
		//}
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
