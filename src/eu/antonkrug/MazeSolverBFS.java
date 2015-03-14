package eu.antonkrug;

/**
 * Breadth first search using FIFO queue
 * 
 * @author Anton Krug
 * @date 2015/03/10
 * @version 1
 * @requires Java 8!
 */

/* Copyright (C) Anton Krug - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Anton Krug <anton.krug@gmail.com>, February 2015
 */

import java.awt.Point;
import java.util.Arrays;
import java.util.stream.Stream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Stack;

public class MazeSolverBFS implements MazeSolver {

	public static final boolean	DEBUG	= false;
	private List<Point>					allDirections;
//	private Stack<Point>				currentPath;
	private Point								currentStep;
	private List<Point>					destinations;
	private boolean							doNotSolveAgain;
	private Maze								maze;
	private Point								origin;
	private Long								timeStart;
	private Long								timeStop;
	private Queue<Point>				visit;
	private Stack<Point>				visitedAlready;
	private Map<Point, Point>		roots;

	/**
	 * Constructor to initialise fields.
	 * 
	 * @param maze
	 *          Reqiress to be give already loaded maze
	 */
	public MazeSolverBFS(Maze maze) throws Exception {

		this.doNotSolveAgain = false;
		this.maze = maze;
		this.currentStep = null;

		this.timeStart = System.nanoTime();
		this.timeStop = this.timeStart;

		this.destinations = new LinkedList<>();

		// all cardinal direction for up,down,left and right
		this.allDirections = Arrays.asList(new Point(-1, 0), new Point(1, 0), new Point(0, 1),
				new Point(0, -1));

		this.visit = new LinkedList<>();
//		this.currentPath = new Stack<>();
		this.visitedAlready = new Stack<>();
		this.roots = new HashMap<>();

		this.addStartingAndDestionationPositions();
	}

	/**
	 * Will add one or more destinations to maze
	 * 
	 * @param destination
	 */
	@Override
	public void addDestinationPosition(Point destination) {
		if (!destinations.contains(destination)) {
			this.destinations.add(destination);
		}
	}

	/**
	 * Will add both starting and final destination points from the maze is given
	 * to this solver
	 * 
	 * @throws Exception
	 *           If there is no destination present it will throw exception
	 */
	@Override
	public void addStartingAndDestionationPositions() throws Exception {
		this.setDestinations(maze.getAllBlock(Maze.Block.FINISH));
		this.addStartingPositions(maze.getAllBlock(Maze.Block.START));
	}

	/**
	 * Will add one or more starting positions for the maze
	 * 
	 * @param starts
	 *          List of starting points
	 * @exception If
	 *              there is no destination present it will throw exception
	 */
	@Override
	public void addStartingPositions(List<Point> starts) throws Exception {
		for (Point point : starts) {
			this.addStartPosition(point);
		}
//		currentPath.push(origin);
	}

	/**
	 * Will add starting position into maze, a maze can contain multiple starting
	 * positions. And position which will gain the shortest path will choosen.
	 * 
	 * @param origin
	 * @throws Exception
	 *           If there is no destination present it will throw exception
	 */
	@Override
	public void addStartPosition(Point origin) throws Exception {
//		currentPath.push(origin);
		visit.add(origin);
		this.origin = origin;
	}

	/**
	 * Returns final solved path
	 * 
	 * @return
	 */
	@Override
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

		Optional<Point> destination = destinations.stream().filter(visitedAlready::contains)
				.findFirst();

		// if we didn't found destination do not continue
		if (!destination.isPresent()) return null;
		
		this.currentStep=destination.get();

