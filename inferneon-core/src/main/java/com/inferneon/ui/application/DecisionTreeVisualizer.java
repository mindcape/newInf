package com.inferneon.ui.application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.Border;

import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.ext.JGraphModelAdapter;

import com.inferneon.core.Attribute;
import com.inferneon.core.IInstances;
import com.inferneon.core.Instances;
import com.inferneon.core.InstancesFactory;
import com.inferneon.core.arffparser.ArffElements;
import com.inferneon.core.arffparser.ParserUtils;
import com.inferneon.core.exceptions.InvalidDataException;
import com.inferneon.core.utils.DataLoader;
import com.inferneon.supervised.decisiontree.DecisionTreeBuilder;
import com.inferneon.supervised.decisiontree.DecisionTreeEdge;
import com.inferneon.supervised.decisiontree.DecisionTreeNode;
import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;

public class DecisionTreeVisualizer {

	private static final long serialVersionUID = 3256444702936019250L;
	private JPanel jPanelfirst,jPanelSecond, jPanelTop;
	private JSplitPane jPanelMain; 
	private JScrollPane area;
	private JComboBox<String> optList;
	
	private static final String ROOT = "/TestResources";

	public DecisionTreeVisualizer() {
		final JFrame frame = new JFrame("JTextArea Test");
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setResizable(true);
		

		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		frame.setSize(new Dimension(width, height));
		frame.setResizable(true);
		frame.setLayout(new BorderLayout());	

		String defaultFolder = System.getProperty("user.home");
		optList = new JComboBox<String>();
		optList.addItem(defaultFolder);

		jPanelTop = new JPanel(new FlowLayout());
		frame.add(jPanelTop, BorderLayout.NORTH);
		jPanelTop.setBackground(Color.cyan);
		JLabel label = new JLabel("Select Training Data");
		jPanelTop.add(label);
		jPanelTop.add(optList);

		JButton nextButton = new JButton("Browse");
		nextButton.addActionListener(optiButtonClick );
		jPanelTop.add(nextButton);

		jPanelMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		jPanelMain.setLeftComponent(jPanelfirst);
		jPanelMain.setRightComponent(jPanelSecond);
		frame.add(jPanelMain,BorderLayout.CENTER);
		jPanelMain.setBackground(Color.cyan);

		jPanelfirst = new JPanel(new BorderLayout());
		jPanelfirst.setIgnoreRepaint(true);
		jPanelMain.add(jPanelfirst);

		JButton optimize = new JButton("Next Optimization");
		optimize.addActionListener(optiButtonClick );
		jPanelTop.add(optimize);
		jPanelSecond = new JPanel(new BorderLayout());
		jPanelSecond.setBounds(610 ,5,
				510,900);
		jPanelSecond.setBackground(Color.GRAY);
	}

	public static void main(String [] args) throws Exception{
		DecisionTreeVisualizer dtVisualizer = new DecisionTreeVisualizer();
		
		String trainingSamples = "PlayTennis.csv";
		DecisionTreeBuilder dt = new DecisionTreeBuilder();

		List<String> attrNames = new ArrayList<>();
		attrNames.add("Outlook"); attrNames.add("Temperature");attrNames.add("Humidity"); attrNames.add("Wind"); attrNames.add("PlayTennis");

		List<String> attrNominalValues = new ArrayList<>();
		attrNominalValues.add("Sunny"); attrNominalValues.add("Overcast"); attrNominalValues.add("Rain");
		attrNominalValues.add("Hot"); attrNominalValues.add("Mild"); attrNominalValues.add("Cool");
		attrNominalValues.add("High"); attrNominalValues.add("Normal");
		attrNominalValues.add("Strong"); attrNominalValues.add("Weak");
		attrNominalValues.add("Yes"); attrNominalValues.add("No");

		int lengths[] = new int[5]; lengths[0] = 3; lengths[1] = 3; lengths[2] = 2; lengths[3] = 2; lengths[4] = 2;

		ArffElements arffElements = ParserUtils.getArffElements(ROOT, trainingSamples);		
		List<Attribute> attributes = arffElements.getAttributes();

		IInstances instances = InstancesFactory.getInstance().createInstances("STAND_ALONE", 
				attributes, attributes.size() -1, trainingSamples);

		dt.train(instances);
	}
	
