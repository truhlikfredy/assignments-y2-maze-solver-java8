package eu.antonkrug;

/**
 * Depth first search using stacks 
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

public class MazeSolverDFS extends MazeSolverBase {

	private Stack<Point>				currentPath;
	private Stack<Point>				visit;
	private Stack<Point>				visitedAlready;

	/**
	 * Constructor to initialise fields.
	 * 
	 * @param maze
	 *          Reqiress to be give already loaded maze
	 */
	public MazeSolverDFS(Maze maze) throws Exception {
		super(maze, Aproach.DFS_STACK);
		
		// not used, but if in future this implementation would be asked if it can
		// see the destination then it would return correct value
		this.destinationVisible = false;
		

		this.visit = new Stack<>();
		this.currentPath = new Stack<>();
		this.visitedAlready = new Stack<>();

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
		currentPath.push(origin);
		visit.push(origin);
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

		LinkedList<Point> path = new LinkedList<>(currentPath);

		return path;
	}

	/**
	 * Returns current final path by partly solved search
	 * 
	 * @return
	 */
	@Override
	public List<Point> backTracePathParty() {
		return new LinkedList<>(currentPath);
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

			Point testPoint = new Point(currentPosition);
			testPoint.translate(direction.x, direction.y);

			if (maze.canWalkTo(testPoint) && !visitedAlready.contains(testPoint)) {
				nextMove = testPoint;
				// do not add duplicates
				if (!visit.contains(testPoint)) visit.push(testPoint);
			}
		}
		// TODO This method picked just first possible point, even when it's
		// changing direction. Interesting would be to pick point which continues in
		// the previous direction and if that not possible then any point. This
		// would change behaviour in open fields.

		// if dead end backtrack
		if (nextMove == null) {

			if (currentPath.size() > 1) {
				// but do not return this step as curent step, return the next step
				// back as next step because we are revertsing now
				currentPath.pop();
				nextMove = currentPath.peek();

			} else if (currentPath.size() > 2) {
				// there is not much left, back track slowly
				nextMove = currentPath.pop();

			} else {
				return null;
			}
		} else {
			// if something was found (no deadend) display is as current path
			currentPath.push(nextMove);
		}

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
	 * with this alternative method.
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
		if (visit.contains(index)) {
			// add it to visited list and then removed it from visit list
			visitedAlready.push(index);
			visit.remove(index);
		}
	}

	/**
	 * Condition which will be checked in each step
	 * 
	 * @return
	 */
	@Override
	public boolean solveStepCondition() {
		return !destinations.contains(currentStep) && currentPath.size() > 0;
	}

}
