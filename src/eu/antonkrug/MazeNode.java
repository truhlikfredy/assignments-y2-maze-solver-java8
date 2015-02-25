package eu.antonkrug;

import java.awt.Point;

/**
 * A node class used inside A* best path searching algoritm
 * 
 * @author Anton Krug
 * @date 2015/02/22
 * @version 1
 */
public class MazeNode {

	// distance from start heurestic
	private int		g;

	// manhatan distance to destination.
	// short the range is maximum of 32767, but that would require maze about 16k
	// long and wide and start & destination be far away from each other. there is
	// no need for h be int.
	private short	h;

	// pointer to parent
	private Point	parent;

	// con save memory the F value is not stored, but always calculated

	/**
	 * Constructor using coordinates for current position
	 * 
	 * @param previousCost
	 * @param currentX
	 * @param currentY
	 * @param destination
	 */
	public MazeNode(Point parent, int previousCost, int currentX, int currentY, Point destination) {
		this.parent = parent;
		this.setG(previousCost);
		this.setH(currentX, currentY, destination);
	}

	/**
	 * Constructor using point for current position
	 * 
	 * @param previousCost
	 * @param current
	 * @param destination
	 */
	public MazeNode(Point parent, int previousCost, Point current, Point destination) {
		this.parent = parent;
		this.setG(previousCost);
		this.setH(current.x, current.y, destination);
	}

	/**
	 * Get G and F combined Want return Integer so on lamba expressions i just can
	 * call getF().compare directly
	 * 
	 * @return
	 */
	public Integer getF() {
		return g + h;
	}

	/**
	 * Get distantance traveled from start
	 * 
	 * @return the g
	 */
	public int getG() {
		return g;
	}

	/**
	 * Get manhatan distance to destination
	 * 
	 * @return the h
	 */
	public short getH() {
		return h;
	}

	/**
	 * @return the parent
	 */
	public Point getParent() {
		return parent;
	}

	/**
	 * @param previousCost
	 *          will add 10 to this and store it into G
	 */
	public void setG(int previousCost) {
		// not dealing with diagonals so 1 can be fixed and don't need 1.4 for
		// diagonals
		this.g = previousCost + 1;
	}

	/**
	 * @param h
	 *          the h to set
	 */
	public void setH(short h) {
		this.h = h;
	}

	/**
	 * Calculates manhatan distance to destination and sets it as H
	 * 
	 * @param currentX
	 * @param currentY
	 * @param destination
	 */
	public void setH(int currentX, int currentY, Point destination) {
		// manhatan distance
		this.h = (short) (Math.abs(currentX - destination.x) + Math.abs(currentY - destination.y));
	}

	/**
	 * @param parent
	 *          the parent to set
	 */
	public void setParent(Point parent) {
		this.parent = parent;
	}

	@Override
	public String toString() {
		return "Node [Parent = " + parent + ", G=" + g + ", H=" + h + ", F=" + getF() + "]";
	}

}
