package edu.vcu.cyber.dashboard.cybok.queries;

import edu.vcu.cyber.dashboard.Application;
import edu.vcu.cyber.dashboard.cybok.CybokQuery;

import java.util.ArrayList;
import java.util.List;

public class AttackVectorQuery extends CybokQuery<AttackVectorQuery>
{
	private final String input;
	private List<String[]> attackChains;

	public AttackVectorQuery(String input)
	{
		this(input, null);
	}

	public AttackVectorQuery(String input, CybokQuery.Result<AttackVectorQuery> resultHandler)
	{
		super(resultHandler);
		this.input = input;
		attackChains = new ArrayList<>();
	}

	@Override
	public void onExecute()
	{
		Application.getInstance().setStatusLabel("Searching for attack vectors...");
	}

	@Override
	public String[] searchQuery()
	{
		return new String[]{"-i", input};
	}

	@Override
	public void onMessage(String line)
	{

	}
}
