package Robot;

import java.awt.Point;
import Robot.RobotConstants.Direction;

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
    

}
