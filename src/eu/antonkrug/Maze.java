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

		public char getChar() {
			return ' ';
		}
	}

	private short			width;
	private short			height;

	/**
	 * @return the width
	 */
	public short getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(short width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public short getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(short height) {
		this.height = height;
	}

	private char[][]	maze;

	public Maze() {
		maze = new char[1][1];
	}

	public void initialize() {
		maze = new char[width][height];
		for (int x = 0; x < width; x++) {
			maze[x][0] = Block.WALL.getChar();
			maze[x][height - 1] = Block.WALL.getChar();
		}
		for (int y = 0; y < height; y++) {
			maze[0][y] = Block.WALL.getChar();
			maze[width - 1][y] = Block.WALL.getChar();
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
				if (maze[x][y] == block.getChar()) list.add(new Point(x, y));

		return list;
	}

	private void setDimenstions(String line) {
		Scanner dimensions = new Scanner(line);
		this.width = (short) (2 + Short.parseShort(dimensions.next()));
		this.height = (short) (2 + Short.parseShort(dimensions.next()));
		dimensions.close();
	}

	private void loadLineOfMaze(short lineNumber, String line) {
		for (int x = 0; x < width - 2; x++) {
			maze[x + 1][lineNumber] = line.charAt(x);
		}
	}

	public boolean canWalkTo(Point point) {
		if (maze[point.x][point.y] == Block.EMPTY.getChar()) return true;
		if (maze[point.x][point.y] == Block.START.getChar()) return true;
		if (maze[point.x][point.y] == Block.FINISH.getChar()) return true;
		return false;
	}

	public boolean load(String fileName) {
		BufferedReader br = null;
		String line = new String();

		try {
			br = new BufferedReader(new FileReader(fileName));

			setDimenstions(br.readLine());
			initialize();

			short lineNumber = 1;
			while ((line = br.readLine()) != null) {
				loadLineOfMaze(lineNumber, line);
				lineNumber++;
			}

		} catch (Exception e) {
			System.out.println("Error while reading file:");
			e.printStackTrace();
			return false;
		} finally {
			// when done, try to close file
			try {
				br.close();
			} catch (Exception e) {
				System.out.println("Error while closing file:");
				e.printStackTrace();
			}
		}
		return true;
	}

}
