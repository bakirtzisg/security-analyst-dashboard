package edu.vcu.cyber.dashboard.ui.docking;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import java.awt.*;

public class DockingPanel extends JPanel implements DockingItem
{

	@Override
	public String getTitle()
	{
		return "Panel";
	}

	enum DockType
	{
		Split, Tabs, Content
	}

	enum DockPos
	{
		Center, Top, Right, Bottom, Left
	}


	private DockingPanel container;
	private JComponent content;
	private DockType type;

	public DockingPanel(JComponent content, DockingPanel container)
	{
		this.container = container;
		this.content = content;
		type = DockType.Content;

		setLayout(new BorderLayout());


		add(content, BorderLayout.CENTER);
	}


	@Override
	public boolean dock(JComponent item, int pos)
	{
		if (content instanceof JSplitPane)
		{
			return false;
		}
		else if (content instanceof JTabbedPane)
		{
			JTabbedPane tabs = (JTabbedPane) content;
			if (item instanceof Dockable)
			{
				tabs.add(((Dockable) item).getTitle(), item);
			}
			else
			{
				tabs.add(item);
			}
		}
		else
		{
			if (pos == 0)
			{
				JTabbedPane tabs = new JTabbedPane();

				remove(content);

				if (content instanceof Dockable)
				{
					tabs.add(((Dockable) content).getTitle(), content);
				}
				else
				{
					tabs.add(content);
				}

				if (item instanceof Dockable)
				{
					tabs.add(((Dockable) item).getTitle(), item);
				}
				else
				{
					tabs.add(item);
				}

				content = tabs;
				add(content, BorderLayout.CENTER);

				return true;
			}
			else
			{
				JSplitPane split = new JSplitPane();
				remove(content);
				item = new DockingPanel(item, this);
				content = new DockingPanel(content, this);
				if (pos == 1 || pos == 3)
				{
					split.setOrientation(JSplitPane.VERTICAL_SPLIT);
					if (pos == 1)
					{
						split.setTopComponent(item);
						split.setBottomComponent(content);
					}
					else
					{
						split.setTopComponent(content);
						split.setBottomComponent(item);
					}
				}
				else
				{
					split.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
					if (pos == 2)
					{
						split.setRightComponent(item);
						split.setLeftComponent(content);
					}
					else
					{

						split.setRightComponent(content);
						split.setLeftComponent(item);
					}
				}
				add(split, BorderLayout.CENTER);
				content = split;
			}
		}

		return false;
	}

	@Override
	public boolean dock(JComponent item)
	{
		return dock(item, 0);
	}

	@Override
	public void undock(JComponent item)
	{
		if (content instanceof JSplitPane)
		{
			JSplitPane split = (JSplitPane) content;

			split.remove(item);
			remove(split);

			for (Component cmp : split.getComponents())
			{
				if (!(cmp instanceof BasicSplitPaneDivider))
				{
					add(cmp, BorderLayout.CENTER);
					content = (JComponent) cmp;
					break;
				}
			}
		}
		else if (content instanceof JTabbedPane)
		{

		}
		else
		{

		}

	}

	@Override
	public boolean canUndock()
	{
		return type == DockType.Content && container != null;
	}

	@Override
	public boolean canDock()
	{
		return true;
	}

	@Override
	public JComponent getComponent()
	{
		return this;
	}

	@Override
	public DockingItem getParentContainer()
	{
		return null;
	}

	@Override
	public void setParentContainer(DockingItem item)
	{

	}
}
