package edu.vcu.cyber.dashboard.cybok2;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import edu.vcu.cyber.dashboard.cybok2.info.AVQueryResponse;
import jdk.nashorn.internal.objects.Global;
import jdk.nashorn.internal.parser.JSONParser;
import jdk.nashorn.internal.runtime.Context;
import scala.util.parsing.json.JSONObject;

import java.io.*;
import java.net.Socket;

public class CybokServerHandler implements Runnable
{

	public static void main(String[] args)
	{
		try
		{

			Gson gson = new Gson();
//			AVQueryResponse res = new AVQueryResponse("testID", 0.5f, new String[]{"a", "b", "c"});
//
//
//			System.out.println(res);
//			String json = gson.toJson(res);
//			System.out.println(json);
//			res = new AVQueryResponse();
//			res = gson.fromJson(json, AVQueryResponse.class);
//			System.out.println(res);
//
//			JsonElement ele = new JsonObject();

			AVQueryResponse res = new AVQueryResponse();

///
			BufferedReader in = new BufferedReader(new FileReader(new File("./src/main/resources/test.json")));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = in.readLine()) != null)
			{
				sb.append(line);
			}

			System.out.println(sb.toString());
			res = gson.fromJson(sb.toString(), AVQueryResponse.class);



			res.print();
		} catch (Exception e)
		{

			e.printStackTrace();
		}
	}

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

			BufferedReader in = new BufferedReader(new InputStreamReader(input));
			String line;
			while ((line = in.readLine()) != null)
			{
				JSONParser parser = new JSONParser(line, Global.instance(), false);
				JSONObject ret = (JSONObject) parser.parse();
				System.out.println(ret);

			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