		return backTracePathParty();
	}

	/**
	 * Returns current final path by partly solved search
	 * 
	 * @return
	 */
	@Override
	public List<Point> backTracePathParty() {
		LinkedList<Point> path= new LinkedList<>();
		
		Point nextMove=null;

		for (Point direction : allDirections) {

			Point testPoint =  addPoints(currentStep, direction);
			
			if (roots.containsKey(testPoint)) nextMove=testPoint;
		}
		

		@SuppressWarnings("unused")
		
		int iteration=0;
		while (nextMove != null) {
			if (DEBUG) System.out.println(nextMove);
			path.add(nextMove);
			nextMove = roots.get(nextMove);
			iteration++;
		}

		if (DEBUG) System.out.println("Path is " + iteration + " steps long.");			
		
		return path;
	}
	
	protected Point addPoints(Point first,Point second) {
		Point ret = new Point(first);
		ret.translate(second.x, second.y);
		return ret;
	}

	/**
	 * Evaluates given position with all cardinal directions and then returns the
	 * best next step.
	 * 
	 * @param currentPosition
	 * @return
	 */
	private Point doOneStep(Point currentPosition) {
		// mark this point as visited
		markNodeAsVisited(currentPosition);
		
		Point nextMove=null;


		// test all directions if i can move that way and I wasn't there before
		for (Point direction : allDirections) {

			Point testPoint =  addPoints(currentPosition, direction);
			
			if (maze.canWalkTo(testPoint) && !visitedAlready.contains(testPoint) && !visit.contains(testPoint)) {
				visit.add(testPoint);
				roots.put(testPoint, currentPosition);
			}
		}
		
//		currentPath.push(nextMove);
		nextMove=visit.peek();

		if (DEBUG) System.out.println(nextMove);
		return nextMove;
	}

	/**
	 * Will return Approach of this implementation
	 * 
	 * @return
	 */
	@Override
	public Aproach getAproach() {
		return Aproach.BFS_STACK;
	}

	/**
	 * Gets the current step position inside the solver
	 * 
	 * @return the currentStep
	 */
	@Override
	public Point getCurrentStep() {
		return currentStep;
	}

	/**
	 * Returns all given destinations
	 * 
	 * @return the destinations
	 */
	@Override
	public List<Point> getDestinations() {
		return destinations;
	}

	/**
	 * Returns open list
	 * 
	 * @return the visit
	 */
	@Override
	public Stream<Point> getVisit() {
		return visit.stream();
	}

	/**
	 * Returns null so alternative can be called
	 * 
	 * @return null
	 */
	@Override
	public Map<Point, Point> getVisitedAlready() {
		return null;
	}

	/**
	 * If it's unconviet to return closed list as map, you can return it as stream
	 * with this method.
	 * 
	 * @return
	 */
	@Override
	public Stream<Point> getVisitedAlreadyAlternative() {
		return visitedAlready.stream();
	}

	/**
	 * Returns size of the closed list
	 * 
	 * @return
	 */
	@Override
	public int getVisitedAlreadySize() {
		return visitedAlready.size();
	}

	/**
	 * Returns size of the open list
	 * 
	 * @return
	 */
	@Override
	public int getVisitSize() {
		return visit.size();
	}

	/**
	 * Return flag if the alrgorithm is allowed to see the destination
	 * 
	 * @return the destinationVisible
	 */
	@Override
	public boolean isDestinationVisible() {
		return false;
	}

	/**
	 * Flag if algorithm is solved (with solution or not) and locked for any new
	 * solving attempt.
	 * 
	 * @return the doNotSolveAgain
	 */
	@Override
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
			// add it to visited list and then removed it from visit list
		if (visit.size()>0) {
			visitedAlready.push(index);
			visit.remove(index);
		}
	}

	/**
	 * Set all destinations to given list.
	 * 
	 * @param destinations
	 */
	@Override
	public void setDestinations(List<Point> destinations) {
		this.destinations = destinations;
	}

	/**
	 * Flag if algorithm is allowed to see destination
	 * 
	 * @param destinationVisible
	 *          the destinationVisible to set
	 */
	@Override
	public void setDestinationVisible(boolean destinationVisible) {
		// destination visibility ignored for this implementation
	}

	/**
	 * Will attempt to find path from start to finish
	 * 
	 * @return
	 */
	@Override
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
	@Override
	public boolean solveStepCondition() {
		return !destinations.contains(currentStep) && visit.size() > 0;
	}

	/**
	 * Set current step to null
	 * 
	 * @return
	 */
	@Override
	public boolean solveStepDidntStarted() {
		return currentStep == null;
	}

	/**
	 * If solver is finished, do final checks and cleanup
	 * 
	 * @return
	 */
	@Override
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
	@Override
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
	@Override
	public void solveStepOneIteration() {
		currentStep = doOneStep(currentStep);
	}

	/**
	 * Returns measured time between the solver was started, till it found
	 * solution
	 * 
	 * @return
	 */
	@Override
	public long timeTaken() {
		return (timeStop - timeStart) / 1000000;
	}
}
