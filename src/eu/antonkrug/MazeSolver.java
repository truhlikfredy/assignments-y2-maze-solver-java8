package eu.antonkrug;

import java.awt.Point;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author Anton Krug
 * @date 2015/02/22
 * @version 1
 * @requires Java 8!
 * 
 */
public class MazeSolver {

	private LinkedList<Point>										allDirections;
	private ConcurrentHashMap<Point, MazeNode>	visit;
	private HashMap<Point, Point>								visitedAlready;
	private LinkedList<Point>										destinations;
	// private Point destination;
	private Point																origin;
	private Maze																maze;
	private boolean															doNotSolveAgain;
	private Point																currentStep;

	public static final boolean									DEBUG	= false;

	public MazeSolver(Maze maze) {

		this.doNotSolveAgain = false;
		this.maze = maze;
		this.currentStep = null;

		this.destinations = new LinkedList<>();

		// this.allDirections = Collections.EMPTY_LIST;
		this.allDirections = new LinkedList<>();
		// all cardinal direction for up,down,left and right
		this.allDirections.add(new Point(-1, 0));
		this.allDirections.add(new Point(1, 0));
		this.allDirections.add(new Point(0, 1));
		this.allDirections.add(new Point(0, -1));

		this.visit = new ConcurrentHashMap<>();
		this.visitedAlready = new HashMap<>();
	}

	public void setDestinations(LinkedList<Point> destinations) {
		this.destinations = destinations;
	}

	/**
	 * @return the visit
	 */
	public ConcurrentHashMap<Point, MazeNode> getVisit() {
		return visit;
	}

	/**
	 * @return the visitedAlready
	 */
	public HashMap<Point, Point> getVisitedAlready() {
		return visitedAlready;
	}

	/**
	 * @return the destinations
	 */
	public LinkedList<Point> getDestinations() {
		return destinations;
	}

	public void addStartingPositions(LinkedList<Point> starts) throws Exception {
		for (Point point : starts) {
			this.addStartPosition(point);
		}
	}

	private void evaluatePoint(Point currentPoint, Point direction) {
		Point testPoint = new Point(currentPoint);
		testPoint.translate(direction.x, direction.y);

		// if node is visited already do not continue
		if (visitedAlready.containsKey(testPoint)) return;

		// if you can't walk on that block then do not continue
		if (!maze.canWalkTo(testPoint)) return;

		try {
			MazeNode proposedNode = new MazeNode(currentPoint, visit.get(currentPoint).getG(), testPoint,
					destinations);

			// System.out.println(testPoint);
			// System.out.println(proposedNode);

			// will replace if it's not found already or when it found a entry, but
			// new node has better value. i.e. always put new/replace entry unless it
			// has already best one
			if (!visit.containsKey(testPoint) || visit.get(testPoint).getF() > proposedNode.getF())
				visit.put(testPoint, proposedNode);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Will add one or more destinations to maze
	 * 
	 * @param destination
	 */
	public void addDestinationPosition(Point destination) {
		if (!destinations.contains(destination)) {
			this.destinations.add(destination);
		}
	}

	public void messg() {

	}

	/**
	 * Will add starting position into maze, a maze can contain multiple starting
	 * positions. And position which will gain the shortest path will choosen.
	 * 
	 * @param origin
	 */
	public void addStartPosition(Point origin) throws Exception {
		visit.put(origin, new MazeNode(null, 0, origin, destinations));
		this.origin = origin;
	}

	public int solvePath() {

		int iteration = 0;

		if (solveStepInit() < 0) return -1;

		while (solveStepCondition()) {
			solveStepOneIteration();
			iteration++;
		}

		if (solveStepFinish() < 0) return -1;

		if (DEBUG) System.out.println("Took " + iteration + " iterations.");

		return iteration;
	}
	
	public boolean solveStepDidntStarted() {
		return currentStep==null;
	}

	public int solveStepInit() {
		if (origin == null || doNotSolveAgain) {
			doNotSolveAgain=true;
			return -1;
		}
		currentStep = origin;
		return 0;
	}

	public boolean solveStepCondition() {
		return !destinations.contains(currentStep) && visit.size() > 0;
	}

	/**
	 * @return the doNotSolveAgain
	 */
	public boolean isDoNotSolveAgain() {
		return doNotSolveAgain;
	}

	public void solveStepOneIteration() {
		currentStep = doOneStep(currentStep);
	}

	public int solveStepFinish() {
		doNotSolveAgain = true;

		if (!destinations.contains(currentStep)) return -1;

		// last step, when destination and current step are the same, we will flag
		// which destionation we reached
		markNodeAsVisited(currentStep);

		return 0;
	}

	public LinkedList<Point> backTracePath() {
		if (DEBUG) {
			System.out.println("**********");
			System.out.println("Trace back");
			System.out.println("**********");
		}

		@SuppressWarnings("unused")
		int iteration = 0;

		// try to find between destination points a point which was visited.
		// ie: find out if we "visited destination" aka "found destination"
		Optional<Point> destination = destinations.parallelStream().filter(visitedAlready::containsKey)
				.findFirst();

		// if we didn't found destination do not continue
		if (!destination.isPresent()) return null;

		LinkedList<Point> path = new LinkedList<>();

		Point currentStep = destination.get();

		while (currentStep != null) {
			if (DEBUG) System.out.println(currentStep);
			path.add(currentStep);
			currentStep = visitedAlready.get(currentStep);
			iteration++;
		}

		if (DEBUG) System.out.println("Path is " + iteration + " steps long.");
		return path;
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
		markNodeAsVisited(currentPosition);

		// get smallest node from not visited ones so we can use it as next move
		Entry<Point, MazeNode> min = Collections.min(visit.entrySet(), (Entry<Point, MazeNode> a,
				Entry<Point, MazeNode> b) -> a.getValue().getF().compareTo(b.getValue().getF()));

		if (DEBUG) System.out.println(min.getKey());
		return min.getKey();
	}

	private void markNodeAsVisited(Point index) {
		// check if it's not removed from visited list already
		if (visit.containsKey(index)) {
			// add it to visited list and then removed it from visit list
			visitedAlready.put(index, visit.get(index).getParent());
			visit.remove(index);
		}
	}
}
