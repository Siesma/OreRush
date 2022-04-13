package game.datastructures;

import java.util.Random;

/**
 * This class stores the information of the game-board, it's size and the objects on it.
 */
public class GameMap {
    public GameMap(int sizeX, int sizeY, float oreDensity) {
        this.gameMapSize[1] = sizeX;
        this.gameMapSize[0] = sizeY;
        this.oreMap = new int[gameMapSize[0]][gameMapSize[1]];
        spawnOreInMap(oreDensity, 0.75f);
        this.objectMap = new Object[gameMapSize[0]][gameMapSize[1]];
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
    private void spawnOreInMap(float oreSpawnLikelyhood, float threshold) {
        int w = gameMapSize[1];
        int h = gameMapSize[0];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                OreType curOreType = determineOreType();
                if (createCluster(i, j, threshold)) {
                    for (int xo = (int) -(1 + getRandomNumber() * oreSpawnLikelyhood); xo < (int) (1 + getRandomNumber() * oreSpawnLikelyhood); xo++) {
                        for (int yo = (int) (1 + getRandomNumber() * oreSpawnLikelyhood); yo < (int) (1 + getRandomNumber() * oreSpawnLikelyhood); yo++) {
                            int ni, nj;
                            ni = i + xo;
                            nj = j + yo;
                            if (isInBounds(ni, nj, 0, w, 0, h)) {
                                double max = oreSpawnLikelyhood * 2;
                                objectMap[ni][nj] = new Ore(
                                        curOreType, getAmountOfOre(max, 0.84d, oreSpawnLikelyhood, ni, nj, i, j, 0, 0)
                                );
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * Determines the amount of ore a field should have.
     * However, this function is more or less useless as the amount of ore is set to 1, but because we may change it
     * the function is already implemented.
     */
    public int getAmountOfOre(double max, double exp, float oreSpawnLikelyhood, int ni, int nj, int i, int j, int shift_a, int shift_b) {
        return (int) Math.round(lessen(max, exp, lessen(1, 0.5, getRandomNumber(), oreSpawnLikelyhood, shift_a) - (oreSpawnLikelyhood * max - oreSpawnLikelyhood), dist(ni, nj, i, j), shift_b));
    }

    /**
     * Returns the
     */
    private double lessen(double max, double exp, double in, double fac, double shift) {
        return max - (Math.pow(exp, in * fac + shift));
    }


    /**
     * Returns the distance between the points (P(nx, ny) and P(x, y)) by taking the
     * square root of the absolute difference between the two points raised to the m th power where m is a constant
     */
    public double dist(int nx, int ny, int x, int y) {
        double m = 2; // Default distance function for m = 2
        // can control density, for m > 2 the density shrinks
        return Math.sqrt(Math.pow(Math.abs(nx - x), m) + Math.pow(Math.abs(ny - y), m));
    }

    /**
     * Returns a random number between 0 and 1
     */
    public double getRandomNumber() {
        return getRandomNumber((new Random()).nextLong());
    }

    private double getRandomNumber(long seed) {
        Random random = new Random();
        random.setSeed(seed);
        return random.nextDouble();
    }

    /**
     * Calculates whether a given Cellindex should create a cluster by asking whether (1 - exponential function) greater
     * than a threshold is
     */
    private boolean createCluster(int i, int j, double threshold) {
        int w = objectMap[0].length;
        int h = objectMap.length;
        int ni = i - (w / 10);
        int nj = j - (h / 2);
        return lessen(1d, 0.4, Math.max(ni, 0) * getRandomNumber() - Math.abs(nj), 0.5, 2) > threshold;
    }

    public boolean isInBounds(int x, int y, int min_x, int max_x, int min_y, int max_y) {
        return x > min_x && x < max_x && y > min_y && y < max_y;
    }

    public OreType determineOreType() {
        // TODO: make it so that more valuable oretypes spawn futher on the right
        return OreType.values()[(int) (Math.random() * OreType.values().length)];
    }
    public void placeObjectOnMap(GameObject object, int x, int y) {
        objectMap[y][x] = object;
    }

    public void printOreMapToConsole() {
        for(int i = 0; i<gameMapSize[0]; i++) {
            for(int j = 0; j<gameMapSize[1]; j++) {
                System.out.print("["+oreMap[i][j]+"]");
            }
            System.out.println("");
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