	private ActionListener optiButtonClick = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				updateDataSourceList();				
				runAlgorithm();

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	};

	private void updateDataSourceList() {
		// TODO Auto-generated method stub

	}

	private void runAlgorithm(){

	}

	public void show(DirectedAcyclicGraph<DecisionTreeNode, DecisionTreeEdge> tree){

		// Create a visualization using JGraph, via an adapter
		AttributeMap vertexMap = createVertexAttributes();
		AttributeMap edgeMap = createEdgeAttributes(tree);
		JGraphModelAdapter jgAdapter =  new JGraphModelAdapter(tree, vertexMap, edgeMap);
		final JGraph jgraph = new JGraph(jgAdapter);
		HashMap<DefaultGraphCell, AttributeMap> cellsAndAttrs = new HashMap<DefaultGraphCell, AttributeMap>();
		jgAdapter.edit(cellsAndAttrs, null, null, null);

		area = new JScrollPane(jgraph, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		area.setAutoscrolls(true);
		jPanelfirst.add(area);

		jgraph.setBackground(Color.LIGHT_GRAY);

		final  JGraphHierarchicalLayout hir = new JGraphHierarchicalLayout();
		hir.setInterHierarchySpacing(20);
		hir.setInterRankCellSpacing(20);
		hir.setIntraCellSpacing(20);
		hir.setFixRoots(true);
		hir.setFineTuning(true);
		hir.setDeterministic(true);

		final JGraphFacade graphFacade = new JGraphFacade(jgraph);
		hir.run(graphFacade);

		final Map<?, ?> nestedMap = graphFacade.createNestedMap(true, true);
		jgraph.getGraphLayoutCache().edit(nestedMap);
		//this.setVisible(true);
	}

	public static AttributeMap createVertexAttributes() {
		AttributeMap map = new AttributeMap();
		Color c = Color.WHITE;

		GraphConstants.setBounds(map, new Rectangle2D.Double(100, 50, 90, 30));
		GraphConstants.setBackground(map, c);
		GraphConstants.setForeground(map, Color.BLACK);
		GraphConstants.setFont(map, GraphConstants.DEFAULTFONT.deriveFont(Font.ITALIC,(float)11));
		GraphConstants.setOpaque(map, true);
		GraphConstants.setAutoSize(map, true);
		GraphConstants.setResize(map, true);
		GraphConstants.setBendable(map,true);
		Border insideBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		Border outsideBorder = BorderFactory.createLineBorder(Color.BLACK,1);
		Border compoundBorder = BorderFactory.createCompoundBorder(outsideBorder, insideBorder);
		GraphConstants.setBorder(map,compoundBorder);
		return map;
	}

	public static <V, E> AttributeMap createEdgeAttributes(
			Graph<V, E> jGraphTGraph) {
		AttributeMap map = new AttributeMap();

		if (jGraphTGraph instanceof DirectedGraph<?, ?>) {
			GraphConstants.setLineEnd(map, GraphConstants.ARROW_TECHNICAL);
			GraphConstants.setEndFill(map, true);
			GraphConstants.setEndSize(map, 10);
		}

		GraphConstants.setForeground(map, Color.decode("#25507C"));
		GraphConstants.setFont( map,
				GraphConstants.DEFAULTFONT.deriveFont(Font.BOLD,20));
		GraphConstants.setLineColor(map, Color.BLACK);
		GraphConstants.setLineWidth(map, (float) 1);
		GraphConstants.setBendable(map,true);
		GraphConstants.setLineStyle(map, GraphConstants.STYLE_SPLINE);
		return map;
	}

	protected List<Attribute> createAttributesWithNominalValues(
			List<String> attrNames, int[] lengths,
			List<String> attrNominalValues) {

		List<Attribute> attributes = new ArrayList<>();
		int count = 0, startIndex = 0;
		for(String attrName : attrNames){

			List<String> namesForAttr = attrNominalValues.subList(startIndex, startIndex + lengths[count]);

			startIndex = startIndex + lengths[count];

			Attribute attr = new Attribute(attrName, namesForAttr);
			attributes.add(attr);

			count++;

		}		
		return attributes;		
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}
}
