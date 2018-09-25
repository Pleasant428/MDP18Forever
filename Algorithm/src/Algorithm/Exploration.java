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
	private Robot bot;
	private double coverageLimit;
	private int timeLimit;				//In milliseconds
	private double areaExplored;
	private long startTime;
	private long endTime;
	
	public Exploration(Map exploredMap, Map map, Robot bot, double coverageLimit, int timeLimit) {
		this.exploredMap = exploredMap;
        this.map = map;
        this.bot = bot;
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
		
		//Loop to explore the map 
		while(areaExplored < coverageLimit && System.currentTimeMillis() < endTime) {
			bot.sense(exploredMap, map);
			getMove();
			exploredMap.draw(true);
			areaExplored = exploredMap.exploredPercentage();
			
			//If robot reaches back to start location and has explored everything end
			if(bot.getPosition() == start) {
				if(areaExplored >=100)
					break;
			}
		}
		goToPoint(start);
		
	}
	
	//Fast Algo to a point (used to go back to start
	public void goToPoint(Point loc) {
		//bot already at start
		if(bot.getPosition() == loc) {
			while(bot.getDirection() != Direction.UP) {
				bot.move(Command.TURN_LEFT, RobotConstants.MOVE_STEPS, exploredMap);
				exploredMap.draw(true);
			}
			return;
		}
		ArrayList<Command> commands = new ArrayList<Command>();
		ArrayList<Cell> path = new ArrayList<Cell>();
		
		FastestPath backToStart = new FastestPath(exploredMap,bot);
		path = backToStart.run(bot.getPosition(), loc, bot.getDirection());
		commands = backToStart.getPathCommands(path);
		for(Command c: commands) {
			bot.move(c,  RobotConstants.MOVE_STEPS, exploredMap);
			exploredMap.draw(true);
		}
		
		//Orient robot to face UP
		while(bot.getDirection() != Direction.UP)
		{
			bot.move(Command.TURN_LEFT, RobotConstants.MOVE_STEPS, exploredMap);
			exploredMap.draw(true);
		}
	}
	
	public void getMove() {
		Direction dir = bot.getDirection();
		//Check Left if free then turn left
		if(movable(Direction.getNext(dir)))
		{
			System.out.println("Left Direction "+Direction.getNext(dir).name());
			bot.move(Command.TURN_LEFT, RobotConstants.MOVE_STEPS, exploredMap);
		}
		else if(movable((dir)))
		{
			System.out.println("Forward");
			bot.move(Command.FORWARD, RobotConstants.MOVE_STEPS, exploredMap);
		}
		else if(movable(Direction.getPrevious(dir)))
		{
			System.out.println("Right");
			bot.move(Command.TURN_RIGHT, RobotConstants.MOVE_STEPS, exploredMap);
		}
		else {
			System.out.println("Backward");
			bot.move(Command.BACKWARD, RobotConstants.MOVE_STEPS, exploredMap);
		}
		try {
			TimeUnit.MILLISECONDS.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		System.out.println("Checking Cell: "+(bot.getPosition().y + rowInc)+", "+(bot.getPosition().x + colInc)+" validMove: "+exploredMap.checkValidMove(bot.getPosition().y + rowInc, bot.getPosition().x + colInc));
		return exploredMap.checkValidMove(bot.getPosition().y + rowInc, bot.getPosition().x + colInc);
		
	}
	
}
