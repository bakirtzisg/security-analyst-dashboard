package edu.vcu.cyber.dashboard;

import edu.vcu.cyber.dashboard.cybok.CybokQueryHandler;
import edu.vcu.cyber.dashboard.data.GraphData;
import edu.vcu.cyber.dashboard.graph.listeners.AVActionListener;
import edu.vcu.cyber.dashboard.graph.listeners.IBDActionListener;
import edu.vcu.cyber.dashboard.graph.layout.LayeredSectionsLayout;
import edu.vcu.cyber.dashboard.graph.renderer.SectionRenderer;
import edu.vcu.cyber.dashboard.project.AppSession;
import edu.vcu.cyber.dashboard.ui.DashboardUI;
import edu.vcu.cyber.dashboard.ui.graphpanel.GraphPanel;

import javax.swing.*;

public class Application
{
	
	private static Application instance;
	
	public static Application getInstance()
	{
		return instance;
	}
	
	private DashboardUI gui;
	private AppSession session;
	
	public Application()
	{
		instance = this;
	}
	
	
	/**
	 * Runs the application
	 * - Loads the UI
	 * - Loads the graphs to be displayed on the UI
	 */
	public void run()
	{
		gui = new DashboardUI();
		gui.display();
		
		
		openProject(new AppSession());
	}
	
	/**
	 * Loads all the graphs to be displays on the UI
	 */
	public void openProject(AppSession session)
	{
		this.session = session;
		session.load();
		
		setupTopologyGraph();
		setupSpecificationsGraph();
		setupAttackVectorGraph();
		
	}
	
	/**
	 * Loads the Topology Graph data and generates the graph
	 * then adds the graph to the GraphPanel to be shown in the UI
	 */
	private void setupTopologyGraph()
	{
		GraphData graph = session.getTopGraph();
		GraphPanel gp = gui.getTopGraphPanel();
		gp.setGraph(graph);
		gp.getViewer().enableAutoLayout();
		gp.setGraphActionListener(new IBDActionListener(session));
		if (!CybokQueryHandler.isCybokInstalled())
		{
			AppSession.getInstance().toggleAttackSurfaces();
		}
	}
	
	/**
	 * Loads the Specifications data and generates the graph
	 * then adds the graph to the GraphPanel to be shown in the UI
	 */
	private void setupSpecificationsGraph()
	{
		if (Config.USE_SPEC_GRAPH)
		{
			GraphData graph = session.getSpecGraph();
			GraphPanel gp = gui.getSpecGraphPanel();
			gp.setGraph(graph);
			gp.setGraphActionListener(new IBDActionListener(session));
			
			LayeredSectionsLayout sectionsLayout = new LayeredSectionsLayout(graph.getGraph());
			sectionsLayout.registerSections("Mission", "Function", "Structure");
			sectionsLayout.computePositions();
			
			gp.getViewer().disableAutoLayout();
			
			gp.setBackgroundRenderer(new SectionRenderer(sectionsLayout, gp));
		}
	}
	
	/**
	 * Loads the Attack Vector Graph data and generates the graph
	 * then adds the graph to the GraphPanel to be shown in the UI
	 */
	private void setupAttackVectorGraph()
	{
		GraphData graph = session.getAvGraph();
		GraphPanel gp = gui.getAvGraphPanel();
		gp.setGraph(graph);
		gp.setGraphActionListener(new AVActionListener(session));
		gp.getViewer().enableAutoLayout();
		
		//TODO: parse the description vector stuff and set attributes for the visual stuffs
		
	}
	
	/**
	 * @return the instance of the UI
	 */
	public DashboardUI getGui()
	{
		return gui;
	}
	
	/**
	 * @return an instance of the AppSession
	 */
	public AppSession getSession()
	{
		return session;
	}
	
	/**
	 * sets the text of the status label at the bottom of the UI
	 */
	public void setStatusLabel(String text)
	{
		SwingUtilities.invokeLater(() ->
		{
			gui.setStatusLabel(text);
		});
	}
	
}
