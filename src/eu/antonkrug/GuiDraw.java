package eu.antonkrug;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Maze drawing class and status bar handler
 * 
 * @author Anton Krug
 * @date 2015/02/27
 * @version 1.2
 * @requires Java 8!
 * 
 */

/*
 * Copyright (C) Anton Krug - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 * Written by Anton Krug <anton.krug@gmail.com>, February 2015
 */

public class GuiDraw {

	private int					blockSpacingWidth;
	private int					blockSpacingHeight;
	private int					blockWidth;
	private int					blockHeight;

	private Maze				maze;

	private Graphics		mazeImageGFX;
	private JLabel			statusBarLabel;
	private JPanel			mazePanel;
	private MazeSolver	solver;
	private JFrame			frame;

	/**
	 * Constructor needs some parameters from main class, dimensions of blocks and
	 * access to parent JPanels so in case it's needed they can mutate them or
	 * refresh them.
	 * 
	 * @param blockSpacingWidth
	 * @param blockSpacingHeight
	 * @param blockWidth
	 * @param blockHeight
	 * @param statusBarLabel
	 * @param mazePanel
	 * @param frame
	 */
	public GuiDraw(JLabel statusBarLabel, JPanel mazePanel, JFrame frame) {
		this.statusBarLabel = statusBarLabel;
		this.mazePanel = mazePanel;
		this.frame = frame;
	}
	
	/**
	 * Will set up spacing and size of blocks
	 * 
	 * @param blockSpacingWidth
	 * @param blockSpacingHeight
	 * @param blockWidth
	 * @param blockHeight
	 */
	public void setBlocksDimensions(int blockSpacingWidth, int blockSpacingHeight, int blockWidth, int blockHeight) {		
		this.blockSpacingWidth = blockSpacingWidth;
		this.blockSpacingHeight = blockSpacingHeight;
		this.blockWidth = blockWidth;
		this.blockHeight = blockHeight;
	}

	/**
	 * Gives access to this drawer class to loaded maze
	 * @param maze
	 */
	public void setMaze(Maze maze) {
		this.maze = maze;
	}

	/**
	 * Gives access to this drawer class to initialized solver
	 * @param maze
	 */
	public void setSolver(MazeSolver solver) {
		this.solver = solver;
	}

	/**
	 * Set status bar to some exception
	 * @param exception
	 */
	public void setStatusBarException(Exception exception) {
		statusBarLabel.setText(exception.toString());
	}

	/**
	 * Set status bar to given text
	 * @param text
	 */
	public void setStatusBar(String text) {
		statusBarLabel.setText(text);
	}

