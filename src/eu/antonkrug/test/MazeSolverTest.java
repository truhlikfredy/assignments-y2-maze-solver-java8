package eu.antonkrug.test;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Test;

import eu.antonkrug.*;
import eu.antonkrug.MazeSolver.Aproach;

public class MazeSolverTest {
	private Maze					maze;
	private MazeSolver		solver;
	private List<Integer>	results;
	private List<String>	resultsDescriptions;
	private List<Aproach>	solvers;

	@Before
	public void setUp() throws Exception {
		maze = new Maze();
	}

	private void loadMaze(String mazeName) {
		File mazeFile = new File(mazeName);

		if (mazeFile.exists()) {
			try {
				maze.load(mazeName);
			} catch (Exception e) {
			}
		} else {
			fail("ERROR: Test maze " + mazeName + " is missing!");
		}
	}

	private void storeResults() {
		resultsDescriptions = new ArrayList<>();

		int iterations = solver.solvePath();
		results.add(iterations);
		resultsDescriptions.add("Number iterations it took");

		if (iterations > 0) {
			results.add(solver.backTracePath().size());
			resultsDescriptions.add("Lenght of solution");
		}

		results.add(solver.getVisitedAlreadySize());
		resultsDescriptions.add("Size of closed (visited) list");

		results.add(solver.getVisitSize());
		resultsDescriptions.add("Size of opened (visit) list");
	}

	private void drawMaze(BufferedImage img, int offsetX, int offsetY) {
		for (Point point : maze.getAllBlock(Maze.Block.EMPTY)) {
			img.setRGB(offsetX + point.x, offsetY + point.y, Color.WHITE.getRGB());
		}
	}

	private void drawOpenClosedCurrentLists(BufferedImage img, int offsetX, int offsetY) {
		int close = Color.GRAY.getRGB();
		int open = Color.RED.getRGB();
		int current = Color.GREEN.getRGB();

		// draw closed list
		if (solver.getVisitedAlready() == null) {

			// non map alternative
			solver.getVisitedAlreadyAlternative().forEach(
					point -> img.setRGB(offsetX + point.x, offsetY + point.y, close));

		} else {

			// map aprroach
			solver.getVisitedAlready().entrySet()
					.forEach(n -> img.setRGB(offsetX + n.getKey().x, offsetY + n.getKey().y, close));

		}

		// draw open list
		solver.getVisit().forEach(point -> img.setRGB(offsetX + point.x, offsetY + point.y, open));

		// draw current path
		List<Point> currentPath = solver.backTracePath();

		if (currentPath != null) {
			for (Point point : currentPath)
				img.setRGB(offsetX + point.x, offsetY + point.y, current);
		}

	}

	private void solveAll() throws Exception {
		solvers = new ArrayList<>();
		results = new ArrayList<>();

		int aproachesTypes = 3;
		int width = maze.getWidth();
		int height = maze.getHeight();

		BufferedImage img = new BufferedImage(width * aproachesTypes, height * 2,
				BufferedImage.TYPE_INT_RGB);

		solvers.add(Aproach.ASTAR_HASHMAP);
		solver = new MazeSolverAStar(maze, Aproach.ASTAR_HASHMAP);
		storeResults();
		drawMaze(img, width * 0, height * 0);
		drawOpenClosedCurrentLists(img, width * 0, height * 0);

		solvers.add(Aproach.ASTAR_CONCURENT_HASHMAP);
		solver = new MazeSolverAStar(maze, Aproach.ASTAR_CONCURENT_HASHMAP);
		storeResults();
		drawMaze(img, width * 0, height * 1);
		drawOpenClosedCurrentLists(img, width * 0, height * 1);

		solvers.add(Aproach.BFS_QUEUE_JDK);
		solver = new MazeSolverBFS(maze,Aproach.BFS_QUEUE_JDK);
		storeResults();
		drawMaze(img, width * 1, height * 0);
		drawOpenClosedCurrentLists(img, width * 1, height * 0);

		solvers.add(Aproach.BFS_QUEUE_MINE);
		solver = new MazeSolverBFS(maze,Aproach.BFS_QUEUE_MINE);
		storeResults();
		drawMaze(img, width * 1, height * 1);
		drawOpenClosedCurrentLists(img, width * 1, height * 1);

		solvers.add(Aproach.DFS_STACK_JDK);
		solver = new MazeSolverDFS(maze,Aproach.DFS_STACK_JDK);
		storeResults();
		drawMaze(img, width * 2, height * 0);
		drawOpenClosedCurrentLists(img, width * 2, height * 0);
		
		solvers.add(Aproach.DFS_STACK_MINE);
		solver = new MazeSolverDFS(maze,Aproach.DFS_STACK_MINE);
		storeResults();
		drawMaze(img, width * 2, height * 1);
		drawOpenClosedCurrentLists(img, width * 2, height * 1);

		File outputfile = new File(maze.getFileName().replaceAll("\\.maze$", ".png"));
		// System.out.println(outputfile);

		ImageIO.write(img, "png", outputfile);

	}

