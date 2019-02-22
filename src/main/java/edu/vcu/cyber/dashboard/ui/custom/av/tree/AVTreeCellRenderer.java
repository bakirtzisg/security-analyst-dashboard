package edu.vcu.cyber.dashboard.ui.custom.av.tree;

import edu.vcu.cyber.dashboard.av.AttackVector;
import edu.vcu.cyber.dashboard.av.VisHandler;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.util.function.Predicate;

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

	private static final Color FilteredColor = new Color(100, 100, 100);
	private static final Border BucketBorder = new LineBorder(Color.darkGray, 1);
	private static final Border NoBucketBorder = new LineBorder(Color.lightGray, 1);

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object val, boolean selected, boolean expanded, boolean leaf, int row, boolean focus)
	{
		Component c = super.getTreeCellRendererComponent(tree, val, selected, expanded, leaf, row, focus);
		if (val instanceof AVTreeNode)
		{
			setOpaque(true);
			AttackVector av = ((AVTreeNode) val).av;

			Predicate<AttackVector> filter = VisHandler.treeVis().getFilter();
			boolean filtered = filter == null || filter.test(av);


			if (av.inBucket)
			{
				setBorder(BucketBorder);
			}
			else
			{
				setBorder(NoBucketBorder);
			}
			setForeground(av.deleted ? Color.RED : filtered ? Color.BLACK : FilteredColor);

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
		else
		{
			setOpaque(false);
		}

		return c;
	}
}
