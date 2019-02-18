package edu.vcu.cyber.dashboard.cybok.queries;

import edu.vcu.cyber.dashboard.Application;
import edu.vcu.cyber.dashboard.cybok.CybokQuery;

import java.util.ArrayList;
import java.util.List;

public class AttackChainSearchQuery extends CybokQuery
{

	private static final String ATTACK_SEPARATOR = "↦";
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

	private List<String> chain = new ArrayList<>();


	@Override
	public void onMessage(String line)
	{
		if (startRead && !line.startsWith("===="))
		{
			if (line.contains(ATTACK_SEPARATOR))
			{
//				System.out.println(line);
				chain.add(line.split(ATTACK_SEPARATOR)[1].trim());
				if (line.contains(target))
				{
					attackChains.add(chain.toArray(new String[]{}));
					System.out.println(chain);
					chain.clear();
				}
			}
			else
			{
				chain.add(line);

//				System.out.println(line);
			}
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
