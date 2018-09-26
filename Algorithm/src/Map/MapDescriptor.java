/**
 * 
 */
package Map;

import Map.*;
import Map.MapConstants;

/**
 * @author 
 *
 */
public class MapDescriptor {

    private static String binToHex(String binStr) {
        int dec = Integer.parseInt(binStr, 2);
        return Integer.toHexString(dec);
    }
    
    public static String generateMDFStringPart1(Map exploredMap) {

		String mapString = "11"; // First two bits set to 11

		for (int row = (MapConstants.MAP_HEIGHT - 1); row >= 0; row--) {
			for (int col = 0; col < MapConstants.MAP_WIDTH; col++) {
				mapString += exploredMap.getCell(row, col).isExplored() ? "1" : "0";
			}
		}

		// Last two bits set to 11
		// return mapString;
		return binToHex(mapString + "11");
	}

	public static String generateMDFStringPart2(Map exploredMap) {

		String mapString = "";

		for (int row = (MapConstants.MAP_HEIGHT - 1); row >= 0; row--) {
			for (int col = 0; col < MapConstants.MAP_WIDTH; col++) {
				if (exploredMap.getCell(row, col).isExplored())
					mapString += exploredMap.getCell(row, col).isObstacle() ? "1" : "0";
			}
		}

		// Pad with '0' to make the length a multiple of 8
		int mapStringLength = mapString.length();
		int paddingLength = mapStringLength % 8;

		if (paddingLength != 0) {

			// Find the number of required bits
			paddingLength = 8 - paddingLength;

			for (int i = 0; i < paddingLength; i++) {
				mapString += "0";
			}
		}

		return binToHex(mapString);
	}
    
}
