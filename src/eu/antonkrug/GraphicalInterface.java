package eu.antonkrug;

/**
 * 
 * @author Anton Krug
 * @date 2015/02/22
 * @version 1
 * @requires Java 8!
 * 
 */

/* Copyright (C) Anton Krug - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Anton Krug <anton.krug@gmail.com>, February 2015
 */

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import eu.antonkrug.MazeSolver.Aproach;
import utils.Pair;

/**
 * The main class which will start up GUI and handle all action eventss
 */

public class GraphicalInterface implements ActionListener {

	public enum GuiButton {
		BFS {
			@Override
			public String toString() {
				return "1";
			}
		},
		DFS {
			@Override
			public String toString() {
				return "2";
			}
		},
		HASH {
			@Override
			public String toString() {
				return "3";
			}
		},
		CONCURENT {
			@Override
			public String toString() {
				return "4";
			}
		},
		ANIMATE {
			@Override
			public String toString() {
				return "Toggle animation";
			}
		},
		DESTINATION_IGNORE {
			@Override
			public String toString() {
				return "Ignore Heurestics";
			}
		},
		EXIT {
			@Override
			public String toString() {
				return "Exit";
			}
		},
		FLUSH {
			@Override
			public String toString() {
				return "Flush solution";
			}
		},
		GENERATE {
			@Override
			public String toString() {
				return "Generate";
			}
		},
		LOAD {
			@Override
			public String toString() {
				return "Load";
			}
		},
		NULL {
			@Override
			public String toString() {
				return "";
			}
		},
		SAVE {
			@Override
			public String toString() {
				return "Save";
			}
		},
		SOLVE {
			@Override
			public String toString() {
				return "Solve";
			}
		},
		STEP {
			@Override
			public String toString() {
				return "Step";
			}
		};

		private static final GuiButton[]	allEnums	= values();

		public static GuiButton getEnum(String input) {
			for (GuiButton button : allEnums) {
				if (button.equals(input)) return button;
			}
			return GuiButton.NULL;
		}

		public String toString() {
			return "";
		}

	}

	// gui fields
	private static final int														BLOCK_HEIGHT					= 16;
	private static final int														BLOCK_WIDTH						= 16;
	private static final int														BLOCK_SPACING_HEIGHT	= BLOCK_HEIGHT + 1;
	private static final int														BLOCK_SPACING_WIDTH		= BLOCK_WIDTH + 1;
	private Map<String, Pair<AbstractButton, Runnable>>	actions;
	private Timer																				animationTimer;
	private JFrame																			frame;
	private JLabel																			statusBarLabel;

	// maze related fields
	private MazeSolver																	solver;
	private Aproach																			implementationToUse;
	private Maze																				maze;
	private ImageIcon																		mazeImage;
	private Graphics																		mazeImageGFX;
	private JPanel																			mazePanel;
	private JPanel																			implementationPanel;

