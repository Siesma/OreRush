package game.server;


import java.util.ArrayList;

public class ServerSettings {

  /*
  TODO: Make a new Datatype called "ServerSettings" which holds information about packet-replacement procedure,
   the setting and a way to default back to working values in case something gets messed up.
   */
  private int numberOfRobots;
  private int mapWidth, mapHeight;
  private int numberOfRounds;
  private float oreDensity;
  private int maxAllowedMoves;
  private int radarDistance;
  private ArrayList<Object> ores;

  private int maxClusterSize;
  private float oreThreshold;


  public ServerSettings() {
    setDefaultValues();
  }

  /**
   * @param pathToFile Used  to import settings from a ".ore_game_settings" file
   */
  public ServerSettings(String pathToFile) {
    setDefaultValues();
    // TODO: Make a file system for the settings
  }

  /**
   * @param numberOfRobots The number of Robots each player should control
   * @param mapWidth The width of the game field
   * @param mapHeight The height of the game field
   * @param numberOfRounds The number of round that the game should last
   * @param ores           Default user settings initialization
   */
  public ServerSettings(int numberOfRobots, int mapWidth, int mapHeight, int numberOfRounds, ArrayList<Object> ores) {
    setDefaultValues();
    this.numberOfRobots = numberOfRobots;
    this.mapWidth = mapWidth;
    this.mapHeight = mapHeight;
    this.numberOfRounds = numberOfRounds;
    this.ores = ores;
  }

  private void setDefaultValues() {
    this.numberOfRobots = 1;
    this.mapWidth = 100;
    this.mapHeight = 15;
    this.numberOfRounds = 5;
    this.oreDensity = 1f;
    this.maxAllowedMoves = 4;
    this.radarDistance = 4;
    this.maxClusterSize = 4;
    this.oreThreshold = 0.75f;
  }

  public int getNumberOfRobots() {
    return numberOfRobots;
  }

  public ServerSettings setNumberOfRobots(int numberOfRobots) {
    this.numberOfRobots = numberOfRobots;
    return this;
  }

  public int getMapWidth() {
    return mapWidth;
  }

  public ServerSettings setMapWidth(int mapWidth) {
    this.mapWidth = mapWidth;
    return this;
  }

  public int getMapHeight() {
    return mapHeight;
  }

  public ServerSettings setMapHeight(int mapHeight) {
    this.mapHeight = mapHeight;
    return this;
  }

  public int getNumberOfRounds() {
    return numberOfRounds;
  }

  public ServerSettings setNumberOfRounds(int numberOfRounds) {
    this.numberOfRounds = numberOfRounds;
    return this;
  }

  public ArrayList<Object> getOres() {
    return ores;
  }

  public ServerSettings setOres(ArrayList<Object> ores) {
    this.ores = ores;
    return this;
  }

  public ServerSettings setOreDensity(float oreDensity) {
    this.oreDensity = oreDensity;
    return this;
  }

  public ServerSettings setMaxAllowedMoves(int maxAllowedMoves) {
    this.maxAllowedMoves = maxAllowedMoves;
    return this;
  }

  public int getRadarDistance() {
    return radarDistance;
  }

  public ServerSettings setRadarDistance(int radarDistance) {
    this.radarDistance = radarDistance;
    return this;
  }

  public ServerSettings setMaxClusterSize(int maxClusterSize) {
    this.maxClusterSize = maxClusterSize;
    return this;
  }

  public ServerSettings setOreThreshold(float oreThreshold) {
    this.oreThreshold = oreThreshold;
    return this;
  }

  public int getMaxAllowedMoves() {
    return maxAllowedMoves;
  }

  public float getOreDensity() {
    return oreDensity;
  }

  public float getOreThreshold() {
    return oreThreshold;
  }

  public int getMaxClusterSize() {
    return maxClusterSize;
  }
}
