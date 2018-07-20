package edu.vcu.cyber.dashboard.cybok.queries;

import edu.vcu.cyber.dashboard.Application;
import edu.vcu.cyber.dashboard.cybok.CybokQuery;

public class UpdateQuery extends CybokQuery<UpdateQuery>
{

	private String status;

	@Override
	public String[] searchQuery()
	{
		return new String[]{"--update"};
	}

	public void onExecute()
	{
		Application.getInstance().setStatusLabel("Updating Cybok...");
	}

	@Override
	public void onMessage(String line)
	{
		status = line;
		Application.getInstance().setStatusLabel(status);
	}
}
