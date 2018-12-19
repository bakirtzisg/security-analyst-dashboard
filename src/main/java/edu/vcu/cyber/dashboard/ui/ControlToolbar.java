package edu.vcu.cyber.dashboard.ui;

import edu.vcu.cyber.dashboard.Config;
import edu.vcu.cyber.dashboard.data.*;
import edu.vcu.cyber.dashboard.project.AppSession;
import edu.vcu.cyber.dashboard.project.GraphAnalysis;
import edu.vcu.cyber.dashboard.util.Utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ControlToolbar extends JToolBar
{
	private static final boolean useIcons = false;
	private static final int iconWidth = 25;
	private static final int iconHeight = 25;
	
	private static final String CMD_LOAD_TOPOLOGY = "Load Topology";
	
	private static final String CMD_TOGGLE_ATTACK_SURFACES = "Attack Surfaces";
	private static final String CMD_REDO_ANALYSIS = "Analysis";
	
	private static final String CMD_SHOW_DELETED = "Show Deleted";
	private static final String CMD_SHOW_HIDDEN = "Show Hidden";
	private static final String CMD_SHOW_CVE = "Show CVE";
	
	private static final String CMD_ADD_BUCKET = "Add to Bucket";
	private static final String CMD_DELETE_ATTACKS = "Delete Attacks";
	
	public ControlToolbar()
	{
		// -------------- Poject Tools --------------
		addButton(CMD_LOAD_TOPOLOGY, "open.png").addActionListener((evt) -> DashboardUI.askLoadTopology());
		
		addSeparator();
		
		// -------------- Analysis Tools --------------
		addToggleButton(CMD_TOGGLE_ATTACK_SURFACES, null).addActionListener((evt) ->
		{
			Config.showAttackSurfaces = isSelected(evt);
			Utils.updateAttackSurfaces();
		});
		addButton(CMD_REDO_ANALYSIS, "analysis.png").addActionListener((evt) -> GraphAnalysis.analyseTopologyGraph());
		
		// -------------- VIEW TOGGLES --------------
		addSeparator();
		
		addToggleButton(CMD_SHOW_DELETED, null).addActionListener((evt) ->
		{
			AttackVectors.showDeletedNodes = isSelected(evt);
			AttackVectors.update(AppSession.getInstance().getAvGraph());
		});
		addToggleButton(CMD_SHOW_HIDDEN, "hidden.png").addActionListener((evt) ->
		{
			AttackVectors.showHiddenNodes = isSelected(evt);
			AttackVectors.update(AppSession.getInstance().getAvGraph());
		});
		addToggleButton(CMD_SHOW_CVE, "vcve.png").addActionListener((evt) ->
		{
			AttackVectors.showCVENodes = isSelected(evt);
			AttackVectors.getAllAttackVectors().forEach(av -> av.hidden = av.type == AttackType.CVE ? !AttackVectors.showCVENodes : av.hidden);
			AttackVectors.update(AppSession.getInstance().getAvGraph());
		});
		
		
		// -------------- Attack Vector Tools --------------
		addSeparator();
		addButton(CMD_ADD_BUCKET, null).addActionListener((evt) ->
		{
			GraphData graphData = AppSession.getInstance().getAvGraph();
			for (NodeData nd : graphData.getSelectedNodes())
			{
				AttackVector attack = AttackVectors.getAttackVector(nd.getId());
				if (attack != null)
					BucketPanel.instance.addRow(attack);
				
			}
		});
		addButton(CMD_DELETE_ATTACKS, "delete.png").addActionListener((evt) ->
		{
			GraphData graphData = AppSession.getInstance().getAvGraph();
			for (NodeData nd : graphData.getSelectedNodes())
			{
				AttackVector attack = AttackVectors.getAttackVector(nd.getId());
				if (attack != null)
				{
					attack.deleted = true;
					if (nd.getNode() != null)
					{
						graphData.getGraph().removeNode(nd.getNode());
					}
				}
			}
		});
		
		
	}
	
	private ImageIcon createImageIcon(String name, String desc)
	{
		try
		{
			Image img = ImageIO.read(getClass().getResource("/icon/" + name));
			
			BufferedImage bi = new BufferedImage(iconWidth, iconHeight, BufferedImage.TYPE_INT_ARGB);
			bi.getGraphics().drawImage(img, 0, 0, iconWidth, iconHeight, null, null);
			
			ImageIcon icon = new ImageIcon(bi);
			return icon;
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.err.println("Couldn't find file: " + name);
			return new ImageIcon();
		}
	}
	
	private JButton addButton(String text, String icon)
	{
		JButton button;
		if (icon != null && useIcons)
		{
			ImageIcon iconImage = createImageIcon(icon, "");
			button = new JButton(iconImage);
			button.setActionCommand(text);
			button.setToolTipText(text);
		}
		else
		{
			button = new JButton(text);
		}
		add(button);
		button.setFocusable(false);
		return button;
	}
	
	private JToggleButton addToggleButton(String text, String icon)
	{
		JToggleButton button;
		if (icon != null && useIcons)
		{
			ImageIcon iconImage = createImageIcon(icon, "");
			button = new JToggleButton(iconImage);
			button.setActionCommand(text);
			button.setToolTipText(text);
		}
		else
		{
			button = new JToggleButton(text);
		}
		add(button);
		button.setFocusable(false);
		return button;
	}
	
	private boolean isSelected(ActionEvent evt)
	{
		return ((JToggleButton) evt.getSource()).isSelected();
	}
}
