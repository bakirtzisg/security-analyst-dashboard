package edu.vcu.cyber.dashboard.actions;

public class ActionManager
{

	private static ActionManager instance = new ActionManager(1000);

	private static int ActionTrackingCount;

	private int actionSize;

	private ActionNode head;
	private ActionNode base;

	public ActionManager(int size)
	{
		setActionTrackingCount(size);
		head = base = new ActionNode(new BaseAction(), null);
	}

	public void setActionTrackingCount(int count)
	{
		ActionTrackingCount = count;
	}

	public static void action(Action action)
	{
		instance.doAction(action);
	}

	public static void undo()
	{
		instance.undoAction();
	}

	public static void redo()
	{
		instance.redoAction();
	}

	public void doAction(Action action)
	{
		head = head.append(action);
		actionSize += action.getActionSize();
		action.action();

		shiftBottom();
	}

	public void undoAction()
	{
		actionSize -= head.action.getActionSize();
		head.action.undo();
		head = head.prev;

	}

	public void redoAction()
	{
		if (head != null && head.next != null)
		{
			head = head.next;
			head.action.action();
			actionSize += head.action.getActionSize();
			shiftBottom();
		}
	}

	public void clear()
	{
		base.next = null;
		head = base;
	}

	private void shiftBottom()
	{
		while (actionSize > ActionTrackingCount)
		{
			if (base.next != null)
			{
				actionSize -= base.next.action.getActionSize();
				base.next.remove();
			}
			else
			{
				return;
			}
		}
	}

	private class ActionNode
	{
		private ActionNode next;
		private ActionNode prev;
		private Action action;

		ActionNode(Action action, ActionNode prev)
		{
			this.action = action;
			this.prev = prev;
			if (prev == null)
				this.prev = this;
		}

		ActionNode append(Action action)
		{
			return next = new ActionNode(action, this);
		}

		void remove()
		{
			if (prev != null)
			{
				prev.next = next;
			}
		}
	}

	private static class BaseAction extends Action
	{

		BaseAction()
		{
			actionSize = 0;
		}

		@Override
		public void doAction()
		{
		}

		@Override
		public void undoAction()
		{
		}
	}

}
