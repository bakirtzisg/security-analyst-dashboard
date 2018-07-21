package edu.vcu.cyber.dashboard.data;

import edu.vcu.cyber.dashboard.cybok.CybokQuery;
import edu.vcu.cyber.dashboard.cybok.CybokQueryHandler;
import edu.vcu.cyber.dashboard.cybok.queries.AttackChainSearchQuery;
import edu.vcu.cyber.dashboard.cybok.queries.FullAnalysisQuery;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class SystemAnalysis
{


	private static final Map<String, List<String[]>> attackPaths = new HashMap<>();
	private static final Map<String, List<String>> attackVectors = new HashMap<>();


	public static void getAttackChains(String target, AttackChainResult result)
	{
		List<String[]> paths = attackPaths.computeIfAbsent(target, key -> new ArrayList<>());
		if (paths.isEmpty())
		{
			String inputFile = new File("./data/topology.graphml").getAbsolutePath();
			AttackChainSearchQuery query = new AttackChainSearchQuery(inputFile, target, res ->
			{
				List<String[]> attackChains = res.getAttackChains();
				if (attackChains.isEmpty())
				{
					attackChains.add(new String[]{target});
				}
				paths.addAll(attackChains);
				result.onReceive(attackChains);
			});
			CybokQueryHandler.sendQuery(query);


		}
		else
		{
			result.onReceive(paths);
		}

	}

	public interface AttackChainResult
	{
		void onReceive(List<String[]> attackChains);
	}


	public static void doAnalysis()
	{
		String inputFile = new File("data/topology.graphml").getAbsolutePath();
		CybokQueryHandler.sendQuery(new FullAnalysisQuery(inputFile));

	}

	public static void makePathDefaults()
	{
		List<String[]> paths = attackPaths.computeIfAbsent("Primary Application Processor", key -> new ArrayList<>());
		paths.add(new String[]{"ZigBee", "FCS Radio Module", "Primary Application Processor"});
		paths.add(new String[]{"GPS", "NMEA GPS", "Primary Application Processor"});
		paths.add(new String[]{"Wi-Fi", "Laptop", "GCS Radio Module", "FCS Radio Module", "Primary Application Processor"});
		paths.add(new String[]{"ZigBee", "GCS Radio Module", "FCS Radio Module", "Primary Application Processor"});
		paths.add(new String[]{"ZigBee", "Imagery Radio Module", "GCS Radio Module", "FCS Radio Module", "Primary Application Processor"});

		paths = attackPaths.computeIfAbsent("Imagery Radio Module", key -> new ArrayList<>());
		paths.add(new String[]{"ZigBee", "GCS Radio Module", "Imagery Radio Module", "Imagery Application Processor"});
		paths.add(new String[]{"ZigBee", "FCS Radio Module", "GCS Radio Module", "Imagery Radio Module", "Imagery Application Processor"});
		paths.add(new String[]{"ZigBee", "Imagery Radio Module", "Imagery Application Processor"});
		paths.add(new String[]{"Wi-Fi", "Laptop", "GCS Radio Module", "Imagery Radio Module", "Imagery Application Processor"});
		paths.add(new String[]{"GPS", "NMEA GPS", "Primary Application Processor", "FCS Radio Module", "GCS Radio Module", "Imagery Radio Module", "Imagery Application Processor"});

		paths = attackPaths.computeIfAbsent("GCS Radio Module", key -> new ArrayList<>());
		paths.add(new String[]{"GPS", "NMEA GPS", "Primary Application Processor", "FCS Radio Module", "GCS Radio Module"});
		paths.add(new String[]{"ZigBee", "FCS Radio Module", "GCS Radio Module"});
		paths.add(new String[]{"ZigBee", "Imagery Radio Module", "GCS Radio Module"});
		paths.add(new String[]{"Wi-Fi", "Laptop", "GCS Radio Module"});
		paths.add(new String[]{"ZigBee", "GCS Radio Module"});

		paths = attackPaths.computeIfAbsent("Laptop", key -> new ArrayList<>());
		paths.add(new String[]{"ZigBee", "GCS Radio Module", "Laptop"});
		paths.add(new String[]{"ZigBee", "FCS Radio Module", "GCS Radio Module", "Laptop"});
		paths.add(new String[]{"GPS", "NMEA GPS", "Primary Application Processor", "FCS Radio Module", "GCS Radio Module", "Laptop"});
		paths.add(new String[]{"Wi-Fi", "Laptop"});
		paths.add(new String[]{"ZigBee", "Imagery Radio Module", "GCS Radio Module", "Laptop"});

		paths = attackPaths.computeIfAbsent("FCS Radio Module", key -> new ArrayList<>());
		paths.add(new String[]{"Wi-Fi", "Laptop", "GCS Radio Module", "FCS Radio Module"});
		paths.add(new String[]{"GPS", "NMEA GPS", "Primary Application Processor", "FCS Radio Module"});
		paths.add(new String[]{"ZigBee", "GCS Radio Module", "FCS Radio Module"});
		paths.add(new String[]{"ZigBee", "Imagery Radio Module", "GCS Radio Module", "FCS Radio Module"});
		paths.add(new String[]{"ZigBee", "FCS Radio Module"});

		paths = attackPaths.computeIfAbsent("NMEA GPS", key -> new ArrayList<>());
		paths.add(new String[]{"GPS", "NMEA GPS"});
		paths.add(new String[]{"ZigBee", "GCS Radio Module", "FCS Radio Module", "Primary Application Processor", "NMEA GPS"});
		paths.add(new String[]{"Wi-Fi", "Laptop", "GCS Radio Module", "FCS Radio Module", "Primary Application Processor", "NMEA GPS"});
		paths.add(new String[]{"ZigBee", "FCS Radio Module", "Primary Application Processor", "NMEA GPS"});
		paths.add(new String[]{"ZigBee", "Imagery Radio Module", "GCS Radio Module", "FCS Radio Module", "Primary Application Processor", "NMEA GPS"});

		paths = attackPaths.computeIfAbsent("Safety Switch Processor", key -> new ArrayList<>());
		paths.add(new String[]{"GPS", "NMEA GPS", "Primary Application Processor", "Safety Switch Processor"});
		paths.add(new String[]{"ZigBee", "Imagery Radio Module", "GCS Radio Module", "FCS Radio Module", "Primary Application Processor", "Safety Switch Processor"});
		paths.add(new String[]{"Wi-Fi", "Laptop", "GCS Radio Module", "FCS Radio Module", "Primary Application Processor", "Safety Switch Processor"});
		paths.add(new String[]{"ZigBee", "FCS Radio Module", "Primary Application Processor", "Safety Switch Processor"});
		paths.add(new String[]{"ZigBee", "GCS Radio Module", "FCS Radio Module", "Primary Application Processor", "Safety Switch Processor"});
	}


}
