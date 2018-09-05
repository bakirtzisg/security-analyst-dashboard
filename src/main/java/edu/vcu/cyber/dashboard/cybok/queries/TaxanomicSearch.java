package edu.vcu.cyber.dashboard.cybok.queries;

import edu.vcu.cyber.dashboard.Application;
import edu.vcu.cyber.dashboard.cybok.CybokQuery;
import edu.vcu.cyber.dashboard.data.AttackVector;
import edu.vcu.cyber.dashboard.data.AttackVectors;
import edu.vcu.cyber.dashboard.data.GraphData;
import edu.vcu.cyber.dashboard.data.NodeData;
import edu.vcu.cyber.dashboard.project.AppSession;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.ArrayList;
import java.util.List;

public class TaxanomicSearch extends CybokQuery
{

	private final String input, target;

	public TaxanomicSearch(String input, String target)
	{
		this(input, target, null);

		setResultHandler((Result<TaxanomicSearch>) query ->
		{
			AttackVectors.showInGraph(AppSession.getInstance().getAvGraph(), av -> av.taxaScore > 0);
		});
	}

	public TaxanomicSearch(String input, String target, CybokQuery.Result<TaxanomicSearch> resultHandler)
	{
		super(resultHandler);
		this.input = input;
		this.target = target;
	}


	public void onExecute()
	{
		Application.getInstance().setStatusLabel("Collecting taxonometric scores for " + target + "...");
	}

	@Override
	public String[] searchQuery()
	{
		return new String[]{"-i", input, "-T", target};
	}

	@Override
	public void onMessage(String line)
	{
		line = line.trim();
		if (line.length() > 0)
		{
			String[] args = line.split("\t");
			String id = args[0];
			AttackVector av = AttackVectors.getAttackVector(id);
			GraphData graph = AppSession.getInstance().getAvGraph();
			if (av != null)
			{
				double score = AttackVectors.getAttackVector(id).taxaScore = Double.parseDouble(args[1]);
				NodeData node = graph.getNode(id);
				if (node != null)
				{
					node.setAttribute("attr.taxa", score);
				}
				else
				{
					System.out.println("??");
				}
			}
			else
			{
				System.out.println("Unknown Attack Vector: " + id + " :: " + line);
			}
		}
	}
}
