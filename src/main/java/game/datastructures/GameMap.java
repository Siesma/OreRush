package game.datastructures;

import game.helper.FileHelper;
import game.helper.MapType;
import game.helper.MathHelper;
import game.packet.AbstractPacket;
import game.server.Server;
import game.server.ServerSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/**
 * This class stores the information of the game-board, it's size and the objects on it.
 */
public class GameMap {
  /**
   * An array holding the width (at: 0) and the height (at: 1) of the map
   */
  private int[] gameMapSize = new int[2];
  /**
   * The array of cells that will hold the game object on the game map
   */
  private Cell[][] cellArray;
  /**
   * Hold the game settings of the game, the Game Map is used in
   */
  private ServerSettings serverSettings;
  /**
   * Log4j logger that allows great, clear and useful logging of information and errors
   * instead of the ugly commandline prints
   */
  public static final Logger logger = LogManager.getLogger(GameMap.class);

  /**
   * Constructor of the GameMap.
   * @param serverSettings game settings of the game, the Game Map is used in
   */
  public GameMap(ServerSettings serverSettings) {
    this.gameMapSize[0] = serverSettings.getMapWidth();
    this.gameMapSize[1] = serverSettings.getMapHeight();
    this.cellArray = new Cell[this.gameMapSize[0]][this.gameMapSize[1]];
    this.serverSettings = serverSettings;
    fillCellArray();
    fillCellArrayWithNothing();
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
  public void spawnOreInMap() {
    float oreSpawnLikelyhood = serverSettings.getOreDensity();
    float threshold = serverSettings.getOreThreshold();
    int w = gameMapSize[0];
    int h = gameMapSize[1];
    for (int i = 0; i < w; i++) {
      for (int j = 0; j < h; j++) {
        if (createCluster(i, j, threshold)) {
          double clusterSize = Math.sqrt(serverSettings.getMaxClusterSize());
          int curOreType = determineOreTypeIndex(i);
          for (int xo = (int) -(1 + MathHelper.getRandomNumber() * clusterSize); xo < (int) (1 + MathHelper.getRandomNumber() * clusterSize); xo++) {
            for (int yo = (int) (1 + MathHelper.getRandomNumber() * clusterSize); yo < (int) (1 + MathHelper.getRandomNumber() * clusterSize); yo++) {
              int ni, nj;
              ni = i + xo;
              nj = j + yo;
              if (MathHelper.isInBounds(ni, nj, serverSettings)) {
                double max = oreSpawnLikelyhood * 2;
                int amount = (int) getAmountOfOre(max, 0.84d, oreSpawnLikelyhood, ni, nj, i, j, 0, 0);
                Ore ore = new Ore();
                ore.setOwner(getUniqueServerName());
                ore.setAmount(amount);
                ore.setID(curOreType);
                placeObjectOnMap(ore, i, j);
              }
            }
          }
        }
      }
    }
  }

  // TODO: figure out a unique name different from every other players name.
  private String getUniqueServerName() {
    return "Server";
  }

  /**
   * Determines the amount of ore a field should have.
   * However, this function is more or less useless as the amount of ore is set to 1, but because we may change it
   * the function is already implemented.
   * This function would basically be the evaluation of the exponential function
   * but for its input it also has another exponential function. The exponential function in question
   * is the function "lessen" defined a little earlier.
   *
   * @param max                the maximum ore allowed per field. - can be any real value.
   * @param exp                the exponent of the first function. - 1 smaller than exp smaller than 0.
   * @param oreSpawnLikelyhood the likelyhood of something actually spawning.
   * @param ni                 the x coordinate that is being compared to.
   * @param nj                 the y coordinate that is being compared to.
   * @param i                  the reference x coordinate.
   * @param j                  the reference y coordinate.
   * @param shift_a            the shift in the x-direction from the function.
   * @param shift_b            the shift in the x-direction of the distribution.
   * @return the Ore Amount
   */
  public double getAmountOfOre(double max, double exp, float oreSpawnLikelyhood, int ni, int nj, int i, int j, int shift_a, int shift_b) {
    return Math.round(MathHelper.inverseExponential(max, exp, MathHelper.inverseExponential(1, 0.5, MathHelper.getRandomNumber(), oreSpawnLikelyhood, shift_a) - (oreSpawnLikelyhood * max - oreSpawnLikelyhood), MathHelper.exactDistanceBetweenPoints(ni, nj, i, j), shift_b));
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
    return MathHelper.inverseExponential(1d, 0.4, Math.max(ni, 0) * MathHelper.getRandomNumber() - Math.abs(nj), 0.5, 2) > threshold;
  }

  /**
   * Calculates the Ore Type. It is semi random, taking into account the current X position to allow for a bias. More valuable ore spawns on the right.
   *
   * @param xCoordinate the xCoordinate of the field the Ore is to be place in
   * @return the Ore Type as an int
   */
  public int determineOreTypeIndex(int xCoordinate) {
    // some constants for an appealing distribution, they are not based on any experimental values but just values we thought look good.
    double a = 1.2;
    double b = 0.7;
    double c = 1.4;
    double d = -0.2;

    /*
    Creates a value based on a function that can be examined using the following link:
    https://www.desmos.com/calculator/luphcn1lum
     */

    double r = MathHelper.getRandomNumber();
    int n = OreType.values().length;
    double inverseN = 1d / n;
    double w = serverSettings.getMapWidth();
    double xw = (double) ((w) - xCoordinate) / w;
    double fx = MathHelper.exponential(a, b, xw, c, d);
    double shiftFactor = (((r + 1) + d)) / (Math.abs(xw - d));
    return MathHelper.clamp((int) Math.floor(Math.abs((fx - shiftFactor) / (Math.abs(xw - d)) * (inverseN))), 0, n - 1);

  }

  /**
   * Places an object onto the map and also updates the position of the object to the new position.
   * @param object the object that is being placed down on the map
   * @param x the respective x coordinate
   * @param y the respective y coordinate
   */
  public void placeObjectOnMap(GameObject object, int x, int y) {
    object.setPosition(x, y);
    cellArray[x][y].place(object);
  }

  public void placeObjectOnMap(GameObject gameObject, int[] xy) {
    this.placeObjectOnMap(gameObject, xy[0], xy[1]);
  }

  /**
   * removes an object from the map
   * @param gameObject the object that is being removed
   * @param x the respective x coordinate
   * @param y the respective y coordinate
   */
  public void removeObjectFromMap(GameObject gameObject, int x, int y) {
    cellArray[x][y].remove(gameObject);
  }

  public void removeObjectFromMap(GameObject gameObject, int[] xy) {
    this.removeObjectFromMap(gameObject, xy[0], xy[1]);
  }

  /**
   * This function is used to determine what a player should see or not.
   *
   * @param playerName name of the player asking for the map.
   * @return a new GameMap that consists of the players visible objects.
   */
  public GameMap getIndividualGameMapForPlayer(String playerName) {
    // generates a new gamemap that is used to return.
    GameMap out = new GameMap(serverSettings);
    ArrayList<GameObject> playerOwnedGameObjects = new ArrayList<>();
    // adds all the gameobjects that were placed by the player himself.
    for (int i = 0; i < gameMapSize[0]; i++) {
      for (int j = 0; j < gameMapSize[1]; j++) {
        for (GameObject gameObject : cellArray[i][j].getPlacedObjects()) {
          if (gameObject.getOwner() == null) {
            logger.debug("The Owner of the Object \"" + gameObject + "\" in the position (" + i + ", " + j + ") was not set correctly.");
            continue;
          }
          if (gameObject instanceof Robot || gameObject.getOwner().equals(playerName)) {
            playerOwnedGameObjects.add(gameObject);
          }
        }
      }
    }
    for (GameObject gameObject : playerOwnedGameObjects) {
      out.placeObjectOnMap(gameObject, gameObject.getPosition());
      if (gameObject instanceof Radar) {
        revealAround(out, gameObject.getPosition(), serverSettings.getRadarDistance());
      }
    }

    return out;
  }

  /**
   * This function fills the new build gameMap with the relevant gameObjects from the complete map.
   *
   * @param gameMap the current (unfilled) GameMap.
   * @param xy the position of the Radar.
   * @param revealSize the size in which the radar is revealing.
   */
  public void revealAround(GameMap gameMap, int[] xy, int revealSize) {
    for (int xi = -revealSize; xi <= revealSize; xi++) {
      for (int yi = -revealSize; yi <= revealSize; yi++) {
        int[] nPos = new int[] {xy[0] + xi, xy[1] + yi};
        if(!MathHelper.isInBounds(nPos[0], nPos[1], serverSettings)) {
          continue;
        }
        if(!(MathHelper.absoluteCellDistance(xy, nPos) <= revealSize)) {
          continue;
        }
        for(GameObject objectOnCell : this.cellArray[nPos[0]][nPos[1]].getPlacedObjects()) {
          gameMap.placeObjectOnMap(objectOnCell, nPos);
        }
      }
    }
  }

  /**
   * Prints the map to the console, this is currently not really used and will most likely be removed in a future commit.
   * @param gameMap the map that is being printed onto the console.
   */
  public static void printMapToConsole(GameMap gameMap) {
    Cell[][] cells = gameMap.getCellArray();
    for (int i = 0; i < cells[0].length; i++) {
      for (int j = 0; j < cells.length; j++) {
        Cell cell = cells[j][i];
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
          switch (cell.oreOnCell().get(0).getOreType()) {
            case Copper:
              out.append("C");
              break;
            case Iron:
              out.append("F");
              break;
            case Gold:
              out.append("G");
              break;
            case Platinum:
              out.append("P");
              break;
            case Indium:
              out.append("I");
              break;
          }
        } else {
          out.append("__");
        }
        if (robot) {
          out.append("R");
          if (!(cell.robotsOnCell().get(0).getInventory() instanceof Nothing)) {
            GameObject inv = cell.robotsOnCell().get(0).getInventory();
            if (inv instanceof Ore) {
              out.append("O");
            } else if (inv instanceof Radar) {
              out.append("H");
            } else if (inv instanceof Trap) {
              out.append("T");
            } else {
              out.append("_");
            }
          } else {
            out.append("_");
          }
        } else {
          out.append("__");
        }

//        out.setLength(0);
//        int[] now = new int[] {3, 4};
//        int[] then = new int[] {i, j};
//        out.append(fixedLengthString("" + MathHelper.absoluteCellDistance(now, then), 5));
        logger.info("[" + out.toString() + "]");
      }
    }
  }

