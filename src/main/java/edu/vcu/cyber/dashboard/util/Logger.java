package edu.vcu.cyber.dashboard.util;

public class Logger
{
	public void log(String format, Object... vals)
	{
		System.out.printf(format, vals);
	}

	public void log(String text)
	{
		System.out.print(text);
	}

	public void err(String format, Object... vals)
	{
		System.err.printf(format, vals);
	}

	public void err(String text)
	{
		System.err.println(text);
	}

	public void debug(String format, Object... vals)
	{
		System.out.printf(format, vals);
	}

	public void debug(String text)
	{
		System.out.print(text);
	}

}
