package edu.vcu.cyber.dashboard.util;

import edu.vcu.cyber.dashboard.av.AttackVector;
import edu.vcu.cyber.dashboard.av.AttackVectors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class CSVParser
{
	public static void readCSV(File file)
	{
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line = in.readLine(); // reads the header, but isn't needed
			while ((line = in.readLine()) != null)
			{
				try
				{
					ParserData info = new ParserData();
					info.parse(line);

					//resolve full related names since it's not consistent in the existing .csv files
					for (int i = 0; i < info.related_cwe.length; i++)
					{
						if (!info.related_cwe[i].contains("CWE-"))
						{
							info.related_cwe[i] = "CWE-" + info.related_cwe[i];
						}
					}
					for (int i = 0; i < info.related_cve.length; i++)
					{
						if (!info.related_cve[i].contains("CVE-"))
						{
							info.related_cve[i] = "CVE-" + info.related_cve[i];
						}
					}
					for (int i = 0; i < info.related_capec.length; i++)
					{
						if (!info.related_capec[i].contains("CAPEC-"))
						{
							info.related_capec[i] = "CAPEC-" + info.related_capec[i];
						}
					}


				}
				catch (Exception e)
				{
//					e.printStackTrace();
				}
			}
			in.close();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
		}
	}


	public static class ParserData
	{
		String violated_component;
		int hits_for_component;
		String attack_vector;
		String database;
		String id;
		String[] related_cwe;
		String[] related_capec;
		String[] related_cve;
		String contents;

		int pos;

		String[] parseArray(String[] input)
		{
			String field = readField(input);
			field = field.replaceAll("[\\[\\]'|\"\\s]", "");
			if (field.equals("N/A"))
			{
				return new String[]{};
			}
			return field.split(",");
		}

		private String readField(String[] input)
		{
			if (input[pos].startsWith("\""))
			{
				StringBuilder sb = new StringBuilder();
				for (; pos < input.length; pos++)
				{
					sb.append(input[pos]);
					if (!input[pos].endsWith("\""))
					{
						sb.append(",");
					}
					else
					{
						break;
					}
				}
				pos++;
				String ret = sb.toString().trim();
				ret = ret.replace("\"\"", "\"");
				if (ret.startsWith("\""))
					ret = ret.substring(1);
				if (ret.endsWith("\""))
					ret = ret.substring(0, ret.length() - 2);
				return ret;
			}
			return input[pos++];
		}

		void parse(String line)
		{
			pos = 0;
			String[] data = line.split(",");


			violated_component = readField(data);
			try
			{
				hits_for_component = Integer.valueOf(readField(data));
			}
			catch (Exception e)
			{
				hits_for_component = 0;
			}
			attack_vector = readField(data);
			database = readField(data);
			id = readField(data);

			AttackVector attackVector = new AttackVector(database, id);
			attackVector.description = attack_vector;
			related_cwe = attackVector.related_cwe = parseArray(data);
			related_capec = attackVector.related_capec = parseArray(data);
			related_cve = attackVector.related_cve = parseArray(data);
			contents = attackVector.contents = readField(data);

			AttackVectors.addAttack(attackVector, violated_component);


		}

		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(violated_component).append(" :: ")
					.append(attack_vector).append(" :: ")
					.append(database).append("-").append(id).append(" :: ");
			sb.append("\n\tcwe[");
			if (related_cwe != null)
			{
				for (String s : related_cwe)
				{
					sb.append(s).append(", ");
				}
			}
			sb.append("],\n\tcapec[");
			if (related_capec != null)
			{
				for (String s : related_capec)
				{
					sb.append(s).append(", ");
				}
			}
			sb.append("],\n\tcve[");
			if (related_cve != null)
			{
				for (String s : related_cve)
				{
					sb.append(s).append(", ");
				}
			}
			sb.append("], ").append("\n\t\tContents: ").append(contents);

			return sb.toString();
		}
	}

}
