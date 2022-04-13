package game.datastructures;

import java.util.Random;

/**
 * This class stores the information of the game-board, it's size and the objects on it.
 */
public class GameMap {
    public GameMap(int sizeX, int sizeY, float oreDensity) {
        this.gameMapSize[1] = sizeX;
        this.gameMapSize[0] = sizeY;
        spawnOreInMap(oreDensity);
        printOreMapToConsole();
        this.objectMap = new GameObject[gameMapSize[0]][gameMapSize[1]];
    }

    private int[] gameMapSize = new int[2];
    private GameObject[][] objectMap;

    /**
     * This function generates ores in the map.
     * It will generate less valuable ores at lower X values (closer to the starting line)
     * and more valuable ores at higher X Values (farther away from the starting line)
     *
     * It uses an int to represent the different ores:
     * 0 = Empty
     * 1 = Copper
     * 2 = Iron
     * 3 = Gold
     * 4 = Platinum
     * 5 = Indium
     *
     * It decides what ore to place in a field based on the following calculation:
     * OreInt = RandomNumber / IntervalSize
     *
     * @param oreSpawnLikelyhood a value from 0 to 1. The higher the number is, the more ores spawn
     */
    //TODO: Make this spawn more valuable ores at higher X Values
    private void spawnOreInMap(float oreSpawnLikelyhood) {
        /*
        Random r = new Random();
        int maxRanNumBound = 100; //Clamp the random number from 0-99
        int intervalSize = maxRanNumBound/5;
        for(int i = 0; i < gameMapSize[0]; i++) {
            for(int j = 0; j < gameMapSize[1]; j++) {
                if (r.nextInt(maxRanNumBound) < oreSpawnLikelyhood*maxRanNumBound){
                    //int randomNumber = r.nextInt(maxRanNumBound);
                    //oreMap[i][j] = randomNumber/intervalSize;
                    oreMap[i][j] = (int)Math.round(r.nextGaussian()+(((float)j/gameMapSize[1])*5));
                    if (oreMap[i][j] > 5) {
                        oreMap[i][j] = 5;
                    }
                    if (oreMap[i][j] < 1) {
                        oreMap[i][j] = 1;
                    }
                }
                else {
                    oreMap[i][j] = 0;
                }
            }
        }
         */
    }

    public void placeObjectOnMap(GameObject object, int x, int y) {
        object.setPosition(x, y);
    }

    public void printOreMapToConsole() { //TODO: Fix this so it works with the new mapSystem Tom is going to commit
        /*
        for(int i = 0; i<gameMapSize[0]; i++) {
            for(int j = 0; j<gameMapSize[1]; j++) {
                System.out.print("["+oreMap[i][j]+"]");
            }
            System.out.println("");
        }
         */
    }

    public int[] getGameMapSize() {
        return gameMapSize;
    }

    public Object[][] getObjectMap() {
        return objectMap;
    }

    public String encodeGameMap () {
        StringBuilder s = new StringBuilder();
        for (int y = 0; y < getGameMapSize()[0]; y++) {
            for (int x = 0; x < getGameMapSize()[1]; x++) {
                if (objectMap[y][x] != null) {
                    s.append(objectMap[y][x].encodeToString()); //TODO: Update this using the container system @Tom
                }
            }
        }
        return s.toString();
    }
}
