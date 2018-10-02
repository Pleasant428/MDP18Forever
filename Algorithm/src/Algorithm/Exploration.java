package Algorithm;

import java.awt.Point;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import Map.*;
import Robot.*;
import Robot.RobotConstants.Command;
import Robot.RobotConstants.Direction;

/**
 * @author Saklani Pankaj
 *
 */
public class Exploration {

	private Map exploredMap;
	private Map map;
	private Robot robot;
	private double coverageLimit;
	private int timeLimit; // In milliseconds
	private double areaExplored;
	private long startTime;
	private long endTime;
	private int stepPerSecond;
	private Point start;

	public Exploration(Map exploredMap, Map map, Robot robot, double coverageLimit, int timeLimit, int stepPerSecond) {
		this.exploredMap = exploredMap;
		this.map = map;
		this.robot = robot;
		this.coverageLimit = coverageLimit;
		this.timeLimit = timeLimit;
		this.stepPerSecond = stepPerSecond;
	}

	public Map getExploredMap() {
		return exploredMap;
	}

	public void setExploredMap(Map exploredMap) {
		this.exploredMap = exploredMap;
	}

	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
	}

	public double getCoverageLimit() {
		return coverageLimit;
	}

	public void setCoverageLimit(double coverageLimit) {
		this.coverageLimit = coverageLimit;
	}

	public int getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(int timeLimit) {
		this.timeLimit = timeLimit;
	}

	public void exploration(Point start) {
		areaExplored = exploredMap.exploredPercentage();
		startTime = System.currentTimeMillis();
		endTime = startTime + timeLimit;
		double prevArea = exploredMap.exploredPercentage();
		int moves = 1;
		int checkingStep = 3;
		this.start = start;
		
		// Loop to explore the map
		do {
			System.out.println(System.currentTimeMillis()-startTime);
			try {
				getMove();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			areaExplored = exploredMap.exploredPercentage();
			if(areaExplored >= 100)
				break;
			//returned to start
			if (prevArea==areaExplored) {
				goToUnexplored();
			}
			if(moves%checkingStep == 0)
				prevArea = areaExplored;
		} while (areaExplored < coverageLimit && System.currentTimeMillis() < endTime);
		System.out.println("Robot Cell: "+ robot.getPosition().toString());
		goToPoint(start);
	}

	// Locate nearest unexplored point
	public void goToUnexplored() {
		System.out.println("Go to UnExplored");
		// Located the nearest unexplored cell and return the nearest explored cell to
		// it
		Cell cell = exploredMap.nearestExp(exploredMap.nearestUnexp(robot.getPosition()).getPos());
		System.out.println(cell.toString());
		goToPoint(cell.getPos());
	}

	// Fast Algo to a point (used to go back to start
	public void goToPoint(Point loc) {
		// robot already at start
		if (robot.getPosition().equals(start)) {
			while (robot.getDirection() != Direction.UP) {
				try {
					TimeUnit.MILLISECONDS.sleep(RobotConstants.MOVE_SPEED/stepPerSecond);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				robot.sense(exploredMap, map);
				robot.move(Command.TURN_RIGHT, RobotConstants.MOVE_STEPS, exploredMap);
			}
			return;
		}
		ArrayList<Command> commands = new ArrayList<Command>();
		ArrayList<Cell> path = new ArrayList<Cell>();

		FastestPath backToStart = new FastestPath(exploredMap, robot);
		path = backToStart.run(robot.getPosition(), loc, robot.getDirection());
		commands = backToStart.getPathCommands(path);
		for (Command c : commands) {
			try {
				TimeUnit.MILLISECONDS.sleep(RobotConstants.MOVE_SPEED/stepPerSecond);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			robot.sense(exploredMap, map);
			robot.move(c, RobotConstants.MOVE_STEPS, exploredMap);
		}

		// Orient robot to face UP
		if(loc.equals(start)) {
			while (robot.getDirection() != Direction.UP) {
				System.out.println("in End While Loop");
				robot.move(Command.TURN_RIGHT, RobotConstants.MOVE_STEPS, exploredMap);
				try {
					TimeUnit.MILLISECONDS.sleep(RobotConstants.MOVE_SPEED/stepPerSecond);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				robot.sense(exploredMap, map);
				System.out.println(robot.getDirection());
			}
		}
	}

	public void getMove() throws InterruptedException {
		Direction dir = robot.getDirection();
		
		// Check Right if free then turn Right
		if (movable(Direction.getPrevious(dir))) {
			System.out.println("Right");
			TimeUnit.MILLISECONDS.sleep(RobotConstants.MOVE_SPEED/stepPerSecond);
			robot.sense(exploredMap, map);
			robot.move(Command.TURN_RIGHT, RobotConstants.MOVE_STEPS, exploredMap);
			if (movable(robot.getDirection())) {
				// System.out.println("Right Direction then forrwad "+robot.getDirection());
				TimeUnit.MILLISECONDS.sleep(RobotConstants.MOVE_SPEED/stepPerSecond);
				robot.sense(exploredMap, map);
				robot.move(Command.FORWARD, RobotConstants.MOVE_STEPS, exploredMap);
			}
		}
		// Check front if free move forward
		else if (movable(dir)) {
			System.out.println("Forward");
			TimeUnit.MILLISECONDS.sleep(RobotConstants.MOVE_SPEED/stepPerSecond);
			robot.sense(exploredMap, map);
			robot.move(Command.FORWARD, RobotConstants.MOVE_STEPS, exploredMap);
		}
		// Check left free and move left
		else if (movable(Direction.getNext(dir))) {
			System.out.println("Left Direction " + Direction.getNext(dir).name());
			TimeUnit.MILLISECONDS.sleep(RobotConstants.MOVE_SPEED/stepPerSecond);
			robot.sense(exploredMap, map);
			robot.move(Command.TURN_LEFT, RobotConstants.MOVE_STEPS, exploredMap);
			if (movable(robot.getDirection())) {
				// System.out.println("Left Direction then forrwad "+robot.getDirection());
				TimeUnit.MILLISECONDS.sleep(RobotConstants.MOVE_SPEED/stepPerSecond);
				robot.sense(exploredMap, map);
				robot.move(Command.FORWARD, RobotConstants.MOVE_STEPS, exploredMap);
			}
		}

		// Backwards
		else {
			// Keep Moving back till either can turn right/ left
			while (!movable(Direction.getPrevious(dir)) && !movable(Direction.getNext(dir))) {
				System.out.println("Backward While");
				TimeUnit.MILLISECONDS.sleep(RobotConstants.MOVE_SPEED/stepPerSecond);
				robot.sense(exploredMap, map);
				robot.move(Command.BACKWARD, RobotConstants.MOVE_STEPS, exploredMap);
			}
			// Turn left if possible
			if (movable(Direction.getNext(dir))) {
				System.out.println("Backward Turn Left");
				TimeUnit.MILLISECONDS.sleep(RobotConstants.MOVE_SPEED/stepPerSecond);
				robot.sense(exploredMap, map);
				robot.move(Command.TURN_LEFT, RobotConstants.MOVE_STEPS, exploredMap);
				if (movable(robot.getDirection())) {
					// System.out.println("Left Direction then forrwad "+robot.getDirection());
					TimeUnit.MILLISECONDS.sleep(RobotConstants.MOVE_SPEED/stepPerSecond);
					robot.sense(exploredMap, map);
					robot.move(Command.FORWARD, RobotConstants.MOVE_STEPS, exploredMap);
				}
			}
			// All other cases turn right
			else {
				System.out.println("Backward Turn Right");
				TimeUnit.MILLISECONDS.sleep(RobotConstants.MOVE_SPEED/stepPerSecond);
				robot.sense(exploredMap, map);
				robot.move(Command.TURN_RIGHT, RobotConstants.MOVE_STEPS, exploredMap);
			}

		}
	}

	// Returns true if a direction is movable to or not
	public boolean movable(Direction dir) {
		int rowInc = 0, colInc = 0;

		switch (dir) {
		case UP:
			rowInc = 1;
			colInc = 0;
			break;

		case LEFT:
			rowInc = 0;
			colInc = -1;
			break;

		case RIGHT:
			rowInc = 0;
			colInc = 1;
			break;

		case DOWN:
			rowInc = -1;
			colInc = 0;
			break;
		}
		// System.out.println("Checking Cell: "+(robot.getPosition().y + rowInc)+",
		// "+(robot.getPosition().x + colInc)+" validMove:
		// "+exploredMap.checkValidMove(robot.getPosition().y + rowInc,
		// robot.getPosition().x + colInc));
		return exploredMap.checkValidMove(robot.getPosition().y + rowInc, robot.getPosition().x + colInc);

	}

}
