package eu.antonkrug.test;

import static org.junit.Assert.*;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import eu.antonkrug.AStartNode;

/**
 * Simple test for node datastructure
 * 
 * @author Anton Krug
 * @date 2015/02/26
 * @version 1.0
 */
public class AStartNodeTest {
	
	private AStartNode node;
	private List<Point> destinations;
	private Point sample;
	private Point sampleParent;

	@Before
	public void setUp() throws Exception {
		destinations = new ArrayList<>();
		destinations.add(new Point(100,100));
		destinations.add(new Point(20,20));
		sample = new Point(10,10);
		sampleParent = new Point(9,10);
	}

	@Test
	public void constructorTest() throws Exception {
		node = new AStartNode(sampleParent, 10, sample, destinations);

		assertEquals(sampleParent, node.getParent());
		assertEquals((Integer)(11), node.getG());
		assertEquals(20, node.getHeurestic());
		assertEquals((Integer)(31), node.getF());
	}

	@Test
	public void heuresticsTest() throws Exception {
		destinations.add(new Point(8,8));
		
		node = new AStartNode(sampleParent, 10, sample, destinations);

		assertEquals(4, node.getHeurestic());
		assertEquals((Integer)(15), node.getF());
	}
	
	@Test(expected=Exception.class)
	public void noDestinationTest() throws Exception {
		node = new AStartNode(sampleParent, 10, sample, new ArrayList<Point>());
		fail();
	}

}
