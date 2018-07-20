package edu.vcu.cyber.dashboard.cybok;

import edu.vcu.cyber.dashboard.Application;

import javax.swing.*;

public abstract class CybokQuery<T extends CybokQuery>
{

	private boolean isDone;
	private Result<T> resultHandler;

	private boolean hadError;

	public CybokQuery()
	{

	}

	public CybokQuery(Result<T> resultHandler)
	{
		this.resultHandler = resultHandler;
	}

	public abstract String[] searchQuery();

	public abstract void onMessage(String line);

	public boolean hadError()
	{
		return hadError;
	}

	public void onExecute()
	{

	}

	public boolean shouldRun()
	{
		return true;
	}

	public void onFinish(boolean error)
	{
		hadError = error;

		Application.getInstance().setStatusLabel(" ");

		if (resultHandler != null)
		{
			SwingUtilities.invokeLater(() ->
			{
				resultHandler.onResult((T) this);
			});
		}
	}

	public void setResultHandler(Result<T> resultHandler)
	{
		this.resultHandler = resultHandler;
	}

	protected final void done()
	{
		isDone = true;
	}

	protected final boolean isDone()
	{
		return isDone;
	}

	public interface Result<T extends CybokQuery>
	{
		void onResult(T query);
	}

}
