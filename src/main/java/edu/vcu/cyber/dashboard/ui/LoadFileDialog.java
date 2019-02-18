package edu.vcu.cyber.dashboard.ui;

import edu.vcu.cyber.dashboard.Config;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

public class LoadFileDialog extends JPanel implements ActionListener
{

	private JPanel content;

	private JLabel topLabel;
	private JLabel specLabel;

	private JTextField topFileTF;
	private JTextField specFileTF;

	private JButton browseTopBtn;
	private JButton browseSpecBtn;

	private JCheckBox doAnalysisCheck;

	public int ask(Frame parent)
	{

		final JOptionPane optionPane = new JOptionPane(this, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
		final JDialog dialog = new JDialog(parent, "Load Graph Files", true);

		dialog.setContentPane(optionPane);

		optionPane.setValue(-1);
		optionPane.addPropertyChangeListener(evt ->
		{
			String prop = evt.getPropertyName();
			if (dialog.isVisible() && evt.getSource() == optionPane && prop.equals(JOptionPane.VALUE_PROPERTY))
			{
				if ((int) evt.getNewValue() == 0 && !getTopologyGraphFile().exists())
				{
					JOptionPane.showMessageDialog(this, "Topology graph doesn't exist!");
					optionPane.setValue(-1);
				}
				else if ((int) evt.getNewValue() >= 0)
				{
					dialog.setVisible(false);
				}
			}
		});

		dialog.pack();
		dialog.setVisible(true);

		return (int) optionPane.getValue();
	}

	public File getTopologyGraphFile()
	{
		return new File(topFileTF.getText());
	}

	public File getSpecificationGraphFile()
	{
		return new File(specFileTF.getText());
	}

	public boolean hasSpecGraphFile()
	{
		return getSpecificationGraphFile().exists();
	}

	public boolean doAnalysis()
	{
		return doAnalysisCheck.isSelected();
	}


	public static void main(String[] args)
	{
		JFrame frame = new JFrame("File Selection");

		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(500, 300));
		frame.setContentPane(panel);

		LoadFileDialog lfd = new LoadFileDialog();

		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);

		lfd.ask(frame);
	}

	public LoadFileDialog()
	{
		createComponent();
	}

	@SuppressWarnings("Duplicates")
	private void initComponents()
	{
		int compHeight = 25;
		Dimension browseSize = new Dimension(25, compHeight);
		Dimension fileSize = new Dimension(400, compHeight);
		Dimension opButtonSize = new Dimension(85, compHeight);

		topLabel = new JLabel("Topology Graph:");
		topFileTF = new JTextField();
		topFileTF.setPreferredSize(fileSize);

		if (Config.LAST_TOPOLOGY_FILE != null)
			topFileTF.setText(Config.LAST_TOPOLOGY_FILE.toString());

		browseTopBtn = new JButton("...");
		browseTopBtn.addActionListener(this);
		browseTopBtn.setActionCommand("BrowseTop");
		browseTopBtn.setPreferredSize(browseSize);


		specLabel = new JLabel("Specifications Graph (optional):");
		specFileTF = new JTextField();
		specFileTF.setPreferredSize(fileSize);

		if (Config.LAST_TOPOLOGY_FILE != null)
			specFileTF.setText(Config.LAST_SPEC_FILE.toString());

		browseSpecBtn = new JButton("...");
		browseSpecBtn.addActionListener(this);
		browseSpecBtn.setActionCommand("BrowseSpec");
		browseSpecBtn.setPreferredSize(browseSize);

		doAnalysisCheck = new JCheckBox("Perform analysis after opening");
		doAnalysisCheck.setSelected(Config.LAST_DO_ANALYSIS);

	}

	public JComponent createComponent()
	{
		initComponents();

		content = new JPanel(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(2, 2, 2, 2);

		c.anchor = GridBagConstraints.WEST;
		addComp(content, topLabel, 0, 0, 2, c);
		addComp(content, topFileTF, 0, 1, 1, c);
		c.anchor = GridBagConstraints.EAST;
		addComp(content, browseTopBtn, 1, 1, 1, c);


		c.anchor = GridBagConstraints.WEST;
		addComp(content, specLabel, 0, 2, 2, c);
		addComp(content, specFileTF, 0, 3, 1, c);
		c.anchor = GridBagConstraints.EAST;
		addComp(content, browseSpecBtn, 1, 3, 1, c);

		c.insets = new Insets(15, 5, 10, 5);
		c.anchor = GridBagConstraints.WEST;
		addComp(content, doAnalysisCheck, 0, 4, 1, c);


		add(content);

		return content;
	}

	private void addComp(JPanel panel, Component comp, int x, int y, int w, GridBagConstraints c)
	{
		c.gridx = x;
		c.gridy = y;
		c.gridwidth = w;
		panel.add(comp, c);
	}

	private File askFile(String title)
	{
		JFileChooser fc = new JFileChooser(Config.LAST_FILE_LOAD_DIRECTORY != null ? Config.LAST_FILE_LOAD_DIRECTORY : new File("./"));
		fc.setDialogTitle(title);
		fc.setFileFilter(new FileNameExtensionFilter("GraphML", "graphml"));

		int ret = fc.showOpenDialog(content);
		if (ret == JFileChooser.APPROVE_OPTION)
		{
			Config.LAST_FILE_LOAD_DIRECTORY = fc.getCurrentDirectory();
			return fc.getSelectedFile();
		}
		return null;
	}

	@Override
	public void actionPerformed(ActionEvent evt)
	{
		switch (evt.getActionCommand())
		{
			case "Open":
				File topFile = new File(topFileTF.getText());
				File specFile = new File(specFileTF.getText());
				if (!topFile.exists())
					JOptionPane.showMessageDialog(content, "Topology File\n" + topFileTF.getText() + "\n doesn't exist!");
				else if (!specFileTF.getText().equals("") && !specFile.exists())
					JOptionPane.showMessageDialog(content, "Specification File\n" + specFileTF.getText() + "\n doesn't exist!");


				break;


			case "Cancel":

//				dispose();

				break;


			case "BrowseTop":
			{
				File f = askFile("Select Topology Graph File");
				if (f != null)
				{
					topFileTF.setText(f.getAbsolutePath());
				}
			}

			break;


			case "BrowseSpec":
			{
				File f = askFile("Select Specifications Graph File");
				if (f != null)
				{
					specFileTF.setText(f.getAbsolutePath());
				}
			}
			break;
		}
	}
}
