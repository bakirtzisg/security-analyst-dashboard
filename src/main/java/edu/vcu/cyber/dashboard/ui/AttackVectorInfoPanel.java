package edu.vcu.cyber.dashboard.ui;

import edu.vcu.cyber.dashboard.data.AttackVector;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class AttackVectorInfoPanel extends JPanel
{

	private static AttackVectorInfoPanel instance = new AttackVectorInfoPanel();

	public static void display(AttackVector attackVector)
	{

		instance.setAttack(attackVector);
		instance.frame.setVisible(true);
	}

	private JFrame frame;
	private JTextPane textPane;
	JEditorPane editorPane;
	private JScrollPane sp;


	public AttackVectorInfoPanel()
	{
		setLayout(new BorderLayout());

		editorPane = new JEditorPane();


//		textPane = new JTextPane();
		editorPane.setPreferredSize(new Dimension(750, 500));
//		textPane.setContentType("text/html");
//		textPane.setEditable(false);
//
//		sp = new JScrollPane(textPane);
		sp = new JScrollPane(editorPane);
		add(sp, BorderLayout.CENTER);

		frame = new JFrame("Node Info");
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.add(this);
		frame.pack();

		frame.setVisible(true);
	}

	public void setAttack(AttackVector av)
	{
		frame.setTitle(av.qualifiedName);

//		String sb = "<html><h1>" + av.qualifiedName + "</h1>\n" +
//				"<h3>" + av.description + "</h3>\n" +
//				"<a href=\"" + av.getURI() + "\">" + av.getURI() + "</a>" +
//				"<p><h4> Violated Components </h4>" + av.violatedComponents.toString() + "</p>" +
//				"<p><h4> Contents </h4>" + av.contents + "</p></html>";
//
//		textPane.setText(sb);
		try
		{
			editorPane.setPage(new URL(av.getURI()));
		} catch (IOException e)
		{
			e.printStackTrace();
		}


		SwingUtilities.invokeLater(() -> sp.getVerticalScrollBar().setValue(0));

	}

	private void filterEditorPane()
	{
		editorPane.getText();
	}

}
