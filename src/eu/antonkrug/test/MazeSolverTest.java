package eu.antonkrug.test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import eu.antonkrug.Maze;
import eu.antonkrug.MazeSolver;
import eu.antonkrug.MazeSolver.Aproach;
import eu.antonkrug.MazeSolverAStar;
import eu.antonkrug.MazeSolverBFS;
import eu.antonkrug.MazeSolverDFS;

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

	private void loadMaze(String mazeName)  {
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

	private void solveAll() throws Exception {
		solvers = new ArrayList<>();
		results = new ArrayList<>();

		solvers.add(Aproach.ASTAR_HASHMAP);
		solver = new MazeSolverAStar(maze, Aproach.ASTAR_HASHMAP);
		storeResults();

		solvers.add(Aproach.ASTAR_CONCURENT_HASHMAP);
		solver = new MazeSolverAStar(maze, Aproach.ASTAR_CONCURENT_HASHMAP);
		storeResults();

		solvers.add(Aproach.BFS_STACK);
		solver = new MazeSolverBFS(maze);
		storeResults();

		solvers.add(Aproach.DFS_STACK);
		solver = new MazeSolverDFS(maze);
		storeResults();
	}

	private void validateResults(List<Integer> expected) throws Exception {
		System.out.println("Maze:"+maze.getFileName()+" : "+ results);

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
						+ " -> " + resultsDescriptions.get(index % resultsDescriptions.size()));

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
		//we want get -1 as solution, max number of iterated blocks, and 0 blocks in queue for visit
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
		// want -1 as solution, and all lists 0 (nothing attepted, or even planned to attempt)
		validateResults(Arrays.asList(-1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0));
	}

//	very slow
//	@Test
//	public void largeTest() throws Exception {
//		loadMaze("./testMazes/large.maze");
//		solveAll();
//	}
	
	

}
