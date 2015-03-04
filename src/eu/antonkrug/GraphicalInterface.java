package eu.antonkrug;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import utils.Pair;

/**
 * 
 * @author Anton Krug
 * @date 2015/02/22
 * @version 1
 * @requires Java 8!
 * 
 */
public class GraphicalInterface implements ActionListener {

	private Map<String, Pair<JButton, Runnable>>	actions;

	// private JTextArea display, output;
	private JFrame																frame;
	private JLabel																statusBarLabel;
	private JPanel																mazePanel;
	private ImageIcon															mazeImage;
	private Graphics															mazeImageGFX;
	private Maze																	maze;
	private MazeSolver														solver;
	private Timer																	animationTimer;

	private static final int											BLOCK_WIDTH						= 16;
	private static final int											BLOCK_HEIGHT					= 16;

	private static final int											BLOCK_SPACING_WIDTH		= BLOCK_WIDTH + 1;
	private static final int											BLOCK_SPACING_HEIGHT	= BLOCK_HEIGHT + 1;

	public GraphicalInterface() {
		animationTimer = new Timer(200, (actionEvent) -> (this.stepChecks()));
		
		makeFrame();
	}

	public enum GuiButton {
		NULL {
			@Override
			public String toString() {
				return " ";
			}
		},
		LOAD {
			@Override
			public String toString() {
				return "Load";
			}
		},
		GENERATE {
			@Override
			public String toString() {
				return "Generate";
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
		FLUSH {
			@Override
			public String toString() {
				return "Flush solution";
			}
		},
		STEP {
			@Override
			public String toString() {
				return "Step";
			}
		},
		ANIMATE {
			@Override
			public String toString() {
				return "Toggle animation";
			}
		},
		EXIT {
			@Override
			public String toString() {
				return "Exit";
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

	/**
	 * An interface action has been performed. Find out what it was and handle it.
	 */
	public void actionPerformed(ActionEvent event) {
		String action = event.getActionCommand();
		if (actions.containsKey(action)) actions.get(action).second.run();
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
	private void addButton(Container panel, GuiButton buttonText, Runnable actionPerformed) {
		JButton button = new JButton(buttonText.toString());
		button.addActionListener(this);
		panel.add(button);

		actions.put(buttonText.toString(), new Pair<JButton, Runnable>(button, actionPerformed));
	}

	/**
	 * Create GUI parts
	 */
	private void makeFrame() {
		// creates window with minimum resolution
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

		// will ocuppy all avaiable space
		mazePanel = new JPanel();
		mazePanel.setLayout(new BorderLayout());

		// create empty maze image and wrap it in desired objects
		BufferedImage noMazeBuf = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
		Graphics noMazeBufGraphics;
		noMazeBufGraphics = noMazeBuf.getGraphics();
		noMazeBufGraphics.setColor(Color.red);
		noMazeBufGraphics.drawString("No maze", 100, 100);
		mazeImage = new ImageIcon(noMazeBuf);

		// make the maze scrollable and resizes by content window
		mazePanel.add(new JScrollPane(new JLabel(mazeImage)), BorderLayout.CENTER);
		uiPane.add(mazePanel, BorderLayout.CENTER);

		// ******* buttons and desired actions for buttons *******
		actions = new HashMap<>();

		JPanel buttonPanel = new JPanel(new GridLayout(9, 1));

		addButton(buttonPanel, GuiButton.LOAD, this::loadMaze);
		addButton(buttonPanel, GuiButton.GENERATE, this::buttonActionNoImplemented);
		addButton(buttonPanel, GuiButton.SAVE, this::buttonActionNoImplemented);
		addButton(buttonPanel, GuiButton.SOLVE, this::solve);
		addButton(buttonPanel, GuiButton.FLUSH, this::flushSolver);
		addButton(buttonPanel, GuiButton.STEP, this::stepChecks);
		addButton(buttonPanel, GuiButton.ANIMATE, this::animate);
		addButton(buttonPanel, GuiButton.EXIT, this::exit);

		// just leave some buttons enabled
		buttonDisableAll();
		buttonEnable(GuiButton.LOAD);
		buttonEnable(GuiButton.GENERATE);
		buttonEnable(GuiButton.EXIT);

		uiPane.add(buttonPanel, BorderLayout.WEST);

		frame.pack();
	}

	private void buttonDisableAll() {
		actions.entrySet().forEach(s -> s.getValue().first.setEnabled(false));
	}

	private void buttonEnable(GuiButton name) {
		if (actions.containsKey(name.toString())) {
			actions.get(name.toString()).first.setEnabled(true);
		}
	}

	private void buttonDisable(GuiButton name) {
		if (actions.containsKey(name.toString())) {
			actions.get(name.toString()).first.setEnabled(false);
		}
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
		gfx.drawImage(icon, position.x * BLOCK_SPACING_WIDTH + 1,
				position.y * BLOCK_SPACING_HEIGHT + 1, null);
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

	private void animate() {
		if (animationTimer.isRunning()) {
			animationTimer.stop();
			buttonEnable(GuiButton.LOAD);
			buttonEnable(GuiButton.GENERATE);
			
		} else {
			// disable everything just itself
			buttonDisableAll();
			buttonEnable(GuiButton.ANIMATE);
			buttonEnable(GuiButton.STEP);
			buttonEnable(GuiButton.EXIT);

			animationTimer.start();
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

	private void drawSolvedPath() {
		
		if (animationTimer.isRunning()) {
			animationTimer.stop();
			buttonEnable(GuiButton.LOAD);
			buttonEnable(GuiButton.GENERATE);
		}			
		
		for (Point point : solver.backTracePath()) {
			drawBlock(mazeImageGFX, point, Color.GREEN);
			// TODO draw arrows here as well (not sure if it will look nice :/ )
		}

		drawMazeIcons(false);
		statusBarLabel.setText(String.format("Solution found, took %d ms ", solver.timeTaken()));

		// when solution is found disable some buttons
		buttonDisable(GuiButton.SOLVE);
		buttonDisable(GuiButton.STEP);
		buttonEnable(GuiButton.FLUSH);
		buttonDisable(GuiButton.ANIMATE);
	}

	private void stepExecute() {
		buttonEnable(GuiButton.FLUSH);
		buttonDisable(GuiButton.SOLVE);

		if (solver.solveStepDidntStarted()) solver.solveStepInit();

		if (solver.solveStepCondition()) {
			solver.solveStepOneIteration();

			// draw the block from the open and closed list (visit and visited)
			solver.getVisit().entrySet()
					.forEach(node -> drawBlock(mazeImageGFX, node.getKey(), Color.CYAN));

			solver.getVisitedAlready().entrySet().forEach(node -> {
				drawBlock(mazeImageGFX, node.getKey(), Color.LIGHT_GRAY);
				drawArrow(mazeImageGFX, node.getKey(), node.getValue(), Color.GRAY);
			});

			// higlight next planed block
			drawBlock(mazeImageGFX, solver.getCurrentStep(), Color.RED);

			// draw the start / finish icons over the blocks
			drawMazeIcons(false);

			statusBarLabel.setText(String.format("Made step #%d nodesToVisit=%d, next step is %s", solver
					.getVisitedAlready().size(), solver.getVisit().size(), solver.getCurrentStep()));

		} else {
			if (solver.solveStepFinish() < 0) {
				statusBarLabel.setText("No solution found");
			} else {
				drawSolvedPath();
				statusBarLabel.setText(String.format("Solution found, took %d ms ", solver.timeTaken()));
			}
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

	private void drawMazeIconsStart(boolean blankBlock) throws IOException {
		BufferedImage iconStart = ImageIO.read(new File("img/start.png"));

		// draw all start icons
		for (Point point : maze.getAllBlock(Maze.Block.START)) {
			if (blankBlock) drawBlock(mazeImageGFX, point, Color.WHITE);
			drawBlock(mazeImageGFX, point, iconStart);
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
		}
		
		solver = new MazeSolver(maze);
		solver.setDestinations(maze.getAllBlock(Maze.Block.FINISH));
		try {

			solver.addStartingPositions(maze.getAllBlock(Maze.Block.START));
			drawMaze();
			statusBarLabel.setText("Maze loaded");

			// when flushed or loaded allow /disable some buttons
			// buttonEnable(GuiButton.SAVE);
			buttonEnable(GuiButton.SOLVE);
			buttonEnable(GuiButton.STEP);
			buttonEnable(GuiButton.ANIMATE);
			buttonDisable(GuiButton.FLUSH);
			

		} catch (Exception lackingNodesException) {
			setStatusBarException(lackingNodesException);
		}
	}

	private void loadMaze() {
		maze = new Maze();

		try {

			maze.load("mazes/tiny.maze");
			flushSolver();

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
	}

}