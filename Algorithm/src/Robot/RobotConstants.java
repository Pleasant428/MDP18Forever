package Robot;
/**
 * @author Saklani Pankaj
 *
 */

import javafx.scene.paint.Color;

public class RobotConstants {

	// G values used for A* algorithm
	public static final int MOVE_COST = 1;
	public static final int TURN_COST = 5;
	public static final double INFINITE_COST = 10000000;

	// Sensors default range (In grids)
	public static final int SHORT_MIN = 1;
	public static final int SHORT_MAX = 2;

	public static final int LONG_MIN = 1;
	public static final int LONG_MAX = 4;
	
	//Constants to render Robot
	public static final Color ROBOT_BODY = Color.rgb(139, 0, 0, 0.8);
	public static final Color ROBOT_OUTLINE = Color.BLACK;
	public static final Color ROBOT_DIRECTION = Color.WHITESMOKE;
	
	// Direction enum based on compass
	public static enum Direction {
		UP, LEFT, DOWN, RIGHT;

		// Used to Get the new direction, when robot turns right
		public static Direction getNext(Direction currDirection) {
			return values()[(currDirection.ordinal() + 1) % values().length];
		}

		// Used to Get the new direction, when robot turns left
		public static Direction getPrevious(Direction currDirection) {
			return values()[(currDirection.ordinal() + values().length - 1) % values().length];
		}

	};
	
	
}
