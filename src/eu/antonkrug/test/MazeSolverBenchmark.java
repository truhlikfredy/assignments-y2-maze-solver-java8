package eu.antonkrug.test;

/**
 * @author Anton Krug
 * 
 * Warning this will solve a maze 1000 times, it can take some time,
 * On old core 2 duo 2.4Ghz cpu running JDK 8 on Debain Wheezy it can take 
 * around 20s to finish all benchmarks. 
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
		maze = new Maze();
		maze.load("./testMazes/benchmark.maze");
	}

	private void aStarAproaches(Aproach implementationAproach) throws Exception {
		totalTime = 0;
		System.out.print(implementationAproach + ":\t");
		for (int count = 0; count < REPEAT_COUNT; count++) {
			MazeSolver solver;
			
			switch (implementationAproach) {
				
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
	}
	
	@Test
	public void concurent() throws Exception {
		aStarAproaches(Aproach.ASTAR_CONCURENT_HASHMAP);
	}

	@Test
	public void hashMap() throws Exception {
		aStarAproaches(Aproach.ASTAR_HASHMAP);
	}

//	@Test
//	public void koloboke() throws Exception {
//		aStarAproaches(Aproach.KOLOBOKE);
//	}


//	@Test
//	public void fastutil() throws Exception {
//		aStarAproaches(Aproach.FASTUTIL_HASHMAP);
//	}

}
