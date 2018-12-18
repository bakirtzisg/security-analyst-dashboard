package edu.vcu.cyber.dashboard.cybok.queries;

import edu.vcu.cyber.dashboard.Application;
import edu.vcu.cyber.dashboard.Config;
import edu.vcu.cyber.dashboard.cybok.CybokQuery;
import edu.vcu.cyber.dashboard.data.*;
import edu.vcu.cyber.dashboard.project.AppSession;
import edu.vcu.cyber.dashboard.util.Attributes;
import edu.vcu.cyber.dashboard.util.CSVParser;
import edu.vcu.cyber.dashboard.util.NodeUtil;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.w3c.dom.Attr;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FullAnalysisQuery extends CybokQuery<FullAnalysisQuery> implements CybokQuery.Result<FullAnalysisQuery>
{
	private final String input;
	
	public FullAnalysisQuery(String input)
	{
		this(input, null);
	}
	
	public FullAnalysisQuery(String input, CybokQuery.Result<FullAnalysisQuery> resultHandler)
	{
		super(resultHandler);
		setResultHandler(this);
		this.input = input;
	}
	
	@Override
	public void onExecute()
	{
		Application.getInstance().setStatusLabel("Analysing system topology...");
	}
	
	@Override
	public String[] searchQuery()
	{
		Config.TEMP_DATA.mkdirs();
		return new String[]{"-i", input, "-o", Config.TEMP_DATA.getAbsoluteFile().toString() + "/tmp"};
	}
	
	
	private List<String> attackSurfaces = new ArrayList<>();
	private Map<String, List<AttackVector>> components = new HashMap<>();
	private List<AttackVector> activeList;
	private String violated;
	
	private boolean isAttackSurfaceAnalysis = false;
	
	private static final String ATTACK_REGEX_PATTERN = "(CVE|CAPEC|CWE).([0-9]*[\\-]?[0-9]*)(.*)";
	private static final Pattern pattern = Pattern.compile(ATTACK_REGEX_PATTERN);
	
	@Override
	public void onMessage(String line)
	{
//
//		if (line.equals("Attack surface analysis"))
//		{
//			isAttackSurfaceAnalysis = true;
//			return;
//		}
//		else if (line.equals("Full system analysis"))
//		{
//			return;
//		}
//
//		Matcher matcher = pattern.matcher(line);
//		if (matcher.find())
//		{
//			String db = matcher.group(1);
//			String id = matcher.group(2);
//			String text = "";
//			if (matcher.groupCount() == 3)
//			{
//				text = matcher.group(3);
//			}
//
//			AttackVector tmp = AttackVectors.addAttack(new AttackVector(db, id, text), violated);
////			activeList.add(tmp);
//
//			tmp.hidden = !AttackVectors.showCVENodes && tmp.type == AttackType.CVE;
//
//		}
//		else if (isAttackSurfaceAnalysis && line.contains(" -> "))
//		{
//			String[] args = line.split(" -> ");
//			attackSurfaces.add(args[0]);
//			attackSurfaces.add(args[1]);
//		}
//		else
//		{
//			if (!line.contains("--") && !line.contains("==") && line.length() > 0)
//			{
//				violated = line;
////				activeList = components.computeIfAbsent(violated, key -> new ArrayList<>());
////				System.out.println("Violated: " + violated);
//			}
//		}
	}
	
	
	@Override
	public void onFinish(boolean error)
	{
		super.onFinish(error);
		
//		System.out.println(attackSurfaces);
		
		CSVParser.readCSV(Config.FILE_AV_GRAPH);
		AttackVectors.getAllAttackVectors().forEach(av -> av.addToGraph(AppSession.getInstance().getAvGraph().getGraph()));
	}
	
	@Override
	public void onResult(FullAnalysisQuery query)
	{
//		System.out.println("Result");
//		Graph topGraph = AppSession.getInstance().getTopGraph().getGraph();
//
//		NodeUtil.clearAllAttributesOf(topGraph, Attributes.ATTACK_SURFACE);
//
//		for (int i = 0; i < attackSurfaces.size(); i += 2)
//		{
//			String asId = attackSurfaces.get(i);
//			String targetId = attackSurfaces.get(i + 1);
//			Node as = topGraph.getNode(asId);
//			Node target = topGraph.getNode(targetId);
//			if (as == null)
//			{
//				as = topGraph.addNode(asId);
//			}
//			if (!as.hasAttribute(Attributes.ATTACK_SURFACE))
//			{
//				as.addAttribute(Attributes.ATTACK_SURFACE);
//				NodeUtil.addCssClass(as, Attributes.ATTACK_SURFACE);
//			}
//			if (target != null)
//			{
//				NodeUtil.addCssClass(target, "attack_surface_target");
//				String edgeId = asId + "-" + targetId;
//				Edge edge = topGraph.getEdge(edgeId);
//				if (edge == null)
//				{
//					edge = topGraph.addEdge(edgeId, asId, targetId, true);
//				}
//
//				if (!edge.hasAttribute(Attributes.ATTACK_SURFACE))
//				{
//					edge.addAttribute(Attributes.ATTACK_SURFACE);
//					NodeUtil.addCssClass(edge, Attributes.ATTACK_SURFACE);
//				}
//			}
//
//		}
//
//		GraphData attackGraph = AppSession.getInstance().getAvGraph();
//		Graph graph = attackGraph.getGraph();
//		AttackVectors.computeSizes();
//		Collection<AttackVector> attackVectors = AttackVectors.getAllAttackVectors();
//		attackVectors.forEach(av -> av.addToGraph(graph));
		
	}
}
