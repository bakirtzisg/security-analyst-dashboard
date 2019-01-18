package edu.vcu.cyber.dashboard.data;

import edu.vcu.cyber.dashboard.av.AttackVector;
import edu.vcu.cyber.dashboard.av.AttackVectors;
import edu.vcu.cyber.dashboard.util.Config;
import org.graphstream.graph.Node;

public enum AttackType
{
	CAPEC, CWE, CVE;
	
	public final String css;
	
	AttackType()
	{
		css = name().toLowerCase();
	}
	
	public static AttackType getType(Node node)
	{
		return getType(node.getId());
	}
	
	public static AttackType getType(String name)
	{
		name = name.toLowerCase();
		if (name.startsWith("cve"))
		{
			return CVE;
		}
		else if (name.startsWith("cwe"))
		{
			return CWE;
		}
		else if (name.startsWith("capec"))
		{
			return CAPEC;
		}
		return null;
	}
	
	public boolean canConsume(AttackType type)
	{
		return ordinal() < type.ordinal();
	}
	
	public boolean canShow()
	{
		switch (this)
		{
			case CAPEC:
				return Config.showCAPECNodes;
			case CWE:
				return Config.showCWENodes;
			case CVE:
				return Config.showCVENodes;
			default:
				return true;
		}
	}
}
