package edu.vcu.cyber.dashboard;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class SystemConfiguration
{
	private static String PYTHON_VERSION;


	public static File CYBOK_INSTALL_DIR;

	public static boolean verifyDependancies()
	{
		if (!checkPythonVersion())
		{
			JOptionPane.showMessageDialog(null, "Invalid python version!\n" + PYTHON_VERSION);
			return false;
		}

		if ((CYBOK_INSTALL_DIR = findCybokInstall()) == null)
		{
			JOptionPane.showMessageDialog(null, "Couldn't find Cybok install!");
			return false;
		}

		if (!isCybokIndexUpdated())
		{

			int result = JOptionPane.showConfirmDialog(null, "Cybok isn't updated!\n Would you like to update? \n (This could take a while)", "Update Cybok?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

			if (result == JOptionPane.YES_OPTION)
			{
				if (!updateCybok())
					return false;
			}
			else
			{
				return false;
			}
		}
		return true;
	}

	private static boolean pythonVersion(String line)
	{
		PYTHON_VERSION = line;

		String args[] = line.split("\\.");
		if (args.length > 2)
		{
			int major = Integer.parseInt(args[0]);
			int minor = Integer.parseInt(args[1]);
			int vers = major * 10 + minor;
			return vers >= 36;
		}
		return line.contains("3.6");

	}


	private static boolean checkPythonVersion()
	{

		ProcessBuilder pb = new ProcessBuilder("python3", "--version");
		pb.redirectErrorStream(true);

		try
		{
			Process process = pb.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			boolean versionCheck = pythonVersion(reader.readLine().replace("Python ", ""));

			reader.close();

			return versionCheck;

		}
		catch (IOException e)
		{
			PYTHON_VERSION = "No python install found";
			e.printStackTrace();
		}


		return false;
	}


	public static File findCybokInstall()
	{
		if (CYBOK_INSTALL_DIR != null)
			return CYBOK_INSTALL_DIR;

		File file;

		String env = System.getProperty("CYBOK_INSTALL");
		if (env != null)
		{
			file = new File(env);
			if (dirContainsCybok(file))
				return file;
		}
		else
		{
			String[] possibleLocations = {"./cybok", "./cybok-cli", "../cybok", "../cybok-cli"};
			for (String f : possibleLocations)
			{
				file = new File(f);
				if (dirContainsCybok(file))
					return file;
			}
		}


		return null;
	}

	public static boolean dirContainsCybok(File dir)
	{
		if (dir != null && dir.exists())
		{
			String list[] = dir.list();
			if (list != null)
			{
				for (String f : list)
				{
					if (f.equals("cybok"))
					{
						if (new File(dir, f + "/__main__.py").exists())
						{
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public static boolean isCybokIndexUpdated()
	{

		File file = new File(CYBOK_INSTALL_DIR, "indexdir");
		if (file.exists())
		{
			return file.list().length > 0;
		}
		return false;
	}

	private static boolean wasUpdatedSuccessfully = false;

	public static boolean updateCybok()
	{

		JOptionPane optionPane = new JOptionPane();
		final JLabel label = new JLabel("Updating Cybok Databases...");
		label.setVerticalAlignment(SwingConstants.CENTER);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		final JLabel disclaimer = new JLabel("This dialog will close when finished");

		disclaimer.setVerticalAlignment(SwingConstants.CENTER);
		disclaimer.setHorizontalAlignment(SwingConstants.CENTER);
		JDialog dialog = optionPane.createDialog("Updating Cybok Databases...");

		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		ProcessBuilder pb = new ProcessBuilder("python3", "-u", "cybok", "--update");
		pb.directory(CYBOK_INSTALL_DIR);
		pb.redirectErrorStream(true);

		try
		{
			final Process process;
			process = pb.start();


			new Thread(() ->
			{
				try
				{

					BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

					reader.lines().filter(line -> !line.equals("")).forEach(line ->
							SwingUtilities.invokeLater(() -> label.setText(line)));

					reader.close();

					dialog.dispose();

					wasUpdatedSuccessfully = true;
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}

			}).start();


			JPanel panel = new JPanel(new BorderLayout());
			panel.add(label, BorderLayout.CENTER);
			panel.add(disclaimer, BorderLayout.SOUTH);
			dialog.setContentPane(panel);
			dialog.setSize(360, 150);

			dialog.setVisible(true);

			process.destroyForcibly();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return wasUpdatedSuccessfully;
	}


}
