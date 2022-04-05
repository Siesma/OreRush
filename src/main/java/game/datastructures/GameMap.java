package game.datastructures;

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
     * Kupfer
     * 	Eisen
     * 	Gold
     * 	Platinum
     * 	Indium
     */
    private void spawnOreInMap() {

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
