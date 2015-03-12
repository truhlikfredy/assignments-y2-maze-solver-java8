package eu.antonkrug;

/**
 * This is just abstract interface what each algorithm has to have, so when 
 * multiple aproaches are implementented they can be used by the same GUI
 * 
 * @author Anton Krug
 * @date 2015/02/22
 * @version 1.2
 * 
 */

import java.awt.Point;
import java.util.List;
import java.util.Map;

public interface MazeSolver {

	public static final boolean	DEBUG	= false;

	/**
	 * Will add one or more destinations to maze
	 * 
	 * @param destination
	 */
	public void addDestinationPosition(Point destination);

	/**
	 * Will add one or more starting positions for the maze
	 * 
	 * @param starts
	 *          List of starting points
	 * @throws Exception If there is no destination present it will throw exception
	 */
	public void addStartingPositions(List<Point> starts) throws Exception;

	/**
	 * Will add both starting and final destination points from the maze is given
	 * to this solver
	 * 
	 * @throws Exception
	 *           If there is no destination present it will throw exception
	 */
	public void addStartingAndDestionationPositions() throws Exception;

	/**
	 * Will add starting position into maze, a maze can contain multiple starting
	 * positions. And position which will gain the shortest path will choosen.
	 * 
	 * @param origin
	 * @throws Exception 
	 */
	public void addStartPosition(Point origin) throws Exception;

	/**
	 * Returns final solved path
	 * 
	 * @return
	 */
	public List<Point> backTracePath();
	
	/**
	 * Returns current final path by partly solved search
	 * 
	 * @return
	 */
	public List<Point> backTracePathParty();

	/**
	 * Gets the current step position inside the solver
	 * 
	 * @return the currentStep
	 */
	public Point getCurrentStep();

	/**
	 * Returns all given destinations
	 * 
	 * @return the destinations
	 */
	public List<Point> getDestinations();

	/**
	 * Returns open list
	 * 
	 * @return the visit
	 */
	public Map<Point, AStartNode> getVisit();

	/**
	 * Returns closed list
	 * 
	 * @return the visitedAlready
	 */
	public Map<Point, Point> getVisitedAlready();

	/**
	 * Return flag if the alrgorithm is allowed to see the destination
	 * 
	 * @return the destinationVisible
	 */
	public boolean isDestinationVisible();

	/**
	 * Flag if algorithm is solved (with solution or not) and locked for any new
	 * solving attempt.
	 * 
	 * @return the doNotSolveAgain
	 */
	public boolean isDoNotSolveAgain();

	/**
	 * Set all destinations to given list.
	 * 
	 * @param destinations
	 */
	public void setDestinations(List<Point> destinations);

	/**
	 * Flag if algorithm is allowed to see destination
	 * 
	 * @param destinationVisible
	 *          the destinationVisible to set
	 */
	public void setDestinationVisible(boolean destinationVisible);

	/**
	 * Will attempt to find path from start to finish, will call other solveStep*
	 * methods, this is split in such way that the animate and manual steper and
	 * even completely automated solver will use same code and same conditions,
	 * affecting condition checker will affect all 3 aproaches.
	 * 
	 * @return
	 */
	public int solvePath();

	/**
	 * Condition which will be checked if given step can be executed
	 * 
	 * @return returns true if you can do one iteration of step
	 */
	public boolean solveStepCondition();

	/**
	 * Set current step to null
	 * 
	 * @return
	 */
	public boolean solveStepDidntStarted();

	/**
	 * If solver is finished, do final checks and cleanup
	 * 
	 * @return
	 */
	public int solveStepFinish();

	/**
	 * Called this initialization before solver can do each step iteration
	 * 
	 * @return
	 */
	public int solveStepInit();

	/**
	 * If solveStepCondition() returns true you can do one step iteration
	 */
	public void solveStepOneIteration();

	/**
	 * Returns measured time between the solver was started, till it found
	 * solution
	 * 
	 * @return
	 */
	public long timeTaken();

}
