package edu.vcu.cyber.dashboard.ui;

import edu.vcu.cyber.dashboard.data.AttackVector;
import edu.vcu.cyber.dashboard.data.AttackVectors;
import edu.vcu.cyber.dashboard.data.GraphData;
import edu.vcu.cyber.dashboard.project.AppSession;
import edu.vcu.cyber.dashboard.ui.custom.HintTextField;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BucketPanel extends JPanel implements ListSelectionListener, ActionListener, KeyListener
{


	private static final Color CAPEC_COLOR = new Color(255, 15, 25, 125);
	private static final Color CWE_COLOR = new Color(11, 36, 251, 125);
	private static final Color CVE_COLOR = new Color(254, 188, 108, 125);

	private static final String[] columnNames = {"CB", "Attack", "Description", "Violated Components"};
	public static BucketPanel instance = new BucketPanel();


	public static BucketPanel showBucket(boolean visible)
	{
		return instance;
	}


	public CustomTableModel tableModel;

	private TableRowSorter<CustomTableModel> sorter;

	private JTable table;
	//	private JTextPane infoPane;
	private JTextField searchText;
	//	private JSplitPane splitPane;
	private JComboBox<String> filterMethodCombo;

	private JPanel contentPanel;

//	private JScrollPane infoScrollPane;
//
//	private boolean detached;


	public BucketPanel()
	{

		instance = this;
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);

		createTable();
		contentPanel = new JPanel(new BorderLayout());

//		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
//		infoPane = new JTextPane();
		JButton searchButton = new JButton("Filter Entries");
		searchText = new HintTextField("Filter Bucket Entries...");

		filterMethodCombo = new JComboBox<>(new String[]{"All", "Attacks", "Description", "Component", "Contents"});

		searchButton.addActionListener(this);
		searchText.addKeyListener(this);

//		infoPane.setContentType("text/html");
//		infoPane.setEditable(false);
//		infoPane.setText("<h> Attack Information </h>");

		JScrollPane tableScroll = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		table.addKeyListener(this);

//		splitPane.setTopComponent(tableScroll);
//		infoScrollPane = new JScrollPane(infoPane);
//		splitPane.setBottomComponent(infoScrollPane);
//		splitPane.setDividerSize(3);
//		SwingUtilities.invokeLater(() ->
//		{
//
//			splitPane.setDividerLocation(getHeight() / 3);
//		});

		contentPanel.add(tableScroll, BorderLayout.CENTER);

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(2, 2, 2, 2);
		c.fill = GridBagConstraints.BOTH;

		// search/filter
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0D;
		add(searchText, c);

		c.gridx = 1;
		c.weightx = 0.0D;

		add(filterMethodCombo, c);

		c.gridx = 2;
		add(searchButton, c);

		// table/info content
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy++;
		c.weighty = 1.0D;
		add(tableScroll, c);


	}

	private void createTable()
	{
		tableModel = new CustomTableModel();
		table = new JTable(tableModel);

		table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setCellSelectionEnabled(false);
		table.setUpdateSelectionOnSort(true);
		table.setRowSelectionAllowed(true);


		table.setDefaultRenderer(String.class, new CellRenderer());


		sorter = new TableRowSorter<>(tableModel);
		table.setRowSorter(sorter);

		table.getSelectionModel().addListSelectionListener(this);
		table.getColumnModel().getColumn(0).setMaxWidth(50);
		table.getColumnModel().getColumn(0).setHeaderValue(null);
		table.getColumnModel().getColumn(0).setResizable(false);
		table.getColumnModel().getColumn(0).setPreferredWidth(50);


	}

	public void addRow(AttackVector av)
	{
		TableRow row = new TableRow();

		if (!av.inBucket)
		{
			row.vector = av;
			av.inBucket = true;
			tableModel.addData(row);

			table.getColumnModel().getColumn(0).setMaxWidth(50);
		}
	}

	public void removeRow(AttackVector av)
	{
		if (av.inBucket)
		{
			av.inBucket = false;
			tableModel.rows.removeIf(row -> !row.vector.inBucket);
		}
	}

	public void clear()
	{
		tableModel.rows.clear();
		tableModel.fireTableDataChanged();
	}

	public void bucketFromAVList()
	{
		clear();
		AttackVectors.getAllAttackVectors().forEach(av ->
		{
			if (av.inBucket)
			{
				addRow(av);
			}
		});
//		tableModel.fireTableDataChanged();
	}

	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		GraphData graph = AppSession.getInstance().getAvGraph();
		graph.clearFocus();
		int[] selected = table.getSelectedRows();

		for (int i : selected)
		{
			if (i >= 0)
			{
				int modelRow = table.convertRowIndexToModel(i);

				AttackVector av = tableModel.rows.get(modelRow).vector;

				graph.setFocus(av.qualifiedName);


			}
		}

