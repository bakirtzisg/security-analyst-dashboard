package edu.vcu.cyber.dashboard.cybok.queries;

import edu.vcu.cyber.dashboard.Application;
import edu.vcu.cyber.dashboard.cybok.CybokQuery;

import java.util.ArrayList;
import java.util.List;

public class AttackChainSearchQuery extends CybokQuery
{

	private static final String ATTACK_SEPARATOR = " -> ";
	private static final String READ_START_LINE = "Exploit chain analysis";

	private final String input, target;
	private List<String[]> attackChains;
	private boolean startRead;

	public AttackChainSearchQuery(String input, String target)
	{
		this(input, target, null);
	}

	public AttackChainSearchQuery(String input, String target, CybokQuery.Result<AttackChainSearchQuery> resultHandler)
	{
		super(resultHandler);
		this.input = input;
		this.target = target;
		attackChains = new ArrayList<>();
	}


	public void onExecute()
	{
		Application.getInstance().setStatusLabel("Searching for attack chains for " + target + "...");
	}

	@Override
	public String[] searchQuery()
	{
		return new String[]{"-i", input, "-t", target};
	}

	@Override
	public void onMessage(String line)
	{
		if (startRead && line.contains(ATTACK_SEPARATOR))
		{
//			System.out.println(line);
			String[] chain = line.split(ATTACK_SEPARATOR);
			attackChains.add(chain);
		}
		else if (line.equals(READ_START_LINE))
		{
			startRead = true;
		}
	}

	public List<String[]> getAttackChains()
	{
		return attackChains;
	}

}
