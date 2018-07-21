package edu.vcu.cyber.dashboard.cybok;

import edu.vcu.cyber.dashboard.cybok.queries.AttackChainSearchQuery;

import java.io.*;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class CybokQueryHandler implements Runnable
{

	private static CybokQueryHandler queryHandler = new CybokQueryHandler();

	public static void sendQuery(CybokQuery<?> query)
	{
		queryHandler.addToQueue(query);
	}

	public static boolean isCybokInstalled()
	{
		return queryHandler.installDir != null;
	}

	public static void main(String[] args)
	{
		CybokQueryHandler handler = new CybokQueryHandler();
		handler.installDir = findCybokInstall();

		String inputFile = new File("./data/topology.graphml").getAbsolutePath();

		handler.addToQueue(new AttackChainSearchQuery(inputFile, "Primary Application Processor", query ->
		{
			if (!query.hadError())
			{
				System.out.println("Attack Chains: ");
				for (String[] chains : query.getAttackChains())
				{
					for (String attack : chains)
					{
						System.out.print(attack + "\t>\t");
					}
					System.out.print("\n");
				}
			}
		}));

	}

	public static void setupHandler()
	{
		CybokQueryHandler handler = new CybokQueryHandler();
		handler.installDir = findCybokInstall();
	}

	private static File findCybokInstall()
	{

//		File file;
//
//		String env = System.getProperty("CYBOK_INSTALL");
//		if (env != null)
//		{
//			file = new File(env);
//			if (dirContainsCybok(file))
//				return file;
//		}
//		else
//		{
//			file = new File("../cybok");
//			if (dirContainsCybok(file))
//				return file;
//		}

		return null;
	}

	private static boolean dirContainsCybok(File dir)
	{
		if (dir != null)
		{
			String list[] = dir.list();
			if (list != null)
			{
				for (String f : list)
				{
					if (f.equals("cybok"))
					{
						if (new File(dir, f + "/__main__.py").exists())
						{
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public CybokQueryHandler()
	{
		queue = new LinkedBlockingQueue<>();

		installDir = findCybokInstall();
		if (installDir != null)
		{
			queueThread = new Thread(this);
		}
	}

	private File installDir;
	private final Queue<CybokQuery> queue;

	private CybokQuery eventHandler;
	private Process process;

	private boolean isRunning;

	private Thread queueThread;

	private void executeQuery(CybokQuery query)
	{
		this.eventHandler = query;
		try
		{
			String[] command = new String[2 + query.searchQuery().length];
			command[0] = "python3.6";
			command[1] = "cybok";
			System.arraycopy(query.searchQuery(), 0, command, 2, query.searchQuery().length);

			query.onExecute();

			process = Runtime.getRuntime().exec(command, new String[]{}, installDir);

			setupReaders();

		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void setupReaders()
	{
		if (process.isAlive())
		{
			InputStream inputStream = process.getInputStream();
			InputStream errorStream = process.getErrorStream();

			// Data Thread
			new Thread(() ->
			{
				try
				{
					BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
					String line;
					while ((line = in.readLine()) != null)
					{
						eventHandler.onMessage(line);
					}
				} catch (Exception e)
				{
					e.printStackTrace();
				} finally
				{
					eventHandler.onFinish(false);
					eventHandler.done();

					if (process.isAlive())
					{
						process.destroy();
					}
				}

			}).start();

			// Error Thread
			new Thread(() ->
			{
				try
				{
					BufferedReader in = new BufferedReader(new InputStreamReader(errorStream));
					String line;
					boolean err = false;
					while ((line = in.readLine()) != null)
					{
//						if (!err)
//							eventHandler.onFinish(true);
//						err = true;
						System.err.println("Error: " + line);
					}
				} catch (Exception e)
				{
					e.printStackTrace();
				} finally
				{
					eventHandler.done();
					if (process.isAlive())
					{
						process.destroy();
					}
				}

			}).start();
		}
	}

	public void addToQueue(CybokQuery query)
	{
		if (installDir != null)
		{
			synchronized (queue)
			{
				queue.add(query);

				if (!isRunning)
				{
					isRunning = true;
					queueThread = new Thread(this);
					queueThread.start();
				}
			}
		}
	}

	@Override
	public void run()
	{
		CybokQuery query;

		synchronized (queue)
		{
			query = queue.poll();
		}

		while (query != null)
		{

			if (query.shouldRun())
			{

				executeQuery(query);

				while (!query.isDone())
				{
					try
					{
						Thread.sleep(10);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}

				if (process.isAlive())
				{
					process.destroyForcibly();
				}
			}

			synchronized (queue)
			{
				if (!queue.isEmpty())
				{
					query = queue.poll();
				}
				else
				{
					query = null;
				}
			}
		}

		isRunning = false;

	}


}
