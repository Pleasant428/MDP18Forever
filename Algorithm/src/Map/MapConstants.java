package Map;

import javafx.scene.paint.Color;
/**
 * @author Saklani Pankaj
 *
 */

//Map Constants for the Map
public class MapConstants {
	
	//Public Variables 
	public static final short CELL_CM = 10;
	public static final short MAP_HEIGHT = 20;
	public static final short MAP_WIDTH = 15;
	public static final int GOALZONE_ROW = 18;
	public static final int GOALZONE_COL = 13;
	public static final int STARTZONE_ROW = 1;
	public static final int STARTZONE_COL = 1;
	
	//Graphic Constants
	public static final Color SZ_COLOR = Color.YELLOW;	//Start Zone Color
	public static final Color GZ_COLOR = Color.GREEN;	//Goal Zone Color
	public static final Color UE_COLOR = Color.LIGHTGRAY;	//Unexplored Color
	public static final Color EX_COLOR = Color.WHITE;	//Explored Color
	public static final Color OB_COLOR = Color.BLACK;	//Obstacle Color
	public static final Color CW_COLOR = Color.WHITESMOKE;	//Cell Border Color
	
	public static final int MAP_CELL_SZ = 20;			//Size of the Cells on the Map (Pixels)
	
}