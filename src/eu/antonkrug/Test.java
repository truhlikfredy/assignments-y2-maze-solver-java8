package eu.antonkrug;

import java.awt.Point;
import java.util.Collections;
import java.util.HashMap;
//import java.util.Collections;
//import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

//import utils.HashedLinkedBlockingQueue;

/**
 * 
 * @author Anton Krug
 * @date 2015/02/22
 * @version 1
 * @requires Java 8!
 * 
 */
public class Test  {

	private LinkedList<Point>										allDirections;
	private ConcurrentHashMap<Point, MazeNode>	visit;
	// private HashedLinkedBlockingQueue<Point> visited;
	private HashMap<Point, Point>								visited;
	private Point																destination;
	private Point																origin;

	public static final boolean									DEBUG	= true;

	public Test(Point destination) {

		this.allDirections = new LinkedList<Point>();
		// acumulative delta movement for up,down,left and right
		this.allDirections.add(new Point(-1, 0));
		this.allDirections.add(new Point(1, 0));
		this.allDirections.add(new Point(0, 1));
		this.allDirections.add(new Point(0, -1));

		this.visit = new ConcurrentHashMap<Point, MazeNode>();
		this.visited = new HashMap<Point, Point>();

		// this.visited = new HashedLinkedBlockingQueue<Point>();

		this.destination = destination;
	}

	private void evaluatePoint(Point currentPoint, Point direction) {
		Point testPoint = new Point(currentPoint);
		testPoint.translate(direction.x, direction.y);

		// if node is visited already do not continue
		if (visited.containsKey(testPoint)) return;

		MazeNode proposedNode = new MazeNode(currentPoint, visit.get(currentPoint).getG(), testPoint,
				destination);
		// System.out.println(testPoint);
		// System.out.println(proposedNode);

		// will replace if it's not found already or when it found a entry, but new
		// node has better value
		if (!visit.containsKey(testPoint) || visit.get(testPoint).getF() > proposedNode.getF())
			visit.put(testPoint, proposedNode);

	}

	/**
	 * Will add starting position into maze, a maze can contain multiple starting
	 * positions. And position which will gain the shortest path will choosen.
	 * 
	 * @param origin
	 */
	public void startPosition(Point origin) {
		visit.put(origin, new MazeNode(null, 0, origin, destination));
		this.origin = origin;
	}

	public int solvePath() {
		if (origin == null) return -1;

		int iteration = 0;

		Point currentStep = origin;
		while (!currentStep.equals(destination) && visit.size() > 0) {
			currentStep = doOneStep(currentStep);
			iteration++;
		}

		if (!currentStep.equals(destination)) return -1;

		if (DEBUG) System.out.println("Took " + iteration + " iterations.");
		return iteration;
	}

	public int backTracePath() {
		if (DEBUG) {
			System.out.println("**********");
			System.out.println("Trace back");
			System.out.println("**********");
		}

		int iteration = 0;
		
		System.out.println(visited);

		
		if (!visited.containsKey(destination)) return -1;
		
		Point currentStep = destination;

		while (currentStep != null) {
			System.out.println(currentStep);
			currentStep = visited.get(currentStep);
			iteration++;
		}

		if (DEBUG) System.out.println("Path is " + iteration + " steps long.");
		return iteration;
	}

	/**
	 * Evaluates given position with all cardinal directions and then returns the
	 * best next step.
	 * 
	 * @param currentPosition
	 * @return
	 */
	private Point doOneStep(Point currentPosition) {

		// test each carduninal directions in multiple threads at once
		allDirections.parallelStream().forEach(direction -> evaluatePoint(currentPosition, direction));

		// mark this point as visited
		visited.put(currentPosition, visit.get(currentPosition).getParent());
		visit.remove(currentPosition);

		// get smallest node from not visited ones so we can use it as next move
		Entry<Point, MazeNode> min = Collections.min(visit.entrySet(), (Entry<Point, MazeNode> a,
				Entry<Point, MazeNode> b) -> a.getValue().getF().compareTo(b.getValue().getF()));

		System.out.println(min.getKey());
		return min.getKey();
	}

	public static void main(String[] args) {
		Test app = new Test(new Point(2, 2));

		app.startPosition(new Point(10, 10));

		if (app.solvePath() > 0) {
			app.backTracePath();
		} else {
			System.out.print("Can't find path (no origin, or no possible path)");
		}

	}
}
