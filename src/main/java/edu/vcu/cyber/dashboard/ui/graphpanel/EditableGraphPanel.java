package edu.vcu.cyber.dashboard.ui.graphpanel;

import com.alee.utils.swing.PopupMenuAdapter;
import edu.vcu.cyber.dashboard.Config;
import edu.vcu.cyber.dashboard.av.AttackVectors;
import edu.vcu.cyber.dashboard.data.GraphType;
import edu.vcu.cyber.dashboard.data.NodeData;
import edu.vcu.cyber.dashboard.ui.ControlToolbar;
import edu.vcu.cyber.dashboard.ui.NodeEditorDialog;
import edu.vcu.cyber.dashboard.util.Attributes;
import edu.vcu.cyber.dashboard.util.Projection;
import edu.vcu.cyber.dashboard.util.Utils;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditableGraphPanel extends GraphPanel implements ActionListener
{

	private static final String CMD_ADD_NODE = "Add Component";
	private static final String CMD_SHOW_ATTACK_VECTORS = "Show Attack Vectors";
	private static final String CMD_CLEAR_ATTACK_VECTORS = "Clear Attack Vectors";

	public EditableGraphPanel(GraphType graphType)
	{
		super(graphType);
		JPopupMenu popupMenu = new JPopupMenu();
//		popupMenu.add("Freeze Autolayout").addActionListener(this);
//		popupMenu.addSeparator();
		popupMenu.add("Properties").addActionListener(this);
		if (graphType == GraphType.TOPOLOGY)
		{
			final JCheckBoxMenuItem cb = new JCheckBoxMenuItem("Show Attack Surfaces");
			popupMenu.add(cb).addActionListener(this);
			popupMenu.addPopupMenuListener(new PopupMenuAdapter()
			{
				@Override
				public void popupMenuWillBecomeVisible(PopupMenuEvent e)
				{
					cb.setSelected(Config.showAttackSurfaces);
				}
			});
			popupMenu.add(CMD_SHOW_ATTACK_VECTORS).addActionListener(this);
			popupMenu.add(CMD_CLEAR_ATTACK_VECTORS).addActionListener(this);
			popupMenu.add(CMD_ADD_NODE).addActionListener(this);
		}
		setComponentPopupMenu(popupMenu);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		switch (e.getActionCommand())
		{
			case "Show Attack Surfaces":
				Config.showAttackSurfaces = ((JCheckBoxMenuItem) e.getSource()).isSelected();
				ControlToolbar.attackSurfaceButton.setSelected(Config.showAttackSurfaces);
				Utils.updateAttackSurfaces();

				break;

			case "Freeze Autolayout":
				viewer.disableAutoLayout();
				break;
			case CMD_SHOW_ATTACK_VECTORS:
				AttackVectors.showInGraph(av -> av.inBucket);

				AttackVectors.forEach(av ->
				{
					if (av.inBucket)
					{
						Projection.project(graph.getGraph(), av);
					}
				});


				break;

			case CMD_CLEAR_ATTACK_VECTORS:
				graph.getGraph().getNodeSet().removeIf(_n -> _n.hasAttribute(Attributes.ATTACK_VECTOR));
				break;

			case "Properties":

				NodeData node = graph.getLastSelectedNode();
				if (node != null)
				{
					NodeEditorDialog.edit(node);
				}

				break;
			case CMD_ADD_NODE:
			{
				String id = JOptionPane.showInputDialog("Node Name: ");
				while (graph.getGraph().getNode(id) != null)
					id += "_";
				graph.getGraph().addNode(id);

			}
			break;
		}
	}
}
