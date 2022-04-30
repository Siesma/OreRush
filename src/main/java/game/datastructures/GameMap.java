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
import java.util.Random;

/**
 * This class stores the information of the game-board, it's size and the objects on it.
 */
public class GameMap {
  private int[] gameMapSize = new int[2];
  private Cell[][] cellArray;

  private ServerSettings serverSettings;
  public static final Logger logger = LogManager.getLogger(Server.class);

  public GameMap(ServerSettings serverSettings) {
    this.gameMapSize[0] = serverSettings.getMapWidth();
    this.gameMapSize[1] = serverSettings.getMapHeight();
    this.cellArray = new Cell[serverSettings.getMapWidth()][serverSettings.getMapHeight()];
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
          int curOreType = determineOreTypeIndex(i);
          for (int xo = (int) -(1 + MathHelper.getRandomNumber() * clusterSize); xo < (int) (1 + MathHelper.getRandomNumber() * clusterSize); xo++) {
            for (int yo = (int) (1 + MathHelper.getRandomNumber() * clusterSize); yo < (int) (1 + MathHelper.getRandomNumber() * clusterSize); yo++) {
              int ni, nj;
              ni = i + xo;
              nj = j + yo;
              if (MathHelper.isInBounds(ni, nj, serverSettings)) {
                double max = oreSpawnLikelyhood * 2;
                Cell c = cellArray[ni][nj];
                int amount = (int) getAmountOfOre(max, 0.84d, oreSpawnLikelyhood, ni, nj, i, j, 0, 0);
                Ore ore = new Ore();
                ore.setOwner(getUniqueServerName());
                ore.setAmount(amount);
                ore.setID(curOreType);
                c.place(ore);
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
   * @param exp                the exponent of the first function. - 1 > exp > 0.
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


  public int determineOreTypeIndex(int xCoordinate) {
    // a - b ^ (c*d + f)
    // TODO: make it so that more valuable oretypes spawn futher on the right


    return (int) Math.floor(MathHelper.exponential(1,1,(MathHelper.exponential(1,1,MathHelper.getRandomNumber(),1,0)-xCoordinate),1,0)*OreType.values().length);
            //(Math.random() * OreType.values().length);
  }

  public void placeObjectOnMap(GameObject object, int x, int y) {
    cellArray[x][y].place(object);
    object.setPosition(x, y);
  }

  public void placeObjectOnMap(GameObject gameObject, int[] xy) {
    this.placeObjectOnMap(gameObject, xy[0], xy[1]);
  }

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
    for (int j = 0; j < gameMapSize[1]; j++) {
      for (int i = 0; i < gameMapSize[0]; i++) {
        for (GameObject gameObject : cellArray[i][j].getPlacedObjects()) {
          if(gameObject.getOwner() == null) {
            logger.debug("The Owner of the Object \"" + gameObject + "\" in the position (" + i + ", " + j + ") was not set correctly.");
            continue;
          }
          if(gameObject instanceof Robot) {
            logger.debug("This robots owner is: \"" + gameObject.getOwner() + "\"");
          }
          if (gameObject.getOwner().equals(playerName)) {
            playerOwnedGameObjects.add(gameObject);
          }
        }
      }
    }
    // goes through all of the players own gameobjects and if the gameobject is a radar it will reveal the surrounding area.
    for (GameObject gameObject : playerOwnedGameObjects) {
      out.placeObjectOnMap(gameObject, gameObject.getPosition());
      if (gameObject instanceof Radar) {
        int dist = serverSettings.getRadarDistance() / 2;
        for (int xi = -dist; xi <= dist; xi++) {
          for (int yi = -dist; yi <= dist; yi++) {
            int[] xyi = new int[]{gameObject.getPosition()[0] + xi, gameObject.getPosition()[1] + yi};
            // checks if the radar is scanning outside the gamemap.
            if (!MathHelper.isInBounds(xyi, new int[]{0, 0}, gameMapSize)) {
              continue;
            }
            // because we are looking in an n x n grid around the radar position some of those are outside of the normal reach.
            if(MathHelper.absoluteCellDistance(gameObject.getPosition(), xyi) <= dist) {
              continue;
            }
            // if every checks pass, it will place every gameobject from the full gamemap on the map that is being returned.
            for(GameObject object : this.cellArray[xyi[0]][xyi[1]].getPlacedObjects()) {
              out.placeObjectOnMap(object, xyi);
            }
          }
        }
      }
    }

    return out;
  }

  public static void printMapToConsole(GameMap gameMap) {
    Cell[][] cells = gameMap.getCellArray();
    for (int j = 0; j < cells.length; j++) {
      for (int i = 0; i < cells[j].length; i++) {
        Cell cell = cells[i][j];
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
        System.out.print("[" + out.toString() + "]");
      }
      System.out.println("");
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
        getCellArray()[newPosition[0]][newPosition[1]].place(robotObject.getInventory());
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
        cellArray[i][j].place(nothing);
      }
    }
  }

  public static GameMap getMapFromString(String message) {
    // splits the incoming singular information into an array.
    String[] individualCell = AbstractPacket.splitMessageBySpacer(message);
    // defines new serverSettings to be used here to obtain needed informations.
    ServerSettings serverSettings = new ServerSettings();
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
            logger.error("Somehow the cell index was not updated");
            continue;
          }

          if(!MathHelper.isInBounds(cellX, cellY, serverSettings)) {
            logger.error("Somehow the cell index was outside of the map boundaries.\t" + cellX + "|" + cellY+ "|" + serverSettings.getMapWidth()+ "|" + serverSettings.getMapHeight());
            continue;
          }

          // splits the object information to its parts.
          // -> type:id:optionalInventory
          String[] objectData = s.split(":");
          String objectType = objectData[0];
          int objectID = Integer.parseInt(objectData[1]);
          Object object;
          // the type will be tried to make a new instance of it.
          try {
            object = (new FileHelper()).createNewInstanceFromName(MapType.GameObjects, objectType);
//            object = (new FileHelper()).createInstanceOfClass("game.datastructures." + objectType);
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
          finalGameObject.setID(objectID);
          // if there is an optionalInventory it will be tried to make a new instance of it.
          if (objectData.length > 2) {
            if (finalGameObject instanceof Robot) {
              Object inv;
              try {
                inv = (new FileHelper()).createNewInstanceFromName(MapType.GameObjects, s.split(":")[2]);
//                inv = (new FileHelper()).createInstanceOfClass("game.datastructures." + s.split(":")[2]);
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
          newMap.getCellArray()[cellX][cellY].place(finalGameObject);
        }
      }
    }

    return newMap;
  }

  public void setCellArray(Cell[][] array) {
    this.cellArray = array;
  }

  public Cell[][] getCellArray() {
    return cellArray;
  }
}
