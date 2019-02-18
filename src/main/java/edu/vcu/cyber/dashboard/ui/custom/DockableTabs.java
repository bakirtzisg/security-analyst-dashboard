package edu.vcu.cyber.dashboard.ui.custom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DockableTabs extends JTabbedPane implements MouseListener, MouseMotionListener
{
	private Point mouseStartingPoint;

	private Component focusedComponent;
	private String focusedTabTitle;

	private DockingListener listener;

	public DockableTabs()
	{
		super();
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}

	public void setDockingListener(DockingListener listener)
	{
		this.listener = listener;
	}


	private UndockedFrame undock(Component current, String title)
	{
		Point p = current.getLocationOnScreen();
		remove(current);
		UndockedFrame frame = new UndockedFrame(current, title);

		p.translate(20, 20);
		frame.setLocation(p);
		frame.setVisible(true);
		fireStateChanged();
		return frame;
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{

	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		mouseStartingPoint = e.getPoint();


		for (int i = 0; i < getTabCount(); i++)
		{
			Rectangle bounds = getBoundsAt(i);
			if (bounds.contains(mouseStartingPoint))
			{
				focusedComponent = getComponentAt(i);
				focusedTabTitle = getTitleAt(i);
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		focusedComponent = null;
		focusedTabTitle = null;
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{

	}

	@Override
	public void mouseExited(MouseEvent e)
	{

	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		if (focusedComponent != null)
		{
			if (e.getPoint().distance(mouseStartingPoint) > 50)
			{
				UndockedFrame frame = undock(focusedComponent, focusedTabTitle);

				if (listener != null)
				{
					listener.onDockingEvent(this, frame, false);
				}

				focusedComponent = null;
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{

	}

	private class UndockedFrame extends JFrame
	{
		Component current;

		UndockedFrame(Component current, String title)
		{
			this.current = current;

			this.setTitle(title);

			Container content = getContentPane();
			content.setLayout(new BorderLayout());

			content.add(current, BorderLayout.CENTER);

			setBounds(current.getBounds());
			addWindowListener(new WindowAdapter()
			{
				@Override
				public void windowClosing(WindowEvent e)
				{
					Window w = e.getWindow();
					if (w instanceof UndockedFrame)
					{
						UndockedFrame frame = (UndockedFrame) w;
						frame.redock();

						if (listener != null)
						{
							listener.onDockingEvent(DockableTabs.this, frame, true);
						}
					}
				}
			});
		}

		void redock()
		{
			this.dispose();
			DockableTabs.this.add(getTitle(), current);
		}
	}

	public interface DockingListener
	{
		void onDockingEvent(DockableTabs tabs, Component component, boolean dock);
	}
}
