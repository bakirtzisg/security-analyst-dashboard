package edu.vcu.cyber.dashboard.graph.renderer;

import edu.vcu.cyber.dashboard.data.GraphData;
import edu.vcu.cyber.dashboard.data.GraphType;
import edu.vcu.cyber.dashboard.project.AppSession;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.swingViewer.LayerRenderer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

// TODO: Add implementation for hover menu
public class HoverMenuRenderer implements LayerRenderer
{
	private static final Font textFont = new Font("Fira Sans Light", Font.PLAIN, 14);

//	public static HoverMenuRenderer instance = new HoverMenuRenderer();

//	public static HoverMenuRenderer getInstance()
//	{
//		return instance;
//	}

	public static void display(String title, List<String> text, int px, int py, GraphType graphType)
	{
		if (!text.isEmpty())
		{
//			instance.graphType = graphType;
			HoverMenuRenderer.px = px;
			HoverMenuRenderer.py = py;
			HoverMenuRenderer.isVisible = true;
			if (!title.equals(HoverMenuRenderer.title))
			{
				HoverMenuRenderer.title = title;
				HoverMenuRenderer.text = text;

				setupDisplay();
			}
		}
	}

	public static void hide()
	{
		isVisible = false;
	}

	private static int px;
	private static int py;

	private static boolean isVisible;
	private static String title;
	private static List<String> text = new ArrayList<>();
	;
	private GraphData graphData;

	private static int width;
	private static int height;

	private static final int borderSize = 2;
	private static final int paddingX = 5;
	private static final int paddingY = 5;
	private static final int offsetX = 15;


	private static final int maxWidth = 250;
	private static BufferedImage disp;

	public HoverMenuRenderer(GraphData graphData)
	{
		this.graphData = graphData;
	}

	private static final Color BACKGROUND_COLOR = new Color(230, 230, 230, 240);
	private static final Color TEXT_COLOR = new Color(96, 96, 96);

	private static void setupDisplay()
	{
		BufferedImage display = new BufferedImage(maxWidth, 200, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) display.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


		g.setFont(textFont);
		FontMetrics metrics = g.getFontMetrics();
		int strHeight = metrics.getHeight();


		int posY = strHeight;

		List<String> lines = splitString(text, metrics);

		// hover menu background
		g.setColor(BACKGROUND_COLOR);
		g.fillRect(0, 0, width + paddingX - 1, lines.size() * strHeight + strHeight / 2);

		// hover menu outline
//		g.setColor(Color.BLACK);
//		g.drawRect(0, 0, width + paddingX - 1, lines.size() * strHeight + strHeight / 2);

		//draw node info
		g.setColor(TEXT_COLOR);
		for (String line : lines)
		{
			g.drawString(line, paddingX, posY);
			posY += strHeight;
		}
		height = posY + paddingY;

		HoverMenuRenderer.disp = display;
	}

	private static List<String> splitString(List<String> text, FontMetrics m)
	{
		List<String> newList = new ArrayList<>();


		int curWidth = 0;
		width = 0;

		int widthLim = maxWidth - paddingX * 2;

		for (String line : text)
		{
			if (m.stringWidth(line) >= widthLim)
			{
				StringBuilder sb = new StringBuilder();
				String[] split = line.split(" ");
				for (String word : split)
				{
					if (curWidth + m.stringWidth(word) > widthLim)
					{
						newList.add(sb.toString());
						sb = new StringBuilder("    ");
					}
					sb.append(word).append(" ");
					curWidth = m.stringWidth(sb.toString());

				}
				newList.add(sb.toString());
			}
			else
			{
				newList.add(line);
			}

		}
		for (String s : newList)
		{
			width = Math.max(width, m.stringWidth(s)) + paddingX;
		}

		return newList;
	}
//
//	private GraphPanel gp;
//	private GraphData graphData;
//
//	public HoverMenuRenderer(GraphPanel gp, GraphData graphData)
//	{
//		this.gp = gp;
//		this.graphData = graphData;
//	}


	/**
	 * Render something under or above the graph.
	 *
	 * @param g        The Swing graphics.
	 * @param graph    The graphic representation of the graph.
	 * @param px2Gu    The ratio to pass from pixels to graph units.
	 * @param widthPx  The width in pixels of the view port.
	 * @param heightPx The height in pixels of the view port.
	 * @param minXGu   The minimum hidden point abscissa of the graph in graph units.
	 * @param minYGu   The minimum hidden point ordinate of the graph in graph units.
	 * @param maxXGu   The maximum hidden point abscissa of the graph in graph units.
	 * @param maxYGu   The maximum hidden point ordinate of the graph in graph units.
	 */
	@Override
	public void render(Graphics2D g, GraphicGraph graph, double px2Gu,
	                   int widthPx, int heightPx, double minXGu, double minYGu,
	                   double maxXGu, double maxYGu)
	{
		if (AppSession.getSelectedGraph() == graphData.getGraphType())
		{
			int borderWidth = 1;
			g.setColor(Color.lightGray);
			g.fillRect(0, 0, widthPx, borderWidth);
			g.fillRect(0, 0, borderWidth, heightPx);
			g.fillRect(widthPx - borderWidth, 0, borderWidth, heightPx);
			g.fillRect(0, heightPx - borderWidth, widthPx, borderWidth);
		}

		if (AppSession.getFocusedGraph() == graphData.getGraphType())
		{

			if (isVisible)
			{
				if (disp != null)
				{
					int posX = px + offsetX;
					int posY = py;
					posX = Math.min(posX, widthPx - width - paddingX * 2);
					posY = Math.min(posY, heightPx - height);

					g.drawImage(disp, posX, posY, null);
				}
			}
		}

		g.setColor(TEXT_COLOR);
		g.setFont(textFont);
		g.drawString(graphData.getDisplayString(), 10, heightPx - textFont.getSize());

	}


}
