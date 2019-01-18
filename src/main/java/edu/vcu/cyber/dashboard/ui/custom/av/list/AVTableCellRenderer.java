package edu.vcu.cyber.dashboard.ui.custom.av.list;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

import static edu.vcu.cyber.dashboard.ui.custom.av.list.AVListPanel.*;

public class AVTableCellRenderer extends DefaultTableCellRenderer
{
	
	private AVTableModel model;
	
	public AVTableCellRenderer(AVTableModel model)
	{
		this.model = model;
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object val, boolean selected, boolean focus, int row, int col)
	{
		JComponent c = (JLabel) super.getTableCellRendererComponent(table, val, selected, focus, row, col);
		
		row = table.convertRowIndexToModel(row);
		AVTableRow tr = model.rows.get(row);
		switch (tr.av.type)
		{
			case CAPEC:
				c.setBackground(CAPEC_COLOR);
				break;
			case CWE:
				c.setBackground(CWE_COLOR);
				break;
			case CVE:
				c.setBackground(CVE_COLOR);
		}
		
		c.setToolTipText(tr.av.createTooltip());
		
		
		return c;
	}
}