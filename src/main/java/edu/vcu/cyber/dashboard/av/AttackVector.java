package edu.vcu.cyber.dashboard.av;

import edu.vcu.cyber.dashboard.data.AttackType;
import edu.vcu.cyber.dashboard.util.Config;
import edu.vcu.cyber.dashboard.util.Point2D;
import org.graphstream.graph.Node;

import java.util.ArrayList;
import java.util.List;

public class AttackVector
{
	
	private static int _id = 0;
	protected final int uid = _id++;
	
	/**
	 * what type is this? (CAPEC, CVE, CWE)
	 */
	public AttackType type;
	
	/**
	 * What database is it from?
	 * What's the ID?
	 */
	public String database, id;
	public String qualifiedName;
	
	/**
	 * The full description of the AttackVector (from the website)
	 * TODO: find a different solution to this, it uses a lot of memory
	 */
	public String contents;
	
	/**
	 * The brief description of the attack
	 */
	public String description;
	
	/**
	 * All of the related Attack Vectors
	 */
	public String[] related_cwe;
	public String[] related_capec;
	public String[] related_cve;
	
	/**
	 * A list of edges
	 */
	public List<Relation> relations;
	
	/**
	 * What components in the topology does this attack violate
	 */
	public List<String> violatedComponents;
	
	/**
	 * Is the attack current visible on the visualizer
	 */
	public boolean shown;
	
	/**
	 * Is the attack hidden?
	 * i.e. CVEs are hidden by default and will never be visible
	 * unless otherwise specified
	 */
	public boolean hidden;
	
	/**
	 * What the attack deleted?
	 * can be used to determine if the attack isn't important
	 */
	public boolean deleted;
	
	/**
	 * true to show the attack regardless of deleted or hidden status
	 */
	public boolean forceShow;
	
	/**
	 * Is the attack listed in the bucket?
	 */
	public boolean inBucket;
	
	/**
	 * The score for the attack determined by the taxascore analysis
	 */
	public double taxaScore;
	
	/**
	 * The size of the node
	 * (perceived level of importance)
	 */
	public int size;
	
	/**
	 * Used in the graph visualizer to keep track of the last known position of the node
	 */
	public Point2D lastPosition;
	
	/**
	 * Creates a new AttackVector object using the database and name
	 */
	public AttackVector(String database, String id)
	{
		this.database = database;
		this.id = id;
		this.qualifiedName = database + "-" + id;
		type = AttackType.valueOf(database);
		
		violatedComponents = new ArrayList<>();
		
		this.relations = new ArrayList<>();
		
		if (type == AttackType.CVE && !Config.showCVENodes)
		{
			hidden = true;
		}
		
		shown = false;
	}
	
	/**
	 * Creates a new AttackVector object using the database, name, and a description
	 */
	public AttackVector(String database, String id, String text)
	{
		this(database, id);
		description = text;
	}
	
	/**
	 * @return the URL to the website where the Attack is listed
	 */
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
	
	/**
	 * Updates the known location of the node on the graph
	 */
	public void setPos(Node node)
	{
		if (node.hasAttribute("xyz"))
		{
			Object[] pos = node.getAttribute("xyz");
			double x = Double.valueOf(pos[0].toString());
			double y = Double.valueOf(pos[1].toString());
			
			lastPosition = new Point2D(x, y);
		}
	}
	
	
	/**
	 * @return true if forced show or all the following applies
	 * - isn't deleted OR deleted should be shown
	 * - isn't hidden OR hidden should be shown
	 * - isn't a CVE OR cve can be shown
	 */
	public boolean canShow()
	{
		return forceShow || ((!deleted || Config.showDeletedNodes) && (!hidden || Config.showHiddenNodes) && type.canShow());
	}
	
	/**
	 * @return true if the attack should be hidden
	 */
	public boolean shouldHide()
	{
		return false;
	}
	
	/**
	 * adds a relation to the attack
	 */
	public void addRelation(Relation relation)
	{
		if (!relations.contains(relation))
		{
			AttackVectors.numEdges++;
			relations.add(relation);
		}
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof AttackVector && ((AttackVector) obj).uid == uid;
	}
	
	public String createTooltip()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		
		sb.append(description).append("<br>");
		
		if (relations.size() > 10)
		{
			sb.append("Too many relationships to display (").append(relations.size()).append(")<br>");
		}
		else
		{
			sb.append("Relationships:<br>");
			for (Relation rel : relations)
			{
				sb.append(rel.getChild().qualifiedName).append("<br>");
			}
		}
		
		sb.append("</html>");
		
		return sb.toString();
	}
}