	private void validateResults(List<Integer> expected) throws Exception {
		// System.out.println("Maze:" + maze.getFileName() + " : " + results);

		// if they are the same just contine to let test pass
		if (!expected.equals(results)) {
			int index = 0;

			if (expected.size() == results.size()) {

				// if they are they same size find where they are broken
				while (index < expected.size()) {
					if (!results.get(index).equals(expected.get(index))) break;
					index++;
				}
				throw new Exception("Wrong result in " + solvers.get(index / resultsDescriptions.size())
						+ " -> " + resultsDescriptions.get(index % resultsDescriptions.size()) + " expected "
						+ expected.get(index) + " but got " + results.get(index));

			} else {
				// if they are not same size, then any of them could be broken
				throw new Exception("Any of involved aproached could be broken" + solvers.toString());
			}

		}
	}

	// HASHMAP & CONCURENT HASHMAP should yeld same numbers all the time

	@Test
	public void tinyTest() throws Exception {
		loadMaze("./testMazes/tiny.maze");
		solveAll();
		validateResults(Arrays.asList(59, 39, 60, 0, 59, 39, 60, 0, 59, 39, 60, 0, 58, 40, 50, 4));
	}

	@Test
	public void noSolutionTest() throws Exception {
		loadMaze("./testMazes/noSolution.maze");
		solveAll();
		// we want get -1 as solution, max number of iterated blocks, and 0 blocks
		// in queue for visit
		validateResults(Arrays.asList(-1, 1298, 0, -1, 1298, 0, -1, 1298, 0, -1, 1298, 0));
	}

	@Test
	public void noBorderTest() throws Exception {
		loadMaze("./testMazes/noBorder.maze");
		solveAll();
		validateResults(Arrays.asList(56, 37, 57, 3, 56, 37, 57, 3, 60, 37, 61, 1, 50, 40, 46, 5));
	}

	@Test
	public void noStartOrDesitnationTest() throws Exception {
		loadMaze("./testMazes/noStartOrDestination.maze");
		solveAll();
		// want -1 as solution, and all lists 0 (nothing attepted, or even planned
		// to attempt)
		validateResults(Arrays.asList(-1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0));
	}

	@Test
	public void openSpaceTest() throws Exception {
		loadMaze("./testMazes/openSpace.maze");
		solveAll();
		validateResults(Arrays.asList(960, 160, 961, 6, 960, 160, 961, 6, 1052, 160, 1053, 5, 734, 385,
				561, 115));
	}

	@Test
	public void smallSizeHardTest() throws Exception {
		loadMaze("./testMazes/hard55x37.maze");
		solveAll();
		validateResults(Arrays.asList(949, 235, 950, 3, 949, 235, 950, 3, 962, 235, 963, 1, 1228, 239,
				735, 28));
	}

	@Test
	public void mediumSizeHardTest() throws Exception {
		loadMaze("./testMazes/hard62x150.maze");
		solveAll();
		validateResults(Arrays.asList(4376, 580, 4377, 8, 4376, 580, 4377, 8, 4498, 580, 4499, 4, 4493,
				599, 2547, 113));
	}

	@Test
	public void smallSimpleTest() throws Exception {
		loadMaze("./testMazes/smallSimple.maze");
		solveAll();
		validateResults(Arrays.asList(365, 75, 366, 35, 365, 75, 366, 35, 760, 75, 761, 14, 1722, 78,
				901, 8));
	}

	@Test
	public void mediumSimpleTest() throws Exception {
		loadMaze("./testMazes/mediumSimple.maze");
		solveAll();
		validateResults(Arrays.asList(2078, 201, 2079, 72, 2078, 201, 2079, 72, 8216, 201, 8217, 109,
				3518, 340, 1930, 89));
	}

	// very slow
	// @Test
	// public void largeTest() throws Exception {
	// loadMaze("./testMazes/large.maze");
	// solveAll();
	// }

}
