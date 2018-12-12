package edu.vcu.cyber.dashboard;

import edu.vcu.cyber.dashboard.cybok.CybokQueryHandler;
import edu.vcu.cyber.dashboard.data.SystemAnalysis;
import edu.vcu.cyber.dashboard.util.EnvUtils;

import javax.swing.plaf.FontUIResource;
import java.awt.*;

public class MainClass
{

	/*

	Key bindings

	CTRL+S - save node positions of the selected graph to a file
	CTRL+L - load node positions of the selected graph from file
	CTRL+E - exports the selected graph as a .graphml file

	CTRL+F - freeze/unfreeze auto layout

	CTRL+G - (Attack Vector Graph)  grows the current selection (selects the nodes related to it)
	CTRL+B - (Attack Vector Graph) adds the selected nodes to the bucket
	CTRL+I - (Attack Vector Graph) opens a panel with additional information of the selected node
	CTRL+A - (Bucket) selects all visible nodes

	DELETE - deselects the current selection
	DELETE - (bucket) removes all checked entries from the bucket


	 */


	/**
	 * The main method for the entire application
	 */
	public static void main(String[] args)
	{

		EnvUtils.listLookAndFeels();
		EnvUtils.setLookAndFeel();
		EnvUtils.setGraphStreamRenderer();
		EnvUtils.registerFonts();
		EnvUtils.setGlobalUIFont(new FontUIResource("Fira Sans Light", Font.PLAIN, 14));

		CybokQueryHandler.setupHandler();

		if (!CybokQueryHandler.isCybokInstalled())
		{
			SystemAnalysis.makePathDefaults();
		}

		Application app = new Application();
		app.run();
	}
}
