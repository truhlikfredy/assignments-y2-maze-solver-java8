package eu.antonkrug;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Maze loader and generator class, handles file IO as well.
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
public class Maze {

	/**
	 * All possible types of blocks a maze can contain
	 */
	public enum Block {
		EMPTY {
			@Override
			public char getChar() {
				return '.';
			}
		},
		FINISH {
			@Override
			public char getChar() {
				return '*';
			}
		},
		NULL {
			@Override
			public char getChar() {
				return ' ';
			}
		},
		START {
			@Override
			public char getChar() {
				return 'o';
			}
		},
		WALL {
			@Override
			public char getChar() {
				return '#';
			}
		};

		private static final Block[]	allEnums	= values();

		public static Block getEnum(char input) {
			for (Block block : allEnums) {
				if (block.getChar() == input) return block;
			}
			return Block.NULL;
		}

		public char getChar() {
			return ' ';
		}

	}

	// 1000 + 1 border + 1 border = 1002
	private final static short	MAX_MAZE_HEIGHT	= 1002;
	private final static short	MAX_MAZE_WIDTH	= 1002;

	private List<Point>					allDirections;

	private short								height;
	private Block[][]						maze;
	private ArrayList<Point>		wallList;

	private short								width;

	/**
	 * Constructor with some very small maze just to have something
	 */
	public Maze() {
		maze = new Block[1][1];
	}

	/**
	 * Add this point as finish
	 * 
	 * @param point
	 */
	public void addFinish(Point point) {
		maze[point.x][point.y] = Block.FINISH;
	}

	/**
	 * Add point as starting position
	 * 
	 * @param point
	 */
	public void addStart(Point point) {
		maze[point.x][point.y] = Block.START;
	}

	/**
	 * Add this point as walkable (not abscured by wall)
	 * 
	 * @param point
	 */
	public void addWalkablePath(Point point) {
		maze[point.x][point.y] = Block.EMPTY;
	}

	/**
	 * Create non walkable border around the edge of the maze
	 */
	public void border() {
		for (int x = 0; x < width; x++) {
			maze[x][0] = Block.WALL;
			maze[x][height - 1] = Block.WALL;
		}
		for (int y = 0; y < height; y++) {
			maze[0][y] = Block.WALL;
			maze[width - 1][y] = Block.WALL;
		}
	}

	/**
	 * Check if it's not blocked by wall
	 * 
	 * @param point
	 * @return
	 */
	public boolean canWalkTo(Point point) {
		// this way if some new block types will be implemented it will be safer to
		// extend
		if (maze[point.x][point.y] == Block.EMPTY) return true;
		if (maze[point.x][point.y] == Block.START) return true;
		if (maze[point.x][point.y] == Block.FINISH) return true;
		return false;
	}

