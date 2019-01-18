package edu.vcu.cyber.dashboard.ui.docking;

import javax.swing.*;

public class DockingPanel extends JPanel implements DockingItem
{
	
	
	private DockingPanel container;
	private JComponent content;
	
	public DockingPanel(JComponent content, DockingPanel container)
	{
		this.container = container;
		this.content = content;
	}
	
	public void add(JComponent comp)
	{
	
	}
	
	public void remove(JComponent comp)
	{
	
	
	}
	
}
