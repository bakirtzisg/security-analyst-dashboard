package edu.vcu.cyber.dashboard.graph.renderer;

import edu.vcu.cyber.dashboard.graph.layout.LayeredSectionsLayout;
import edu.vcu.cyber.dashboard.ui.graphpanel.GraphPanel;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.swingViewer.LayerRenderer;
import org.graphstream.ui.view.Camera;

import static edu.vcu.cyber.dashboard.graph.layout.LayeredSectionsLayout.LayoutSection;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.List;


public class SectionRenderer implements LayerRenderer
{
	private static final Font textFont = new Font("Fira Sans Light", Font.PLAIN, 20);

	private final LayeredSectionsLayout layout;
	private Camera camera;
	private final GraphPanel gp;

	public SectionRenderer(LayeredSectionsLayout layout, GraphPanel gp)
	{
		this.layout = layout;
		this.gp = gp;
	}

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
		double regionWidth = (maxXGu - minXGu) * px2Gu;
		double regionHeight = (maxYGu - minYGu) * px2Gu;

		double minPx = (widthPx - regionWidth) / 2;
		double minPy = (heightPx - regionHeight) / 2;

		camera = gp.getViewPanel().getCamera();
		layout.calculateBounds();

		List<LayoutSection> sections = layout.getSectionList();

		double bounds[] =
				{
						0,
						(sections.get(0).boundBottom + sections.get(1).boundTop) / 2,
						(sections.get(1).boundBottom + sections.get(2).boundTop) / 2,
						heightPx
				};

		bounds[1] = transformGu2Px(bounds[1], px2Gu, regionHeight, minPy, minYGu);
		bounds[2] = transformGu2Px(bounds[2], px2Gu, regionHeight, minPy, minYGu);

//		int lineY1 = (int) ((sections.get(0).boundBottom + sections.get(1).boundTop) / 2);
//		int lineY2 = (int) ((sections.get(1).boundBottom + sections.get(2).boundTop) / 2);

//		lineY1 = transformGu2Px(lineY1, px2Gu, regionHeight, minPy, minYGu);
//		lineY2 = transformGu2Px(lineY2, px2Gu, regionHeight, minPy, minYGu);

		g.setColor(Color.BLACK);
		g.drawLine(0, (int) bounds[1], widthPx, (int) bounds[1]);
		g.drawLine(0, (int) bounds[2], widthPx, (int) bounds[2]);

		g.setFont(textFont);
		FontMetrics metrics = g.getFontMetrics();

		int textLoc1 = (int) ((bounds[0] + bounds[1]) / 2);
		int textLoc2 = (int) ((bounds[1] + bounds[2]) / 2);
		int textLoc3 = (int) ((bounds[2] + bounds[3]) / 2);


		drawRotatedText(g, "Mission", 20, textLoc1, metrics);
		drawRotatedText(g, "Function", 20, textLoc2, metrics);
		drawRotatedText(g, "Structure", 20, textLoc3, metrics);

	}

	private int getCenteredTextPos(int y1, int y2, String text, FontMetrics m)
	{
		int width = m.stringWidth(text);
		return (y2 - y1 - width) / 2 + y1;
	}

	public int transformGu2Px(double gu, double px2Gu, double regionLength, double minPx, double minGu)
	{
		return (int) ((minGu - gu) * px2Gu + minPx + regionLength);
	}


	private void drawRotatedText(Graphics2D g, String msg, int posX, int posY, FontMetrics m)
	{
		int width = m.stringWidth(msg);
		posY += width / 2;

		AffineTransform affineTransform = new AffineTransform();
		affineTransform.rotate(Math.toRadians(-90), 0, 0);
		Font rotatedFont = textFont.deriveFont(affineTransform);

		g.setFont(rotatedFont);
		g.drawString(msg, posX, posY);
	}
}
