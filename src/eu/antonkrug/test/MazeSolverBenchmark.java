package eu.antonkrug.test;

import java.util.Properties;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.antonkrug.Maze;
import eu.antonkrug.MazeSolver;
import eu.antonkrug.MazeSolver.Aproach;
import eu.antonkrug.MazeSolverAStar;
import eu.antonkrug.MazeSolverDFS;

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
 *  
 *  Benchmark 2 - smaller (1 start + 1 closed start, 1 destination + 1 closed destination)
 *  
 *  Benchmark 3 - tiny (2 reachable starts, 2 rechable destinations)
 *  
 *  Benchmark 4 - medium maze (1 start, 1 destination, but no solution)
 *  
 *  Benchmark 5 - medium maze (1 start, 1 destination, but lot's of empty space
 *                and 2 different solutions)
 *  
 *  Results:
 *
 *  
//System:	Linux - amd64 - 3.2.0-4-amd64 - CPUs: 2
//Java:	Oracle Corporation - Java(TM) SE Runtime Environment - 1.8.0_31-b13
//Memory:	1841823744 - Available: 118897648
//Count:	1000 times repeated benchmark
//---------------------------------------
//
//Benchmark 1 ASTAR_CONCURENT_HASHMAP:	17096 ms
//Benchmark 2 ASTAR_CONCURENT_HASHMAP:	2040 ms
//Benchmark 3 ASTAR_CONCURENT_HASHMAP:	5 ms
//Benchmark 4 ASTAR_CONCURENT_HASHMAP:	4066 ms
//Benchmark 5 ASTAR_CONCURENT_HASHMAP:	2853 ms
//
//Benchmark 1 BFS_STACK:	39182 ms
//Benchmark 2 BFS_STACK:	2030 ms
//Benchmark 3 BFS_STACK:	1 ms
//Benchmark 4 BFS_STACK:	10283 ms
//Benchmark 5 BFS_STACK:	1059 ms
//
//Benchmark 1 DFS_STACK:	39134 ms
//Benchmark 2 DFS_STACK:	2031 ms
//Benchmark 3 DFS_STACK:	0 ms
//Benchmark 4 DFS_STACK:	10294 ms
//Benchmark 5 DFS_STACK:	1115 ms
//
//Benchmark 1 ASTAR_HASHMAP:	5343 ms
//Benchmark 2 ASTAR_HASHMAP:	24 ms
//Benchmark 3 ASTAR_HASHMAP:	0 ms
//Benchmark 4 ASTAR_HASHMAP:	1041 ms
//Benchmark 5 ASTAR_HASHMAP:	1010 ms
 * 
 */


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

				case BFS_QUEUE_MINE:
					solver = new MazeSolverDFS(maze,Aproach.BFS_QUEUE_MINE);
					break;

				case BFS_QUEUE_JDK:
					solver = new MazeSolverDFS(maze,Aproach.BFS_QUEUE_JDK);
					break;

				case DFS_STACK_MINE:
					solver = new MazeSolverDFS(maze,Aproach.DFS_STACK_MINE);
					break;

				case DFS_STACK_JDK:
					solver = new MazeSolverDFS(maze,Aproach.DFS_STACK_JDK);
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
	public void dfsMine() throws Exception {
		aStarAproaches(Aproach.DFS_STACK_MINE);
		loadMaze("2");
		aStarAproaches(Aproach.DFS_STACK_MINE);
		loadMaze("3");
		aStarAproaches(Aproach.DFS_STACK_MINE);
		loadMaze("4");
		aStarAproaches(Aproach.DFS_STACK_MINE);
		loadMaze("5");
		aStarAproaches(Aproach.DFS_STACK_MINE);
	}

	@Test
	public void dfsJdk() throws Exception {
		aStarAproaches(Aproach.DFS_STACK_JDK);
		loadMaze("2");
		aStarAproaches(Aproach.DFS_STACK_JDK);
		loadMaze("3");
		aStarAproaches(Aproach.DFS_STACK_JDK);
		loadMaze("4");
		aStarAproaches(Aproach.DFS_STACK_JDK);
		loadMaze("5");
		aStarAproaches(Aproach.DFS_STACK_JDK);
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
		loadMaze("5");
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
		loadMaze("5");
		aStarAproaches(Aproach.ASTAR_HASHMAP);
	}

	@Test
	public void bfsMine() throws Exception {
		aStarAproaches(Aproach.BFS_QUEUE_MINE);
		loadMaze("2");
		aStarAproaches(Aproach.BFS_QUEUE_MINE);
		loadMaze("3");
		aStarAproaches(Aproach.BFS_QUEUE_MINE);
		loadMaze("4");
		aStarAproaches(Aproach.BFS_QUEUE_MINE);
		loadMaze("5");
		aStarAproaches(Aproach.BFS_QUEUE_MINE);
	}

	@Test
	public void bfsJdk() throws Exception {
		aStarAproaches(Aproach.BFS_QUEUE_JDK);
		loadMaze("2");
		aStarAproaches(Aproach.BFS_QUEUE_JDK);
		loadMaze("3");
		aStarAproaches(Aproach.BFS_QUEUE_JDK);
		loadMaze("4");
		aStarAproaches(Aproach.BFS_QUEUE_JDK);
		loadMaze("5");
		aStarAproaches(Aproach.BFS_QUEUE_JDK);
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
