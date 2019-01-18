package edu.vcu.cyber.dashboard.ui.custom.av.tree;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class AVTreeCellRenderer extends DefaultTreeCellRenderer
{
	
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object val, boolean selected, boolean expanded, boolean leaf, int row, boolean focus)
	{
		JComponent c = (JLabel) super.getTreeCellRendererComponent(tree, val, selected, expanded, leaf, row, focus);
		
		return c;
	}
}
