package eu.antonkrug;

import java.awt.Point;
import java.util.stream.Stream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
//import java.util.Queue;
//import java.util.Stack;


import utils.Agenda;
import utils.AgendaJdk;
import utils.AgendaJdk.Function;
import utils.AgendaQueue;

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
public class MazeSolverBfs extends MazeSolverBase {

	private Agenda<Point>			visit;
	private Agenda<Point>			visitedAlready;

	// not needed for algorithm, but it makes GUI more pretty
	private Map<Point, Point>	parents;

	/**
	 * Constructor to initialise fields.
	 * 
	 * @param maze
	 *          Reqiress to be give already loaded maze
	 */
	public MazeSolverBfs(Maze maze, Aproach aproach) throws Exception {
		super(maze, aproach);

		// not used, but if in future this implementation would be asked if it can
		// see the destination then it would return correct value
		this.destinationVisible = false;

		// as FIFO queue linked list is used
//		this.visit = new LinkedList<>();
//		this.visitedAlready = new Stack<>();
		

		switch (aproach) {
			case BFS_QUEUE_JDK:
				this.visit = new AgendaJdk<>(Function.QUEUE);
				this.visitedAlready = new AgendaJdk<>(Function.QUEUE); 				
				break;
				
			case BFS_QUEUE_MINE:
				this.visit = new AgendaQueue<>();
				this.visitedAlready = new AgendaQueue<>(); 
				break;

			default:
				throw new Exception("Usuported aproach "+aproach+" called with this solver");
		}

		// not needed for the search itself, but gui wants to display higlighted
		// which path is currently investigated pointing from the current step to
		// the starting point, to which map of parents is needed. so we can look up
		// which point has parent to which direction
		this.parents = new HashMap<>();

		this.addStartingAndDestionationPositions();
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
		// currentPath.push(origin);
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

		// this.currentStep = destination.get();

		return backTraceFromPoint(destination.get());
	}

	/**
	 * Returns current final path by partly solved search
	 * 
	 * @return
	 */
	@Override
	public List<Point> backTracePathPartially() {

		Point nextMove = null;
		// try find traced path near the last position
		for (Point direction : allDirections) {

			Point testPoint = pointsTranslate(currentStep, direction);

			if (parents.containsKey(testPoint)) nextMove = testPoint;
		}

		// traverse the path till start reached
		List<Point> path = backTraceFromPoint(nextMove);

		if (DEBUG) System.out.println("Path is " + path.size() + " steps long.");

		return path;
	}

	/**
	 * Will begin at the destination point and step back till starting point is
	 * reached
	 * 
	 * @param destinationPoint
	 * @return Path which is betwen start poistion and destinaitonPoint
	 */
	private List<Point> backTraceFromPoint(Point destinationPoint) {

		LinkedList<Point> path = new LinkedList<>();

		Point currentStep = destinationPoint;

		// traverse the path till start reached
		while (currentStep != null) {
			if (DEBUG) System.out.println(currentStep);
			path.add(currentStep);
			currentStep = parents.get(currentStep);
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
	@Override
	protected Point doOneStep(Point currentPosition) {
		// mark this point as visited
		markNodeAsVisited(currentPosition);

		Point nextMove = null;

		// test all directions if i can move that way and I wasn't there before
		for (Point direction : allDirections) {

			Point testPoint = pointsTranslate(currentPosition, direction);

			if (maze.canWalkTo(testPoint) && !visitedAlready.contains(testPoint)
					&& !visit.contains(testPoint)) {
				visit.add(testPoint);
				parents.put(testPoint, currentPosition);
			}
		}

		// currentPath.push(nextMove);
		nextMove = visit.peek();

		if (DEBUG) System.out.println(nextMove);
		return nextMove;
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
	 * If it's unconviet to return closed list as map, you can return it as stream
	 * with this method. And becuase getVisitedAlready was not overiden the GUI
	 * will use this method instead
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
	 * Move a position from open list to closed list
	 * 
	 * @param index
	 */
	@Override
	protected void markNodeAsVisited(Point index) {
		// check if it's not removed from visited list already
		// add it to visited list and then removed it from visit list
		if (visit.size() > 0) {
			visitedAlready.push(index);
			visit.remove(index);
		}
	}

}
