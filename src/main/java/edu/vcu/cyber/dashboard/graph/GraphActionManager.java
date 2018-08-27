package edu.vcu.cyber.dashboard.graph;

import edu.vcu.cyber.dashboard.graph.listeners.GraphActionListener;
import edu.vcu.cyber.dashboard.graph.renderer.HoverMenuRenderer;
import edu.vcu.cyber.dashboard.project.AppSession;
import edu.vcu.cyber.dashboard.util.Attributes;
import org.graphstream.graph.Node;
import org.graphstream.ui.geom.Point2;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Camera;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.util.DefaultMouseManager;
import scala.App;

import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class GraphActionManager extends DefaultMouseManager implements MouseWheelListener, KeyListener, FocusListener
{

	private GraphActionListener listener;
	private boolean panView;
	private Point3 lastPos;

	private Point2 pressPos;
	private Point3 nodeOffset;

	public GraphActionManager()
	{

	}

	public GraphActionListener getListener()
	{
		return listener;
	}

	public void setListener(GraphActionListener listener)
	{
		this.listener = listener;
	}

	@Override
	public void init(GraphicGraph g, View v)
	{
		super.init(g, v);
	}

	@Override
	public void release()
	{
		super.release();

		((ViewPanel) view).removeMouseWheelListener(this);
		view.removeKeyListener(this);
		((ViewPanel) view).removeFocusListener(this);
	}

	public void mouseEntered(MouseEvent e)
	{
		super.mouseEntered(e);
		if (listener != null)
		{
			listener.onMouseEnter();
		}
		curElement = null;
	}

	public void mouseExited(MouseEvent e)
	{
		super.mouseExited(e);
		HoverMenuRenderer.hide();
		graph.graphChanged = true;


		if (listener != null)
		{
			listener.onMouseExit();
		}
		curElement = null;
	}

	// Mouse Events
	public void mouseClicked(MouseEvent e)
	{
		if (listener == null)
			return;

		if (e.getButton() == MouseEvent.BUTTON1)
		{
			Node node = getNodeAt(e.getX(), e.getY());
			if (e.getClickCount() == 1)
			{
				listener.onSingleClick(node);
			}
			else if (e.getClickCount() == 2)
			{
				listener.onDoubleClick(node);
			}
		}


	}

	@Override
	public void mousePressed(MouseEvent e)
	{

		AppSession.setSelectedGraph(listener.getGraphData().getGraphType());

		pressPos = new Point2(e.getX(), e.getY());


		view.requestFocus();
		HoverMenuRenderer.hide();

		if (e.getButton() == MouseEvent.BUTTON1)
		{
//			super.mouseButtonPress(e);
			super.mousePressed(e);
			if (curElement != null)
			{
				nodeOffset = view.getCamera().transformGuToPx(curElement.getX(), curElement.getY(), curElement.getZ());
				nodeOffset.x -= pressPos.x;
				nodeOffset.y -= pressPos.y;

			}


			if (listener != null)
			{
				listener.onMousePressed((Node) curElement);
			}
		}
		else if (e.getButton() == MouseEvent.BUTTON2)
		{
			panView = true;
			lastPos = view.getCamera().transformPxToGu(e.getX(), e.getY());
		}
		else
		{
			panView = false;
		}
	}

	public void mouseMoved(MouseEvent e)
	{
		if (listener == null)
			return;

		if (!panView)
		{
			Node node = getNodeAt(e.getX(), e.getY());
			graph.graphChanged = listener.onMouseHover(node, e.getX(), e.getY());
		}

	}

	public void mouseReleased(MouseEvent event)
	{
		pressPos = null;
		if (!panView)
		{
			try
			{
				if (listener != null)
				{
					listener.onMouseRelease((Node) curElement);
				}
				if (event.getButton() == MouseEvent.BUTTON1)
				{
					super.mouseReleased(event);
				}

			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		panView = false;
	}

	protected void mouseButtonRelease(MouseEvent event, Iterable<GraphicElement> elementsInArea)
	{
		super.mouseButtonRelease(event, elementsInArea);

		List<Node> selected = new ArrayList<>();
		elementsInArea.forEach(item -> selected.add((Node) item));

		if (!selected.isEmpty())
		{
			listener.onSelection(selected);
		}
	}

	protected void mouseButtonReleaseOffElement(GraphicElement element, MouseEvent event)
	{
		if (event.getButton() != 3)
		{
			element.removeAttribute(Attributes.NODE_CLICKED);
		}
	}

	public void mouseDragged(MouseEvent event)
	{
		if (!panView)
		{
			super.mouseDragged(event);
		}
		else
		{

			Point3 curPos = view.getCamera().transformPxToGu(event.getX(), event.getY());
			Point3 camPos = view.getCamera().getViewCenter();
			camPos.x = lastPos.x - curPos.x + camPos.x;
			camPos.y = lastPos.y - curPos.y + camPos.y;
			camPos.z = lastPos.z - curPos.z + camPos.z;

			view.getCamera().setViewCenter(camPos.x, camPos.y, camPos.z);

			lastPos = curPos;
		}
	}

	protected void elementMoving(GraphicElement element, MouseEvent event)
	{

		if (event.isShiftDown() && pressPos != null)
		{
			double dx = Math.abs(pressPos.x - event.getX());
			double dy = Math.abs(pressPos.y - event.getY());

			if (dx < dy)
			{
				view.moveElementAtPx(element, nodeOffset.x + pressPos.x, nodeOffset.y + event.getY());
			}
			else
			{

				view.moveElementAtPx(element, nodeOffset.x + event.getX(), nodeOffset.y + pressPos.y);
			}

		}
		else
		{
			view.moveElementAtPx(element, nodeOffset.x + event.getX(), nodeOffset.y + event.getY());
		}


		listener.onNodeMoved((Node) element);
	}


	//Zooming and panning

	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		view.requestFocus();
		Camera camera = view.getCamera();

		double zoom = camera.getViewPercent();
		if (zoom <= 0.05)
		{
			zoom += 0.01 * e.getWheelRotation();
		}
		else
		{
			zoom += 0.05 * e.getWheelRotation();
		}
		if (zoom < 0.01)
		{
			zoom = 0.01;
		}
		camera.setViewPercent(zoom);

		Point3 relLoc = camera.transformPxToGu(e.getX(), e.getY());
		Point3 curLoc = camera.getViewCenter();
		relLoc = curLoc.interpolate(relLoc, 0.1);
		camera.setViewCenter(relLoc.x, relLoc.y, relLoc.z);

	}

	// Helper methods

	protected Node getNodeAt(int px, int py)
	{
		GraphicElement currentElement = view.findNodeOrSpriteAt(px, py);

		if (currentElement != null)
		{
			return (Node) currentElement;
		}

		return null;
	}


	@Override
	public void keyTyped(KeyEvent e)
	{

	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if (listener != null)
		{
			listener.onKeyPress(e);
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{

	}

	@Override
	public void focusGained(FocusEvent e)
	{
		AppSession.setFocus(listener.getGraphData().getGraphType());
	}

	@Override
	public void focusLost(FocusEvent e)
	{

	}

}