	/**
	 * Will draw simple arrow inside block. Useful to see where parent of given
	 * node is
	 * 
	 * @param gfx
	 * @param at
	 * @param to
	 * @param color
	 */
	private void arrow(Graphics gfx, Point at, Point to, Color color) {
		if (at != null && to != null) {
			int difX = to.x - at.x;
			int difY = to.y - at.y;

			// calculate middle of AT point
			int pixMiddleX = at.x * blockSpacingWidth + blockWidth / 2;
			int pixMiddleY = at.y * blockSpacingHeight + blockHeight / 2;

			// daw line torwards TO point
			int pixEndX = pixMiddleX + difX * blockWidth / 2;
			int pixEndY = pixMiddleY + difY * blockHeight / 2;

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
	private void block(Graphics gfx, Point position, BufferedImage icon) {
		gfx.drawImage(icon, position.x * blockSpacingWidth + 1, position.y * blockSpacingHeight + 1,
				null);
	}

	/**
	 * Just fill maze block with given color
	 * 
	 * @param gfx
	 * @param position
	 * @param color
	 */
	private void block(Graphics gfx, Point position, Color color) {
		gfx.setColor(color);
		gfx.fillRect(position.x * blockSpacingWidth, position.y * blockSpacingHeight, blockWidth,
				blockHeight);
	}

	/**
	 * Draw whole maze graphics
	 */
	public void maze() {
		BufferedImage noMazeBuf = new BufferedImage(maze.getWidth() * blockSpacingWidth,
				maze.getHeight() * blockSpacingHeight, BufferedImage.TYPE_INT_RGB);

		mazeImageGFX = noMazeBuf.getGraphics();

		try {
			// draw wall JPG as the wall background
			BufferedImage wall = ImageIO.read(getClass().getResource("/resources/wall.jpg"));

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
				block(mazeImageGFX, point, Color.WHITE);
			}

			mazeIcons(true);

			// replace the old maze graphics inside the jframe with this one
			swapMazePanel(noMazeBuf);

		} catch (IOException loadingImgFilesException) {
			setStatusBarException(loadingImgFilesException);
		}
	}

	/**
	 * Draw both start and finish icons
	 * 
	 * @param blankBlock
	 */
	private void mazeIcons(boolean blankBlock) {
		try {
			mazeIconsStart(blankBlock);
			mazeIconsFinish(blankBlock);

			mazePanel.repaint();

		} catch (IOException loadingImgFilesException) {
			setStatusBarException(loadingImgFilesException);
		}
	}

	/**
	 * Draw finish icons
	 * 
	 * @param blankBlock
	 * @throws IOException
	 */
	private void mazeIconsFinish(boolean blankBlock) throws IOException {
		BufferedImage iconFinish = ImageIO.read(getClass().getResource("/resources/finish.png"));

		// draw all finish icons
		for (Point point : solver.getDestinations()) {
			if (blankBlock) block(mazeImageGFX, point, Color.WHITE);
			block(mazeImageGFX, point, iconFinish);
		}
	}

	/**
	 * Draw start icon
	 * 
	 * @param blankBlock
	 * @throws IOException
	 */
	private void mazeIconsStart(boolean blankBlock) throws IOException {
		BufferedImage iconStart = ImageIO.read(getClass().getResource("/resources/start.png"));

		// draw all start icons
		for (Point point : maze.getAllBlock(Maze.Block.START)) {
			if (blankBlock) block(mazeImageGFX, point, Color.WHITE);
			block(mazeImageGFX, point, iconStart);
		}
	}

	/**
	 * Draw higlighted the solved path
	 */
	public void solvedPath() {
		// draw the blocks from the open and closed list (visit and visited),
		// current path, and start,end icons
		openClosedCurrentLists(solver.backTracePath(), false);

		statusBarLabel.setText(String.format("Solution found, took %d ms and took %d iterations.",
				solver.timeTaken(), solver.getVisitedAlreadySize()));

		// when solution is found disable some buttons
	}

	/**
	 * Draw colored block from all avaiable list to visualise better what is the
	 * solver doing.
	 * 
	 * Open list is list of blocks planed for visiting Closed list is list of
	 * blocks which were already visited
	 * 
	 * Current path is highlighted part between start and current step
	 * 
	 */
	public void openClosedCurrentLists(List<Point> currentPath, boolean drawCurrentStep) {
		Map<Point, Point> backtraceCache = null;

		// draw closed list
		if (solver.getVisitedAlready() == null) {

			// non map alternative
			solver.getVisitedAlreadyAlternative().forEach(
					point -> block(mazeImageGFX, point, Color.LIGHT_GRAY));

		} else {
			backtraceCache = solver.getVisitedAlready();

			// map aprroach (can draw arrows on the blocks)
			backtraceCache.entrySet().forEach(node -> {
				block(mazeImageGFX, node.getKey(), Color.LIGHT_GRAY);
				arrow(mazeImageGFX, node.getKey(), node.getValue(), Color.GRAY);
			});

		}

		// draw open list
		solver.getVisit().forEach(point -> block(mazeImageGFX, point, Color.CYAN));

		// draw current path
		for (Point point : currentPath) {
			block(mazeImageGFX, point, Color.GREEN);

			// if the data are avaiable then draw arrows on current path as well
			if (backtraceCache != null) {
				arrow(mazeImageGFX, point, backtraceCache.get(point), Color.BLUE);
			}
		}

		// higlight next planed block
		if (drawCurrentStep && solver.getCurrentStep() != null)
			block(mazeImageGFX, solver.getCurrentStep(), Color.RED);

		// draw the start / finish icons over the blocks
		mazeIcons(false);
	}

	/**
	 * Will remove all Jpane and replace it with new one contain new image
	 * 
	 * @param buffer
	 */
	private void swapMazePanel(BufferedImage buffer) {
		ImageIcon mazeImage = new ImageIcon(buffer);

		mazePanel.remove(0);
		mazePanel.add(new JScrollPane(new JLabel(mazeImage)), BorderLayout.CENTER);

		// do not resize it anymore, but force it to redraw
		frame.revalidate();
		// frame.pack();
	}

}
