package eu.antonkrug;

import java.awt.Point;
import java.util.Collections;
//import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

//import utils.HashedLinkedBlockingQueue;

public class Test {

	private List<Point>												testDirections;
	private Map<Point, Node>									visit;
//	private HashedLinkedBlockingQueue<Point>	visited;
	private Point															destination;
	private Point															origin;

	public Test(Point origin, Point destination) {

		this.testDirections = new LinkedList<Point>();
		// acumulative delta movement for up,down,left and right
		this.testDirections.add(new Point(-1, 0));
		this.testDirections.add(new Point(1, 0));
		this.testDirections.add(new Point(0, 1));
		this.testDirections.add(new Point(0, -1));

		this.visit = new HashMap<Point, Node>();

//		this.visited = new HashedLinkedBlockingQueue<Point>();

		this.destination = destination;
		this.origin = origin;
	}

	private void evaluatePoint(Point currentPoint, Point direction) {		
		Point testPoint = new Point(currentPoint);
		testPoint.translate(direction.x, direction.y);

		Node proposedNode = new Node(currentPoint, visit.get(currentPoint).getG(), testPoint, destination);
//		System.out.println(testPoint);
//		System.out.println(proposedNode);

		// will replace if it's not found already or when it's found but new value
		// is better
		if (!visit.containsKey(testPoint) || visit.get(testPoint).getF() > proposedNode.getF())
			visit.put(testPoint, proposedNode);

	}

	public void solve() {
		visit.put(origin, new Node(null, 0, origin, destination));
		
		Point currentStep=origin;
		
		while (!currentStep.equals(destination)) {
			currentStep=doOneStep(currentStep);
		}
		
		System.out.println("************");
		System.out.println("Trace back!!!");
		System.out.println("**********");
		
		while (currentStep!=null) {
			System.out.println(currentStep);
			currentStep=visit.get(currentStep).getParent();
		}
	}
	
	private Point doOneStep(Point currentPosition) {
		
		//mark as visited
		visit.get(currentPosition).setVisited(true);

		//test each direction
		testDirections.parallelStream().forEach(
				direction -> evaluatePoint(currentPosition, direction));
		
		//get smallest move, except the visited ones
		Entry<Point, Node> min = Collections.min(visit.entrySet(), (Entry<Point, Node> a,
				Entry<Point, Node> b) -> {
			if (a.getValue().isVisited())
				return Integer.MAX_VALUE;
			else if (b.getValue().isVisited())
				return Integer.MIN_VALUE;
			else return a.getValue().getF().compareTo(b.getValue().getF());
		});

		System.out.println(min.getKey()+" "+min.getValue());
		return min.getKey();		
	}

	public static void main(String[] args) {
		Point origin = new Point(10, 10);
//		Point destination = new Point(20, 10);
		Point destination = new Point(7, 8);

		Test app = new Test(origin, destination);
		app.solve();
	}
}
