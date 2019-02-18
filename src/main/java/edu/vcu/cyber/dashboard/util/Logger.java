package edu.vcu.cyber.dashboard.util;

public class Logger
{
	public static void log(String format, Object... vals)
	{
		System.out.printf(format, vals);
	}

	public static void log(String text)
	{
		System.out.print(text);
	}

	public static void err(String format, Object... vals)
	{
		System.err.printf(format, vals);
	}

	public static void err(String text)
	{
		System.err.println(text);
	}

	public static void debug(String format, Object... vals)
	{
		System.out.printf(format, vals);
	}

	public static void debug(String text)
	{
		System.out.print(text);
	}

}