//		int viewRow = table.getSelectedRow();
//		if (viewRow >= 0)
//		{
//			int modelRow = table.convertRowIndexToModel(viewRow);
//
//			AttackVector av = tableModel.rows.get(modelRow).vector;
//
//			graph.setFocus(av.qualifiedName);
//
//
//		}

	}

	private void applyFilter()
	{
		RowFilter<CustomTableModel, Object> rf = null;
		try
		{
			int filterMode = filterMethodCombo.getSelectedIndex();
			Pattern pattern = Pattern.compile(searchText.getText().toLowerCase());
			rf = new RowFilter<CustomTableModel, Object>()
			{
				@Override
				public boolean include(Entry<? extends CustomTableModel, ?> entry)
				{
					int index = (Integer) entry.getIdentifier();
					TableRow row = tableModel.rows.get(index);

					String searchString = "";

					switch (filterMode)
					{
						case 0: // all
							searchString = String.format("attack[ %s ]; description[ %s ]; components[ %s ]; contents[ %s ];",
									row.vector.qualifiedName, row.vector.description,
									row.vector.violatedComponents.toString(), row.vector.contents);
							break;
						case 1: // attack
							searchString = String.format("attack[ %s ];", row.vector.qualifiedName);
							break;
						case 2: // description
							searchString = String.format("description[ %s ];", row.vector.description);
							break;
						case 3: // component
							searchString = String.format("components[ %s ];", row.vector.violatedComponents.toString());
							break;
						case 4: // contents
							searchString = String.format("contents[ %s ];", row.vector.contents);
							break;
					}
					searchString = searchString.toLowerCase();

					Matcher matcher = pattern.matcher(searchString);
					return matcher.find();
				}
			};


		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		sorter.setRowFilter(rf);
		tableModel.fireTableStructureChanged();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		switch (e.getActionCommand())
		{
			case "Filter Entries":
				applyFilter();
				break;
			default:
		}
	}

	@Override
	public void keyTyped(KeyEvent e)
	{

	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			applyFilter();
		}
		else if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			table.clearSelection();
		}
		else if (e.getKeyCode() == KeyEvent.VK_DELETE)
		{
//			if (e.isControlDown())
//			{
			tableModel.rows.forEach(row -> row.vector.inBucket = !row.selected);
			tableModel.rows.removeIf(row -> row.selected);
			tableModel.fireTableDataChanged();
//			}
//			else
//			{
//				int selectedRow = table.getSelectedRow();
//				if (selectedRow < tableModel.rows.size())
//				{
//					selectedRow = table.convertColumnIndexToModel(selectedRow);
//					if (selectedRow != -1)
//					{
//						TableRow row = tableModel.rows.get(selectedRow);
//						row.vector.inBucket = false;
//						tableModel.rows.remove(row);
//						tableModel.fireTableRowsDeleted(selectedRow, selectedRow);
//					}
//				}
//			}

		}
		else if (e.getKeyCode() == KeyEvent.VK_A && (e.isControlDown() || e.isAltDown()))
		{
			boolean isCtrl = e.isControlDown();
			for (int i = 0; i < table.getRowCount(); i++)
			{
				int row = table.convertRowIndexToModel(i);
				tableModel.rows.get(row).selected = isCtrl;
			}
			tableModel.fireTableDataChanged();
		}
	}

	private void toggleInfoPane(boolean visible)
	{

	}

	@Override
	public void keyReleased(KeyEvent e)
	{

	}


	public static class CustomTableModel extends AbstractTableModel
	{

		List<TableRow> rows = new ArrayList<>();

		@Override
		public int getRowCount()
		{
			return rows.size();
		}

		@Override
		public int getColumnCount()
		{
			return columnNames.length;
		}

		public String getColumnName(int col)
		{
			return columnNames[col];
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			if (rowIndex < rows.size())
			{
				TableRow info = rows.get(rowIndex);
				return info.contents()[columnIndex];
			}
			return "";
		}

		@Override
		public Class getColumnClass(int column)
		{
			switch (column)
			{
				case 0:
					return Boolean.class;
				default:
					return String.class;
			}
		}

		public boolean isCellEditable(int row, int col)
		{
			return col == 0;
		}

		public void setValueAt(Object value, int row, int col)
		{
			TableRow r = rows.get(row);
			r.selected = (boolean) value;
			fireTableCellUpdated(row, col);
		}

		public void addData(TableRow tableRow)
		{
			try
			{
				if (!rows.contains(tableRow))
				{
					rows.add(tableRow);
					fireTableRowsInserted(rows.size() - 2, rows.size() - 1);
				}
			} catch (Exception e)
			{

			}
		}
	}

	private class CellRenderer extends DefaultTableCellRenderer
	{

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			row = table.convertRowIndexToModel(row);
			TableRow tr = tableModel.rows.get(row);
			switch (tr.vector.type)
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

	public static class TableRow
	{
		public boolean selected; // checkbox
		public AttackVector vector;

		Object[] contents()
		{
			return new Object[]{selected, vector.qualifiedName, vector.description, vector.violatedComponents};
		}

		public boolean equals(Object obj)
		{
			return obj instanceof TableRow && ((TableRow) obj).vector.equals(vector);
		}
	}

}
