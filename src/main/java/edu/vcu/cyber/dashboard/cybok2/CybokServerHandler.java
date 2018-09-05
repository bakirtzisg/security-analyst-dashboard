package edu.vcu.cyber.dashboard.cybok2;

import jdk.nashorn.internal.parser.JSONParser;

import java.io.*;
import java.net.Socket;

public class CybokServerHandler implements Runnable
{

	private Socket socket;
	private InputStream input;
	private OutputStream output;

	public CybokServerHandler()
	{

	}

	public void open() throws IOException
	{
		socket = new Socket("localhost", 1116);

		input = socket.getInputStream();
		output = socket.getOutputStream();
	}

	@Override
	public void run()
	{
		try
		{
			JSONParser parser;

			BufferedReader in = new BufferedReader(new InputStreamReader(input));
			String line;
			while ((line = in.readLine()) != null)
			{
//				parser = new JSONParser(line);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
