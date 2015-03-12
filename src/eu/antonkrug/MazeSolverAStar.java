package eu.antonkrug;

/**
 * Single threaded, multithread and fastutil implementation of A* using 
 * hash maps as main datastructure to hold open and closed lists.
 * 
 * @author Anton Krug
 * @date 2015/02/22
 * @version 1.3
 * @requires Java 8!
 */

/* Copyright (C) Anton Krug - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Anton Krug <anton.krug@gmail.com>, February 2015
 */

import java.awt.Point;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;

//import net.openhft.koloboke.collect.map.hash.HashObjObjMaps;

//import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class MazeSolverAStar implements MazeSolver {

	public enum Aproach {
		JDK_HASHMAP, JDK_CONCURENT_HASHMAP, 
		// KOLOBOKE, 
		//FASTUTIL_HASHMAP
		;
	}

	public static final boolean		DEBUG	= false;
	private List<Point>						allDirections;
	private Point									currentStep;
	private List<Point>						destinations;
	private boolean								destinationVisible;
	private boolean								doNotSolveAgain;
	private Maze									maze;
	private Point									origin;
	private Long									timeStart;
	private Long									timeStop;
	private Map<Point, AStartNode>	visit;
	private Map<Point, Point>			visitedAlready;
	private Aproach								implementationAproach;

	/**
	 * Constructor to initialise fields.
	 * 
	 * @param maze
	 *          Reqiress to be give already loaded maze
	 */
	public MazeSolverAStar(Maze maze, Aproach implementationAproach) throws Exception {

		this.destinationVisible = true;
		this.doNotSolveAgain = false;
		this.maze = maze;
		this.currentStep = null;

		this.timeStart = System.nanoTime();
		this.timeStop = this.timeStart;

		this.destinations = new LinkedList<>();

		// all cardinal direction for up,down,left and right
		this.allDirections = Arrays.asList(new Point(-1, 0), new Point(1, 0), new Point(0, 1),
				new Point(0, -1));

		this.implementationAproach = implementationAproach;

		switch (implementationAproach) {
			case JDK_HASHMAP:
				this.visit = new HashMap<>();
				this.visitedAlready = new HashMap<>();
				break;

			case JDK_CONCURENT_HASHMAP:
				this.visit = new ConcurrentHashMap<>();
				this.visitedAlready = new HashMap<>();
				break;

//			case KOLOBOKE:
//			  this.visit=HashObjObjMaps.getDefaultFactory().withNullKeyAllowed(false).<Point, MazeNode>newMutableMap();
//				this.visitedAlready = new HashMap<>();
//				break;

//			case FASTUTIL_HASHMAP:
//				this.visit = new Object2ObjectOpenHashMap<>();
//				this.visitedAlready = new Object2ObjectOpenHashMap<>();
//				break;

			default:
				throw new Exception("Not valid aproach selected");
		}

		this.addStartingAndDestionationPositions();
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

	/**
	 * Will add one or more starting positions for the maze
	 * 
	 * @param starts
	 *          List of starting points
	 * @exception If
	 *              there is no destination present it will throw exception
	 */
	public void addStartingPositions(List<Point> starts) throws Exception {
		for (Point point : starts) {
			this.addStartPosition(point);
		}
	}

	/**
	 * Will add both starting and final destination points from the maze is given
	 * to this solver
	 * 
	 * @throws Exception
	 *           If there is no destination present it will throw exception
	 */
	public void addStartingAndDestionationPositions() throws Exception {
		this.setDestinations(maze.getAllBlock(Maze.Block.FINISH));
		this.addStartingPositions(maze.getAllBlock(Maze.Block.START));
	}

	/**
	 * Will add starting position into maze, a maze can contain multiple starting
	 * positions. And position which will gain the shortest path will choosen.
	 * 
	 * @param origin
	 * @throws Exception
	 *           If there is no destination present it will throw exception
	 */
	public void addStartPosition(Point origin) throws Exception {
		visit.put(origin, new AStartNode(null, 0, origin, destinations));
		this.origin = origin;
	}

	/**
	 * Returns final solved path
	 * 
	 * @return
	 */
	public List<Point> backTracePath() {
		if (DEBUG) {
			System.out.println("**********");
			System.out.println("Trace back");
			System.out.println("**********");
		}

		@SuppressWarnings("unused")
		int iteration = 0;

		// try to find between destination points a point which was visited.
		// ie: find out if we "visited destination" aka "found destination"

		Optional<Point> destination;
		switch (implementationAproach) {
			case JDK_CONCURENT_HASHMAP:
				destination = destinations.parallelStream().filter(visitedAlready::containsKey).findFirst();
				break;

			default:
				destination = destinations.stream().filter(visitedAlready::containsKey).findFirst();
				break;
		}

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
	
	public List<Point> backTracePathParty() {
		Point currentStep = this.currentStep;
		for (Point direction: allDirections) {
			Point checkPoint = new Point(currentStep);
			checkPoint.translate(direction.x, direction.y);
			if (visitedAlready.containsKey(checkPoint)) {
				currentStep=checkPoint;
			}
		}
		LinkedList<Point> path = new LinkedList<>();

		while (currentStep != null) {
			if (DEBUG) System.out.println(currentStep);
			path.add(currentStep);
			currentStep = visitedAlready.get(currentStep);
		}
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

		switch (implementationAproach) {
			case JDK_CONCURENT_HASHMAP:
				allDirections.parallelStream().forEach(
						direction -> evaluatePoint(currentPosition, direction));
				break;

			default:
				allDirections.stream().forEach(direction -> evaluatePoint(currentPosition, direction));
				break;
		}

		// mark this point as visited
		markNodeAsVisited(currentPosition);

		Entry<Point, AStartNode> min = null;

		// Check if we just didn't deleted the very last point in the visit list
		if (visit.size() > 0) {

			// Depending if I'm allowed to see destination or not. The heurestics (H
			// value) is knowledge of the destination and F=G+H so getting G value
			// instead of F will ignore the heurestic part and will behave like it
			// doesn't know the destination
			if (destinationVisible) {

				// get smallest node from not visited ones so we can use it as next move
				min = Collections.min(visit.entrySet(),
						(a, b) -> a.getValue().getF().compareTo(b.getValue().getF()));
			} else {

				// get smallest node from not visited ones so we can use it as next move
				min = Collections.min(visit.entrySet(),
						(a, b) -> a.getValue().getG().compareTo(b.getValue().getG()));
			}
		} else {
			return null;
		}

		if (DEBUG) System.out.println(min.getKey());
		return min.getKey();
	}

	/**
	 * Checks given cardinal direction and calculates or required fields for given
	 * node, it will check if it should be put into open list or not.
	 * 
	 * @param currentPoint
	 * @param direction
	 */
	private void evaluatePoint(Point currentPoint, Point direction) {
		Point testPoint = new Point(currentPoint);
		testPoint.translate(direction.x, direction.y);

		// if node is visited already do not continue
		if (visitedAlready.containsKey(testPoint)) return;

		// if you can't walk on that block then do not continue
		if (!maze.canWalkTo(testPoint)) return;

		try {
			AStartNode proposedNode = new AStartNode(currentPoint, visit.get(currentPoint).getG(), testPoint,
					destinations);

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
	 * Gets the current step position inside the solver
	 * 
	 * @return the currentStep
	 */
	public Point getCurrentStep() {
		return currentStep;
	}

	/**
	 * Returns all given destinations
	 * 
	 * @return the destinations
	 */
	public List<Point> getDestinations() {
		return destinations;
	}

	/**
	 * Returns open list
	 * 
	 * @return the visit
	 */
	public Map<Point, AStartNode> getVisit() {
		return visit;
	}

	/**
	 * Returns closed list
	 * 
	 * @return the visitedAlready
	 */
	public Map<Point, Point> getVisitedAlready() {
		return visitedAlready;
	}

	/**
	 * Return flag if the alrgorithm is allowed to see the destination
	 * 
	 * @return the destinationVisible
	 */
	public boolean isDestinationVisible() {
		return destinationVisible;
	}

	/**
	 * Flag if algorithm is solved (with solution or not) and locked for any new
	 * solving attempt.
	 * 
	 * @return the doNotSolveAgain
	 */
	public boolean isDoNotSolveAgain() {
		return doNotSolveAgain;
	}

	/**
	 * Move a position from open list to closed list
	 * 
	 * @param index
	 */
	private void markNodeAsVisited(Point index) {
		// check if it's not removed from visited list already
		if (visit.containsKey(index)) {
			// add it to visited list and then removed it from visit list
			visitedAlready.put(index, visit.get(index).getParent());
			visit.remove(index);
		}
	}

	/**
	 * Set all destinations to given list.
	 * 
	 * @param destinations
	 */
	public void setDestinations(List<Point> destinations) {
		this.destinations = destinations;
	}

	/**
	 * Flag if algorithm is allowed to see destination
	 * 
	 * @param destinationVisible
	 *          the destinationVisible to set
	 */
	public void setDestinationVisible(boolean destinationVisible) {
		this.destinationVisible = destinationVisible;
	}

	/**
	 * Will attempt to find path from start to finish
	 * 
	 * @return
	 */
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

	/**
	 * Condition which will be checked in each step
	 * 
	 * @return
	 */
	public boolean solveStepCondition() {
		return !destinations.contains(currentStep) && visit.size() > 0;
	}

	/**
	 * Set current step to null
	 * 
	 * @return
	 */
	public boolean solveStepDidntStarted() {
		return currentStep == null;
	}

	/**
	 * If solver is finished, do final checks and cleanup
	 * 
	 * @return
	 */
	public int solveStepFinish() {
		this.timeStop = System.nanoTime();

		doNotSolveAgain = true;

		if (!destinations.contains(currentStep)) return -1;

		// last step, when destination and current step are the same, we will flag
		// which destionation we reached
		markNodeAsVisited(currentStep);

		return 0;
	}

	/**
	 * Called before solver can do each step
	 * 
	 * @return
	 */
	public int solveStepInit() {
		if (origin == null || doNotSolveAgain) {
			doNotSolveAgain = true;
			return -1;
		}
		this.timeStart = System.nanoTime();
		currentStep = origin;

		return 0;
	}

	/**
	 * If solveStepCondition() returns true you can do one step iteration
	 */
	public void solveStepOneIteration() {
		currentStep = doOneStep(currentStep);
	}

	/**
	 * Returns measured time between the solver was started, till it found
	 * solution
	 * 
	 * @return
	 */
	public long timeTaken() {
		return (timeStop - timeStart) / 1000000;
	}
}