	/**
	 * Fully fill with walls.
	 */
	public void fill() {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				maze[x][y] = Block.WALL;
			}
		}
	}

	/**
	 * Will generate low dificulty maze but with many dead ends
	 */
	public void generate() {
		// all cardinal direction for up,down,left and right
		this.allDirections = Arrays.asList(new Point(-1, 0), new Point(1, 0), new Point(0, 1),
				new Point(0, -1));

		wallList = new ArrayList<>();
		generateAddWalls(width / 2, height / 2);
		// generateAddWalls( 3, 3);

		Random rand = new Random();

		while (wallList.size() > 0) {
			Point wall = wallList.get(rand.nextInt(wallList.size()));

			int emptyWallX = wall.x;
			int emptyWallY = wall.y;

			for (Point point : allDirections) {
				if (maze[wall.x + point.x][wall.y + point.y] == Block.EMPTY) {
					emptyWallX = wall.x + point.x;
					emptyWallY = wall.y + point.y;
				}
			}

			// find if oposite direction is empty by inverting the delta
			int deltaX = wall.x - emptyWallX;
			int deltaY = wall.y - emptyWallY;

			if (maze[wall.x + deltaX][wall.y + deltaY] == Block.WALL) {
				maze[wall.x][wall.y] = Block.EMPTY;
				generateAddWalls(wall.x + deltaX, wall.y + deltaY);
			}

			wallList.remove(wall);
		}
	}

	/**
	 * Will add walls all around this coordinate to the wall list and make center
	 * walkable
	 * 
	 * @param x
	 * @param y
	 */
	private void generateAddWalls(int x, int y) {

		// make center walkable
		maze[x][y] = Block.EMPTY;

		// all around add to list
		for (Point point : allDirections) {
			if (x > 1 && y > 1 && x < width - 2 && y < height - 2
					&& maze[x + point.x][y + point.y] == Block.WALL)
				wallList.add(new Point(x + point.x, y + point.y));
		}
	}

	/**
	 * Get whole maze as list
	 * 
	 * @param block
	 * @return
	 */
	public LinkedList<Point> getAllBlock(Block block) {
		LinkedList<Point> list = new LinkedList<>();

		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
				if (maze[x][y] == block) list.add(new Point(x, y));

		return list;
	}

	/**
	 * Get block from given position
	 * 
	 * @param point
	 * @return
	 */
	public Block getBlock(Point point) {
		return maze[point.x][point.y];
	}

	/**
	 * @return the height
	 */
	public short getHeight() {
		return height;
	}

	/**
	 * @return the width
	 */
	public short getWidth() {
		return width;
	}

	/**
	 * Create new maze with given width & height (width & height setup by
	 * different methods)
	 */
	public void initialize() {
		maze = new Block[width][height];
		border();
	}

	/**
	 * Load maze from file
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public boolean load(String fileName) throws Exception {
		BufferedReader br = null;
		String line = new String();

		br = new BufferedReader(new FileReader(fileName));

		setDimenstions(br.readLine());
		initialize();

		short lineNumber = 1;
		while ((line = br.readLine()) != null) {
			loadLineOfMaze(lineNumber, line);
			lineNumber++;
		}

		br.close();

		return true;
	}

	/**
	 * Parse one line a time
	 * 
	 * @param lineNumber
	 * @param line
	 */
	private void loadLineOfMaze(short lineNumber, String line) {
		for (int x = 0; x < width - 2; x++) {
			maze[x + 1][lineNumber] = Block.getEnum(line.charAt(x));
		}
	}

	/**
	 * Console debug output of the maze
	 */
	public void printDebugMaze() {
		System.out.println(width);
		System.out.println(height);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				System.out.print(maze[x][y]);
			}
			System.out.println("");
		}
	}

	/**
	 * Save maze to a file
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public boolean save(String fileName) throws Exception {
		PrintWriter printer = new PrintWriter(new File(fileName));
		printer.write(width + " " + height + "\n");

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				printer.write(maze[x][y].getChar());
			}
			printer.write("\n");
		}
		printer.flush();
		printer.close();

		return true;
	}

	/**
	 * Set dimensions of the maze
	 * @param line
	 * @throws Exception
	 */
	private void setDimenstions(String line) throws Exception {
		Scanner dimensions = new Scanner(line);

		setWidth((short) (2 + Short.parseShort(dimensions.next())));
		setHeight((short) (2 + Short.parseShort(dimensions.next())));

		dimensions.close();
	}

	/**
	 * If small enough (defualt 1002) will set height.
	 * 
	 * @param height
	 *          the height to set
	 */
	public void setHeight(short height) throws Exception {

		if (height > MAX_MAZE_HEIGHT)
			throw new Exception(String.format("Height %d is bigger than maximum allowed height %d",
					height, MAX_MAZE_HEIGHT));

		this.height = height;
	}

	/**
	 * If small enough (defualt 1002) will set width.
	 * @param width
	 *          the width to set
	 */
	public void setWidth(short width) throws Exception {

		if (width > MAX_MAZE_WIDTH)
			throw new Exception(String.format("Width %d is bigger than maximum allowed width %d", width,
					MAX_MAZE_WIDTH));

		this.width = width;
	}

}
