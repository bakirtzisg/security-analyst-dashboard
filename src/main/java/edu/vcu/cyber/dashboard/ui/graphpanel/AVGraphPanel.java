package edu.vcu.cyber.dashboard.ui.graphpanel;

import edu.vcu.cyber.dashboard.actions.ActionManager;
import edu.vcu.cyber.dashboard.actions.ActionNodeDelete;
import edu.vcu.cyber.dashboard.data.*;
import edu.vcu.cyber.dashboard.graph.listeners.AVActionListener;
import edu.vcu.cyber.dashboard.project.AppSession;
import edu.vcu.cyber.dashboard.ui.FilterToolbar;
import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AVGraphPanel extends GraphPanel implements ActionListener
{
	
	private static final String CMD_SHOW_CVE = "Show Related";
	private static final String CMD_HIDE_RELATED = "Hide Related";
	private static final String CMD_ADD_BUCKET = "Add to bucket";
	private static final String CMD_DELETE = "Delete Selected";
	
	private boolean cve_shown;
	
	
	public AVGraphPanel(GraphType graphType)
	{
		super(graphType);
		JPopupMenu popupMenu = new JPopupMenu();
//		popupMenu.add(CMD_SHOW_CVE).addActionListener(this);
//		popupMenu.add(CMD_HIDE_RELATED).addActionListener(this);
		popupMenu.addSeparator();
		popupMenu.add(CMD_ADD_BUCKET).addActionListener(this);
		popupMenu.add(CMD_DELETE).addActionListener(this);
		
		setComponentPopupMenu(popupMenu);
		
		add(new FilterToolbar(), BorderLayout.NORTH);
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		switch (e.getActionCommand())
		{
			case CMD_SHOW_CVE:
				cve_shown = !cve_shown;
				
				List<NodeData> nodes = graph.getSelectedNodes();
				NodeData node = graph.getLastSelectedNode();
//				for (NodeData node : nodes)
				if (node != null)
				{
					AttackVector av = AttackVectors.getAttackVector(node.getId());
					if (av != null)
					{
//
						AttackVectors.showAllRelated(av.qualifiedName, graph);
					}
				}
				else
				{
					
					if (cve_shown)
					{
						AttackVectors.hideAttacks(av -> false);
						AttackVectors.showInGraph(graph, av -> av.shown | av.type == AttackType.CVE);
					}
					else
					{
						AttackVectors.hideAttacks(av -> av.type == AttackType.CVE);
						AttackVectors.showInGraph(graph, av -> av.shown && av.type != AttackType.CVE);
					}
				}
				
				
				break;
			
			case CMD_HIDE_RELATED:
				AttackVectors.hideAttacks(av -> av.type == AttackType.CVE);
				AttackVectors.showInGraph(graph, av -> av.shown && av.type != AttackType.CVE);
				break;
			
			case CMD_ADD_BUCKET:
				((AVActionListener) mouseManager.getListener()).moveToBucket();
				break;
			
			case CMD_DELETE:
				
				ActionNodeDelete act = new ActionNodeDelete(graph);
				for (NodeData n : graph.getSelectedNodes())
				{
					AttackVector av = AttackVectors.getAttackVector(n.getId());
					if (av != null)
					{
						act.addAttackVector(av);
						av.setPos(getViewGraph().getNode(av.qualifiedName));
					}
				}
				
				ActionManager.action(act);
				break;
		}
	}
}
