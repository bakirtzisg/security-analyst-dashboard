package edu.vcu.cyber.dashboard.ui.custom.av.tree;

import org.w3c.dom.css.Rect;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

import static edu.vcu.cyber.dashboard.ui.custom.av.list.AVListPanel.*;

public class AVTreeCellRenderer extends DefaultTreeCellRenderer
{


//	@Override
//	public Component getTreeCellRendererComponent(JTree tree, Object val, boolean selected, boolean expanded, boolean leaf, int row, boolean focus)
//	{
//		JLabel c = (JLabel) super.getTreeCellRendererComponent(tree, val, selected, expanded, leaf, row, focus);
//		c.setOpaque(true);
//		setBorder(new LineBorder(Color.lightGray));
//
//
//		if (val instanceof AVTreeNode)
//		{
//			switch (((AVTreeNode) val).av.type)
//			{
//				case CAPEC:
//
//					setBackground(CAPEC_COLOR);
//					break;
//				case CWE:
//					setBackground(CWE_COLOR);
//					break;
//				case CVE:
//					setBackground(CVE_COLOR);
//			}
//		}
//
//		return c;
//	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object val, boolean selected, boolean expanded, boolean leaf, int row, boolean focus)
	{
		Component c = super.getTreeCellRendererComponent(tree, val, selected, expanded, leaf, row, focus);
		setBorder(new LineBorder(Color.lightGray));
		setOpaque(true);
		if (val instanceof AVTreeNode)
		{
			switch (((AVTreeNode) val).av.type)
			{
				case CAPEC:
					setBackground(CAPEC_COLOR);

					break;
				case CWE:
					setBackground(CWE_COLOR);
					break;
				case CVE:
					setBackground(CVE_COLOR);
			}
		}

		return c;
	}
}
