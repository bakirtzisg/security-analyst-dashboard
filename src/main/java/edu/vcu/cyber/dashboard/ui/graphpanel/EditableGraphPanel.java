package edu.vcu.cyber.dashboard.ui.graphpanel;

import edu.vcu.cyber.dashboard.data.AttackVectors;
import edu.vcu.cyber.dashboard.data.GraphType;
import edu.vcu.cyber.dashboard.data.NodeData;
import edu.vcu.cyber.dashboard.ui.NodeEditorDialog;
import edu.vcu.cyber.dashboard.util.Attributes;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditableGraphPanel extends GraphPanel implements ActionListener
{

	public EditableGraphPanel(GraphType graphType)
	{
		super(graphType);
		JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.add("Freeze Autolayout").addActionListener(this);
		popupMenu.addSeparator();
		popupMenu.add("Properties").addActionListener(this);
		if (graphType == GraphType.TOPOLOGY)
		{
			popupMenu.add("Show Attack Vectors").addActionListener(this);
			popupMenu.add("Clear Attack Vectors").addActionListener(this);
		}
		setComponentPopupMenu(popupMenu);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		switch (e.getActionCommand())
		{
			case "Freeze Autolayout":
				viewer.disableAutoLayout();
				break;
			case "Properties":

				NodeData node = graph.getLastSelectedNode();
				if (node != null)
				{
					NodeEditorDialog.edit(node);
				}

				break;

			case "Show Attack Vectors":
				AttackVectors.showInGraph(graph, av -> av.inBucket);
				break;

			case "Clear Attack Vectors":
				graph.getGraph().getNodeSet().removeIf(_n -> _n.hasAttribute(Attributes.ATTACK_VECTOR));
				break;
		}
	}
}
