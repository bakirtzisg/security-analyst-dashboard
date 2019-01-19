package edu.vcu.cyber.dashboard.ui.docking;

import javax.swing.*;
import java.awt.*;

public class DockWrapper extends JPanel implements Dockable
{

	protected JComponent content;
	protected final String title;

	public DockWrapper(JComponent content, String title)
	{
		this.content = content;
		this.title = title;

		setLayout(new BorderLayout());
		add(content, BorderLayout.CENTER);
	}

	public String getTitle()
	{
		return title;
	}
}
