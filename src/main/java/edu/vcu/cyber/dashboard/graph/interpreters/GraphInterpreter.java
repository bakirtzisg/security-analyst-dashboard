package edu.vcu.cyber.dashboard.graph.interpreters;

import org.graphstream.graph.Graph;
import org.graphstream.stream.Sink;

public abstract class GraphInterpreter implements Sink
{

	protected Graph graph;
	protected boolean initalGenerationDone;

	public void init(Graph graph)
	{
		this.graph = graph;
		graph.addSink(this);
	}

	public void generationComplete()
	{
		initalGenerationDone = true;
	}

	@Override
	public void graphAttributeAdded(String sourceId, long timeId, String attribute, Object value)
	{

	}

	@Override
	public void graphAttributeChanged(String sourceId, long timeId, String attribute, Object oldValue, Object newValue)
	{

	}

	@Override
	public void graphAttributeRemoved(String sourceId, long timeId, String attribute)
	{

	}

	@Override
	public void nodeAttributeAdded(String sourceId, long timeId, String nodeId, String attribute, Object value)
	{

	}

	@Override
	public void nodeAttributeChanged(String sourceId, long timeId, String nodeId, String attribute, Object oldValue, Object newValue)
	{

	}

	@Override
	public void nodeAttributeRemoved(String sourceId, long timeId, String nodeId, String attribute)
	{

	}

	@Override
	public void edgeAttributeAdded(String sourceId, long timeId, String edgeId, String attribute, Object value)
	{

	}

	@Override
	public void edgeAttributeChanged(String sourceId, long timeId, String edgeId, String attribute, Object oldValue, Object newValue)
	{

	}

	@Override
	public void edgeAttributeRemoved(String sourceId, long timeId, String edgeId, String attribute)
	{

	}

	@Override
	public void nodeRemoved(String sourceId, long timeId, String nodeId)
	{

	}

	@Override
	public void edgeAdded(String sourceId, long timeId, String edgeId, String fromNodeId, String toNodeId, boolean directed)
	{

	}

	@Override
	public void edgeRemoved(String sourceId, long timeId, String edgeId)
	{

	}

	@Override
	public void graphCleared(String sourceId, long timeId)
	{

	}

	@Override
	public void stepBegins(String sourceId, long timeId, double step)
	{

	}
}
