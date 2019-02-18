package edu.vcu.cyber.dashboard.ui.search;

import edu.vcu.cyber.dashboard.ui.custom.HintTextField;
import edu.vcu.cyber.dashboard.util.EnvUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SearchPanel extends JPanel implements ActionListener
{

	public static void main(String[] args)
	{
		EnvUtils.registerFonts();
		EnvUtils.setLookAndFeel();
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setContentPane(new SearchPanel());
		frame.pack();
		frame.setVisible(true);
	}

	private static final String columnNames[] = {" ", "Match", "Ancestral", "Descendant", "Source"};

	private HintTextField queryText;
	private JCheckBox searchCAPEC, searchCWE, searchCVE;
	private JCheckBox reportCAPEC, reportCWE, reportCVE;
	private JCheckBox taxCheckbox;

	private JRadioButton weightedRadio, unweightedRadio;
	private JRadioButton singleRadio, doubleRadio;

	private JTable taxWeightTable;

	public SearchPanel()
	{
		setLayout(new GridBagLayout());

		initComponents();

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(4, 4, 4, 4);
		add(queryText, c);


		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.setBorder(new TitledBorder("Sources to search"));
		panel.add(searchCAPEC);
		panel.add(searchCWE);
		panel.add(searchCVE);
		c.gridy++;
		add(panel, c);

		panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.setBorder(new TitledBorder("Sources to report"));
		panel.add(reportCAPEC);
		panel.add(reportCWE);
		panel.add(reportCVE);
		c.gridy++;
		add(panel, c);

		panel = new JPanel(new BorderLayout());
		panel.setBorder(new TitledBorder("Taxonomy"));

		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		p1.add(weightedRadio);
		p1.add(unweightedRadio);
		panel.add(p1, BorderLayout.NORTH);

		p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		p1.add(taxCheckbox);
		p1.add(singleRadio);
		p1.add(doubleRadio);
		panel.add(p1, BorderLayout.SOUTH);

		c.gridy++;
		add(panel, c);


		c.gridy++;
		panel = new JPanel(new BorderLayout(5, 5));
		panel.setBorder(new TitledBorder("Taxonomy weight composition"));
		panel.add(taxWeightTable.getTableHeader(), BorderLayout.NORTH);
		panel.add(taxWeightTable, BorderLayout.CENTER);
		add(panel, c);

		panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton btn = new JButton("Cancel");
		btn.addActionListener(this);
		panel.add(btn);

		btn = new JButton("Search");
		btn.addActionListener(this);
		panel.add(btn);
		c.gridy++;
		add(panel, c);


	}

	private void initComponents()
	{
		queryText = new HintTextField("Search Query");
		searchCAPEC = new JCheckBox("CAPEC", true);
		searchCWE = new JCheckBox("CWE", true);
		searchCVE = new JCheckBox("CVE", true);

		reportCAPEC = new JCheckBox("CAPEC", true);
		reportCWE = new JCheckBox("CWE", true);
		reportCVE = new JCheckBox("CVE", true);

		weightedRadio = new JRadioButton("Weighted", true);
		weightedRadio.addActionListener(this);
		unweightedRadio = new JRadioButton("Unweighted");
		unweightedRadio.addActionListener(this);
		ButtonGroup group1 = new ButtonGroup();
		group1.add(weightedRadio);
		group1.add(unweightedRadio);

		singleRadio = new JRadioButton("Single", true);
		doubleRadio = new JRadioButton("Double");
		ButtonGroup group2 = new ButtonGroup();
		group2.add(singleRadio);
		group2.add(doubleRadio);

		taxCheckbox = new JCheckBox("Taxonomic", true);


		taxWeightTable = new JTable(new WeightTableModel());
		taxWeightTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		taxWeightTable.getColumn(" ").setPreferredWidth(100);

	}

	private void attemptCloseWindow()
	{
		try
		{
			Component parent = getParent();
			while (!(parent instanceof Window))
			{
				parent = parent.getParent();
				if (parent == null)
					return;
			}
			((Window) parent).dispose();
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		switch (e.getActionCommand())
		{
			case "Search":

				// TODO: Create the search query

//				SearchConfig.searchConfig.setSearchParams(new boolean[]{searchCAPEC.isSelected(), searchCWE.isSelected(), searchCVE.isSelected()});
//				SearchConfig.searchConfig.setReportedParams(new boolean[]{reportCAPEC.isSelected(), reportCWE.isSelected(), reportCVE.isSelected()});

				attemptCloseWindow();
				break;

			case "Cancel":
				attemptCloseWindow();
				break;

			case "Weighted":
				taxWeightTable.setEnabled(weightedRadio.isSelected());
				taxWeightTable.getTableHeader().setEnabled(weightedRadio.isSelected());
				taxWeightTable.setBackground(Color.WHITE);

				break;
			case "Unweighted":
				taxWeightTable.setEnabled(weightedRadio.isSelected());
				taxWeightTable.getTableHeader().setEnabled(weightedRadio.isSelected());
				taxWeightTable.setBackground(Color.LIGHT_GRAY);
				taxWeightTable.clearSelection();
				break;
		}
	}


	private class WeightTableModel extends AbstractTableModel
	{
		Object[][] data = {
				{"CAPEC", 1.0f, 1.0f, 1.0f, 1.0f},
				{"CWE", 1.0f, 1.0f, 1.0f, 1.0f},
				{"CVE", 1.0f, 1.0f, 1.0f, 1.0f},
		};


		@Override
		public int getRowCount()
		{
			return 3;
		}

		@Override
		public int getColumnCount()
		{
			return 5;
		}

		public String getColumnName(int col)
		{
			return columnNames[col];
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			return data[rowIndex][columnIndex];
		}

		public void setValueAt(Object value, int row, int col)
		{

			data[row][col] = (float) value;

			fireTableCellUpdated(row, col);
		}

		@Override
		public Class getColumnClass(int column)
		{
			switch (column)
			{
				case 0:
					return String.class;
				default:
					return Float.class;
			}
		}

		public boolean isCellEditable(int row, int col)
		{
			return col > 0;
		}
	}


}
