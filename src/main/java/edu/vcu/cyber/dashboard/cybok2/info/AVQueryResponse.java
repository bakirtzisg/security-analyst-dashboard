package edu.vcu.cyber.dashboard.cybok2.info;


import java.util.List;

public class AVQueryResponse implements Result
{

	private ResultEntry[] RESULTS;

	public AVQueryResponse()
	{

	}

	public void print()
	{
		for (ResultEntry entry : RESULTS)
		{
			entry.print();
		}
		RESULTS[0].print();
	}

	public ResultEntry[] getResults()
	{
		return RESULTS;
	}

	public static class ResultEntry implements Result
	{
		private int QID;
		private String QUERY;
		private ResultData RES;

		public int getQueryID()
		{
			return QID;
		}

		public String getQuery()
		{
			return QUERY;
		}

		public ResultData getResult()
		{
			return RES;
		}

		public ResultEntry()
		{
			RES = new ResultData();
		}

		public void print()
		{
			System.out.printf("qid: %d, query: %s, res:{\n", QID, QUERY);
//			for (ResultData dat : RES)
//			{
			RES.print();
//			}
			System.out.println("}");
		}
	}

	public static class ResultData implements Result
	{
		private List<AVEntry> CAPEC;
		private List<AVEntry> CWE;
		private List<AVEntry> CVE;

		public List<AVEntry> getCapec()
		{
			return CAPEC;
		}

		public List<AVEntry> getCwe()
		{
			return CWE;
		}

		public List<AVEntry> getCVE()
		{
			return CVE;
		}

		public void print()
		{
			System.out.print("\tcapec: {");
			for (AVEntry entry : CAPEC)
			{
				entry.print();
			}
			System.out.print("\n\t}\n\tcwe: {");
			for (AVEntry entry : CWE)
			{
				entry.print();
			}
			System.out.print("\n\t}\n\tcve: {");
			for (AVEntry entry : CVE)
			{
				entry.print();
			}
			System.out.println("\n\t}");
		}
	}

	public static class AVEntry implements Result
	{
		private String ID;
		private String NAME;
		private double SCORE;
		private String[] REL;

		public String getID()
		{
			return ID;
		}

		public String getName()
		{
			return NAME;
		}

		public double getScore()
		{
			return SCORE;
		}

		public String[] getRelated()
		{
			return REL;
		}

		public void print()
		{
			System.out.printf("\n\t\t%s: [name: %s, score: %2.2f, rel:{", ID, NAME, SCORE);
			if (REL != null)
				for (String dat : REL)
				{
					System.out.printf("%s, ", dat);
				}
			System.out.print("}]");
		}
	}

}