  /**
   * Returns an array of Strings that is used to make the update packet.
   *
   * @return the array of Strings
   */
  public String[] cellStrings() {
    ArrayList<String> strings = new ArrayList<>();
    for (int i = 0; i < gameMapSize[0]; i++) {
      for (int j = 0; j < gameMapSize[1]; j++) {
        for (GameObject objectOnCell : this.getCellArray()[i][j].getPlacedObjects()) {
          StringBuilder out = new StringBuilder();
          out.append("").append(i).append(",").append(j).append("");
          out.append("_");
          out.append(objectOnCell.encodeToString());
          strings.add(out.toString());
        }
      }
    }
    String[] out = new String[strings.size()];
    for (int i = 0; i < out.length; i++) {
      out[i] = strings.get(i);
    }
    return out;
  }

  /**
   * Changes the position of an already existing object.
   * The method restricts the amount of cells an object can move through if the target position is too far.
   *
   * @param object      the object that is to be moved
   * @param newPosition the new position of the object
   */
  public void replaceObject(GameObject object, int[] newPosition) {
    int[] curPosition = object.getPosition();
    if (curPosition == null) {
      return;
    }
    if (!MathHelper.isInBounds(curPosition, new int[]{0, 0}, gameMapSize)) {
      curPosition = new int[]{
              MathHelper.clamp(curPosition[0], 0, gameMapSize[0] - 1),
              MathHelper.clamp(curPosition[1], 0, gameMapSize[1] - 1)
      };
    }
    ArrayList<GameObject> placedObjects = cellArray[curPosition[0]][curPosition[1]].getPlacedObjects();
    if (!placedObjects.contains(object)) {
      logger.error("The placed object was not on the original position");
      return;
    }
    if (!MathHelper.isInBounds(newPosition, new int[]{0, 0}, gameMapSize)) {
      newPosition = new int[]{
              MathHelper.clamp(newPosition[0], 0, gameMapSize[0] - 1),
              MathHelper.clamp(newPosition[1], 0, gameMapSize[1] - 1)
      };
    }
    removeObjectFromMap(object, curPosition);
    placeObjectOnMap(object, newPosition);
    if (object instanceof Robot) {
      Robot robotObject = (Robot) object;
      if (robotObject.getRobotAction() != RobotAction.Dig) {
        return;
      }
      if (getCellArray()[newPosition[0]][newPosition[1]].trapOnCell() != null) {
        robotObject.setDead(true);
        getCellArray()[newPosition[0]][newPosition[1]].remove(getCellArray()[newPosition[0]][newPosition[1]].trapOnCell());
      }
      if (!(robotObject.getInventory() instanceof Nothing)) {
        placeObjectOnMap(robotObject.getInventory(), newPosition);
        Nothing nothing = new Nothing();
        nothing.setOwner(getUniqueServerName());
        robotObject.loadInventory(nothing);
      }
      ArrayList<Ore> ore = getCellArray()[newPosition[0]][newPosition[1]].oreOnCell();
      if (ore == null) {
        return;
      }
      if (ore.size() > 0) {
        robotObject.loadInventory(ore.get(0));
        getCellArray()[newPosition[0]][newPosition[1]].remove(getCellArray()[newPosition[0]][newPosition[1]].oreOnCell().get(0));
      }
    }
  }

