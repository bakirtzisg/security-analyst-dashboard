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

	CTRL+S - save selected graph
	CTRL+L - load selected graph from file (positions)
	CTRL+E - exports the selected graph as a .graphml file

	CTRL+F - freeze/unfreeze auto layout

	CTRL+G - grows the current selection
	CTRL+B - (Attack Vector Graph) adds the selected nodes to the bucket
	CTRL+A - (Bucket) selects all visible nodes

	DELETE - deletes the current selection


	 */


	/**
	 * The main method for the entire application
	 */
	public static void main(String[] args)
	{

		EnvUtils.setLookAndFeel();
		EnvUtils.setGraphStreamRenderer();
		EnvUtils.registerFonts();
		EnvUtils.setGlobalUIFont(new FontUIResource("Fira Sans Light", Font.PLAIN, 14));

		if (!CybokQueryHandler.isCybokInstalled())
		{
			SystemAnalysis.makePathDefaults();
		}

//		BucketPanel.test();
		Application app = new Application();
		app.run();
//		SystemAnalysis.doAnalysis();

//		NodeEditorDialog nd = new NodeEditorDialog(null);
//		nd.setVisible(true);
//		nd.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
}
