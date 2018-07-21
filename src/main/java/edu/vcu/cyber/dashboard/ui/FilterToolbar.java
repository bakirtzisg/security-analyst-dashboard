package edu.vcu.cyber.dashboard.ui;

import edu.vcu.cyber.dashboard.data.AttackVectors;
import edu.vcu.cyber.dashboard.data.GraphData;
import edu.vcu.cyber.dashboard.data.GraphType;
import edu.vcu.cyber.dashboard.project.AppSession;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilterToolbar extends JToolBar implements ActionListener, KeyListener
{

	private static final String[] filterMethods = {"All", "Attacks", "Description", "Component", "Contents", "Bucket"};

	private JTextField filterTextField;
	private JComboBox<String> filterMethod;
	private JButton filterButton;

	public FilterToolbar()
	{
		initComponents();
	}

	private void initComponents()
	{
		filterTextField = new JTextField();
		filterTextField.setMinimumSize(new Dimension(300, 30));
		filterMethod = new JComboBox<>(filterMethods);
		filterButton = new JButton("Filter Graph");

		filterMethod.addActionListener(this);

		filterButton.addActionListener(this);
		filterTextField.addKeyListener(this);

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(2, 2, 2, 2);
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.gridy = 0;

		c.gridx = 0;
		c.weightx = 1.0;
		add(filterTextField, c);

		c.gridx = 1;
		c.weightx = 0;
		add(filterMethod, c);

		c.gridx = 2;
		add(filterButton, c);

	}

	private void filter()
	{
		try
		{
			GraphData graph = AppSession.getInstance().getAvGraph();

			int filterMode = filterMethod.getSelectedIndex();

			if (filterMode == 5)
			{
				AttackVectors.showInGraph(graph, av -> av.inBucket);
			}
			else
			{
				Pattern pattern = Pattern.compile(filterTextField.getText().toLowerCase());
				AttackVectors.showInGraph(graph, av ->
				{
					String searchString = "";

					StringBuilder sb = new StringBuilder();
					switch (filterMode)
					{
						case 0: // all
							searchString = String.format("attack[ %s ]; description[ %s ]; components[ %s ]; contents[ %s ]; modes[ %sFS ];",
									av.qualifiedName, av.description,
									av.violatedComponents.toString(), av.contents, av.inBucket ? "bucket" : "");
							break;
						case 1: // attack
							searchString = String.format("attack[ %s ];", av.qualifiedName);
							break;
						case 2: // description
							searchString = String.format("description[ %s ];", av.description);
							break;
						case 3: // component
							searchString = String.format("components[ %s ];", av.violatedComponents.toString());
							break;
						case 4: // contents
							searchString = String.format("contents[ %s ];", av.contents);
							break;
					}
					searchString = searchString.toLowerCase();

					Matcher matcher = pattern.matcher(searchString);
					return matcher.find();

				});
			}

		} catch (Exception e)
		{

		}
	}


	@Override
	public void actionPerformed(ActionEvent e)
	{
		switch (e.getActionCommand())
		{
			case "Filter Graph":
				filter();
				break;
			case "comboBoxChanged":
				filter();

				break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
	}

	@Override
	public void keyPressed(KeyEvent e)
	{

		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			filter();
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{

	}
}
