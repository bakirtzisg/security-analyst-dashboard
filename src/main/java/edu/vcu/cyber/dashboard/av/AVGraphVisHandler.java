package edu.vcu.cyber.dashboard.av;

import edu.vcu.cyber.dashboard.data.GraphData;
import edu.vcu.cyber.dashboard.util.Attributes;
import edu.vcu.cyber.dashboard.util.Config;
import edu.vcu.cyber.dashboard.util.NodeUtil;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.List;


public class AVGraphVisHandler extends AttackVectorVisualizer
{
	
	private GraphData graph;
	
	public AVGraphVisHandler(GraphData graph)
	{
		this.graph = graph;
	}
	
	@Override
	public void dispose()
	{
		clearAll();
	}
	
	@Override
	public void populate()
	{
//		update();
	}
	
	@Override
	public void showAttack(AttackVector av)
	{
		if (!av.shown && av.canShow())
		{
			Graph graph = this.graph.getGraph();
			Node node = graph.getNode(av.qualifiedName);
			
			if (node == null)
			{
				node = graph.addNode(av.qualifiedName);
			}
			av.shown = true;
			
			node.setAttribute(Attributes.HOVER_TEXT, av.description);
			node.setAttribute(Attributes.ATTACK_VECTOR);
			
			if (av.lastPosition != null)
			{
				node.setAttribute(Attributes.XYZ_POS, av.lastPosition.x, av.lastPosition.y, 0);
			}
			
			double size = av.size * Config.AV_NODE_SCALE;
			
			double nodeSize = Math.sqrt(size) + Config.AV_NODE_MIN_SIZE;
			double layoutWeight = 2.0D / ((Math.sqrt(size) + 1) + 0.015);
			
			if (av.size > Config.AV_LAYOUT_MIN_SIZE)
				node.setAttribute(Attributes.LAYOUT_WEIGHT, layoutWeight);
			else
				node.setAttribute(Attributes.LAYOUT_WEIGHT, Config.AV_LAYOUT_WEIGHT_DEF);
			
			node.setAttribute(Attributes.UI_SIZE, nodeSize);
			
			av.relations.forEach(this::addEdge);
			
			node.setAttribute(Attributes.STYLE_CLASS, av.type.css);
			
		}
		
	}
	
	@Override
	public void hideAttack(AttackVector av)
	{
		graph.flagRemoval(av.qualifiedName);
		av.relations.forEach(rel -> rel.shown = false);
		av.shown = false;
	}
	
	@Override
	public void removeAttack(AttackVector av)
	{
		av.shown = false;
		av.relations.forEach(rel -> rel.shown = false);
		graph.getGraph().removeNode(av.qualifiedName);
	}
	
	@Override
	public void addEdge(Relation rel)
	{
		if (rel.shouldBeVisible() && !rel.shown)
		{
			Graph graph = this.graph.getGraph();
			Edge edge = graph.getEdge(rel.edgeId);
			if (edge == null)
			{
				edge = graph.addEdge(rel.edgeId, rel.parent.qualifiedName, rel.child.qualifiedName);
				NodeUtil.addCssClass(edge, Attributes.CSS_ATTACK_VECTOR);
			}
			rel.shown = true;
		}
	}
	
	@Override
	public void removeEdge(Relation rel)
	{
		if (rel.shown)
		{
			Graph graph = this.graph.getGraph();
			Edge edge = graph.getEdge(rel.edgeId);
			if (edge != null)
			{
				graph.removeEdge(rel.edgeId);
			}
			rel.shown = false;
		}
	}
	
	@Override
	public void clearAll()
	{
		graph.getGraph().getEdgeSet().clear();
		graph.getGraph().getNodeSet().clear();
		AttackVectors.forEach(av ->
		{
			av.shown = false;
			av.relations.forEach(rel -> rel.shown = false);
		});
	}
	
	@Override
	public boolean isShown(AttackVector av)
	{
		return graph.getGraph().getNode(av.qualifiedName) != null;
	}
	
	@Override
	public void purgeFlagged()
	{
		graph.removeFlagged();
	}
	
	@Override
	public List<AttackVector> getShown()
	{
		return null;
	}
}
