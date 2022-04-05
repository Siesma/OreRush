package game.datastructures;

import java.util.Random;

/**
 * This class stores the information of the game-board, it's size and the objects on it.
 */
public class GameMap {
    public GameMap(int sizeX, int sizeY) {
        this.gameMapSize[0] = sizeX;
        this.gameMapSize[1] = sizeY;
        this.oreMap = new int[sizeX][sizeY];
        this.objectMap = new Object[sizeX][sizeY];
    }

    private int[] gameMapSize = new int[2];
    private int[][] oreMap;
    private Object[][] objectMap;

    /**
     * This function generates ores in the map.
     * It will generate less valuable ores at lower X values (closer to the starting line)
     * and more valuable ores at higher X Values (farther away from the starting line)
     *
     * It uses an int to represent the different ores:
     * 1 = Copper
     * 2 = Iron
     * 3 = Gold
     * 4 = Platinum
     * 5 = Indium
     *
     * It decides what ore to place in a field based on the following calculation:
     * OreInt = RandomNumber / IntervalSize
     */
    private void spawnOreInMap() {
        Random r = new Random();
        int maxRanNumBound = 100; //Clamp the random number from 0-99
        int intervalSize = maxRanNumBound/5;
        for(int i = 0; i<gameMapSize[0]; i++) {
            for(int j = 0; j<gameMapSize[1]; j++) {
                int randomNumber = r.nextInt(maxRanNumBound);
                objectMap[i][j] = Math.floor(randomNumber/intervalSize);
            }
        }
    }

    public int[] getGameMapSize() {
        return gameMapSize;
    }
    public int[][] getOreMap() {
        return oreMap;
    }
    public Object[][] getObjectMap() {
        return objectMap;
    }
}
