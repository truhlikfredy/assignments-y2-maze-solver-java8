package eu.antonkrug;

import java.awt.Point;
import java.util.stream.Stream;
import java.util.AbstractList;
import java.util.List;
import java.util.Optional;

import utils.Agenda;
import utils.AgendaJdk;
import utils.AgendaStack;
import utils.AgendaJdk.Function;

/**
 * Depth first search using stacks
 * 
 * @author Anton Krug
 * @date 2015/03/10
 * @version 1
 * @requires Java 8!
 */

/*
 * Copyright (C) Anton Krug - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 * Written by Anton Krug <anton.krug@gmail.com>, February 2015
 */
public class MazeSolverDfs extends MazeSolverBase {

	private Agenda<Point>	visit;
	private Agenda<Point>	visitedAlready;

	// not required as such for algorithm but without it GUI woudn't be able
	// display current path which is the algorithm traversing
	private Agenda<Point>	currentPath;

	/**
	 * Constructor to initialise fields.
	 * 
	 * @param maze
	 *          Reqiress to be give already loaded maze
	 */
	public MazeSolverDfs(Maze maze, Aproach aproach) throws Exception {
		super(maze, aproach);

		// not used, but if in future this implementation would be asked if it can
		// see the destination then it would return correct value
		this.destinationVisible = false;

		
//		this.visit = new Stack<>();
//		this.currentPath = new Stack<>();
//		this.visitedAlready = new Stack<>();
		
		switch (aproach) {
			case DFS_STACK_JDK:
				this.visit = new AgendaJdk<>(Function.STACK);
				this.visitedAlready = new AgendaJdk<>(Function.STACK);
				this.currentPath = new AgendaJdk<>(Function.STACK);
				break;
				
			case DFS_STACK_MINE:
				this.visit = new AgendaStack<>();
				this.visitedAlready = new AgendaStack<>(); 
				this.currentPath = new AgendaStack<>();
				break;

			default:
				throw new Exception("Usuported aproach "+aproach+" called with this solver");
		}
		

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

		return backTracePathPartially();
	}

	/**
	 * Returns current final path by partly solved search
	 * 
	 * @return
	 */
	@Override
	public AbstractList<Point> backTracePathPartially() {
//		return new LinkedList<>(currentPath);
		return currentPath.getList();
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

		// check if dead end and then backtrack
		nextMove=backTrack(nextMove);

		if (DEBUG) System.out.println(nextMove);
		return nextMove;
	}
	
	/**
	 * In case dead end was reached do backtracking
	 * 
	 * @param nextMove
	 * @return
	 */
	private Point backTrack(Point nextMove) {
		if (nextMove == null) {

			currentPath.pop();

			if (currentPath.size() > 0) {
				// let's backtrack the curennt path
				nextMove = currentPath.peek();
			} else {
				// there is not much left, back track slowly and take any other path
				if (visit.size()>0) {
					nextMove=visit.peek();
					currentPath.push(nextMove);
				}
			} 
			
		} else {
			// if something was found (no deadend) display is as current path
			currentPath.push(nextMove);
		}		
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
//			if (!currentPath.contains(index)) currentPath.push(index);
		}
	}

}
