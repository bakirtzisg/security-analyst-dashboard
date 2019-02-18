package edu.vcu.cyber.dashboard.cybok.queries;

import edu.vcu.cyber.dashboard.Application;
import edu.vcu.cyber.dashboard.Config;
import edu.vcu.cyber.dashboard.av.AttackVectors;
import edu.vcu.cyber.dashboard.av.VisHandler;
import edu.vcu.cyber.dashboard.cybok.CybokQuery;
import edu.vcu.cyber.dashboard.data.*;
import edu.vcu.cyber.dashboard.project.AppSession;
import edu.vcu.cyber.dashboard.util.*;

import java.util.*;

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
		Config.HIDDEN_DATA.mkdirs();
		return new String[]{"-i", input, "-o", Config.HIDDEN_DATA.getAbsoluteFile().toString() + "/tmp"};
	}
	
	
	private List<String> attackSurfaces = new ArrayList<>();
//	private Map<String, List<AttackVector>> components = new HashMap<>();
//	private List<AttackVector> activeList;
//	private String violated;
//
//	private boolean isAttackSurfaceAnalysis = false;

//	private static final String ATTACK_REGEX_PATTERN = "(CVE|CAPEC|CWE).([0-9]*[\\-]?[0-9]*)(.*)";
//	private static final Pattern pattern = Pattern.compile(ATTACK_REGEX_PATTERN);
	
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
	}
	
	@Override
	public void onResult(FullAnalysisQuery query)
	{
		
		// load attack vector information
		AttackVectors.clearAll();
		CSVParser.readCSV(Config.FILE_AV_GRAPH);
//		AttackVectors.getAllAttackVectors().forEach(av -> av.addToGraph(AppSession.getInstance().getAvGraph().getGraph()));
		
		// reset the visualizer to and display all nodes
		VisHandler.dataUpdate();
		
		// load attack surface information
		GraphData as = GraphMLParser.parse(Config.FILE_ATTACK_SURFACE, GraphType.ATTACK_SURFACE);
		GraphData top = AppSession.getInstance().getTopGraph();
		
		as.getNodes().forEach(node ->
		{
			if (top.getNode(node.getId()) == null)
			{
				NodeData nd = top.addNode(node.getId());
				node.getTargets().forEach(t -> top.addEdge(node.getId(), t.getId()));
				nd.setAttribute(Attributes.ATTACK_SURFACE, "true");
			}
		});
		
		Utils.updateAttackSurfaces();
		
	}
}
