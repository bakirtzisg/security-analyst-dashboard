package edu.vcu.cyber.dashboard.ui;

import edu.vcu.cyber.dashboard.Config;
import edu.vcu.cyber.dashboard.av.*;
import edu.vcu.cyber.dashboard.cybok.CybokQueryHandler;
import edu.vcu.cyber.dashboard.cybok.queries.UpdateQuery;
import edu.vcu.cyber.dashboard.data.*;
import edu.vcu.cyber.dashboard.graph.interpreters.AVGraphInterpreter;
import edu.vcu.cyber.dashboard.project.AppSession;
import edu.vcu.cyber.dashboard.ui.custom.DockableTabs;
import edu.vcu.cyber.dashboard.ui.graphpanel.AVGraphPanel;
import edu.vcu.cyber.dashboard.ui.graphpanel.GraphPanel;
import edu.vcu.cyber.dashboard.ui.graphpanel.EditableGraphPanel;
import edu.vcu.cyber.dashboard.util.BucketData;
import edu.vcu.cyber.dashboard.util.GraphExporter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class DashboardUI extends JFrame implements ActionListener
{


	private GraphPanel topGraphPanel;
	private GraphPanel avGraphPanel;
	private GraphPanel specGraphPanel;

	private AVTreePanel avTreePanel;

	private JPanel contentPane;
	private JTabbedPane tabs;

	private JSplitPane sp;
	private JSplitPane updown;

	private BucketPanel bucket;

	private JLabel statusLabel;

	private boolean usingSpecGraph;

	public void setStatusLabel(String statusText)
	{
		statusLabel.setText("\t" + statusText);
	}

	public DashboardUI()
	{
		setTitle("Security Analyst Dashboard");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		initComponents();
		setupMenu();

		contentPane.setOpaque(true);
		setContentPane(contentPane);
		pack();
	}

	public JSplitPane getGraphSplitPane()
	{
		return sp;
	}

	public JTabbedPane getGraphTabs()
	{
		return tabs;
	}

	public JSplitPane getBucketSplitPane()
	{
		return updown;
	}

	public BucketPanel getBucketPanel()
	{
		return bucket;
	}

	private void initComponents()
	{
		statusLabel = new JLabel(" ");

		contentPane = new JPanel(new BorderLayout(5, 5));
		contentPane.add(new ControlToolbar(), BorderLayout.NORTH);

		sp = new JSplitPane();
		sp.setPreferredSize(new Dimension(1600, 900));
		sp.setDividerLocation(700);
		sp.setDividerSize(5);

		topGraphPanel = new EditableGraphPanel(GraphType.TOPOLOGY);
		avGraphPanel = new AVGraphPanel(GraphType.ATTACKS);

		specGraphPanel = new EditableGraphPanel(GraphType.SPECIFICATIONS);
		avTreePanel = new AVTreePanel();

		sp.setLeftComponent(topGraphPanel);
//		setAVVisComponent(false);

		tabs = new DockableTabs();
		tabs.add("Attack Vector Space", avGraphPanel);
		tabs.add("Attack Vector Tree", avTreePanel);
		sp.setRightComponent(tabs);
		sp.setDividerLocation(700);
		sp.setDividerSize(5);

		setUseSpecGraph(false);

		contentPane.add(sp, BorderLayout.CENTER);
		contentPane.add(statusLabel, BorderLayout.SOUTH);

//		new QuickFrame(avTreePanel, 600, 600).display();


	}

	public void setUseSpecGraph(boolean useSpecGraph)
	{
		edu.vcu.cyber.dashboard.Config.USE_SPEC_GRAPH = useSpecGraph;
		if (usingSpecGraph != useSpecGraph)
		{
			if (useSpecGraph)
			{
				tabs.add("Specifications", specGraphPanel);
			}
			else
			{
				tabs.remove(specGraphPanel);
			}
		}
		sp.setDividerLocation(700);
		sp.setDividerSize(5);
		usingSpecGraph = useSpecGraph;
	}

	private void setupMenu()
	{
		JMenuBar menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu("File");
//		fileMenu.add("Open").addActionListener(this);
		fileMenu.add("Load").addActionListener(this);
		fileMenu.add("Load Last").addActionListener(this);
		fileMenu.add("Save").addActionListener(this);

		JMenu exportMenu = (JMenu) fileMenu.add(new JMenu("Export"));
		exportMenu.add("Graph to GraphML").addActionListener(this);
		exportMenu.add("Bucket to CSV").addActionListener(this);

		JMenu importMenu = (JMenu) fileMenu.add(new JMenu("Import"));
		importMenu.add("Bucket from CSV").addActionListener(this);

		fileMenu.add("Exit").addActionListener(this);

		JMenu cybokMenu = new JMenu("Cybok");
//		cybokMenu.add("Configure").addActionListener(this);
		cybokMenu.add("Redo Analysis").addActionListener(this);
		cybokMenu.add("Update Cybok").addActionListener(this);

		JMenu viewMenu = new JMenu("View");
		viewMenu.add(new JCheckBoxMenuItem("Bucket")).addActionListener(this);

		JMenu filterMenu = new JMenu("Filter");
		filterMenu.add(new JCheckBoxMenuItem("Show Deleted")).addActionListener(this);
//		filterMenu.add(new JCheckBoxMenuItem("Show Hidden")).addActionListener(this);
		filterMenu.add(new JCheckBoxMenuItem("Show CVEs")).addActionListener(this);

		menuBar.add(fileMenu);
		menuBar.add(viewMenu);
		if (CybokQueryHandler.isCybokInstalled())
		{
			menuBar.add(cybokMenu);
		}
		menuBar.add(filterMenu);

		setJMenuBar(menuBar);
	}

	public void display()
	{
		setVisible(true);
	}

	public GraphPanel getTopGraphPanel()
	{
		return topGraphPanel;
	}

	public GraphPanel getAvGraphPanel()
	{
		return avGraphPanel;
	}

	public GraphPanel getSpecGraphPanel()
	{
		return specGraphPanel;
	}

	public GraphPanel getGraphPanel(GraphType type)
	{
		switch (type)
		{
			case ATTACKS:
				return avGraphPanel;
			case SPECIFICATIONS:
				return specGraphPanel;
			case ATTACK_SURFACE:
			case TOPOLOGY:
				return topGraphPanel;
		}
		System.out.println("??" + type);
		return null;
	}

//	public void setAVVisComponent(boolean isGraph)
//	{
//		if (isGraph && !(AttackVectors.vis() instanceof AVGraphVisHandler))
//		{
//			VisHandler.register(new AVGraphVisHandler(Application.getInstance().getSession().getAvGraph()));
//		}
//		else if (!isGraph && !(AttackVectors.vis() instanceof AVTreeVisHandler))
//		{
//			AttackVectors.setVisualizer(new AVTreeVisHandler(avTreePanel));
//		}
//	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		switch (e.getActionCommand())
		{
			case "Exit":
				System.exit(0);
				break;
			case "Attack Surfaces":
				AppSession.getInstance().toggleAttackSurfaces();
				break;

			case "Bucket":
				showBucket(updown == null);
				break;

			case "Show Node ID":

				AVGraphInterpreter.showNodeID = !AVGraphInterpreter.showNodeID;
				break;

			case "Show Deleted":
				edu.vcu.cyber.dashboard.Config.showDeletedNodes = !edu.vcu.cyber.dashboard.Config.showDeletedNodes;
				AttackVectors.update();
				break;

			case "Show Hidden":
				edu.vcu.cyber.dashboard.Config.showDeletedNodes = !edu.vcu.cyber.dashboard.Config.showDeletedNodes;
				AttackVectors.update();
				break;

			case "Show CVEs":
				edu.vcu.cyber.dashboard.Config.showCVENodes = !Config.showCVENodes;

				AttackVectors.update();
				break;


			// power to getting tired of repositioning all of the nodes!
			case "Save":
//				ApplicationSettings.saveAll(this);
				break;

			case "Load":
			{
//				ApplicationSettings.loadAll(this);
				askLoadTopology();

			}
			break;
			case "Load Last":
			{
//				ApplicationSettings.loadAll(this);

				if (Config.LAST_TOPOLOGY_FILE != null)
					AppSession.getInstance().load(Config.LAST_TOPOLOGY_FILE, Config.LAST_SPEC_FILE, Config.LAST_DO_ANALYSIS);
			}
			break;

			case "Bucket to CSV":
				BucketData.saveBukkitData();
				break;

			case "Bucket from CSV":
				BucketData.loadBukkitData(bucket);
				break;

			case "Graph to GraphML":
				GraphData graphData = AppSession.getFocusedGraphData();
				if (graphData != null)
				{
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setMultiSelectionEnabled(false);
					fileChooser.setSelectedFile(new File("./export/", graphData.getGraphType().name().toLowerCase() + ".graphml"));
					int ret = fileChooser.showSaveDialog(this);
					if (ret == JFileChooser.APPROVE_OPTION)
					{
						GraphExporter.exportGraph(AppSession.getSelectedGraphData(), fileChooser.getSelectedFile());
					}
				}
				break;
			case "Update Cybok":
			{
				int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to do this?\n This will take a while to finish!", "Update Cybok Database", JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.OK_OPTION)
				{
					CybokQueryHandler.sendQuery(new UpdateQuery());

					UpdateQuery.showDialog(this);
				}
			}
			break;
			case "Configure":
				// TODO: allow to specify cybok install location
				break;

			case "Redo Analysis":
				SystemAnalysis.doAnalysis();
				break;
		}
	}

	public static void askLoadTopology()
	{
		LoadFileDialog lfd = new LoadFileDialog();
		int result = lfd.ask(null);
		if (result == JOptionPane.OK_OPTION)
		{
			AppSession.getInstance().load(lfd.getTopologyGraphFile(), lfd.getSpecificationGraphFile(), lfd.doAnalysis());
		}
	}


	public void showBucket(boolean show)
	{
		if (show)
		{
			if (bucket == null)
			{

				bucket = BucketPanel.showBucket(true);

				contentPane.remove(sp);

				updown = new JSplitPane();
				updown.setOrientation(JSplitPane.VERTICAL_SPLIT);
				updown.setDividerSize(5);
				updown.setTopComponent(sp);
				updown.setBottomComponent(bucket);
				contentPane.add(updown, BorderLayout.CENTER);
				updown.setDividerLocation(contentPane.getHeight() / 3 * 2);
			}

		}
		else
		{
			if (bucket != null)
			{
				updown.remove(sp);
				contentPane.remove(updown);
				updown = null;
				contentPane.add(sp, BorderLayout.CENTER);
				bucket = null;
			}
		}

		contentPane.updateUI();
	}
}
