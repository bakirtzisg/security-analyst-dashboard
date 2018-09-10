package edu.vcu.cyber.dashboard.cybok2.info;


import jdk.nashorn.internal.parser.JSONParser;

public class AVQueryResponse
{

	String id;
	String name;
	float score;

	String[] related;

	public AVQueryResponse()
	{

	}

	public AVQueryResponse(String id, float score, String[] related)
	{
		this.id = id;
		this.score = score;
		this.related = related;
	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(id).append(" [score: ").append(score).append(", children: {");
		for (int i = 0; i < related.length; i++)
		{
			sb.append(related[i]);
			if (i < related.length - 1)
				sb.append(", ");
		}
		sb.append("}]");
		return sb.toString();
	}

	class ResultEntry
	{
		int qid;
		String query;

	}

	class Meh
	{

	}

	class AVEntry
	{
		String id;
		String name;
		String[] related;
	}

}
