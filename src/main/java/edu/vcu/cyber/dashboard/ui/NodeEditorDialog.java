package edu.vcu.cyber.dashboard.ui;

import edu.vcu.cyber.dashboard.data.GraphData;
import edu.vcu.cyber.dashboard.data.NodeData;
import edu.vcu.cyber.dashboard.project.AppSession;
import edu.vcu.cyber.dashboard.util.Attributes;
import org.graphstream.graph.Node;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

public class NodeEditorDialog extends JDialog implements ActionListener
{

	private static final String[] attrColumnNames = {"Attribute", "Value"};
	private static final String[] edgesColumnNames = {"Node", "Type"};

	private JTable attributesTable;
	private JTable edgesTable;
	private JTextField nodeName;

	private NodeData node;

	private static NodeEditorDialog instance;

	public NodeEditorDialog()
	{
		initComponents();
		setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);

		addWindowFocusListener(new WindowFocusListener()
		{
			@Override
			public void windowGainedFocus(WindowEvent e)
			{

			}

			@Override
			public void windowLostFocus(WindowEvent e)
			{
				requestFocus();
			}
		});
	}

	public void setNode(NodeData node)
	{
		this.node = node;
		if (node != null)
		{
//			GraphData graphData = AppSession.getInstance().getTopGraph();
//			node = graphData.getNode(node.getId());

			GraphData graph = AppSession.getFocusedGraphData();

			if (node.hasAttribute(Attributes.NODE_LABEL))
			{
				nodeName.setText(node.getAttribute(Attributes.NODE_LABEL));
			}
			else
			{
				nodeName.setText(node.getId());
			}


			DefaultTableModel attrModel = new DefaultTableModel();
			attrModel.setColumnIdentifiers(attrColumnNames);
			node.getAttributes().forEach((key, val) ->
			{
				if (!key.startsWith("ui."))
				{
					attrModel.addRow(new String[]{key.replace("attr.", ""), val.toString()});
				}
			});

			attrModel.addTableModelListener(e ->
			{
				int row = e.getFirstRow();
				if (e.getType() == TableModelEvent.UPDATE && row == e.getLastRow())
				{
					// if the data in the last row is updated, add a new blank row
					String col1 = attrModel.getValueAt(row, 0).toString().trim();
					String col2 = attrModel.getValueAt(row, 1).toString().trim();
					boolean isBlank = col1.equals(col2) && col1.isEmpty();
					if (isBlank)
					{
						attrModel.removeRow(row);
					}
					else
					{
						if (e.getFirstRow() == attrModel.getRowCount() - 1)
						{
							attrModel.addRow(new String[]{"", ""});
						}
					}
					if (col1.isEmpty())
					{
						attrModel.setValueAt("attr_name", row, 0);
					}
				}
			});

			attrModel.addRow(new String[]{"", ""});

			attributesTable.setModel(attrModel);


			DefaultTableModel edgeModel = new DefaultTableModel();

			Vector<Object[]> data = new Vector<>();
			Node n = node.getNode();
			n.getEdgeSet().forEach(edge ->
			{
				if (edge.isDirected())
				{
					data.add(new Object[]{
							edge.getOpposite(n),
							edge.getSourceNode().equals(n) ? "Leaving" : "Entering"

					});
				}
				else
				{
					data.add(new Object[]{edge.getOpposite(n), "Undirected"});
				}
			});

			edgeModel.setDataVector(data.toArray(new Object[][]{}), edgesColumnNames);
			edgesTable.setModel(edgeModel);

			List<String> nodeList = new ArrayList<>();
			nodeList.add(" ");
			graph.getNodes().forEach(gn -> nodeList.add(gn.getId()));

			List<String> dirList = new ArrayList<>();
			dirList.add(" ");
			dirList.add("Undirected");
			dirList.add("Leaving");
			dirList.add("Entering");

			setupColumnComboBox(edgesTable, edgesTable.getColumnModel().getColumn(0), nodeList);
			setupColumnComboBox(edgesTable, edgesTable.getColumnModel().getColumn(1), dirList);

			edgeModel.addRow(new Object[]{" ", " "});


			edgeModel.addTableModelListener(e ->
			{
				int row = e.getFirstRow();
				if (e.getType() == TableModelEvent.UPDATE && row == e.getLastRow() && row < edgeModel.getRowCount())
				{
					// if the data in the last row is updated, add a new blank row
					String col1 = edgeModel.getValueAt(row, 0).toString();
					String col2 = edgeModel.getValueAt(row, 1).toString();
					boolean isBlank = col1.equals(" ") && col2.equals(" ");
					if (isBlank)
					{
						edgeModel.removeRow(row);
					}
					else
					{
						if (e.getFirstRow() == edgeModel.getRowCount() - 1)
						{
							edgeModel.addRow(new String[]{" ", " "});
						}
					}
//					if (col1.isEmpty())
//					{
//						edgeModel.setValueAt("attr_name", row, 0);
//					}
				}
			});

		}
	}

	private void initComponents()
	{
		JLabel nameLabel = new JLabel("Node Name");
		JButton applyButton = new JButton("Apply");
		JButton cancelButton = new JButton("Cancel");
		nodeName = new JTextField();
		attributesTable = new JTable();
		edgesTable = new JTable();
		JTabbedPane tabs = new JTabbedPane();

		tabs.add(new JScrollPane(attributesTable), "Attributes");
		tabs.add(new JScrollPane(edgesTable), "Edges");

		applyButton.addActionListener(this);
		cancelButton.addActionListener(this);

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 5, 5);
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weighty = 0.0D;
		c.weightx = 0.0D;
		add(nameLabel, c);

		c.gridx = 1;
		c.weightx = 1.0D;
		add(nodeName, c);

		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1.0D;
		c.weighty = 1.0D;
		c.gridwidth = 2;
		add(tabs, c);

		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		btnPanel.add(cancelButton);
		btnPanel.add(applyButton);

		c.anchor = GridBagConstraints.EAST;
		c.gridwidth = 1;
		c.weighty = 0;
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = 2;
		add(btnPanel, c);

		pack();

	}

	private void setupColumnComboBox(JTable table, TableColumn column, List<String> options)
	{
		JComboBox<String> cb = new JComboBox<>();
		options.forEach(cb::addItem);

		column.setCellEditor(new DefaultCellEditor(cb));

		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setToolTipText("Click for combo box");
		column.setCellRenderer(renderer);
	}


	public void apply()
	{
		GraphData graph = AppSession.getFocusedGraphData();
		int attributeCount = attributesTable.getRowCount();
		node.getNode().clearAttributes();
		node.getAttributes().clear();
		for (int i = 0; i < attributeCount; i++)
		{
			String key = (String) attributesTable.getValueAt(i, 0);
			String value = (String) attributesTable.getValueAt(i, 1);
			if (key.trim().length() != 0)
			{
				if (value.isEmpty())
				{
					value = "true";
				}
				node.setAttribute(key, value);
			}
		}
		node.getNode().getEdgeSet().clear();
		int edgeCount = edgesTable.getRowCount();
		for (int i = 0; i < edgeCount; i++)
		{
			try
			{
				String n = (String) edgesTable.getValueAt(i, 0).toString();
				String dir = (String) edgesTable.getValueAt(i, 1).toString();
				if (n.equals(" ") || dir.equals(" "))
					continue;
				if (dir.equals("Undirected"))
				{
					graph.getGraph().addEdge(node.getId() + "-" + n, node.getId(), n, false);
				}
				else if (dir.equals("Entering"))
				{
					graph.getGraph().addEdge(n + "-" + node.getId(), n, node.getId(), true);
				}
				else
				{
					graph.getGraph().addEdge(node.getId() + "-" + n, node.getId(), n, true);
				}
			}
			catch (Exception e)
			{

			}

		}

		node.setAttribute("ui.label", nodeName.getText());

		dispose();
	}

	public static void edit(NodeData node)
	{
		if (node != null)
		{

			if (instance != null && instance.isVisible())
			{
				instance.requestFocus();
			}
			else
			{
				instance = new NodeEditorDialog();
				instance.setNode(node);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		switch (e.getActionCommand())
		{
			case "Apply":
				apply();
				break;
			case "Cancel":
				dispose();
				break;
		}
	}
}
