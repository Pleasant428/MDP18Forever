import java.awt.Point;
/**
 * 
 */

/**
 * @author Saklani Pankaj
 *
 */

public class Map {
	
	private final Cell[][] grid;
	
	//KIV add Robot once Created
	public Map()
	{
		grid = new Cell[MapConstants.MAP_HEIGHT][MapConstants.MAP_WIDTH];
		
		//Init Cells on the grid;
		for(int row=0; row < MapConstants.MAP_HEIGHT; row++) {
			for(int col=0; col < MapConstants.MAP_WIDTH; col++) {
				grid[row][col] = new Cell(new Point(row,col));
				
				//Init Virtual wall
				if(row==0||col==0||row==MapConstants.MAP_HEIGHT-1||col==MapConstants.MAP_WIDTH-1) {
					grid[row][col].setVirtualWall(true);
				}
				
			}
		}
		
	}

}
