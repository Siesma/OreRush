package game.server;


import java.util.ArrayList;

public class ServerSettings {

  /*
  TODO: Make a new Datatype called "ServerSettings" which holds information about packet-replacement procedure,
   the setting and a way to default back to working values in case something gets messed up.
   */
  private int NumberOfRobots;
  private int MapWidth, MapHeight;
  private int NumberOfRounds;
  private ArrayList<Object> ores;

  /**
   *
   * @param pathToFile
   * Used  to import settings from a ".ore_game_settings" file
   */
  public ServerSettings (String pathToFile) {
    
  }

  /**
   *
   * @param numberOfRobots
   * @param mapWidth
   * @param mapHeight
   * @param numberOfRounds
   * @param ores
   * Default user settings initialization
   */
  public ServerSettings(int numberOfRobots, int mapWidth, int mapHeight, int numberOfRounds, ArrayList<Object> ores) {
    NumberOfRobots = numberOfRobots;
    MapWidth = mapWidth;
    MapHeight = mapHeight;
    NumberOfRounds = numberOfRounds;
    this.ores = ores;
  }

  public int getNumberOfRobots() {
    return NumberOfRobots;
  }

  public ServerSettings setNumberOfRobots(int numberOfRobots) {
    NumberOfRobots = numberOfRobots;
    return this;
  }

  public int getMapWidth() {
    return MapWidth;
  }

  public ServerSettings setMapWidth(int mapWidth) {
    MapWidth = mapWidth;
    return this;
  }

  public int getMapHeight() {
    return MapHeight;
  }

  public ServerSettings setMapHeight(int mapHeight) {
    MapHeight = mapHeight;
    return this;
  }

  public int getNumberOfRounds() {
    return NumberOfRounds;
  }

  public ServerSettings setNumberOfRounds(int numberOfRounds) {
    NumberOfRounds = numberOfRounds;
    return this;
  }

  public ArrayList<Object> getOres() {
    return ores;
  }

  public ServerSettings setOres(ArrayList<Object> ores) {
    this.ores = ores;
    return this;
  }
}
