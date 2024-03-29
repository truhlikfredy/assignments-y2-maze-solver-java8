package eu.antonkrug;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
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
 * The main class which will start up GUI and handle all action events
 * 
 * @author Anton Krug
 * @date 2015/02/22
 * @version 1.3
 * @requires Java 8!
 * 
 */

/*
 * Copyright (C) Anton Krug - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 * Written by Anton Krug <anton.krug@gmail.com>, February 2015
 */
public class Gui implements ActionListener {

	/**
	 * Texts for buttons
	 */
	public enum GuiButton {
		ANIMATE {
			@Override
			public String toString() {
				return "Toggle animation";
			}
		},
		ASTAR {
			@Override
			public String toString() {
				return "3";
			}
		},
		ASTART_CONCURENT {
			@Override
			public String toString() {
				return "4";
			}
		},
		BFS {
			@Override
			public String toString() {
				return "1";
			}
		},
		DESTINATION_IGNORE {
			@Override
			public String toString() {
				return "Ignore Heurestics";
			}
		},
		DFS {
			@Override
			public String toString() {
				return "2";
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
	private static final int														BLOCK_WIDTH						= 16;
	private static final int														BLOCK_HEIGHT					= 16;
	private static final int														BLOCK_SPACING_WIDTH		= BLOCK_WIDTH + 1;
	private static final int														BLOCK_SPACING_HEIGHT	= BLOCK_HEIGHT + 1;
	private Map<String, Pair<AbstractButton, Runnable>>	actions;
	private Timer																				animationTimer;
	private JFrame																			frame;
	private JPanel																			implementationPanel;
	// private JLabel statusBarLabel;

	// maze related fields
	private MazeSolver																	solver;
	private Aproach																			implementationToUse;

	private Maze																				maze;
	private JPanel																			mazePanel;

	private boolean																			stepButtonPressed;
	private GuiDraw																			draw;

	/**
	 * Constructor which will create frame, but will not make it public
	 */
	public Gui() {
		animationTimer = new Timer(75, actionEvent -> this.actionStepChecks());
		// animationTimer = new Timer(25, actionEvent -> this.actionStepChecks());
		// animationTimer = new Timer(5, actionEvent -> this.actionStepChecks());
		implementationToUse = Aproach.ASTAR_HASHMAP;
		stepButtonPressed = false;

		try {
			createGuiElements();
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
		Gui app = new Gui();
		app.run();
	}

	/**
	 * Animate button action
	 */
	private void actionAnimate() {
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
	 * Ignore heurestics button action
	 */
	private void actionDestinationIgnoreToggle() {
		if (solver != null) {
			solver.setDestinationVisible(!solver.isDestinationVisible());
			// System.out.println(solver.isDestinationVisible());
		}
	}

	/**
	 * Will quit the GUI
	 */
	private void actionExit() {
		// on lovely windows you have to stop timers to get the application to exit,
		// good i did some testing on non-linux platforms as well
		if (animationTimer.isRunning()) {
			animationTimer.stop();
		}
		// frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
		frame.setVisible(false);
		frame.dispose();
	}

	/**
	 * Will initialize new fresh solver
	 */
	private void actionFlushSolver() {

		if (animationTimer.isRunning()) {
			animationTimer.stop();
			buttonEnable(GuiButton.LOAD);
			buttonEnable(GuiButton.GENERATE);
			buttonToggle(GuiButton.ANIMATE, false);
		}

		try {
			switch (implementationToUse) {
				case BFS_QUEUE_JDK:
					solver = new MazeSolverBfs(maze, Aproach.BFS_QUEUE_JDK);
					break;

				case DFS_STACK_JDK:
					solver = new MazeSolverDfs(maze, Aproach.DFS_STACK_JDK);
					break;

				case ASTAR_HASHMAP:
					solver = new MazeSolverAStar(maze, Aproach.ASTAR_HASHMAP);
					break;

				case ASTAR_CONCURENT_HASHMAP:
					solver = new MazeSolverAStar(maze, Aproach.ASTAR_CONCURENT_HASHMAP);
					break;

				default:
					throw new Exception("Not implemented aproach for solver selected");
			}
			draw.setSolver(solver);
			draw.maze();
			draw.setStatusBar("Maze loaded and solver implementation now used: " + solver.getAproach());

			// when flushed or loaded allow /disable some buttons
			// buttonEnable(GuiButton.SAVE);
			buttonEnable(GuiButton.SOLVE);
			buttonEnable(GuiButton.STEP);
			buttonEnable(GuiButton.ANIMATE);

			buttonEnable(GuiButton.DESTINATION_IGNORE);
			buttonToggle(GuiButton.DESTINATION_IGNORE, false);

			buttonDisable(GuiButton.FLUSH);

		} catch (Exception lackingNodesException) {
			draw.setStatusBarException(lackingNodesException);
		}
	}

	/**
	 * Will generate new fresh maze
	 */
	private void actionGenerateMaze() {
		maze = new Maze();
		draw.setMaze(maze);

		try {
			maze.setWidth((short) (55));
			maze.setHeight((short) (37));
			// maze.setWidth((short) (150));
			// maze.setHeight((short) (150));

			maze.initialize();
			maze.fill();
			maze.generate();

			Random rand = new Random();

			// add some random walkable point instead of walls in hope to create loops
			// or alternative paths, how many depends on the maze size:
			// for maze 55x37 it will create 6 points

			int walkablePoints = (maze.getWidth() + maze.getHeight()) / 15;

			while (walkablePoints >= 0) {
				Point randomPoint = new Point(rand.nextInt(maze.getWidth()), rand.nextInt(maze.getHeight()));
				if (!maze.canWalkTo(randomPoint)) {
					maze.addWalkablePath(randomPoint);
					walkablePoints--;
				}
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
			actionFlushSolver();

		} catch (Exception e) {
			draw.setStatusBarException(e);
		}
	}

	private boolean buttonIsToggledNEnabled(GuiButton button) {
		return buttonIsToggled(button) && buttonIsEnabled(button);
	}

	/**
	 * Action for all implementation select buttons
	 */
	private void actionImplementationSelect() {
		HashMap<GuiButton, Aproach> buttonAproachMapping = new HashMap<>();

		buttonAproachMapping.put(GuiButton.BFS, Aproach.BFS_QUEUE_JDK);
		buttonAproachMapping.put(GuiButton.DFS, Aproach.DFS_STACK_JDK);
		buttonAproachMapping.put(GuiButton.ASTAR, Aproach.ASTAR_HASHMAP);
		buttonAproachMapping.put(GuiButton.ASTART_CONCURENT, Aproach.ASTAR_CONCURENT_HASHMAP);

		buttonAproachMapping.entrySet().stream().filter(item -> buttonIsToggledNEnabled(item.getKey()))
				.limit(1).forEach(item -> {
					buttonDisable(item.getKey());
					implementationToUse = item.getValue();
				});

		draw.setStatusBar(String
				.format(
						"In NEXT solver initialization a %s implementation will be used (load new maze, or flush solution to apply)",
						implementationToUse));

		implementationDetect();
	}

	/**
	 * Gui file chooser and loader dialog
	 * 
	 */
	private void actionLoadMaze() {
		maze = new Maze();
		draw.setMaze(maze);

		String fileName = openSaveDialog(false);

		if (fileName != null) {
			try {

				maze.load(fileName);
				actionFlushSolver();

			} catch (Exception loadException) {
				draw.setStatusBarException(loadException);
			}
		}
	}

	/**
	 * An interface action has been performed. Find out what it was and handle it.
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		String action = event.getActionCommand();
		if (actions.containsKey(action)) actions.get(action).second.run();
	}

	/**
	 * Save button action
	 */
	public void actionSaveMaze() {
		String fileName = openSaveDialog(true);

		if (fileName != null) {
			try {
				maze.save(fileName + ".maze");
			} catch (Exception e) {
				draw.setStatusBarException(e);
			}
		}
	}

	/**
	 * Do buttons manipulation before running solvePath
	 */
	private void preSolvePathButtons() {
		if (animationTimer.isRunning()) {
			animationTimer.stop();
			buttonEnable(GuiButton.LOAD);
			buttonEnable(GuiButton.GENERATE);
			buttonToggle(GuiButton.ANIMATE, false);
		}
	}

	/**
	 * Do buttons manipulation after solvePath was run
	 */
	private void postSolvePathButtons() {
		buttonDisable(GuiButton.SOLVE);
		buttonDisable(GuiButton.STEP);
		buttonEnable(GuiButton.FLUSH);
		buttonDisable(GuiButton.ANIMATE);
		buttonDisable(GuiButton.DESTINATION_IGNORE);
	}

	/**
	 * Solve button action
	 */
	private void actionSolve() {
		if (solver.solvePath() > 0) {

			preSolvePathButtons();
			draw.solvedPath();
			postSolvePathButtons();

		} else {
			// when solution is not found disable some buttons
			buttonDisable(GuiButton.SOLVE);
			buttonDisable(GuiButton.STEP);
			buttonDisable(GuiButton.ANIMATE);
			buttonEnable(GuiButton.FLUSH);

			draw.setStatusBar("Something wrong (no origin, or no possible path)");
		}
	}

	/**
	 * Do one step with all safe checks
	 */
	private void actionStepChecks() {
		// make sure it will not execute at same time (button + timmer)
		if (stepButtonPressed == false) {
			stepButtonPressed = true;
			if (!solver.isDoNotSolveAgain()) {
				stepExecute();
			} else {
				draw.setStatusBar("Something wrong (no origin, or no possible path)");
				buttonDisable(GuiButton.STEP);
			}
			stepButtonPressed = false;
		}
	}

	/**
	 * Add a button to the button panel.
	 * 
	 * @param panel
	 * @param toggle
	 * @param buttonText
	 * @param iconName
	 * @param actionPerformed
	 */
	private void addButtonToPanel(Container panel, boolean toggle, GuiButton buttonText,
			String iconName, Runnable actionPerformed) {
		AbstractButton button;

		Icon icon = new ImageIcon(getClass().getResource("/resources/icon_" + iconName + ".png"));

		if (toggle) {
			button = new JToggleButton(buttonText.toString(), icon);
		} else {
			button = new JButton(buttonText.toString(), icon);
		}

		button.addActionListener(this);
		panel.add(button);

		actions.put(buttonText.toString(), new Pair<AbstractButton, Runnable>(button, actionPerformed));
	}

	/**
	 * Just Helper method which can be used for button when there is not action
	 * implemented yet
	 */
	@SuppressWarnings("unused")
	private void buttonActionNoImplemented() {
		draw.setStatusBar("This button action is not implemented yet");
	}

	/**
	 * Disable desired button
	 * 
	 * @param name
	 */
	private void buttonDisable(GuiButton name) {
		if (actions.containsKey(name.toString())) {
			actions.get(name.toString()).first.setEnabled(false);
		}
	}

	/**
	 * Disable all buttons
	 */
	private void buttonDisableAll() {
		actions.entrySet().forEach(s -> s.getValue().first.setEnabled(false));
		implementationDetect();
	}

	/**
	 * Enable given button
	 * 
	 * @param name
	 */
	private void buttonEnable(GuiButton name) {
		if (actions.containsKey(name.toString())) {
			actions.get(name.toString()).first.setEnabled(true);
		}
	}

	/**
	 * Probe if button is disabled or enableds
	 * 
	 * @param name
	 * @return
	 */
	private boolean buttonIsEnabled(GuiButton name) {
		if (actions.containsKey(name.toString())) {
			return actions.get(name.toString()).first.isEnabled();
		}
		return false;
	}

	/**
	 * Probe if button is toggled
	 * 
	 * @param name
	 * @return
	 */
	private boolean buttonIsToggled(GuiButton name) {
		if (actions.containsKey(name.toString())) {
			return actions.get(name.toString()).first.isSelected();
		}
		return false;
	}

	/**
	 * Will change the toggle state of toggle buttons
	 * 
	 * @param name
	 * @param selected
	 */
	private void buttonToggle(GuiButton name, boolean selected) {
		if (actions.containsKey(name.toString())) {
			actions.get(name.toString()).first.setSelected(selected);
		}
	}

	/**
	 * Will change selected buttons depending on the implementation is selected
	 */
	private void implementationDetect() {
		// do not continue if it's not ready
		if (implementationPanel == null) return;

		// enable all
		for (int i = 0; i < 4; i++) {
			implementationPanel.getComponent(i).setEnabled(true);
		}
		buttonToggle(GuiButton.BFS, false);
		buttonToggle(GuiButton.DFS, false);
		buttonToggle(GuiButton.ASTAR, false);
		buttonToggle(GuiButton.ASTART_CONCURENT, false);

		// detect which implementation is set to be used and show it as selected
		switch (implementationToUse) {

			case BFS_QUEUE_JDK:
				buttonToggle(GuiButton.BFS, true);
				buttonDisable(GuiButton.BFS);
				break;

			case DFS_STACK_JDK:
				buttonToggle(GuiButton.DFS, true);
				buttonDisable(GuiButton.DFS);
				break;

			case ASTAR_HASHMAP:
				buttonToggle(GuiButton.ASTAR, true);
				buttonDisable(GuiButton.ASTAR);
				break;

			case ASTAR_CONCURENT_HASHMAP:
				buttonToggle(GuiButton.ASTART_CONCURENT, true);
				buttonDisable(GuiButton.ASTART_CONCURENT);
				break;

			default:
				break;
		}
	}

	/**
	 * Create GUI parts
	 */
	private void createGuiElements() throws Exception {
		// creates window with minimum resolution
		frame = new JFrame("Maze solver data structures assignment by Anton Krug");
		frame.setMinimumSize(new Dimension(900, 500));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// create the status bar on the bottom of the frame
		JPanel statusBarPanel = new JPanel();
		statusBarPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		frame.add(statusBarPanel, BorderLayout.SOUTH);
		statusBarPanel.setPreferredSize(new Dimension(frame.getWidth(), 24));
		statusBarPanel.setLayout(new BoxLayout(statusBarPanel, BoxLayout.X_AXIS));
		JLabel statusBarLabel = new JLabel("");
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

		BufferedImage wall = ImageIO.read(getClass().getResource("/resources/noMaze.jpg"));
		noMazeBufGraphics.drawImage(wall, 0, 0, null);

		// noMazeBufGraphics.setColor(Color.red);
		// noMazeBufGraphics.drawString("No maze", 100, 100);
		ImageIcon mazeImage = new ImageIcon(noMazeBuf);

		// make the maze scrollable and resizes by content window
		mazePanel.add(new JScrollPane(new JLabel(mazeImage)), BorderLayout.CENTER);
		uiPane.add(mazePanel, BorderLayout.CENTER);

		createGuiButtons(uiPane);

		frame.pack();

		draw = new GuiDraw(statusBarLabel, mazePanel, frame);
		draw.setBlocksDimensions(BLOCK_SPACING_WIDTH, BLOCK_SPACING_HEIGHT, BLOCK_WIDTH, BLOCK_HEIGHT);
		draw.setStatusBar("App loaded");
	}

	/**
	 * Buttons and desired actions for buttons
	 */
	private void createGuiButtons(JPanel parent) {
		actions = new HashMap<>();

		JPanel buttonPanel = new JPanel(new GridLayout(10, 1));

		addButtonToPanel(buttonPanel, false, GuiButton.LOAD, "open", this::actionLoadMaze);
		addButtonToPanel(buttonPanel, false, GuiButton.GENERATE, "generate", this::actionGenerateMaze);
		addButtonToPanel(buttonPanel, false, GuiButton.SAVE, "save", this::actionSaveMaze);

		implementationPanel = new JPanel(new GridLayout(1, 4));

		addButtonToPanel(implementationPanel, true, GuiButton.BFS, "stack_bottom",
				this::actionImplementationSelect);

		addButtonToPanel(implementationPanel, true, GuiButton.DFS, "stack_top",
				this::actionImplementationSelect);

		addButtonToPanel(implementationPanel, true, GuiButton.ASTAR, "hash",
				this::actionImplementationSelect);

		addButtonToPanel(implementationPanel, true, GuiButton.ASTART_CONCURENT, "multi",
				this::actionImplementationSelect);

		buttonPanel.add(implementationPanel);

		addButtonToPanel(buttonPanel, false, GuiButton.SOLVE, "solve2", this::actionSolve);
		addButtonToPanel(buttonPanel, false, GuiButton.FLUSH, "clean", this::actionFlushSolver);
		addButtonToPanel(buttonPanel, false, GuiButton.STEP, "solve", this::actionStepChecks);
		addButtonToPanel(buttonPanel, true, GuiButton.DESTINATION_IGNORE, "target",
				this::actionDestinationIgnoreToggle);

		addButtonToPanel(buttonPanel, true, GuiButton.ANIMATE, "clock", this::actionAnimate);
		addButtonToPanel(buttonPanel, false, GuiButton.EXIT, "exit", this::actionExit);

		// disable all, just leave some buttons enabled
		buttonDisableAll();
		buttonEnable(GuiButton.LOAD);
		buttonEnable(GuiButton.GENERATE);

		buttonEnable(GuiButton.EXIT);

		parent.add(buttonPanel, BorderLayout.WEST);
	}

	/**
	 * Will generate open / save dialog and return selected filename
	 * 
	 * @param saveDialog
	 * @return
	 */
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

	/**
	 * Execunte 1 step inside the solving algorithm
	 */
	private void stepExecute() {
		if (!animationTimer.isRunning()) {
			buttonEnable(GuiButton.FLUSH);
		}
		buttonDisable(GuiButton.SOLVE);

		if (solver.solveStepDidntStarted()) solver.solveStepInit();

		if (solver.solveStepCondition()) {
			solver.solveStepOneIteration();

			// draw the blocks from the open and closed list (visit and visited),
			// current path, and start,end icons
			draw.openClosedCurrentLists(solver.backTracePathPartially(), true);

			draw.setStatusBar(String.format("Made step #%d nodesToVisit=%d, next step is %s",
					solver.getVisitedAlreadySize(), solver.getVisitSize(), solver.getCurrentStep()));

		} else {
			if (solver.solveStepFinish() < 0) {
				draw.setStatusBar("No solution found");
			} else {
				preSolvePathButtons();
				draw.solvedPath();
				postSolvePathButtons();
			}
		}
	}

}
