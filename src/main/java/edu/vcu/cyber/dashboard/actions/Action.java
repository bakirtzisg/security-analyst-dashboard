package edu.vcu.cyber.dashboard.actions;

public abstract class Action
{

	private boolean done;

	protected int actionSize;

	public boolean done()
	{
		return done;
	}

	public int getActionSize()
	{
		return actionSize;
	}

	public void action()
	{
		if (!done)
			doAction();
		done = true;
	}

	public void undo()
	{
		if (done)
			undoAction();
		done = false;
	}

	protected abstract void doAction();

	protected abstract void undoAction();

}
