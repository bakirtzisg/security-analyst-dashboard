package edu.vcu.cyber.dashboard.data;

import edu.vcu.cyber.dashboard.graph.interpreters.AVGraphInterpreter;
import edu.vcu.cyber.dashboard.graph.interpreters.BasicGraphInterpreter;
import edu.vcu.cyber.dashboard.graph.interpreters.GraphInterpreter;

public enum GraphType
{
	TOPOLOGY("graph.css", BasicGraphInterpreter.class),
	ATTACK_SURFACE("graph.css", BasicGraphInterpreter.class),
	SPECIFICATIONS("spec.css", BasicGraphInterpreter.class),
	ATTACKS("attack.css", AVGraphInterpreter.class),
	OTHER("graph.css", BasicGraphInterpreter.class);

	public final String stylesheet;
	public final Class<? extends GraphInterpreter> interpreterClass;

	GraphType(String stylesheet, Class<? extends GraphInterpreter> interpreterClass)
	{
		this.stylesheet = stylesheet;
		this.interpreterClass = interpreterClass;
	}


	public GraphInterpreter getInterpreter()
	{

		if (interpreterClass == null)
			return null;

		try
		{
			return interpreterClass.newInstance();
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

}
