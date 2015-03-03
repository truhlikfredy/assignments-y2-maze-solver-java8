package eu.antonkrug;

/**
 * 
 * @author Anton Krug
 * @date 2015/02/22
 * @version 1
 * @requires Java 8!
 * 
 */

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.Scanner;

public class Maze {

	private final static short	MAX_MAZE_WIDTH	= 1000;
	private final static short	MAX_MAZE_HEIGHT	= 1000;

	public enum Block {
		NULL {
			@Override
			public char getChar() {
				return ' ';
			}
		},
		EMPTY {
			@Override
			public char getChar() {
				return '.';
			}
		},
		WALL {
			@Override
			public char getChar() {
				return '#';
			}
		},
		START {
			@Override
			public char getChar() {
				return 'o';
			}
		},
		FINISH {
			@Override
			public char getChar() {
				return '*';
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

	private short	width;
	private short	height;

	/**
	 * @return the width
	 */
	public short getWidth() {
		return width;
	}

	/**
	 * @param width
	 *          the width to set
	 */
	public void setWidth(short width) throws Exception {

		if (width > MAX_MAZE_WIDTH)
			throw new Exception(String.format("Width %d is bigger than maximum allowed width %d", width,
					MAX_MAZE_WIDTH));

		this.width = width;
	}

	/**
	 * @return the height
	 */
	public short getHeight() {
		return height;
	}

	/**
	 * @param height
	 *          the height to set
	 */
	public void setHeight(short height) throws Exception {

		if (height > MAX_MAZE_HEIGHT)
			throw new Exception(String.format("Height %d is bigger than maximum allowed height %d",
					height, MAX_MAZE_HEIGHT));

		this.height = height;
	}

	private Block[][]	maze;

	public Maze() {
		maze = new Block[1][1];
	}

	public void initialize() {
		maze = new Block[width][height];
		for (int x = 0; x < width; x++) {
			maze[x][0] = Block.WALL;
			maze[x][height - 1] = Block.WALL;
		}
		for (int y = 0; y < height; y++) {
			maze[0][y] = Block.WALL;
			maze[width - 1][y] = Block.WALL;
		}
	}

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

	public LinkedList<Point> getAllBlock(Block block) {
		LinkedList<Point> list = new LinkedList<>();

		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
				if (maze[x][y] == block) list.add(new Point(x, y));

		return list;
	}

	private void setDimenstions(String line) throws Exception {
		Scanner dimensions = new Scanner(line);

		setWidth((short) (2 + Short.parseShort(dimensions.next())));
		setHeight((short) (2 + Short.parseShort(dimensions.next())));

		dimensions.close();
	}

	private void loadLineOfMaze(short lineNumber, String line) {
		for (int x = 0; x < width - 2; x++) {
			maze[x + 1][lineNumber] = Block.getEnum(line.charAt(x));
		}
	}

	public boolean canWalkTo(Point point) {
		if (maze[point.x][point.y] == Block.EMPTY) return true;
		if (maze[point.x][point.y] == Block.START) return true;
		if (maze[point.x][point.y] == Block.FINISH) return true;
		return false;
	}

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

}
