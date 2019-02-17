package edu.vcu.cyber.dashboard.cybok;

import edu.vcu.cyber.dashboard.SystemConfiguration;
import edu.vcu.cyber.dashboard.cybok.queries.AttackChainSearchQuery;
import edu.vcu.cyber.dashboard.cybok.queries.UpdateQuery;
import sun.rmi.log.LogOutputStream;

import java.io.*;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class CybokQueryHandler implements Runnable
{

	private static CybokQueryHandler queryHandler = new CybokQueryHandler();

	public static boolean useCybok = true;

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
		handler.installDir = SystemConfiguration.findCybokInstall();

//		String inputFile = new File("./data/topology.graphml").getAbsolutePath();
//
//		handler.addToQueue(new AttackChainSearchQuery(inputFile, "Primary Application Processor", query ->
//		{
//			if (!query.hadError())
//			{
//				System.out.println("Attack Chains: ");
//				for (String[] chains : query.getAttackChains())
//				{
//					for (String attack : chains)
//					{
//						System.out.print(attack + "\t>\t");
//					}
//					System.out.print("\n");
//				}
//			}
//		}));
		handler.addToQueue(new UpdateQuery());

	}

	public static void setupHandler()
	{
		queryHandler = new CybokQueryHandler();
//		queryHandler.installDir = findCybokInstall();
	}


	public CybokQueryHandler()
	{
		queue = new LinkedBlockingQueue<>();

		installDir = SystemConfiguration.findCybokInstall();
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
			command[0] = "python3";
			command[1] = "cybok";
			System.arraycopy(query.searchQuery(), 0, command, 2, query.searchQuery().length);

			query.onExecute();

//			process = Runtime.getRuntime().exec(command, new String[]{}, installDir);




			ProcessBuilder ps = new ProcessBuilder(command).directory(installDir);
//			ps.redirectOutput(new LogOutputStream());
			ps.redirectErrorStream(true);

			process = ps.start();
			setupReaders();

			try
			{
				process.waitFor();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void setupReaders()
	{
		if (process.isAlive())
		{
			System.out.println("setup Readers");
			InputStream inputStream = process.getInputStream();

			// Data Thread
//			Thread t = new Thread(() ->
//			{
				try
				{
					BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
					in.lines().forEach(line->
					{
						eventHandler.onMessage(line);
						System.out.println(line);
					});
//					String line;
//					while ((line = in.readLine()) != null)
//					{
//						eventHandler.onMessage(line);
//						System.out.println(line);
//					}

					in.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					eventHandler.onFinish(false);
					eventHandler.done();

					if (process.isAlive())
					{
						process.destroy();
					}
				}
				System.out.println("DAT Done.");

//			});
//			t.setPriority(Thread.MAX_PRIORITY);
//			t.setDaemon(true);
//			t.start();

//			InputStream errorStream = process.getErrorStream();
//			// Error Thread
//			new Thread(() ->
//			{
//				try
//				{
//					BufferedReader in = new BufferedReader(new InputStreamReader(errorStream));
//					String line;
//					boolean err = false;
//					while ((line = in.readLine()) != null)
//					{
////						if (!err)
////							eventHandler.onFinish(true);
////						err = true;
//						System.err.println("Error: " + line);
//					}
//				}
//				catch (Exception e)
//				{
//					e.printStackTrace();
//				}
//				finally
//				{
//					eventHandler.done();
//					if (process.isAlive())
//					{
//						process.destroy();
//					}
//				}
//				System.out.println("ERR Done.");
//
//			}).start();
		}
		else
		{
			System.out.println("??");
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

			System.out.println("\nexecute query");
			if (query.shouldRun())
			{

				executeQuery(query);

				while (!query.isDone())
				{
					try
					{
						Thread.sleep(10);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}

				if (process.isAlive())
				{
					process.destroyForcibly();
				}

				while (process.isAlive())
				{
					try
					{
						Thread.sleep(10);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				process = null;
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
