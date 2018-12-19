package edu.vcu.cyber.dashboard.util;

import edu.vcu.cyber.dashboard.Config;
import edu.vcu.cyber.dashboard.data.GraphData;
import edu.vcu.cyber.dashboard.project.AppSession;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Utils
{
	
	public static boolean openWebPage(String uri)
	{
		
		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE))
		{
			try
			{
				Desktop.getDesktop().browse(new URI(uri));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			catch (URISyntaxException e)
			{
				e.printStackTrace();
			}
			return true;
		}
		
		return false;
	}
	
	public static void updateAttackSurfaces()
	{
		final GraphData gd = AppSession.getInstance().getTopGraph();
		final Graph graph = gd.getGraph();
		
		if (Config.showAttackSurfaces)
		{
			gd.getNodes().forEach(node ->
			{
				if (node.hasAttribute(Attributes.ATTACK_SURFACE))
				{
					if (graph.getNode(node.getId()) == null)
					{
						Node n = graph.addNode(node.getId());
						NodeUtil.addCssClass(n, Attributes.ATTACK_SURFACE);
						n.addAttribute(Attributes.ATTACK_SURFACE, "true");
						
						
						node.getTargets().forEach(t ->
						{
							Edge edge = NodeUtil.addEdge(graph, n.getId(), t.getId(), true);
							NodeUtil.addCssClass(edge, Attributes.ATTACK_SURFACE);
							edge.addAttribute(Attributes.ATTACK_SURFACE, "true");
						});
						
					}
				}
			});
		}
		else
		{
			graph.getEdgeSet().removeIf(edge -> edge.hasAttribute(Attributes.ATTACK_SURFACE));
			graph.getNodeSet().removeIf(node -> node.hasAttribute(Attributes.ATTACK_SURFACE));
		}
		
		
	}
	
}
