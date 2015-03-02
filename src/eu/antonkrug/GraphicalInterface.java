package eu.antonkrug;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import edu.princeton.cs.introcs.StdDraw;

/**
 * 
 * @author Anton Krug
 * @date 2015/02/22
 * @version 1
 * @requires Java 8!
 * 
 */
public class GraphicalInterface implements ActionListener {

	private Map<String, Runnable>	actions;

	// private JTextArea display, output;
	private JFrame								frame;
	private JLabel								statusBarLabel;

	public GraphicalInterface() {
		makeFrame();
	}

	/**
	 * An interface action has been performed. Find out what it was and handle it.
	 */
	public void actionPerformed(ActionEvent event) {
		String action = event.getActionCommand();
		if (actions.containsKey(action)) actions.get(action).run();
	}

	/**
	 * Add a button to the button panel.
	 */
	private void addButton(Container panel, String buttonText) {
		JButton button = new JButton(buttonText);
		button.addActionListener(this);
		panel.add(button);
	}

	/**
	 * Create GUI parts
	 */
	private void makeFrame() {
		frame = new JFrame("Maze solver");
		frame.setMinimumSize(new Dimension(640, 400));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// create the status bar on the bottom of the frame
		JPanel statusBarPanel = new JPanel();
		statusBarPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		frame.add(statusBarPanel, BorderLayout.SOUTH);
		statusBarPanel.setPreferredSize(new Dimension(frame.getWidth(), 24));
		statusBarPanel.setLayout(new BoxLayout(statusBarPanel, BoxLayout.X_AXIS));
		statusBarLabel = new JLabel("App started");
		statusBarPanel.add(statusBarLabel);

		// border
		JPanel uiPane = (JPanel) frame.getContentPane();
		// contentPane.setLayout(new BorderLayout());
		uiPane.setBorder(new EmptyBorder(10, 10, 10, 10));

		// buttons
		// display = new JTextArea(15, 30);
		// output = new JTextArea(15, 30);
		// name = new JTextField();
		JPanel buttonPanel = new JPanel(new GridLayout(9, 1));
		addButton(buttonPanel, "Load");
		addButton(buttonPanel, "Generate");
		addButton(buttonPanel, "Save");
		addButton(buttonPanel, "Solve");
		addButton(buttonPanel, "Flush solution");
		addButton(buttonPanel, "Step");
		addButton(buttonPanel, "Animate");
		addButton(buttonPanel, "Exit");
		
		JPanel displayPanel = new JPanel();
		//		displayPanel.add(new JLabel(Messages.getString("FamilyTree.display"))); //$NON-NLS-1$

		// will ocuppy all space
		displayPanel.setLayout(new BorderLayout());
		
		Canvas mazeCanvas = new Canvas();
		mazeCanvas.createImage(400, 400);
		
		JPanel dd = new JPanel();
		dd.setMinimumSize(new Dimension(900,900));
		dd.setMaximumSize(new Dimension(900,900));
//		dd.preferredSize();
		dd.setVisible(true);
		dd.setBackground(new java.awt.Color(0, 255, 255));
		
		BufferedImage buf = new BufferedImage(900, 900, BufferedImage.TYPE_INT_ARGB);
		
		Graphics bufG;
		
		bufG = buf.getGraphics();
//		bufG.
		bufG.setColor(Color.red);
    bufG.drawString("Testing",100,100);
    
    JLabel picLabel;
//    ImageIcon test = new ImageIcon(buf);
    picLabel = new JLabel(new ImageIcon(buf));
    

		//make the text area scrollable and resizes by content window
  	displayPanel.add(new JScrollPane(picLabel), BorderLayout.CENTER);

		// JPanel outputPanel = new JPanel();
		//		outputPanel.add(new JLabel(Messages.getString("FamilyTree.output"))); //$NON-NLS-1$
		uiPane.add(displayPanel, BorderLayout.CENTER);

		// will ocuppy all space
		// outputPanel.setLayout(new BorderLayout());

		// make the text area scrollable and resizes by content window
		// outputPanel.add(new JScrollPane(output), BorderLayout.CENTER);

		uiPane.add(buttonPanel, BorderLayout.WEST);
		// uiPane.add(outputPanel, BorderLayout.EAST);
		frame.pack();

		// actions for buttons
		actions = new HashMap<>();

		// addAction("loadDB", this::loadDatabase);
		// addAction("displayAll", this::printAllNames);
		// addAction("details", this::printPerson);
		// addAction("parents", this::printParents);
		// addAction("children", this::printChildren);
		// addAction("grandChildren", this::grandChildren);
		// addAction("clear", this::clearText);

	}

	/**
	 * Make the GUI visible, if not called class can be used for JUnit test
	 */
	public void run() {
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		GraphicalInterface app = new GraphicalInterface();
		app.run();

		// Maze maze = new Maze();
		//
		// maze.load("mazes/tiny.maze");
		// // maze.printDebugMaze();
		// StdDraw.setXscale(0.0, (double) maze.getWidth());
		// StdDraw.setYscale((double) maze.getHeight(), 0.0);
		// StdDraw.setPenRadius(0.08);
		//
		// MazeSolver solver = new MazeSolver(maze);
		//
		// solver.setDestinations(maze.getAllBlock(Maze.Block.FINISH));
		// StdDraw.setPenColor(Color.GREEN);
		// for (Point point : maze.getAllBlock(Maze.Block.FINISH)) {
		// StdDraw.point(point.x, point.y);
		// }
		//
		// StdDraw.setPenColor(Color.RED);
		// for (Point point : maze.getAllBlock(Maze.Block.WALL)) {
		// StdDraw.point(point.x, point.y);
		// }
		//
		// try {
		// solver.addStartingPositions(maze.getAllBlock(Maze.Block.START));
		// StdDraw.setPenColor(Color.MAGENTA);
		// for (Point point : maze.getAllBlock(Maze.Block.START)) {
		// StdDraw.point(point.x, point.y);
		// }
		//
		// if (solver.solvePath() > 0) {
		// StdDraw.setPenColor(Color.BLACK);
		// StdDraw.setPenRadius(0.04);
		//
		// for (Point point : solver.backTracePath()) {
		// StdDraw.point(point.x, point.y);
		// }
		// } else {
		// System.out.print("Can't find path (no origin, or no possible path)");
		// }
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		// MazeSolver solver = new MazeSolver();
		//
		// solver.addDestinationPosition(new Point(17,3));
		//
		// try {
		// solver.addStartPosition(new Point(10, 10));
		//
		// if (solver.solvePath() > 0) {
		// solver.backTracePath();
		// } else {
		// System.out.print("Can't find path (no origin, or no possible path)");
		// }
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

	}

}
