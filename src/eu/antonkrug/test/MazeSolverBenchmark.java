package eu.antonkrug.test;

/**
 * @author Anton Krug
 * @date 2015/03/01
 * @version 1.1
 * @requires Java 8!
 * 
 * WARNING this will solve a maze 1000 times, it can take some time!
 * On old core 2 duo 2.4Ghz cpu running JDK 8 on Debain Wheezy it can take 
 * around 120s (2 minutes) to finish all benchmarks.
 * 
 *  Benchmark 1 - medium maze (1 start,1 destination)
 *  Benchmark 2 - smaller (1 start + 1 closed start, 1 destination + 1 closed destination)
 *  Benchmark 3 - tiny (2 reachable starts, 2 rechable destinations)
 *  Benchmark 4 - medium maze (1 start, 1 destination, but no solution)
 *  
 *  Results:
 *  
 *  System:	Linux - amd64 - 3.2.0-4-amd64 - CPUs: 2
 *  Java:	Oracle Corporation - Java(TM) SE Runtime Environment - 1.8.0_31-b13
 *  Memory:	1841823744 - Available: 118247312
 *  Count:	1000 times repeated benchmark
 *  ---------------------------------------
 *  Benchmark 1 ASTAR_CONCURENT_HASHMAP:	14552 ms
 *  Benchmark 2 ASTAR_CONCURENT_HASHMAP:	2027 ms
 *  Benchmark 3 ASTAR_CONCURENT_HASHMAP:	5 ms
 *  Benchmark 4 ASTAR_CONCURENT_HASHMAP:	3423 ms
 *  
 *  Benchmark 1 BFS_STACK:	38356 ms
 *  Benchmark 2 BFS_STACK:	2036 ms
 *  Benchmark 3 BFS_STACK:	0 ms
 *  Benchmark 4 BFS_STACK:	10083 ms
 *  
 *  Benchmark 1 DFS_STACK:	38545 ms
 *  Benchmark 2 DFS_STACK:	2059 ms
 *  Benchmark 3 DFS_STACK:	1 ms
 *  Benchmark 4 DFS_STACK:	10099 ms
 *  
 *  Benchmark 1 ASTAR_HASHMAP:	4385 ms
 *  Benchmark 2 ASTAR_HASHMAP:	25 ms
 *  Benchmark 3 ASTAR_HASHMAP:	0 ms
 *  Benchmark 4 ASTAR_HASHMAP:	1026 ms
 */

import java.util.Properties;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.antonkrug.Maze;
import eu.antonkrug.MazeSolver;
import eu.antonkrug.MazeSolver.Aproach;
import eu.antonkrug.MazeSolverAStar;
import eu.antonkrug.MazeSolverDFS;

public class MazeSolverBenchmark {
	private static final int	REPEAT_COUNT	= 1000;
	private Maze							maze;
	private long							totalTime;
	private String						benchmarkName;

	@BeforeClass
	public static void specs() {
		Properties p = System.getProperties();

		System.out.println("System:\t" + System.getProperty("os.name") + " - "
				+ System.getProperty("os.arch") + " - " + System.getProperty("os.version") + " - CPUs: "
				+ Runtime.getRuntime().availableProcessors());

		System.out.println("Java:\t" + p.getProperty("java.vm.vendor") + " - "
				+ p.getProperty("java.runtime.name") + " - " + p.getProperty("java.runtime.version"));

		System.out.println("Memory:\t" + Runtime.getRuntime().maxMemory() + " - " + "Available: "
				+ Runtime.getRuntime().freeMemory());

		System.out.println("Count:\t" + REPEAT_COUNT + " times repeated benchmark");
		System.out.println("---------------------------------------");

	}

	@Before
	public void setUp() throws Exception {
		System.out.println("");
		loadMaze("1");
	}

	private void loadMaze(String benchmarkName) throws Exception {
		maze = new Maze();
		this.benchmarkName = benchmarkName;
		maze.load("./testMazes/benchmark" + benchmarkName + ".maze");
	}

	private void aStarAproaches(Aproach implementationAproach) throws Exception {
		totalTime = 0;
		System.out.print("Benchmark " + benchmarkName + " " + implementationAproach + ":\t");
		for (int count = 0; count < REPEAT_COUNT; count++) {
			MazeSolver solver;

			switch (implementationAproach) {

				case BFS_STACK:
					solver = new MazeSolverDFS(maze);
					break;

				case DFS_STACK:
					solver = new MazeSolverDFS(maze);
					break;

				default:
					solver = new MazeSolverAStar(maze, implementationAproach);
					break;
			}
			solver.solvePath();
			totalTime += solver.timeTaken();
		}
		System.out.println(totalTime + " ms");
	}

	@Test
	public void DFS() throws Exception {
		aStarAproaches(Aproach.DFS_STACK);
		loadMaze("2");
		aStarAproaches(Aproach.DFS_STACK);
		loadMaze("3");
		aStarAproaches(Aproach.DFS_STACK);
		loadMaze("4");
		aStarAproaches(Aproach.DFS_STACK);
	}

	@Test
	public void concurent() throws Exception {
		aStarAproaches(Aproach.ASTAR_CONCURENT_HASHMAP);
		loadMaze("2");
		aStarAproaches(Aproach.ASTAR_CONCURENT_HASHMAP);
		loadMaze("3");
		aStarAproaches(Aproach.ASTAR_CONCURENT_HASHMAP);
		loadMaze("4");
		aStarAproaches(Aproach.ASTAR_CONCURENT_HASHMAP);
	}

	@Test
	public void hashMap() throws Exception {
		aStarAproaches(Aproach.ASTAR_HASHMAP);
		loadMaze("2");
		aStarAproaches(Aproach.ASTAR_HASHMAP);
		loadMaze("3");
		aStarAproaches(Aproach.ASTAR_HASHMAP);
		loadMaze("4");
		aStarAproaches(Aproach.ASTAR_HASHMAP);
	}

	@Test
	public void BFS() throws Exception {
		aStarAproaches(Aproach.BFS_STACK);
		loadMaze("2");
		aStarAproaches(Aproach.BFS_STACK);
		loadMaze("3");
		aStarAproaches(Aproach.BFS_STACK);
		loadMaze("4");
		aStarAproaches(Aproach.BFS_STACK);
	}

	// @Test
	// public void koloboke() throws Exception {
	// aStarAproaches(Aproach.KOLOBOKE);
	// }

	// @Test
	// public void fastutil() throws Exception {
	// aStarAproaches(Aproach.FASTUTIL_HASHMAP);
	// }

}
