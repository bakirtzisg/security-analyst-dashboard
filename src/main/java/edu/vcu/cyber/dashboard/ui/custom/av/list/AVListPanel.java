package edu.vcu.cyber.dashboard.ui.custom.av.list;

import edu.vcu.cyber.dashboard.av.AttackVector;
import edu.vcu.cyber.dashboard.av.AttackVectors;
import edu.vcu.cyber.dashboard.data.AttackType;
import edu.vcu.cyber.dashboard.ui.BucketPanel;
import edu.vcu.cyber.dashboard.util.FilterPredicate;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class AVListPanel extends JPanel
{
	
	protected static AVListPanel inst;
	
	public static final Color CAPEC_COLOR = new Color(255, 15, 25, 125);
	public static final Color CWE_COLOR = new Color(11, 36, 251, 125);
	public static final Color CVE_COLOR = new Color(254, 188, 108, 125);
	
	protected static final String[] TABLE_HEADERS = {"B", "I", "Attack", "Name"};
	protected static final Class[] COLUMN_CLASS = {Boolean.class, Boolean.class, String.class, String.class};
	protected static final int[] COLUMN_WIDTH = {40, 40, 120, -1};
	protected static final int COLUMN_COUNT = TABLE_HEADERS.length;
	
	protected AVTableModel model;
	protected JTable table;
	
	private TableRowSorter<AVTableModel> sorter;
	private AVTableRowFilter filter;
	
	protected static boolean hideChecked = true;
	
	public AVListPanel()
	{
		inst = this;
		setLayout(new BorderLayout());
		createTable();
		add(new JScrollPane(table), BorderLayout.CENTER);
	}
	
	private void createTable()
	{
		model = new AVTableModel();
		table = new JTable(model);
		
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setCellSelectionEnabled(false);
		table.setUpdateSelectionOnSort(true);
		table.setRowSelectionAllowed(true);
		table.setAutoCreateColumnsFromModel(false);
		
		AVTableCellRenderer cellRenderer = new AVTableCellRenderer(model);
		
		table.setDefaultRenderer(String.class, cellRenderer);
		
		sorter = new TableRowSorter<>(model);
		table.setRowSorter(sorter);

//		table.setColumnModel();
		
		filter = new AVTableRowFilter(model);
		sorter.setRowFilter(filter);
		sorter.setSortsOnUpdates(true);
		
		TableColumnModel columnModel = new DefaultTableColumnModel();
		
		for (int i = 0; i < COLUMN_COUNT; i++)
		{
			TableColumn col = new TableColumn(i, COLUMN_WIDTH[i]);
//			col.setPreferredWidth(COLUMN_WIDTH[i]);
			if (COLUMN_WIDTH[i] != -1)
				col.setMaxWidth(COLUMN_WIDTH[i]);
			col.setHeaderValue(TABLE_HEADERS[i]);
//			col.sizeWidthToFit();
			
			columnModel.addColumn(col);
		}
		table.setColumnModel(columnModel);
		
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
	}
	
	
	public void populate()
	{
		model.rows.clear();
		AttackVectors.getAllAttackVectors().forEach(this::addRow);
		refresh();
	}
	
	public void filter(Predicate<AttackVector> filter)
	{
		this.filter.setFilter(filter);
		sorter.sort();
	}
	
	public void dispose()
	{
		model.rows.clear();
	}
	
	public void addRow(AttackVector av)
	{
		model.rows.add(new AVTableRow(av));
		model.fireTableStructureChanged();
	}
	
	public void removeRow(AttackVector av)
	{
		model.rows.remove(new AVTableRow(av));
		refresh();
	}
	
	public void clearAll()
	{
		model.rows.clear();
		model.fireTableDataChanged();
		sorter.sort();
	}
	
	public boolean contains(AttackVector av)
	{
		return model.rows.contains(new AVTableRow(av));
	}
	
	public void refresh()
	{
		model.fireTableDataChanged();
		sorter.modelStructureChanged();
		sorter.sort();
	}
	
	
}
