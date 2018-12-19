package edu.vcu.cyber.dashboard.project;

import edu.vcu.cyber.dashboard.cybok.CybokQueryHandler;
import edu.vcu.cyber.dashboard.cybok.queries.FullAnalysisQuery;
import edu.vcu.cyber.dashboard.data.GraphData;
import edu.vcu.cyber.dashboard.util.GraphExporter;

import java.io.File;

public class GraphAnalysis
{
	
	
	public static void analyseTopologyGraph()
	{
		
		GraphData graphData = AppSession.getInstance().getTopGraph();
		
		File file = new File("./.graph/");
		file.mkdirs();
		file = new File(file, "topology.graphml");
		
		GraphExporter.exportGraph(graphData, file);
		
		CybokQueryHandler.sendQuery(new FullAnalysisQuery(file.getAbsolutePath()));
	}
	
	
}
