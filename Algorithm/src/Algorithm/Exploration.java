package Algorithm;

import java.awt.Point;

import Map.*;
import Robot.*;
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
		
		while(areaExplored < coverageLimit || System.currentTimeMillis() < endTime) {
			
		}
		
	}
	
	public void getMove() {
		//return go left
	}
	
	public boolean look(Direction dir) {
		int rowInc, colInc;
		
		//Look at different directions
		return false;
	}
	
}