	/**
	 * Constructor which will create frame, but will not make it public
	 */
	public GraphicalInterface() {
		animationTimer = new Timer(100, actionEvent -> this.stepChecks());
		implementationToUse = Aproach.JDK_HASHMAP;

		try {
			makeFrame();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Main method which will startup the GUI
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		GraphicalInterface app = new GraphicalInterface();
		app.run();
	}

	/**
	 * An interface action has been performed. Find out what it was and handle it.
	 */
	public void actionPerformed(ActionEvent event) {
		String action = event.getActionCommand();
		if (actions.containsKey(action)) actions.get(action).second.run();
	}

	/**
	 * Add a button to the button panel.
	 */
	private void addButtonToPanel(Container panel, boolean toggle, GuiButton buttonText,
			String iconName, Runnable actionPerformed) {
		AbstractButton button;

		Icon icon = new ImageIcon("./img/icon_" + iconName + ".png");

		if (toggle) {
			button = new JToggleButton(buttonText.toString(), icon);
		} else {
			button = new JButton(buttonText.toString(), icon);
		}

		button.addActionListener(this);
		panel.add(button);

		actions.put(buttonText.toString(), new Pair<AbstractButton, Runnable>(button, actionPerformed));
	}

	private void animate() {
		if (animationTimer.isRunning()) {
			animationTimer.stop();
			buttonEnable(GuiButton.LOAD);
			buttonEnable(GuiButton.GENERATE);
			buttonEnable(GuiButton.FLUSH);
			buttonEnable(GuiButton.SAVE);
		} else {
			// disable everything just keep couple buttons
			buttonDisableAll();
			buttonEnable(GuiButton.ANIMATE);
			// buttonEnable(GuiButton.FLUSH);
			buttonEnable(GuiButton.STEP);
			buttonEnable(GuiButton.DESTINATION_IGNORE);
			buttonEnable(GuiButton.EXIT);

			animationTimer.start();
		}

	}

	/**
	 * Just Helper method which can be used for button when there is not action
	 * implemented yet
	 */
	@SuppressWarnings("unused")
	private void buttonActionNoImplemented() {
		statusBarLabel.setText("This button action is not implemented yet");
	}

	private void buttonToggle(GuiButton name, boolean selected) {
		if (actions.containsKey(name.toString())) {
			actions.get(name.toString()).first.setSelected(selected);
		}
		// destinationIgnoreToggle();
	}

	private boolean buttonIsToggled(GuiButton name) {
		if (actions.containsKey(name.toString())) {
			return actions.get(name.toString()).first.isSelected();
		}
		return false;
	}

	// private void destinationConsider() {
	// solver.setDestinationVisible(true);
	//
	// //swap buttons
	// if (actions.containsKey(GuiButton.DESTINATION_CONSIDER.toString())) {
	// actions.get(GuiButton.DESTINATION_CONSIDER.toString()).first.setText(GuiButton.DESTINATION_IGNORE.toString());
	// }
	//
	// }

	private void buttonDisable(GuiButton name) {
		if (actions.containsKey(name.toString())) {
			actions.get(name.toString()).first.setEnabled(false);
		}
		// destinationIgnoreToggle();
	}

	private void buttonDisableAll() {
		actions.entrySet().forEach(s -> s.getValue().first.setEnabled(false));
		implementationDetect();
	}

	private void buttonEnable(GuiButton name) {
		if (actions.containsKey(name.toString())) {
			actions.get(name.toString()).first.setEnabled(true);
		}
	}

	private boolean buttonIsEnabled(GuiButton name) {
		if (actions.containsKey(name.toString())) {
			return actions.get(name.toString()).first.isEnabled();
		}
		return false;
	}

	private void implementationSelect() {

		if (buttonIsToggled(GuiButton.BFS) && buttonIsEnabled(GuiButton.BFS)) {

			buttonDisable(GuiButton.BFS);
			implementationToUse = Aproach.BFS;

		} else if (buttonIsToggled(GuiButton.DFS) && buttonIsEnabled(GuiButton.DFS)) {

			buttonDisable(GuiButton.DFS);
			implementationToUse = Aproach.DFS;

		} else if (buttonIsToggled(GuiButton.HASH) && buttonIsEnabled(GuiButton.HASH)) {

			buttonDisable(GuiButton.HASH);
			implementationToUse = Aproach.JDK_HASHMAP;

		} else if (buttonIsToggled(GuiButton.CONCURENT) && buttonIsEnabled(GuiButton.CONCURENT)) {

			buttonDisable(GuiButton.CONCURENT);
			implementationToUse = Aproach.JDK_CONCURENT_HASHMAP;

		}

		statusBarLabel.setText(String.format(
				"In next solver initialization a %s implementation will be used.", implementationToUse));

		implementationDetect();
	}

	private void implementationDetect() {
		// do not continue if it's not ready
		if (implementationPanel == null) return;

		// enable all
		for (int i = 0; i < 4; i++) {
			implementationPanel.getComponent(i).setEnabled(true);
		}
		buttonToggle(GuiButton.BFS, false);
		buttonToggle(GuiButton.DFS, false);
		buttonToggle(GuiButton.HASH, false);
		buttonToggle(GuiButton.CONCURENT, false);

		// detect which implementation is set to be used and show it as selected
		switch (implementationToUse) {

			case BFS:
				buttonToggle(GuiButton.BFS, true);
				buttonDisable(GuiButton.BFS);
				break;

			case DFS:
				buttonToggle(GuiButton.DFS, true);
				buttonDisable(GuiButton.DFS);
				break;

			case JDK_HASHMAP:
				buttonToggle(GuiButton.HASH, true);
				buttonDisable(GuiButton.HASH);
				break;

			case JDK_CONCURENT_HASHMAP:
				buttonToggle(GuiButton.CONCURENT, true);
				buttonDisable(GuiButton.CONCURENT);
				break;

			default:
				break;
		}
	}

	private void destinationIgnoreToggle() {
		if (solver != null) {
			solver.setDestinationVisible(!solver.isDestinationVisible());
			System.out.println(solver.isDestinationVisible());
		}
	}

	private void drawArrow(Graphics gfx, Point at, Point to, Color color) {
		if (at != null && to != null) {
			int difX = to.x - at.x;
			int difY = to.y - at.y;

			int pixMiddleX = at.x * BLOCK_SPACING_WIDTH + BLOCK_WIDTH / 2;
			int pixMiddleY = at.y * BLOCK_SPACING_HEIGHT + BLOCK_HEIGHT / 2;

			int pixEndX = pixMiddleX + difX * BLOCK_WIDTH / 2;
			int pixEndY = pixMiddleY + difY * BLOCK_HEIGHT / 2;

			gfx.setColor(color);
			gfx.drawLine(pixMiddleX, pixMiddleY, pixEndX, pixEndY);
		}
	}

	/**
	 * Paste into the maze block a image file (icon)
	 * 
	 * @param gfx
	 * @param position
	 * @param icon
	 */
	private void drawBlock(Graphics gfx, Point position, BufferedImage icon) {
		gfx.drawImage(icon, position.x * BLOCK_SPACING_WIDTH + 1,
				position.y * BLOCK_SPACING_HEIGHT + 1, null);
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

	private void drawMaze() {
		BufferedImage noMazeBuf = new BufferedImage(maze.getWidth() * BLOCK_SPACING_WIDTH,
				maze.getHeight() * BLOCK_SPACING_HEIGHT, BufferedImage.TYPE_INT_RGB);
		mazeImageGFX = noMazeBuf.getGraphics();

		try {
			// draw wall JPG as the wall background
			BufferedImage wall = ImageIO.read(new File("img/wall.jpg"));

			// if maze is bigger than background tile the background
			int iw = wall.getWidth();
			int ih = wall.getHeight();
			if (iw > 0 && ih > 0) {
				for (int x = 0; x < noMazeBuf.getWidth(); x += iw) {
					for (int y = 0; y < noMazeBuf.getHeight(); y += ih) {
						mazeImageGFX.drawImage(wall, x, y, null);
					}
				}
			}

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

	private void drawMazeIcons(boolean blankBlock) {
		try {
			// get icons ready

			// // draw all start icons
			// maze.getAllBlock(Maze.Block.START).stream().forEach(point -> {
			// if (blankBlock) drawBlock(mazeImageGFX, point, Color.WHITE);
			// drawBlock(mazeImageGFX, point, iconStart);
			// });
			//
			// // draw all finish icons
			// solver.getDestinations().stream().forEach(point -> {
			// if (blankBlock) drawBlock(mazeImageGFX, point, Color.WHITE);
			// drawBlock(mazeImageGFX, point, iconFinish);
			// });

			drawMazeIconsStart(blankBlock);
			drawMazeIconsFinish(blankBlock);

			mazePanel.repaint();

		} catch (IOException loadingImgFilesException) {
			setStatusBarException(loadingImgFilesException);
		}
	}

	private void drawMazeIconsFinish(boolean blankBlock) throws IOException {
		BufferedImage iconFinish = ImageIO.read(new File("img/finish.png"));

		// draw all finish icons
		for (Point point : solver.getDestinations()) {
			if (blankBlock) drawBlock(mazeImageGFX, point, Color.WHITE);
			drawBlock(mazeImageGFX, point, iconFinish);
		}
	}

	private void drawMazeIconsStart(boolean blankBlock) throws IOException {
		BufferedImage iconStart = ImageIO.read(new File("img/start.png"));

		// draw all start icons
		for (Point point : maze.getAllBlock(Maze.Block.START)) {
			if (blankBlock) drawBlock(mazeImageGFX, point, Color.WHITE);
			drawBlock(mazeImageGFX, point, iconStart);
		}
	}

	private void drawSolvedPath() {

		if (animationTimer.isRunning()) {
			animationTimer.stop();
			buttonEnable(GuiButton.LOAD);
			buttonEnable(GuiButton.GENERATE);
			buttonToggle(GuiButton.ANIMATE, false);
		}

		// draw the blocks from the open and closed list (visit and visited)

		if (solver.getVisitedAlready() == null) {

			// simpler alternative will have no direction arrows
			solver.getVisitedAlreadyAlternative().forEach(
					point -> drawBlock(mazeImageGFX, point, Color.LIGHT_GRAY));

		} else {

			// using map so we can draw directions
			solver.getVisitedAlready().entrySet().forEach(node -> {
				drawBlock(mazeImageGFX, node.getKey(), Color.LIGHT_GRAY);
				drawArrow(mazeImageGFX, node.getKey(), node.getValue(), Color.GRAY);
			});
		}

		solver.getVisit().forEach(point -> drawBlock(mazeImageGFX, point, Color.CYAN));
		
		// draw the final path
		for (Point point : solver.backTracePath()) {
			drawBlock(mazeImageGFX, point, Color.GREEN);
			// TODO draw arrows here as well (not sure if it will look nice :/ )
		}

		drawMazeIcons(false);
		statusBarLabel.setText(String.format("Solution found, took %d ms and took %d iterations.",
				solver.timeTaken(), solver.getVisitedAlreadySize()));

		// when solution is found disable some buttons
		buttonDisable(GuiButton.SOLVE);
		buttonDisable(GuiButton.STEP);
		buttonEnable(GuiButton.FLUSH);
		buttonDisable(GuiButton.ANIMATE);
		buttonDisable(GuiButton.DESTINATION_IGNORE);
	}

	private void exit() {
		// frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
		frame.setVisible(false);
		frame.dispose();
	}

	private void flushSolver() {

		if (animationTimer.isRunning()) {
			animationTimer.stop();
			buttonEnable(GuiButton.LOAD);
			buttonEnable(GuiButton.GENERATE);
			buttonToggle(GuiButton.ANIMATE, false);
		}

		try {
			switch (implementationToUse) {
				case DFS:
					solver = new MazeSolverDFS(maze);
					break;
					
				case JDK_HASHMAP:
					solver = new MazeSolverAStar(maze, Aproach.JDK_HASHMAP);
					break;

				case JDK_CONCURENT_HASHMAP:
					solver = new MazeSolverAStar(maze, Aproach.JDK_CONCURENT_HASHMAP);
					break;

				default:
					throw new Exception("Not implemented aproach for solver selected");
			}

			drawMaze();
			statusBarLabel.setText("Maze loaded");

			// when flushed or loaded allow /disable some buttons
			// buttonEnable(GuiButton.SAVE);
			buttonEnable(GuiButton.SOLVE);
			buttonEnable(GuiButton.STEP);
			buttonEnable(GuiButton.ANIMATE);

			buttonEnable(GuiButton.DESTINATION_IGNORE);
			buttonToggle(GuiButton.DESTINATION_IGNORE, false);

			buttonDisable(GuiButton.FLUSH);

		} catch (Exception lackingNodesException) {
			setStatusBarException(lackingNodesException);
		}
	}

	private void generateMaze() {
		maze = new Maze();

		try {
			maze.setWidth((short) (55));
			maze.setHeight((short) (150));
			// maze.setWidth((short)(562));
			// maze.setHeight((short)(550));

			maze.initialize();
			maze.fill();
			maze.generate();

			Random rand = new Random();

			// add some random points, hopefuly will create a loop or few
			// how many depends on the maze size
			int walkablePoints = (maze.getWidth() + maze.getHeight()) / 20;
			for (int count = 0; count < walkablePoints; count++) {
				maze.addWalkablePath(new Point(rand.nextInt(maze.getWidth()),
						rand.nextInt(maze.getHeight())));
			}

			// make border around maze as fail safe
			maze.border();

			Point startPoint;
			Point endPoint;
			int minimumDistance = (maze.getWidth() + maze.getHeight()) / 3;

			// add start & finish somewhere randomly, but bit far away from each other
			do {
				startPoint = new Point(rand.nextInt(maze.getWidth()), rand.nextInt(maze.getHeight()));
				endPoint = new Point(rand.nextInt(maze.getWidth()), rand.nextInt(maze.getHeight()));
			} while (!maze.canWalkTo(startPoint) || !maze.canWalkTo(endPoint)
					|| startPoint.distance(endPoint) < minimumDistance);

			maze.addStart(startPoint);
			maze.addFinish(endPoint);

			buttonEnable(GuiButton.SAVE);
			// prepare solver & refresh screen
			flushSolver();

		} catch (Exception e) {
			setStatusBarException(e);
		}
	}

	/**
	 * Gui file chooser and loader dialog
	 * 
	 */
	private void loadMaze() {
		maze = new Maze();

		String fileName = openSaveDialog(false);

		if (fileName != null) {
			try {

				// maze.load("mazes/tiny.maze");
				// maze.load("mazes/another.maze");
				// maze.load("mazes/test.maze");
				maze.load(fileName);
				flushSolver();

			} catch (Exception loadException) {
				setStatusBarException(loadException);
			}
		}
	}

	/**
	 * Create GUI parts
	 */
	private void makeFrame() throws Exception {
		// creates window with minimum resolution
		frame = new JFrame("Maze solver");
		frame.setMinimumSize(new Dimension(800, 500));
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

		// will ocuppy all avaiable space
		mazePanel = new JPanel();
		mazePanel.setLayout(new BorderLayout());

		// create empty maze image and wrap it in desired objects
		BufferedImage noMazeBuf = new BufferedImage(480, 310, BufferedImage.TYPE_INT_ARGB);
		Graphics noMazeBufGraphics;
		noMazeBufGraphics = noMazeBuf.getGraphics();

		BufferedImage wall = ImageIO.read(new File("./img/noMaze.jpg"));
		noMazeBufGraphics.drawImage(wall, 0, 0, null);

		// noMazeBufGraphics.setColor(Color.red);
		// noMazeBufGraphics.drawString("No maze", 100, 100);
		mazeImage = new ImageIcon(noMazeBuf);

		// make the maze scrollable and resizes by content window
		mazePanel.add(new JScrollPane(new JLabel(mazeImage)), BorderLayout.CENTER);
		uiPane.add(mazePanel, BorderLayout.CENTER);

		makeFrameButtons(uiPane);

		frame.pack();
	}

	/**
	 * Buttons and desired actions for buttons
	 */
	private void makeFrameButtons(JPanel parent) {
		actions = new HashMap<>();

		JPanel buttonPanel = new JPanel(new GridLayout(10, 1));

		addButtonToPanel(buttonPanel, false, GuiButton.LOAD, "open", this::loadMaze);
		addButtonToPanel(buttonPanel, false, GuiButton.GENERATE, "generate", this::generateMaze);
		addButtonToPanel(buttonPanel, false, GuiButton.SAVE, "save", this::saveMaze);

		implementationPanel = new JPanel(new GridLayout(1, 4));

		addButtonToPanel(implementationPanel, true, GuiButton.BFS, "stack_top",
				this::implementationSelect);

		addButtonToPanel(implementationPanel, true, GuiButton.DFS, "stack_bottom",
				this::implementationSelect);

		addButtonToPanel(implementationPanel, true, GuiButton.HASH, "hash", this::implementationSelect);

		addButtonToPanel(implementationPanel, true, GuiButton.CONCURENT, "multi",
				this::implementationSelect);

		buttonPanel.add(implementationPanel);

		addButtonToPanel(buttonPanel, false, GuiButton.SOLVE, "solve2", this::solve);
		addButtonToPanel(buttonPanel, false, GuiButton.FLUSH, "clean", this::flushSolver);
		addButtonToPanel(buttonPanel, false, GuiButton.STEP, "solve", this::stepChecks);
		addButtonToPanel(buttonPanel, true, GuiButton.DESTINATION_IGNORE, "target",
				this::destinationIgnoreToggle);

		addButtonToPanel(buttonPanel, true, GuiButton.ANIMATE, "clock", this::animate);
		addButtonToPanel(buttonPanel, false, GuiButton.EXIT, "exit", this::exit);

		// disable all, just leave some buttons enabled
		buttonDisableAll();
		buttonEnable(GuiButton.LOAD);
		buttonEnable(GuiButton.GENERATE);

		buttonEnable(GuiButton.EXIT);

		parent.add(buttonPanel, BorderLayout.WEST);
	}

	private String openSaveDialog(boolean saveDialog) {
		// Choose maze file filter
		FileFilter ff = new FileFilter() {
			public boolean accept(File f) {
				if (f.isDirectory())
					return true;
				else if (f.getName().endsWith(".txt"))
					return true;
				else if (f.getName().endsWith(".maze"))
					return true;
				else return false;
			}

			public String getDescription() {
				return "All supported maze files";
			}
		};

		// Choose maze dialog
		final JFileChooser fc = new JFileChooser("./mazes");
		fc.setFileFilter(ff);

		if (saveDialog) {
			if (fc.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION) return null;
		} else {
			if (fc.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) return null;
		}

		java.io.File file = fc.getSelectedFile();
		return file.getPath();

	}

	/**
	 * Make the GUI visible, if not called class can be used for JUnit test
	 */
	public void run() {
		frame.setVisible(true);
	}

	public void saveMaze() {
		String fileName = openSaveDialog(true);

		if (fileName != null) {
			try {
				maze.save(fileName);
			} catch (Exception e) {
				setStatusBarException(e);
			}
		}
	}

	private void setStatusBarException(Exception exception) {
		statusBarLabel.setText(exception.toString());
	}

	private void solve() {
		if (solver.solvePath() > 0) {

			drawSolvedPath();

		} else {
			// when solution is not found disable some buttons
			buttonDisable(GuiButton.SOLVE);
			buttonDisable(GuiButton.STEP);
			buttonDisable(GuiButton.ANIMATE);
			buttonEnable(GuiButton.FLUSH);

			statusBarLabel.setText("Something wrong (no origin, or no possible path)");
		}
	}

	private void stepChecks() {
		if (!solver.isDoNotSolveAgain()) {
			stepExecute();
		} else {
			statusBarLabel.setText("Something wrong (no origin, or no possible path)");
			buttonDisable(GuiButton.STEP);
		}
	}

	private void stepExecute() {
		if (!animationTimer.isRunning()) {
			buttonEnable(GuiButton.FLUSH);
		}
		buttonDisable(GuiButton.SOLVE);

		if (solver.solveStepDidntStarted()) solver.solveStepInit();

		if (solver.solveStepCondition()) {
			solver.solveStepOneIteration();

			// draw the block from the open and closed list (visit and visited)

			if (solver.getVisitedAlready() == null) {

				// non map alternative
				solver.getVisitedAlreadyAlternative().forEach(
						point -> drawBlock(mazeImageGFX, point, Color.LIGHT_GRAY));

			} else {

				// map aprroach (can draw arrows on the blocks)
				solver.getVisitedAlready().entrySet().forEach(node -> {
					drawBlock(mazeImageGFX, node.getKey(), Color.LIGHT_GRAY);
					drawArrow(mazeImageGFX, node.getKey(), node.getValue(), Color.GRAY);
				});

			}
			
			solver.getVisit().forEach(point -> drawBlock(mazeImageGFX, point, Color.CYAN));
			

			for (Point point : solver.backTracePathParty()) {
				drawBlock(mazeImageGFX, point, Color.GREEN);
			}

			// higlight next planed block
			drawBlock(mazeImageGFX, solver.getCurrentStep(), Color.RED);

			// draw the start / finish icons over the blocks
			drawMazeIcons(false);

			statusBarLabel.setText(String.format("Made step #%d nodesToVisit=%d, next step is %s",
					solver.getVisitedAlreadySize(), solver.getVisitSize(), solver.getCurrentStep()));

		} else {
			if (solver.solveStepFinish() < 0) {
				statusBarLabel.setText("No solution found");
			} else {
				drawSolvedPath();
				// statusBarLabel.setText(String.format("Solution found, took %d ms and took %d iterations.",
				// solver.timeTaken(),solver.getVisitedAlready().size()));
			}
		}
	}

	private void swapMazePanel(BufferedImage buffer) {
		mazeImage = new ImageIcon(buffer);

		mazePanel.remove(0);
		mazePanel.add(new JScrollPane(new JLabel(mazeImage)), BorderLayout.CENTER);
		frame.pack();
	}

}
