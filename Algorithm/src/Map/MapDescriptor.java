/**
 * 
 */
package Map;

import java.io.*;

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
    
    private static String hexToBin(String hexStr) {
    	int num = Integer.parseInt(hexStr,16);
    	return Integer.toBinaryString(num);
    }
    
    //Explore MDF String
    public static String generateMDFStringPart1(Map map) {

		String mapString = ""; // First two bits set to 11
		String temp ="11";
		for (int row = 0; row < MapConstants.MAP_HEIGHT; row++) {
			for (int col = 0; col < MapConstants.MAP_WIDTH; col++) {
				
				if(temp.length()<4)
					temp += map.getCell(row, col).isExplored() ? "1" : "0";
				else {
					mapString += binToHex(temp);
					temp = "";
				}
			}
		}
		temp+= "11";

		// Last two bits set to 11
		// return mapString;
		return mapString + binToHex(temp);
	}
    
    //Obstacle MDF String
	public static String generateMDFStringPart2(Map map) {

		String mapString = "";
		String temp = "";
		for (int row = 0; row < MapConstants.MAP_HEIGHT; row++) {
			for (int col = 0; col < MapConstants.MAP_WIDTH; col++) {
				if (map.getCell(row, col).isExplored()) {
					System.out.println(temp);
					if(temp.length()<4)
						temp += map.getCell(row, col).isObstacle() ? "1" : "0";
					else {
						mapString += binToHex(temp);
						temp = "";
					}
				}
			}
		}
		System.out.println(mapString);
		// Pad with '0' to make the length a multiple of 8
		int tempLength = temp.length();
		int paddingLength = tempLength % 8;

		if (paddingLength != 0) {
			// Find the number of required bits
			paddingLength = 8 - paddingLength;

			for (int i = 0; i < paddingLength; i++) {
				temp += "0";
			}
			mapString += binToHex(temp);
		}

		return mapString;
	}
	
	public static void loadMapFromDisk(Map map, String filename) {
		try {
			FileReader file = new FileReader(filename);
			BufferedReader buf = new BufferedReader(file);
			
			String line = buf.readLine();
			String hexStr = "";
			while(line != null);
			{
				hexStr.concat(line);
				line = buf.readLine();
			}
			String binStr = hexToBin(hexStr);
			
			int strIndex = 2; //Set to 2 to avoid first 2 11
			for(int row=0; row < MapConstants.MAP_HEIGHT; row++) {
				for(int col=0; col< MapConstants.MAP_WIDTH; col++) {
					if(binStr.charAt(strIndex)=='1')
						map.getCell(row, col).setObstacle(true);
					
					strIndex++;
				}
			}
			buf.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void saveMapToDisk(Map map, String filename) {
		try {
			
			FileWriter file = new FileWriter(filename);
			BufferedWriter buf = new BufferedWriter(file);
			String mapDes = generateMDFStringPart2(map);
			System.out.println(mapDes);
			buf.write(mapDes);
			buf.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
    
}
