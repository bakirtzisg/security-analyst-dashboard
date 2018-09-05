package edu.vcu.cyber.dashboard.graph.listeners;

import edu.vcu.cyber.dashboard.actions.ActionAVToBucket;
import edu.vcu.cyber.dashboard.actions.ActionManager;
import edu.vcu.cyber.dashboard.actions.ActionNodeDelete;
import edu.vcu.cyber.dashboard.data.AttackVector;
import edu.vcu.cyber.dashboard.data.AttackVectors;
import edu.vcu.cyber.dashboard.data.NodeData;
import edu.vcu.cyber.dashboard.project.AppSession;
import edu.vcu.cyber.dashboard.ui.AttackVectorInfoPanel;
import edu.vcu.cyber.dashboard.ui.BucketPanel;
import edu.vcu.cyber.dashboard.util.NodeUtil;
import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Node;
import org.graphstream.ui.geom.Point3;

import java.awt.event.KeyEvent;
import java.util.List;

public class AVActionListener extends GraphActionListener
{

	private AppSession session;

	public AVActionListener(AppSession session)
	{
		this.session = session;
	}

	@Override
	public void onMousePressed(Node node)
	{
		super.onMousePressed(node);


		if (node != null)
		{
			node.addAttribute("ui.selected");
			view.getCamera().setAutoFitView(false);
			Node n = graph.getNode(node.getId());
			if (n != null)
			{
				Point3 lastPos = Toolkit.nodePointPosition(node);
				n.setAttribute("origPos", lastPos);
			}
		}
	}

	private void returnOriginalPos(Node node)
	{
		if (node != null)
		{
			Node n = graph.getNode(node.getId());
			if (n != null && n.hasAttribute("origPos"))
			{
				Point3 lastPos = n.getAttribute("origPos");
				n.setAttribute("xy", lastPos.x, lastPos.y);
			}
		}
	}

	public void onMouseRelease(Node node)
	{

		super.onMouseRelease(node);
	}

	@Override
	public void onSingleClick(Node node)
	{
		super.onSingleClick(node);
		if (node != null)
		{
//			PropertiesPanel.setText(NodeUtil.generateHtmlString(graphData.getNode(node.getId())));
		}
	}

	@Override
	public void onDoubleClick(Node node)
	{
		super.onDoubleClick(node);

		AttackVector av = AttackVectors.getAttackVector(node.getId());
		if (av != null)
		{
			AttackVectorInfoPanel.display(av);
		}
	}


	public void onMouseExit()
	{
		if (currentNode != null)
		{
			returnOriginalPos(currentNode);
			super.onMouseExit();
		}
	}

	public void onKeyPress(KeyEvent evt)
	{
		super.onKeyPress(evt);
		switch (evt.getKeyCode())
		{
			case KeyEvent.VK_ESCAPE:
				graphData.clearSelected();
				break;
			case KeyEvent.VK_DELETE: // delete -> delete the selected nodes from the graph

				ActionNodeDelete act = new ActionNodeDelete(graphData);
				for (NodeData node : graphData.getSelectedNodes())
				{
					AttackVector av = AttackVectors.getAttackVector(node.getId());
					if (av != null)
					{
						act.addAttackVector(av);
						av.setPos(graphPanel.getViewGraph().getNode(av.qualifiedName));
					}
				}

				ActionManager.action(act);

//				graphData.getSelectedNodes().forEach(node -> deleteNode(node.getNode()));
				graphData.clearSelected();
				break;
			case KeyEvent.VK_Z:// ctrl-Z -> undo last action (not implemented yet)
				if (evt.isControlDown())
				{
					ActionManager.undo();
					// TODO: undo
				}
				break;
			case KeyEvent.VK_Y: // ctrl-Y -> redo last action (not implemented yet)
				if (evt.isControlDown())
				{
					ActionManager.redo();
					// TODO: redo
				}
				break;
			case KeyEvent.VK_B: // ctrl-B -> add nodes to bucket
				if (evt.isControlDown())
				{
					moveToBucket();
				}
				break;
			case KeyEvent.VK_I:
				NodeData nd = graphData.getLastSelectedNode();
				if (nd != null)
				{
					AttackVector av = AttackVectors.getAttackVector(nd.getId());
					if (av != null)
					{
						AttackVectorInfoPanel.display(av);
					}
				}
				break;
		}
	}

	private void deleteNode(Node node)
	{
		if (node != null)
		{
			NodeUtil.toggleConsumed(graphData, node.getId());
			AttackVector av = AttackVectors.getAttackVector(node.getId());
			av.deleted = true;

			graphData.flagRemoval(node.getId());
			graphData.removeFlagged();
		}
	}

	public void moveToBucket()
	{

		ActionAVToBucket act = new ActionAVToBucket();
		for (NodeData nd : graphData.getSelectedNodes())
		{
			AttackVector attack = AttackVectors.getAttackVector(nd.getId());
			if (attack != null)
			{
				act.addAttackVector(attack);
			}
		}
		if (act.getActionSize() > 0)
		{
			ActionManager.action(act);
		}

//		graphData.getSelectedNodes().forEach(node ->
//		{
//			AttackVector attack = AttackVectors.getAttackVector(node.getId());
//			if (attack != null)
//			{
//				BucketPanel.instance.addRow(attack);
//			}
//		});


	}
}
