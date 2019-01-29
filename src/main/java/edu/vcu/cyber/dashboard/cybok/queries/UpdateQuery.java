package edu.vcu.cyber.dashboard.cybok.queries;

import edu.vcu.cyber.dashboard.Application;
import edu.vcu.cyber.dashboard.cybok.CybokQuery;

import javax.swing.*;
import java.awt.*;

public class UpdateQuery extends CybokQuery<UpdateQuery>
{

	public static JDialog updateInProgress;
	private static JLabel label;

	private String status;

	@Override
	public String[] searchQuery()
	{
		return new String[]{"--update"};
	}

	public void onExecute()
	{
		Application.getInstance().setStatusLabel("Updating Cybok...");
	}

	@Override
	public void onMessage(String line)
	{
		status = line;
		System.out.println(line);
		Application.getInstance().setStatusLabel(status);
		SwingUtilities.invokeLater(() -> label.setText(line));
	}

	@Override
	public void onFinish(boolean error)
	{
		super.onFinish(error);
		if (updateInProgress != null)
		{

			updateInProgress.dispose();

			updateInProgress = null;
		}
	}

	public static void showDialog(JFrame frame)
	{
//		JOptionPane optionPane = new JOptionPane();
//		JDialog dialog = optionPane.createDialog("Updating Cybok Databases...");
//
//		dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
//		updateInProgress = dialog;
//
//
//		JPanel panel = new JPanel(new BorderLayout());
//		panel.add(label = new JLabel("Updating Cybok Databases..."), BorderLayout.CENTER);
//		dialog.setContentPane(panel);
//
//		dialog.setVisible(true);
	}
}
