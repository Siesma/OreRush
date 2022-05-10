package game.server;


import java.util.ArrayList;

/**
 * This class holds the settings for a particular game instance such as game length and map size
 * It can  also be used to set the default values of these parameters.
 */
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
    this.numberOfRobots = 2;
    this.mapWidth = 30;
    this.mapHeight = 15;
    this.numberOfRounds = 25;
    this.oreDensity = 1f;
    this.maxAllowedMoves = 4;
    this.radarDistance = 4;
    this.maxClusterSize = 4;
    this.oreThreshold = 0.75f;
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

  public float getOreDensity() {
    return oreDensity;
  }

  public ServerSettings setOreDensity(float oreDensity) {
    this.oreDensity = oreDensity;
    return this;
  }

  public int getMaxAllowedMoves() {
    return maxAllowedMoves;
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

  public ArrayList<Object> getOres() {
    return ores;
  }

  public ServerSettings setOres(ArrayList<Object> ores) {
    this.ores = ores;
    return this;
  }

  public int getMaxClusterSize() {
    return maxClusterSize;
  }

  public ServerSettings setMaxClusterSize(int maxClusterSize) {
    this.maxClusterSize = maxClusterSize;
    return this;
  }

  public float getOreThreshold() {
    return oreThreshold;
  }

  public ServerSettings setOreThreshold(float oreThreshold) {
    this.oreThreshold = oreThreshold;
    return this;
  }

  public int getNumberOfRobots() {
    return numberOfRobots;
  }

  public ServerSettings setNumberOfRobots(int numberOfRobots) {
    this.numberOfRobots = numberOfRobots;
    return this;
  }
}
