package edu.vcu.cyber.dashboard.ui.custom.av;

import edu.vcu.cyber.dashboard.data.AttackVector;
import edu.vcu.cyber.dashboard.data.AttackVectors;
import edu.vcu.cyber.dashboard.data.GraphData;
import edu.vcu.cyber.dashboard.data.NodeData;
import edu.vcu.cyber.dashboard.util.Attributes;
import edu.vcu.cyber.dashboard.util.NodeUtil;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.List;
import java.util.function.Predicate;

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
		
	}
	
	@Override
	public void populate()
	{
		update();
	}
	
	@Override
	public void filterAttacks(Predicate<AttackVector> filter)
	{
		this.filter = filter;
		AttackVectors.getAllAttackVectors().forEach(av ->
		{
			boolean pred = filter.test(av);
			NodeData existing = graph.getNode(av.qualifiedName);
			boolean shown = existing != null && existing.getNode() != null;
			av.shown = pred;
			if (shown && !pred)
			{
				hideAttack(av);
			}
			else if (!shown && pred)
			{
				showAttack(av);
			}
		});
		
		purgeFlagged();
	}
	
	@Override
	public void showAttack(AttackVector av)
	{
		Graph graph = this.graph.getGraph();
		Node node = graph.getNode(av.qualifiedName);
		if (((!av.deleted || AttackVectors.showDeletedNodes) && (!av.hidden || AttackVectors.showHiddenNodes)))
		{
			if (node == null)
			{
				node = graph.addNode(av.qualifiedName);
			}
			
			node.setAttribute(Attributes.HOVER_TEXT, av.description);
			node.setAttribute(Attributes.ATTACK_VECTOR);
			
			if (av.px != 0 && av.py != 0)
			{
				node.setAttribute(Attributes.XYZ_POS, av.px, av.py, 0);
			}
			
			node.setAttribute(Attributes.UI_SIZE, Math.sqrt(av.size) + 6.0D);
			if (av.size > 2)
				node.setAttribute(Attributes.LAYOUT_WEIGHT, 2.0D / ((Math.sqrt(av.size) + 1) + 0.15));
			else
				node.setAttribute(Attributes.LAYOUT_WEIGHT, 0.1);
			
			
			attemptAddEdge(av, graph, av.related_capec, node);
			attemptAddEdge(av, graph, av.related_cwe, node);
			attemptAddEdge(av, graph, av.related_cve, node);
			
			node.setAttribute(Attributes.STYLE_CLASS, Attributes.ATTACK_VECTOR, av.type.css);
			
			
		}
		else if (node != null) // if node exists but fails the show conditions
		{
			graph.removeNode(node.getId());
		}
	}
	
	private void attemptAddEdge(AttackVector av, Graph graph, String[] related, Node node)
	{
		if (related != null)
		{
			Node relatedNode;
			for (String rel : related)
			{
				relatedNode = graph.getNode(rel);
				if (relatedNode != null && !node.hasEdgeBetween(relatedNode))
				{
					String edgeId = av.qualifiedName + "-" + rel;
					Edge edge = graph.addEdge(edgeId, av.qualifiedName, rel);
					NodeUtil.addCssClass(edge, Attributes.CSS_ATTACK_VECTOR);
				}
			}
		}
	}
	
	@Override
	public void hideAttack(AttackVector av)
	{
		graph.flagRemoval(av.qualifiedName);
	}
	
	@Override
	public boolean isShown(AttackVector av)
	{
		return graph.getGraph().getNode(av.qualifiedName) != null;
	}
	
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
