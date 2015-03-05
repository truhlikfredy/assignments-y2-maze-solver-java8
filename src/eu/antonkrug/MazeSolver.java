package eu.antonkrug;

/**
 * 
 * @author Anton Krug
 * @date 2015/02/22
 * @version 1
 * @requires Java 8!
 * 
 */

/* Copyright (C) Anton Krug - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Anton Krug <anton.krug@gmail.com>, February 2015
 */

import java.awt.Point;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class MazeSolver {

	public static final boolean	DEBUG	= false;
	private List<Point>										allDirections;
	private Point																currentStep;
	private LinkedList<Point>										destinations;
	private boolean															destinationVisible;
	private boolean															doNotSolveAgain;
	private Maze																maze;
	private Point																origin;
	private Long																timeStart;
	private Long																timeStop;
	private ConcurrentHashMap<Point, MazeNode>	visit;

	private HashMap<Point, Point>								visitedAlready;

	public MazeSolver(Maze maze) {

		this.destinationVisible = true;
		this.doNotSolveAgain = false;
		this.maze = maze;
		this.currentStep = null;

		this.timeStart = System.nanoTime();
		this.timeStop = this.timeStart;

		this.destinations = new LinkedList<>();

		// this.allDirections = Collections.EMPTY_LIST;

		// all cardinal direction for up,down,left and right
		this.allDirections = Arrays.asList(new Point(-1, 0), new Point(1, 0), new Point(0, 1),
				new Point(0, -1));

		//TODO clean up
		
//		this.allDirections = new LinkedList<>();
//		// all cardinal direction for up,down,left and right
//		this.allDirections.add(new Point(-1, 0));
//		this.allDirections.add(new Point(1, 0));
//		this.allDirections.add(new Point(0, 1));
//		this.allDirections.add(new Point(0, -1));

		this.visit = new ConcurrentHashMap<>();
		this.visitedAlready = new HashMap<>();
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

	public void addStartingPositions(LinkedList<Point> starts) throws Exception {
		for (Point point : starts) {
			this.addStartPosition(point);
		}
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

		// Entry<Point, MazeNode> min = Collections.min(visit.entrySet(),
		// (Entry<Point, MazeNode> a, Entry<Point, MazeNode> b) ->
		// a.getValue().getF().compareTo(b.getValue().getF()));

		//depending if I'm allowed to see destination or not. The heurestics (H value) is
		//knowledge of the destination and F=G+H so getting G value instead of F will ignore
		//the heurestic part and will behave like it doesn't know the destination
		Entry<Point, MazeNode> min;
		if (destinationVisible) {
			min = Collections.min(visit.entrySet(),
					(a, b) -> a.getValue().getF().compareTo(b.getValue().getF()));
		} else {
			min = Collections.min(visit.entrySet(),
					(a, b) -> a.getValue().getG().compareTo(b.getValue().getG()));
		}

		if (DEBUG) System.out.println(min.getKey());
		return min.getKey();
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
	 * @return the currentStep
	 */
	public Point getCurrentStep() {
		return currentStep;
	}

	/**
	 * @return the destinations
	 */
	public LinkedList<Point> getDestinations() {
		return destinations;
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
	 * @return the destinationVisible
	 */
	public boolean isDestinationVisible() {
		return destinationVisible;
	}

	/**
	 * @return the doNotSolveAgain
	 */
	public boolean isDoNotSolveAgain() {
		return doNotSolveAgain;
	}

	private void markNodeAsVisited(Point index) {
		// check if it's not removed from visited list already
		if (visit.containsKey(index)) {
			// add it to visited list and then removed it from visit list
			visitedAlready.put(index, visit.get(index).getParent());
			visit.remove(index);
		}
	}

	public void setDestinations(LinkedList<Point> destinations) {
		this.destinations = destinations;
	}
	
	/**
	 * @param destinationVisible the destinationVisible to set
	 */
	public void setDestinationVisible(boolean destinationVisible) {
		this.destinationVisible = destinationVisible;
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

	public boolean solveStepCondition() {
		return !destinations.contains(currentStep) && visit.size() > 0;
	}

	public boolean solveStepDidntStarted() {
		return currentStep == null;
	}

	public int solveStepFinish() {
		this.timeStop = System.nanoTime();

		doNotSolveAgain = true;

		if (!destinations.contains(currentStep)) return -1;

		// last step, when destination and current step are the same, we will flag
		// which destionation we reached
		markNodeAsVisited(currentStep);

		return 0;
	}

	public int solveStepInit() {
		if (origin == null || doNotSolveAgain) {
			doNotSolveAgain = true;
			return -1;
		}
		this.timeStart = System.nanoTime();
		currentStep = origin;

		return 0;
	}

	public void solveStepOneIteration() {
		currentStep = doOneStep(currentStep);
	}

	public long timeTaken() {
		return (timeStop - timeStart) / 1000000;
	}
}
