package edu.vcu.cyber.dashboard.ui;

import edu.vcu.cyber.dashboard.av.AttackVectors;
import edu.vcu.cyber.dashboard.data.GraphData;
import edu.vcu.cyber.dashboard.project.AppSession;
import edu.vcu.cyber.dashboard.ui.custom.HintTextField;
import edu.vcu.cyber.dashboard.util.FilterPredicate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class FilterToolbar extends JPanel implements ActionListener, KeyListener
{

	private static final String[] filterMethods = {"All", "Attacks", "Description", "Component", "Contents", "Bucket"};

	private JTextField filterTextField;
	private JComboBox<String> filterMethod;
	private JButton filterButton;
	private JButton clearButton;

	private FilterTarget target;

	public interface FilterTarget
	{
		void onFilter(FilterPredicate filter);
	}

	public FilterToolbar()
	{
		initComponents();
	}

	public FilterToolbar(FilterTarget target)
	{
		this();
		this.target = target;
	}

	private void initComponents()
	{
		JPanel content = new JPanel();
		filterTextField = new HintTextField("Filter Attack Vectors...");
		filterTextField.setPreferredSize(new Dimension(300, 30));
		filterMethod = new JComboBox<>(filterMethods);
		filterButton = new JButton("Filter Graph");

		clearButton = new JButton("Clear");
		clearButton.addActionListener(this);

		filterMethod.addActionListener(this);

		filterButton.addActionListener(this);
		filterTextField.addKeyListener(this);

		setLayout(new BorderLayout());

		content.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(2, 2, 2, 2);
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.gridy = 0;

		c.gridx = 0;
		c.weightx = 1.0;
		content.add(filterTextField, c);

		c.gridx = 1;
		c.weightx = 0.1;
		c.anchor = GridBagConstraints.EAST;
		content.add(filterMethod, c);

		c.gridx = 2;
		content.add(filterButton, c);

		c.gridx = 3;
		content.add(clearButton, c);

		add(content, BorderLayout.CENTER);

	}

	private void filter()
	{
		try
		{
			int filterMode = filterMethod.getSelectedIndex();
			if (target != null)
			{
				target.onFilter(new FilterPredicate(filterTextField.getText(), FilterPredicate.FilterMode.values()[filterMode]));
			}
			else
			{

				if (filterMode == 5)
				{
					AttackVectors.showInGraph(av -> av.inBucket);
				}
				else
				{
//				Pattern pattern = Pattern.compile(filterTextField.getText().toLowerCase());

					FilterPredicate predicate = new FilterPredicate(filterTextField.getText(), FilterPredicate.FilterMode.values()[filterMode]);

					AttackVectors.showInGraph(predicate);
				}
			}

		}
		catch (Exception e)
		{

		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		switch (e.getActionCommand())
		{
			case "Filter Graph":
				System.out.println("Filter Graph: " + filterTextField.getText());

				filter();
				break;

			case "Clear":
				System.out.println("Clear Filter");
				filterTextField.setText("");
				AttackVectors.showInGraph(av -> true);

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
