package edu.vcu.cyber.dashboard.ui.custom;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class HintTextField extends JTextField implements KeyListener, CaretListener
{
	
	private static final Color HINT_COLOR = Color.lightGray;
	private static final Color DEFAULT_COLOR = Color.black;
	
	private String hint;
	
	public HintTextField(String hint)
	{
		super(hint);
		this.hint = hint;
		addCaretListener(this);
		addKeyListener(this);
		
		setForeground(HINT_COLOR);
		setSelectionStart(0);
		setSelectionEnd(0);
	}
	
	public String getHint()
	{
		return hint;
	}
	
	public void setHint(String hint)
	{
		this.hint = hint;
	}
	
	@Override
	public String getText()
	{
		if (super.getText().equals(hint))
		{
			return "";
		}
		else
		{
			return super.getText();
		}
	}
	
	@Override
	public void setText(String text)
	{
		if ("".equals(text))
			super.setText(hint);
		else
			super.setText(text);
	}
	
	@Override
	public void keyTyped(KeyEvent e)
	{
	
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		if (super.getText().equals(hint))
		{
			super.setText("");
			setForeground(DEFAULT_COLOR);
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e)
	{
		
		if (super.getText().equals(""))
		{
			setText(hint);
			setForeground(HINT_COLOR);
			setSelectionStart(0);
			setSelectionEnd(0);
		}
	}
	
	@Override
	public void caretUpdate(CaretEvent e)
	{
		if (super.getText().equals(hint))
		{
			if (getSelectionStart() != 0)
			{
				setSelectionStart(0);
				setSelectionEnd(0);
			}
		}
	}
	
}
