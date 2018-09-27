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
	private int timeLimit;				//In milliseconds
	private double areaExplored;
	private long startTime;
	private long endTime;
	
	public Exploration(Map exploredMap, Map map, Robot robot, double coverageLimit, int timeLimit) {
		this.exploredMap = exploredMap;
        this.map = map;
        this.robot = robot;
        this.coverageLimit = coverageLimit;
        this.timeLimit = timeLimit;
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
		
		//Loop to explore the map 
		do {
			getMove();
			areaExplored = exploredMap.exploredPercentage();
			//Entered a Loop or returned to start
			if(prevArea == areaExplored || robot.getPosition().equals(start))
			{
				if(areaExplored == 100)
					break;
				else
					goToUnexplored();
			}	
		}while((areaExplored < coverageLimit || System.currentTimeMillis() < endTime));
		
		goToPoint(start);
	}
	
	//Locate nearest unexplored point
	public void goToUnexplored() {
		System.out.println("Go to UnExplored");
		//Located the nearest unexplored cell and return the nearest explored cell to it
		Cell cell = exploredMap.nearestExp(exploredMap.nearestUnexp(robot.getPosition()).getPos());
		goToPoint(cell.getPos());
	}
	
	
	//Fast Algo to a point (used to go back to start
	public void goToPoint(Point loc) {
		//robot already at start
		if(robot.getPosition() == loc) {
			while(robot.getDirection() != Direction.UP) {
				robot.move(Command.TURN_LEFT, RobotConstants.MOVE_STEPS, exploredMap);
				robot.sense(exploredMap, exploredMap);
			}
			return;
		}
		ArrayList<Command> commands = new ArrayList<Command>();
		ArrayList<Cell> path = new ArrayList<Cell>();
		
		FastestPath backToStart = new FastestPath(exploredMap,robot);
		path = backToStart.run(robot.getPosition(), loc, robot.getDirection());
		System.out.println(path.size());
		commands = backToStart.getPathCommands(path);
		for(Command c: commands) {
			System.out.println(c.toString());
			robot.move(c,  RobotConstants.MOVE_STEPS, exploredMap);
			robot.sense(exploredMap, exploredMap);
		}
		
		//Orient robot to face UP
		while(robot.getDirection() != Direction.UP)
		{
			robot.move(Command.TURN_LEFT, RobotConstants.MOVE_STEPS, exploredMap);
			robot.sense(exploredMap, exploredMap);
		}
	}
	
	public void getMove(){
		Direction dir = robot.getDirection();
		
		//Check Right if free then turn Right
		if(movable(Direction.getPrevious(dir)))
		{
			//System.out.println("Right");
			robot.move(Command.TURN_RIGHT, RobotConstants.MOVE_STEPS, exploredMap);
			robot.sense(exploredMap, exploredMap);
			if(movable(robot.getDirection()))
			{
				//System.out.println("Right Direction then forrwad "+robot.getDirection());
				robot.move(Command.FORWARD, RobotConstants.MOVE_STEPS, exploredMap);
				robot.sense(exploredMap, exploredMap);
			}
		}
		else if(movable(dir))
		{
			//System.out.println("Forward");
			robot.move(Command.FORWARD, RobotConstants.MOVE_STEPS, exploredMap);
			robot.sense(exploredMap, exploredMap);
		}
		else if(movable(Direction.getNext(dir)))
		{
			//System.out.println("Left Direction "+Direction.getNext(dir).name());
			robot.move(Command.TURN_LEFT, RobotConstants.MOVE_STEPS, exploredMap);
			senseAndDraw();
			if(movable(robot.getDirection()))
			{
				//System.out.println("Left Direction then forrwad "+robot.getDirection());
				robot.move(Command.FORWARD, RobotConstants.MOVE_STEPS, exploredMap);
				robot.sense(exploredMap, exploredMap);
			}
		}
		else {
			//System.out.println("Backward");
			robot.move(Command.BACKWARD, RobotConstants.MOVE_STEPS, exploredMap);
			robot.sense(exploredMap, exploredMap);
		}
	}
	
	public void senseAndDraw() {
		exploredMap.draw(true);
		robot.draw();
		robot.sense(exploredMap, exploredMap);
		exploredMap.draw(true);
		robot.draw();
	}
	
	//Returns true if a direction is movable to or not
	public boolean movable(Direction dir) {
		int rowInc = 0, colInc=0;
		
		switch(dir)
		{
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
		//System.out.println("Checking Cell: "+(robot.getPosition().y + rowInc)+", "+(robot.getPosition().x + colInc)+" validMove: "+exploredMap.checkValidMove(robot.getPosition().y + rowInc, robot.getPosition().x + colInc));
		return exploredMap.checkValidMove(robot.getPosition().y + rowInc, robot.getPosition().x + colInc);
		
	}
	
}
