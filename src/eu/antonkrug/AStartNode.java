package eu.antonkrug;

import java.awt.Point;
import java.util.List;

/**
 * A node class used inside A* best path searching algoritm
 * 
 * @author Anton Krug
 * @date 2015/02/22
 * @version 1
 */

/* Copyright (C) Anton Krug - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Anton Krug <anton.krug@gmail.com>, February 2015
 */
public class AStartNode {

	// distance from start
	private int		g;

	// H or heurestic is manhatan distance to destination.
	// short has range of 32767, but that would require maze about 16k
	// long and wide and start & destination be far away from each other. there is
	// no need for h be int. Maybe there is no gain having it short (JVM will
	// probably use 32bit block to store it anyway, but there is no real need to
	// be int either. And because I don't use any multiplication (where there is
	// performance penaulty for mixing types) there is no direct drawback to not
	// using it.
	private short	heurestic;

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
	public AStartNode(Point parent, int previousCost, int currentX, int currentY,
			List<Point> destinations) throws Exception {

		if (destinations.size() == 0)
			throw new Exception("Before creating Nodes you have to set at least one destination!");

		this.parent = parent;
		this.setG(previousCost);
		this.setH(currentX, currentY, destinations);

	}

	/**
	 * Constructor using point for current position
	 * 
	 * @param previousCost
	 * @param current
	 * @param destination
	 */
	public AStartNode(Point parent, int previousCost, Point current, List<Point> destinations)
			throws Exception {

		if (destinations.size() == 0)
			throw new Exception("Before creating Nodes you have to set at least one destination!");

		this.parent = parent;
		this.setG(previousCost);
		this.setH(current.x, current.y, destinations);
	}

	/**
	 * Get G and F combined Want return Integer so on lamba expressions just can
	 * call getF().compare directly
	 * 
	 * @return
	 */
	public Integer getF() {
		return g + heurestic;
	}

	/**
	 * Get distantance traveled from start. Return Integer so on lamba expressions
	 * i just can call getG().compare directly
	 * 
	 * @return the g
	 */
	public Integer getG() {
		return g;
	}

	/**
	 * Get manhatan distance to destination
	 * 
	 * @return the h
	 */
	public short getH() {
		return heurestic;
	}

	/**
	 * Get manhatan distance to destination
	 * 
	 * @return the h
	 */
	public short getHeurestic() {
		return heurestic;
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
	 * Calculates manhatan distance to destination and sets it as H
	 * 
	 * @param currentX
	 * @param currentY
	 * @param destination
	 */
	public void setH(int currentX, int currentY, List<Point> destinations) {

		short smallest = Short.MAX_VALUE;

		// find closest destination for this point and use him as heurestics
		for (Point destination : destinations) {
			short heurestic = (short) (Math.abs(currentX - destination.x) + Math.abs(currentY
					- destination.y));
			if (heurestic < smallest) smallest = heurestic;
		}

		// manhatan distance
		this.heurestic = smallest;
	}

	/**
	 * @param h
	 *          the heurestics set to specific value
	 */
	public void setH(short h) {
		this.heurestic = h;
	}

	/**
	 * @param h
	 *          the heurestics set to specific value
	 */
	public void setHeurestic(short h) {
		this.heurestic = h;
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
		return "Node [Parent = " + parent + ", G=" + g + ", H=" + heurestic + ", F=" + getF() + "]";
	}

}
