package eu.antonkrug;

import java.awt.Point;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;

//import net.openhft.koloboke.collect.map.hash.HashObjObjMaps;

//import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

/**
 * Single threaded, multithread, koloboke and fastutil implementation of A* using 
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
public class MazeSolverAStar extends MazeSolverBase {

	private Map<Point, AStartNode>	visit;
	private Map<Point, Point>				visitedAlready;

	/**
	 * Constructor to initialise fields.
	 * 
	 * @param maze
	 *          Reqiress to be give already loaded maze
	 */
	public MazeSolverAStar(Maze maze, Aproach implementationAproach) throws Exception {

		super(maze, implementationAproach);

		switch (implementationAproach) {
			case ASTAR_HASHMAP:
				this.visit = new HashMap<>();
				this.visitedAlready = new HashMap<>();
				break;

			case ASTAR_CONCURENT_HASHMAP:
				this.visit = new ConcurrentHashMap<>();
				this.visitedAlready = new HashMap<>();
				break;

			// case KOLOBOKE:
			// this.visit=HashObjObjMaps.getDefaultFactory().withNullKeyAllowed(false).<Point,
			// MazeNode>newMutableMap();
			// this.visitedAlready = new HashMap<>();
			// break;

			// case FASTUTIL_HASHMAP:
			// this.visit = new Object2ObjectOpenHashMap<>();
			// this.visitedAlready = new Object2ObjectOpenHashMap<>();
			// break;

			default:
				throw new Exception("Not valid aproach selected");
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
		visit.put(origin, new AStartNode(null, 0, origin, destinations));
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

		Optional<Point> destination;
		switch (implementationAproach) {
			case ASTAR_CONCURENT_HASHMAP:
				destination = destinations.parallelStream().filter(visitedAlready::containsKey).findFirst();
				break;

			default:
				destination = destinations.stream().filter(visitedAlready::containsKey).findFirst();
				break;
		}

		// if we didn't found destination do not continue
		if (!destination.isPresent()) return null;

		List<Point> path = backTraceFromPoint(destination.get());

		if (DEBUG) System.out.println("Path is " + path.size() + " steps long.");
		return path;
	}

	/**
	 * Returns current final path by partly solved search
	 * 
	 * @return
	 */
	@Override
	public List<Point> backTracePathPartially() {
		
		//try find traced path near the last position
		Point currentStep = this.currentStep;
		for (Point direction : allDirections) {

			Point checkPoint = pointsTranslate(currentStep, direction);

			if (visitedAlready.containsKey(checkPoint)) {
				currentStep = checkPoint;
			}
		}
		
		return backTraceFromPoint(currentStep);
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

		//traverse the path till start reached
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
	@Override
	protected Point doOneStep(Point currentPosition) {

		// test each carduninal directions in multiple threads at once

		switch (implementationAproach) {
			case ASTAR_CONCURENT_HASHMAP:
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
		Point testPoint = pointsTranslate(currentPoint, direction);

		// if node is visited already do not continue
		if (visitedAlready.containsKey(testPoint)) return;

		// if you can't walk on that block then do not continue
		if (!maze.canWalkTo(testPoint)) return;

		try {
			AStartNode proposedNode = new AStartNode(currentPoint, visit.get(currentPoint).getG(),
					testPoint, destinations);

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
	 * Returns open list
	 * 
	 * @return the visit
	 */
	@Override
	public Stream<Point> getVisit() {
		return visit.keySet().stream();
	}

	/**
	 * Returns open list size
	 * 
	 * @return the visit size
	 */
	@Override
	public int getVisitSize() {
		return visit.size();
	}

	/**
	 * Returns closed list
	 * 
	 * @return the visitedAlready
	 */
	@Override
	public Map<Point, Point> getVisitedAlready() {
		return visitedAlready;
	}

	@Override
	public int getVisitedAlreadySize() {
		return visitedAlready.size();
	}

	/**
	 * Move a position from open list to closed list
	 * 
	 * @param index
	 */
	@Override
	protected void markNodeAsVisited(Point index) {
		// check if it's not removed from visited list already
		if (visit.containsKey(index)) {
			// add it to visited list and then removed it from visit list
			visitedAlready.put(index, visit.get(index).getParent());
			visit.remove(index);
		}
	}

}
