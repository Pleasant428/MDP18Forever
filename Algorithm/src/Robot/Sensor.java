package Robot;

import java.awt.Point;
import Robot.RobotConstants.Direction;
import Map.*;

public class Sensor {
	
	//Ranges of the Sensors
	private int minRange;
    private int maxRange;

    // Sensor's position on the map
    private Point pos;

    private Direction sensorDir;

    public Sensor(int minRange, int maxRange, int sensorPosRow, int sensorPosCol, Direction sensorDirection) {
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.pos = new Point(sensorPosCol, sensorPosRow);
        this.sensorDir = sensorDirection;
    }

	public int getMinRange() {
		return minRange;
	}

	public void setMinRange(int minRange) {
		this.minRange = minRange;
	}

	public int getMaxRange() {
		return maxRange;
	}

	public void setMaxRange(int maxRange) {
		this.maxRange = maxRange;
	}

	public Point getPos() {
		return pos;
	}

	public void setPos(int col, int row) {
		this.pos.setLocation(col, row);
	}

	public Direction getSensorDir() {
		return sensorDir;
	}

	public void setSensorDir(Direction sensorDir) {
		this.sensorDir = sensorDir;
	}
    
	//Senses at which grid there is a obstacle -1 if none detected
	//Detected within the min and max range of sensor
	public int detectObstacle(Map map, boolean sim) {
		if(!sim) {
			
			//Checking the range
			for(int cur= minRange; cur <= maxRange; cur++) {
				switch(sensorDir){
				case UP:
					if(cur - pos.y > 0)
						return cur;
					
					break;
				case RIGHT:
					break;
				case DOWN:
					break;
				case LEFT:
					break;
				}
				
			}
			
		}
	}

}
