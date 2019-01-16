package edu.vcu.cyber.dashboard.ui.custom.av;

import edu.vcu.cyber.dashboard.data.AttackVector;
import edu.vcu.cyber.dashboard.data.AttackVectors;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AVListPanel extends JPanel
{
	private static final Color CAPEC_COLOR = new Color(255, 15, 25, 125);
	private static final Color CWE_COLOR = new Color(11, 36, 251, 125);
	private static final Color CVE_COLOR = new Color(254, 188, 108, 125);
	
	private static final String[] TABLE_HEADERS = {"B", "U", "I", "Attack", "Name"};
	private static final Class[] COLUMN_CLASS = {Boolean.class, Boolean.class, Boolean.class, String.class, String.class};
	private static final int COLUMN_COUNT = TABLE_HEADERS.length;
	
	protected AVTableModel model;
	protected JTable table;
	
	
	private void createTable()
	{
		model = new AVTableModel();
		table = new JTable(model);
		
		table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setCellSelectionEnabled(false);
		table.setUpdateSelectionOnSort(true);
		table.setRowSelectionAllowed(true);
		
		table.setDefaultRenderer(String.class, new AVTableCellRenderer());
		
	}
	
	public void populate()
	{
		model.rows.clear();
		AttackVectors.getAllAttackVectors().forEach(av ->
		{
			if (av.inBucket)
			{
			
			}
			
			addRow(av);
			
		});
	}
	
	private void addRow(AttackVector av)
	{
		model.rows.add(new TableRow(av));
	}
	
	public void dispose()
	{
		model.rows.clear();
	}
	
	
	private class AVTableModel extends AbstractTableModel
	{
		
		private List<TableRow> rows;
		
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
			TableRow r = rows.get(row);
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
	
	private class AVTableCellRenderer extends DefaultTableCellRenderer
	{
		@Override
		public Component getTableCellRendererComponent(JTable table, Object val, boolean selected, boolean focus, int row, int col)
		{
			Component c = super.getTableCellRendererComponent(table, val, selected, focus, row, col);
			
			row = table.convertRowIndexToModel(row);
			TableRow tr = model.rows.get(row);
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
			
			return c;
		}
	}
	
	/**
	 * Object created for every entry to the table
	 */
	private class TableRow
	{
		// the AttackVector associated with this row
		AttackVector av;
		
		TableRow(AttackVector av)
		{
			this.av = av;
		}
		
		/**
		 * converts the input index to the respective output value
		 */
		Object getValue(int index)
		{
			switch (index)
			{
				case 0:
					return av.inBucket;
				case 1:
					return !av.needCheck;
				case 2:
					return av.deleted;
				case 3:
					return av.qualifiedName;
				case 4:
					return av.description;
				default:
					return "--";
			}
		}
		
		/**
		 * attempts to set the specified value
		 */
		void setValue(Object val, int col)
		{
			switch (col)
			{
				case 0: // important
					av.inBucket = (boolean) val;
					av.needCheck = false;
					break;
				case 1: // unchecked
					av.needCheck = (boolean) val;
					break;
				case 2: // ignore
					av.deleted = (boolean) val;
					av.needCheck = false;
					break;
			}
		}
		
		@Override
		public boolean equals(Object obj)
		{
			return obj instanceof TableRow && av.qualifiedName.equals(((TableRow) obj).av.qualifiedName);
		}
	}
	
}
