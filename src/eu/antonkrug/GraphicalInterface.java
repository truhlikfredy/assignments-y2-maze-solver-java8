package eu.antonkrug;

import java.awt.Point;

public class GraphicalInterface {

	public static void main(String[] args) {
		MazeSolver solver = new MazeSolver();
		
		solver.addDestinationPosition(new Point(17,3));

		try {
			solver.addStartPosition(new Point(10, 10));
			
			if (solver.solvePath() > 0) {
				solver.backTracePath();
			} else {
				System.out.print("Can't find path (no origin, or no possible path)");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} 

	}

}
