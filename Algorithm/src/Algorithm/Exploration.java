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
	private boolean sim;

	public Exploration(Map exploredMap, Map map, Robot robot, double coverageLimit, int timeLimit, int stepPerSecond, boolean sim) {
		this.exploredMap = exploredMap;
		this.map = map;
		this.robot = robot;
		this.coverageLimit = coverageLimit;
		this.timeLimit = timeLimit;
		this.stepPerSecond = stepPerSecond;
		this.sim = sim;
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
	
//	//Exploration 1: UnExplored After go Back to Start
//	public void exploration(Point start) {
//		areaExplored = exploredMap.exploredPercentage();
//		startTime = System.currentTimeMillis();
//		endTime = startTime + timeLimit;
//		double prevArea = exploredMap.exploredPercentage();
//		int moves = 1;
//		int checkingStep = 12;
//		this.start = start;
//		
//		// Loop to explore the map
//		outerloop:
//		do {
//			if(areaExplored >= 100)
//				break;
//			try {
//				getMove();
//			} catch (InterruptedException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			areaExplored = exploredMap.exploredPercentage();
//			//returned to start
//			if (robot.getPosition().distance(start)==0) {
//				while(true){
//					if(!goToUnexplored())
//						break outerloop;
//				}
//			}
//			
//			moves++;
//			System.out.println("areaExplored :"+areaExplored);
//		} while (areaExplored < coverageLimit && System.currentTimeMillis() < endTime);
//		System.out.println("Exploration Ended Going to Start");
//		goToPoint(start);
//	}
	
//	//Exploration 2: Original with Modifications
//	public void exploration(Point start) {
//		areaExplored = exploredMap.exploredPercentage();
//		startTime = System.currentTimeMillis();
//		endTime = startTime + timeLimit;
//		double prevArea = exploredMap.exploredPercentage();
//		int moves = 1;
//		int checkingStep = 15;
//		this.start = start;
//		
//		// Loop to explore the map
//		outerloop:
//		do {
//			if(areaExplored >= 100)
//				break;
//			try {
//				getMove();
//			} catch (InterruptedException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			areaExplored = exploredMap.exploredPercentage();
//			//returned to start
//			if (prevArea == areaExplored||robot.getPosition().distance(start)==0) {
//				do{
//					prevArea = areaExplored;
//					if(!goToUnexplored())
//						break;
//					areaExplored = exploredMap.exploredPercentage();
//				}while(prevArea == areaExplored);
//				checkingStep = 3;
//			}
//			
//			if(moves%checkingStep==0)
//				prevArea = areaExplored;
//			
//			moves++;
//		} while (areaExplored < coverageLimit && System.currentTimeMillis() < endTime);
//		
//		goToPoint(start);
//	}
	
	//Exploration 3: Modification of Original
		public void exploration(Point start) {
			areaExplored = exploredMap.exploredPercentage();
			startTime = System.currentTimeMillis();
			endTime = startTime + timeLimit;
			double prevArea = exploredMap.exploredPercentage();
			int moves = 1;
			int checkingStep = 6;
			this.start = start;
			
			// Loop to explore the map
			outer:
			do {
				prevArea = areaExplored;
				if(areaExplored >= 100)
					break;
				try {
					getMove();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				areaExplored = exploredMap.exploredPercentage();
				if(prevArea==areaExplored)
					moves++;
				else
					moves=1;
				//returned to start
				if (moves%checkingStep==0||robot.getPosition().distance(start)==0) {
					do{
						prevArea = areaExplored;
						if(!goToUnexplored())
							break outer;
						areaExplored = exploredMap.exploredPercentage();
					}while(prevArea == areaExplored);
					moves=1;
					checkingStep=3;
				}
			} while (areaExplored < coverageLimit && System.currentTimeMillis() < endTime);
			
			goToPoint(start);
			endTime = System.currentTimeMillis();
			int seconds = (int)((endTime - startTime)/1000%60);
			int minutes = (int)((endTime - startTime)/1000/60);
			System.out.println("Total Time: "+minutes+"mins "+seconds+"seconds");
		}

	// Locate nearest unexplored point
	public boolean goToUnexplored() {
		System.out.println("Go to UnExplored");
		// Located the nearest unexplored cell and return the nearest explored cell to
		// it
		Cell unCell = exploredMap.nearestUnexp(robot.getPosition());
		System.out.println("Unexplored: "+unCell.toString());
		Cell cell = exploredMap.nearestExp(unCell.getPos(), robot.getPosition());
//		//Unexplored Node cannot be reached from nearest explored cell
		System.out.println("Explored: "+cell.toString());
		return goToPoint(cell.getPos());
	}

	// Fast Algo to a point (used to go back to start
	public boolean goToPoint(Point loc) {
		// robot already at start
		System.out.println("In Go to Point");
		if (robot.getPosition().equals(start)&&loc.equals(start)) {
			while (robot.getDirection() != Direction.UP) {
				if(sim) {
					try {
						TimeUnit.MILLISECONDS.sleep(RobotConstants.MOVE_SPEED/stepPerSecond);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				robot.sense(exploredMap, map);
				robot.move(Command.TURN_RIGHT, RobotConstants.MOVE_STEPS, exploredMap);
			}
			return false;
		}
		ArrayList<Command> commands = new ArrayList<Command>();
		ArrayList<Cell> path = new ArrayList<Cell>();
		FastestPath fp = new FastestPath(exploredMap, robot, sim);
		path = fp.run(robot.getPosition(), loc, robot.getDirection());
		if(path.isEmpty())
			return false;
		fp.displayFastestPath(path, true);
		commands = fp.getPathCommands(path);
		System.out.println(commands);
		for (Command c : commands) {
			if((c==Command.FORWARD) && !movable(robot.getDirection())) {
				System.out.println("Not Executing Forward Not Movable");
				break;
			}
			else
			{
				robot.move(c, RobotConstants.MOVE_STEPS, exploredMap);
				robot.sense(exploredMap, map);
			}
			if(sim) {
				try {
					TimeUnit.MILLISECONDS.sleep(RobotConstants.MOVE_SPEED/stepPerSecond);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		// Orient robot to face UP
		if(loc.equals(start)) {
			while (robot.getDirection() != Direction.UP) {
				robot.move(Command.TURN_RIGHT, RobotConstants.MOVE_STEPS, exploredMap);
				try {
					TimeUnit.MILLISECONDS.sleep(RobotConstants.MOVE_SPEED/stepPerSecond);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println(robot.getDirection());
				robot.sense(exploredMap, map);
			}
		}
		return true;
	}

	public void getMove() throws InterruptedException {
		Direction dir = robot.getDirection();
		// Check Right if free then turn Right
		if (movable(Direction.getPrevious(dir))) {
			if(sim)
				TimeUnit.MILLISECONDS.sleep(RobotConstants.MOVE_SPEED/stepPerSecond);
			robot.move(Command.TURN_RIGHT, RobotConstants.MOVE_STEPS, exploredMap);
			robot.sense(exploredMap, map);
			if (movable(robot.getDirection())) {
				// System.out.println("Right Direction then forward "+robot.getDirection());
				if(sim)
					TimeUnit.MILLISECONDS.sleep(RobotConstants.MOVE_SPEED/stepPerSecond);
				robot.move(Command.FORWARD, RobotConstants.MOVE_STEPS, exploredMap);
				robot.sense(exploredMap, map);
			}
		}
		// Check front if free move forward
		else if (movable(dir)) {
			if(sim)
				TimeUnit.MILLISECONDS.sleep(RobotConstants.MOVE_SPEED/stepPerSecond);
			robot.move(Command.FORWARD, RobotConstants.MOVE_STEPS, exploredMap);
			robot.sense(exploredMap, map);
		}
		// Check left free and move left
		else if (movable(Direction.getNext(dir))) {
			//System.out.println("Left Direction " + Direction.getNext(dir).name());
			if(sim)
				TimeUnit.MILLISECONDS.sleep(RobotConstants.MOVE_SPEED/stepPerSecond);
			robot.move(Command.TURN_LEFT, RobotConstants.MOVE_STEPS, exploredMap);
			robot.sense(exploredMap, map);
			if (movable(robot.getDirection())) {
				// System.out.println("Left Direction then forrwad "+robot.getDirection());
				if(sim)
					TimeUnit.MILLISECONDS.sleep(RobotConstants.MOVE_SPEED/stepPerSecond);
				robot.move(Command.FORWARD, RobotConstants.MOVE_STEPS, exploredMap);
				robot.sense(exploredMap, map);
			}
		}

		// Backwards
		else {
			// Keep Moving back till either can turn right/ left
			while (!movable(Direction.getPrevious(dir)) && !movable(Direction.getNext(dir))) {
				//System.out.println("Backward While");
				if(sim)
					TimeUnit.MILLISECONDS.sleep(RobotConstants.MOVE_SPEED/stepPerSecond);
				robot.move(Command.BACKWARD, RobotConstants.MOVE_STEPS, exploredMap);
				robot.sense(exploredMap, map);
			}
			// Turn left if possible
			if (movable(Direction.getNext(dir))) {
				System.out.println("Backward Turn Left");
				if(sim)
					TimeUnit.MILLISECONDS.sleep(RobotConstants.MOVE_SPEED/stepPerSecond);
				robot.move(Command.TURN_LEFT, RobotConstants.MOVE_STEPS, exploredMap);
				robot.sense(exploredMap, map);
				if (movable(robot.getDirection())) {
					// System.out.println("Left Direction then forrwad "+robot.getDirection());
					if(sim)
						TimeUnit.MILLISECONDS.sleep(RobotConstants.MOVE_SPEED/stepPerSecond);
					robot.move(Command.FORWARD, RobotConstants.MOVE_STEPS, exploredMap);
					robot.sense(exploredMap, map);
				}
			}
			// All other cases turn right
			else {
				System.out.println("Backward Turn Right");
				if(sim)
					TimeUnit.MILLISECONDS.sleep(RobotConstants.MOVE_SPEED/stepPerSecond);
				robot.move(Command.TURN_RIGHT, RobotConstants.MOVE_STEPS, exploredMap);
				robot.sense(exploredMap, map);
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