  /**
   * Getter for the Game Map size
   * @return the size of the Game Map
   */
  public int[] getGameMapSize() {
    return gameMapSize;
  }

  /**
   * Creates for each column and row a new Cell object so NullPointerExceptions are being lessened if the map is improperly handled
   */
  private void fillCellArray() {
    for (int i = 0; i < cellArray.length; i++) {
      for (int j = 0; j < cellArray[i].length; j++) {
        cellArray[i][j] = new Cell(i, j);
      }
    }
  }

  /**
   * Fills the cellArray with placeholders, mainly "Nothing"
   */
  private void fillCellArrayWithNothing() {
    for (int i = 0; i < cellArray.length; i++) {
      for (int j = 0; j < cellArray[i].length; j++) {
        Nothing nothing = new Nothing();
        nothing.setPosition(i, j);
        nothing.setID(0);
        nothing.setOwner(getUniqueServerName());
        placeObjectOnMap(nothing, i, j);
      }
    }
  }

  /**
   * This function will handle any incoming messages, but it has to follow the regex of the Update packet.
   * @param message the content in the format of the Update packet's encoding.
   * @return a new gameMap object containing all the information given by the message
   */
  public static GameMap getMapFromString(String message, ServerSettings serverSettings) {
    // splits the incoming singular information into an array.
    String[] individualCell = AbstractPacket.splitMessageBySpacer(message);
    // defines new serverSettings to be used here to obtain needed informations.
    GameMap newMap = new GameMap(serverSettings);
    // sets default values so that they can be compared to.
    int cellX = -1, cellY = -1;
    for (String impliedCell : individualCell) {
      // splits the impliedCell structure to the two parts.
      // the positional information and the object information.
      String[] type = impliedCell.split("_");
      for (String s : type) {
        // checks if the positional information is from the form "Number, Number" so it can be parsed as an integer.
        if (s.matches("^[0-9]+,[0-9]+$")) {
          cellX = Integer.parseInt(s.split(",")[0]);
          cellY = Integer.parseInt(s.split(",")[1]);
        } else {
          // it was not possible to parse the position as an integer, something went wrong and should not be happening.
          if (cellX == -1 || cellY == -1) {
            logger.error("Somehow the cell index was not updated:" + s);
            continue;
          }

          if (!MathHelper.isInBounds(cellX, cellY, serverSettings)) {
            logger.error("Somehow the cell index was outside of the map boundaries.\t" + cellX + "|" + cellY + "|" + serverSettings.getMapWidth() + "|" + serverSettings.getMapHeight());
            continue;
          }

          // splits the object information to its parts.
          // -> type:id:ownerName:optionalInventory
          String[] objectData = s.split(":");
          String objectType = objectData[0];
          int objectID = Integer.parseInt(objectData[1]);
          String owner = objectData[2];
          Object object;
          // the type will be tried to make a new instance of it.
          try {
            object = (new FileHelper()).createNewInstanceFromName(MapType.GameObjects, objectType);
          } catch (Exception e) {
            // this should never happen as this means that the object is valid but the file for it does not exist.
            logger.error("An unidentified object!");
            logger.error("Ignoring this element!");
            continue;
          }
          // this basically means that the file is corrupt (because it implies
          // that a valid object is no GameObject which should not be possible) or that there was no optionalInventory.
          if (!(object instanceof GameObject)) {
            logger.debug(object + " \"" + objectType + "\"");
            logger.debug("Somehow the object is no gameobject");
            continue;
          }
          // creates a final game object and sets its id.
          GameObject finalGameObject = (GameObject) object;
          finalGameObject.setOwner(owner);
          finalGameObject.setID(objectID);
          // if there is an optionalInventory it will be tried to make a new instance of it.
          if (objectData.length > 3) {
            if (finalGameObject instanceof Robot) {
              Object inv;
              try {
                inv = (new FileHelper()).createNewInstanceFromName(MapType.GameObjects, s.split(":")[3]);
              } catch (Exception e) {
                // this should never happen as this means that the object is valid but the file for it does not exist.
                logger.error("An unidentified object!");
                logger.error("Ignoring this element!");
                continue;
              }
              // this basically means that the file is corrupt (because it implies
              // that a valid object is no GameObject which should not be possible) or that there was no optionalInventory.
              if (!(inv instanceof GameObject)) {
                logger.debug("Somehow the inventory of the Robot is not a GameObject");
                continue;  // should not be possible if the packet is valid, will be included just in case!
              }
              // sets the inventory of the robot to the optionalInventory
              ((Robot) finalGameObject).loadInventory((GameObject) inv);
            }
          }
          // places the new gameObject on the map
            newMap.placeObjectOnMap(finalGameObject, cellX, cellY);
        }
      }
    }

    return newMap;
  }

  /**
   * Setter for the Cell array
   * @param array the new cell array
   */
  public void setCellArray(Cell[][] array) {
    this.cellArray = array;
  }

  /**
   * Getter for the Cell array
   * @return the cell array
   */
  public Cell[][] getCellArray() {
    return cellArray;
  }
}
