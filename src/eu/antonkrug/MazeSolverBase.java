package eu.antonkrug;

import java.awt.Point;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Base containing some defualt methods which all implementations have mostly in
 * common (if some differences are needed, still they can be overwritten) hash
 * maps as main datastructure to hold open and closed lists.
 * 
 * If something very radical is needed, users have option to just implement
 * MazeSolver interface from scratch. For GUI, benchmark and JUnit tests to work
 * this class doesn't have to be used.
 * 
 * @author Anton Krug
 * @date 2015/03/01
 * @version 1.1
 * @requires Java 8!
 */
/*
 * Copyright (C) Anton Krug - All Rights Reserved Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential
 * Written by Anton Krug <anton.krug@gmail.com>, February 2015
 */

public abstract class MazeSolverBase implements MazeSolver {

	public static final boolean	DEBUG	= false;
	protected List<Point>				allDirections;
	protected Point							currentStep;
	protected List<Point>				destinations;
	protected boolean						destinationVisible;
	protected boolean						doNotSolveAgain;
	protected Aproach						implementationAproach;
	protected Maze							maze;
	protected Point							origin;
	private Long								timeStart;
	private Long								timeStop;

	/**
	 * Constructor to initialise fields.
	 * 
	 * @param maze
	 *          Reqiress to be give already loaded maze
	 */
	public MazeSolverBase(Maze maze, Aproach implementationAproach) {
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

	}

	/**
	 * Simplifying some frequently used operation, if this would be C++ we could
	 * overload the operator and have peace, but java must be java.
	 * 
	 * @return Will return point which will be first point shifted by second point
	 *         (or vice versa, still same result)
	 */
	// TODO okontroluj ci sa vsade pouziva
	protected Point pointsTranslate(Point first, Point second) {
		Point ret = new Point(first);
		ret.translate(second.x, second.y);
		return ret;
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
	 * @throws Exception
	 *           If there is no destination present it will throw exception
	 */
	@Override
	public void addStartingPositions(List<Point> starts) throws Exception {
		for (Point point : starts) {
			this.addStartPosition(point);
		}
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
	 * Return flag if the alrgorithm is allowed to see the destination
	 * 
	 * @return the destinationVisible
	 */
	@Override
	public boolean isDestinationVisible() {
		return destinationVisible;
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
	 * Set current step to null
	 * 
	 * @return
	 */
	@Override
	public boolean solveStepDidntStarted() {
		return currentStep == null;
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
		this.destinationVisible = destinationVisible;
	}

	abstract protected Point doOneStep(Point currentPosition);

	/**
	 * If solveStepCondition() returns true you can do one step iteration
	 */
	@Override
	public void solveStepOneIteration() {
		currentStep = doOneStep(currentStep);
	}

	/**
	 * By default retun for both alternatives null, so then if any implementation
	 * will override any of them then that one will be called by Gui (gui detects
	 * which one is the working one by checking for null)
	 */
	@Override
	public Stream<Point> getVisitedAlreadyAlternative() {
		// by default do not support stream
		return null;
	}

	/**
	 * By default retun for both alternatives null, so then if any implementation
	 * will override any of them then that one will be called by Gui (gui detects
	 * which one is the working one by checking for null)
	 */
	@Override
	public Map<Point, Point> getVisitedAlready() {
		// by default do not support map
		return null;
	}

	/**
	 * Condition which will be checked in each step
	 * 
	 * @return
	 */
	@Override
	public boolean solveStepCondition() {
		return !destinations.contains(currentStep) && getVisitSize() > 0;
	}

	/**
	 * Will return Approach of this implementation
	 * 
	 * @return
	 */
	@Override
	public Aproach getAproach() {
		return implementationAproach;
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
	 * Will attempt to find path from start to finish, contains more simpler
	 * methods, each of them can be overider separetly by each implementation, if
	 * the algorithm needs something different from this default one
	 * 
	 * @return Will return -1 if something failed (initializaiton failed like
	 *         destination missing in the maze, or no solution found), otherwise
	 *         it will return number of iterations given algorithm took to fins
	 *         the location
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

	@Override
	abstract public void addStartPosition(Point origin) throws Exception;

	@Override
	abstract public List<Point> backTracePath();

	@Override
	abstract public List<Point> backTracePathPartially();

	@Override
	abstract public Stream<Point> getVisit();

	@Override
	abstract public int getVisitSize();

	@Override
	abstract public int getVisitedAlreadySize();

	/**
	 * Move a position from open list to closed list
	 * 
	 * @param index
	 */
	abstract protected void markNodeAsVisited(Point index);

}
