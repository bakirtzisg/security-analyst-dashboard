package edu.vcu.cyber.dashboard.ui.graphpanel;

import edu.vcu.cyber.dashboard.data.GraphData;
import edu.vcu.cyber.dashboard.data.GraphType;
import edu.vcu.cyber.dashboard.graph.GraphActionManager;
import edu.vcu.cyber.dashboard.graph.listeners.GraphActionListener;
import edu.vcu.cyber.dashboard.graph.renderer.HoverMenuRenderer;
import org.graphstream.graph.Graph;
import org.graphstream.ui.swingViewer.DefaultView;
import org.graphstream.ui.swingViewer.LayerRenderer;
import org.graphstream.ui.view.Viewer;

import javax.swing.*;
import java.awt.*;

public class GraphPanel extends JPanel
{

	protected GraphData graph;
	protected DefaultView viewPanel;
	protected Viewer viewer;

	protected GraphActionManager mouseManager;

	protected GraphType graphType;

	public GraphPanel(GraphType graphType)
	{
		this.graphType = graphType;
		setLayout(new BorderLayout());
	}

	public void setGraph(GraphData graph)
	{
		this.graph = graph;

		if (viewPanel != null)
		{
			remove(viewPanel);
		}

		setupViewer();
	}

	public GraphData getGraphData()
	{
		return graph;
	}

	public Graph getViewGraph()
	{
		return viewer.getGraphicGraph();
	}

	public Graph getGraph()
	{
		return graph.getGraph();
	}

	public String getGraphName()
	{
		return graph.getGraph().getId();
	}

	private void setupViewer()
	{
		if (graph != null)
		{
			viewer = new Viewer(graph.getGraph(), Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
			viewPanel = (DefaultView) viewer.addDefaultView(false);

			viewer.getGraphicGraph();

			JPanel container = new JPanel(new BorderLayout());
			container.add(viewPanel);

			add(container, BorderLayout.CENTER);
			container.setInheritsPopupMenu(true);


			mouseManager = new GraphActionManager();

			viewPanel.setMouseManager(mouseManager);
			viewPanel.addMouseWheelListener(mouseManager);
			viewPanel.addFocusListener(mouseManager);
			viewPanel.addKeyListener(mouseManager);

			viewPanel.setForeLayoutRenderer(new HoverMenuRenderer(graph));

			viewPanel.updateUI();

			viewPanel.setInheritsPopupMenu(true);


		}
	}

	public void setGraphActionListener(GraphActionListener actionListener)
	{
		actionListener.init(graph, viewPanel, this);
		mouseManager.setListener(actionListener);
	}

	public DefaultView getViewPanel()
	{
		return viewPanel;
	}

	public Viewer getViewer()
	{
		return viewer;
	}

	public void setBackgroundRenderer(LayerRenderer renderer)
	{
		viewPanel.setBackLayerRenderer(renderer);
	}

}
