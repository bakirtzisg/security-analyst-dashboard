package edu.vcu.cyber.dashboard.data;

import edu.vcu.cyber.dashboard.project.AppSession;
import edu.vcu.cyber.dashboard.util.Attributes;
import edu.vcu.cyber.dashboard.util.NodeUtil;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class AttackVector
{
	public AttackType type;
	
	public String database, id;
	public String qualifiedName;
	
	public String contents;
	public String description;
	
	public String[] related_cwe;
	public String[] related_capec;
	public String[] related_cve;
	
	public List<String> violatedComponents;
	
	public boolean shown;
	public boolean hidden;
	public boolean deleted;
	public boolean inBucket;
	public boolean needCheck = true;
	
	private boolean hasSpecRelation;
	
	public double taxaScore;
	
	public int size;
	
	public double px;
	public double py;
	
	public AttackVector(String database, String id)
	{
		this.database = database;
		this.id = id;
		this.qualifiedName = database + "-" + id;
		type = AttackType.valueOf(database);
		
		violatedComponents = new ArrayList<>();
		
		if (database.startsWith("CVE"))
		{
			hidden = true;
		}
		
		shown = true;
	}
	
	public AttackVector(String database, String id, String text)
	{
		this(database, id);
		description = text;
	}
	
	public String getURI()
	{
		switch (type)
		{
			case CVE:
				return "https://nvd.nist.gov/vuln/detail/" + qualifiedName;
			case CWE:
				return "https://cwe.mitre.org/data/definitions/" + id + ".html";
		}
		return "https://" + type.name().toLowerCase() + ".mitre.org/data/definitions/" + id + ".html";
	}
	
	public Node addToGraph(Graph graph)
	{
		return addToGraph(graph, false);
	}
	
	
	public Node addToGraph(Graph graph, boolean force)
	{
		Node node = graph.getNode(qualifiedName);
		if (((!deleted || AttackVectors.showDeletedNodes) && (!hidden || AttackVectors.showHiddenNodes)) || force)
		{
			if (node == null)
			{
				node = graph.addNode(qualifiedName);
			}
			
			node.setAttribute(Attributes.HOVER_TEXT, description);
			node.setAttribute(Attributes.ATTACK_VECTOR);
			
			if (px != 0 && py != 0)
			{
				node.setAttribute("xyz", px, py, 0);
			}


//			int size = related_capec.length + related_cwe.length + related_cve.length;
			
			node.setAttribute("ui.size", Math.sqrt(size) + 6.0D);
			if (size > 2)
				node.setAttribute("layout.weight", 2.0D / (Math.sqrt(size) + 1) + 0.15);
			else
				node.setAttribute("layout.weight", 0.1);
			
			
			attemptAddEdge(graph, related_capec, node);
			attemptAddEdge(graph, related_cwe, node);
			attemptAddEdge(graph, related_cve, node);
			
			
			for (String compNode : violatedComponents)
			{
				if (AppSession.getInstance().getSpecGraph().hasNode(compNode))
				{
					hasSpecRelation = true;
				}
				Node n = graph.getNode(compNode);
				if (n != null)
				{
					String edgeId = n.getId() + "-" + node.getId();
					if (graph.getEdge(edgeId) == null)
					{
						Edge edge = graph.addEdge(n.getId() + "-" + node.getId(), n.getId(), node.getId());
						NodeUtil.addCssClass(edge, Attributes.CSS_ATTACK_VECTOR);
					}
				}
			}
			
			if (hasSpecRelation)
			{
				node.setAttribute("ui.class", Attributes.ATTACK_VECTOR, type.css, Attributes.CSS_SPEC_RELATION);
			}
			else
			{
				node.setAttribute("ui.class", Attributes.ATTACK_VECTOR, type.css);
			}
			return node;
		}
		else if (node != null)
		{
			graph.removeNode(node.getId());
		}
		return null;
	}
	
	private void attemptAddEdge(Graph graph, String[] rel, Node node)
	{
		if (rel != null)
		{
			Node relatedNode;
			for (String related : rel)
			{
				relatedNode = graph.getNode(related);
				if (relatedNode != null && !node.hasEdgeBetween(relatedNode))
				{
					String edgeId = qualifiedName + "-" + related;
					Edge edge = graph.addEdge(edgeId, qualifiedName, related);
					
					NodeUtil.addCssClass(edge, Attributes.CSS_ATTACK_VECTOR);
				}
			}
		}
	}
	
	public void setPos(Node node)
	{
//		System.out.println(node.getAttributeKeySet());
		if (node.hasAttribute("xyz"))
		{
			Object[] pos = node.getAttribute("xyz");
			px = Double.valueOf(pos[0].toString());
			py = Double.valueOf(pos[1].toString());
		}
	}
}
