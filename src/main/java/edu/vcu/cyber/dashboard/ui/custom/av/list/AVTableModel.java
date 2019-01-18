package edu.vcu.cyber.dashboard.ui.custom.av.list;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

import static edu.vcu.cyber.dashboard.ui.custom.av.list.AVListPanel.*;

public class AVTableModel extends AbstractTableModel
{
	
	protected List<AVTableRow> rows;
	
	AVTableModel()
	{
		rows = new ArrayList<>();
	}
	
	@Override
	public int getRowCount()
	{
		return rows.size();
	}
	
	@Override
	public int getColumnCount()
	{
		return COLUMN_COUNT;
	}
	
	@Override
	public String getColumnName(int col)
	{
		return TABLE_HEADERS[col];
	}
	
	@Override
	public Object getValueAt(int row, int col)
	{
		if (row < rows.size() && col < COLUMN_COUNT)
		{
			return rows.get(row).getValue(col);
		}
		return null;
	}
	
	@Override
	public void setValueAt(Object val, int row, int col)
	{
		AVTableRow r = rows.get(row);
		if (r != null)
		{
			r.setValue(val, col);
			fireTableCellUpdated(row, col);
		}
	}
	
	@Override
	public Class getColumnClass(int col)
	{
		return COLUMN_CLASS[col];
	}
	
	@Override
	public boolean isCellEditable(int row, int col)
	{
		return col <= 2;
	}
}