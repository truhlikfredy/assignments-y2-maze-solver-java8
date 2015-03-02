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
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
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
	private JPanel								mazePanel;
	private ImageIcon							mazeImage;
	private Graphics							mazeImageGFX;
	private Maze									maze;
	private MazeSolver						solver;

	private static final int			BLOCK_WIDTH						= 16;
	private static final int			BLOCK_HEIGHT					= 16;

	private static final int			BLOCK_SPACING_WIDTH		= BLOCK_WIDTH + 1;
	private static final int			BLOCK_SPACING_HEIGHT	= BLOCK_HEIGHT + 1;

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
	 * Just Helper method which can be used for button when there is not action
	 * implemented yet
	 */
	@SuppressWarnings("unused")
	private void buttonActionNoImplemented() {
		statusBarLabel.setText("This button action is not implemented yet");
	}

	/**
	 * Add a button to the button panel.
	 */
	private void addButton(Container panel, String buttonText, Runnable actionPerformed) {
		JButton button = new JButton(buttonText);
		button.addActionListener(this);
		panel.add(button);

		actions.put(buttonText, actionPerformed);
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

		// buttons and desired actions for buttons
		actions = new HashMap<>();

		// addAction("Load", this::loadMaze);
		// addAction("displayAll", this::printAllNames);
		// addAction("details", this::printPerson);
		// addAction("parents", this::printParents);)
		// addAction("children", this::printChildren);
		// addAction("grandChildren", this::grandChildren);
		// addAction("clear", this::clearText);
		JPanel buttonPanel = new JPanel(new GridLayout(9, 1));

		addButton(buttonPanel, "Load", this::loadMaze);

		addButton(buttonPanel, "Generate", this::buttonActionNoImplemented);
		addButton(buttonPanel, "Save", this::buttonActionNoImplemented);

		addButton(buttonPanel, "Solve", this::solve);

		addButton(buttonPanel, "Flush solution", this::buttonActionNoImplemented);
		addButton(buttonPanel, "Step", this::buttonActionNoImplemented);
		addButton(buttonPanel, "Animate", this::buttonActionNoImplemented);
		addButton(buttonPanel, "Exit", this::buttonActionNoImplemented);

		//		displayPanel.add(new JLabel(Messages.getString("FamilyTree.display"))); //$NON-NLS-1$

		// will ocuppy all space
		mazePanel = new JPanel();
		mazePanel.setLayout(new BorderLayout());

		// create empty maze image and wrap it in desired objects
		BufferedImage noMazeBuf = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
		Graphics noMazeBufGraphics;
		noMazeBufGraphics = noMazeBuf.getGraphics();
		noMazeBufGraphics.setColor(Color.red);
		noMazeBufGraphics.drawString("No maze", 100, 100);
		mazeImage = new ImageIcon(noMazeBuf);

		// make the text area scrollable and resizes by content window
		mazePanel.add(new JScrollPane(new JLabel(mazeImage)), BorderLayout.CENTER);
		uiPane.add(mazePanel, BorderLayout.CENTER);

		// will ocuppy all space
		// outputPanel.setLayout(new BorderLayout());

		// make the text area scrollable and resizes by content window
		// outputPanel.add(new JScrollPane(output), BorderLayout.CENTER);

		uiPane.add(buttonPanel, BorderLayout.WEST);
		// uiPane.add(outputPanel, BorderLayout.EAST);
		frame.pack();

	}

	/**
	 * Adds lambda runnable to collection of action events
	 * 
	 * @param text
	 * @param runnable
	 */
	private void addAction(String text, Runnable runnable) {
		// actions.put(Messages.getString("FamilyTree." + text), runnable);
	}

	private void swapMazePanel(BufferedImage buffer) {
		mazeImage = new ImageIcon(buffer);

		mazePanel.remove(0);
		mazePanel.add(new JScrollPane(new JLabel(mazeImage)), BorderLayout.CENTER);
		frame.pack();
	}

	private void setStatusBarException(Exception exception) {
		statusBarLabel.setText(exception.toString());
	}

	/**
	 * Just fill maze block with given color
	 * 
	 * @param gfx
	 * @param position
	 * @param color
	 */
	private void drawBlock(Graphics gfx, Point position, Color color) {
		gfx.setColor(color);
		gfx.fillRect(position.x * BLOCK_SPACING_WIDTH, position.y * BLOCK_SPACING_HEIGHT, BLOCK_WIDTH,
				BLOCK_HEIGHT);
	}

	/**
	 * Paste into the maze block a image file (icon)
	 * 
	 * @param gfx
	 * @param position
	 * @param icon
	 */
	private void drawBlock(Graphics gfx, Point position, BufferedImage icon) {
		gfx.drawImage(icon, position.x * BLOCK_SPACING_WIDTH, position.y * BLOCK_SPACING_HEIGHT, null);
	}

	private void solve() {
		if (maze == null) {
			statusBarLabel.setText("No maze loaded to be solved");
		} else if (solver.solvePath() > 0) {

			for (Point point : solver.backTracePath()) {
				drawBlock(mazeImageGFX, point, Color.GREEN);
			}

			drawMazeIcons(false);
			mazePanel.repaint();
			statusBarLabel.setText("Solution found");

		} else {
			statusBarLabel
					.setText("Something wrong (no origin, or no possible path, or clicked Solve button multiple times)");
		}
	}

	private void drawMazeIcons(boolean blankBlock) {
		try {
			// get icons ready
			BufferedImage iconStart = ImageIO.read(new File("img/start.png"));
			BufferedImage iconFinish = ImageIO.read(new File("img/finish.png"));

			// draw all start icons
			for (Point point : maze.getAllBlock(Maze.Block.START)) {
				if (blankBlock) drawBlock(mazeImageGFX, point, Color.WHITE);
				drawBlock(mazeImageGFX, point, iconStart);
			}

			// draw all finish icons
			for (Point point : solver.getDestinations()) {
				if (blankBlock) drawBlock(mazeImageGFX, point, Color.WHITE);
				drawBlock(mazeImageGFX, point, iconFinish);
			}

		} catch (IOException loadingImgFilesException) {
			setStatusBarException(loadingImgFilesException);
		}
	}

	private void drawMaze() {
		BufferedImage noMazeBuf = new BufferedImage(maze.getWidth() * BLOCK_SPACING_WIDTH,
				maze.getHeight() * BLOCK_SPACING_HEIGHT, BufferedImage.TYPE_INT_RGB);
		mazeImageGFX = noMazeBuf.getGraphics();

		try {
			// draw wall JPG as the wall background
			BufferedImage wall = ImageIO.read(new File("img/wall.jpg"));
			mazeImageGFX.drawImage(wall, 0, 0, null);

			// draw all empty blocks, starts and destinations
			for (Point point : maze.getAllBlock(Maze.Block.EMPTY)) {
				drawBlock(mazeImageGFX, point, Color.WHITE);
			}

			drawMazeIcons(true);

			// replace the old maze graphics inside the jframe with this one
			swapMazePanel(noMazeBuf);

		} catch (IOException loadingImgFilesException) {
			setStatusBarException(loadingImgFilesException);
		}

	}

	private void loadMaze() {
		maze = new Maze();

		try {

			maze.load("mazes/tiny.maze");
			solver = new MazeSolver(maze);
			solver.setDestinations(maze.getAllBlock(Maze.Block.FINISH));
			solver.addStartingPositions(maze.getAllBlock(Maze.Block.START));

			drawMaze();

			statusBarLabel.setText("Maze loaded");

		} catch (Exception loadException) {
			setStatusBarException(loadException);
		}
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
