package edu.vcu.cyber.dashboard;

import java.io.File;

public class Config
{
	
	public static final boolean USE_SPEC_GRAPH = false;
	public static final File TEMP_DATA = new File("./cybok/");
	public static final String TEMP_DATA_NAME = "tmp";
	
	public static final File FILE_AV_GRAPH = new File(TEMP_DATA, TEMP_DATA_NAME + "_full_analysis.csv");
	public static final File FILE_FULL_ANALYSIS = new File(TEMP_DATA, TEMP_DATA_NAME + "_full_analysis.graphml");
	public static final File FILE_SYS_TOPOLOGY = new File(TEMP_DATA, TEMP_DATA_NAME + "_system_topology.graphml");
	
}
