package eu.antonkrug;

import java.awt.Color;
import java.awt.Point;
import java.util.LinkedList;

import edu.princeton.cs.introcs.StdDraw;

/**
 * 
 * @author Anton Krug
 * @date 2015/02/22
 * @version 1
 * @requires Java 8!
 * 
 */
public class GraphicalInterface {

	public static void main(String[] args) {
		Maze maze = new Maze();
		
		maze.load("tiny.maze");
//		maze.printDebugMaze();
		StdDraw.setXscale(0.0,(double)maze.getWidth());
		StdDraw.setYscale((double)maze.getHeight(),0.0);
		StdDraw.setPenRadius(0.08);
				
		MazeSolver solver = new MazeSolver(maze);

		solver.setDestinations(maze.getAllBlock(Maze.Block.FINISH));
		StdDraw.setPenColor(Color.GREEN);
		for (Point point : maze.getAllBlock(Maze.Block.FINISH)) {
			StdDraw.point(point.x,point.y);
		}

		
		StdDraw.setPenColor(Color.RED);
		for (Point point : maze.getAllBlock(Maze.Block.WALL)) {
			StdDraw.point(point.x,point.y);
		}
		
		try {
			solver.addStartingPositions(maze.getAllBlock(Maze.Block.START));
			StdDraw.setPenColor(Color.MAGENTA);
			for (Point point : maze.getAllBlock(Maze.Block.START)) {
				StdDraw.point(point.x,point.y);
			}
			
			if (solver.solvePath() > 0) {
				StdDraw.setPenColor(Color.BLACK);
				StdDraw.setPenRadius(0.04);
				
				for (Point point : solver.backTracePath()) {
					StdDraw.point(point.x,point.y);
				}
			} else {
				System.out.print("Can't find path (no origin, or no possible path)");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} 

		
		//		MazeSolver solver = new MazeSolver();
//		
//		solver.addDestinationPosition(new Point(17,3));
//
//		try {
//			solver.addStartPosition(new Point(10, 10));
//			
//			if (solver.solvePath() > 0) {
//				solver.backTracePath();
//			} else {
//				System.out.print("Can't find path (no origin, or no possible path)");
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} 

	}

}
