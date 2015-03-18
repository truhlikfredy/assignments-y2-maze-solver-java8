package eu.antonkrug.test;

import static org.junit.Assert.*;

import java.awt.Point;
import java.io.File;

import org.junit.Before;
import org.junit.Test;

import eu.antonkrug.*;
import eu.antonkrug.Maze.Block;

/**
 * Maze loader tester
 * 
 * @author Anton Krug
 * @date 2015/02/26
 * @version 1.0
 */
public class MazeTest {
	Maze	maze;

	@Before
	public void setUp() throws Exception {
		maze=new Maze();
	}

	@Test(expected = Exception.class)  
	public void testLoadErrorNotFound() throws Exception {
		//if this test fails
		File  mazeFile=new File("doNOTexists.fdsafdsmaze");
		
		if (!mazeFile.exists()) {
			maze.load(mazeFile.toString());
		} else {
			fail("ERROR: This "+mazeFile+" is existing and it shoudn't!");
		}
	}
	
	private void loadMaze(String mazeName) throws Exception {
		File  mazeFile=new File(mazeName);
		
		if (mazeFile.exists()) {
			maze.load(mazeName);
		} else {
			fail("ERROR: Test maze "+mazeName+" is missing!");
		}		
	}
	
	@Test(expected = Exception.class)  
	public void testLoadErrorMalformed() throws Exception {
		loadMaze("./testMazes/malformed.maze");
	}

	@Test(expected = Exception.class)  
	public void testLoadErrorTooBig() throws Exception {
		loadMaze("./testMazes/tooBig.maze");
	}

	@Test
	public void testLoadLarge() throws Exception {
		loadMaze("./testMazes/large.maze");
		assertEquals(502, maze.getWidth());
		assertEquals(502, maze.getHeight());
		assertEquals(Block.WALL, maze.getBlock(new Point(1,1)));
		assertEquals(Block.EMPTY, maze.getBlock(new Point(3,3)));
	}

	@Test
	public void testLoadNoBorders() throws Exception {
		loadMaze("./testMazes/noBorder.maze");
		assertEquals(14, maze.getWidth());
		assertEquals(12, maze.getHeight());
		
		for (int x=0;x<maze.getWidth();x++) {
			assertEquals(Block.WALL, maze.getBlock(new Point(x,0)));			
			assertEquals(Block.WALL, maze.getBlock(new Point(x,maze.getHeight()-1)));			
		}
		
		for (int y=0;y<maze.getHeight();y++) {
			assertEquals(Block.WALL, maze.getBlock(new Point(0,y)));			
			assertEquals(Block.WALL, maze.getBlock(new Point(maze.getWidth()-1,y)));			
		}
		
		assertEquals(Block.EMPTY, maze.getBlock(new Point(2,1)));
		
	}
	
}
