package game.datastructures;

import game.server.ServerSettings;

import java.util.ArrayList;
import java.util.Random;

/**
 * This class stores the information of the game-board, it's size and the objects on it.
 */
public class GameMap {
  private int[] gameMapSize = new int[2];
  private Cell[][] cellArray;

  private ServerSettings serverSettings;

  public GameMap(int sizeX, int sizeY, ServerSettings serverSettings) {
    this.gameMapSize[0] = sizeX;
    this.gameMapSize[1] = sizeY;
    this.cellArray = new Cell[sizeX][sizeY];
    this.serverSettings = serverSettings;
    fillCellArray();
//    spawnOreInMap();
//    printOreMapToConsole();
  }


  /**
   * This function generates ores in the map.
   * It will generate less valuable ores at lower X values (closer to the starting line)
   * and more valuable ores at higher X Values (farther away from the starting line)
   * <p>
   * It uses an int to represent the different ores:
   * 0 = Empty
   * 1 = Copper
   * 2 = Iron
   * 3 = Gold
   * 4 = Platinum
   * 5 = Indium
   * <p>
   * It decides what ore to place in a field based on the following calculation:
   * OreInt = RandomNumber / IntervalSize
   * <p>
   * oreSpawnLikelyhood a value from 0 to 1. The higher the number is, the more ores spawn
   */
  //TODO: Make this spawn more valuable ores at higher X Values
  public void spawnOreInMap() {
    float oreSpawnLikelyhood = serverSettings.getOreDensity();
    float threshold = serverSettings.getOreThreshold();
    int w = gameMapSize[0];
    int h = gameMapSize[1];
    for (int i = 0; i < w; i++) {
      for (int j = 0; j < h; j++) {
        if (createCluster(i, j, threshold)) {
          double clusterSize = Math.sqrt(serverSettings.getMaxClusterSize());
          OreType curOreType = determineOreType();
          for (int xo = (int) -(1 + getRandomNumber() * clusterSize); xo < (int) (1 + getRandomNumber() * clusterSize); xo++) {
            for (int yo = (int) (1 + getRandomNumber() * clusterSize); yo < (int) (1 + getRandomNumber() * clusterSize); yo++) {
              int ni, nj;
              ni = i + xo;
              nj = j + yo;
              if (isInBounds(ni, nj, 0, w, 0, h)) {
                double max = oreSpawnLikelyhood * 2;
                Cell c = cellArray[ni][nj];
                int amount = (int) getAmountOfOre(max, 0.84d, oreSpawnLikelyhood, ni, nj, i, j, 0, 0);
                c.place(new Ore(curOreType, amount));
              }
            }
          }
        }
      }
    }
  }

  /**
   * Determines the amount of ore a field should have.
   * However, this function is more or less useless as the amount of ore is set to 1, but because we may change it
   * the function is already implemented.
   */
  public double getAmountOfOre(double max, double exp, float oreSpawnLikelyhood, int ni, int nj, int i, int j, int shift_a, int shift_b) {
    return Math.round(lessen(max, exp, lessen(1, 0.5, getRandomNumber(), oreSpawnLikelyhood, shift_a) - (oreSpawnLikelyhood * max - oreSpawnLikelyhood), dist(ni, nj, i, j), shift_b));
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
    // CAN control density, for m > 2 the density shrinks
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
    int w = gameMapSize[0];
    int h = gameMapSize[1];
    int ni = i - (w / 10);
    int nj = j - (h / 2);
    return lessen(1d, 0.4, Math.max(ni, 0) * getRandomNumber() - Math.abs(nj), 0.5, 2) > 0.75;
  }

  public boolean isInBounds(int x, int y, int min_x, int max_x, int min_y, int max_y) {
    return x > min_x && x < max_x && y > min_y && y < max_y;
  }

  public OreType determineOreType() {
    // TODO: make it so that more valuable oretypes spawn futher on the right
    return OreType.values()[(int) (Math.random() * OreType.values().length)];
  }

  public void placeObjectOnMap(GameObject object, int x, int y) {
    cellArray[x][y].place(object);
  }

  public void removeObjectFromMap(GameObject gameObject, int x, int y) {
    cellArray[x][y].remove(gameObject);
  }

  public void printOreMapToConsole() {
    for (int j = 0; j < gameMapSize[1]; j++) {
      for (int i = 0; i < gameMapSize[0]; i++) {
//        System.out.print("[" + oreMap[i][j] + "]");
        Cell cell = cellArray[i][j];
        boolean trap = cell.trapOnCell() != null;
        boolean radar = cell.radarOnCell() != null;
        boolean ore = cell.oreOnCell() != null;
        boolean robot = cell.robotsOnCell() != null;
        StringBuilder out = new StringBuilder();
        if (trap) {
          out.append("T");
        } else {
          out.append("_");
        }
        if (radar) {
          out.append("H");
        } else {
          out.append("_");
        }
        if (ore) {
          out.append("O");
        } else {
          out.append("_");
        }
        if (robot) {
          out.append("R");
        } else {
          out.append("_");
        }
        System.out.print("[" + out.toString() + "]");
      }
      System.out.println("");
    }
  }

  public String[] cellStrings() {
    ArrayList<String> strings = new ArrayList<>();
    for (int i = 0; i < gameMapSize[0]; i++) {
      for (int j = 0; j < gameMapSize[1]; j++) {
        StringBuilder out = new StringBuilder();
        out.append("").append(i).append(",").append(j).append("");
        for (GameObject objectOnCell : this.getCellArray()[i][j].getPlacedObjects()) {
          out.append("_");
          out.append(objectOnCell.encodeToString());
        }
        strings.add(out.toString());
      }
    }
    String[] out = new String[strings.size()];
    for (int i = 0; i < out.length; i++) {
      out[i] = strings.get(i);
    }
    return out;
  }

  public int[] getGameMapSize() {
    return gameMapSize;
  }

  public void fillCellArray() {
    for (int i = 0; i < cellArray.length; i++) {
      for (int j = 0; j < cellArray[i].length; j++) {
        cellArray[i][j] = new Cell(i, j);
      }
    }
  }

  public void setCellArray(Cell[][] array) {
    this.cellArray = array;
  }

  public Cell[][] getCellArray() {
    return cellArray;
  }
}
