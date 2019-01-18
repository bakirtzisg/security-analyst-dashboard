package edu.vcu.cyber.dashboard.util;

import edu.vcu.cyber.dashboard.av.AttackVector;
import edu.vcu.cyber.dashboard.av.AttackVectors;
import edu.vcu.cyber.dashboard.ui.BucketPanel;

import java.io.*;
import java.util.Collection;

public class BucketData
{

	public static void saveBukkitData()
	{
		File file = new File("./export/buket.csv");
		saveBukkitData(file);
	}

	public static void saveBukkitData(File file)
	{
		try
		{
			if (file.exists() || file.createNewFile())
			{
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false)));
				out.write("In Bucket, Database, ID, Description, URL, Contents, Violated Components\n");
				Collection<AttackVector> avList = AttackVectors.getAllAttackVectors();
				for (AttackVector av : avList)
				{
					if (av.inBucket)
					{
						String line = String.format("true,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
								av.type.name(), av.id, format(av.description), av.getURI(), format(av.contents), format(av.violatedComponents.toString()));
						out.write(line);
					}
				}

				out.flush();
				out.close();
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	public static void main(String[] args)
	{
		loadBukkitData(null);
	}

	public static void loadBukkitData(BucketPanel bucket)
	{
		File file = new File("./export/buket.csv");
		loadBukkitData(bucket, file);
	}

	public static void loadBukkitData(BucketPanel bucket, File file)
	{
		try
		{
			if (file.exists())
			{
				BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				in.readLine(); // ignore the header column

				AttackVectors.getAllAttackVectors().forEach(av -> av.inBucket = false);
				bucket.clear();

				String line;
				while ((line = in.readLine()) != null)
				{
					String[] data = parseLine(line, 3);
					if (data[0].equals("true"))
					{
						String name = data[1] + "-" + data[2];
						AttackVector av = AttackVectors.getAttackVector(name);
						if (av != null)
						{
							bucket.addRow(av);
						}
					}
				}


				in.close();
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private static String[] parseLine(String line, int expectedSize)
	{
		int pos = 0;
		String[] output = new String[expectedSize];
		char[] chars = line.toCharArray();
		StringBuilder sb = new StringBuilder();
		boolean inQuotes = false;
		try
		{
			for (int i = 0; i < chars.length && pos < expectedSize; i++)
			{
				if (sb == null)
					sb = new StringBuilder();

				char ch = chars[i];
				switch (ch)
				{
					case '\"':
						if (i < chars.length - 1)
						{
							switch (chars[i + 1])
							{
								case '\"':
									i++;
									sb.append('\"');
									break;
								case ',':
									inQuotes = false;
									break;
								default:
									inQuotes = true;

							}
						}
						break;

					case ',':
						if (!inQuotes)
						{
							output[pos] = sb.toString().trim();
							pos++;
							sb = null;
							break;
						}

					default:
						sb.append(ch);
				}

			}
			if (sb != null && pos < expectedSize)
				output[pos] = sb.toString().trim();
		} catch (Exception e)
		{
			e.printStackTrace();
			System.out.printf("%b, %d, %s,\t[%s]\n", inQuotes, pos, sb.toString(), line);
		}

		return output;
	}

	private static String format(String input)
	{
		return input.replace("\"", "\"\"");
	}

	private static String clean(String input)
	{
		if (input.startsWith("\"") && input.endsWith("\""))
			input = input.substring(1, input.length() - 2);
		return input.replace("\"\"", "\"");
	}

}
